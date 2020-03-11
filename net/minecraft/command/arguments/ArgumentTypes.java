/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.Collection;
import java.util.Map;
import net.minecraft.SharedConstants;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.BlockPredicateArgumentType;
import net.minecraft.command.arguments.BlockStateArgumentType;
import net.minecraft.command.arguments.BrigadierArgumentTypes;
import net.minecraft.command.arguments.ColorArgumentType;
import net.minecraft.command.arguments.ColumnPosArgumentType;
import net.minecraft.command.arguments.DimensionArgumentType;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.EntitySummonArgumentType;
import net.minecraft.command.arguments.FunctionArgumentType;
import net.minecraft.command.arguments.GameProfileArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.command.arguments.ItemEnchantmentArgumentType;
import net.minecraft.command.arguments.ItemPredicateArgumentType;
import net.minecraft.command.arguments.ItemSlotArgumentType;
import net.minecraft.command.arguments.ItemStackArgumentType;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.command.arguments.MobEffectArgumentType;
import net.minecraft.command.arguments.NbtCompoundTagArgumentType;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.command.arguments.NbtTagArgumentType;
import net.minecraft.command.arguments.NumberRangeArgumentType;
import net.minecraft.command.arguments.ObjectiveArgumentType;
import net.minecraft.command.arguments.ObjectiveCriteriaArgumentType;
import net.minecraft.command.arguments.OperationArgumentType;
import net.minecraft.command.arguments.ParticleArgumentType;
import net.minecraft.command.arguments.RotationArgumentType;
import net.minecraft.command.arguments.ScoreHolderArgumentType;
import net.minecraft.command.arguments.ScoreboardSlotArgumentType;
import net.minecraft.command.arguments.SwizzleArgumentType;
import net.minecraft.command.arguments.TeamArgumentType;
import net.minecraft.command.arguments.TestClassArgumentType;
import net.minecraft.command.arguments.TestFunctionArgumentType;
import net.minecraft.command.arguments.TextArgumentType;
import net.minecraft.command.arguments.TimeArgumentType;
import net.minecraft.command.arguments.Vec2ArgumentType;
import net.minecraft.command.arguments.Vec3ArgumentType;
import net.minecraft.command.arguments.serialize.ArgumentSerializer;
import net.minecraft.command.arguments.serialize.ConstantArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ArgumentTypes {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<Class<?>, Entry<?>> classMap = Maps.newHashMap();
    private static final Map<Identifier, Entry<?>> idMap = Maps.newHashMap();

    public static <T extends ArgumentType<?>> void register(String string, Class<T> class_, ArgumentSerializer<T> argumentSerializer) {
        Identifier identifier = new Identifier(string);
        if (classMap.containsKey(class_)) {
            throw new IllegalArgumentException("Class " + class_.getName() + " already has a serializer!");
        }
        if (idMap.containsKey(identifier)) {
            throw new IllegalArgumentException("'" + identifier + "' is already a registered serializer!");
        }
        Entry entry = new Entry(class_, argumentSerializer, identifier);
        classMap.put(class_, entry);
        idMap.put(identifier, entry);
    }

    public static void register() {
        BrigadierArgumentTypes.register();
        ArgumentTypes.register("entity", EntityArgumentType.class, new EntityArgumentType.Serializer());
        ArgumentTypes.register("game_profile", GameProfileArgumentType.class, new ConstantArgumentSerializer<GameProfileArgumentType>(GameProfileArgumentType::gameProfile));
        ArgumentTypes.register("block_pos", BlockPosArgumentType.class, new ConstantArgumentSerializer<BlockPosArgumentType>(BlockPosArgumentType::blockPos));
        ArgumentTypes.register("column_pos", ColumnPosArgumentType.class, new ConstantArgumentSerializer<ColumnPosArgumentType>(ColumnPosArgumentType::columnPos));
        ArgumentTypes.register("vec3", Vec3ArgumentType.class, new ConstantArgumentSerializer<Vec3ArgumentType>(Vec3ArgumentType::vec3));
        ArgumentTypes.register("vec2", Vec2ArgumentType.class, new ConstantArgumentSerializer<Vec2ArgumentType>(Vec2ArgumentType::vec2));
        ArgumentTypes.register("block_state", BlockStateArgumentType.class, new ConstantArgumentSerializer<BlockStateArgumentType>(BlockStateArgumentType::blockState));
        ArgumentTypes.register("block_predicate", BlockPredicateArgumentType.class, new ConstantArgumentSerializer<BlockPredicateArgumentType>(BlockPredicateArgumentType::blockPredicate));
        ArgumentTypes.register("item_stack", ItemStackArgumentType.class, new ConstantArgumentSerializer<ItemStackArgumentType>(ItemStackArgumentType::itemStack));
        ArgumentTypes.register("item_predicate", ItemPredicateArgumentType.class, new ConstantArgumentSerializer<ItemPredicateArgumentType>(ItemPredicateArgumentType::itemPredicate));
        ArgumentTypes.register("color", ColorArgumentType.class, new ConstantArgumentSerializer<ColorArgumentType>(ColorArgumentType::color));
        ArgumentTypes.register("component", TextArgumentType.class, new ConstantArgumentSerializer<TextArgumentType>(TextArgumentType::text));
        ArgumentTypes.register("message", MessageArgumentType.class, new ConstantArgumentSerializer<MessageArgumentType>(MessageArgumentType::message));
        ArgumentTypes.register("nbt_compound_tag", NbtCompoundTagArgumentType.class, new ConstantArgumentSerializer<NbtCompoundTagArgumentType>(NbtCompoundTagArgumentType::nbtCompound));
        ArgumentTypes.register("nbt_tag", NbtTagArgumentType.class, new ConstantArgumentSerializer<NbtTagArgumentType>(NbtTagArgumentType::nbtTag));
        ArgumentTypes.register("nbt_path", NbtPathArgumentType.class, new ConstantArgumentSerializer<NbtPathArgumentType>(NbtPathArgumentType::nbtPath));
        ArgumentTypes.register("objective", ObjectiveArgumentType.class, new ConstantArgumentSerializer<ObjectiveArgumentType>(ObjectiveArgumentType::objective));
        ArgumentTypes.register("objective_criteria", ObjectiveCriteriaArgumentType.class, new ConstantArgumentSerializer<ObjectiveCriteriaArgumentType>(ObjectiveCriteriaArgumentType::objectiveCriteria));
        ArgumentTypes.register("operation", OperationArgumentType.class, new ConstantArgumentSerializer<OperationArgumentType>(OperationArgumentType::operation));
        ArgumentTypes.register("particle", ParticleArgumentType.class, new ConstantArgumentSerializer<ParticleArgumentType>(ParticleArgumentType::particle));
        ArgumentTypes.register("rotation", RotationArgumentType.class, new ConstantArgumentSerializer<RotationArgumentType>(RotationArgumentType::rotation));
        ArgumentTypes.register("scoreboard_slot", ScoreboardSlotArgumentType.class, new ConstantArgumentSerializer<ScoreboardSlotArgumentType>(ScoreboardSlotArgumentType::scoreboardSlot));
        ArgumentTypes.register("score_holder", ScoreHolderArgumentType.class, new ScoreHolderArgumentType.Serializer());
        ArgumentTypes.register("swizzle", SwizzleArgumentType.class, new ConstantArgumentSerializer<SwizzleArgumentType>(SwizzleArgumentType::swizzle));
        ArgumentTypes.register("team", TeamArgumentType.class, new ConstantArgumentSerializer<TeamArgumentType>(TeamArgumentType::team));
        ArgumentTypes.register("item_slot", ItemSlotArgumentType.class, new ConstantArgumentSerializer<ItemSlotArgumentType>(ItemSlotArgumentType::itemSlot));
        ArgumentTypes.register("resource_location", IdentifierArgumentType.class, new ConstantArgumentSerializer<IdentifierArgumentType>(IdentifierArgumentType::identifier));
        ArgumentTypes.register("mob_effect", MobEffectArgumentType.class, new ConstantArgumentSerializer<MobEffectArgumentType>(MobEffectArgumentType::mobEffect));
        ArgumentTypes.register("function", FunctionArgumentType.class, new ConstantArgumentSerializer<FunctionArgumentType>(FunctionArgumentType::function));
        ArgumentTypes.register("entity_anchor", EntityAnchorArgumentType.class, new ConstantArgumentSerializer<EntityAnchorArgumentType>(EntityAnchorArgumentType::entityAnchor));
        ArgumentTypes.register("int_range", NumberRangeArgumentType.IntRangeArgumentType.class, new NumberRangeArgumentType.IntRangeArgumentType.Serializer());
        ArgumentTypes.register("float_range", NumberRangeArgumentType.FloatRangeArgumentType.class, new NumberRangeArgumentType.FloatRangeArgumentType.Serializer());
        ArgumentTypes.register("item_enchantment", ItemEnchantmentArgumentType.class, new ConstantArgumentSerializer<ItemEnchantmentArgumentType>(ItemEnchantmentArgumentType::itemEnchantment));
        ArgumentTypes.register("entity_summon", EntitySummonArgumentType.class, new ConstantArgumentSerializer<EntitySummonArgumentType>(EntitySummonArgumentType::entitySummon));
        ArgumentTypes.register("dimension", DimensionArgumentType.class, new ConstantArgumentSerializer<DimensionArgumentType>(DimensionArgumentType::dimension));
        ArgumentTypes.register("time", TimeArgumentType.class, new ConstantArgumentSerializer<TimeArgumentType>(TimeArgumentType::time));
        if (SharedConstants.isDevelopment) {
            ArgumentTypes.register("test_argument", TestFunctionArgumentType.class, new ConstantArgumentSerializer<TestFunctionArgumentType>(TestFunctionArgumentType::testFunction));
            ArgumentTypes.register("test_class", TestClassArgumentType.class, new ConstantArgumentSerializer<TestClassArgumentType>(TestClassArgumentType::method_22370));
        }
    }

    @Nullable
    private static Entry<?> byId(Identifier identifier) {
        return idMap.get(identifier);
    }

    @Nullable
    private static Entry<?> byClass(ArgumentType<?> argumentType) {
        return classMap.get(argumentType.getClass());
    }

    public static <T extends ArgumentType<?>> void toPacket(PacketByteBuf packetByteBuf, T argumentType) {
        Entry<?> entry = ArgumentTypes.byClass(argumentType);
        if (entry == null) {
            LOGGER.error("Could not serialize {} ({}) - will not be sent to client!", (Object)argumentType, (Object)argumentType.getClass());
            packetByteBuf.writeIdentifier(new Identifier(""));
            return;
        }
        packetByteBuf.writeIdentifier(entry.id);
        entry.serializer.toPacket(argumentType, packetByteBuf);
    }

    @Nullable
    public static ArgumentType<?> fromPacket(PacketByteBuf packetByteBuf) {
        Identifier identifier = packetByteBuf.readIdentifier();
        Entry<?> entry = ArgumentTypes.byId(identifier);
        if (entry == null) {
            LOGGER.error("Could not deserialize {}", (Object)identifier);
            return null;
        }
        return entry.serializer.fromPacket(packetByteBuf);
    }

    private static <T extends ArgumentType<?>> void toJson(JsonObject jsonObject, T argumentType) {
        Entry<?> entry = ArgumentTypes.byClass(argumentType);
        if (entry == null) {
            LOGGER.error("Could not serialize argument {} ({})!", (Object)argumentType, (Object)argumentType.getClass());
            jsonObject.addProperty("type", "unknown");
        } else {
            jsonObject.addProperty("type", "argument");
            jsonObject.addProperty("parser", entry.id.toString());
            JsonObject jsonObject2 = new JsonObject();
            entry.serializer.toJson(argumentType, jsonObject2);
            if (jsonObject2.size() > 0) {
                jsonObject.add("properties", jsonObject2);
            }
        }
    }

    public static <S> JsonObject toJson(CommandDispatcher<S> commandDispatcher, CommandNode<S> commandNode) {
        Collection<String> collection;
        JsonObject jsonObject = new JsonObject();
        if (commandNode instanceof RootCommandNode) {
            jsonObject.addProperty("type", "root");
        } else if (commandNode instanceof LiteralCommandNode) {
            jsonObject.addProperty("type", "literal");
        } else if (commandNode instanceof ArgumentCommandNode) {
            ArgumentTypes.toJson(jsonObject, ((ArgumentCommandNode)commandNode).getType());
        } else {
            LOGGER.error("Could not serialize node {} ({})!", (Object)commandNode, (Object)commandNode.getClass());
            jsonObject.addProperty("type", "unknown");
        }
        JsonObject jsonObject2 = new JsonObject();
        for (CommandNode<S> commandNode2 : commandNode.getChildren()) {
            jsonObject2.add(commandNode2.getName(), ArgumentTypes.toJson(commandDispatcher, commandNode2));
        }
        if (jsonObject2.size() > 0) {
            jsonObject.add("children", jsonObject2);
        }
        if (commandNode.getCommand() != null) {
            jsonObject.addProperty("executable", true);
        }
        if (commandNode.getRedirect() != null && !(collection = commandDispatcher.getPath(commandNode.getRedirect())).isEmpty()) {
            JsonArray jsonArray = new JsonArray();
            for (String string : collection) {
                jsonArray.add(string);
            }
            jsonObject.add("redirect", jsonArray);
        }
        return jsonObject;
    }

    static class Entry<T extends ArgumentType<?>> {
        public final Class<T> argClass;
        public final ArgumentSerializer<T> serializer;
        public final Identifier id;

        private Entry(Class<T> class_, ArgumentSerializer<T> argumentSerializer, Identifier identifier) {
            this.argClass = class_;
            this.serializer = argumentSerializer;
            this.id = identifier;
        }
    }
}

