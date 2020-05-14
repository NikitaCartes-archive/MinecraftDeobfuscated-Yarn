/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.logging;

import java.io.OutputStream;
import java.io.PrintStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class LoggerPrintStream
extends PrintStream {
    protected static final Logger LOGGER = LogManager.getLogger();
    protected final String name;

    public LoggerPrintStream(String name, OutputStream out) {
        super(out);
        this.name = name;
    }

    @Override
    public void println(@Nullable String string) {
        this.log(string);
    }

    @Override
    public void println(Object object) {
        this.log(String.valueOf(object));
    }

    protected void log(@Nullable String message) {
        LOGGER.info("[{}]: {}", (Object)this.name, (Object)message);
    }
}

