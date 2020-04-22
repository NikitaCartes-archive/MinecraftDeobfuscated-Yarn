/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
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
import org.jetbrains.annotations.Nullable;

public class GameRules {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<RuleKey<?>, RuleType<?>> RULE_TYPES = Maps.newTreeMap(Comparator.comparing(key -> RuleKey.method_20772(key)));
    public static final RuleKey<BooleanRule> DO_FIRE_TICK = GameRules.register("doFireTick", RuleCategory.UPDATES, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> MOB_GRIEFING = GameRules.register("mobGriefing", RuleCategory.MOBS, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> KEEP_INVENTORY = GameRules.register("keepInventory", RuleCategory.PLAYER, BooleanRule.method_20755(false));
    public static final RuleKey<BooleanRule> DO_MOB_SPAWNING = GameRules.register("doMobSpawning", RuleCategory.SPAWNING, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> DO_MOB_LOOT = GameRules.register("doMobLoot", RuleCategory.DROPS, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> DO_TILE_DROPS = GameRules.register("doTileDrops", RuleCategory.DROPS, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> DO_ENTITY_DROPS = GameRules.register("doEntityDrops", RuleCategory.DROPS, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> COMMAND_BLOCK_OUTPUT = GameRules.register("commandBlockOutput", RuleCategory.CHAT, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> NATURAL_REGENERATION = GameRules.register("naturalRegeneration", RuleCategory.PLAYER, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> DO_DAYLIGHT_CYCLE = GameRules.register("doDaylightCycle", RuleCategory.UPDATES, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> LOG_ADMIN_COMMANDS = GameRules.register("logAdminCommands", RuleCategory.CHAT, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> SHOW_DEATH_MESSAGES = GameRules.register("showDeathMessages", RuleCategory.CHAT, BooleanRule.method_20755(true));
    public static final RuleKey<IntRule> RANDOM_TICK_SPEED = GameRules.register("randomTickSpeed", RuleCategory.UPDATES, IntRule.method_20764(3));
    public static final RuleKey<BooleanRule> SEND_COMMAND_FEEDBACK = GameRules.register("sendCommandFeedback", RuleCategory.CHAT, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> REDUCED_DEBUG_INFO = GameRules.register("reducedDebugInfo", RuleCategory.MISC, BooleanRule.method_20757(false, (server, rule) -> {
        byte b = rule.get() ? (byte)22 : (byte)23;
        for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
            serverPlayerEntity.networkHandler.sendPacket(new EntityStatusS2CPacket(serverPlayerEntity, b));
        }
    }));
    public static final RuleKey<BooleanRule> SPECTATORS_GENERATE_CHUNKS = GameRules.register("spectatorsGenerateChunks", RuleCategory.PLAYER, BooleanRule.method_20755(true));
    public static final RuleKey<IntRule> SPAWN_RADIUS = GameRules.register("spawnRadius", RuleCategory.PLAYER, IntRule.method_20764(10));
    public static final RuleKey<BooleanRule> DISABLE_ELYTRA_MOVEMENT_CHECK = GameRules.register("disableElytraMovementCheck", RuleCategory.PLAYER, BooleanRule.method_20755(false));
    public static final RuleKey<IntRule> MAX_ENTITY_CRAMMING = GameRules.register("maxEntityCramming", RuleCategory.MOBS, IntRule.method_20764(24));
    public static final RuleKey<BooleanRule> DO_WEATHER_CYCLE = GameRules.register("doWeatherCycle", RuleCategory.UPDATES, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> DO_LIMITED_CRAFTING = GameRules.register("doLimitedCrafting", RuleCategory.PLAYER, BooleanRule.method_20755(false));
    public static final RuleKey<IntRule> MAX_COMMAND_CHAIN_LENGTH = GameRules.register("maxCommandChainLength", RuleCategory.MISC, IntRule.method_20764(65536));
    public static final RuleKey<BooleanRule> ANNOUNCE_ADVANCEMENTS = GameRules.register("announceAdvancements", RuleCategory.CHAT, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> DISABLE_RAIDS = GameRules.register("disableRaids", RuleCategory.MOBS, BooleanRule.method_20755(false));
    public static final RuleKey<BooleanRule> DO_INSOMNIA = GameRules.register("doInsomnia", RuleCategory.SPAWNING, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> DO_IMMEDIATE_RESPAWN = GameRules.register("doImmediateRespawn", RuleCategory.PLAYER, BooleanRule.method_20757(false, (server, rule) -> {
        for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
            serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(11, rule.get() ? 1.0f : 0.0f));
        }
    }));
    public static final RuleKey<BooleanRule> DROWNING_DAMAGE = GameRules.register("drowningDamage", RuleCategory.PLAYER, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> FALL_DAMAGE = GameRules.register("fallDamage", RuleCategory.PLAYER, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> FIRE_DAMAGE = GameRules.register("fireDamage", RuleCategory.PLAYER, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> DO_PATROL_SPAWNING = GameRules.register("doPatrolSpawning", RuleCategory.SPAWNING, BooleanRule.method_20755(true));
    public static final RuleKey<BooleanRule> DO_TRADER_SPAWNING = GameRules.register("doTraderSpawning", RuleCategory.SPAWNING, BooleanRule.method_20755(true));
    private final Map<RuleKey<?>, Rule<?>> rules;

    private static <T extends Rule<T>> RuleKey<T> register(String name, RuleCategory category, RuleType<T> type) {
        RuleKey ruleKey = new RuleKey(name, category);
        RuleType<T> ruleType = RULE_TYPES.put(ruleKey, type);
        if (ruleType != null) {
            throw new IllegalStateException("Duplicate game rule registration for " + name);
        }
        return ruleKey;
    }

    public GameRules() {
        this.rules = RULE_TYPES.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, e -> ((RuleType)e.getValue()).createRule()));
    }

    private GameRules(Map<RuleKey<?>, Rule<?>> rules) {
        this.rules = rules;
    }

    public <T extends Rule<T>> T get(RuleKey<T> key) {
        return (T)this.rules.get(key);
    }

    public CompoundTag toNbt() {
        CompoundTag compoundTag = new CompoundTag();
        this.rules.forEach((key, rule) -> compoundTag.putString(((RuleKey)key).name, rule.serialize()));
        return compoundTag;
    }

    public void load(CompoundTag nbt) {
        this.rules.forEach((key, rule) -> {
            if (nbt.contains(((RuleKey)key).name)) {
                rule.deserialize(nbt.getString(((RuleKey)key).name));
            }
        });
    }

    public GameRules copy() {
        return new GameRules((Map)this.rules.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> ((Rule)entry.getValue()).copy())));
    }

    public static void forEachType(RuleTypeConsumer action) {
        RULE_TYPES.forEach((key, type) -> GameRules.accept(action, key, type));
    }

    private static <T extends Rule<T>> void accept(RuleTypeConsumer consumer, RuleKey<?> key, RuleType<?> type) {
        RuleKey<?> ruleKey = key;
        RuleType<?> ruleType = type;
        consumer.accept(ruleKey, ruleType);
        ruleType.method_27336(consumer, ruleKey);
    }

    public void setAllValues(GameRules gameRules, @Nullable MinecraftServer server) {
        gameRules.rules.keySet().forEach(ruleKey -> this.setValue((RuleKey)ruleKey, gameRules, server));
    }

    private <T extends Rule<T>> void setValue(RuleKey<T> key, GameRules gameRules, @Nullable MinecraftServer server) {
        T rule = gameRules.get(key);
        ((Rule)this.get(key)).setValue(rule, server);
    }

    public boolean getBoolean(RuleKey<BooleanRule> rule) {
        return this.get(rule).get();
    }

    public int getInt(RuleKey<IntRule> rule) {
        return this.get(rule).get();
    }

    public static class BooleanRule
    extends Rule<BooleanRule> {
        private boolean value;

        private static RuleType<BooleanRule> create(boolean initialValue, BiConsumer<MinecraftServer, BooleanRule> changeCallback) {
            return new RuleType<BooleanRule>(BoolArgumentType::bool, type -> new BooleanRule((RuleType<BooleanRule>)type, initialValue), changeCallback, RuleTypeConsumer::acceptBoolean);
        }

        private static RuleType<BooleanRule> create(boolean initialValue) {
            return BooleanRule.create(initialValue, (server, rule) -> {});
        }

        public BooleanRule(RuleType<BooleanRule> type, boolean initialValue) {
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

        @Override
        protected BooleanRule getThis() {
            return this;
        }

        @Override
        protected BooleanRule copy() {
            return new BooleanRule(this.type, this.value);
        }

        @Override
        public void setValue(BooleanRule booleanRule, @Nullable MinecraftServer minecraftServer) {
            this.value = booleanRule.value;
            this.changed(minecraftServer);
        }

        @Override
        protected /* synthetic */ Rule copy() {
            return this.copy();
        }

        @Override
        protected /* synthetic */ Rule getThis() {
            return this.getThis();
        }

        static /* synthetic */ RuleType method_20755(boolean bl) {
            return BooleanRule.create(bl);
        }

        static /* synthetic */ RuleType method_20757(boolean bl, BiConsumer biConsumer) {
            return BooleanRule.create(bl, biConsumer);
        }
    }

    public static class IntRule
    extends Rule<IntRule> {
        private int value;

        private static RuleType<IntRule> create(int initialValue, BiConsumer<MinecraftServer, IntRule> changeCallback) {
            return new RuleType<IntRule>(IntegerArgumentType::integer, type -> new IntRule((RuleType<IntRule>)type, initialValue), changeCallback, RuleTypeConsumer::acceptInt);
        }

        private static RuleType<IntRule> create(int initialValue) {
            return IntRule.create(initialValue, (server, rule) -> {});
        }

        public IntRule(RuleType<IntRule> rule, int initialValue) {
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
            this.value = IntRule.parseInt(value);
        }

        @Environment(value=EnvType.CLIENT)
        public boolean validate(String input) {
            try {
                this.value = Integer.parseInt(input);
                return true;
            } catch (NumberFormatException numberFormatException) {
                return false;
            }
        }

        private static int parseInt(String input) {
            if (!input.isEmpty()) {
                try {
                    return Integer.parseInt(input);
                } catch (NumberFormatException numberFormatException) {
                    LOGGER.warn("Failed to parse integer {}", (Object)input);
                }
            }
            return 0;
        }

        @Override
        public int getCommandResult() {
            return this.value;
        }

        @Override
        protected IntRule getThis() {
            return this;
        }

        @Override
        protected IntRule copy() {
            return new IntRule(this.type, this.value);
        }

        @Override
        public void setValue(IntRule intRule, @Nullable MinecraftServer minecraftServer) {
            this.value = intRule.value;
            this.changed(minecraftServer);
        }

        @Override
        protected /* synthetic */ Rule copy() {
            return this.copy();
        }

        @Override
        protected /* synthetic */ Rule getThis() {
            return this.getThis();
        }

        static /* synthetic */ RuleType method_20764(int i) {
            return IntRule.create(i);
        }
    }

    public static abstract class Rule<T extends Rule<T>> {
        protected final RuleType<T> type;

        public Rule(RuleType<T> type) {
            this.type = type;
        }

        protected abstract void setFromArgument(CommandContext<ServerCommandSource> var1, String var2);

        public void set(CommandContext<ServerCommandSource> context, String name) {
            this.setFromArgument(context, name);
            this.changed(context.getSource().getMinecraftServer());
        }

        protected void changed(@Nullable MinecraftServer server) {
            if (server != null) {
                ((RuleType)this.type).changeCallback.accept(server, this.getThis());
            }
        }

        protected abstract void deserialize(String var1);

        public abstract String serialize();

        public String toString() {
            return this.serialize();
        }

        public abstract int getCommandResult();

        protected abstract T getThis();

        protected abstract T copy();

        public abstract void setValue(T var1, @Nullable MinecraftServer var2);
    }

    public static class RuleType<T extends Rule<T>> {
        private final Supplier<ArgumentType<?>> argumentType;
        private final Function<RuleType<T>, T> ruleFactory;
        private final BiConsumer<MinecraftServer, T> changeCallback;
        private final RuleAcceptor<T> field_24104;

        private RuleType(Supplier<ArgumentType<?>> argumentType, Function<RuleType<T>, T> ruleFactory, BiConsumer<MinecraftServer, T> changeCallback, RuleAcceptor<T> ruleAcceptor) {
            this.argumentType = argumentType;
            this.ruleFactory = ruleFactory;
            this.changeCallback = changeCallback;
            this.field_24104 = ruleAcceptor;
        }

        public RequiredArgumentBuilder<ServerCommandSource, ?> argument(String name) {
            return CommandManager.argument(name, this.argumentType.get());
        }

        public T createRule() {
            return (T)((Rule)this.ruleFactory.apply(this));
        }

        public void method_27336(RuleTypeConsumer consumer, RuleKey<T> key) {
            this.field_24104.call(consumer, key, this);
        }
    }

    public static final class RuleKey<T extends Rule<T>> {
        private final String name;
        private final RuleCategory category;

        public RuleKey(String name, RuleCategory category) {
            this.name = name;
            this.category = category;
        }

        public String toString() {
            return this.name;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            return obj instanceof RuleKey && ((RuleKey)obj).name.equals(this.name);
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

        @Environment(value=EnvType.CLIENT)
        public RuleCategory getCategory() {
            return this.category;
        }
    }

    public static interface RuleTypeConsumer {
        default public <T extends Rule<T>> void accept(RuleKey<T> key, RuleType<T> type) {
        }

        default public void acceptBoolean(RuleKey<BooleanRule> key, RuleType<BooleanRule> type) {
        }

        default public void acceptInt(RuleKey<IntRule> key, RuleType<IntRule> type) {
        }
    }

    static interface RuleAcceptor<T extends Rule<T>> {
        public void call(RuleTypeConsumer var1, RuleKey<T> var2, RuleType<T> var3);
    }

    public static enum RuleCategory {
        PLAYER("gamerule.category.player"),
        MOBS("gamerule.category.mobs"),
        SPAWNING("gamerule.category.spawning"),
        DROPS("gamerule.category.drops"),
        UPDATES("gamerule.category.updates"),
        CHAT("gamerule.category.chat"),
        MISC("gamerule.category.misc");

        private final String category;

        private RuleCategory(String category) {
            this.category = category;
        }

        @Environment(value=EnvType.CLIENT)
        public String getCategory() {
            return this.category;
        }
    }
}

