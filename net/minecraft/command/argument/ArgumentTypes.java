/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.command.argument.AngleArgumentType;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockPredicateArgumentType;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.command.argument.BrigadierArgumentTypes;
import net.minecraft.command.argument.ColorArgumentType;
import net.minecraft.command.argument.ColumnPosArgumentType;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.EnchantmentArgumentType;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.EntitySummonArgumentType;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.ItemPredicateArgumentType;
import net.minecraft.command.argument.ItemSlotArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.command.argument.NbtElementArgumentType;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.command.argument.NumberRangeArgumentType;
import net.minecraft.command.argument.OperationArgumentType;
import net.minecraft.command.argument.ParticleEffectArgumentType;
import net.minecraft.command.argument.RegistryPredicateArgumentType;
import net.minecraft.command.argument.RotationArgumentType;
import net.minecraft.command.argument.ScoreHolderArgumentType;
import net.minecraft.command.argument.ScoreboardCriterionArgumentType;
import net.minecraft.command.argument.ScoreboardObjectiveArgumentType;
import net.minecraft.command.argument.ScoreboardSlotArgumentType;
import net.minecraft.command.argument.StatusEffectArgumentType;
import net.minecraft.command.argument.SwizzleArgumentType;
import net.minecraft.command.argument.TeamArgumentType;
import net.minecraft.command.argument.TestClassArgumentType;
import net.minecraft.command.argument.TestFunctionArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.command.argument.TimeArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.command.argument.Vec2ArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ArgumentTypes {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<Class<?>, Entry<?>> CLASS_MAP = Maps.newHashMap();
    private static final Map<Identifier, Entry<?>> ID_MAP = Maps.newHashMap();

    /**
     * Registers an argument type's serializer.
     * 
     * @param id the id of the argument type
     */
    public static <T extends ArgumentType<?>> void register(String id, Class<T> argClass, ArgumentSerializer<T> serializer) {
        Identifier identifier = new Identifier(id);
        if (CLASS_MAP.containsKey(argClass)) {
            throw new IllegalArgumentException("Class " + argClass.getName() + " already has a serializer!");
        }
        if (ID_MAP.containsKey(identifier)) {
            throw new IllegalArgumentException("'" + identifier + "' is already a registered serializer!");
        }
        Entry<T> entry = new Entry<T>(serializer, identifier);
        CLASS_MAP.put(argClass, entry);
        ID_MAP.put(identifier, entry);
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
        ArgumentTypes.register("nbt_compound_tag", NbtCompoundArgumentType.class, new ConstantArgumentSerializer<NbtCompoundArgumentType>(NbtCompoundArgumentType::nbtCompound));
        ArgumentTypes.register("nbt_tag", NbtElementArgumentType.class, new ConstantArgumentSerializer<NbtElementArgumentType>(NbtElementArgumentType::nbtElement));
        ArgumentTypes.register("nbt_path", NbtPathArgumentType.class, new ConstantArgumentSerializer<NbtPathArgumentType>(NbtPathArgumentType::nbtPath));
        ArgumentTypes.register("objective", ScoreboardObjectiveArgumentType.class, new ConstantArgumentSerializer<ScoreboardObjectiveArgumentType>(ScoreboardObjectiveArgumentType::scoreboardObjective));
        ArgumentTypes.register("objective_criteria", ScoreboardCriterionArgumentType.class, new ConstantArgumentSerializer<ScoreboardCriterionArgumentType>(ScoreboardCriterionArgumentType::scoreboardCriterion));
        ArgumentTypes.register("operation", OperationArgumentType.class, new ConstantArgumentSerializer<OperationArgumentType>(OperationArgumentType::operation));
        ArgumentTypes.register("particle", ParticleEffectArgumentType.class, new ConstantArgumentSerializer<ParticleEffectArgumentType>(ParticleEffectArgumentType::particleEffect));
        ArgumentTypes.register("angle", AngleArgumentType.class, new ConstantArgumentSerializer<AngleArgumentType>(AngleArgumentType::angle));
        ArgumentTypes.register("rotation", RotationArgumentType.class, new ConstantArgumentSerializer<RotationArgumentType>(RotationArgumentType::rotation));
        ArgumentTypes.register("scoreboard_slot", ScoreboardSlotArgumentType.class, new ConstantArgumentSerializer<ScoreboardSlotArgumentType>(ScoreboardSlotArgumentType::scoreboardSlot));
        ArgumentTypes.register("score_holder", ScoreHolderArgumentType.class, new ScoreHolderArgumentType.Serializer());
        ArgumentTypes.register("swizzle", SwizzleArgumentType.class, new ConstantArgumentSerializer<SwizzleArgumentType>(SwizzleArgumentType::swizzle));
        ArgumentTypes.register("team", TeamArgumentType.class, new ConstantArgumentSerializer<TeamArgumentType>(TeamArgumentType::team));
        ArgumentTypes.register("item_slot", ItemSlotArgumentType.class, new ConstantArgumentSerializer<ItemSlotArgumentType>(ItemSlotArgumentType::itemSlot));
        ArgumentTypes.register("resource_location", IdentifierArgumentType.class, new ConstantArgumentSerializer<IdentifierArgumentType>(IdentifierArgumentType::identifier));
        ArgumentTypes.register("mob_effect", StatusEffectArgumentType.class, new ConstantArgumentSerializer<StatusEffectArgumentType>(StatusEffectArgumentType::statusEffect));
        ArgumentTypes.register("function", CommandFunctionArgumentType.class, new ConstantArgumentSerializer<CommandFunctionArgumentType>(CommandFunctionArgumentType::commandFunction));
        ArgumentTypes.register("entity_anchor", EntityAnchorArgumentType.class, new ConstantArgumentSerializer<EntityAnchorArgumentType>(EntityAnchorArgumentType::entityAnchor));
        ArgumentTypes.register("int_range", NumberRangeArgumentType.IntRangeArgumentType.class, new ConstantArgumentSerializer<NumberRangeArgumentType.IntRangeArgumentType>(NumberRangeArgumentType::intRange));
        ArgumentTypes.register("float_range", NumberRangeArgumentType.FloatRangeArgumentType.class, new ConstantArgumentSerializer<NumberRangeArgumentType.FloatRangeArgumentType>(NumberRangeArgumentType::floatRange));
        ArgumentTypes.register("item_enchantment", EnchantmentArgumentType.class, new ConstantArgumentSerializer<EnchantmentArgumentType>(EnchantmentArgumentType::enchantment));
        ArgumentTypes.register("entity_summon", EntitySummonArgumentType.class, new ConstantArgumentSerializer<EntitySummonArgumentType>(EntitySummonArgumentType::entitySummon));
        ArgumentTypes.register("dimension", DimensionArgumentType.class, new ConstantArgumentSerializer<DimensionArgumentType>(DimensionArgumentType::dimension));
        ArgumentTypes.register("time", TimeArgumentType.class, new ConstantArgumentSerializer<TimeArgumentType>(TimeArgumentType::time));
        ArgumentTypes.register("uuid", UuidArgumentType.class, new ConstantArgumentSerializer<UuidArgumentType>(UuidArgumentType::uuid));
        ArgumentTypes.register("resource_or_tag", ArgumentTypes.upcast(RegistryPredicateArgumentType.class), new RegistryPredicateArgumentType.Serializer());
        if (SharedConstants.isDevelopment) {
            ArgumentTypes.register("test_argument", TestFunctionArgumentType.class, new ConstantArgumentSerializer<TestFunctionArgumentType>(TestFunctionArgumentType::testFunction));
            ArgumentTypes.register("test_class", TestClassArgumentType.class, new ConstantArgumentSerializer<TestClassArgumentType>(TestClassArgumentType::testClass));
        }
    }

    private static <T extends ArgumentType<?>> Class<T> upcast(Class<? super T> clazz) {
        return clazz;
    }

    @Nullable
    private static Entry<?> byId(Identifier id) {
        return ID_MAP.get(id);
    }

    @Nullable
    private static Entry<?> byClass(ArgumentType<?> type) {
        return CLASS_MAP.get(type.getClass());
    }

    public static <T extends ArgumentType<?>> void toPacket(PacketByteBuf buf, T type) {
        Entry<?> entry = ArgumentTypes.byClass(type);
        if (entry == null) {
            LOGGER.error("Could not serialize {} ({}) - will not be sent to client!", (Object)type, (Object)type.getClass());
            buf.writeIdentifier(new Identifier(""));
            return;
        }
        buf.writeIdentifier(entry.id);
        entry.serializer.toPacket(type, buf);
    }

    @Nullable
    public static ArgumentType<?> fromPacket(PacketByteBuf buf) {
        Identifier identifier = buf.readIdentifier();
        Entry<?> entry = ArgumentTypes.byId(identifier);
        if (entry == null) {
            LOGGER.error("Could not deserialize {}", (Object)identifier);
            return null;
        }
        return entry.serializer.fromPacket(buf);
    }

    private static <T extends ArgumentType<?>> void toJson(JsonObject json, T type) {
        Entry<?> entry = ArgumentTypes.byClass(type);
        if (entry == null) {
            LOGGER.error("Could not serialize argument {} ({})!", (Object)type, (Object)type.getClass());
            json.addProperty("type", "unknown");
        } else {
            json.addProperty("type", "argument");
            json.addProperty("parser", entry.id.toString());
            JsonObject jsonObject = new JsonObject();
            entry.serializer.toJson(type, jsonObject);
            if (jsonObject.size() > 0) {
                json.add("properties", jsonObject);
            }
        }
    }

    public static <S> JsonObject toJson(CommandDispatcher<S> dispatcher, CommandNode<S> commandNode) {
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
            jsonObject2.add(commandNode2.getName(), ArgumentTypes.toJson(dispatcher, commandNode2));
        }
        if (jsonObject2.size() > 0) {
            jsonObject.add("children", jsonObject2);
        }
        if (commandNode.getCommand() != null) {
            jsonObject.addProperty("executable", true);
        }
        if (commandNode.getRedirect() != null && !(collection = dispatcher.getPath(commandNode.getRedirect())).isEmpty()) {
            JsonArray jsonArray = new JsonArray();
            for (String string : collection) {
                jsonArray.add(string);
            }
            jsonObject.add("redirect", jsonArray);
        }
        return jsonObject;
    }

    public static boolean hasClass(ArgumentType<?> type) {
        return ArgumentTypes.byClass(type) != null;
    }

    public static <T> Set<ArgumentType<?>> getAllArgumentTypes(CommandNode<T> node) {
        Set<CommandNode<T>> set = Sets.newIdentityHashSet();
        HashSet<ArgumentType<?>> set2 = Sets.newHashSet();
        ArgumentTypes.getAllArgumentTypes(node, set2, set);
        return set2;
    }

    private static <T> void getAllArgumentTypes(CommandNode<T> node2, Set<ArgumentType<?>> argumentTypes, Set<CommandNode<T>> ignoredNodes) {
        if (!ignoredNodes.add(node2)) {
            return;
        }
        if (node2 instanceof ArgumentCommandNode) {
            argumentTypes.add(((ArgumentCommandNode)node2).getType());
        }
        node2.getChildren().forEach(node -> ArgumentTypes.getAllArgumentTypes(node, argumentTypes, ignoredNodes));
        CommandNode<T> commandNode = node2.getRedirect();
        if (commandNode != null) {
            ArgumentTypes.getAllArgumentTypes(commandNode, argumentTypes, ignoredNodes);
        }
    }

    static class Entry<T extends ArgumentType<?>> {
        public final ArgumentSerializer<T> serializer;
        public final Identifier id;

        Entry(ArgumentSerializer<T> serializer, Identifier id) {
            this.serializer = serializer;
            this.id = id;
        }
    }
}

