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
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.HeightLimitView;
import org.jetbrains.annotations.Nullable;

public class CrashReportSection {
    private final CrashReport report;
    private final String title;
    private final List<Element> elements = Lists.newArrayList();
    private StackTraceElement[] stackTrace = new StackTraceElement[0];

    public CrashReportSection(CrashReport report, String title) {
        this.report = report;
        this.title = title;
    }

    @Environment(value=EnvType.CLIENT)
    public static String createPositionString(HeightLimitView world, double x, double y, double z) {
        return String.format(Locale.ROOT, "%.2f,%.2f,%.2f - %s", x, y, z, CrashReportSection.createPositionString(world, new BlockPos(x, y, z)));
    }

    public static String createPositionString(HeightLimitView world, BlockPos pos) {
        return CrashReportSection.createPositionString(world, pos.getX(), pos.getY(), pos.getZ());
    }

    public static String createPositionString(HeightLimitView world, int x, int y, int z) {
        int t;
        int s;
        int r;
        int q;
        int p;
        int o;
        int n;
        int m;
        int l;
        int k;
        int j;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            stringBuilder.append(String.format("World: (%d,%d,%d)", x, y, z));
        } catch (Throwable throwable) {
            stringBuilder.append("(Error finding world loc)");
        }
        stringBuilder.append(", ");
        try {
            int i = ChunkSectionPos.getSectionCoord(x);
            j = ChunkSectionPos.getSectionCoord(y);
            k = ChunkSectionPos.getSectionCoord(z);
            l = x & 0xF;
            m = y & 0xF;
            n = z & 0xF;
            o = ChunkSectionPos.getBlockCoord(i);
            p = world.getBottomY();
            q = ChunkSectionPos.getBlockCoord(k);
            r = ChunkSectionPos.getBlockCoord(i + 1) - 1;
            s = world.getTopY() - 1;
            t = ChunkSectionPos.getBlockCoord(k + 1) - 1;
            stringBuilder.append(String.format("Section: (at %d,%d,%d in %d,%d,%d; chunk contains blocks %d,%d,%d to %d,%d,%d)", l, m, n, i, j, k, o, p, q, r, s, t));
        } catch (Throwable throwable) {
            stringBuilder.append("(Error finding chunk loc)");
        }
        stringBuilder.append(", ");
        try {
            int i = x >> 9;
            j = z >> 9;
            k = i << 5;
            l = j << 5;
            m = (i + 1 << 5) - 1;
            n = (j + 1 << 5) - 1;
            o = i << 9;
            p = world.getBottomY();
            q = j << 9;
            r = (i + 1 << 9) - 1;
            s = world.getTopY() - 1;
            t = (j + 1 << 9) - 1;
            stringBuilder.append(String.format("Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,%d,%d to %d,%d,%d)", i, j, k, l, m, n, o, p, q, r, s, t));
        } catch (Throwable throwable) {
            stringBuilder.append("(Error finding world loc)");
        }
        return stringBuilder.toString();
    }

    public CrashReportSection add(String string, CrashCallable<String> crashCallable) {
        try {
            this.add(string, crashCallable.call());
        } catch (Throwable throwable) {
            this.add(string, throwable);
        }
        return this;
    }

    public CrashReportSection add(String name, Object object) {
        this.elements.add(new Element(name, object));
        return this;
    }

    public void add(String name, Throwable throwable) {
        this.add(name, (Object)throwable);
    }

    public int initStackTrace(int ignoredCallCount) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        if (stackTraceElements.length <= 0) {
            return 0;
        }
        this.stackTrace = new StackTraceElement[stackTraceElements.length - 3 - ignoredCallCount];
        System.arraycopy(stackTraceElements, 3 + ignoredCallCount, this.stackTrace, 0, this.stackTrace.length);
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

    public void trimStackTraceEnd(int callCount) {
        StackTraceElement[] stackTraceElements = new StackTraceElement[this.stackTrace.length - callCount];
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

    public static void addBlockInfo(CrashReportSection element, HeightLimitView world, BlockPos pos, @Nullable BlockState state) {
        if (state != null) {
            element.add("Block", state::toString);
        }
        element.add("Block location", () -> CrashReportSection.createPositionString(world, pos));
    }

    static class Element {
        private final String name;
        private final String detail;

        public Element(String name, @Nullable Object detail) {
            this.name = name;
            if (detail == null) {
                this.detail = "~~NULL~~";
            } else if (detail instanceof Throwable) {
                Throwable throwable = (Throwable)detail;
                this.detail = "~~ERROR~~ " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
            } else {
                this.detail = detail.toString();
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

