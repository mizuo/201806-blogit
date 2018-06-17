package auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import play.mvc.With;

/**
 * 認証処理の注釈群です。
 * @author mizuo
 */
public class AuthenticationAnnotations {

	/**
	 * 認証を必要とするメソッド用の注釈です。
	 * 通常は {@link AuthenticationActionCreator} にて自動的に認証アクションが実行されるので
	 * このアノテーションを使う必要はありません。
	 */
	@With(AuthenticationAction.class)
	@Target({ElementType.TYPE, ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Authenticated {}

	/**
	 * 認証を不要とするメソッド用の注釈です。
	 */
	@Target({ElementType.TYPE, ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@With(AuthenticationAnybodyAction.class)
	public @interface Anybody {}

}
