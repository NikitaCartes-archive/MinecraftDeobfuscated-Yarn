package net.minecraft;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;

public class class_1928 {
	private static final TreeMap<String, class_1928.class_1930> field_9197 = class_156.method_654(new TreeMap(), treeMap -> {
		treeMap.put("doFireTick", new class_1928.class_1930("true", class_1928.class_1931.field_9209));
		treeMap.put("mobGriefing", new class_1928.class_1930("true", class_1928.class_1931.field_9209));
		treeMap.put("keepInventory", new class_1928.class_1930("false", class_1928.class_1931.field_9209));
		treeMap.put("doMobSpawning", new class_1928.class_1930("true", class_1928.class_1931.field_9209));
		treeMap.put("doMobLoot", new class_1928.class_1930("true", class_1928.class_1931.field_9209));
		treeMap.put("doTileDrops", new class_1928.class_1930("true", class_1928.class_1931.field_9209));
		treeMap.put("doEntityDrops", new class_1928.class_1930("true", class_1928.class_1931.field_9209));
		treeMap.put("commandBlockOutput", new class_1928.class_1930("true", class_1928.class_1931.field_9209));
		treeMap.put("naturalRegeneration", new class_1928.class_1930("true", class_1928.class_1931.field_9209));
		treeMap.put("doDaylightCycle", new class_1928.class_1930("true", class_1928.class_1931.field_9209));
		treeMap.put("logAdminCommands", new class_1928.class_1930("true", class_1928.class_1931.field_9209));
		treeMap.put("showDeathMessages", new class_1928.class_1930("true", class_1928.class_1931.field_9209));
		treeMap.put("randomTickSpeed", new class_1928.class_1930("3", class_1928.class_1931.field_9210));
		treeMap.put("sendCommandFeedback", new class_1928.class_1930("true", class_1928.class_1931.field_9209));
		treeMap.put("reducedDebugInfo", new class_1928.class_1930("false", class_1928.class_1931.field_9209, (minecraftServer, arg) -> {
			byte b = (byte)(arg.method_8365() ? 22 : 23);

			for (class_3222 lv : minecraftServer.method_3760().method_14571()) {
				lv.field_13987.method_14364(new class_2663(lv, b));
			}
		}));
		treeMap.put("spectatorsGenerateChunks", new class_1928.class_1930("true", class_1928.class_1931.field_9209));
		treeMap.put("spawnRadius", new class_1928.class_1930("10", class_1928.class_1931.field_9210));
		treeMap.put("disableElytraMovementCheck", new class_1928.class_1930("false", class_1928.class_1931.field_9209));
		treeMap.put("maxEntityCramming", new class_1928.class_1930("24", class_1928.class_1931.field_9210));
		treeMap.put("doWeatherCycle", new class_1928.class_1930("true", class_1928.class_1931.field_9209));
		treeMap.put("doLimitedCrafting", new class_1928.class_1930("false", class_1928.class_1931.field_9209));
		treeMap.put("maxCommandChainLength", new class_1928.class_1930("65536", class_1928.class_1931.field_9210));
		treeMap.put("announceAdvancements", new class_1928.class_1930("true", class_1928.class_1931.field_9209));
	});
	private final TreeMap<String, class_1928.class_1929> field_9196 = new TreeMap();

	public class_1928() {
		for (Entry<String, class_1928.class_1930> entry : field_9197.entrySet()) {
			this.field_9196.put(entry.getKey(), ((class_1928.class_1930)entry.getValue()).method_8368());
		}
	}

	public void method_8359(String string, String string2, @Nullable MinecraftServer minecraftServer) {
		class_1928.class_1929 lv = (class_1928.class_1929)this.field_9196.get(string);
		if (lv != null) {
			lv.method_8366(string2, minecraftServer);
		}
	}

	public boolean method_8355(String string) {
		class_1928.class_1929 lv = (class_1928.class_1929)this.field_9196.get(string);
		return lv != null ? lv.method_8365() : false;
	}

	public int method_8356(String string) {
		class_1928.class_1929 lv = (class_1928.class_1929)this.field_9196.get(string);
		return lv != null ? lv.method_8363() : 0;
	}

