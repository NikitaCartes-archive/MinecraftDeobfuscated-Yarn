/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument;

import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.Map;
import net.minecraft.SharedConstants;
import net.minecraft.command.argument.AngleArgumentType;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockPredicateArgumentType;
import net.minecraft.command.argument.BlockStateArgumentType;
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
import net.minecraft.command.argument.RegistryKeyArgumentType;
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
import net.minecraft.command.argument.serialize.DoubleArgumentSerializer;
import net.minecraft.command.argument.serialize.FloatArgumentSerializer;
import net.minecraft.command.argument.serialize.IntegerArgumentSerializer;
import net.minecraft.command.argument.serialize.LongArgumentSerializer;
import net.minecraft.command.argument.serialize.StringArgumentSerializer;
import net.minecraft.util.registry.Registry;

public class ArgumentTypes {
    private static final Map<Class<?>, ArgumentSerializer<?, ?>> CLASS_MAP = Maps.newHashMap();

    /**
     * Registers an argument type's serializer.
     */
    private static <A extends ArgumentType<?>, T extends ArgumentSerializer.ArgumentTypeProperties<A>> ArgumentSerializer<A, T> register(Registry<ArgumentSerializer<?, ?>> registry, String id, Class<? extends A> clazz, ArgumentSerializer<A, T> serializer) {
        CLASS_MAP.put(clazz, serializer);
        return Registry.register(registry, id, serializer);
    }

