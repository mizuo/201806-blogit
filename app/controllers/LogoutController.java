package controllers;

import auth.AuthenticationAnnotations.Anybody;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * ログアウトコントローラーです。
 * @author mizuo
 */
public class LogoutController extends Controller {

	/**
	 * GET アクセスを制御します。
	 * @return ログアウトページ
	 */
	@Anybody
	public Result get() {
		return ok(views.html.logout.render());
	}

	/**
	 * POST アクセスを制御します。
	 * HTTPセッションをクリアします。
	 * @return ログインページ
	 */
	@Anybody
	public Result post() {
		session().clear();
		return redirect(routes.HomeController.index());
	}

}
