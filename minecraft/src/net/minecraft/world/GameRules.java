package net.minecraft.world;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicLike;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;

public class GameRules {
	public static final int DEFAULT_RANDOM_TICK_SPEED = 3;
	static final Logger LOGGER = LogUtils.getLogger();
	private static final Map<GameRules.Key<?>, GameRules.Type<?>> RULE_TYPES = Maps.newTreeMap(Comparator.comparing(key -> key.name));
	public static final GameRules.Key<GameRules.BooleanRule> DO_FIRE_TICK = register("doFireTick", GameRules.Category.UPDATES, GameRules.BooleanRule.create(true));
	/**
	 * A {@linkplain Rule game rule} which regulates whether mobs can modify the world.
	 * 
	 * <p>Generally one is expected to test this rule before an entity modifies the world.
	 * 
	 * <p>In vanilla, this includes:
	 * <ul>
	 * <li>Whether creeper explosions destroy blocks
	 * <li>Whether a zombie can break down a door
	 * <li>Whether a wither killing an entity will place or drop a wither rose
	 * </ul>
	 */
	public static final GameRules.Key<GameRules.BooleanRule> DO_MOB_GRIEFING = register("mobGriefing", GameRules.Category.MOBS, GameRules.BooleanRule.create(true));
	/**
	 * A {@linkplain Rule game rule} which regulates whether player inventories should be persist through respawning.
	 */
	public static final GameRules.Key<GameRules.BooleanRule> KEEP_INVENTORY = register(
		"keepInventory", GameRules.Category.PLAYER, GameRules.BooleanRule.create(false)
	);
	/**
	 * A {@linkplain Rule game rule} which regulates whether mobs can spawn naturally.
	 */
	public static final GameRules.Key<GameRules.BooleanRule> DO_MOB_SPAWNING = register(
		"doMobSpawning", GameRules.Category.SPAWNING, GameRules.BooleanRule.create(true)
	);
	/**
	 * A {@linkplain Rule game rule} which regulates whether mobs should drop loot on death.
	 */
	public static final GameRules.Key<GameRules.BooleanRule> DO_MOB_LOOT = register("doMobLoot", GameRules.Category.DROPS, GameRules.BooleanRule.create(true));
	public static final GameRules.Key<GameRules.BooleanRule> PROJECTILES_CAN_BREAK_BLOCKS = register(
		"projectilesCanBreakBlocks", GameRules.Category.DROPS, GameRules.BooleanRule.create(true)
	);
	/**
	 * A {@linkplain Rule game rule} which regulates whether blocks should drop their items when broken.
	 */
	public static final GameRules.Key<GameRules.BooleanRule> DO_TILE_DROPS = register("doTileDrops", GameRules.Category.DROPS, GameRules.BooleanRule.create(true));
	public static final GameRules.Key<GameRules.BooleanRule> DO_ENTITY_DROPS = register(
		"doEntityDrops", GameRules.Category.DROPS, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> COMMAND_BLOCK_OUTPUT = register(
		"commandBlockOutput", GameRules.Category.CHAT, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> NATURAL_REGENERATION = register(
		"naturalRegeneration", GameRules.Category.PLAYER, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> DO_DAYLIGHT_CYCLE = register(
		"doDaylightCycle", GameRules.Category.UPDATES, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> LOG_ADMIN_COMMANDS = register(
		"logAdminCommands", GameRules.Category.CHAT, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> SHOW_DEATH_MESSAGES = register(
		"showDeathMessages", GameRules.Category.CHAT, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.IntRule> RANDOM_TICK_SPEED = register("randomTickSpeed", GameRules.Category.UPDATES, GameRules.IntRule.create(3));
	public static final GameRules.Key<GameRules.BooleanRule> SEND_COMMAND_FEEDBACK = register(
		"sendCommandFeedback", GameRules.Category.CHAT, GameRules.BooleanRule.create(true)
	);
	/**
	 * A {@linkplain Rule game rule} which regulates whether clients' {@linkplain net.minecraft.client.gui.hud.DebugHud debug HUD}s show reduced information.
	 * 
	 * <p>When the value of this rule is changed, all connected clients will be notified to update their display.
	 * In vanilla, this includes the visibility of coordinates on the clients' debug HUDs.
	 */
	public static final GameRules.Key<GameRules.BooleanRule> REDUCED_DEBUG_INFO = register(
		"reducedDebugInfo", GameRules.Category.MISC, GameRules.BooleanRule.create(false, (server, rule) -> {
			byte b = rule.get() ? EntityStatuses.USE_REDUCED_DEBUG_INFO : EntityStatuses.USE_FULL_DEBUG_INFO;

			for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
				serverPlayerEntity.networkHandler.sendPacket(new EntityStatusS2CPacket(serverPlayerEntity, b));
			}
		})
	);
	public static final GameRules.Key<GameRules.BooleanRule> SPECTATORS_GENERATE_CHUNKS = register(
		"spectatorsGenerateChunks", GameRules.Category.PLAYER, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.IntRule> SPAWN_RADIUS = register("spawnRadius", GameRules.Category.PLAYER, GameRules.IntRule.create(10));
	public static final GameRules.Key<GameRules.BooleanRule> DISABLE_ELYTRA_MOVEMENT_CHECK = register(
		"disableElytraMovementCheck", GameRules.Category.PLAYER, GameRules.BooleanRule.create(false)
	);
	/**
	 * A {@linkplain Rule game rule} which regulates the number of entities that can be crammed into a block space before they incur cramming damage.
	 */
	public static final GameRules.Key<GameRules.IntRule> MAX_ENTITY_CRAMMING = register("maxEntityCramming", GameRules.Category.MOBS, GameRules.IntRule.create(24));
	public static final GameRules.Key<GameRules.BooleanRule> DO_WEATHER_CYCLE = register(
		"doWeatherCycle", GameRules.Category.UPDATES, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> DO_LIMITED_CRAFTING = register(
		"doLimitedCrafting",
		GameRules.Category.PLAYER,
		GameRules.BooleanRule.create(
			false,
			(server, rule) -> {
				for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
					serverPlayerEntity.networkHandler
						.sendPacket(
							new GameStateChangeS2CPacket(GameStateChangeS2CPacket.LIMITED_CRAFTING_TOGGLED, rule.get() ? 1.0F : GameStateChangeS2CPacket.DEMO_OPEN_SCREEN)
						);
				}
			}
		)
	);
	public static final GameRules.Key<GameRules.IntRule> MAX_COMMAND_CHAIN_LENGTH = register(
		"maxCommandChainLength", GameRules.Category.MISC, GameRules.IntRule.create(65536)
	);
	public static final GameRules.Key<GameRules.IntRule> MAX_COMMAND_FORK_COUNT = register(
		"maxCommandForkCount", GameRules.Category.MISC, GameRules.IntRule.create(65536)
	);
	public static final GameRules.Key<GameRules.IntRule> COMMAND_MODIFICATION_BLOCK_LIMIT = register(
		"commandModificationBlockLimit", GameRules.Category.MISC, GameRules.IntRule.create(32768)
	);
	/**
	 * A {@linkplain Rule game rule} which regulates whether a player's advancements should be announced in chat.
	 */
	public static final GameRules.Key<GameRules.BooleanRule> ANNOUNCE_ADVANCEMENTS = register(
		"announceAdvancements", GameRules.Category.CHAT, GameRules.BooleanRule.create(true)
	);
	/**
	 * A {@linkplain Rule game rule} which regulates whether raids should occur.
	 * 
	 * <p>If this rule is set to {@code true} while raids are occurring, the raids will be stopped.
	 */
	public static final GameRules.Key<GameRules.BooleanRule> DISABLE_RAIDS = register("disableRaids", GameRules.Category.MOBS, GameRules.BooleanRule.create(false));
	public static final GameRules.Key<GameRules.BooleanRule> DO_INSOMNIA = register("doInsomnia", GameRules.Category.SPAWNING, GameRules.BooleanRule.create(true));
	/**
	 * A {@linkplain Rule game rule} which regulates whether a player should immediately respawn upon death.
	 */
	public static final GameRules.Key<GameRules.BooleanRule> DO_IMMEDIATE_RESPAWN = register(
		"doImmediateRespawn",
		GameRules.Category.PLAYER,
		GameRules.BooleanRule.create(
			false,
			(server, rule) -> {
				for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
					serverPlayerEntity.networkHandler
						.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.IMMEDIATE_RESPAWN, rule.get() ? 1.0F : GameStateChangeS2CPacket.DEMO_OPEN_SCREEN));
				}
			}
		)
	);
	public static final GameRules.Key<GameRules.IntRule> PLAYERS_NETHER_PORTAL_DEFAULT_DELAY = register(
		"playersNetherPortalDefaultDelay", GameRules.Category.PLAYER, GameRules.IntRule.create(80)
	);
	public static final GameRules.Key<GameRules.IntRule> PLAYERS_NETHER_PORTAL_CREATIVE_DELAY = register(
		"playersNetherPortalCreativeDelay", GameRules.Category.PLAYER, GameRules.IntRule.create(0)
	);
	public static final GameRules.Key<GameRules.BooleanRule> DROWNING_DAMAGE = register(
		"drowningDamage", GameRules.Category.PLAYER, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> FALL_DAMAGE = register("fallDamage", GameRules.Category.PLAYER, GameRules.BooleanRule.create(true));
	public static final GameRules.Key<GameRules.BooleanRule> FIRE_DAMAGE = register("fireDamage", GameRules.Category.PLAYER, GameRules.BooleanRule.create(true));
	public static final GameRules.Key<GameRules.BooleanRule> FREEZE_DAMAGE = register(
		"freezeDamage", GameRules.Category.PLAYER, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> DO_PATROL_SPAWNING = register(
		"doPatrolSpawning", GameRules.Category.SPAWNING, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> DO_TRADER_SPAWNING = register(
		"doTraderSpawning", GameRules.Category.SPAWNING, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> DO_WARDEN_SPAWNING = register(
		"doWardenSpawning", GameRules.Category.SPAWNING, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> FORGIVE_DEAD_PLAYERS = register(
		"forgiveDeadPlayers", GameRules.Category.MOBS, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> UNIVERSAL_ANGER = register(
		"universalAnger", GameRules.Category.MOBS, GameRules.BooleanRule.create(false)
	);
	public static final GameRules.Key<GameRules.IntRule> PLAYERS_SLEEPING_PERCENTAGE = register(
		"playersSleepingPercentage", GameRules.Category.PLAYER, GameRules.IntRule.create(100)
	);
	public static final GameRules.Key<GameRules.BooleanRule> BLOCK_EXPLOSION_DROP_DECAY = register(
		"blockExplosionDropDecay", GameRules.Category.DROPS, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> MOB_EXPLOSION_DROP_DECAY = register(
		"mobExplosionDropDecay", GameRules.Category.DROPS, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> TNT_EXPLOSION_DROP_DECAY = register(
		"tntExplosionDropDecay", GameRules.Category.DROPS, GameRules.BooleanRule.create(false)
	);
	public static final GameRules.Key<GameRules.IntRule> SNOW_ACCUMULATION_HEIGHT = register(
		"snowAccumulationHeight", GameRules.Category.UPDATES, GameRules.IntRule.create(1)
	);
	public static final GameRules.Key<GameRules.BooleanRule> WATER_SOURCE_CONVERSION = register(
		"waterSourceConversion", GameRules.Category.UPDATES, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> LAVA_SOURCE_CONVERSION = register(
		"lavaSourceConversion", GameRules.Category.UPDATES, GameRules.BooleanRule.create(false)
	);
	public static final GameRules.Key<GameRules.BooleanRule> GLOBAL_SOUND_EVENTS = register(
		"globalSoundEvents", GameRules.Category.MISC, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> DO_VINES_SPREAD = register(
		"doVinesSpread", GameRules.Category.UPDATES, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> ENDER_PEARLS_VANISH_ON_DEATH = register(
		"enderPearlsVanishOnDeath", GameRules.Category.PLAYER, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.IntRule> MINECART_MAX_SPEED = register(
		"minecartMaxSpeed", GameRules.Category.MISC, GameRules.IntRule.create(8, 1, 1000, FeatureSet.of(FeatureFlags.MINECART_IMPROVEMENTS), (server, value) -> {
		})
	);
	public static final GameRules.Key<GameRules.IntRule> SPAWN_CHUNK_RADIUS = register(
		"spawnChunkRadius", GameRules.Category.MISC, GameRules.IntRule.create(2, 0, 32, FeatureSet.empty(), (server, value) -> {
			ServerWorld serverWorld = server.getOverworld();
			serverWorld.setSpawnPos(serverWorld.getSpawnPos(), serverWorld.getSpawnAngle());
		})
	);
	private final Map<GameRules.Key<?>, GameRules.Rule<?>> rules;
	private final FeatureSet enabledFeatures;

	private static <T extends GameRules.Rule<T>> GameRules.Key<T> register(String name, GameRules.Category category, GameRules.Type<T> type) {
		GameRules.Key<T> key = new GameRules.Key<>(name, category);
		GameRules.Type<?> type2 = (GameRules.Type<?>)RULE_TYPES.put(key, type);
		if (type2 != null) {
			throw new IllegalStateException("Duplicate game rule registration for " + name);
		} else {
			return key;
		}
	}

	public GameRules(FeatureSet enabledFeatures, DynamicLike<?> values) {
		this(enabledFeatures);
		this.load(values);
	}

	public GameRules(FeatureSet enabledFeatures) {
		this(
			(Map<GameRules.Key<?>, GameRules.Rule<?>>)streamAllRules(enabledFeatures)
				.collect(ImmutableMap.toImmutableMap(Entry::getKey, entry -> ((GameRules.Type)entry.getValue()).createRule())),
			enabledFeatures
		);
	}

	private static Stream<Entry<GameRules.Key<?>, GameRules.Type<?>>> streamAllRules(FeatureSet enabledFeatures) {
		return RULE_TYPES.entrySet().stream().filter(entry -> ((GameRules.Type)entry.getValue()).requiredFeatures.isSubsetOf(enabledFeatures));
	}

	private GameRules(Map<GameRules.Key<?>, GameRules.Rule<?>> rules, FeatureSet enabledFeatures) {
		this.rules = rules;
		this.enabledFeatures = enabledFeatures;
	}

	public <T extends GameRules.Rule<T>> T get(GameRules.Key<T> key) {
		T rule = (T)this.rules.get(key);
		if (rule == null) {
			throw new IllegalArgumentException("Tried to access invalid game rule");
		} else {
			return rule;
		}
	}

	public NbtCompound toNbt() {
		NbtCompound nbtCompound = new NbtCompound();
		this.rules.forEach((key, rule) -> nbtCompound.putString(key.name, rule.serialize()));
		return nbtCompound;
	}

	private void load(DynamicLike<?> values) {
		this.rules.forEach((key, rule) -> values.get(key.name).asString().ifSuccess(rule::deserialize));
	}

	public GameRules copy(FeatureSet enabledFeatures) {
		return new GameRules(
			(Map<GameRules.Key<?>, GameRules.Rule<?>>)streamAllRules(enabledFeatures)
				.collect(
					ImmutableMap.toImmutableMap(
						Entry::getKey,
						entry -> this.rules.containsKey(entry.getKey()) ? (GameRules.Rule)this.rules.get(entry.getKey()) : ((GameRules.Type)entry.getValue()).createRule()
					)
				),
			enabledFeatures
		);
	}

	/**
	 * Make the visitor visit all registered game rules.
	 * 
	 * <p>The visitation involves calling both {@link Visitor#visit(GameRules.Key, GameRules.Type)} and {@code visitX} for every game rule, where X is the current rule's concrete type such as a boolean.
	 */
	public void accept(GameRules.Visitor visitor) {
		RULE_TYPES.forEach((key, type) -> this.accept(visitor, key, type));
	}

	private <T extends GameRules.Rule<T>> void accept(GameRules.Visitor visitor, GameRules.Key<?> key, GameRules.Type<?> type) {
		if (type.requiredFeatures.isSubsetOf(this.enabledFeatures)) {
			visitor.visit(key, type);
			type.accept(visitor, key);
		}
	}

	public void setAllValues(GameRules rules, @Nullable MinecraftServer server) {
		rules.rules.keySet().forEach(key -> this.setValue(key, rules, server));
	}

	private <T extends GameRules.Rule<T>> void setValue(GameRules.Key<T> key, GameRules rules, @Nullable MinecraftServer server) {
		T rule = rules.get(key);
		this.<T>get(key).setValue(rule, server);
	}

	public boolean getBoolean(GameRules.Key<GameRules.BooleanRule> rule) {
		return this.get(rule).get();
	}

	public int getInt(GameRules.Key<GameRules.IntRule> rule) {
		return this.get(rule).get();
	}

	interface Acceptor<T extends GameRules.Rule<T>> {
		void call(GameRules.Visitor consumer, GameRules.Key<T> key, GameRules.Type<T> type);
	}

	public static class BooleanRule extends GameRules.Rule<GameRules.BooleanRule> {
		private boolean value;

		static GameRules.Type<GameRules.BooleanRule> create(boolean initialValue, BiConsumer<MinecraftServer, GameRules.BooleanRule> changeCallback) {
			return new GameRules.Type<>(
				BoolArgumentType::bool, type -> new GameRules.BooleanRule(type, initialValue), changeCallback, GameRules.Visitor::visitBoolean, FeatureSet.empty()
			);
		}

		static GameRules.Type<GameRules.BooleanRule> create(boolean initialValue) {
			return create(initialValue, (server, rule) -> {
			});
		}

		public BooleanRule(GameRules.Type<GameRules.BooleanRule> type, boolean initialValue) {
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
		public String serialize() {
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

		protected GameRules.BooleanRule copy() {
			return new GameRules.BooleanRule(this.type, this.value);
		}

		public void setValue(GameRules.BooleanRule booleanRule, @Nullable MinecraftServer minecraftServer) {
			this.value = booleanRule.value;
			this.changed(minecraftServer);
		}
	}

	public static enum Category {
		PLAYER("gamerule.category.player"),
		MOBS("gamerule.category.mobs"),
		SPAWNING("gamerule.category.spawning"),
		DROPS("gamerule.category.drops"),
		UPDATES("gamerule.category.updates"),
		CHAT("gamerule.category.chat"),
		MISC("gamerule.category.misc");

		private final String category;

		private Category(final String category) {
			this.category = category;
		}

		public String getCategory() {
			return this.category;
		}
	}

	public static class IntRule extends GameRules.Rule<GameRules.IntRule> {
		private int value;

		private static GameRules.Type<GameRules.IntRule> create(int initialValue, BiConsumer<MinecraftServer, GameRules.IntRule> changeCallback) {
			return new GameRules.Type<>(
				IntegerArgumentType::integer, type -> new GameRules.IntRule(type, initialValue), changeCallback, GameRules.Visitor::visitInt, FeatureSet.empty()
			);
		}

		static GameRules.Type<GameRules.IntRule> create(
			int initialValue, int min, int max, FeatureSet requiredFeatures, BiConsumer<MinecraftServer, GameRules.IntRule> changeCallback
		) {
			return new GameRules.Type<>(
				() -> IntegerArgumentType.integer(min, max),
				type -> new GameRules.IntRule(type, initialValue),
				changeCallback,
				GameRules.Visitor::visitInt,
				requiredFeatures
			);
		}

		static GameRules.Type<GameRules.IntRule> create(int initialValue) {
			return create(initialValue, (server, rule) -> {
			});
		}

		public IntRule(GameRules.Type<GameRules.IntRule> rule, int initialValue) {
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

		public void set(int value, @Nullable MinecraftServer server) {
			this.value = value;
			this.changed(server);
		}

		@Override
		public String serialize() {
			return Integer.toString(this.value);
		}

		@Override
		protected void deserialize(String value) {
			this.value = parseInt(value);
		}

		/**
		 * Validates that an input is valid for this rule, and sets the rule's value if successful.
		 * 
		 * @return whether the input is valid
		 */
		public boolean validateAndSet(String input) {
			try {
				StringReader stringReader = new StringReader(input);
				this.value = (Integer)((ArgumentType)this.type.argumentType.get()).parse(stringReader);
				return !stringReader.canRead();
			} catch (CommandSyntaxException var3) {
				return false;
			}
		}

		private static int parseInt(String input) {
			if (!input.isEmpty()) {
				try {
					return Integer.parseInt(input);
				} catch (NumberFormatException var2) {
					GameRules.LOGGER.warn("Failed to parse integer {}", input);
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

		protected GameRules.IntRule copy() {
			return new GameRules.IntRule(this.type, this.value);
		}

		public void setValue(GameRules.IntRule intRule, @Nullable MinecraftServer minecraftServer) {
			this.value = intRule.value;
			this.changed(minecraftServer);
		}
	}

	public static final class Key<T extends GameRules.Rule<T>> {
		final String name;
		private final GameRules.Category category;

		public Key(String name, GameRules.Category category) {
			this.name = name;
			this.category = category;
		}

		public String toString() {
			return this.name;
		}

		public boolean equals(Object o) {
			return this == o ? true : o instanceof GameRules.Key && ((GameRules.Key)o).name.equals(this.name);
		}

		public int hashCode() {
			return this.name.hashCode();
		}

		public String getName() {
			return this.name;
		}

		public String getTranslationKey() {
			return "gamerule." + this.name;
		}

		public GameRules.Category getCategory() {
			return this.category;
		}
	}

	public abstract static class Rule<T extends GameRules.Rule<T>> {
		protected final GameRules.Type<T> type;

		public Rule(GameRules.Type<T> type) {
			this.type = type;
		}

		protected abstract void setFromArgument(CommandContext<ServerCommandSource> context, String name);

		public void set(CommandContext<ServerCommandSource> context, String name) {
			this.setFromArgument(context, name);
			this.changed(context.getSource().getServer());
		}

		protected void changed(@Nullable MinecraftServer server) {
			if (server != null) {
				this.type.changeCallback.accept(server, this.getThis());
			}
		}

		protected abstract void deserialize(String value);

		public abstract String serialize();

		public String toString() {
			return this.serialize();
		}

		public abstract int getCommandResult();

		protected abstract T getThis();

		protected abstract T copy();

		public abstract void setValue(T rule, @Nullable MinecraftServer server);
	}

	public static class Type<T extends GameRules.Rule<T>> {
		final Supplier<ArgumentType<?>> argumentType;
		private final Function<GameRules.Type<T>, T> ruleFactory;
		final BiConsumer<MinecraftServer, T> changeCallback;
		private final GameRules.Acceptor<T> ruleAcceptor;
		final FeatureSet requiredFeatures;

		Type(
			Supplier<ArgumentType<?>> argumentType,
			Function<GameRules.Type<T>, T> ruleFactory,
			BiConsumer<MinecraftServer, T> changeCallback,
			GameRules.Acceptor<T> ruleAcceptor,
			FeatureSet requiredFeatures
		) {
			this.argumentType = argumentType;
			this.ruleFactory = ruleFactory;
			this.changeCallback = changeCallback;
			this.ruleAcceptor = ruleAcceptor;
			this.requiredFeatures = requiredFeatures;
		}

		public RequiredArgumentBuilder<ServerCommandSource, ?> argument(String name) {
			return CommandManager.argument(name, (ArgumentType<T>)this.argumentType.get());
		}

		public T createRule() {
			return (T)this.ruleFactory.apply(this);
		}

		public void accept(GameRules.Visitor consumer, GameRules.Key<T> key) {
			this.ruleAcceptor.call(consumer, key, this);
		}

		public FeatureSet getRequiredFeatures() {
			return this.requiredFeatures;
		}
	}

	/**
	 * A visitor used to visit all game rules.
	 */
	public interface Visitor {
		/**
		 * Visit a game rule.
		 * 
		 * <p>It is expected all game rules regardless of type will be visited using this method.
		 */
		default <T extends GameRules.Rule<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
		}

		/**
		 * Visit a boolean rule.
		 * 
		 * <p>Note {@link #visit(GameRules.Key, GameRules.Type)} will be called before this method.
		 */
		default void visitBoolean(GameRules.Key<GameRules.BooleanRule> key, GameRules.Type<GameRules.BooleanRule> type) {
		}

		/**
		 * Visit an integer rule.
		 * 
		 * <p>Note {@link #visit(GameRules.Key, GameRules.Type)} will be called before this method.
		 */
		default void visitInt(GameRules.Key<GameRules.IntRule> key, GameRules.Type<GameRules.IntRule> type) {
		}
	}
}
