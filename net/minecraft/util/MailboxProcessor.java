/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.Actor;
import net.minecraft.util.Mailbox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MailboxProcessor<T>
implements Actor<T>,
AutoCloseable,
Runnable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final AtomicInteger stateFlags = new AtomicInteger(0);
    public final Mailbox<? super T, ? extends Runnable> mailbox;
    private final Executor executor;
    private final String name;

    public static MailboxProcessor<Runnable> create(Executor executor, String string) {
        return new MailboxProcessor<Runnable>(new Mailbox.QueueMailbox(new ConcurrentLinkedQueue()), executor, string);
    }

    public MailboxProcessor(Mailbox<? super T, ? extends Runnable> mailbox, Executor executor, String string) {
        this.executor = executor;
        this.mailbox = mailbox;
        this.name = string;
    }

    private boolean lock() {
        int i;
        do {
            if (((i = this.stateFlags.get()) & 3) == 0) continue;
            return false;
        } while (!this.stateFlags.compareAndSet(i, i | 2));
        return true;
    }

    private void unlock() {
        int i;
        while (!this.stateFlags.compareAndSet(i = this.stateFlags.get(), i & 0xFFFFFFFD)) {
        }
    }

    private boolean hasMessages() {
        if ((this.stateFlags.get() & 1) != 0) {
            return false;
        }
        return !this.mailbox.isEmpty();
    }

    @Override
    public void close() {
        int i;
        while (!this.stateFlags.compareAndSet(i = this.stateFlags.get(), i | 1)) {
        }
    }

    private boolean isLocked() {
        return (this.stateFlags.get() & 2) != 0;
    }

    private boolean runNext() {
        if (!this.isLocked()) {
            return false;
        }
        Runnable runnable = this.mailbox.poll();
        if (runnable == null) {
            return false;
        }
        runnable.run();
        return true;
    }

    @Override
    public void run() {
        try {
            this.run(i -> i == 0);
        } finally {
            this.unlock();
            this.execute();
        }
    }

    @Override
    public void send(T object) {
        this.mailbox.add(object);
        this.execute();
    }

    private void execute() {
        if (this.hasMessages() && this.lock()) {
            try {
                this.executor.execute(this);
            } catch (RejectedExecutionException rejectedExecutionException) {
                try {
                    this.executor.execute(this);
                } catch (RejectedExecutionException rejectedExecutionException2) {
                    LOGGER.error("Cound not schedule mailbox", (Throwable)rejectedExecutionException2);
                }
            }
        }
    }

    private int run(Int2BooleanFunction int2BooleanFunction) {
        int i = 0;
        while (int2BooleanFunction.get(i) && this.runNext()) {
            ++i;
        }
        return i;
    }

    public String toString() {
        return this.name + " " + this.stateFlags.get() + " " + this.mailbox.isEmpty();
    }

    @Override
    public String getName() {
        return this.name;
    }
}

