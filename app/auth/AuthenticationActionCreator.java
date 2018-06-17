package auth;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import controllers.LoginController;
import play.Logger;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Http.Request;
import play.mvc.Result;
import play.mvc.Results;

/**
 * 認証アクション生成クラスです。
 * HTTPリクエストに対応するメソッドは原則的に認証処理を経由します。
 * 認証処理を経由したくないメソッドは {@link AuthenticationAnnotations.Anybody} を注釈してください。
 * @author mizuo
 */
public class AuthenticationActionCreator implements play.http.ActionCreator {

	/**
	 * アクションを生成します。
	 * {@link AuthenticationAnnotations} の内部注釈が指定されている場合は、後続処理を実行します。
	 * そうでない場合は、認証処理を実行します。
	 */
	@Override
	public Action<Void> createAction(Request request, Method actionMethod) {
		if (Logger.isDebugEnabled()) {
			Logger.debug("{}#createAction(Request {}, Method {})", getClass().getName(), request.uri(), actionMethod);
		}
		return new Action.Simple() {
			@Override
			public CompletionStage<Result> call(Http.Context ctx) {
				if (hasAuthenticationAnnotation(actionMethod)) {
					return delegate.call(ctx);
				} else {
					if (isLogin(actionMethod)) {
						final Result result = Results.internalServerError();
						return CompletableFuture.completedFuture(result);
					}
				}
				return new AuthenticationAction().call(ctx);
			}
		};
	}

	/**
	 * {@link AuthenticationAnnotation} の内部注釈がアクションメソッドに指定されている場合 true を返します。
	 * @param actionMethod アクションメソッド
	 * @return {@link AuthenticationAnnotation} の内部注釈が指定されている場合 true
	 */
	boolean hasAuthenticationAnnotation(Method actionMethod) {
		for (Annotation annotation : actionMethod.getAnnotations()) {
			final Class<?> enclosingClass = annotation.annotationType().getEnclosingClass();
			if (enclosingClass != null) {
				if (enclosingClass.equals(AuthenticationAnnotations.class)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * アクションメソッドがログイン処理の場合 true を返します。
	 * @param actionMethod アクションメソッド
	 * @return アクションメソッドがログイン処理の場合 true
	 */
	boolean isLogin(Method actionMethod) {
		if (actionMethod.getDeclaringClass().equals(LoginController.class)) {
			for (Method method : LoginController.class.getMethods()) {
				if (method.getName().equals("get")) {
					if (method.equals(actionMethod)) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
