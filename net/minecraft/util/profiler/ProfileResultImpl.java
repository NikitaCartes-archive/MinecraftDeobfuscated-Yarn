/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.SharedConstants;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.ProfilerTiming;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProfileResultImpl
implements ProfileResult {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, Long> timings;
    private final Map<String, Long> field_19382;
    private final long startTime;
    private final int startTick;
    private final long endTime;
    private final int endTick;
    private final int field_19383;

    public ProfileResultImpl(Map<String, Long> map, Map<String, Long> map2, long l, int i, long m, int j) {
        this.timings = map;
        this.field_19382 = map2;
        this.startTime = l;
        this.startTick = i;
        this.endTime = m;
        this.endTick = j;
        this.field_19383 = j - i;
    }

    @Override
    public List<ProfilerTiming> getTimings(String string) {
        String string2 = string;
        long l = this.timings.containsKey("root") ? this.timings.get("root") : 0L;
        long m = this.timings.getOrDefault(string, -1L);
        long n = this.field_19382.getOrDefault(string, 0L);
        ArrayList<ProfilerTiming> list = Lists.newArrayList();
        if (!string.isEmpty()) {
            string = string + ".";
        }
        long o = 0L;
        for (String string3 : this.timings.keySet()) {
            if (string3.length() <= string.length() || !string3.startsWith(string) || string3.indexOf(".", string.length() + 1) >= 0) continue;
            o += this.timings.get(string3).longValue();
        }
        float f = o;
        if (o < m) {
            o = m;
        }
        if (l < o) {
            l = o;
        }
        HashSet<String> set = Sets.newHashSet(this.timings.keySet());
        set.addAll(this.field_19382.keySet());
        for (String string4 : set) {
            if (string4.length() <= string.length() || !string4.startsWith(string) || string4.indexOf(".", string.length() + 1) >= 0) continue;
            long p = this.timings.getOrDefault(string4, 0L);
            double d = (double)p * 100.0 / (double)o;
            double e = (double)p * 100.0 / (double)l;
            String string5 = string4.substring(string.length());
            long q = this.field_19382.getOrDefault(string4, 0L);
            list.add(new ProfilerTiming(string5, d, e, q));
        }
        for (String string4 : this.timings.keySet()) {
            this.timings.put(string4, this.timings.get(string4) * 999L / 1000L);
        }
        if ((float)o > f) {
            list.add(new ProfilerTiming("unspecified", (double)((float)o - f) * 100.0 / (double)o, (double)((float)o - f) * 100.0 / (double)l, n));
        }
        Collections.sort(list);
        list.add(0, new ProfilerTiming(string2, 100.0, (double)o * 100.0 / (double)l, n));
        return list;
    }

    @Override
    public long getStartTime() {
        return this.startTime;
    }

    @Override
    public int getStartTick() {
        return this.startTick;
    }

    @Override
    public long getEndTime() {
        return this.endTime;
    }

    @Override
    public int getEndTick() {
        return this.endTick;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean saveToFile(File file) {
        boolean bl;
        file.getParentFile().mkdirs();
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter((OutputStream)new FileOutputStream(file), StandardCharsets.UTF_8);
            writer.write(this.asString(this.getTimeSpan(), this.getTickSpan()));
            bl = true;
        } catch (Throwable throwable) {
            boolean bl2;
            try {
                LOGGER.error("Could not save profiler results to {}", (Object)file, (Object)throwable);
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

    protected String asString(long l, int i) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("---- Minecraft Profiler Results ----\n");
        stringBuilder.append("// ");
        stringBuilder.append(ProfileResultImpl.generateWittyComment());
        stringBuilder.append("\n\n");
        stringBuilder.append("Version: ").append(SharedConstants.getGameVersion().getId()).append('\n');
        stringBuilder.append("Time span: ").append(l / 1000000L).append(" ms\n");
        stringBuilder.append("Tick span: ").append(i).append(" ticks\n");
        stringBuilder.append("// This is approximately ").append(String.format(Locale.ROOT, "%.2f", Float.valueOf((float)i / ((float)l / 1.0E9f)))).append(" ticks per second. It should be ").append(20).append(" ticks per second\n\n");
        stringBuilder.append("--- BEGIN PROFILE DUMP ---\n\n");
        this.appendTiming(0, "root", stringBuilder);
        stringBuilder.append("--- END PROFILE DUMP ---\n\n");
        return stringBuilder.toString();
    }

    @Override
    public String getTimingTreeString() {
        StringBuilder stringBuilder = new StringBuilder();
        this.appendTiming(0, "root", stringBuilder);
        return stringBuilder.toString();
    }

    private void appendTiming(int i, String string, StringBuilder stringBuilder) {
        List<ProfilerTiming> list = this.getTimings(string);
        if (list.size() < 3) {
            return;
        }
        for (int j = 1; j < list.size(); ++j) {
            ProfilerTiming profilerTiming = list.get(j);
            stringBuilder.append(String.format("[%02d] ", i));
            for (int k = 0; k < i; ++k) {
                stringBuilder.append("|   ");
            }
            stringBuilder.append(profilerTiming.name).append('(').append(profilerTiming.field_19384).append('/').append(String.format(Locale.ROOT, "%.0f", Float.valueOf((float)profilerTiming.field_19384 / (float)this.field_19383))).append(')').append(" - ").append(String.format(Locale.ROOT, "%.2f", profilerTiming.parentSectionUsagePercentage)).append("%/").append(String.format(Locale.ROOT, "%.2f", profilerTiming.totalUsagePercentage)).append("%\n");
            if ("unspecified".equals(profilerTiming.name)) continue;
            try {
                this.appendTiming(i + 1, string + "." + profilerTiming.name, stringBuilder);
                continue;
            } catch (Exception exception) {
                stringBuilder.append("[[ EXCEPTION ").append(exception).append(" ]]");
            }
        }
    }

    private static String generateWittyComment() {
        String[] strings = new String[]{"Shiny numbers!", "Am I not running fast enough? :(", "I'm working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."};
        try {
            return strings[(int)(SystemUtil.getMeasuringTimeNano() % (long)strings.length)];
        } catch (Throwable throwable) {
            return "Witty comment unavailable :(";
        }
    }

    @Override
    public int getTickSpan() {
        return this.field_19383;
    }
}

