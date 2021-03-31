package net.minecraft.test;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TeamCityTestCompletionListener implements TestCompletionListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Escaper ESCAPER = Escapers.builder()
		.addEscape('\'', "|'")
		.addEscape('\n', "|n")
		.addEscape('\r', "|r")
		.addEscape('|', "||")
		.addEscape('[', "|[")
		.addEscape(']', "|]")
		.build();

	@Override
	public void onTestFailed(GameTestState test) {
		String string = ESCAPER.escape(test.getStructurePath());
		String string2 = ESCAPER.escape(test.getThrowable().getMessage());
		String string3 = ESCAPER.escape(Util.getInnermostMessage(test.getThrowable()));
		LOGGER.info("##teamcity[testStarted name='{}']", string);
		if (test.isRequired()) {
			LOGGER.info("##teamcity[testFailed name='{}' message='{}' details='{}']", string, string2, string3);
		} else {
			LOGGER.info("##teamcity[testIgnored name='{}' message='{}' details='{}']", string, string2, string3);
		}

		LOGGER.info("##teamcity[testFinished name='{}' duration='{}']", string, test.getElapsedMilliseconds());
	}

	@Override
	public void onTestPassed(GameTestState test) {
		String string = ESCAPER.escape(test.getStructurePath());
		LOGGER.info("##teamcity[testStarted name='{}']", string);
		LOGGER.info("##teamcity[testFinished name='{}' duration='{}']", string, test.getElapsedMilliseconds());
	}
}
