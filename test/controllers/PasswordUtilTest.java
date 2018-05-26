package controllers;

import org.junit.Assert;
import org.junit.Test;

import controllers.PasswordUtil.TemporaryPasswordHelper;

/**
 * パスワード処理のテストクラスです。
 * @author mizuo
 */
public class PasswordUtilTest {

	/**
	 * 仮パスワードのハッシュ化処理を検証します。
	 * @param temporaryCode 仮登録コード
	 */
	private void assertTemporaryPasswordHelper(String temporaryCode) {
		final TemporaryPasswordHelper password = new TemporaryPasswordHelper();
		Assert.assertNull("インスタンス生成直後は平文の仮パスワードは null である。", password.plainTemporary);
		Assert.assertNull("インスタンス生成直後は平文パスワードは null である。", password.plain);
		final String hashed = password.hash(temporaryCode);
		Assert.assertNotNull("ハッシュ化したら平文の仮パスワードが設定される。", password.plainTemporary);
		Assert.assertNotNull("ハッシュ化したら平文パスワードが設定される。", password.plain);
		Assert.assertEquals("ハッシュ化した結果は60桁固定である。", 60, hashed.length());
		Assert.assertEquals("ハッシュ化した最初の文字は $ である。", '$', hashed.charAt(0));
		Assert.assertTrue("同じ仮登録コードと仮パスワードはハッシュ化した文字列と同等となる。", TemporaryPasswordHelper.equal(temporaryCode, password.plainTemporary, hashed));
		Assert.assertFalse("違う仮登録コードの場合はハッシュ化した文字列も異なる。", TemporaryPasswordHelper.equal("x" + temporaryCode, password.plainTemporary, hashed));
		Assert.assertFalse("違う仮登録コードの場合はハッシュ化した文字列も異なる。", TemporaryPasswordHelper.equal(temporaryCode + "x", password.plainTemporary, hashed));
		Assert.assertFalse("違う仮パスワードの場合はハッシュ化した文字列も異なる。", TemporaryPasswordHelper.equal(temporaryCode, "x" + password.plainTemporary, hashed));
		Assert.assertFalse("違う仮パスワードの場合はハッシュ化した文字列も異なる。", TemporaryPasswordHelper.equal(temporaryCode, password.plainTemporary + "x", hashed));
		Assert.assertTrue("同じ平文パスワードならハッシュ化した文字列と同等となる。", PasswordUtil.equal(password.plain, hashed));
		Assert.assertFalse("異なる平文パスワードの場合はハッシュ化した文字列も異なる。", PasswordUtil.equal("x" + password.plain, hashed));
		Assert.assertFalse("異なる平文パスワードの場合はハッシュ化した文字列も異なる。", PasswordUtil.equal(password.plain + "x", hashed));
		final String hashed2 = password.hash(temporaryCode + "2");
		Assert.assertNotEquals("異なる値をハッシュ化すれば結果が異なる。", hashed, hashed2);
		final String hashed3 = password.hash(temporaryCode);
		Assert.assertNotEquals("ハッシュ化する毎にサルトを再生成するので結果が異なる。", hashed, hashed3);
	}

	/**
	 * 仮パスワードのハッシュ化テストを行います。
	 * 仮登録コードの桁数が多すぎる場合、ハッシュ化した文字列の比較処理で失敗する場合があり
	 * 規則性がわからないので仮登録コードが 16 桁より多い場合の動作は保障しない。
	 */
	@Test
	public void hashTemporaryPassword() {
		assertTemporaryPasswordHelper(null);
		assertTemporaryPasswordHelper("");
		assertTemporaryPasswordHelper("1");
		assertTemporaryPasswordHelper("12");
		assertTemporaryPasswordHelper("123");
		assertTemporaryPasswordHelper("1234");
		assertTemporaryPasswordHelper("12345");
		assertTemporaryPasswordHelper("123456789012345");
		assertTemporaryPasswordHelper("1234567890123456");
		// 16 桁より多い桁数の動作は保障しない。
	}

	/**
	 * パスワードをハッシュ化した結果が一致するか判定します。
	 */
	@Test
	public void equal() {
		Assert.assertFalse(PasswordUtil.equal("password", "$2a$10$KSULr3dVkTqAd1Bl9mBuD.oXHhv35Wz1j0A0WwpmXbeEawLmSwEGux"));
		Assert.assertFalse(PasswordUtil.equal("passwordx", "$2a$10$KSULr3dVkTqAd1Bl9mBuD.oXHhv35Wz1j0A0WwpmXbeEawLmSwEGu"));
		Assert.assertTrue(PasswordUtil.equal("password", "$2a$10$KSULr3dVkTqAd1Bl9mBuD.oXHhv35Wz1j0A0WwpmXbeEawLmSwEGu"));
		Assert.assertTrue(PasswordUtil.equal("1234", "$2a$10$3XpST0mKF96zNmmQrNKVF.xHZPTPMxLH3YMmN.SAhZZOu18gRGWTe"));
	}

}
