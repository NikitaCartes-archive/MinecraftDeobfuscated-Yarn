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
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArgumentTypes {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<Class<?>, ArgumentTypes.Entry<?>> CLASS_MAP = Maps.<Class<?>, ArgumentTypes.Entry<?>>newHashMap();
	private static final Map<Identifier, ArgumentTypes.Entry<?>> ID_MAP = Maps.<Identifier, ArgumentTypes.Entry<?>>newHashMap();

	/**
	 * Registers an argument type's serializer.
	 * 
	 * @param id the id of the argument type
	 */
	public static <T extends ArgumentType<?>> void register(String id, Class<T> class_, ArgumentSerializer<T> argumentSerializer) {
		Identifier identifier = new Identifier(id);
		if (CLASS_MAP.containsKey(class_)) {
			throw new IllegalArgumentException("Class " + class_.getName() + " already has a serializer!");
		} else if (ID_MAP.containsKey(identifier)) {
			throw new IllegalArgumentException("'" + identifier + "' is already a registered serializer!");
		} else {
			ArgumentTypes.Entry<T> entry = new ArgumentTypes.Entry<>(class_, argumentSerializer, identifier);
			CLASS_MAP.put(class_, entry);
			ID_MAP.put(identifier, entry);
		}
	}

	public static void register() {
		BrigadierArgumentTypes.register();
		register("entity", EntityArgumentType.class, new EntityArgumentType.Serializer());
		register("game_profile", GameProfileArgumentType.class, new ConstantArgumentSerializer(GameProfileArgumentType::gameProfile));
		register("block_pos", BlockPosArgumentType.class, new ConstantArgumentSerializer(BlockPosArgumentType::blockPos));
		register("column_pos", ColumnPosArgumentType.class, new ConstantArgumentSerializer(ColumnPosArgumentType::columnPos));
		register("vec3", Vec3ArgumentType.class, new ConstantArgumentSerializer(Vec3ArgumentType::vec3));
		register("vec2", Vec2ArgumentType.class, new ConstantArgumentSerializer(Vec2ArgumentType::vec2));
		register("block_state", BlockStateArgumentType.class, new ConstantArgumentSerializer(BlockStateArgumentType::blockState));
		register("block_predicate", BlockPredicateArgumentType.class, new ConstantArgumentSerializer(BlockPredicateArgumentType::blockPredicate));
		register("item_stack", ItemStackArgumentType.class, new ConstantArgumentSerializer(ItemStackArgumentType::itemStack));
		register("item_predicate", ItemPredicateArgumentType.class, new ConstantArgumentSerializer(ItemPredicateArgumentType::itemPredicate));
		register("color", ColorArgumentType.class, new ConstantArgumentSerializer(ColorArgumentType::color));
		register("component", TextArgumentType.class, new ConstantArgumentSerializer(TextArgumentType::text));
		register("message", MessageArgumentType.class, new ConstantArgumentSerializer(MessageArgumentType::message));
		register("nbt_compound_tag", NbtCompoundArgumentType.class, new ConstantArgumentSerializer(NbtCompoundArgumentType::nbtCompound));
		register("nbt_tag", NbtElementArgumentType.class, new ConstantArgumentSerializer(NbtElementArgumentType::nbtElement));
		register("nbt_path", NbtPathArgumentType.class, new ConstantArgumentSerializer(NbtPathArgumentType::nbtPath));
		register("objective", ScoreboardObjectiveArgumentType.class, new ConstantArgumentSerializer(ScoreboardObjectiveArgumentType::scoreboardObjective));
		register("objective_criteria", ScoreboardCriterionArgumentType.class, new ConstantArgumentSerializer(ScoreboardCriterionArgumentType::scoreboardCriterion));
		register("operation", OperationArgumentType.class, new ConstantArgumentSerializer(OperationArgumentType::operation));
		register("particle", ParticleEffectArgumentType.class, new ConstantArgumentSerializer(ParticleEffectArgumentType::particleEffect));
		register("angle", AngleArgumentType.class, new ConstantArgumentSerializer(AngleArgumentType::angle));
		register("rotation", RotationArgumentType.class, new ConstantArgumentSerializer(RotationArgumentType::rotation));
		register("scoreboard_slot", ScoreboardSlotArgumentType.class, new ConstantArgumentSerializer(ScoreboardSlotArgumentType::scoreboardSlot));
		register("score_holder", ScoreHolderArgumentType.class, new ScoreHolderArgumentType.Serializer());
		register("swizzle", SwizzleArgumentType.class, new ConstantArgumentSerializer(SwizzleArgumentType::swizzle));
		register("team", TeamArgumentType.class, new ConstantArgumentSerializer(TeamArgumentType::team));
		register("item_slot", ItemSlotArgumentType.class, new ConstantArgumentSerializer(ItemSlotArgumentType::itemSlot));
		register("resource_location", IdentifierArgumentType.class, new ConstantArgumentSerializer(IdentifierArgumentType::identifier));
		register("mob_effect", StatusEffectArgumentType.class, new ConstantArgumentSerializer(StatusEffectArgumentType::statusEffect));
		register("function", CommandFunctionArgumentType.class, new ConstantArgumentSerializer(CommandFunctionArgumentType::commandFunction));
		register("entity_anchor", EntityAnchorArgumentType.class, new ConstantArgumentSerializer(EntityAnchorArgumentType::entityAnchor));
		register("int_range", NumberRangeArgumentType.IntRangeArgumentType.class, new ConstantArgumentSerializer(NumberRangeArgumentType::intRange));
		register("float_range", NumberRangeArgumentType.FloatRangeArgumentType.class, new ConstantArgumentSerializer(NumberRangeArgumentType::floatRange));
		register("item_enchantment", EnchantmentArgumentType.class, new ConstantArgumentSerializer(EnchantmentArgumentType::enchantment));
		register("entity_summon", EntitySummonArgumentType.class, new ConstantArgumentSerializer(EntitySummonArgumentType::entitySummon));
		register("dimension", DimensionArgumentType.class, new ConstantArgumentSerializer(DimensionArgumentType::dimension));
		register("time", TimeArgumentType.class, new ConstantArgumentSerializer(TimeArgumentType::time));
		register("uuid", UuidArgumentType.class, new ConstantArgumentSerializer(UuidArgumentType::uuid));
		if (SharedConstants.isDevelopment) {
			register("test_argument", TestFunctionArgumentType.class, new ConstantArgumentSerializer(TestFunctionArgumentType::testFunction));
			register("test_class", TestClassArgumentType.class, new ConstantArgumentSerializer(TestClassArgumentType::testClass));
		}
	}

	@Nullable
	private static ArgumentTypes.Entry<?> byId(Identifier id) {
		return (ArgumentTypes.Entry<?>)ID_MAP.get(id);
	}

	@Nullable
	private static ArgumentTypes.Entry<?> byClass(ArgumentType<?> argumentType) {
		return (ArgumentTypes.Entry<?>)CLASS_MAP.get(argumentType.getClass());
	}

	public static <T extends ArgumentType<?>> void toPacket(PacketByteBuf packetByteBuf, T argumentType) {
		ArgumentTypes.Entry<T> entry = (ArgumentTypes.Entry<T>)byClass(argumentType);
		if (entry == null) {
			LOGGER.error("Could not serialize {} ({}) - will not be sent to client!", argumentType, argumentType.getClass());
			packetByteBuf.writeIdentifier(new Identifier(""));
		} else {
			packetByteBuf.writeIdentifier(entry.id);
			entry.serializer.toPacket(argumentType, packetByteBuf);
		}
	}

	@Nullable
	public static ArgumentType<?> fromPacket(PacketByteBuf buf) {
		Identifier identifier = buf.readIdentifier();
		ArgumentTypes.Entry<?> entry = byId(identifier);
		if (entry == null) {
			LOGGER.error("Could not deserialize {}", identifier);
			return null;
		} else {
			return entry.serializer.fromPacket(buf);
		}
	}

	private static <T extends ArgumentType<?>> void toJson(JsonObject jsonObject, T argumentType) {
		ArgumentTypes.Entry<T> entry = (ArgumentTypes.Entry<T>)byClass(argumentType);
		if (entry == null) {
			LOGGER.error("Could not serialize argument {} ({})!", argumentType, argumentType.getClass());
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
		JsonObject jsonObject = new JsonObject();
		if (commandNode instanceof RootCommandNode) {
			jsonObject.addProperty("type", "root");
		} else if (commandNode instanceof LiteralCommandNode) {
			jsonObject.addProperty("type", "literal");
		} else if (commandNode instanceof ArgumentCommandNode) {
			toJson(jsonObject, ((ArgumentCommandNode)commandNode).getType());
		} else {
			LOGGER.error("Could not serialize node {} ({})!", commandNode, commandNode.getClass());
			jsonObject.addProperty("type", "unknown");
		}

		JsonObject jsonObject2 = new JsonObject();

		for (CommandNode<S> commandNode2 : commandNode.getChildren()) {
			jsonObject2.add(commandNode2.getName(), toJson(commandDispatcher, commandNode2));
		}

		if (jsonObject2.size() > 0) {
			jsonObject.add("children", jsonObject2);
		}

		if (commandNode.getCommand() != null) {
			jsonObject.addProperty("executable", true);
		}

		if (commandNode.getRedirect() != null) {
			Collection<String> collection = commandDispatcher.getPath(commandNode.getRedirect());
			if (!collection.isEmpty()) {
				JsonArray jsonArray = new JsonArray();

				for (String string : collection) {
					jsonArray.add(string);
				}

				jsonObject.add("redirect", jsonArray);
			}
		}

		return jsonObject;
	}

	public static boolean hasClass(ArgumentType<?> argumentType) {
		return byClass(argumentType) != null;
	}

	public static <T> Set<ArgumentType<?>> getAllArgumentTypes(CommandNode<T> node) {
		Set<CommandNode<T>> set = Sets.newIdentityHashSet();
		Set<ArgumentType<?>> set2 = Sets.<ArgumentType<?>>newHashSet();
		getAllArgumentTypes(node, set2, set);
		return set2;
	}

	private static <T> void getAllArgumentTypes(CommandNode<T> node, Set<ArgumentType<?>> argumentTypes, Set<CommandNode<T>> ignoredNodes) {
		if (ignoredNodes.add(node)) {
			if (node instanceof ArgumentCommandNode) {
				argumentTypes.add(((ArgumentCommandNode)node).getType());
			}

			node.getChildren().forEach(nodex -> getAllArgumentTypes(nodex, argumentTypes, ignoredNodes));
			CommandNode<T> commandNode = node.getRedirect();
			if (commandNode != null) {
				getAllArgumentTypes(commandNode, argumentTypes, ignoredNodes);
			}
		}
	}

	static class Entry<T extends ArgumentType<?>> {
		public final Class<T> argClass;
		public final ArgumentSerializer<T> serializer;
		public final Identifier id;

		Entry(Class<T> class_, ArgumentSerializer<T> argumentSerializer, Identifier identifier) {
			this.argClass = class_;
			this.serializer = argumentSerializer;
			this.id = identifier;
		}
	}
}
