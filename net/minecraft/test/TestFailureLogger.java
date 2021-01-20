/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.test;

import net.minecraft.test.FailureLoggingTestCompletionListener;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestCompletionListener;

public class TestFailureLogger {
    private static TestCompletionListener completionListener = new FailureLoggingTestCompletionListener();

    public static void failTest(GameTest test) {
        completionListener.onTestFailed(test);
    }

    public static void passTest(GameTest test) {
        completionListener.onTestPassed(test);
    }
}

