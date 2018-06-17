package auth;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import auth.AuthenticationAnnotations.Anybody;
import auth.AuthenticationAnnotations.Authenticated;
import controllers.LoginController;

/**
 * 認証アクション生成のテストクラスです。
 * @author mizuo
 */
public class AuthenticationActionCreatorTest {

	/** 注釈検証用のダミーメソッドです。 */
	@Ignore
	void dummy() {}
	/** 認証不要の注釈検証用のダミーメソッドです。 */
	@Anybody
	void dummyAnybody() {}
	/** 認証処理の注釈検証用のダミーメソッドです。 */
	@Authenticated
	void dummyAuthenticated() {}

	/**
	 * {@link AuthenticationAnnotation} の内部注釈がアクションメソッドに指定されている場合のテストを行います。
	 */
	@Test
	public void hasAuthenticationAnnotation() throws NoSuchMethodException, SecurityException {
		final AuthenticationActionCreator creator = new AuthenticationActionCreator();
		Assert.assertFalse("AuthenticationAnnotation の内部注釈定義以外は false となる。", creator.hasAuthenticationAnnotation(AuthenticationActionCreatorTest.class.getDeclaredMethod("dummy")));
		Assert.assertTrue("AuthenticationAnnotation の内部注釈定義は true となる。", creator.hasAuthenticationAnnotation(AuthenticationActionCreatorTest.class.getDeclaredMethod("dummyAnybody")));
		Assert.assertTrue("AuthenticationAnnotation の内部注釈定義は true となる。", creator.hasAuthenticationAnnotation(AuthenticationActionCreatorTest.class.getDeclaredMethod("dummyAuthenticated")));
	}

	/**
	 * アクションメソッドがログイン処理の場合のテストを行います。
	 */
	@Test
	public void isLogin() throws NoSuchMethodException, SecurityException {
		final AuthenticationActionCreator creator = new AuthenticationActionCreator();
		for (Method method : getClass().getMethods()) {
			Assert.assertFalse(String.format("対象外クラスのメソッドは認証ありとする。-> %s#%s", method.getDeclaringClass().getName(), method.getName()), creator.isLogin(method));
		}
		Assert.assertTrue("ログイン制御クラスの get メソッドは認証なしとする", creator.isLogin(LoginController.class.getMethod("get")));
		Assert.assertFalse("ログイン制御クラスの get メソッド以外は認証ありとする", creator.isLogin(LoginController.class.getMethod("post")));
	}

}
