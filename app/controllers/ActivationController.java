package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import auth.AuthenticationAnnotations.Anybody;
import controllers.ControllerAuthHelpers.PasswordHelper;
import controllers.ControllerAuthHelpers.TemporaryPasswordHelper;
import controllers.ControllerHelpers.ConfigHelper;
import models.Account;
import models.Applicant;
import models.EmailTemplate;
import models.Individual;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;
import play.data.validation.Constraints.Validatable;
import play.data.validation.Constraints.Validate;
import play.data.validation.ValidationError;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * アカウント本登録コントローラーです。
 * 申込者の本登録ページを制御します。
 * @author mizuo
 */
public class ActivationController extends Controller {

	/** 設定ヘルパー */
	private final ConfigHelper configHelper;
	/** フォーム製造 */
	private final FormFactory formFactory;
	/** メールクライアント */
	private final MailerClient mailerClient;

	/**
	 * @param configHelper 設定ヘルパー
	 * @param formFactory フォーム製造
	 * @param mailerClient メールクライアント
	 */
	@Inject
	public ActivationController(ConfigHelper configHelper, FormFactory formFactory, MailerClient mailerClient) {
		this.configHelper = configHelper;
		this.formFactory = formFactory;
		this.mailerClient = mailerClient;
	}

	/**
	 * GET アクセスを制御します。
	 * @return 申込者の本登録ページ
	 */
	@Anybody
	public Result get() {
		final Form<ActivationParameter> activationForm = formFactory.form(ActivationParameter.class);
		return ok(views.html.activation.render(activationForm));
	}

	/**
	 * POST アクセスを制御します。
	 * @return 申込者の本登録ページ
	 */
	@Anybody
	public Result post() {
		final Form<ActivationParameter> activationForm = formFactory.form(ActivationParameter.class).bindFromRequest();
		if (activationForm.hasErrors()) {
			final ActivationParameter parameter = activationForm.discardingErrors().get();
			if (parameter.storedApplicant.isPresent()) {
				return status(CONFLICT, views.html.activation.render(activationForm));
			} else {
				return badRequest(views.html.activation.render(activationForm));
			}
		} else {
			final ActivationParameter parameter = activationForm.get();
			if (parameter.storedApplicant.isPresent()) {
				final String ownerEmailAddress = configHelper.getOwnerEmailAddress();
				final Applicant applicant = parameter.storedApplicant.get();
				final String hashedPassword = PasswordHelper.hash(parameter.password);
				// アカウント本登録のシナリオを実行する
				return new ActivationScenario<Result>() {
					@Override
					void send(Email email) {
						mailerClient.send(email);
					}
					@Override
					Result failedEmail() {
						return internalServerError();
					}
					@Override
					Result success() {
						return redirect(routes.HomeController.index());
					}
				}.action(ownerEmailAddress, applicant, hashedPassword);
			} else {
				return internalServerError();
			}
		}
	}

	/**
	 * アカウント本登録のシナリオです。
	 * @author mizuo
	 */
	static abstract class ActivationScenario<T> {
		/**
		 * 申込者からアカウントの本登録処理を行います。
		 * 処理成功時に申込者のメールアドレスに本登録完了メールが送信されます。
		 * また、処理成功時は申込者の情報が削除されます。
		 * @param fromEmailAddress Fromメールアドレス
		 * @param applicant 申込者
		 * @param hashedPassword ハッシュ化したパスワード
		 * @return
		 */
		T action(String fromEmailAddress, Applicant applicant, String hashedPassword) {
			final Individual individual = toIndividual(applicant);
			individual.save();
			final Account account = toAccount(applicant, hashedPassword);
			account.individualId = individual.id;
			account.save();
			applicant.delete();
			// 本登録完了メールを送信する
			final Optional<Email> email = EmailTemplate.createActivation(fromEmailAddress, individual.emailAddress);
			if (email.isPresent()) {
				send(email.get());
			} else {
				return failedEmail();
			}
			return success();
		}
		/** 個人に変換します。 */
		Individual toIndividual(Applicant applicant) {
			final Individual individual = new Individual();
			individual.emailAddress = applicant.emailAddress;
			individual.appliedAt = applicant.appliedAt;
			return individual;
		}
		/** アカウントに変換します。 */
		Account toAccount(Applicant applicant, String hashedPassword) {
			final Account account = new Account();
			account.loginId = applicant.emailAddress;
			account.password = hashedPassword;
			return account;
		}
		/** 本登録完了メールを送信します。 */
		abstract void send(Email email);
		/** メールが生成できなかった場合の結果を返します。 */
		abstract T failedEmail();
		/** 処理が成功した場合の結果を返します。 */
		abstract T success();
	}

	/**
	 * アカウント本登録の変数群です。
	 * @author mizuo
	 */
	@Validate
	public static class ActivationParameter implements Validatable<List<ValidationError>> {
		/** メールアドレス */
		@Required
		@MaxLength(255)
		public String emailAddress;
		/** 仮登録コード */
		@Required
		@MaxLength(8)
		public String temporaryCode;
		/** 仮パスワード */
		@Required
		public String temporaryPassword;
		/** パスワード */
		@Required
		public String password;
		/** DBに登録されていた申込者 */
		Optional<Applicant> storedApplicant;
		/***
		 * 申込者の認証を行います。
		 */
		@Override
		public List<ValidationError> validate() {
			storedApplicant = Optional.empty();
			// メールアドレスの一意チェックをする。
			final Individual individual = new Individual();
			individual.emailAddress = emailAddress;
			if (!individual.isUsedEmailAddress()) {
				// 申込者の認証をする。
				storedApplicant = Applicant.findOneOrEmpty(emailAddress);
				if (storedApplicant.isPresent()) {
					final Applicant applicant = storedApplicant.get();
					if (TemporaryPasswordHelper.equal(temporaryCode, temporaryPassword, applicant.password)) {
						// 認証OK
						return null;
					}
				}
			}
			final List<ValidationError> errors = new ArrayList<>();
			errors.add(new ValidationError("", "メールアドレス／仮登録コード／仮パスワードに誤りがあるか、既に登録済みです。"));
			return errors;
		}
	}

}
