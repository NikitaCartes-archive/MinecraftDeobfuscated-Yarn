package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2991 implements class_4013 {
	private static final Logger field_13421 = LogManager.getLogger();
	private static final class_2960 field_13417 = new class_2960("tick");
	private static final class_2960 field_13412 = new class_2960("load");
	public static final int field_13415 = "functions/".length();
	public static final int field_13414 = ".mcfunction".length();
	private final MinecraftServer field_13419;
	private final Map<class_2960, class_2158> field_13420 = Maps.<class_2960, class_2158>newHashMap();
	private final ArrayDeque<class_2991.class_2992> field_13413 = new ArrayDeque();
	private boolean field_13411;
	private final class_3503<class_2158> field_13416 = new class_3503<>(this::method_12905, "tags/functions", true, "function");
	private final List<class_2158> field_13418 = Lists.<class_2158>newArrayList();
	private boolean field_13422;

	public class_2991(MinecraftServer minecraftServer) {
		this.field_13419 = minecraftServer;
	}

	public Optional<class_2158> method_12905(class_2960 arg) {
		return Optional.ofNullable(this.field_13420.get(arg));
	}

	public MinecraftServer method_12907() {
		return this.field_13419;
	}

	public int method_12902() {
		return this.field_13419.method_3767().method_8356("maxCommandChainLength");
	}

	public Map<class_2960, class_2158> method_12912() {
		return this.field_13420;
	}

	public CommandDispatcher<class_2168> method_12900() {
		return this.field_13419.method_3734().method_9235();
	}

	public void method_18699() {
		this.field_13419.method_16044().method_15400(field_13417::toString);

		for (class_2158 lv : this.field_13418) {
			this.method_12904(lv, this.method_12899());
		}

		this.field_13419.method_16044().method_15407();
		if (this.field_13422) {
			this.field_13422 = false;
			Collection<class_2158> collection = this.method_12901().method_15188(field_13412).method_15138();
			this.field_13419.method_16044().method_15400(field_13412::toString);

			for (class_2158 lv2 : collection) {
				this.method_12904(lv2, this.method_12899());
			}

			this.field_13419.method_16044().method_15407();
		}
	}

	public int method_12904(class_2158 arg, class_2168 arg2) {
		int i = this.method_12902();
		if (this.field_13411) {
			if (this.field_13413.size() < i) {
				this.field_13413.addFirst(new class_2991.class_2992(this, arg2, new class_2158.class_2162(arg)));
			}

			return 0;
		} else {
			int var16;
			try {
				this.field_13411 = true;
				int j = 0;
				class_2158.class_2161[] lvs = arg.method_9193();

				for (int k = lvs.length - 1; k >= 0; k--) {
					this.field_13413.push(new class_2991.class_2992(this, arg2, lvs[k]));
				}

				do {
					if (this.field_13413.isEmpty()) {
						return j;
					}

					try {
						class_2991.class_2992 lv = (class_2991.class_2992)this.field_13413.removeFirst();
						this.field_13419.method_16044().method_15400(lv::toString);
						lv.method_12914(this.field_13413, i);
					} finally {
						this.field_13419.method_16044().method_15407();
					}
				} while (++j < i);

				var16 = j;
			} finally {
				this.field_13413.clear();
				this.field_13411 = false;
			}

			return var16;
		}
	}

	@Override
	public void method_14491(class_3300 arg) {
		this.field_13420.clear();
		this.field_13418.clear();
		this.field_13416.method_15195();
		Collection<class_2960> collection = arg.method_14488("functions", stringx -> stringx.endsWith(".mcfunction"));
		List<CompletableFuture<class_2158>> list = Lists.<CompletableFuture<class_2158>>newArrayList();

		for (class_2960 lv : collection) {
			String string = lv.method_12832();
			class_2960 lv2 = new class_2960(lv.method_12836(), string.substring(field_13415, string.length() - field_13414));
			list.add(
				CompletableFuture.supplyAsync(() -> method_12906(arg, lv), class_3306.field_14301)
					.thenApplyAsync(listx -> class_2158.method_9195(lv2, this, listx), this.field_13419.method_17191())
					.handle((arg2, throwable) -> this.method_12903(arg2, throwable, lv))
			);
		}

		CompletableFuture.allOf((CompletableFuture[])list.toArray(new CompletableFuture[0])).join();
		if (!this.field_13420.isEmpty()) {
			field_13421.info("Loaded {} custom command functions", this.field_13420.size());
		}

		this.field_13416.method_18242((Map<class_2960, class_3494.class_3495<class_2158>>)this.field_13416.method_15192(arg, this.field_13419.method_17191()).join());
		this.field_13418.addAll(this.field_13416.method_15188(field_13417).method_15138());
		this.field_13422 = true;
	}

	@Nullable
	private class_2158 method_12903(class_2158 arg, @Nullable Throwable throwable, class_2960 arg2) {
		if (throwable != null) {
			field_13421.error("Couldn't load function at {}", arg2, throwable);
			return null;
		} else {
			synchronized (this.field_13420) {
				this.field_13420.put(arg.method_9194(), arg);
				return arg;
			}
		}
	}

	private static List<String> method_12906(class_3300 arg, class_2960 arg2) {
		try {
			class_3298 lv = arg.method_14486(arg2);
			Throwable var3 = null;

			List var4;
			try {
				var4 = IOUtils.readLines(lv.method_14482(), StandardCharsets.UTF_8);
			} catch (Throwable var14) {
				var3 = var14;
				throw var14;
			} finally {
				if (lv != null) {
					if (var3 != null) {
						try {
							lv.close();
						} catch (Throwable var13) {
							var3.addSuppressed(var13);
						}
					} else {
						lv.close();
					}
				}
			}

			return var4;
		} catch (IOException var16) {
			throw new CompletionException(var16);
		}
	}

	public class_2168 method_12899() {
		return this.field_13419.method_3739().method_9206(2).method_9217();
	}

	public class_3503<class_2158> method_12901() {
		return this.field_13416;
	}

	public static class class_2992 {
		private final class_2991 field_13423;
		private final class_2168 field_13424;
		private final class_2158.class_2161 field_13425;

		public class_2992(class_2991 arg, class_2168 arg2, class_2158.class_2161 arg3) {
			this.field_13423 = arg;
			this.field_13424 = arg2;
			this.field_13425 = arg3;
		}

		public void method_12914(ArrayDeque<class_2991.class_2992> arrayDeque, int i) {
			try {
				this.field_13425.method_9198(this.field_13423, this.field_13424, arrayDeque, i);
			} catch (Throwable var4) {
			}
		}

		public String toString() {
			return this.field_13425.toString();
		}
	}
}
