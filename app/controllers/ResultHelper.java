package controllers;

import javax.inject.Inject;

import play.mvc.Http.HeaderNames;
import play.mvc.Http.Request;
import play.mvc.Http.Status;
import play.mvc.Result;
import play.mvc.Results;

/**
 * 応答ヘルパーです。
 * @author mizuo
 */
class ResultHelper extends Results implements Status, HeaderNames {

	/** リクエスト */
	private final Request request;

	/**
	 * @param request リクエスト
	 */
	@Inject
	ResultHelper(Request request) {
		this.request = request;
	}

	/**
	 * HTTP ステータスコード 410 GONE で応答します。
	 * 所有者アカウントが登録済みの場合に呼び出されます。
	 * @return 410 GONE
	 */
	Result gone() {
		final String method = request.method();
		final String uri = request.uri();
		return status(GONE, views.html.defaultpages.notFound.render(method, uri));
	}

}
