package models;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import io.ebean.annotation.NotNull;

/**
 * CRUD(Create Read Update Delete)モデルです。
 * @author mizuo
 */
@MappedSuperclass
public class CrudModel extends TimestampModel {

	/** ID */
	@Id
	@NotNull
	public Long id;

}
