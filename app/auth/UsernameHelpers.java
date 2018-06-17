package auth;

import java.util.Optional;

import play.libs.typedmap.TypedKey;
import play.mvc.Http.Request;
import play.mvc.Http.Session;

/**
 * 利用者名ヘルパー群です。
 * @author mizuo
 */
public class UsernameHelpers {

	private UsernameHelpers() {}

	/** ユーザー名のセッションキー */
	private static final String KEY_USERNAME = "username";

	/**
	 * 利用者名セッションです。
	 * @author mizuo
	 */
	public static class UsernameSession {
		/** HTTPセッションクッキー */
		private Session session;
		public UsernameSession(Session session) {
			this.session = session;
		}
		/**
		 * 利用者名を返します。
		 * @return 利用者名
		 */
		public Optional<String> get() {
			final String username = session.get(KEY_USERNAME);
			return Optional.ofNullable(username);
		}
		/**
		 * 利用者名を設定します
		 * @param username 利用者名
		 */
		public void set(String username) {
			session.put(KEY_USERNAME, username);
		}
	
		/**
		 * 利用者名を削除します。
		 */
		public void remove(Session session) {
			session.remove(KEY_USERNAME);
		}
	}

	/**
	 * 利用者名リクエストです。
	 * @author mizuo
	 */
	static class UsernameRequest {
		/** ユーザー名のリクエスト属性キー */
		private static final TypedKey<String> USERNAME = TypedKey.create(KEY_USERNAME);
		private Request request;
		UsernameRequest(Request request) {
			this.request = request;
		}
		/**
		 * 利用者名を返します。
		 * @return 利用者名
		 */
		String get() {
			final String username = request.attrs().get(USERNAME);
			return username;
		}
		/**
		 * 利用者名を設定します
		 * @param username 利用者名
		 * @return 利用者名を属性に設定したリクエスト
		 */
		Request addAttr(Optional<String> username) {
			if (username.isPresent()) {
				final Request usernameReq = request.addAttr(USERNAME, username.get());
				return usernameReq;
			} else {
				return request;
			}
		}
	}


}
