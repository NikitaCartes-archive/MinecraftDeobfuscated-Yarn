/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.rcon;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.logging.UncaughtExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class RconBase
implements Runnable {
    private static final Logger field_14430 = LogManager.getLogger();
    private static final AtomicInteger field_14428 = new AtomicInteger(0);
    protected volatile boolean running;
    protected final String description;
    protected Thread thread;

    protected RconBase(String string) {
        this.description = string;
    }

    public synchronized void start() {
        this.running = true;
        this.thread = new Thread((Runnable)this, this.description + " #" + field_14428.incrementAndGet());
        this.thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler(field_14430));
        this.thread.start();
        field_14430.info("Thread {} started", (Object)this.description);
    }

    public synchronized void stop() {
        this.running = false;
        if (null == this.thread) {
            return;
        }
        int i = 0;
        while (this.thread.isAlive()) {
            try {
                this.thread.join(1000L);
                if (++i >= 5) {
                    field_14430.warn("Waited {} seconds attempting force stop!", (Object)i);
                    continue;
                }
                if (!this.thread.isAlive()) continue;
                field_14430.warn("Thread {} ({}) failed to exit after {} second(s)", (Object)this, (Object)this.thread.getState(), (Object)i, (Object)new Exception("Stack:"));
                this.thread.interrupt();
            } catch (InterruptedException interruptedException) {}
        }
        field_14430.info("Thread {} stopped", (Object)this.description);
        this.thread = null;
    }

    public boolean isRunning() {
        return this.running;
    }
}

