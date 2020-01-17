package net.minecraft.world;

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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameRules {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<GameRules.RuleKey<?>, GameRules.RuleType<?>> RULE_TYPES = Maps.newTreeMap(Comparator.comparing(key -> key.name));
	public static final GameRules.RuleKey<GameRules.BooleanRule> DO_FIRE_TICK = register("doFireTick", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> MOB_GRIEFING = register("mobGriefing", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> KEEP_INVENTORY = register("keepInventory", GameRules.BooleanRule.create(false));
	public static final GameRules.RuleKey<GameRules.BooleanRule> DO_MOB_SPAWNING = register("doMobSpawning", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> DO_MOB_LOOT = register("doMobLoot", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> DO_TILE_DROPS = register("doTileDrops", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> DO_ENTITY_DROPS = register("doEntityDrops", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> COMMAND_BLOCK_OUTPUT = register("commandBlockOutput", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> NATURAL_REGENERATION = register("naturalRegeneration", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> DO_DAYLIGHT_CYCLE = register("doDaylightCycle", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> LOG_ADMIN_COMMANDS = register("logAdminCommands", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> SHOW_DEATH_MESSAGES = register("showDeathMessages", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.IntRule> RANDOM_TICK_SPEED = register("randomTickSpeed", GameRules.IntRule.create(3));
	public static final GameRules.RuleKey<GameRules.BooleanRule> SEND_COMMAND_FEEDBACK = register("sendCommandFeedback", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> REDUCED_DEBUG_INFO = register(
		"reducedDebugInfo", GameRules.BooleanRule.create(false, (server, rule) -> {
			byte b = (byte)(rule.get() ? 22 : 23);

			for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
				serverPlayerEntity.networkHandler.sendPacket(new EntityStatusS2CPacket(serverPlayerEntity, b));
			}
		})
	);
	public static final GameRules.RuleKey<GameRules.BooleanRule> SPECTATORS_GENERATE_CHUNKS = register(
		"spectatorsGenerateChunks", GameRules.BooleanRule.create(true)
	);
	public static final GameRules.RuleKey<GameRules.IntRule> SPAWN_RADIUS = register("spawnRadius", GameRules.IntRule.create(10));
	public static final GameRules.RuleKey<GameRules.BooleanRule> DISABLE_ELYTRA_MOVEMENT_CHECK = register(
		"disableElytraMovementCheck", GameRules.BooleanRule.create(false)
	);
	public static final GameRules.RuleKey<GameRules.IntRule> MAX_ENTITY_CRAMMING = register("maxEntityCramming", GameRules.IntRule.create(24));
	public static final GameRules.RuleKey<GameRules.BooleanRule> DO_WEATHER_CYCLE = register("doWeatherCycle", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> DO_LIMITED_CRAFTING = register("doLimitedCrafting", GameRules.BooleanRule.create(false));
	public static final GameRules.RuleKey<GameRules.IntRule> MAX_COMMAND_CHAIN_LENGTH = register("maxCommandChainLength", GameRules.IntRule.create(65536));
	public static final GameRules.RuleKey<GameRules.BooleanRule> ANNOUNCE_ADVANCEMENTS = register("announceAdvancements", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> DISABLE_RAIDS = register("disableRaids", GameRules.BooleanRule.create(false));
	public static final GameRules.RuleKey<GameRules.BooleanRule> DO_INSOMNIA = register("doInsomnia", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> DO_IMMEDIATE_RESPAWN = register(
		"doImmediateRespawn", GameRules.BooleanRule.create(false, (server, rule) -> {
			for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
				serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(11, rule.get() ? 1.0F : 0.0F));
			}
		})
	);
	public static final GameRules.RuleKey<GameRules.BooleanRule> DROWNING_DAMAGE = register("drowningDamage", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> FALL_DAMAGE = register("fallDamage", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> FIRE_DAMAGE = register("fireDamage", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> DO_PATROL_SPAWNING = register("doPatrolSpawning", GameRules.BooleanRule.create(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> DO_TRADER_SPAWNING = register("doTraderSpawning", GameRules.BooleanRule.create(true));
	private final Map<GameRules.RuleKey<?>, GameRules.Rule<?>> rules = (Map<GameRules.RuleKey<?>, GameRules.Rule<?>>)RULE_TYPES.entrySet()
		.stream()
		.collect(ImmutableMap.toImmutableMap(Entry::getKey, e -> ((GameRules.RuleType)e.getValue()).createRule()));

	private static <T extends GameRules.Rule<T>> GameRules.RuleKey<T> register(String name, GameRules.RuleType<T> type) {
		GameRules.RuleKey<T> ruleKey = new GameRules.RuleKey<>(name);
		GameRules.RuleType<?> ruleType = (GameRules.RuleType<?>)RULE_TYPES.put(ruleKey, type);
		if (ruleType != null) {
			throw new IllegalStateException("Duplicate game rule registration for " + name);
		} else {
			return ruleKey;
		}
	}

	public <T extends GameRules.Rule<T>> T get(GameRules.RuleKey<T> key) {
		return (T)this.rules.get(key);
	}

	public CompoundTag toNbt() {
		CompoundTag compoundTag = new CompoundTag();
		this.rules.forEach((key, rule) -> compoundTag.putString(key.name, rule.serialize()));
		return compoundTag;
	}

	public void load(CompoundTag nbt) {
		this.rules.forEach((key, rule) -> {
			if (nbt.contains(key.name)) {
				rule.deserialize(nbt.getString(key.name));
			}
		});
	}

	public static void forEachType(GameRules.RuleTypeConsumer action) {
		RULE_TYPES.forEach((key, type) -> accept(action, key, type));
	}

	private static <T extends GameRules.Rule<T>> void accept(GameRules.RuleTypeConsumer consumer, GameRules.RuleKey<?> key, GameRules.RuleType<?> type) {
		consumer.accept(key, type);
	}

	public boolean getBoolean(GameRules.RuleKey<GameRules.BooleanRule> rule) {
		return this.get(rule).get();
	}

	public int getInt(GameRules.RuleKey<GameRules.IntRule> rule) {
		return this.get(rule).get();
	}

	public static class BooleanRule extends GameRules.Rule<GameRules.BooleanRule> {
		private boolean value;

		private static GameRules.RuleType<GameRules.BooleanRule> create(boolean initialValue, BiConsumer<MinecraftServer, GameRules.BooleanRule> changeCallback) {
			return new GameRules.RuleType<>(BoolArgumentType::bool, type -> new GameRules.BooleanRule(type, initialValue), changeCallback);
		}

		private static GameRules.RuleType<GameRules.BooleanRule> create(boolean initialValue) {
			return create(initialValue, (server, rule) -> {
			});
		}

		public BooleanRule(GameRules.RuleType<GameRules.BooleanRule> type, boolean initialValue) {
			super(type);
			this.value = initialValue;
		}

		@Override
		protected void setFromArgument(CommandContext<ServerCommandSource> context, String name) {
			this.value = BoolArgumentType.getBool(context, name);
		}

		public boolean get() {
			return this.value;
		}

		public void set(boolean value, @Nullable MinecraftServer server) {
			this.value = value;
			this.changed(server);
		}

		@Override
		protected String serialize() {
			return Boolean.toString(this.value);
		}

		@Override
		protected void deserialize(String value) {
			this.value = Boolean.parseBoolean(value);
		}

		@Override
		public int getCommandResult() {
			return this.value ? 1 : 0;
		}

		protected GameRules.BooleanRule getThis() {
			return this;
		}
	}

	public static class IntRule extends GameRules.Rule<GameRules.IntRule> {
		private int value;

		private static GameRules.RuleType<GameRules.IntRule> create(int initialValue, BiConsumer<MinecraftServer, GameRules.IntRule> changeCallback) {
			return new GameRules.RuleType<>(IntegerArgumentType::integer, type -> new GameRules.IntRule(type, initialValue), changeCallback);
		}

		private static GameRules.RuleType<GameRules.IntRule> create(int initialValue) {
			return create(initialValue, (server, rule) -> {
			});
		}

		public IntRule(GameRules.RuleType<GameRules.IntRule> rule, int initialValue) {
			super(rule);
			this.value = initialValue;
		}

		@Override
		protected void setFromArgument(CommandContext<ServerCommandSource> context, String name) {
			this.value = IntegerArgumentType.getInteger(context, name);
		}

		public int get() {
			return this.value;
		}

		@Override
		protected String serialize() {
			return Integer.toString(this.value);
		}

		@Override
		protected void deserialize(String value) {
			this.value = parseInt(value);
		}

		private static int parseInt(String string) {
			if (!string.isEmpty()) {
				try {
					return Integer.parseInt(string);
				} catch (NumberFormatException var2) {
					GameRules.LOGGER.warn("Failed to parse integer {}", string);
				}
			}

			return 0;
		}

		@Override
		public int getCommandResult() {
			return this.value;
		}

		protected GameRules.IntRule getThis() {
			return this;
		}
	}

	public abstract static class Rule<T extends GameRules.Rule<T>> {
		private final GameRules.RuleType<T> type;

		public Rule(GameRules.RuleType<T> type) {
			this.type = type;
		}

		protected abstract void setFromArgument(CommandContext<ServerCommandSource> context, String name);

		public void set(CommandContext<ServerCommandSource> context, String name) {
			this.setFromArgument(context, name);
			this.changed(context.getSource().getMinecraftServer());
		}

		protected void changed(@Nullable MinecraftServer server) {
			if (server != null) {
				this.type.changeCallback.accept(server, this.getThis());
			}
		}

		protected abstract void deserialize(String value);

		protected abstract String serialize();

		public String toString() {
			return this.serialize();
		}

		public abstract int getCommandResult();

		protected abstract T getThis();
	}

	public static final class RuleKey<T extends GameRules.Rule<T>> {
		private final String name;

		public RuleKey(String name) {
			this.name = name;
		}

		public String toString() {
			return this.name;
		}

		public boolean equals(Object obj) {
			return this == obj ? true : obj instanceof GameRules.RuleKey && ((GameRules.RuleKey)obj).name.equals(this.name);
		}

		public int hashCode() {
			return this.name.hashCode();
		}

		public String getName() {
			return this.name;
		}
	}

	public static class RuleType<T extends GameRules.Rule<T>> {
		private final Supplier<ArgumentType<?>> argumentType;
		private final Function<GameRules.RuleType<T>, T> ruleFactory;
		private final BiConsumer<MinecraftServer, T> changeCallback;

		private RuleType(Supplier<ArgumentType<?>> argumentType, Function<GameRules.RuleType<T>, T> ruleFactory, BiConsumer<MinecraftServer, T> changeCallback) {
			this.argumentType = argumentType;
			this.ruleFactory = ruleFactory;
			this.changeCallback = changeCallback;
		}

		public RequiredArgumentBuilder<ServerCommandSource, ?> argument(String name) {
			return CommandManager.argument(name, (ArgumentType<T>)this.argumentType.get());
		}

		public T createRule() {
			return (T)this.ruleFactory.apply(this);
		}
	}

	@FunctionalInterface
	public interface RuleTypeConsumer {
		<T extends GameRules.Rule<T>> void accept(GameRules.RuleKey<T> key, GameRules.RuleType<T> type);
	}
}
