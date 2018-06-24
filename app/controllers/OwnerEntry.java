package controllers;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

import controllers.ActivationController.ActivationScenario;
import controllers.ApplicantController.ApplicantScenario;
import controllers.ControllerHelpers.ConfigHelper;
import models.Applicant;
import models.EmailTemplate;
import modules.OwnerEntryModule;
import play.Logger;
import play.inject.ApplicationLifecycle;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;

/**
 * 所有者アカウント登録処理です。
 * 起動時に{@link OwnerEntryModule}から実行される想定です。
 * @author mizuo
 */
@Singleton
public class OwnerEntry {
	/**
	 * @param lifecycle 生存周期
	 * @param configHelper 設定ヘルパー
	 * @param mailerClient メールクライアント
	 */
	@Inject
	private OwnerEntry(ApplicationLifecycle lifecycle, ConfigHelper configHelper, MailerClient mailerClient) {
		if (Logger.isInfoEnabled()) {
			Logger.info("{}", getClass().getName());
			Logger.info("ApplicationLifecycle lifecycle = {}", lifecycle);
			Logger.info("ConfigHelper configHelper = {}", configHelper);
			Logger.info("MailerClient mailerClient = {}", mailerClient);
		}
		lifecycle.addStopHook(() -> {
			return CompletableFuture.completedFuture(null);
		});
		action(configHelper, mailerClient);
	}

	/**
	 * 所有者アカウント登録処理を実行します。
	 * @param configHelper 設定ヘルパー
	 * @param mailerClient メールクライアント
	 */
	void action(ConfigHelper configHelper, MailerClient mailerClient) {
		if (configHelper.getTemporaryCode().isPresent()) {
			if (Logger.isInfoEnabled()) {
				Logger.info("所有者アカウントの自動登録を開始します。");
			}
			final String ownerEmailAddress = configHelper.getOwnerEmailAddress();
			final String temporaryCode = configHelper.getTemporaryCode().get();
			final ApplicantScenario<Boolean> applicantScenario = createApplicantScenario(ownerEmailAddress, temporaryCode, mailerClient);
			if (applicantScenario.action(ownerEmailAddress, ownerEmailAddress, temporaryCode) ) {
				if (applicantScenario.savedApplicant.isPresent()) {
					final ActivationScenario<Boolean> activationScenario = createActivationScenario(mailerClient);
					final Applicant applicant = applicantScenario.savedApplicant.get();
					if (activationScenario.action(ownerEmailAddress, applicant, applicant.password)) {
						if (Logger.isWarnEnabled()) {
							Logger.warn("所有者アカウントを自動登録しました。");
							return;
						}
					}
				}
			}
			if (Logger.isErrorEnabled()) {
				Logger.error("所有者アカウントの自動登録に失敗しました。");
			}
		} else {
			if (Logger.isInfoEnabled()) {
				Logger.info("仮登録コードが設定されていないので所有者アカウントの自動登録は行いません。");
			}
		}
	}

	/**
	 * 申込者のシナリオを作成します。
	 * @param ownerEmailAddress 所有者メールアドレス
	 * @param temporaryCode 仮登録コード
	 * @param mailerClient メールクライアント
	 * @return 申込者のシナリオ
	 */
	ApplicantScenario<Boolean> createApplicantScenario(String ownerEmailAddress, String temporaryCode, MailerClient mailerClient) {
		final ApplicantScenario<Boolean> scenario = new ApplicantScenario<Boolean>() {
			@Override
			Boolean failedExist() {
				return Boolean.FALSE;
			}
			@Override
			Optional<Email> createEmail(String fromEmailAddress, String toEmailAddress, String plainTemporaryPassword) {
				final Optional<Email> email = EmailTemplate.createOwner(fromEmailAddress, plainTemporaryPassword);
				return email;
			}
			@Override
			void send(Email email) {
				mailerClient.send(email);
			}
			@Override
			Boolean failedEmail() {
				return Boolean.FALSE;
			}
			@Override
			Boolean success() {
				return Boolean.TRUE;
			}
		};
		return scenario;
	}

	/**
	 * アカウント本登録のシナリオを作成します。
	 * @param mailerClient メールクライアント
	 * @return アカウント本登録のシナリオ
	 */
	ActivationScenario<Boolean> createActivationScenario(MailerClient mailerClient) {
		return new ActivationScenario<Boolean>() {
			@Override
			void send(Email email) {
				mailerClient.send(email);
			}
			@Override
			Boolean failedEmail() {
				return Boolean.FALSE;
			}
			@Override
			Boolean success() {
				return Boolean.TRUE;
			}
		};
	}

}
