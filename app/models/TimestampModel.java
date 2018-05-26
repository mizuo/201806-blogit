package models;

import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import io.ebean.Model;
import io.ebean.annotation.CreatedTimestamp;
import io.ebean.annotation.NotNull;
import io.ebean.annotation.UpdatedTimestamp;

/**
 * 作成日時と更新日時を扱うモデルです。
 * @author mizuo
 */
@MappedSuperclass
public class TimestampModel extends Model {

	/** 作成日時 */
	@NotNull
	@CreatedTimestamp
	public Date createdAt;

	/** 更新日時 */
	@NotNull
	@Version
	@UpdatedTimestamp
	public Date updatedAt;

}
