/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.resource;

import com.google.common.collect.ImmutableMap;
import com.google.common.math.LongMath;
import com.google.gson.JsonParser;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class PeriodicNotificationManager
extends SinglePreparationResourceReloader<Map<String, List<Entry>>>
implements AutoCloseable {
    private static final Codec<Map<String, List<Entry>>> CODEC = Codec.unboundedMap(Codec.STRING, RecordCodecBuilder.create(instance -> instance.group(Codec.LONG.optionalFieldOf("delay", 0L).forGetter(Entry::delay), ((MapCodec)Codec.LONG.fieldOf("period")).forGetter(Entry::period), ((MapCodec)Codec.STRING.fieldOf("title")).forGetter(Entry::title), ((MapCodec)Codec.STRING.fieldOf("message")).forGetter(Entry::message)).apply((Applicative<Entry, ?>)instance, Entry::new)).listOf());
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Identifier id;
    private final Object2BooleanFunction<String> countryPredicate;
    @Nullable
    private Timer timer;
    @Nullable
    private NotifyTask task;

    public PeriodicNotificationManager(Identifier id, Object2BooleanFunction<String> countryPredicate) {
        this.id = id;
        this.countryPredicate = countryPredicate;
    }

    /*
     * Enabled aggressive exception aggregation
     */
    @Override
    protected Map<String, List<Entry>> prepare(ResourceManager resourceManager, Profiler profiler) {
        try (Resource resource = resourceManager.getResource(this.id);){
            Map map;
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));){
                map = (Map)CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(bufferedReader)).result().orElseThrow();
            }
            return map;
        } catch (Exception exception) {
            LOGGER.warn("Failed to load {}", (Object)this.id, (Object)exception);
            return ImmutableMap.of();
        }
    }

    @Override
    protected void apply(Map<String, List<Entry>> map, ResourceManager resourceManager, Profiler profiler) {
        List<Entry> list = map.entrySet().stream().filter(entry -> (Boolean)this.countryPredicate.apply((String)entry.getKey())).map(Map.Entry::getValue).flatMap(Collection::stream).collect(Collectors.toList());
        if (list.isEmpty()) {
            this.cancelTimer();
            return;
        }
        if (list.stream().anyMatch(entry -> entry.period == 0L)) {
            Util.error("A periodic notification in " + this.id + " has a period of zero minutes");
            this.cancelTimer();
            return;
        }
        long l = this.getMinDelay(list);
        long m = this.getPeriod(list, l);
        if (this.timer == null) {
            this.timer = new Timer();
        }
        this.task = this.task == null ? new NotifyTask(list, l, m) : this.task.reload(list, m);
        this.timer.scheduleAtFixedRate((TimerTask)this.task, TimeUnit.MINUTES.toMillis(l), TimeUnit.MINUTES.toMillis(m));
    }

    @Override
    public void close() {
        this.cancelTimer();
    }

    private void cancelTimer() {
        if (this.timer != null) {
            this.timer.cancel();
        }
    }

    private long getPeriod(List<Entry> entries, long minDelay) {
        return entries.stream().mapToLong(entry -> {
            long m = entry.delay - minDelay;
            return LongMath.gcd(m, entry.period);
        }).reduce(LongMath::gcd).orElseThrow(() -> new IllegalStateException("Empty notifications from: " + this.id));
    }

    private long getMinDelay(List<Entry> entries) {
        return entries.stream().mapToLong(entry -> entry.delay).min().orElse(0L);
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
        return this.prepare(manager, profiler);
    }

    @Environment(value=EnvType.CLIENT)
    static class NotifyTask
    extends TimerTask {
        private final MinecraftClient client = MinecraftClient.getInstance();
        private final List<Entry> entries;
        private final long periodMs;
        private final AtomicLong delayMs;

        public NotifyTask(List<Entry> entries, long minDelayMs, long periodMs) {
            this.entries = entries;
            this.periodMs = periodMs;
            this.delayMs = new AtomicLong(minDelayMs);
        }

        public NotifyTask reload(List<Entry> entries, long period) {
            this.cancel();
            return new NotifyTask(entries, this.delayMs.get(), period);
        }

        @Override
        public void run() {
            long l = this.delayMs.getAndAdd(this.periodMs);
            long m = this.delayMs.get();
            for (Entry entry : this.entries) {
                long o;
                long n;
                if (l < entry.delay || (n = l / entry.period) == (o = m / entry.period)) continue;
                this.client.execute(() -> SystemToast.add(MinecraftClient.getInstance().getToastManager(), SystemToast.Type.PERIODIC_NOTIFICATION, new TranslatableText(entry.title, n), new TranslatableText(entry.message, n)));
                return;
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Entry(long delay, long period, String title, String message) {
        public Entry(long l, long m, String string, String string2) {
            this.delay = l != 0L ? l : m;
            this.period = m;
            this.title = string;
            this.message = string2;
        }
    }
}

