/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.test;

import net.minecraft.test.GameTest;
import net.minecraft.test.TestCompletionListener;
import net.minecraft.util.SystemUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FailureLoggingTestCompletionListener
implements TestCompletionListener {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onTestFailed(GameTest gameTest) {
        if (gameTest.isRequired()) {
            LOGGER.error(gameTest.getStructureName() + " failed! " + SystemUtil.getInnermostMessage(gameTest.getThrowable()));
        } else {
            LOGGER.warn("(optional) " + gameTest.getStructureName() + " failed. " + SystemUtil.getInnermostMessage(gameTest.getThrowable()));
        }
    }
}

