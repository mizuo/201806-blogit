package models;

import java.util.Date;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import io.ebean.Model;
import io.ebean.annotation.CreatedTimestamp;

/**
 * 申込者です。
 * @author mizuo
 */
@Entity
public class Applicant extends Model {

	/** メールアドレス */
	@Id
	@Column(length=255)
	@NotNull
	public String emailAddress;

	/** ハッシュ化済みパスワード */
	@Column(length=64)
	@NotNull
	public String password;

	/** 申込日時 */
	@NotNull
	@CreatedTimestamp
	public Date appliedAt;

	/**
	 * 引数のメールアドレスの登録行を取得します。
	 * @param emailAddress メールアドレス
	 * @return 申込者
	 */
	public static Optional<Applicant> findOneOrEmpty(String emailAddress) {
		final Optional<Applicant> stored = db().find(Applicant.class).where().eq("email_address", emailAddress).findOneOrEmpty();
		return stored;
	}

	/**
	 * 引数のメールアドレスの登録行を取得します。
	 * 未登録の場合は引数のメールアドレスをセットした申込者を返します。
	 * @param emailAddress メールアドレス
	 * @return 申込者
	 */
	public static Applicant findOne(String emailAddress) {
		final Optional<Applicant> stored = findOneOrEmpty(emailAddress);
		if (stored.isPresent()) {
			return stored.get();
		} else {
			final Applicant applicant = new Applicant();
			applicant.emailAddress = emailAddress;
			return applicant;
		}
	}

}
