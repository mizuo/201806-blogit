package controllers;

import java.util.Optional;

import javax.inject.Inject;

import auth.AuthenticationAnnotations.Anybody;
import controllers.ControllerHelpers.ConfigHelper;
import controllers.ControllerHelpers.ResultHelper;
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
import play.mvc.Result;

/**
 * 所有者アカウント仮登録コントローラーです。
 * 所有者メールアドレスからアカウント仮登録を行うページを制御します。
 * 所有者アカウントが本登録された後はアクセス不能となります。
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
	 * @return 所有者アカウント仮登録ページ
	 */
	@Anybody
	public Result get() {
		final String configEmailAddress = configHelper.getOwnerEmailAddress();
		final Individual individual = new Individual();
		individual.emailAddress = configEmailAddress;
		if (individual.isUsedEmailAddress()) {
			// 所有者アカウントが登録済みであれば、このページは消滅扱いとする。
			return new ResultHelper(request()).gone();
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
	 * @return アカウント本登録ページ
	 */
	@Anybody
	public Result post() {
		final Form<OwnerParameter> ownerForm = formFactory.form(OwnerParameter.class).bindFromRequest();
		if (ownerForm.hasErrors()) {
			return badRequest(views.html.owner.render(ownerForm));
		} else {
			// 所有者メールアドレスを設定から取得する。
			final String ownerEmailAddress = configHelper.getOwnerEmailAddress();
			// 仮登録コードをリクエストから取得する。
			final OwnerParameter parameter = ownerForm.get();
			final String temporaryCode = parameter.temporaryCode;
			// 申込者のシナリオを実行する
			return new ApplicantController.ApplicantScenario<Result>() {
				@Override
				Result failedExist() {
					// 所有者アカウントが登録済みであれば、このページは消滅扱いとする。
					return new ResultHelper(request()).gone();
				}
				@Override
				Optional<Email> createEmail(String fromEmailAddress, String toEmailAddress, String plainTemporaryPassword) {
					final Optional<Email> email = EmailTemplate.createOwner(fromEmailAddress, plainTemporaryPassword);
					return email;
				}
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
					return redirect(routes.ActivationController.get());
				}
			}.action(ownerEmailAddress, ownerEmailAddress, temporaryCode);
		}
	}

	/**
	 * 所有者アカウント仮登録の変数群です。
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
