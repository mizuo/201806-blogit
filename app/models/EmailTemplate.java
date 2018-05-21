package models;

import java.util.Date;
import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import io.ebean.Model;
import play.libs.mailer.Email;

/**
 * 電子メールの雛形です。
 * @author mizuo
 */
@Entity
public class EmailTemplate extends Model {

	/** 雛形コード */
	@Id
	@NotNull
	public String code;

	/** 件名 */
	@NotNull
	public String subject;

	/** 本文 */
	@NotNull
	public String body;

	/** 登録日時 */
	@NotNull
	public Date registered;

	/**
	 * 仮登録メールを取得します。
	 * 雛形が登録されていない場合は、null を返します。
	 * @param ownerEmailAddress 所有者メールアドレス
	 * @param temporaryPassword 仮パスワード
	 * @return 仮登録メール
	 */
	public static Optional<Email> createOwnerTemporaryRegistration(String ownerEmailAddress, String temporaryPassword) {
		final Optional<EmailTemplate> stored = db().find(EmailTemplate.class).where().eq("code", "createOwnerTemporaryRegistration").findOneOrEmpty();
		if (stored.isPresent()) {
			final EmailTemplate template = stored.get();
			final Email email = new Email()
					.setSubject(template.subject)
					.setFrom(ownerEmailAddress)
					.addTo(ownerEmailAddress)
					.setBodyText(template.body.replaceAll(":temporaryPassword", temporaryPassword));
			return Optional.of(email);
		} else {
			return Optional.empty();
		}
	}

}
