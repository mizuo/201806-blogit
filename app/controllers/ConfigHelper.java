package controllers;

import javax.inject.Inject;

import com.typesafe.config.Config;

/**
 * 設定ヘルパーです。
 * @author mizuo
 */
class ConfigHelper {

	/** 設定 */
	private final Config config;

	/**
	 * @param config 設定
	 */
	@Inject
	ConfigHelper(Config config) {
		this.config = config;
	}

	/**
	 * 設定で定義された所有者のメールアドレスを取得します。
	 * @return 設定で定義された所有者のメールアドレス
	 */
	String getConfigEmailAddress() {
		return config.getString("owner.emailAddress");
	}

}
