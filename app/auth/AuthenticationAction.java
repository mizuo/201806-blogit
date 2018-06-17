package auth;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import play.Logger;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Http.Request;
import play.mvc.Result;
import play.mvc.Results;

/**
 * 認証アクションです。
 * @author mizuo
 */
public class AuthenticationAction extends Action<Results> {

	/**
	 * 認証済みであれば、利用者名をリクエスト属性に設定して後続の処理を実行します。
	 * 未認証の場合はログインページへリダイレクトします。
	 */
	@Override
	public CompletionStage<Result> call(final Context ctx) {
		if (Logger.isDebugEnabled()) {
			Logger.debug("{} -> {}#call", ctx.request().uri(), getClass().getName());
		}
		final Optional<String> username = new UsernameHelpers.UsernameSession(ctx.session()).get();
		if (username.isPresent()) {
			final Request usernameReq = new UsernameHelpers.UsernameRequest(ctx.request()).addAttr(username);
			final Context usernameCtx = ctx.withRequest(usernameReq);
			return delegate.call(usernameCtx);
		} else {
			final Result result = Results.unauthorized(views.html.unauthorized.render());
			//final String path = routes.OwnerController.get().path();
			//final Result result = new Result(Status.UNAUTHORIZED, Collections.singletonMap(LOCATION, path));
			//final Result result = new Result(Status.SEE_OTHER, Collections.singletonMap(LOCATION, path));
			return CompletableFuture.completedFuture(result);
			//return delegate.call(ctx);
		}
	}

}
