package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import io.ebean.Model;
import io.ebean.annotation.CreatedTimestamp;
import io.ebean.annotation.UpdatedTimestamp;

/**
 * アカウントです。
 * @author mizuo
 */
@Entity
public class Account extends Model {

	/** ログインID */
	@Id
	@Column(length=255)
	@NotNull
	public String loginId;

	/** ハッシュ化済みパスワード */
	@Column(length=64)
	@NotNull
	public String password;

	/** 作成日時 */
	@NotNull
	@CreatedTimestamp
	public Date createdAt;

	/** 更新日時 */
	@Version
	@UpdatedTimestamp
	public Date updatedAt;

	/** 個人 */
	@NotNull
	@OneToOne
	public Individual individual;

}
