package net.minecraft.client.resource;

import com.google.common.collect.ImmutableMap;
import com.google.common.math.LongMath;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class PeriodicNotificationManager
	extends SinglePreparationResourceReloader<Map<String, List<PeriodicNotificationManager.Entry>>>
	implements AutoCloseable {
	private static final Codec<Map<String, List<PeriodicNotificationManager.Entry>>> CODEC = Codec.unboundedMap(
		Codec.STRING,
		RecordCodecBuilder.<PeriodicNotificationManager.Entry>create(
				instance -> instance.group(
							Codec.LONG.optionalFieldOf("delay", Long.valueOf(0L)).forGetter(PeriodicNotificationManager.Entry::delay),
							Codec.LONG.fieldOf("period").forGetter(PeriodicNotificationManager.Entry::period),
							Codec.STRING.fieldOf("title").forGetter(PeriodicNotificationManager.Entry::title),
							Codec.STRING.fieldOf("message").forGetter(PeriodicNotificationManager.Entry::message)
						)
						.apply(instance, PeriodicNotificationManager.Entry::new)
			)
			.listOf()
	);
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Identifier id;
	private final Object2BooleanFunction<String> countryPredicate;
	@Nullable
	private Timer timer;
	@Nullable
	private PeriodicNotificationManager.NotifyTask task;

	public PeriodicNotificationManager(Identifier id, Object2BooleanFunction<String> countryPredicate) {
		this.id = id;
		this.countryPredicate = countryPredicate;
	}

	protected Map<String, List<PeriodicNotificationManager.Entry>> prepare(ResourceManager resourceManager, Profiler profiler) {
		try {
			Reader reader = resourceManager.openAsReader(this.id);

			Map var4;
			try {
				var4 = (Map)CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(reader)).result().orElseThrow();
			} catch (Throwable var7) {
				if (reader != null) {
					try {
						reader.close();
					} catch (Throwable var6) {
						var7.addSuppressed(var6);
					}
				}

				throw var7;
			}

			if (reader != null) {
				reader.close();
			}

			return var4;
		} catch (Exception var8) {
			LOGGER.warn("Failed to load {}", this.id, var8);
			return ImmutableMap.of();
		}
	}

	protected void apply(Map<String, List<PeriodicNotificationManager.Entry>> map, ResourceManager resourceManager, Profiler profiler) {
		List<PeriodicNotificationManager.Entry> list = (List<PeriodicNotificationManager.Entry>)map.entrySet()
			.stream()
			.filter(entry -> this.countryPredicate.apply((String)entry.getKey()))
			.map(java.util.Map.Entry::getValue)
			.flatMap(Collection::stream)
			.collect(Collectors.toList());
		if (list.isEmpty()) {
			this.cancelTimer();
		} else if (list.stream().anyMatch(entry -> entry.period == 0L)) {
			Util.logErrorOrPause("A periodic notification in " + this.id + " has a period of zero minutes");
			this.cancelTimer();
		} else {
			long l = this.getMinDelay(list);
			long m = this.getPeriod(list, l);
			if (this.timer == null) {
				this.timer = new Timer();
			}

			if (this.task == null) {
				this.task = new PeriodicNotificationManager.NotifyTask(list, l, m);
			} else {
				this.task = this.task.reload(list, m);
			}

			this.timer.scheduleAtFixedRate(this.task, TimeUnit.MINUTES.toMillis(l), TimeUnit.MINUTES.toMillis(m));
		}
	}

	public void close() {
		this.cancelTimer();
	}

	private void cancelTimer() {
		if (this.timer != null) {
			this.timer.cancel();
		}
	}

	private long getPeriod(List<PeriodicNotificationManager.Entry> entries, long minDelay) {
		return entries.stream().mapToLong(entry -> {
			long m = entry.delay - minDelay;
			return LongMath.gcd(m, entry.period);
		}).reduce(LongMath::gcd).orElseThrow(() -> new IllegalStateException("Empty notifications from: " + this.id));
	}

	private long getMinDelay(List<PeriodicNotificationManager.Entry> entries) {
		return entries.stream().mapToLong(entry -> entry.delay).min().orElse(0L);
	}

	@Environment(EnvType.CLIENT)
	public static record Entry(long delay, long period, String title, String message) {

		public Entry(final long delay, final long period, final String title, final String message) {
			this.delay = delay != 0L ? delay : period;
			this.period = period;
			this.title = title;
			this.message = message;
		}
	}

	@Environment(EnvType.CLIENT)
	static class NotifyTask extends TimerTask {
		private final MinecraftClient client = MinecraftClient.getInstance();
		private final List<PeriodicNotificationManager.Entry> entries;
		private final long periodMs;
		private final AtomicLong delayMs;

		public NotifyTask(List<PeriodicNotificationManager.Entry> entries, long minDelayMs, long periodMs) {
			this.entries = entries;
			this.periodMs = periodMs;
			this.delayMs = new AtomicLong(minDelayMs);
		}

		public PeriodicNotificationManager.NotifyTask reload(List<PeriodicNotificationManager.Entry> entries, long period) {
			this.cancel();
			return new PeriodicNotificationManager.NotifyTask(entries, this.delayMs.get(), period);
		}

		public void run() {
			long l = this.delayMs.getAndAdd(this.periodMs);
			long m = this.delayMs.get();

			for (PeriodicNotificationManager.Entry entry : this.entries) {
				if (l >= entry.delay) {
					long n = l / entry.period;
					long o = m / entry.period;
					if (n != o) {
						this.client
							.execute(
								() -> SystemToast.add(
										MinecraftClient.getInstance().getToastManager(),
										SystemToast.Type.PERIODIC_NOTIFICATION,
										Text.translatable(entry.title, n),
										Text.translatable(entry.message, n)
									)
							);
						return;
					}
				}
			}
		}
	}
}
