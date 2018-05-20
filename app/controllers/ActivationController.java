package controllers;

import javax.inject.Inject;

import play.data.Form;
import play.data.FormFactory;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * アカウント活性化コントローラーです。
 * 仮登録アカウントから本登録を行うページを制御します。
 * @author mizuo
 */
public class ActivationController extends Controller {

	/** フォーム製造 */
	private final FormFactory formFactory;

	/**
	 * @param formFactory フォーム製造
	 */
	@Inject
	public ActivationController(FormFactory formFactory) {
		this.formFactory = formFactory;
	}

	/**
	 * GET アクセスを制御します。
	 * @return アカウント活性化ページ
	 */
	public Result get() {
		final Form<TemporaryAccount> temporaryForm = formFactory.form(TemporaryAccount.class);
		return ok(views.html.activation.render(temporaryForm));
	}

	/**
	 * POST アクセスを制御します。
	 * @return アカウント活性化ページ
	 */
	public Result post() {
		final Form<TemporaryAccount> temporaryForm = formFactory.form(TemporaryAccount.class).bindFromRequest();
		if (temporaryForm.hasErrors()) {
			return badRequest(views.html.activation.render(temporaryForm));
		} else {
			return ok(views.html.activation.render(temporaryForm));
		}
	}

	/**
	 * 仮登録アカウントです。
	 * @author mizuo
	 */
	public static class TemporaryAccount {
		/** 仮登録コード */
		@Required
		@MaxLength(8)
		public String temporaryCode;
		/** 仮パスワード */
		@Required
		public String temporaryPassword;
	}

}
