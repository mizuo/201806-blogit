package controllers;

import java.util.Optional;

import javax.inject.Inject;

import com.typesafe.config.Config;

import play.mvc.Http.HeaderNames;
import play.mvc.Http.Request;
import play.mvc.Http.Status;
import play.Logger;
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
		String getOwnerEmailAddress() {
			return config.getString("owner.emailAddress");
		}
		/**
		 * 設定で定義された所有者の仮登録コードを取得します。
		 * @return 設定で定義された仮登録コード
		 */
		Optional<String> getTemporaryCode() {
			final String path = "owner.temporaryCode";
			if (config.hasPath(path)) {
				final String temporaryCode = config.getString(path);
				if (temporaryCode.isEmpty()) {
					if (Logger.isDebugEnabled()) {
						Logger.debug("{}#getTemporaryCode() is empty.", getClass().getName());
					}
					return Optional.empty();
				} else {
					return Optional.of(temporaryCode);
				}
			} else {
				if (Logger.isDebugEnabled()) {
					Logger.debug("{}#getTemporaryCode() -> The path '{}' is null.", getClass().getName(), path);
				}
				return Optional.empty();
			}
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
