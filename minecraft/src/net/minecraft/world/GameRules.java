package net.minecraft.world;

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
import net.minecraft.client.network.packet.EntityStatusClientPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.SystemUtil;

public class GameRules {
	private static final TreeMap<String, GameRules.Key> KEYS = SystemUtil.consume(new TreeMap(), treeMap -> {
		treeMap.put("doFireTick", new GameRules.Key("true", GameRules.Type.BOOLEAN));
		treeMap.put("mobGriefing", new GameRules.Key("true", GameRules.Type.BOOLEAN));
		treeMap.put("keepInventory", new GameRules.Key("false", GameRules.Type.BOOLEAN));
		treeMap.put("doMobSpawning", new GameRules.Key("true", GameRules.Type.BOOLEAN));
		treeMap.put("doMobLoot", new GameRules.Key("true", GameRules.Type.BOOLEAN));
		treeMap.put("doTileDrops", new GameRules.Key("true", GameRules.Type.BOOLEAN));
		treeMap.put("doEntityDrops", new GameRules.Key("true", GameRules.Type.BOOLEAN));
		treeMap.put("commandBlockOutput", new GameRules.Key("true", GameRules.Type.BOOLEAN));
		treeMap.put("naturalRegeneration", new GameRules.Key("true", GameRules.Type.BOOLEAN));
		treeMap.put("doDaylightCycle", new GameRules.Key("true", GameRules.Type.BOOLEAN));
		treeMap.put("logAdminCommands", new GameRules.Key("true", GameRules.Type.BOOLEAN));
		treeMap.put("showDeathMessages", new GameRules.Key("true", GameRules.Type.BOOLEAN));
		treeMap.put("randomTickSpeed", new GameRules.Key("3", GameRules.Type.INTEGER));
		treeMap.put("sendCommandFeedback", new GameRules.Key("true", GameRules.Type.BOOLEAN));
		treeMap.put("reducedDebugInfo", new GameRules.Key("false", GameRules.Type.BOOLEAN, (minecraftServer, value) -> {
			byte b = (byte)(value.getBoolean() ? 22 : 23);

			for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayerList()) {
				serverPlayerEntity.networkHandler.sendPacket(new EntityStatusClientPacket(serverPlayerEntity, b));
			}
		}));
		treeMap.put("spectatorsGenerateChunks", new GameRules.Key("true", GameRules.Type.BOOLEAN));
		treeMap.put("spawnRadius", new GameRules.Key("10", GameRules.Type.INTEGER));
		treeMap.put("disableElytraMovementCheck", new GameRules.Key("false", GameRules.Type.BOOLEAN));
		treeMap.put("maxEntityCramming", new GameRules.Key("24", GameRules.Type.INTEGER));
		treeMap.put("doWeatherCycle", new GameRules.Key("true", GameRules.Type.BOOLEAN));
		treeMap.put("doLimitedCrafting", new GameRules.Key("false", GameRules.Type.BOOLEAN));
		treeMap.put("maxCommandChainLength", new GameRules.Key("65536", GameRules.Type.INTEGER));
		treeMap.put("announceAdvancements", new GameRules.Key("true", GameRules.Type.BOOLEAN));
	});
	private final TreeMap<String, GameRules.Value> rules = new TreeMap();

	public GameRules() {
		for (Entry<String, GameRules.Key> entry : KEYS.entrySet()) {
			this.rules.put(entry.getKey(), ((GameRules.Key)entry.getValue()).createValue());
		}
	}

	public void put(String string, String string2, @Nullable MinecraftServer minecraftServer) {
		GameRules.Value value = (GameRules.Value)this.rules.get(string);
		if (value != null) {
			value.set(string2, minecraftServer);
		}
	}

	public boolean getBoolean(String string) {
		GameRules.Value value = (GameRules.Value)this.rules.get(string);
		return value != null ? value.getBoolean() : false;
	}

	public int getInteger(String string) {
		GameRules.Value value = (GameRules.Value)this.rules.get(string);
		return value != null ? value.getInteger() : 0;
	}

	public CompoundTag serialize() {
		CompoundTag compoundTag = new CompoundTag();

		for (String string : this.rules.keySet()) {
			GameRules.Value value = (GameRules.Value)this.rules.get(string);
			compoundTag.putString(string, value.getString());
		}

		return compoundTag;
	}

	public void deserialize(CompoundTag compoundTag) {
		for (String string : compoundTag.getKeys()) {
			this.put(string, compoundTag.getString(string), null);
		}
	}

	public GameRules.Value get(String string) {
		return (GameRules.Value)this.rules.get(string);
	}

	public static TreeMap<String, GameRules.Key> getKeys() {
		return KEYS;
	}

	public static class Key {
		private final GameRules.Type type;
		private final String defaultValue;
		private final BiConsumer<MinecraftServer, GameRules.Value> field_9204;

		public Key(String string, GameRules.Type type) {
			this(string, type, (minecraftServer, value) -> {
			});
		}

		public Key(String string, GameRules.Type type, BiConsumer<MinecraftServer, GameRules.Value> biConsumer) {
			this.type = type;
			this.defaultValue = string;
			this.field_9204 = biConsumer;
		}

		public GameRules.Value createValue() {
			return new GameRules.Value(this.defaultValue, this.type, this.field_9204);
		}

		public GameRules.Type getType() {
			return this.type;
		}
	}

	public static enum Type {
		STRING(StringArgumentType::greedyString, (commandContext, string) -> commandContext.getArgument(string, String.class)),
		BOOLEAN(BoolArgumentType::bool, (commandContext, string) -> commandContext.<Boolean>getArgument(string, Boolean.class).toString()),
		INTEGER(IntegerArgumentType::integer, (commandContext, string) -> commandContext.<Integer>getArgument(string, Integer.class).toString());

		private final Supplier<ArgumentType<?>> field_9208;
		private final BiFunction<CommandContext<ServerCommandSource>, String, String> field_9207;

		private Type(Supplier<ArgumentType<?>> supplier, BiFunction<CommandContext<ServerCommandSource>, String, String> biFunction) {
			this.field_9208 = supplier;
			this.field_9207 = biFunction;
		}

		public RequiredArgumentBuilder<ServerCommandSource, ?> method_8371(String string) {
			return ServerCommandManager.argument(string, (ArgumentType)this.field_9208.get());
		}

		public void method_8370(CommandContext<ServerCommandSource> commandContext, String string, GameRules.Value value) {
			value.set((String)this.field_9207.apply(commandContext, string), commandContext.getSource().getMinecraftServer());
		}
	}

	public static class Value {
		private String asString;
		private boolean asBoolean;
		private int asInteger;
		private double asDouble;
		private final GameRules.Type type;
		private final BiConsumer<MinecraftServer, GameRules.Value> applyConsumer;

		public Value(String string, GameRules.Type type, BiConsumer<MinecraftServer, GameRules.Value> biConsumer) {
			this.type = type;
			this.applyConsumer = biConsumer;
			this.set(string, null);
		}

		public void set(String string, @Nullable MinecraftServer minecraftServer) {
			this.asString = string;
			this.asBoolean = Boolean.parseBoolean(string);
			this.asInteger = this.asBoolean ? 1 : 0;

			try {
				this.asInteger = Integer.parseInt(string);
			} catch (NumberFormatException var5) {
			}

			try {
				this.asDouble = Double.parseDouble(string);
			} catch (NumberFormatException var4) {
			}

			if (minecraftServer != null) {
				this.applyConsumer.accept(minecraftServer, this);
			}
		}

		public String getString() {
			return this.asString;
		}

		public boolean getBoolean() {
			return this.asBoolean;
		}

		public int getInteger() {
			return this.asInteger;
		}

		public GameRules.Type getType() {
			return this.type;
		}
	}
}
