package models;

import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import play.libs.mailer.Email;

/**
 * 電子メールの雛形です。
 * @author mizuo
 */
@Entity
public class EmailTemplate extends TimestampModel {

	/** 雛形コード */
	@Id
	@Column(length=64)
	@NotNull
	public String code;

	/** 件名 */
	@Column(length=255)
	@NotNull
	public String subject;

	/** 本文 */
	@Column(length=1024)
	@NotNull
	public String body;

	/**
	 * 引数の雛形コードの登録行を取得します。
	 * @param code 雛形コード
	 * @return 電子メールの雛形
	 */
	private static Optional<EmailTemplate> findOneOrEmpty(String code) {
		final Optional<EmailTemplate> stored = db().find(EmailTemplate.class).where().eq("code", code).findOneOrEmpty();
		return stored;
	}

	/**
	 * 所有者仮登録メールを生成します。
	 * @param ownerEmailAddress 所有者メールアドレス
	 * @param plainTemporaryPassword 平分の仮パスワード
	 * @return 仮登録メール
	 */
	public static Optional<Email> createOwner(String ownerEmailAddress, String plainTemporaryPassword) {
		final Optional<EmailTemplate> stored = findOneOrEmpty("owner");
		if (stored.isPresent()) {
			final EmailTemplate template = stored.get();
			final Email email = new Email()
					.setSubject(template.subject)
					.setFrom(ownerEmailAddress)
					.addTo(ownerEmailAddress)
					.setBodyText(template.body.replaceAll(":temporaryPassword", plainTemporaryPassword));
			return Optional.of(email);
		} else {
			return Optional.empty();
		}
	}

	/**
	 * 本登録完了メールを生成します。
	 * @param fromEmailAddress Fromメールアドレス
	 * @param individualEmailAddress 個人メールアドレス
	 * @return 本登録完了メール
	 */
	public static Optional<Email> createActivation(String fromEmailAddress, String individualEmailAddress) {
		final Optional<EmailTemplate> stored = findOneOrEmpty("activation");
		if (stored.isPresent()) {
			final EmailTemplate template = stored.get();
			final Email email = new Email()
					.setSubject(template.subject)
					.setFrom(fromEmailAddress)
					.addTo(individualEmailAddress)
					.setBodyText(template.body);
			return Optional.of(email);
		} else {
			return Optional.empty();
		}
	}

}
