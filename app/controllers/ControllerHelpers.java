package controllers;

import javax.inject.Inject;

import com.typesafe.config.Config;

import play.mvc.Http.HeaderNames;
import play.mvc.Http.Request;
import play.mvc.Http.Status;
import play.mvc.Result;
import play.mvc.Results;

/**
 * 制御処理のヘルパー群です。
 * @author mizuo
 */
class ControllerHelpers {

	private ControllerHelpers() {}

	/**
	 * 設定ヘルパーです。
	 * @author mizuo
	 */
	static class ConfigHelper {
		/** 設定 */
		private final Config config;
		/**
		 * @param config 設定
		 */
		@Inject
		ConfigHelper(Config config) {
			this.config = config;
		}
		/**
		 * 設定で定義された所有者のメールアドレスを取得します。
		 * @return 設定で定義された所有者のメールアドレス
		 */
		String getConfigEmailAddress() {
			return config.getString("owner.emailAddress");
		}
	}

	/**
	 * 応答ヘルパーです。
	 * @author mizuo
	 */
	static class ResultHelper extends Results implements Status, HeaderNames {
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
		 * @return 410 GONE
		 */
		Result gone() {
			final String method = request.method();
			final String uri = request.uri();
			return status(GONE, views.html.defaultpages.notFound.render(method, uri));
		}
	}

}
