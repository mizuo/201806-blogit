package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import io.ebean.Model;
import io.ebean.SqlRow;

/**
 * 個人です。
 * @author mizuo
 */
@Entity
public class Individual extends Model {

	/** ID */
	@Id
	@NotNull
	public Long id;

	/** 電子メールアドレス */
	@Column(length=255)
	@NotNull
	public String emailAddress;

	/** 申込日時 */
	@NotNull
	public Date applied;

	/** 加入日時 */
	@NotNull
	public Date joined;

	/** 電子メールアドレスがアカウントテーブルと個人テーブルでの登録件数を取得するSQLです。 */
	private static final String UNIQUE_EMAIL_ADDRESS_SQL = " SELECT COUNT(*) AS counter FROM ("
			+ " SELECT email_address FROM individual where email_address = :emailAddress"
			+ " UNION ALL "
			+ " SELECT login_id FROM account where login_id = :emailAddress"
			+ ") A";

	/**
	 * 保持している電子メールアドレスが使用済みであるか判定します。
	 * @return 使用済みである場合 true
	 */
	public boolean isUsedEmailAddress() {
		final SqlRow result = db().createSqlQuery(UNIQUE_EMAIL_ADDRESS_SQL).setParameter("emailAddress", emailAddress).findOne();
		final long counter = result.getLong("counter");
		return counter > 0;
	}

}
