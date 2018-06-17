package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;

import auth.AuthenticationAnnotations.Anybody;
import auth.UsernameHelpers.UsernameSession;
import controllers.ControllerAuthHelpers.PasswordHelper;
import models.Account;
import models.AccountSession;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.Constraints.Required;
import play.data.validation.Constraints.Validatable;
import play.data.validation.Constraints.Validate;
import play.data.validation.ValidationError;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.Http.Session;
import play.mvc.Result;

/**
 * ログインコントローラーです。
 * @author mizuo
 */
public class LoginController extends Controller {

	/** フォーム製造 */
	private final FormFactory formFactory;

	/**
	 * @param formFactory フォーム製造
	 */
	@Inject
	public LoginController(FormFactory formFactory) {
		this.formFactory = formFactory;
	}

	/**
	 * GET アクセスを制御します。
	 * @return ログインページ
	 */
	@Anybody
	public Result get() {
		final Form<LoginParameter> loginForm = formFactory.form(LoginParameter.class);
		return ok(views.html.login.render(loginForm));
	}

	/**
	 * POST アクセスを制御します。
	 * 認証が成功した場合はHTTPセッションを発行します。
	 * @return ログインページ
	 */
	@Anybody
	public Result post() {
		final Form<LoginParameter> loginForm = formFactory.form(LoginParameter.class).bindFromRequest();
		if (loginForm.hasErrors()) {
			return badRequest(views.html.login.render(loginForm));
		} else {
			final LoginParameter parameter = loginForm.get();
			if (parameter.storedIndividualId.isPresent()) {
				final String remote = Context.current().request().remoteAddress();
				final AccountSession accountSession = new AccountSession();
				accountSession.uuid = UUID.randomUUID();
				accountSession.ipAddress = remote;
				accountSession.individualId = parameter.storedIndividualId.get();
				accountSession.save();
				//
				final Session session = Context.current().session();
				final UsernameSession username = new UsernameSession(session);
				username.set(accountSession.uuid.toString());
				return redirect(routes.HomeController.index());
			} else {
				return internalServerError();
			}
		}
	}

	/**
	 * ログインの変数群です。
	 * @author mizuo
	 */
	@Validate
	public static class LoginParameter implements Validatable<List<ValidationError>> {
		/** ログインID */
		@Required
		public String loginId;
		/** パスワード(平文) */
		@Required
		public String password;
		/** DBに登録されていた個人ID */
		Optional<Long> storedIndividualId;
		/***
		 * 認証を行います。
		 */
		@Override
		public List<ValidationError> validate() {
			storedIndividualId = Optional.empty();
			final Optional<Account> stored = Account.findOneOrEmpty(loginId);
			if (stored.isPresent()) {
				final Account account = stored.get();
				if (PasswordHelper.equal(password, account.password)) {
					// 認証OK
					storedIndividualId = Optional.of(account.individualId);
					return null;
				}
			}
			final List<ValidationError> errors = new ArrayList<>();
			errors.add(new ValidationError("", "認証できません。"));
			return errors;
		}
	}

}
