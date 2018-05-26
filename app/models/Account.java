package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
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

	/** 個人 */
	@NotNull
	@OneToOne
	public Individual individual;

}
