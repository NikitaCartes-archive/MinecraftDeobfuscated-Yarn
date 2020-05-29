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
import com.mojang.serialization.DynamicLike;
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
    private static final Map<Key<?>, Type<?>> RULE_TYPES = Maps.newTreeMap(Comparator.comparing(key -> Key.method_20772(key)));
    public static final Key<BooleanRule> DO_FIRE_TICK = GameRules.register("doFireTick", Category.UPDATES, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> MOB_GRIEFING = GameRules.register("mobGriefing", Category.MOBS, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> KEEP_INVENTORY = GameRules.register("keepInventory", Category.PLAYER, BooleanRule.method_20755(false));
    public static final Key<BooleanRule> DO_MOB_SPAWNING = GameRules.register("doMobSpawning", Category.SPAWNING, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> DO_MOB_LOOT = GameRules.register("doMobLoot", Category.DROPS, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> DO_TILE_DROPS = GameRules.register("doTileDrops", Category.DROPS, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> DO_ENTITY_DROPS = GameRules.register("doEntityDrops", Category.DROPS, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> COMMAND_BLOCK_OUTPUT = GameRules.register("commandBlockOutput", Category.CHAT, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> NATURAL_REGENERATION = GameRules.register("naturalRegeneration", Category.PLAYER, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> DO_DAYLIGHT_CYCLE = GameRules.register("doDaylightCycle", Category.UPDATES, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> LOG_ADMIN_COMMANDS = GameRules.register("logAdminCommands", Category.CHAT, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> SHOW_DEATH_MESSAGES = GameRules.register("showDeathMessages", Category.CHAT, BooleanRule.method_20755(true));
    public static final Key<IntRule> RANDOM_TICK_SPEED = GameRules.register("randomTickSpeed", Category.UPDATES, IntRule.method_20764(3));
    public static final Key<BooleanRule> SEND_COMMAND_FEEDBACK = GameRules.register("sendCommandFeedback", Category.CHAT, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> REDUCED_DEBUG_INFO = GameRules.register("reducedDebugInfo", Category.MISC, BooleanRule.method_20757(false, (server, rule) -> {
        byte b = rule.get() ? (byte)22 : (byte)23;
        for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
            serverPlayerEntity.networkHandler.sendPacket(new EntityStatusS2CPacket(serverPlayerEntity, b));
        }
    }));
    public static final Key<BooleanRule> SPECTATORS_GENERATE_CHUNKS = GameRules.register("spectatorsGenerateChunks", Category.PLAYER, BooleanRule.method_20755(true));
    public static final Key<IntRule> SPAWN_RADIUS = GameRules.register("spawnRadius", Category.PLAYER, IntRule.method_20764(10));
    public static final Key<BooleanRule> DISABLE_ELYTRA_MOVEMENT_CHECK = GameRules.register("disableElytraMovementCheck", Category.PLAYER, BooleanRule.method_20755(false));
    public static final Key<IntRule> MAX_ENTITY_CRAMMING = GameRules.register("maxEntityCramming", Category.MOBS, IntRule.method_20764(24));
    public static final Key<BooleanRule> DO_WEATHER_CYCLE = GameRules.register("doWeatherCycle", Category.UPDATES, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> DO_LIMITED_CRAFTING = GameRules.register("doLimitedCrafting", Category.PLAYER, BooleanRule.method_20755(false));
    public static final Key<IntRule> MAX_COMMAND_CHAIN_LENGTH = GameRules.register("maxCommandChainLength", Category.MISC, IntRule.method_20764(65536));
    public static final Key<BooleanRule> ANNOUNCE_ADVANCEMENTS = GameRules.register("announceAdvancements", Category.CHAT, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> DISABLE_RAIDS = GameRules.register("disableRaids", Category.MOBS, BooleanRule.method_20755(false));
    public static final Key<BooleanRule> DO_INSOMNIA = GameRules.register("doInsomnia", Category.SPAWNING, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> DO_IMMEDIATE_RESPAWN = GameRules.register("doImmediateRespawn", Category.PLAYER, BooleanRule.method_20757(false, (server, rule) -> {
        for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
            serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(11, rule.get() ? 1.0f : 0.0f));
        }
    }));
    public static final Key<BooleanRule> DROWNING_DAMAGE = GameRules.register("drowningDamage", Category.PLAYER, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> FALL_DAMAGE = GameRules.register("fallDamage", Category.PLAYER, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> FIRE_DAMAGE = GameRules.register("fireDamage", Category.PLAYER, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> DO_PATROL_SPAWNING = GameRules.register("doPatrolSpawning", Category.SPAWNING, BooleanRule.method_20755(true));
    public static final Key<BooleanRule> DO_TRADER_SPAWNING = GameRules.register("doTraderSpawning", Category.SPAWNING, BooleanRule.method_20755(true));
    private final Map<Key<?>, Rule<?>> rules;

    private static <T extends Rule<T>> Key<T> register(String name, Category category, Type<T> type) {
        Key key = new Key(name, category);
        Type<T> type2 = RULE_TYPES.put(key, type);
        if (type2 != null) {
            throw new IllegalStateException("Duplicate game rule registration for " + name);
        }
        return key;
    }

    public GameRules(DynamicLike<?> dynamicLike) {
        this();
        this.load(dynamicLike);
    }

    public GameRules() {
        this.rules = RULE_TYPES.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, e -> ((Type)e.getValue()).createRule()));
    }

    private GameRules(Map<Key<?>, Rule<?>> rules) {
        this.rules = rules;
    }

    public <T extends Rule<T>> T get(Key<T> key) {
        return (T)this.rules.get(key);
    }

    public CompoundTag toNbt() {
        CompoundTag compoundTag = new CompoundTag();
        this.rules.forEach((key, rule) -> compoundTag.putString(((Key)key).name, rule.serialize()));
        return compoundTag;
    }

    private void load(DynamicLike<?> dynamicLike) {
        this.rules.forEach((key, rule) -> dynamicLike.get(((Key)key).name).asString().result().ifPresent(rule::deserialize));
    }

    public GameRules copy() {
        return new GameRules((Map)this.rules.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> ((Rule)entry.getValue()).copy())));
    }

    public static void forEachType(TypeConsumer action) {
        RULE_TYPES.forEach((key, type) -> GameRules.accept(action, key, type));
    }

    private static <T extends Rule<T>> void accept(TypeConsumer consumer, Key<?> key, Type<?> type) {
        Key<?> key2 = key;
        Type<?> type2 = type;
        consumer.accept(key2, type2);
        type2.method_27336(consumer, key2);
    }

    @Environment(value=EnvType.CLIENT)
    public void setAllValues(GameRules gameRules, @Nullable MinecraftServer server) {
        gameRules.rules.keySet().forEach(key -> this.setValue((Key)key, gameRules, server));
    }

    @Environment(value=EnvType.CLIENT)
    private <T extends Rule<T>> void setValue(Key<T> key, GameRules gameRules, @Nullable MinecraftServer server) {
        T rule = gameRules.get(key);
        ((Rule)this.get(key)).setValue(rule, server);
    }

    public boolean getBoolean(Key<BooleanRule> rule) {
        return this.get(rule).get();
    }

    public int getInt(Key<IntRule> rule) {
        return this.get(rule).get();
    }

    public static class BooleanRule
    extends Rule<BooleanRule> {
        private boolean value;

        private static Type<BooleanRule> create(boolean initialValue, BiConsumer<MinecraftServer, BooleanRule> changeCallback) {
            return new Type<BooleanRule>(BoolArgumentType::bool, type -> new BooleanRule((Type<BooleanRule>)type, initialValue), changeCallback, TypeConsumer::acceptBoolean);
        }

        private static Type<BooleanRule> create(boolean initialValue) {
            return BooleanRule.create(initialValue, (server, rule) -> {});
        }

        public BooleanRule(Type<BooleanRule> type, boolean initialValue) {
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
        @Environment(value=EnvType.CLIENT)
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

        static /* synthetic */ Type method_20755(boolean bl) {
            return BooleanRule.create(bl);
        }

        static /* synthetic */ Type method_20757(boolean bl, BiConsumer biConsumer) {
            return BooleanRule.create(bl, biConsumer);
        }
    }

    public static class IntRule
    extends Rule<IntRule> {
        private int value;

        private static Type<IntRule> create(int initialValue, BiConsumer<MinecraftServer, IntRule> changeCallback) {
            return new Type<IntRule>(IntegerArgumentType::integer, type -> new IntRule((Type<IntRule>)type, initialValue), changeCallback, TypeConsumer::acceptInt);
        }

        private static Type<IntRule> create(int initialValue) {
            return IntRule.create(initialValue, (server, rule) -> {});
        }

        public IntRule(Type<IntRule> rule, int initialValue) {
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
        @Environment(value=EnvType.CLIENT)
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

        static /* synthetic */ Type method_20764(int i) {
            return IntRule.create(i);
        }
    }

    public static abstract class Rule<T extends Rule<T>> {
        protected final Type<T> type;

        public Rule(Type<T> type) {
            this.type = type;
        }

        protected abstract void setFromArgument(CommandContext<ServerCommandSource> var1, String var2);

        public void set(CommandContext<ServerCommandSource> context, String name) {
            this.setFromArgument(context, name);
            this.changed(context.getSource().getMinecraftServer());
        }

        protected void changed(@Nullable MinecraftServer server) {
            if (server != null) {
                ((Type)this.type).changeCallback.accept(server, this.getThis());
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

        @Environment(value=EnvType.CLIENT)
        public abstract void setValue(T var1, @Nullable MinecraftServer var2);
    }

    public static class Type<T extends Rule<T>> {
        private final Supplier<ArgumentType<?>> argumentType;
        private final Function<Type<T>, T> ruleFactory;
        private final BiConsumer<MinecraftServer, T> changeCallback;
        private final Acceptor<T> field_24104;

        private Type(Supplier<ArgumentType<?>> argumentType, Function<Type<T>, T> ruleFactory, BiConsumer<MinecraftServer, T> changeCallback, Acceptor<T> acceptor) {
            this.argumentType = argumentType;
            this.ruleFactory = ruleFactory;
            this.changeCallback = changeCallback;
            this.field_24104 = acceptor;
        }

        public RequiredArgumentBuilder<ServerCommandSource, ?> argument(String name) {
            return CommandManager.argument(name, this.argumentType.get());
        }

        public T createRule() {
            return (T)((Rule)this.ruleFactory.apply(this));
        }

        public void method_27336(TypeConsumer consumer, Key<T> key) {
            this.field_24104.call(consumer, key, this);
        }
    }

    public static final class Key<T extends Rule<T>> {
        private final String name;
        private final Category category;

        public Key(String name, Category category) {
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
            return obj instanceof Key && ((Key)obj).name.equals(this.name);
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
        public Category getCategory() {
            return this.category;
        }
    }

    public static interface TypeConsumer {
        default public <T extends Rule<T>> void accept(Key<T> key, Type<T> type) {
        }

        default public void acceptBoolean(Key<BooleanRule> key, Type<BooleanRule> type) {
        }

        default public void acceptInt(Key<IntRule> key, Type<IntRule> type) {
        }
    }

    static interface Acceptor<T extends Rule<T>> {
        public void call(TypeConsumer var1, Key<T> var2, Type<T> var3);
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

        private Category(String category) {
            this.category = category;
        }

        @Environment(value=EnvType.CLIENT)
        public String getCategory() {
            return this.category;
        }
    }
}

