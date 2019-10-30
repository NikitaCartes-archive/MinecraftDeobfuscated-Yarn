package net.minecraft.test;

import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FailureLoggingTestCompletionListener implements TestCompletionListener {
	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onTestFailed(GameTest gameTest) {
		if (gameTest.isRequired()) {
			LOGGER.error(gameTest.getStructurePath() + " failed! " + Util.getInnermostMessage(gameTest.getThrowable()));
		} else {
			LOGGER.warn("(optional) " + gameTest.getStructurePath() + " failed. " + Util.getInnermostMessage(gameTest.getThrowable()));
		}
	}
}
