package net.minecraft.command.argument;

import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.Locale;
import java.util.Map;
import net.minecraft.SharedConstants;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.command.argument.serialize.DoubleArgumentSerializer;
import net.minecraft.command.argument.serialize.FloatArgumentSerializer;
import net.minecraft.command.argument.serialize.IntegerArgumentSerializer;
import net.minecraft.command.argument.serialize.LongArgumentSerializer;
import net.minecraft.command.argument.serialize.StringArgumentSerializer;
import net.minecraft.registry.Registry;

public class ArgumentTypes {
	private static final Map<Class<?>, ArgumentSerializer<?, ?>> CLASS_MAP = Maps.<Class<?>, ArgumentSerializer<?, ?>>newHashMap();

	/**
	 * Registers an argument type's serializer.
	 */
	private static <A extends ArgumentType<?>, T extends ArgumentSerializer.ArgumentTypeProperties<A>> ArgumentSerializer<A, T> register(
		Registry<ArgumentSerializer<?, ?>> registry, String id, Class<? extends A> clazz, ArgumentSerializer<A, T> serializer
	) {
		CLASS_MAP.put(clazz, serializer);
		return Registry.register(registry, id, serializer);
	}

	public static ArgumentSerializer<?, ?> register(Registry<ArgumentSerializer<?, ?>> registry) {
		register(registry, "brigadier:bool", BoolArgumentType.class, ConstantArgumentSerializer.of(BoolArgumentType::bool));
		register(registry, "brigadier:float", FloatArgumentType.class, new FloatArgumentSerializer());
		register(registry, "brigadier:double", DoubleArgumentType.class, new DoubleArgumentSerializer());
		register(registry, "brigadier:integer", IntegerArgumentType.class, new IntegerArgumentSerializer());
		register(registry, "brigadier:long", LongArgumentType.class, new LongArgumentSerializer());
		register(registry, "brigadier:string", StringArgumentType.class, new StringArgumentSerializer());
		register(registry, "entity", EntityArgumentType.class, new EntityArgumentType.Serializer());
		register(registry, "game_profile", GameProfileArgumentType.class, ConstantArgumentSerializer.of(GameProfileArgumentType::gameProfile));
		register(registry, "block_pos", BlockPosArgumentType.class, ConstantArgumentSerializer.of(BlockPosArgumentType::blockPos));
		register(registry, "column_pos", ColumnPosArgumentType.class, ConstantArgumentSerializer.of(ColumnPosArgumentType::columnPos));
		register(registry, "vec3", Vec3ArgumentType.class, ConstantArgumentSerializer.of(Vec3ArgumentType::vec3));
		register(registry, "vec2", Vec2ArgumentType.class, ConstantArgumentSerializer.of(Vec2ArgumentType::vec2));
		register(registry, "block_state", BlockStateArgumentType.class, ConstantArgumentSerializer.of(BlockStateArgumentType::blockState));
		register(registry, "block_predicate", BlockPredicateArgumentType.class, ConstantArgumentSerializer.of(BlockPredicateArgumentType::blockPredicate));
		register(registry, "item_stack", ItemStackArgumentType.class, ConstantArgumentSerializer.of(ItemStackArgumentType::itemStack));
		register(registry, "item_predicate", ItemPredicateArgumentType.class, ConstantArgumentSerializer.of(ItemPredicateArgumentType::itemPredicate));
		register(registry, "color", ColorArgumentType.class, ConstantArgumentSerializer.of(ColorArgumentType::color));
		register(registry, "component", TextArgumentType.class, ConstantArgumentSerializer.of(TextArgumentType::text));
		register(registry, "message", MessageArgumentType.class, ConstantArgumentSerializer.of(MessageArgumentType::message));
		register(registry, "nbt_compound_tag", NbtCompoundArgumentType.class, ConstantArgumentSerializer.of(NbtCompoundArgumentType::nbtCompound));
		register(registry, "nbt_tag", NbtElementArgumentType.class, ConstantArgumentSerializer.of(NbtElementArgumentType::nbtElement));
		register(registry, "nbt_path", NbtPathArgumentType.class, ConstantArgumentSerializer.of(NbtPathArgumentType::nbtPath));
		register(registry, "objective", ScoreboardObjectiveArgumentType.class, ConstantArgumentSerializer.of(ScoreboardObjectiveArgumentType::scoreboardObjective));
		register(
			registry, "objective_criteria", ScoreboardCriterionArgumentType.class, ConstantArgumentSerializer.of(ScoreboardCriterionArgumentType::scoreboardCriterion)
		);
		register(registry, "operation", OperationArgumentType.class, ConstantArgumentSerializer.of(OperationArgumentType::operation));
		register(registry, "particle", ParticleEffectArgumentType.class, ConstantArgumentSerializer.of(ParticleEffectArgumentType::particleEffect));
		register(registry, "angle", AngleArgumentType.class, ConstantArgumentSerializer.of(AngleArgumentType::angle));
		register(registry, "rotation", RotationArgumentType.class, ConstantArgumentSerializer.of(RotationArgumentType::rotation));
		register(registry, "scoreboard_slot", ScoreboardSlotArgumentType.class, ConstantArgumentSerializer.of(ScoreboardSlotArgumentType::scoreboardSlot));
		register(registry, "score_holder", ScoreHolderArgumentType.class, new ScoreHolderArgumentType.Serializer());
		register(registry, "swizzle", SwizzleArgumentType.class, ConstantArgumentSerializer.of(SwizzleArgumentType::swizzle));
		register(registry, "team", TeamArgumentType.class, ConstantArgumentSerializer.of(TeamArgumentType::team));
		register(registry, "item_slot", ItemSlotArgumentType.class, ConstantArgumentSerializer.of(ItemSlotArgumentType::itemSlot));
		register(registry, "resource_location", IdentifierArgumentType.class, ConstantArgumentSerializer.of(IdentifierArgumentType::identifier));
		register(registry, "function", CommandFunctionArgumentType.class, ConstantArgumentSerializer.of(CommandFunctionArgumentType::commandFunction));
		register(registry, "entity_anchor", EntityAnchorArgumentType.class, ConstantArgumentSerializer.of(EntityAnchorArgumentType::entityAnchor));
		register(registry, "int_range", NumberRangeArgumentType.IntRangeArgumentType.class, ConstantArgumentSerializer.of(NumberRangeArgumentType::intRange));
		register(registry, "float_range", NumberRangeArgumentType.FloatRangeArgumentType.class, ConstantArgumentSerializer.of(NumberRangeArgumentType::floatRange));
		register(registry, "dimension", DimensionArgumentType.class, ConstantArgumentSerializer.of(DimensionArgumentType::dimension));
		register(registry, "gamemode", GameModeArgumentType.class, ConstantArgumentSerializer.of(GameModeArgumentType::gameMode));
		register(registry, "time", TimeArgumentType.class, new TimeArgumentType.Serializer());
		register(registry, "resource_or_tag", upcast(RegistryEntryPredicateArgumentType.class), new RegistryEntryPredicateArgumentType.Serializer());
		register(registry, "resource_or_tag_key", upcast(RegistryPredicateArgumentType.class), new RegistryPredicateArgumentType.Serializer());
		register(registry, "resource", upcast(RegistryEntryArgumentType.class), new RegistryEntryArgumentType.Serializer());
		register(registry, "resource_key", upcast(RegistryKeyArgumentType.class), new RegistryKeyArgumentType.Serializer());
		register(registry, "template_mirror", BlockMirrorArgumentType.class, ConstantArgumentSerializer.of(BlockMirrorArgumentType::blockMirror));
		register(registry, "template_rotation", BlockRotationArgumentType.class, ConstantArgumentSerializer.of(BlockRotationArgumentType::blockRotation));
		register(registry, "heightmap", HeightmapArgumentType.class, ConstantArgumentSerializer.of(HeightmapArgumentType::heightmap));
		if (SharedConstants.isDevelopment) {
			register(registry, "test_argument", TestFunctionArgumentType.class, ConstantArgumentSerializer.of(TestFunctionArgumentType::testFunction));
			register(registry, "test_class", TestClassArgumentType.class, ConstantArgumentSerializer.of(TestClassArgumentType::testClass));
		}

		return register(registry, "uuid", UuidArgumentType.class, ConstantArgumentSerializer.of(UuidArgumentType::uuid));
	}

	private static <T extends ArgumentType<?>> Class<T> upcast(Class<? super T> clazz) {
		return (Class<T>)clazz;
	}

	public static boolean has(Class<?> clazz) {
		return CLASS_MAP.containsKey(clazz);
	}

	public static <A extends ArgumentType<?>> ArgumentSerializer<A, ?> get(A argumentType) {
		ArgumentSerializer<?, ?> argumentSerializer = (ArgumentSerializer<?, ?>)CLASS_MAP.get(argumentType.getClass());
		if (argumentSerializer == null) {
			throw new IllegalArgumentException(String.format(Locale.ROOT, "Unrecognized argument type %s (%s)", argumentType, argumentType.getClass()));
		} else {
			return (ArgumentSerializer<A, ?>)argumentSerializer;
		}
	}

	public static <A extends ArgumentType<?>> ArgumentSerializer.ArgumentTypeProperties<A> getArgumentTypeProperties(A argumentType) {
		return get(argumentType).getArgumentTypeProperties(argumentType);
	}
}
