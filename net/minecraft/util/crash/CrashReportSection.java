/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.crash;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class CrashReportSection {
    private final CrashReport report;
    private final String title;
    private final List<Element> elements = Lists.newArrayList();
    private StackTraceElement[] stackTrace = new StackTraceElement[0];

    public CrashReportSection(CrashReport crashReport, String string) {
        this.report = crashReport;
        this.title = string;
    }

    @Environment(value=EnvType.CLIENT)
    public static String createPositionString(double d, double e, double f) {
        return String.format(Locale.ROOT, "%.2f,%.2f,%.2f - %s", d, e, f, CrashReportSection.createPositionString(new BlockPos(d, e, f)));
    }

    public static String createPositionString(BlockPos blockPos) {
        return CrashReportSection.createPositionString(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static String createPositionString(int i, int j, int k) {
        int t;
        int s;
        int r;
        int q;
        int p;
        int o;
        int n;
        int m;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            stringBuilder.append(String.format("World: (%d,%d,%d)", i, j, k));
        } catch (Throwable throwable) {
            stringBuilder.append("(Error finding world loc)");
        }
        stringBuilder.append(", ");
        try {
            int l = i >> 4;
            m = k >> 4;
            n = i & 0xF;
            o = j >> 4;
            p = k & 0xF;
            q = l << 4;
            r = m << 4;
            s = (l + 1 << 4) - 1;
            t = (m + 1 << 4) - 1;
            stringBuilder.append(String.format("Chunk: (at %d,%d,%d in %d,%d; contains blocks %d,0,%d to %d,255,%d)", n, o, p, l, m, q, r, s, t));
        } catch (Throwable throwable) {
            stringBuilder.append("(Error finding chunk loc)");
        }
        stringBuilder.append(", ");
        try {
            int l = i >> 9;
            m = k >> 9;
            n = l << 5;
            o = m << 5;
            p = (l + 1 << 5) - 1;
            q = (m + 1 << 5) - 1;
            r = l << 9;
            s = m << 9;
            t = (l + 1 << 9) - 1;
            int u = (m + 1 << 9) - 1;
            stringBuilder.append(String.format("Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,0,%d to %d,255,%d)", l, m, n, o, p, q, r, s, t, u));
        } catch (Throwable throwable) {
            stringBuilder.append("(Error finding world loc)");
        }
        return stringBuilder.toString();
    }

    public void add(String string, CrashCallable<String> crashCallable) {
        try {
            this.add(string, crashCallable.call());
        } catch (Throwable throwable) {
            this.add(string, throwable);
        }
    }

    public void add(String string, Object object) {
        this.elements.add(new Element(string, object));
    }

    public void add(String string, Throwable throwable) {
        this.add(string, (Object)throwable);
    }

    public int trimStackTrace(int i) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        if (stackTraceElements.length <= 0) {
            return 0;
        }
        this.stackTrace = new StackTraceElement[stackTraceElements.length - 3 - i];
        System.arraycopy(stackTraceElements, 3 + i, this.stackTrace, 0, this.stackTrace.length);
        return this.stackTrace.length;
    }

    public boolean method_584(StackTraceElement stackTraceElement, StackTraceElement stackTraceElement2) {
        if (this.stackTrace.length == 0 || stackTraceElement == null) {
            return false;
        }
        StackTraceElement stackTraceElement3 = this.stackTrace[0];
        if (!(stackTraceElement3.isNativeMethod() == stackTraceElement.isNativeMethod() && stackTraceElement3.getClassName().equals(stackTraceElement.getClassName()) && stackTraceElement3.getFileName().equals(stackTraceElement.getFileName()) && stackTraceElement3.getMethodName().equals(stackTraceElement.getMethodName()))) {
            return false;
        }
        if (stackTraceElement2 != null != this.stackTrace.length > 1) {
            return false;
        }
        if (stackTraceElement2 != null && !this.stackTrace[1].equals(stackTraceElement2)) {
            return false;
        }
        this.stackTrace[0] = stackTraceElement;
        return true;
    }

    public void method_580(int i) {
        StackTraceElement[] stackTraceElements = new StackTraceElement[this.stackTrace.length - i];
        System.arraycopy(this.stackTrace, 0, stackTraceElements, 0, stackTraceElements.length);
        this.stackTrace = stackTraceElements;
    }

    public void addStackTrace(StringBuilder stringBuilder) {
        stringBuilder.append("-- ").append(this.title).append(" --\n");
        stringBuilder.append("Details:");
        for (Element element : this.elements) {
            stringBuilder.append("\n\t");
            stringBuilder.append(element.getName());
            stringBuilder.append(": ");
            stringBuilder.append(element.getDetail());
        }
        if (this.stackTrace != null && this.stackTrace.length > 0) {
            stringBuilder.append("\nStacktrace:");
            for (StackTraceElement stackTraceElement : this.stackTrace) {
                stringBuilder.append("\n\tat ");
                stringBuilder.append(stackTraceElement);
            }
        }
    }

    public StackTraceElement[] getStackTrace() {
        return this.stackTrace;
    }

    public static void addBlockInfo(CrashReportSection crashReportSection, BlockPos blockPos, @Nullable BlockState blockState) {
        if (blockState != null) {
            crashReportSection.add("Block", blockState::toString);
        }
        crashReportSection.add("Block location", () -> CrashReportSection.createPositionString(blockPos));
    }

    static class Element {
        private final String name;
        private final String detail;

        public Element(String string, Object object) {
            this.name = string;
            if (object == null) {
                this.detail = "~~NULL~~";
            } else if (object instanceof Throwable) {
                Throwable throwable = (Throwable)object;
                this.detail = "~~ERROR~~ " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
            } else {
                this.detail = object.toString();
            }
        }

        public String getName() {
            return this.name;
        }

        public String getDetail() {
            return this.detail;
        }
    }
}

