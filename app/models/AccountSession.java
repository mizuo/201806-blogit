package models;

import java.util.Optional;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * アカウントセッションです。認証後のID管理を行います。
 * @author mizuo
 */
@Entity
public class AccountSession extends TimestampModel {

	/** UUID */
	@Id
	@NotNull
	public UUID uuid;

	/** IPアドレス */
	@NotNull
	public String ipAddress;

	/** 個人ID (複数端末でアクセスすることがあるので unique にはしない) */
	@NotNull
	@Column(unique = false)
	public Long individualId;

	/**
	 * 引数のUUIDの登録行を取得します。
	 * @param uuid UUID
	 * @return アカウントセッション
	 */
	public static Optional<AccountSession> findOneOrEmpty(String uuid) {
		final Optional<AccountSession> stored = db().find(AccountSession.class).where().eq("uuid", uuid).findOneOrEmpty();
		return stored;
	}

}
