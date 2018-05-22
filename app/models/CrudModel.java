package models;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import io.ebean.Model;
import io.ebean.annotation.CreatedTimestamp;
import io.ebean.annotation.NotNull;
import io.ebean.annotation.UpdatedTimestamp;

/**
 * CRUD(Create Read Update Delete)モデルです。
 * @author mizuo
 */
@MappedSuperclass
public class CrudModel extends Model {

	/** ID */
	@Id
	@NotNull
	public Long id;

	/** 作成日時 */
	@NotNull
	@CreatedTimestamp
	public Date createdAt;

	/** 更新日時 */
	@Version
	@UpdatedTimestamp
	public Date updatedAt;

}
