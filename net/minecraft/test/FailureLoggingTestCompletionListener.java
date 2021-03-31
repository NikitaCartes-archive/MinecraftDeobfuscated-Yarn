/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.test;

import net.minecraft.test.GameTestState;
import net.minecraft.test.TestCompletionListener;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FailureLoggingTestCompletionListener
implements TestCompletionListener {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onTestFailed(GameTestState test) {
        if (test.isRequired()) {
            LOGGER.error("{} failed! {}", (Object)test.getStructurePath(), (Object)Util.getInnermostMessage(test.getThrowable()));
        } else {
            LOGGER.warn("(optional) {} failed. {}", (Object)test.getStructurePath(), (Object)Util.getInnermostMessage(test.getThrowable()));
        }
    }

    @Override
    public void onTestPassed(GameTestState test) {
    }
}