	public class_2487 method_8358() {
		class_2487 lv = new class_2487();

		for (String string : this.field_9196.keySet()) {
			class_1928.class_1929 lv2 = (class_1928.class_1929)this.field_9196.get(string);
			lv.method_10582(string, lv2.method_8362());
		}

		return lv;
	}

	public void method_8357(class_2487 arg) {
		for (String string : arg.method_10541()) {
			this.method_8359(string, arg.method_10558(string), null);
		}
	}

	public class_1928.class_1929 method_8360(String string) {
		return (class_1928.class_1929)this.field_9196.get(string);
	}

	public static TreeMap<String, class_1928.class_1930> method_8354() {
		return field_9197;
	}

	public static class class_1929 {
		private String field_9199;
		private boolean field_9202;
		private int field_9201;
		private double field_9198;
		private final class_1928.class_1931 field_9200;
		private final BiConsumer<MinecraftServer, class_1928.class_1929> field_9203;

		public class_1929(String string, class_1928.class_1931 arg, BiConsumer<MinecraftServer, class_1928.class_1929> biConsumer) {
			this.field_9200 = arg;
			this.field_9203 = biConsumer;
			this.method_8366(string, null);
		}

		public void method_8366(String string, @Nullable MinecraftServer minecraftServer) {
			this.field_9199 = string;
			this.field_9202 = Boolean.parseBoolean(string);
			this.field_9201 = this.field_9202 ? 1 : 0;

			try {
				this.field_9201 = Integer.parseInt(string);
			} catch (NumberFormatException var5) {
			}

			try {
				this.field_9198 = Double.parseDouble(string);
			} catch (NumberFormatException var4) {
			}

			if (minecraftServer != null) {
				this.field_9203.accept(minecraftServer, this);
			}
		}

		public String method_8362() {
			return this.field_9199;
		}

		public boolean method_8365() {
			return this.field_9202;
		}

		public int method_8363() {
			return this.field_9201;
		}

		public class_1928.class_1931 method_8364() {
			return this.field_9200;
		}
	}

	public static class class_1930 {
		private final class_1928.class_1931 field_9205;
		private final String field_9206;
		private final BiConsumer<MinecraftServer, class_1928.class_1929> field_9204;

		public class_1930(String string, class_1928.class_1931 arg) {
			this(string, arg, (minecraftServer, argx) -> {
			});
		}

		public class_1930(String string, class_1928.class_1931 arg, BiConsumer<MinecraftServer, class_1928.class_1929> biConsumer) {
			this.field_9205 = arg;
			this.field_9206 = string;
			this.field_9204 = biConsumer;
		}

		public class_1928.class_1929 method_8368() {
			return new class_1928.class_1929(this.field_9206, this.field_9205, this.field_9204);
		}

		public class_1928.class_1931 method_8367() {
			return this.field_9205;
		}
	}

	public static enum class_1931 {
		field_9211(StringArgumentType::greedyString, (commandContext, string) -> commandContext.getArgument(string, String.class)),
		field_9209(BoolArgumentType::bool, (commandContext, string) -> commandContext.<Boolean>getArgument(string, Boolean.class).toString()),
		field_9210(IntegerArgumentType::integer, (commandContext, string) -> commandContext.<Integer>getArgument(string, Integer.class).toString());

		private final Supplier<ArgumentType<?>> field_9208;
		private final BiFunction<CommandContext<class_2168>, String, String> field_9207;

		private class_1931(Supplier<ArgumentType<?>> supplier, BiFunction<CommandContext<class_2168>, String, String> biFunction) {
			this.field_9208 = supplier;
			this.field_9207 = biFunction;
		}

		public RequiredArgumentBuilder<class_2168, ?> method_8371(String string) {
			return class_2170.method_9244(string, (ArgumentType)this.field_9208.get());
		}

		public void method_8370(CommandContext<class_2168> commandContext, String string, class_1928.class_1929 arg) {
			arg.method_8366((String)this.field_9207.apply(commandContext, string), commandContext.getSource().method_9211());
		}
	}
}
