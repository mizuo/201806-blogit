package controllers;

import play.mvc.Controller;
import play.mvc.Result;

/**
 * ホームコントローラーです。
 * サイトのどこからでもアクセスされやすいページを制御します。
 * @author mizuo
 */
public class HomeController extends Controller {

	/**
	 * GET アクセスを制御します。
	 * @return インデックスページ
	 */
	public Result index() {
		return ok(views.html.index.render());
	}

}
