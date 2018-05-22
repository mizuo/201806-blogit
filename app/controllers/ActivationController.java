package controllers;

import javax.inject.Inject;

import models.Individual;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;
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

	/**
	 * @param configHelper 設定ヘルパー
	 * @param mailerClient メールクライアント
	 * @param formFactory フォーム製造
	 */
	@Inject
	public ActivationController(ConfigHelper configHelper, FormFactory formFactory) {
		this.configHelper = configHelper;
		this.formFactory = formFactory;
	}

	/**
	 * GET アクセスを制御します。
	 * @return アカウント本登録ページ
	 */
	public Result get() {
		final Form<ActivationAccount> activationForm = formFactory.form(ActivationAccount.class);
		return ok(views.html.activation.render(activationForm));
	}

	/**
	 * POST アクセスを制御します。
	 * @return アカウント本登録ページ
	 */
	public Result post() {
//		final Date requestedAt = new Date();
		final Form<ActivationAccount> activationForm = formFactory.form(ActivationAccount.class).bindFromRequest();
		if (activationForm.hasErrors()) {
			return badRequest(views.html.activation.render(activationForm));
		} else {
			// メールアドレスの一意チェックをする。
			final String configEmailAddress = configHelper.getConfigEmailAddress();
			final Individual individual = new Individual();
			individual.emailAddress = configEmailAddress;
			if (individual.isUsedEmailAddress()) {
				// 登録済みの場合は競合扱いとする。
				return status(CONFLICT, views.html.activation.render(activationForm));
			} else {
				// TODO 仮登録コードをリクエストから抽出する。
				//  …
				return redirect(routes.HomeController.index());
			}
		}
	}

	/**
	 * 本登録アカウントです。
	 * @author mizuo
	 */
	public static class ActivationAccount {
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
	}

}
