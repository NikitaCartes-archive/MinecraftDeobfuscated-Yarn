/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.logging;

import org.slf4j.Logger;

public class UncaughtExceptionHandler
implements Thread.UncaughtExceptionHandler {
    private final Logger logger;

    public UncaughtExceptionHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        this.logger.error("Caught previously unhandled exception :");
        this.logger.error(thread.getName(), throwable);
    }
}

