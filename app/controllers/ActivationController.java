package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import controllers.ControllerHelpers.ConfigHelper;
import controllers.ControllerHelpers.PasswordHelper;
import controllers.ControllerHelpers.TemporaryPasswordHelper;
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
 * 仮登録アカウントから本登録を行うページを制御します。
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
	 * @return アカウント本登録ページ
	 */
	public Result get() {
		final Form<ActivationParameter> activationForm = formFactory.form(ActivationParameter.class);
		return ok(views.html.activation.render(activationForm));
	}

	/**
	 * POST アクセスを制御します。
	 * @return アカウント本登録ページ
	 */
	public Result post() {
		final Form<ActivationParameter> activationForm = formFactory.form(ActivationParameter.class).bindFromRequest();
		if (activationForm.hasErrors()) {
			final ActivationParameter parameter = activationForm.discardingErrors().get();
			if (parameter.stored.isPresent()) {
				return status(CONFLICT, views.html.activation.render(activationForm));
			} else {
				return badRequest(views.html.activation.render(activationForm));
			}
		} else {
			final ActivationParameter parameter = activationForm.get();
			if (parameter.stored.isPresent()) {
				final Account account = parameter.toAccount();
				account.individual.save();
				account.save();
				parameter.stored.get().delete();
				// 本登録完了メールを送信する
				final String configEmailAddress = configHelper.getConfigEmailAddress();
				final Optional<Email> email = EmailTemplate.createActivation(configEmailAddress, account.individual.emailAddress);
				if (email.isPresent()) {
					mailerClient.send(email.get());
				} else {
					return internalServerError();
				}
				return redirect(routes.HomeController.index());
			} else {
				return internalServerError();
			}
		}
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
		/** 申込者 */
		Optional<Applicant> stored;
		/** 個人 */
		private Individual toIndividual() {
			final Applicant applicant = stored.get();
			final Individual individual = new Individual();
			individual.emailAddress = applicant.emailAddress;
			individual.appliedAt = applicant.appliedAt;
			return individual;
		}
		/** アカウント */
		Account toAccount() {
			final Applicant applicant = stored.get();
			final Account account = new Account();
			account.loginId = applicant.emailAddress;
			account.password = PasswordHelper.hash(password);
			account.individual = toIndividual();
			return account;
		}

		/***
		 * 申込者の認証を行います。
		 */
		@Override
		public List<ValidationError> validate() {
			this.stored = Optional.empty();
			final List<ValidationError> errors = new ArrayList<>();
			// メールアドレスの一意チェックをする。
			final Individual individual = new Individual();
			individual.emailAddress = emailAddress;
			if (!individual.isUsedEmailAddress()) {
				// 申込者の認証をする。
				this.stored = Applicant.findOneOrEmpty(emailAddress);
				if (stored.isPresent()) {
					final Applicant applicant = stored.get();
					if (TemporaryPasswordHelper.equal(temporaryCode, temporaryPassword, applicant.password)) {
						return null;
					}
				}
			}
			errors.add(new ValidationError("", "メールアドレス／仮登録コード／仮パスワードに誤りがあるか、既に登録済みです。"));
			return errors;
		}
	}

}
