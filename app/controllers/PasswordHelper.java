package controllers;

import org.mindrot.jbcrypt.BCrypt;

/**
 * パスワードヘルパーです。
 * 仮パスワードの生成とハッシュ化処理を支援します。
 * 仮パスワードは 8 文字から 16 文字の長さのランダムな英数字が生成されます。
 * 16 文字を超えた仮登録コードの場合は動作保証しません。
 * @author mizuo
 */
class PasswordHelper {

	/** 平文の仮パスワード */
	String plainTemporary;
	/** 平文パスワード */
	String plain;

	/**
	 * {@link #plainTemporary 平文の仮パスワード} を再生成します。
	 * @return 平文の仮パスワード
	 */
	@SuppressWarnings("deprecation")
	private String resetPlainTemporary() {
		this.plainTemporary = org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric(8, 16);
		return this.plainTemporary;
	}

	/** 平文パスワードの初期値用フォーマット */
	private static final String INITIALIZE_FORMAT = "%s%s%s";

	/**
	 * 平文パスワードの初期値を生成します。
	 * @param temporaryCode 仮登録コード
	 * @param plainTemporary 平文の仮パスワード
	 * @return 平文パスワードの初期値
	 */
	private String createPlain(String temporaryCode, String plainTemporary) {
		final String plain = String.format(INITIALIZE_FORMAT, temporaryCode, plainTemporary, temporaryCode);
		return plain;
	}

	/**
	 * {@link #plain 平文パスワード} を再設定します。
	 * @param temporaryCode 仮登録コード
	 * @param plainTemporary 平文の仮パスワード
	 * @return 平文パスワード
	 */
	private String resetPlain(String temporaryCode, String plainTemporary) {
		this.plainTemporary = plainTemporary;
		this.plain = createPlain(temporaryCode, plainTemporary);
		return this.plain;
	}

	/**
	 * 仮登録コードと再生成した {@link #plainTemporaryPassword 平文の仮パスワード} を組み合わせた
	 * {@link #plain 平文パスワード} を設定しハッシュ化します。
	 * 16 文字を超えた仮登録コードの場合は動作保証しません。
	 * @param temporaryCode 仮登録コード
	 * @return ハッシュ化した文字列
	 */
	String hash(String temporaryCode) {
		final String salt = BCrypt.gensalt();
		final String plainTemporary = resetPlainTemporary();
		final String plain = resetPlain(temporaryCode, plainTemporary);
		final String hashed = BCrypt.hashpw(plain, salt);
		return hashed;
	}

	/**
	 * ハッシュ化した場合に一致し得るか判定します。
	 * @param temporaryCode 仮登録コード
	 * @param plainTemporary 平文の仮パスワード
	 * @param hashed ハッシュ化した文字列
	 * @return 一致する場合 true 
	 */
	boolean equal(String temporaryCode, String plainTemporary, String hashed) {
		final String plain = createPlain(temporaryCode, plainTemporary);
		return equal(plain, hashed);
	}

	/**
	 * ハッシュ化した場合に一致し得るか判定します。
	 * @param plain 平文パスワード
	 * @param hashed ハッシュ化した文字列
	 * @return 一致する場合 true 
	 */
	boolean equal(String plain, String hashed) {
		return BCrypt.checkpw(plain, hashed);
	}

}
