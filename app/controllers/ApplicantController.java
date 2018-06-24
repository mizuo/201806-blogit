package controllers;

import java.util.Optional;

import auth.AuthenticationAnnotations.Anybody;
import controllers.ControllerAuthHelpers.TemporaryPasswordHelper;
import models.Applicant;
import models.Individual;
import play.libs.mailer.Email;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * 申込者コントローラーです。
 * 当面は受付しないので、アクセスされても 403 Forbidden で応答します。
 * @author mizuo
 */
public class ApplicantController extends Controller {

	/**
	 * GET アクセスを制御します。
	 * 現在は申し込みを受け付けていないので 403 Forbidden を応答します。
	 * @return 403 Forbidden
	 */
	@Anybody
	public Result get() {
		return forbidden();
	}

	/**
	 * POST アクセスを制御します。
	 * 現在は申し込みを受け付けていないので 403 Forbidden を応答します。
	 * @return 403 Forbidden
	 */
	@Anybody
	public Result post() {
		return forbidden();
	}

	/**
	 * 申込者のシナリオです。
	 * @author mizuo
	 */
	static abstract class ApplicantScenario<T> {
		Optional<Applicant> savedApplicant;
		T action(String fromEmailAddress, String toEmailAddress, String temporaryCode) {
			savedApplicant = Optional.empty();
			// メールアドレスの一意チェックをする。
			final Individual individual = new Individual();
			individual.emailAddress = toEmailAddress;
			if (individual.isUsedEmailAddress()) {
				// メールアドレスが登録済み
				return failedExist();
			} else {
				// 仮パスワードを生成する。
				final TemporaryPasswordHelper password = new TemporaryPasswordHelper();
				final String hashed = password.hash(temporaryCode);
				// 申込者
				final Applicant applicant = Applicant.findOneOrCreate(toEmailAddress);
				applicant.password = hashed;
				// 仮パスワードの一部をメール送信する
				final Optional<Email> email = createEmail(fromEmailAddress, toEmailAddress, password.plainTemporary);
				if (email.isPresent()) {
					send(email.get());
				} else {
					return failedEmail();
				}
				// 登録する
				applicant.save();
				savedApplicant = Optional.of(applicant);
				return success();
			}
		}
		/** メールアドレスが登録済みだった場合の結果を返します。 */
		abstract T failedExist();
		/** メールを生成します。 */
		abstract Optional<Email> createEmail(String fromEmailAddress, String toEmailAddress, String temporaryPassword);
		/** 仮パスワードメールを送信します。 */
		abstract void send(Email email);
		/** メールが生成できなかった場合の結果を返します。 */
		abstract T failedEmail();
		/** 処理が成功した場合の結果を返します。 */
		abstract T success();
	}

}
