/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import java.io.OutputStream;
import net.minecraft.util.PrintStreamLogger;

public class DebugPrintStreamLogger
extends PrintStreamLogger {
    public DebugPrintStreamLogger(String string, OutputStream outputStream) {
        super(string, outputStream);
    }

    @Override
    protected void log(String string) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement stackTraceElement = stackTraceElements[Math.min(3, stackTraceElements.length)];
        LOGGER.info("[{}]@.({}:{}): {}", (Object)this.name, (Object)stackTraceElement.getFileName(), (Object)stackTraceElement.getLineNumber(), (Object)string);
    }
}

