package controllers;

import java.util.Date;
import java.util.Optional;

import javax.inject.Inject;

import models.Applicant;
import models.EmailTemplate;
import models.Individual;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Result;

/**
 * 所有者アカウント登録コントローラーです。
 * 最初のアカウントを登録するページを制御します。
 * アカウントが本登録された後はアクセス不能となります。
 * @author mizuo
 */
public class OwnerController extends Controller {

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
	public OwnerController(ConfigHelper configHelper, FormFactory formFactory, MailerClient mailerClient) {
		this.configHelper = configHelper;
		this.formFactory = formFactory;
		this.mailerClient = mailerClient;
	}

	/**
	 * GET アクセスを制御します。
	 * 所有者アカウントが登録されていない場合のみアクセス可能です。
	 * 所有者アカウントが登録済みであれば、410 GONE を応答します。
	 * @return 所有者ページ
	 */
	public Result get() {
		final String configEmailAddress = configHelper.getConfigEmailAddress();
		final Individual individual = new Individual();
		individual.emailAddress = configEmailAddress;
		if (individual.isUsedEmailAddress()) {
			// 所有者アカウントが登録済みであれば、このページは消滅扱いとする。
			return gone();
		} else {
			// 所有者アカウントが登録されていない場合のみアクセス可能とする。
			final Form<OwnerParameter> ownerForm = formFactory.form(OwnerParameter.class);
			return ok(views.html.owner.render(ownerForm));
		}
	}

	/**
	 * POST アクセスを制御します。
	 * 所有者アカウントが登録されていない場合のみアクセス可能です。
	 * 所有者アカウントが登録済みであれば、410 GONE を応答します。
	 * 処理成功した場合はアカウントの仮登録を行います。
	 * @return 所有者ページ
	 */
	public Result post() {
		final Date requestedAt = new Date();
		final Form<OwnerParameter> ownerForm = formFactory.form(OwnerParameter.class).bindFromRequest();
		if (ownerForm.hasErrors()) {
			return badRequest(views.html.owner.render(ownerForm));
		} else {
			// メールアドレスの一意チェックをする。
			final String configEmailAddress = configHelper.getConfigEmailAddress();
			final Individual individual = new Individual();
			individual.emailAddress = configEmailAddress;
			if (individual.isUsedEmailAddress()) {
				// 所有者アカウントが登録済みであれば、このページは消滅扱いとする。
				return gone();
			} else {
				// 仮登録コードをリクエストから抽出する。
				final OwnerParameter parameter = ownerForm.get();
				final String temporaryCode = parameter.temporaryCode;
				// 仮パスワードを生成する。
				final PasswordHelper password = new PasswordHelper();
				final String hashed = password.hash(temporaryCode);
				// 申込者
				final Applicant applicant = Applicant.findOne(configEmailAddress);
				applicant.password = hashed;
				applicant.appliedAt = requestedAt;
				// 仮パスワードの一部をメール送信する
				final Optional<Email> email = EmailTemplate.createOwnerTemporaryRegistration(configEmailAddress, password.plainTemporary);
				if (email.isPresent()) {
					mailerClient.send(email.get());
				} else {
					return internalServerError(views.html.owner.render(ownerForm));
				}
				// 登録する
				applicant.save();
				return redirect(routes.ActivationController.get());
			}
		}
	}

	/**
	 * HTTP ステータスコード 410 GONE で応答します。
	 * 所有者アカウントが登録済みの場合に呼び出されます。
	 * @return 410 GONE
	 */
	private Result gone() {
		final Request request = request();
		final String method = request.method();
		final String uri = request.uri();
		return status(GONE, views.html.defaultpages.notFound.render(method, uri));
	}

	/**
	 * 所有者アカウント登録の変数群です。
	 * @author mizuo
	 */
	public static class OwnerParameter {
		/** 仮登録コード */
		@Required
		@MinLength(4)
		@MaxLength(8)
		public String temporaryCode;
	}

}
