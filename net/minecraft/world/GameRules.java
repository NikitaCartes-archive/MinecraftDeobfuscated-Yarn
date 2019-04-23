/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.client.network.packet.EntityStatusS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.SystemUtil;
import org.jetbrains.annotations.Nullable;

public class GameRules {
    private static final TreeMap<String, Key> KEYS = SystemUtil.consume(new TreeMap(), treeMap -> {
        treeMap.put("doFireTick", new Key("true", Type.BOOLEAN_VALUE));
        treeMap.put("mobGriefing", new Key("true", Type.BOOLEAN_VALUE));
        treeMap.put("keepInventory", new Key("false", Type.BOOLEAN_VALUE));
        treeMap.put("doMobSpawning", new Key("true", Type.BOOLEAN_VALUE));
        treeMap.put("doMobLoot", new Key("true", Type.BOOLEAN_VALUE));
        treeMap.put("doTileDrops", new Key("true", Type.BOOLEAN_VALUE));
        treeMap.put("doEntityDrops", new Key("true", Type.BOOLEAN_VALUE));
        treeMap.put("commandBlockOutput", new Key("true", Type.BOOLEAN_VALUE));
        treeMap.put("naturalRegeneration", new Key("true", Type.BOOLEAN_VALUE));
        treeMap.put("doDaylightCycle", new Key("true", Type.BOOLEAN_VALUE));
        treeMap.put("logAdminCommands", new Key("true", Type.BOOLEAN_VALUE));
        treeMap.put("showDeathMessages", new Key("true", Type.BOOLEAN_VALUE));
        treeMap.put("randomTickSpeed", new Key("3", Type.NUMERICAL_VALUE));
        treeMap.put("sendCommandFeedback", new Key("true", Type.BOOLEAN_VALUE));
        treeMap.put("reducedDebugInfo", new Key("false", Type.BOOLEAN_VALUE, (minecraftServer, value) -> {
            byte b = value.getBoolean() ? (byte)22 : (byte)23;
            for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayerList()) {
                serverPlayerEntity.networkHandler.sendPacket(new EntityStatusS2CPacket(serverPlayerEntity, b));
            }
        }));
        treeMap.put("spectatorsGenerateChunks", new Key("true", Type.BOOLEAN_VALUE));
        treeMap.put("spawnRadius", new Key("10", Type.NUMERICAL_VALUE));
        treeMap.put("disableElytraMovementCheck", new Key("false", Type.BOOLEAN_VALUE));
        treeMap.put("maxEntityCramming", new Key("24", Type.NUMERICAL_VALUE));
        treeMap.put("doWeatherCycle", new Key("true", Type.BOOLEAN_VALUE));
        treeMap.put("doLimitedCrafting", new Key("false", Type.BOOLEAN_VALUE));
        treeMap.put("maxCommandChainLength", new Key("65536", Type.NUMERICAL_VALUE));
        treeMap.put("announceAdvancements", new Key("true", Type.BOOLEAN_VALUE));
    });
    private final TreeMap<String, Value> rules = new TreeMap();

    public GameRules() {
        for (Map.Entry<String, Key> entry : KEYS.entrySet()) {
            this.rules.put(entry.getKey(), entry.getValue().createValue());
        }
    }

    public void put(String string, String string2, @Nullable MinecraftServer minecraftServer) {
        Value value = this.rules.get(string);
        if (value != null) {
            value.set(string2, minecraftServer);
        }
    }

    public boolean getBoolean(String string) {
        Value value = this.rules.get(string);
        if (value != null) {
            return value.getBoolean();
        }
        return false;
    }

    public int getInteger(String string) {
        Value value = this.rules.get(string);
        if (value != null) {
            return value.getInteger();
        }
        return 0;
    }

    public CompoundTag serialize() {
        CompoundTag compoundTag = new CompoundTag();
        for (String string : this.rules.keySet()) {
            Value value = this.rules.get(string);
            compoundTag.putString(string, value.getString());
        }
        return compoundTag;
    }

    public void deserialize(CompoundTag compoundTag) {
        Set<String> set = compoundTag.getKeys();
        for (String string : set) {
            this.put(string, compoundTag.getString(string), null);
        }
    }

    public Value get(String string) {
        return this.rules.get(string);
    }

    public static TreeMap<String, Key> getKeys() {
        return KEYS;
    }

    public static enum Type {
        ANY_VALUE(StringArgumentType::greedyString, (commandContext, string) -> commandContext.getArgument((String)string, String.class)),
        BOOLEAN_VALUE(BoolArgumentType::bool, (commandContext, string) -> commandContext.getArgument((String)string, Boolean.class).toString()),
        NUMERICAL_VALUE(IntegerArgumentType::integer, (commandContext, string) -> commandContext.getArgument((String)string, Integer.class).toString());

        private final Supplier<ArgumentType<?>> argumentType;
        private final BiFunction<CommandContext<ServerCommandSource>, String, String> argumentProvider;

        private Type(Supplier<ArgumentType<?>> supplier, BiFunction<CommandContext<ServerCommandSource>, String, String> biFunction) {
            this.argumentType = supplier;
            this.argumentProvider = biFunction;
        }

        public RequiredArgumentBuilder<ServerCommandSource, ?> argument(String string) {
            return CommandManager.argument(string, this.argumentType.get());
        }

        public void set(CommandContext<ServerCommandSource> commandContext, String string, Value value) {
            value.set(this.argumentProvider.apply(commandContext, string), commandContext.getSource().getMinecraftServer());
        }
    }

    public static class Value {
        private String asString;
        private boolean asBoolean;
        private int asInteger;
        private double asDouble;
        private final Type type;
        private final BiConsumer<MinecraftServer, Value> applyConsumer;

        public Value(String string, Type type, BiConsumer<MinecraftServer, Value> biConsumer) {
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
            } catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            try {
                this.asDouble = Double.parseDouble(string);
            } catch (NumberFormatException numberFormatException) {
                // empty catch block
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

        public Type getType() {
            return this.type;
        }
    }

    public static class Key {
        private final Type type;
        private final String defaultValue;
        private final BiConsumer<MinecraftServer, Value> field_9204;

        public Key(String string, Type type) {
            this(string, type, (minecraftServer, value) -> {});
        }

        public Key(String string, Type type, BiConsumer<MinecraftServer, Value> biConsumer) {
            this.type = type;
            this.defaultValue = string;
            this.field_9204 = biConsumer;
        }

        public Value createValue() {
            return new Value(this.defaultValue, this.type, this.field_9204);
        }

        public Type getType() {
            return this.type;
        }
    }
}

