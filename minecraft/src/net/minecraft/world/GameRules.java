package net.minecraft.world;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.DynamicLike;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
	private static final Map<GameRules.Key<?>, GameRules.Type<?>> RULE_TYPES = Maps.newTreeMap(Comparator.comparing(key -> key.name));
	public static final GameRules.Key<GameRules.BooleanRule> field_19387 = register(
		"doFireTick", GameRules.Category.field_24098, GameRules.BooleanRule.create(true)
	);
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
	public static final GameRules.Key<GameRules.BooleanRule> DO_MOB_GRIEFING = register(
		"mobGriefing", GameRules.Category.field_24095, GameRules.BooleanRule.create(true)
	);
	/**
	 * A {@linkplain Rule game rule} which regulates whether player inventories should be persist through respawning.
	 */
	public static final GameRules.Key<GameRules.BooleanRule> KEEP_INVENTORY = register(
		"keepInventory", GameRules.Category.field_24094, GameRules.BooleanRule.create(false)
	);
	/**
	 * A {@linkplain Rule game rule} which regulates whether mobs can spawn naturally.
	 */
	public static final GameRules.Key<GameRules.BooleanRule> DO_MOB_SPAWNING = register(
		"doMobSpawning", GameRules.Category.field_24096, GameRules.BooleanRule.create(true)
	);
	/**
	 * A {@linkplain Rule game rule} which regulates whether mobs should drop loot on death.
	 */
	public static final GameRules.Key<GameRules.BooleanRule> DO_MOB_LOOT = register(
		"doMobLoot", GameRules.Category.field_24097, GameRules.BooleanRule.create(true)
	);
	/**
	 * A {@linkplain Rule game rule} which regulates whether blocks should drop their items when broken.
	 */
	public static final GameRules.Key<GameRules.BooleanRule> DO_TILE_DROPS = register(
		"doTileDrops", GameRules.Category.field_24097, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> field_19393 = register(
		"doEntityDrops", GameRules.Category.field_24097, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> field_19394 = register(
		"commandBlockOutput", GameRules.Category.field_24099, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> field_19395 = register(
		"naturalRegeneration", GameRules.Category.field_24094, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> field_19396 = register(
		"doDaylightCycle", GameRules.Category.field_24098, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> field_19397 = register(
		"logAdminCommands", GameRules.Category.field_24099, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> field_19398 = register(
		"showDeathMessages", GameRules.Category.field_24099, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.IntRule> field_19399 = register("randomTickSpeed", GameRules.Category.field_24098, GameRules.IntRule.create(3));
	public static final GameRules.Key<GameRules.BooleanRule> field_19400 = register(
		"sendCommandFeedback", GameRules.Category.field_24099, GameRules.BooleanRule.create(true)
	);
	/**
	 * A {@linkplain Rule game rule} which regulates whether clients' {@linkplain net.minecraft.client.gui.hud.DebugHud debug HUD}s show reduced information.
	 * 
	 * <p>When the value of this rule is changed, all connected clients will be notified to update their display.
	 * In vanilla, this includes the visibility of coordinates on the clients' debug HUDs.
	 */
	public static final GameRules.Key<GameRules.BooleanRule> REDUCED_DEBUG_INFO = register(
		"reducedDebugInfo", GameRules.Category.field_24100, GameRules.BooleanRule.create(false, (server, rule) -> {
			byte b = (byte)(rule.get() ? 22 : 23);

			for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
				serverPlayerEntity.networkHandler.sendPacket(new EntityStatusS2CPacket(serverPlayerEntity, b));
			}
		})
	);
	public static final GameRules.Key<GameRules.BooleanRule> field_19402 = register(
		"spectatorsGenerateChunks", GameRules.Category.field_24094, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.IntRule> field_19403 = register("spawnRadius", GameRules.Category.field_24094, GameRules.IntRule.create(10));
	public static final GameRules.Key<GameRules.BooleanRule> field_19404 = register(
		"disableElytraMovementCheck", GameRules.Category.field_24094, GameRules.BooleanRule.create(false)
	);
	/**
	 * A {@linkplain Rule game rule} which regulates the number of entities that can be crammed into a block space before they incur cramming damage.
	 */
	public static final GameRules.Key<GameRules.IntRule> MAX_ENTITY_CRAMMING = register(
		"maxEntityCramming", GameRules.Category.field_24095, GameRules.IntRule.create(24)
	);
	public static final GameRules.Key<GameRules.BooleanRule> field_19406 = register(
		"doWeatherCycle", GameRules.Category.field_24098, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> field_19407 = register(
		"doLimitedCrafting", GameRules.Category.field_24094, GameRules.BooleanRule.create(false)
	);
	public static final GameRules.Key<GameRules.IntRule> field_19408 = register(
		"maxCommandChainLength", GameRules.Category.field_24100, GameRules.IntRule.create(65536)
	);
	/**
	 * A {@linkplain Rule game rule} which regulates whether a player's advancements should be announced in chat.
	 */
	public static final GameRules.Key<GameRules.BooleanRule> ANNOUNCE_ADVANCEMENTS = register(
		"announceAdvancements", GameRules.Category.field_24099, GameRules.BooleanRule.create(true)
	);
	/**
	 * A {@linkplain Rule game rule} which regulates whether raids should occur.
	 * 
	 * <p>If this rule is set to {@code true} while raids are occurring, the raids will be stopped.
	 */
	public static final GameRules.Key<GameRules.BooleanRule> DISABLE_RAIDS = register(
		"disableRaids", GameRules.Category.field_24095, GameRules.BooleanRule.create(false)
	);
	public static final GameRules.Key<GameRules.BooleanRule> field_20637 = register(
		"doInsomnia", GameRules.Category.field_24096, GameRules.BooleanRule.create(true)
	);
	/**
	 * A {@linkplain Rule game rule} which regulates whether a player should immediately respawn upon death.
	 */
	public static final GameRules.Key<GameRules.BooleanRule> DO_IMMEDIATE_RESPAWN = register(
		"doImmediateRespawn", GameRules.Category.field_24094, GameRules.BooleanRule.create(false, (server, rule) -> {
			for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
				serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.IMMEDIATE_RESPAWN, rule.get() ? 1.0F : 0.0F));
			}
		})
	);
	public static final GameRules.Key<GameRules.BooleanRule> field_20634 = register(
		"drowningDamage", GameRules.Category.field_24094, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> field_20635 = register(
		"fallDamage", GameRules.Category.field_24094, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> field_20636 = register(
		"fireDamage", GameRules.Category.field_24094, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> field_21831 = register(
		"doPatrolSpawning", GameRules.Category.field_24096, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> field_21832 = register(
		"doTraderSpawning", GameRules.Category.field_24096, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> field_25401 = register(
		"forgiveDeadPlayers", GameRules.Category.field_24095, GameRules.BooleanRule.create(true)
	);
	public static final GameRules.Key<GameRules.BooleanRule> field_25402 = register(
		"universalAnger", GameRules.Category.field_24095, GameRules.BooleanRule.create(false)
	);
	private final Map<GameRules.Key<?>, GameRules.Rule<?>> rules;

	private static <T extends GameRules.Rule<T>> GameRules.Key<T> register(String name, GameRules.Category category, GameRules.Type<T> type) {
		GameRules.Key<T> key = new GameRules.Key<>(name, category);
		GameRules.Type<?> type2 = (GameRules.Type<?>)RULE_TYPES.put(key, type);
		if (type2 != null) {
			throw new IllegalStateException("Duplicate game rule registration for " + name);
		} else {
			return key;
		}
	}

	public GameRules(DynamicLike<?> dynamicLike) {
		this();
		this.load(dynamicLike);
	}

	public GameRules() {
		this.rules = (Map<GameRules.Key<?>, GameRules.Rule<?>>)RULE_TYPES.entrySet()
			.stream()
			.collect(ImmutableMap.toImmutableMap(Entry::getKey, e -> ((GameRules.Type)e.getValue()).createRule()));
	}

	private GameRules(Map<GameRules.Key<?>, GameRules.Rule<?>> rules) {
		this.rules = rules;
	}

	public <T extends GameRules.Rule<T>> T get(GameRules.Key<T> key) {
		return (T)this.rules.get(key);
	}

	public CompoundTag toNbt() {
		CompoundTag compoundTag = new CompoundTag();
		this.rules.forEach((key, rule) -> compoundTag.putString(key.name, rule.serialize()));
		return compoundTag;
	}

	private void load(DynamicLike<?> dynamicLike) {
		this.rules.forEach((key, rule) -> dynamicLike.get(key.name).asString().result().ifPresent(rule::deserialize));
	}

	public GameRules copy() {
		return new GameRules(
			(Map<GameRules.Key<?>, GameRules.Rule<?>>)this.rules
				.entrySet()
				.stream()
				.collect(ImmutableMap.toImmutableMap(Entry::getKey, entry -> ((GameRules.Rule)entry.getValue()).copy()))
		);
	}

	/**
	 * Make the visitor visit all registered game rules.
	 * 
	 * <p>The visitation involves calling both {@link Visitor#visit(GameRules.Key, GameRules.Type)} and {@code visitX} for every game rule, where X is the current rule's concrete type such as a boolean.
	 */
	public static void accept(GameRules.Visitor visitor) {
		RULE_TYPES.forEach((key, type) -> accept(visitor, key, type));
	}

	private static <T extends GameRules.Rule<T>> void accept(GameRules.Visitor consumer, GameRules.Key<?> key, GameRules.Type<?> type) {
		consumer.visit(key, type);
		type.accept(consumer, key);
	}

	@Environment(EnvType.CLIENT)
	public void setAllValues(GameRules rules, @Nullable MinecraftServer server) {
		rules.rules.keySet().forEach(key -> this.setValue(key, rules, server));
	}

	@Environment(EnvType.CLIENT)
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

		private static GameRules.Type<GameRules.BooleanRule> create(boolean initialValue, BiConsumer<MinecraftServer, GameRules.BooleanRule> changeCallback) {
			return new GameRules.Type<>(BoolArgumentType::bool, type -> new GameRules.BooleanRule(type, initialValue), changeCallback, GameRules.Visitor::visitBoolean);
		}

		private static GameRules.Type<GameRules.BooleanRule> create(boolean initialValue) {
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

		protected GameRules.BooleanRule method_20761() {
			return this;
		}

		protected GameRules.BooleanRule method_27327() {
			return new GameRules.BooleanRule(this.type, this.value);
		}

		@Environment(EnvType.CLIENT)
		public void method_27326(GameRules.BooleanRule booleanRule, @Nullable MinecraftServer minecraftServer) {
			this.value = booleanRule.value;
			this.changed(minecraftServer);
		}
	}

	public static enum Category {
		field_24094("gamerule.category.player"),
		field_24095("gamerule.category.mobs"),
		field_24096("gamerule.category.spawning"),
		field_24097("gamerule.category.drops"),
		field_24098("gamerule.category.updates"),
		field_24099("gamerule.category.chat"),
		field_24100("gamerule.category.misc");

		private final String category;

		private Category(String category) {
			this.category = category;
		}

		@Environment(EnvType.CLIENT)
		public String getCategory() {
			return this.category;
		}
	}

	public static class IntRule extends GameRules.Rule<GameRules.IntRule> {
		private int value;

		private static GameRules.Type<GameRules.IntRule> create(int initialValue, BiConsumer<MinecraftServer, GameRules.IntRule> changeCallback) {
			return new GameRules.Type<>(IntegerArgumentType::integer, type -> new GameRules.IntRule(type, initialValue), changeCallback, GameRules.Visitor::visitInt);
		}

		private static GameRules.Type<GameRules.IntRule> create(int initialValue) {
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

		@Override
		public String serialize() {
			return Integer.toString(this.value);
		}

		@Override
		protected void deserialize(String value) {
			this.value = parseInt(value);
		}

		/**
		 * Validates that an input is valid for this rule.
		 */
		@Environment(EnvType.CLIENT)
		public boolean validate(String input) {
			try {
				this.value = Integer.parseInt(input);
				return true;
			} catch (NumberFormatException var3) {
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

		protected GameRules.IntRule method_20770() {
			return this;
		}

		protected GameRules.IntRule method_27333() {
			return new GameRules.IntRule(this.type, this.value);
		}

		@Environment(EnvType.CLIENT)
		public void method_27331(GameRules.IntRule intRule, @Nullable MinecraftServer minecraftServer) {
			this.value = intRule.value;
			this.changed(minecraftServer);
		}
	}

	public static final class Key<T extends GameRules.Rule<T>> {
		private final String name;
		private final GameRules.Category category;

		public Key(String name, GameRules.Category category) {
			this.name = name;
			this.category = category;
		}

		public String toString() {
			return this.name;
		}

		public boolean equals(Object obj) {
			return this == obj ? true : obj instanceof GameRules.Key && ((GameRules.Key)obj).name.equals(this.name);
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

		@Environment(EnvType.CLIENT)
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
			this.changed(context.getSource().getMinecraftServer());
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

		@Environment(EnvType.CLIENT)
		public abstract void setValue(T rule, @Nullable MinecraftServer server);
	}

	public static class Type<T extends GameRules.Rule<T>> {
		private final Supplier<ArgumentType<?>> argumentType;
		private final Function<GameRules.Type<T>, T> ruleFactory;
		private final BiConsumer<MinecraftServer, T> changeCallback;
		private final GameRules.Acceptor<T> ruleAcceptor;

		private Type(
			Supplier<ArgumentType<?>> argumentType,
			Function<GameRules.Type<T>, T> ruleFactory,
			BiConsumer<MinecraftServer, T> changeCallback,
			GameRules.Acceptor<T> ruleAcceptor
		) {
			this.argumentType = argumentType;
			this.ruleFactory = ruleFactory;
			this.changeCallback = changeCallback;
			this.ruleAcceptor = ruleAcceptor;
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
