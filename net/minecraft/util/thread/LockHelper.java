/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.thread;

import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.thread.AtomicStack;
import org.jetbrains.annotations.Nullable;

public class LockHelper {
    public static void checkLock(Semaphore semaphore, @Nullable AtomicStack<Pair<Thread, StackTraceElement[]>> lockStack, String message) {
        boolean bl = semaphore.tryAcquire();
        if (!bl) {
            throw LockHelper.crash(message, lockStack);
        }
    }

    public static CrashException crash(String message, @Nullable AtomicStack<Pair<Thread, StackTraceElement[]>> lockStack) {
        String string = Thread.getAllStackTraces().keySet().stream().filter(Objects::nonNull).map(thread -> thread.getName() + ": \n\tat " + Arrays.stream(thread.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat "))).collect(Collectors.joining("\n"));
        CrashReport crashReport = new CrashReport("Accessing " + message + " from multiple threads", new IllegalStateException());
        CrashReportSection crashReportSection = crashReport.addElement("Thread dumps");
        crashReportSection.add("Thread dumps", string);
        if (lockStack != null) {
            StringBuilder stringBuilder = new StringBuilder();
            List<Pair<Thread, StackTraceElement[]>> list = lockStack.toList();
            for (Pair<Thread, StackTraceElement[]> pair : list) {
                stringBuilder.append("Thread ").append(pair.getFirst().getName()).append(": \n\tat ").append(Arrays.stream(pair.getSecond()).map(Object::toString).collect(Collectors.joining("\n\tat "))).append("\n");
            }
            crashReportSection.add("Last threads", stringBuilder.toString());
        }
        return new CrashException(crashReport);
    }
}

