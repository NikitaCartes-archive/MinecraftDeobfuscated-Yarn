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
import net.minecraft.client.network.packet.EntityStatusS2CPacket;
import net.minecraft.client.network.packet.GameStateChangeS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameRules {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<GameRules.RuleKey<?>, GameRules.RuleType<?>> RULES = Maps.newTreeMap(Comparator.comparing(ruleKey -> ruleKey.name));
	public static final GameRules.RuleKey<GameRules.BooleanRule> DO_FIRE_TICK = register("doFireTick", GameRules.BooleanRule.of(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> MOB_GRIEFING = register("mobGriefing", GameRules.BooleanRule.of(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> KEEP_INVENTORY = register("keepInventory", GameRules.BooleanRule.of(false));
	public static final GameRules.RuleKey<GameRules.BooleanRule> DO_MOB_SPAWNING = register("doMobSpawning", GameRules.BooleanRule.of(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> DO_MOB_LOOT = register("doMobLoot", GameRules.BooleanRule.of(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> DO_TILE_DROPS = register("doTileDrops", GameRules.BooleanRule.of(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> DO_ENTITY_DROPS = register("doEntityDrops", GameRules.BooleanRule.of(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> COMMAND_BLOCK_OUTPUT = register("commandBlockOutput", GameRules.BooleanRule.of(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> NATURAL_REGENERATION = register("naturalRegeneration", GameRules.BooleanRule.of(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> DO_DAYLIGHT_CYCLE = register("doDaylightCycle", GameRules.BooleanRule.of(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> LOG_ADMIN_COMMANDS = register("logAdminCommands", GameRules.BooleanRule.of(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> SHOW_DEATH_MESSAGES = register("showDeathMessages", GameRules.BooleanRule.of(true));
	public static final GameRules.RuleKey<GameRules.IntRule> RANDOM_TICK_SPEED = register("randomTickSpeed", GameRules.IntRule.of(3));
	public static final GameRules.RuleKey<GameRules.BooleanRule> SEND_COMMAND_FEEDBACK = register("sendCommandFeedback", GameRules.BooleanRule.of(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> REDUCED_DEBUG_INFO = register(
		"reducedDebugInfo", GameRules.BooleanRule.of(false, (minecraftServer, booleanRule) -> {
			byte b = (byte)(booleanRule.get() ? 22 : 23);

			for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayerList()) {
				serverPlayerEntity.networkHandler.sendPacket(new EntityStatusS2CPacket(serverPlayerEntity, b));
			}
		})
	);
	public static final GameRules.RuleKey<GameRules.BooleanRule> SPECTATORS_GENERATE_CHUNKS = register("spectatorsGenerateChunks", GameRules.BooleanRule.of(true));
	public static final GameRules.RuleKey<GameRules.IntRule> SPAWN_RADIUS = register("spawnRadius", GameRules.IntRule.of(10));
	public static final GameRules.RuleKey<GameRules.BooleanRule> DISABLE_ELYTRA_MOVEMENT_CHECK = register(
		"disableElytraMovementCheck", GameRules.BooleanRule.of(false)
	);
	public static final GameRules.RuleKey<GameRules.IntRule> MAX_ENTITY_CRAMMING = register("maxEntityCramming", GameRules.IntRule.of(24));
	public static final GameRules.RuleKey<GameRules.BooleanRule> DO_WEATHER_CYCLE = register("doWeatherCycle", GameRules.BooleanRule.of(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> DO_LIMITED_CRAFTING = register("doLimitedCrafting", GameRules.BooleanRule.of(false));
	public static final GameRules.RuleKey<GameRules.IntRule> MAX_COMMAND_CHAIN_LENGTH = register("maxCommandChainLength", GameRules.IntRule.of(65536));
	public static final GameRules.RuleKey<GameRules.BooleanRule> ANNOUNCE_ADVANCEMENTS = register("announceAdvancements", GameRules.BooleanRule.of(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> DISABLE_RAIDS = register("disableRaids", GameRules.BooleanRule.of(false));
	public static final GameRules.RuleKey<GameRules.BooleanRule> DO_INSOMNIA = register("doInsomnia", GameRules.BooleanRule.of(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> DO_IMMEDIATE_RESPAWN = register(
		"doImmediateRespawn", GameRules.BooleanRule.of(false, (minecraftServer, booleanRule) -> {
			for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayerList()) {
				serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(11, booleanRule.get() ? 1.0F : 0.0F));
			}
		})
	);
	public static final GameRules.RuleKey<GameRules.BooleanRule> DROWNING_DAMAGE = register("drowningDamage", GameRules.BooleanRule.of(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> FALL_DAMAGE = register("fallDamage", GameRules.BooleanRule.of(true));
	public static final GameRules.RuleKey<GameRules.BooleanRule> FIRE_DAMAGE = register("fireDamage", GameRules.BooleanRule.of(true));
	private final Map<GameRules.RuleKey<?>, GameRules.Rule<?>> rules = (Map<GameRules.RuleKey<?>, GameRules.Rule<?>>)RULES.entrySet()
		.stream()
		.collect(ImmutableMap.toImmutableMap(Entry::getKey, entry -> ((GameRules.RuleType)entry.getValue()).newRule()));

	private static <T extends GameRules.Rule<T>> GameRules.RuleKey<T> register(String string, GameRules.RuleType<T> ruleType) {
		GameRules.RuleKey<T> ruleKey = new GameRules.RuleKey<>(string);
		GameRules.RuleType<?> ruleType2 = (GameRules.RuleType<?>)RULES.put(ruleKey, ruleType);
		if (ruleType2 != null) {
			throw new IllegalStateException("Duplicate game rule registration for " + string);
		} else {
			return ruleKey;
		}
	}

	public <T extends GameRules.Rule<T>> T get(GameRules.RuleKey<T> ruleKey) {
		return (T)this.rules.get(ruleKey);
	}

	public CompoundTag toNbt() {
		CompoundTag compoundTag = new CompoundTag();
		this.rules.forEach((ruleKey, rule) -> compoundTag.putString(ruleKey.name, rule.valueToString()));
		return compoundTag;
	}

	public void fromNbt(CompoundTag compoundTag) {
		this.rules.forEach((ruleKey, rule) -> {
			if (compoundTag.contains(ruleKey.name)) {
				rule.setFromString(compoundTag.getString(ruleKey.name));
			}
		});
	}

	public static void forEach(GameRules.RuleConsumer ruleConsumer) {
		RULES.forEach((ruleKey, ruleType) -> consumeTyped(ruleConsumer, ruleKey, ruleType));
	}

	private static <T extends GameRules.Rule<T>> void consumeTyped(
		GameRules.RuleConsumer ruleConsumer, GameRules.RuleKey<?> ruleKey, GameRules.RuleType<?> ruleType
	) {
		ruleConsumer.accept(ruleKey, ruleType);
	}

	public boolean getBoolean(GameRules.RuleKey<GameRules.BooleanRule> ruleKey) {
		return this.get(ruleKey).get();
	}

	public int getInt(GameRules.RuleKey<GameRules.IntRule> ruleKey) {
		return this.get(ruleKey).get();
	}

	public static class BooleanRule extends GameRules.Rule<GameRules.BooleanRule> {
		private boolean value;

		private static GameRules.RuleType<GameRules.BooleanRule> of(boolean bl, BiConsumer<MinecraftServer, GameRules.BooleanRule> biConsumer) {
			return new GameRules.RuleType<>(BoolArgumentType::bool, ruleType -> new GameRules.BooleanRule(ruleType, bl), biConsumer);
		}

		private static GameRules.RuleType<GameRules.BooleanRule> of(boolean bl) {
			return of(bl, (minecraftServer, booleanRule) -> {
			});
		}

		public BooleanRule(GameRules.RuleType<GameRules.BooleanRule> ruleType, boolean bl) {
			super(ruleType);
			this.value = bl;
		}

		@Override
		protected void setFromArgument(CommandContext<ServerCommandSource> commandContext, String string) {
			this.value = BoolArgumentType.getBool(commandContext, string);
		}

		public boolean get() {
			return this.value;
		}

		public void set(boolean bl, @Nullable MinecraftServer minecraftServer) {
			this.value = bl;
			this.notify(minecraftServer);
		}

		@Override
		protected String valueToString() {
			return Boolean.toString(this.value);
		}

		@Override
		protected void setFromString(String string) {
			this.value = Boolean.parseBoolean(string);
		}

		@Override
		public int toCommandResult() {
			return this.value ? 1 : 0;
		}

		protected GameRules.BooleanRule method_20761() {
			return this;
		}
	}

	public static class IntRule extends GameRules.Rule<GameRules.IntRule> {
		private int value;

		private static GameRules.RuleType<GameRules.IntRule> of(int i, BiConsumer<MinecraftServer, GameRules.IntRule> biConsumer) {
			return new GameRules.RuleType<>(IntegerArgumentType::integer, ruleType -> new GameRules.IntRule(ruleType, i), biConsumer);
		}

		private static GameRules.RuleType<GameRules.IntRule> of(int i) {
			return of(i, (minecraftServer, intRule) -> {
			});
		}

		public IntRule(GameRules.RuleType<GameRules.IntRule> ruleType, int i) {
			super(ruleType);
			this.value = i;
		}

		@Override
		protected void setFromArgument(CommandContext<ServerCommandSource> commandContext, String string) {
			this.value = IntegerArgumentType.getInteger(commandContext, string);
		}

		public int get() {
			return this.value;
		}

		@Override
		protected String valueToString() {
			return Integer.toString(this.value);
		}

		@Override
		protected void setFromString(String string) {
			this.value = parseInt(string);
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
		public int toCommandResult() {
			return this.value;
		}

		protected GameRules.IntRule method_20770() {
			return this;
		}
	}

	public abstract static class Rule<T extends GameRules.Rule<T>> {
		private final GameRules.RuleType<T> type;

		public Rule(GameRules.RuleType<T> ruleType) {
			this.type = ruleType;
		}

		protected abstract void setFromArgument(CommandContext<ServerCommandSource> commandContext, String string);

		public void set(CommandContext<ServerCommandSource> commandContext, String string) {
			this.setFromArgument(commandContext, string);
			this.notify(commandContext.getSource().getMinecraftServer());
		}

		protected void notify(@Nullable MinecraftServer minecraftServer) {
			if (minecraftServer != null) {
				this.type.notifier.accept(minecraftServer, this.getThis());
			}
		}

		protected abstract void setFromString(String string);

		protected abstract String valueToString();

		public String toString() {
			return this.valueToString();
		}

		public abstract int toCommandResult();

		protected abstract T getThis();
	}

	@FunctionalInterface
	public interface RuleConsumer {
		<T extends GameRules.Rule<T>> void accept(GameRules.RuleKey<T> ruleKey, GameRules.RuleType<T> ruleType);
	}

	public static final class RuleKey<T extends GameRules.Rule<T>> {
		private final String name;

		public RuleKey(String string) {
			this.name = string;
		}

		public String toString() {
			return this.name;
		}

		public boolean equals(Object object) {
			return this == object ? true : object instanceof GameRules.RuleKey && ((GameRules.RuleKey)object).name.equals(this.name);
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
		private final Function<GameRules.RuleType<T>, T> factory;
		private final BiConsumer<MinecraftServer, T> notifier;

		private RuleType(Supplier<ArgumentType<?>> supplier, Function<GameRules.RuleType<T>, T> function, BiConsumer<MinecraftServer, T> biConsumer) {
			this.argumentType = supplier;
			this.factory = function;
			this.notifier = biConsumer;
		}

		public RequiredArgumentBuilder<ServerCommandSource, ?> argument(String string) {
			return CommandManager.argument(string, (ArgumentType<T>)this.argumentType.get());
		}

		public T newRule() {
			return (T)this.factory.apply(this);
		}
	}
}
