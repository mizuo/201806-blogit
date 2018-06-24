package modules;

import com.google.inject.AbstractModule;

import controllers.OwnerEntry;
import play.Logger;

/**
 * 所有者アカウント登録モジュールです。
 * @see OwnerEntry
 * @author mizuo
 */
public class OwnerEntryModule extends AbstractModule {

	/**
	 * {@link OwnerEntry}をシングルトンでインスタンス化します。
	 */
	protected void configure() {
		if (Logger.isInfoEnabled()) {
			Logger.info("{}#configure", getClass().getName());
		}
		bind(OwnerEntry.class).asEagerSingleton();
	}

}
