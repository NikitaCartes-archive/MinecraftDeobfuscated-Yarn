package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_1928 {
	private static final Logger field_19410 = LogManager.getLogger();
	private static final Map<class_1928.class_4313<?>, class_1928.class_4314<?>> field_9197 = Maps.newTreeMap(Comparator.comparing(arg -> arg.field_19413));
	public static final class_1928.class_4313<class_1928.class_4310> field_19387 = method_8359("doFireTick", class_1928.class_4310.method_20759(true));
	public static final class_1928.class_4313<class_1928.class_4310> field_19388 = method_8359("mobGriefing", class_1928.class_4310.method_20759(true));
	public static final class_1928.class_4313<class_1928.class_4310> field_19389 = method_8359("keepInventory", class_1928.class_4310.method_20759(false));
	public static final class_1928.class_4313<class_1928.class_4310> field_19390 = method_8359("doMobSpawning", class_1928.class_4310.method_20759(true));
	public static final class_1928.class_4313<class_1928.class_4310> field_19391 = method_8359("doMobLoot", class_1928.class_4310.method_20759(true));
	public static final class_1928.class_4313<class_1928.class_4310> field_19392 = method_8359("doTileDrops", class_1928.class_4310.method_20759(true));
	public static final class_1928.class_4313<class_1928.class_4310> field_19393 = method_8359("doEntityDrops", class_1928.class_4310.method_20759(true));
	public static final class_1928.class_4313<class_1928.class_4310> field_19394 = method_8359("commandBlockOutput", class_1928.class_4310.method_20759(true));
	public static final class_1928.class_4313<class_1928.class_4310> field_19395 = method_8359("naturalRegeneration", class_1928.class_4310.method_20759(true));
	public static final class_1928.class_4313<class_1928.class_4310> field_19396 = method_8359("doDaylightCycle", class_1928.class_4310.method_20759(true));
	public static final class_1928.class_4313<class_1928.class_4310> field_19397 = method_8359("logAdminCommands", class_1928.class_4310.method_20759(true));
	public static final class_1928.class_4313<class_1928.class_4310> field_19398 = method_8359("showDeathMessages", class_1928.class_4310.method_20759(true));
	public static final class_1928.class_4313<class_1928.class_4312> field_19399 = method_8359("randomTickSpeed", class_1928.class_4312.method_20768(3));
	public static final class_1928.class_4313<class_1928.class_4310> field_19400 = method_8359("sendCommandFeedback", class_1928.class_4310.method_20759(true));
	public static final class_1928.class_4313<class_1928.class_4310> field_19401 = method_8359(
		"reducedDebugInfo", class_1928.class_4310.method_20760(false, (minecraftServer, arg) -> {
			byte b = (byte)(arg.method_20753() ? 22 : 23);

			for (class_3222 lv : minecraftServer.method_3760().method_14571()) {
				lv.field_13987.method_14364(new class_2663(lv, b));
			}
		})
	);
	public static final class_1928.class_4313<class_1928.class_4310> field_19402 = method_8359(
		"spectatorsGenerateChunks", class_1928.class_4310.method_20759(true)
	);
	public static final class_1928.class_4313<class_1928.class_4312> field_19403 = method_8359("spawnRadius", class_1928.class_4312.method_20768(10));
	public static final class_1928.class_4313<class_1928.class_4310> field_19404 = method_8359(
		"disableElytraMovementCheck", class_1928.class_4310.method_20759(false)
	);
	public static final class_1928.class_4313<class_1928.class_4312> field_19405 = method_8359("maxEntityCramming", class_1928.class_4312.method_20768(24));
	public static final class_1928.class_4313<class_1928.class_4310> field_19406 = method_8359("doWeatherCycle", class_1928.class_4310.method_20759(true));
	public static final class_1928.class_4313<class_1928.class_4310> field_19407 = method_8359("doLimitedCrafting", class_1928.class_4310.method_20759(false));
	public static final class_1928.class_4313<class_1928.class_4312> field_19408 = method_8359("maxCommandChainLength", class_1928.class_4312.method_20768(65536));
	public static final class_1928.class_4313<class_1928.class_4310> field_19409 = method_8359("announceAdvancements", class_1928.class_4310.method_20759(true));
	public static final class_1928.class_4313<class_1928.class_4310> field_19422 = method_8359("disableRaids", class_1928.class_4310.method_20759(false));
	private final Map<class_1928.class_4313<?>, class_1928.class_4315<?>> field_9196 = (Map<class_1928.class_4313<?>, class_1928.class_4315<?>>)field_9197.entrySet()
		.stream()
		.collect(ImmutableMap.toImmutableMap(Entry::getKey, entry -> ((class_1928.class_4314)entry.getValue()).method_20773()));

	private static <T extends class_1928.class_4315<T>> class_1928.class_4313<T> method_8359(String string, class_1928.class_4314<T> arg) {
		class_1928.class_4313<T> lv = new class_1928.class_4313<>(string);
		class_1928.class_4314<?> lv2 = (class_1928.class_4314<?>)field_9197.put(lv, arg);
		if (lv2 != null) {
			throw new IllegalStateException("Duplicate game rule registration for " + string);
		} else {
			return lv;
		}
	}

	public <T extends class_1928.class_4315<T>> T method_20746(class_1928.class_4313<T> arg) {
		return (T)this.field_9196.get(arg);
	}

	public class_2487 method_8358() {
		class_2487 lv = new class_2487();
		this.field_9196.forEach((arg2, arg3) -> lv.method_10582(arg2.field_19413, arg3.method_20779()));
		return lv;
	}

	public void method_8357(class_2487 arg) {
		this.field_9196.forEach((arg2, arg3) -> arg3.method_20777(arg.method_10558(arg2.field_19413)));
	}

	public static void method_20744(class_1928.class_4311 arg) {
		field_9197.forEach((arg2, arg3) -> method_20745(arg, arg2, arg3));
	}

	private static <T extends class_1928.class_4315<T>> void method_20745(class_1928.class_4311 arg, class_1928.class_4313<?> arg2, class_1928.class_4314<?> arg3) {
		arg.method_20762(arg2, arg3);
	}

	public boolean method_8355(class_1928.class_4313<class_1928.class_4310> arg) {
		return this.method_20746(arg).method_20753();
	}

	public int method_8356(class_1928.class_4313<class_1928.class_4312> arg) {
		return this.method_20746(arg).method_20763();
	}

	public static class class_4310 extends class_1928.class_4315<class_1928.class_4310> {
		private boolean field_19411;

		private static class_1928.class_4314<class_1928.class_4310> method_20760(boolean bl, BiConsumer<MinecraftServer, class_1928.class_4310> biConsumer) {
			return new class_1928.class_4314<>(BoolArgumentType::bool, arg -> new class_1928.class_4310(arg, bl), biConsumer);
		}

		private static class_1928.class_4314<class_1928.class_4310> method_20759(boolean bl) {
			return method_20760(bl, (minecraftServer, arg) -> {
			});
		}

		public class_4310(class_1928.class_4314<class_1928.class_4310> arg, boolean bl) {
			super(arg);
			this.field_19411 = bl;
		}

		@Override
		protected void method_20776(CommandContext<class_2168> commandContext, String string) {
			this.field_19411 = BoolArgumentType.getBool(commandContext, string);
		}

		public boolean method_20753() {
			return this.field_19411;
		}

		public void method_20758(boolean bl, @Nullable MinecraftServer minecraftServer) {
			this.field_19411 = bl;
			this.method_20778(minecraftServer);
		}

		@Override
		protected String method_20779() {
			return Boolean.toString(this.field_19411);
		}

		@Override
		protected void method_20777(String string) {
			this.field_19411 = Boolean.parseBoolean(string);
		}

		@Override
		public int method_20781() {
			return this.field_19411 ? 1 : 0;
		}

		protected class_1928.class_4310 method_20761() {
			return this;
		}
	}

	public interface class_4311 {
		<T extends class_1928.class_4315<T>> void method_20762(class_1928.class_4313<T> arg, class_1928.class_4314<T> arg2);
	}

	public static class class_4312 extends class_1928.class_4315<class_1928.class_4312> {
		private int field_19412;

		private static class_1928.class_4314<class_1928.class_4312> method_20766(int i, BiConsumer<MinecraftServer, class_1928.class_4312> biConsumer) {
			return new class_1928.class_4314<>(IntegerArgumentType::integer, arg -> new class_1928.class_4312(arg, i), biConsumer);
		}

		private static class_1928.class_4314<class_1928.class_4312> method_20768(int i) {
			return method_20766(i, (minecraftServer, arg) -> {
			});
		}

		public class_4312(class_1928.class_4314<class_1928.class_4312> arg, int i) {
			super(arg);
			this.field_19412 = i;
		}

		@Override
		protected void method_20776(CommandContext<class_2168> commandContext, String string) {
			this.field_19412 = IntegerArgumentType.getInteger(commandContext, string);
		}

		public int method_20763() {
			return this.field_19412;
		}

		@Override
		protected String method_20779() {
			return Integer.toString(this.field_19412);
		}

		@Override
		protected void method_20777(String string) {
			this.field_19412 = method_20769(string);
		}

		private static int method_20769(String string) {
			if (!string.isEmpty()) {
				try {
					return Integer.parseInt(string);
				} catch (NumberFormatException var2) {
					class_1928.field_19410.warn("Failed to parse integer {}", string);
				}
			}

			return 0;
		}

		@Override
		public int method_20781() {
			return this.field_19412;
		}

		protected class_1928.class_4312 method_20770() {
			return this;
		}
	}

	public static final class class_4313<T extends class_1928.class_4315<T>> {
		private final String field_19413;

		public class_4313(String string) {
			this.field_19413 = string;
		}

		public String toString() {
			return this.field_19413;
		}

		public boolean equals(Object object) {
			return this == object ? true : object instanceof class_1928.class_4313 && ((class_1928.class_4313)object).field_19413.equals(this.field_19413);
		}

		public int hashCode() {
			return this.field_19413.hashCode();
		}

		public String method_20771() {
			return this.field_19413;
		}
	}

	public static class class_4314<T extends class_1928.class_4315<T>> {
		private final Supplier<ArgumentType<?>> field_19414;
		private final Function<class_1928.class_4314<T>, T> field_19415;
		private final BiConsumer<MinecraftServer, T> field_19416;

		private class_4314(Supplier<ArgumentType<?>> supplier, Function<class_1928.class_4314<T>, T> function, BiConsumer<MinecraftServer, T> biConsumer) {
			this.field_19414 = supplier;
			this.field_19415 = function;
			this.field_19416 = biConsumer;
		}

		public RequiredArgumentBuilder<class_2168, ?> method_20775(String string) {
			return class_2170.method_9244(string, (ArgumentType<T>)this.field_19414.get());
		}

		public T method_20773() {
			return (T)this.field_19415.apply(this);
		}
	}

	public abstract static class class_4315<T extends class_1928.class_4315<T>> {
		private final class_1928.class_4314<T> field_19417;

		public class_4315(class_1928.class_4314<T> arg) {
			this.field_19417 = arg;
		}

		protected abstract void method_20776(CommandContext<class_2168> commandContext, String string);

		public void method_20780(CommandContext<class_2168> commandContext, String string) {
			this.method_20776(commandContext, string);
			this.method_20778(commandContext.getSource().method_9211());
		}

		protected void method_20778(@Nullable MinecraftServer minecraftServer) {
			if (minecraftServer != null) {
				this.field_19417.field_19416.accept(minecraftServer, this.method_20782());
			}
		}

		protected abstract void method_20777(String string);

		protected abstract String method_20779();

		public String toString() {
			return this.method_20779();
		}

		public abstract int method_20781();

		protected abstract T method_20782();
	}
}
