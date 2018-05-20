package models;

import java.util.Date;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import io.ebean.Model;

/**
 * 申込者です。
 * @author mizuo
 */
@Entity
public class Applicant extends Model {

	/** 電子メールアドレス */
	@Id
	@Column(unique=true, length=255)
	@NotNull
	public String emailAddress;

	/** ハッシュ化済みパスワード */
	@Column(length=64)
	@NotNull
	public String password;

	/** 申込日時 */
	@NotNull
	public Date applied;

	/**
	 * 引数の電子メールアドレスの登録行を取得します。
	 * 未登録の場合は引数の電子メールアドレスをセットした申込者を返します。
	 * @param emailAddress 電子メールアドレス
	 * @return 申込者
	 */
	public static Applicant findOne(String emailAddress) {
		final Optional<Applicant> stored = db().find(Applicant.class).where().eq("email_address", emailAddress).findOneOrEmpty();
		if (stored.isPresent()) {
			return stored.get();
		} else {
			final Applicant applicant = new Applicant();
			applicant.emailAddress = emailAddress;
			return applicant;
		}
	}

}
