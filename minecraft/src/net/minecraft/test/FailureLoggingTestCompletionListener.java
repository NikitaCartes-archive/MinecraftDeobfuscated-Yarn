package net.minecraft.test;

import com.mojang.logging.LogUtils;
import net.minecraft.util.Util;
import org.slf4j.Logger;

public class FailureLoggingTestCompletionListener implements TestCompletionListener {
	private static final Logger LOGGER = LogUtils.getLogger();

	@Override
	public void onTestFailed(GameTestState test) {
		String string = test.getPos().toShortString();
		if (test.isRequired()) {
			LOGGER.error("{} failed at {}! {}", test.getTemplatePath(), string, Util.getInnermostMessage(test.getThrowable()));
		} else {
			LOGGER.warn("(optional) {} failed at {}. {}", test.getTemplatePath(), string, Util.getInnermostMessage(test.getThrowable()));
		}
	}

	@Override
	public void onTestPassed(GameTestState test) {
	}
}
