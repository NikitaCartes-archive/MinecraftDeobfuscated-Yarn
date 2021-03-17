/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.crash;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReportSection;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrashReport {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String message;
    private final Throwable cause;
    private final CrashReportSection systemDetailsSection = new CrashReportSection(this, "System Details");
    private final List<CrashReportSection> otherSections = Lists.newArrayList();
    private File file;
    private boolean hasStackTrace = true;
    private StackTraceElement[] stackTrace = new StackTraceElement[0];

    public CrashReport(String message, Throwable cause) {
        this.message = message;
        this.cause = cause;
        this.fillSystemDetails();
    }

    private void fillSystemDetails() {
        this.systemDetailsSection.add("Minecraft Version", () -> SharedConstants.getGameVersion().getName());
        this.systemDetailsSection.add("Minecraft Version ID", () -> SharedConstants.getGameVersion().getId());
        this.systemDetailsSection.add("Operating System", () -> System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version"));
        this.systemDetailsSection.add("Java Version", () -> System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
        this.systemDetailsSection.add("Java VM Version", () -> System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor"));
        this.systemDetailsSection.add("Memory", () -> {
            Runtime runtime = Runtime.getRuntime();
            long l = runtime.maxMemory();
            long m = runtime.totalMemory();
            long n = runtime.freeMemory();
            long o = l / 1024L / 1024L;
            long p = m / 1024L / 1024L;
            long q = n / 1024L / 1024L;
            return n + " bytes (" + q + " MB) / " + m + " bytes (" + p + " MB) up to " + l + " bytes (" + o + " MB)";
        });
        this.systemDetailsSection.add("CPUs", Runtime.getRuntime().availableProcessors());
        this.systemDetailsSection.add("JVM Flags", () -> {
            List list = Util.getJVMFlags().collect(Collectors.toList());
            return String.format("%d total; %s", list.size(), list.stream().collect(Collectors.joining(" ")));
        });
    }

    public String getMessage() {
        return this.message;
    }

    public Throwable getCause() {
        return this.cause;
    }

    public void addStackTrace(StringBuilder crashReportBuilder) {
        if (!(this.stackTrace != null && this.stackTrace.length > 0 || this.otherSections.isEmpty())) {
            this.stackTrace = ArrayUtils.subarray(this.otherSections.get(0).getStackTrace(), 0, 1);
        }
        if (this.stackTrace != null && this.stackTrace.length > 0) {
            crashReportBuilder.append("-- Head --\n");
            crashReportBuilder.append("Thread: ").append(Thread.currentThread().getName()).append("\n");
            crashReportBuilder.append("Stacktrace:\n");
            for (StackTraceElement stackTraceElement : this.stackTrace) {
                crashReportBuilder.append("\t").append("at ").append(stackTraceElement);
                crashReportBuilder.append("\n");
            }
            crashReportBuilder.append("\n");
        }
        for (CrashReportSection crashReportSection : this.otherSections) {
            crashReportSection.addStackTrace(crashReportBuilder);
            crashReportBuilder.append("\n\n");
        }
        this.systemDetailsSection.addStackTrace(crashReportBuilder);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getCauseAsString() {
        String string;
        StringWriter stringWriter = null;
        PrintWriter printWriter = null;
        Throwable throwable = this.cause;
        if (throwable.getMessage() == null) {
            if (throwable instanceof NullPointerException) {
                throwable = new NullPointerException(this.message);
            } else if (throwable instanceof StackOverflowError) {
                throwable = new StackOverflowError(this.message);
            } else if (throwable instanceof OutOfMemoryError) {
                throwable = new OutOfMemoryError(this.message);
            }
            throwable.setStackTrace(this.cause.getStackTrace());
        }
        try {
            stringWriter = new StringWriter();
            printWriter = new PrintWriter(stringWriter);
            throwable.printStackTrace(printWriter);
            string = stringWriter.toString();
        } catch (Throwable throwable2) {
            IOUtils.closeQuietly(stringWriter);
            IOUtils.closeQuietly(printWriter);
            throw throwable2;
        }
        IOUtils.closeQuietly(stringWriter);
        IOUtils.closeQuietly(printWriter);
        return string;
    }

    public String asString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("---- Minecraft Crash Report ----\n");
        stringBuilder.append("// ");
        stringBuilder.append(CrashReport.generateWittyComment());
        stringBuilder.append("\n\n");
        stringBuilder.append("Time: ");
        stringBuilder.append(new SimpleDateFormat().format(new Date()));
        stringBuilder.append("\n");
        stringBuilder.append("Description: ");
        stringBuilder.append(this.message);
        stringBuilder.append("\n\n");
        stringBuilder.append(this.getCauseAsString());
        stringBuilder.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");
        for (int i = 0; i < 87; ++i) {
            stringBuilder.append("-");
        }
        stringBuilder.append("\n\n");
        this.addStackTrace(stringBuilder);
        return stringBuilder.toString();
    }

    @Environment(value=EnvType.CLIENT)
    public File getFile() {
        return this.file;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean writeToFile(File file) {
        boolean bl;
        if (this.file != null) {
            return false;
        }
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter((OutputStream)new FileOutputStream(file), StandardCharsets.UTF_8);
            writer.write(this.asString());
            this.file = file;
            bl = true;
        } catch (Throwable throwable) {
            boolean bl2;
            try {
                LOGGER.error("Could not save crash report to {}", (Object)file, (Object)throwable);
                bl2 = false;
            } catch (Throwable throwable2) {
                IOUtils.closeQuietly(writer);
                throw throwable2;
            }
            IOUtils.closeQuietly(writer);
            return bl2;
        }
        IOUtils.closeQuietly(writer);
        return bl;
    }

    public CrashReportSection getSystemDetailsSection() {
        return this.systemDetailsSection;
    }

    public CrashReportSection addElement(String name) {
        return this.addElement(name, 1);
    }

    public CrashReportSection addElement(String name, int ignoredStackTraceCallCount) {
        CrashReportSection crashReportSection = new CrashReportSection(this, name);
        if (this.hasStackTrace) {
            int i = crashReportSection.initStackTrace(ignoredStackTraceCallCount);
            StackTraceElement[] stackTraceElements = this.cause.getStackTrace();
            StackTraceElement stackTraceElement = null;
            StackTraceElement stackTraceElement2 = null;
            int j = stackTraceElements.length - i;
            if (j < 0) {
                System.out.println("Negative index in crash report handler (" + stackTraceElements.length + "/" + i + ")");
            }
            if (stackTraceElements != null && 0 <= j && j < stackTraceElements.length) {
                stackTraceElement = stackTraceElements[j];
                if (stackTraceElements.length + 1 - i < stackTraceElements.length) {
                    stackTraceElement2 = stackTraceElements[stackTraceElements.length + 1 - i];
                }
            }
            this.hasStackTrace = crashReportSection.shouldGenerateStackTrace(stackTraceElement, stackTraceElement2);
            if (i > 0 && !this.otherSections.isEmpty()) {
                CrashReportSection crashReportSection2 = this.otherSections.get(this.otherSections.size() - 1);
                crashReportSection2.trimStackTraceEnd(i);
            } else if (stackTraceElements != null && stackTraceElements.length >= i && 0 <= j && j < stackTraceElements.length) {
                this.stackTrace = new StackTraceElement[j];
                System.arraycopy(stackTraceElements, 0, this.stackTrace, 0, this.stackTrace.length);
            } else {
                this.hasStackTrace = false;
            }
        }
        this.otherSections.add(crashReportSection);
        return crashReportSection;
    }

    private static String generateWittyComment() {
        String[] strings = new String[]{"Who set us up the TNT?", "Everything's going to plan. No, really, that was supposed to happen.", "Uh... Did I do that?", "Oops.", "Why did you do that?", "I feel sad now :(", "My bad.", "I'm sorry, Dave.", "I let you down. Sorry :(", "On the bright side, I bought you a teddy bear!", "Daisy, daisy...", "Oh - I know what I did wrong!", "Hey, that tickles! Hehehe!", "I blame Dinnerbone.", "You should try our sister game, Minceraft!", "Don't be sad. I'll do better next time, I promise!", "Don't be sad, have a hug! <3", "I just don't know what went wrong :(", "Shall we play a game?", "Quite honestly, I wouldn't worry myself about that.", "I bet Cylons wouldn't have this problem.", "Sorry :(", "Surprise! Haha. Well, this is awkward.", "Would you like a cupcake?", "Hi. I'm Minecraft, and I'm a crashaholic.", "Ooh. Shiny.", "This doesn't make any sense!", "Why is it breaking :(", "Don't do that.", "Ouch. That hurt :(", "You're mean.", "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]", "There are four lights!", "But it works on my machine."};
        try {
            return strings[(int)(Util.getMeasuringTimeNano() % (long)strings.length)];
        } catch (Throwable throwable) {
            return "Witty comment unavailable :(";
        }
    }

    public static CrashReport create(Throwable cause, String title) {
        while (cause instanceof CompletionException && cause.getCause() != null) {
            cause = cause.getCause();
        }
        CrashReport crashReport = cause instanceof CrashException ? ((CrashException)cause).getReport() : new CrashReport(title, cause);
        return crashReport;
    }

    public static void initCrashReport() {
        new CrashReport("Don't panic!", new Throwable()).asString();
    }
}