    public static ArgumentSerializer<?, ?> register(Registry<ArgumentSerializer<?, ?>> registry) {
        ArgumentTypes.register(registry, "brigadier:bool", BoolArgumentType.class, ConstantArgumentSerializer.of(BoolArgumentType::bool));
        ArgumentTypes.register(registry, "brigadier:float", FloatArgumentType.class, new FloatArgumentSerializer());
        ArgumentTypes.register(registry, "brigadier:double", DoubleArgumentType.class, new DoubleArgumentSerializer());
        ArgumentTypes.register(registry, "brigadier:integer", IntegerArgumentType.class, new IntegerArgumentSerializer());
        ArgumentTypes.register(registry, "brigadier:long", LongArgumentType.class, new LongArgumentSerializer());
        ArgumentTypes.register(registry, "brigadier:string", StringArgumentType.class, new StringArgumentSerializer());
        ArgumentTypes.register(registry, "entity", EntityArgumentType.class, new EntityArgumentType.Serializer());
        ArgumentTypes.register(registry, "game_profile", GameProfileArgumentType.class, ConstantArgumentSerializer.of(GameProfileArgumentType::gameProfile));
        ArgumentTypes.register(registry, "block_pos", BlockPosArgumentType.class, ConstantArgumentSerializer.of(BlockPosArgumentType::blockPos));
        ArgumentTypes.register(registry, "column_pos", ColumnPosArgumentType.class, ConstantArgumentSerializer.of(ColumnPosArgumentType::columnPos));
        ArgumentTypes.register(registry, "vec3", Vec3ArgumentType.class, ConstantArgumentSerializer.of(Vec3ArgumentType::vec3));
        ArgumentTypes.register(registry, "vec2", Vec2ArgumentType.class, ConstantArgumentSerializer.of(Vec2ArgumentType::vec2));
        ArgumentTypes.register(registry, "block_state", BlockStateArgumentType.class, ConstantArgumentSerializer.of(BlockStateArgumentType::blockState));
        ArgumentTypes.register(registry, "block_predicate", BlockPredicateArgumentType.class, ConstantArgumentSerializer.of(BlockPredicateArgumentType::blockPredicate));
        ArgumentTypes.register(registry, "item_stack", ItemStackArgumentType.class, ConstantArgumentSerializer.of(ItemStackArgumentType::itemStack));
        ArgumentTypes.register(registry, "item_predicate", ItemPredicateArgumentType.class, ConstantArgumentSerializer.of(ItemPredicateArgumentType::itemPredicate));
        ArgumentTypes.register(registry, "color", ColorArgumentType.class, ConstantArgumentSerializer.of(ColorArgumentType::color));
        ArgumentTypes.register(registry, "component", TextArgumentType.class, ConstantArgumentSerializer.of(TextArgumentType::text));
        ArgumentTypes.register(registry, "message", MessageArgumentType.class, ConstantArgumentSerializer.of(MessageArgumentType::message));
        ArgumentTypes.register(registry, "nbt_compound_tag", NbtCompoundArgumentType.class, ConstantArgumentSerializer.of(NbtCompoundArgumentType::nbtCompound));
        ArgumentTypes.register(registry, "nbt_tag", NbtElementArgumentType.class, ConstantArgumentSerializer.of(NbtElementArgumentType::nbtElement));
        ArgumentTypes.register(registry, "nbt_path", NbtPathArgumentType.class, ConstantArgumentSerializer.of(NbtPathArgumentType::nbtPath));
        ArgumentTypes.register(registry, "objective", ScoreboardObjectiveArgumentType.class, ConstantArgumentSerializer.of(ScoreboardObjectiveArgumentType::scoreboardObjective));
        ArgumentTypes.register(registry, "objective_criteria", ScoreboardCriterionArgumentType.class, ConstantArgumentSerializer.of(ScoreboardCriterionArgumentType::scoreboardCriterion));
        ArgumentTypes.register(registry, "operation", OperationArgumentType.class, ConstantArgumentSerializer.of(OperationArgumentType::operation));
        ArgumentTypes.register(registry, "particle", ParticleEffectArgumentType.class, ConstantArgumentSerializer.of(ParticleEffectArgumentType::particleEffect));
        ArgumentTypes.register(registry, "angle", AngleArgumentType.class, ConstantArgumentSerializer.of(AngleArgumentType::angle));
        ArgumentTypes.register(registry, "rotation", RotationArgumentType.class, ConstantArgumentSerializer.of(RotationArgumentType::rotation));
        ArgumentTypes.register(registry, "scoreboard_slot", ScoreboardSlotArgumentType.class, ConstantArgumentSerializer.of(ScoreboardSlotArgumentType::scoreboardSlot));
        ArgumentTypes.register(registry, "score_holder", ScoreHolderArgumentType.class, new ScoreHolderArgumentType.Serializer());
        ArgumentTypes.register(registry, "swizzle", SwizzleArgumentType.class, ConstantArgumentSerializer.of(SwizzleArgumentType::swizzle));
        ArgumentTypes.register(registry, "team", TeamArgumentType.class, ConstantArgumentSerializer.of(TeamArgumentType::team));
        ArgumentTypes.register(registry, "item_slot", ItemSlotArgumentType.class, ConstantArgumentSerializer.of(ItemSlotArgumentType::itemSlot));
        ArgumentTypes.register(registry, "resource_location", IdentifierArgumentType.class, ConstantArgumentSerializer.of(IdentifierArgumentType::identifier));
        ArgumentTypes.register(registry, "mob_effect", StatusEffectArgumentType.class, ConstantArgumentSerializer.of(StatusEffectArgumentType::statusEffect));
        ArgumentTypes.register(registry, "function", CommandFunctionArgumentType.class, ConstantArgumentSerializer.of(CommandFunctionArgumentType::commandFunction));
        ArgumentTypes.register(registry, "entity_anchor", EntityAnchorArgumentType.class, ConstantArgumentSerializer.of(EntityAnchorArgumentType::entityAnchor));
        ArgumentTypes.register(registry, "int_range", NumberRangeArgumentType.IntRangeArgumentType.class, ConstantArgumentSerializer.of(NumberRangeArgumentType::intRange));
        ArgumentTypes.register(registry, "float_range", NumberRangeArgumentType.FloatRangeArgumentType.class, ConstantArgumentSerializer.of(NumberRangeArgumentType::floatRange));
        ArgumentTypes.register(registry, "item_enchantment", EnchantmentArgumentType.class, ConstantArgumentSerializer.of(EnchantmentArgumentType::enchantment));
        ArgumentTypes.register(registry, "entity_summon", EntitySummonArgumentType.class, ConstantArgumentSerializer.of(EntitySummonArgumentType::entitySummon));
        ArgumentTypes.register(registry, "dimension", DimensionArgumentType.class, ConstantArgumentSerializer.of(DimensionArgumentType::dimension));
        ArgumentTypes.register(registry, "time", TimeArgumentType.class, ConstantArgumentSerializer.of(TimeArgumentType::time));
        ArgumentTypes.register(registry, "resource_or_tag", ArgumentTypes.upcast(RegistryPredicateArgumentType.class), new RegistryPredicateArgumentType.Serializer());
        ArgumentTypes.register(registry, "resource", ArgumentTypes.upcast(RegistryKeyArgumentType.class), new RegistryKeyArgumentType.Serializer());
        if (SharedConstants.isDevelopment) {
            ArgumentTypes.register(registry, "test_argument", TestFunctionArgumentType.class, ConstantArgumentSerializer.of(TestFunctionArgumentType::testFunction));
            ArgumentTypes.register(registry, "test_class", TestClassArgumentType.class, ConstantArgumentSerializer.of(TestClassArgumentType::testClass));
        }
        return ArgumentTypes.register(registry, "uuid", UuidArgumentType.class, ConstantArgumentSerializer.of(UuidArgumentType::uuid));
    }

    private static <T extends ArgumentType<?>> Class<T> upcast(Class<? super T> clazz) {
        return clazz;
    }

    public static boolean has(Class<?> clazz) {
        return CLASS_MAP.containsKey(clazz);
    }

    public static <A extends ArgumentType<?>> ArgumentSerializer<A, ?> get(A argumentType) {
        ArgumentSerializer<?, ?> argumentSerializer = CLASS_MAP.get(argumentType.getClass());
        if (argumentSerializer == null) {
            throw new IllegalArgumentException("Unrecognized argument type %s (%s)".formatted(argumentType, argumentType.getClass()));
        }
        return argumentSerializer;
    }

    public static <A extends ArgumentType<?>> ArgumentSerializer.ArgumentTypeProperties<A> getArgumentTypeProperties(A argumentType) {
        return ArgumentTypes.get(argumentType).getArgumentTypeProperties(argumentType);
    }
}

