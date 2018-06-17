package models;

import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * アカウントです。
 * @author mizuo
 */
@Entity
public class Account extends TimestampModel {

	/** ログインID */
	@Id
	@Column(length=255)
	@NotNull
	public String loginId;

	/** ハッシュ化済みパスワード */
	@Column(length=64)
	@NotNull
	public String password;

	/** 個人ID */
	@NotNull
	@Column(unique = true)
	public Long individualId;

	/**
	 * 引数のログインIDの登録行を取得します。
	 * @param loginId ログインID
	 * @return アカウント
	 */
	public static Optional<Account> findOneOrEmpty(String loginId) {
		final Optional<Account> stored = db().find(Account.class).where().eq("login_id", loginId).findOneOrEmpty();
		return stored;
	}

}
