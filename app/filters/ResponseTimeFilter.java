package filters;

import java.util.concurrent.Executor;

import javax.inject.Inject;

import play.Logger;
import play.mvc.EssentialAction;
import play.mvc.EssentialFilter;

/**
 * 応答時間フィルターです。
 * @author mizuo
 */
public class ResponseTimeFilter extends EssentialFilter {

	/** デバッグレベルで出力するミリ秒の閾値 */
	private static final long DEBUG_LIMIT_MILLISECONDS = 1000;
	/** 情報レベルで出力するミリ秒の閾値 */
	private static final long INFO_LIMIT_MILLISECONDS = 2000;
	/** リクエストログに出力するメッセージ */
	private static final String REQUEST_LOG_MESSAGE = "request: {} {}";
	/** レスポンスログに出力するメッセージ */
	private static final String RESPONSE_LOG_MESSAGE = "request: {} {} -> speed: {} ms; status: {}";

	private final Executor executor;

	@Inject
	public ResponseTimeFilter(Executor executor) {
		this.executor = executor;
	}

	/**
	 * 処理開始から応答終了までの処理時間を計測しログ出力します。
	 * 応答時間が想定内の場合は情報レベルで出力し、応答時間が遅い場合は警告レベルで出力します。
	 */
	@Override
	public EssentialAction apply(EssentialAction next) {
		if (Logger.isTraceEnabled()) {
			Logger.trace("{}#apply", getClass().getName());
		}
		return EssentialAction.of(requestHeader -> {
			if (Logger.isDebugEnabled()) {
				Logger.debug(REQUEST_LOG_MESSAGE, requestHeader.method(), requestHeader.uri());
			}
			final long startTimeMillis = System.currentTimeMillis();
			return next.apply(requestHeader).map(result -> {
				final long endTimeMillis = System.currentTimeMillis();
				long responseimeMillis = endTimeMillis - startTimeMillis;
				if (responseimeMillis < (DEBUG_LIMIT_MILLISECONDS)) {
					if (Logger.isDebugEnabled()) {
						Logger.debug(RESPONSE_LOG_MESSAGE, requestHeader.method(), requestHeader.uri(), responseimeMillis, result.status());
					}
				} else {
					if (responseimeMillis < INFO_LIMIT_MILLISECONDS * 2) {
						if (Logger.isInfoEnabled()) {
							Logger.info(RESPONSE_LOG_MESSAGE, requestHeader.method(), requestHeader.uri(), responseimeMillis, result.status());
						}
					} else {
						if (Logger.isWarnEnabled()) {
							Logger.warn(RESPONSE_LOG_MESSAGE, requestHeader.method(), requestHeader.uri(), responseimeMillis, result.status());
						}
					}
				}
				return result;
			}, executor);
		});
	}

}
