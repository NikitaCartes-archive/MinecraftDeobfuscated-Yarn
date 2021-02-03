/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

public class class_5798 {
    public static void method_33566(ReentrantLock reentrantLock, String string) {
        if (reentrantLock.isLocked() && !reentrantLock.isHeldByCurrentThread()) {
            throw class_5798.method_33564(string);
        }
        reentrantLock.lock();
    }

    public static CrashException method_33564(String string) {
        String string2 = Thread.getAllStackTraces().keySet().stream().filter(Objects::nonNull).map(thread -> thread.getName() + ": \n\tat " + Arrays.stream(thread.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat "))).collect(Collectors.joining("\n"));
        CrashReport crashReport = new CrashReport("Accessing " + string + " from multiple threads", new IllegalStateException());
        CrashReportSection crashReportSection = crashReport.addElement("Thread dumps");
        crashReportSection.add("Thread dumps", string2);
        return new CrashException(crashReport);
    }
}

