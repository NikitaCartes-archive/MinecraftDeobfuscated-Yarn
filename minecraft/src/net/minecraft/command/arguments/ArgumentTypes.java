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
import javax.annotation.Nullable;
import net.minecraft.command.arguments.serialize.ArgumentSerializer;
import net.minecraft.command.arguments.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArgumentTypes {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<Class<?>, ArgumentTypes.Entry<?>> classMap = Maps.<Class<?>, ArgumentTypes.Entry<?>>newHashMap();
	private static final Map<Identifier, ArgumentTypes.Entry<?>> idMap = Maps.<Identifier, ArgumentTypes.Entry<?>>newHashMap();

	public static <T extends ArgumentType<?>> void register(String string, Class<T> class_, ArgumentSerializer<T> argumentSerializer) {
		Identifier identifier = new Identifier(string);
		if (classMap.containsKey(class_)) {
			throw new IllegalArgumentException("Class " + class_.getName() + " already has a serializer!");
		} else if (idMap.containsKey(identifier)) {
			throw new IllegalArgumentException("'" + identifier + "' is already a registered serializer!");
		} else {
			ArgumentTypes.Entry<T> entry = new ArgumentTypes.Entry<>(class_, argumentSerializer, identifier);
			classMap.put(class_, entry);
			idMap.put(identifier, entry);
		}
	}

	public static void register() {
		BrigadierArgumentTypes.register();
		register("entity", EntityArgumentType.class, new EntityArgumentType.Serializer());
		register("game_profile", GameProfileArgumentType.class, new ConstantArgumentSerializer(GameProfileArgumentType::create));
		register("block_pos", BlockPosArgumentType.class, new ConstantArgumentSerializer(BlockPosArgumentType::create));
		register("column_pos", ColumnPosArgumentType.class, new ConstantArgumentSerializer(ColumnPosArgumentType::create));
		register("vec3", Vec3ArgumentType.class, new ConstantArgumentSerializer(Vec3ArgumentType::create));
		register("vec2", Vec2ArgumentType.class, new ConstantArgumentSerializer(Vec2ArgumentType::create));
		register("block_state", BlockStateArgumentType.class, new ConstantArgumentSerializer(BlockStateArgumentType::create));
		register("block_predicate", BlockPredicateArgumentType.class, new ConstantArgumentSerializer(BlockPredicateArgumentType::create));
		register("item_stack", ItemStackArgumentType.class, new ConstantArgumentSerializer(ItemStackArgumentType::create));
		register("item_predicate", ItemPredicateArgumentType.class, new ConstantArgumentSerializer(ItemPredicateArgumentType::create));
		register("color", ColorArgumentType.class, new ConstantArgumentSerializer(ColorArgumentType::create));
		register("component", TextArgumentType.class, new ConstantArgumentSerializer(TextArgumentType::create));
		register("message", MessageArgumentType.class, new ConstantArgumentSerializer(MessageArgumentType::create));
		register("nbt_compound_tag", NbtCompoundTagArgumentType.class, new ConstantArgumentSerializer(NbtCompoundTagArgumentType::create));
		register("nbt_tag", NbtTagArgumentType.class, new ConstantArgumentSerializer(NbtTagArgumentType::create));
		register("nbt_path", NbtPathArgumentType.class, new ConstantArgumentSerializer(NbtPathArgumentType::create));
		register("objective", ObjectiveArgumentType.class, new ConstantArgumentSerializer(ObjectiveArgumentType::create));
		register("objective_criteria", ObjectiveCriteriaArgumentType.class, new ConstantArgumentSerializer(ObjectiveCriteriaArgumentType::create));
		register("operation", OperationArgumentType.class, new ConstantArgumentSerializer(OperationArgumentType::create));
		register("particle", ParticleArgumentType.class, new ConstantArgumentSerializer(ParticleArgumentType::create));
		register("rotation", RotationArgumentType.class, new ConstantArgumentSerializer(RotationArgumentType::create));
		register("scoreboard_slot", ScoreboardSlotArgumentType.class, new ConstantArgumentSerializer(ScoreboardSlotArgumentType::create));
		register("score_holder", ScoreHolderArgumentType.class, new ScoreHolderArgumentType.Serializer());
		register("swizzle", SwizzleArgumentType.class, new ConstantArgumentSerializer(SwizzleArgumentType::create));
		register("team", TeamArgumentType.class, new ConstantArgumentSerializer(TeamArgumentType::create));
		register("item_slot", ItemSlotArgumentType.class, new ConstantArgumentSerializer(ItemSlotArgumentType::create));
		register("resource_location", IdentifierArgumentType.class, new ConstantArgumentSerializer(IdentifierArgumentType::create));
		register("mob_effect", MobEffectArgumentType.class, new ConstantArgumentSerializer(MobEffectArgumentType::create));
		register("function", FunctionArgumentType.class, new ConstantArgumentSerializer(FunctionArgumentType::create));
		register("entity_anchor", EntityAnchorArgumentType.class, new ConstantArgumentSerializer(EntityAnchorArgumentType::create));
		register("int_range", NumberRangeArgumentType.IntRangeArgumentType.class, new NumberRangeArgumentType.IntRangeArgumentType.Serializer());
		register("float_range", NumberRangeArgumentType.FloatRangeArgumentType.class, new NumberRangeArgumentType.FloatRangeArgumentType.Serializer());
		register("item_enchantment", ItemEnchantmentArgumentType.class, new ConstantArgumentSerializer(ItemEnchantmentArgumentType::create));
		register("entity_summon", EntitySummonArgumentType.class, new ConstantArgumentSerializer(EntitySummonArgumentType::create));
		register("dimension", DimensionArgumentType.class, new ConstantArgumentSerializer(DimensionArgumentType::create));
		register("time", TimeArgumentType.class, new ConstantArgumentSerializer(TimeArgumentType::create));
	}

	@Nullable
	private static ArgumentTypes.Entry<?> byId(Identifier identifier) {
		return (ArgumentTypes.Entry<?>)idMap.get(identifier);
	}

	@Nullable
	private static ArgumentTypes.Entry<?> byClass(ArgumentType<?> argumentType) {
		return (ArgumentTypes.Entry<?>)classMap.get(argumentType.getClass());
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
	public static ArgumentType<?> fromPacket(PacketByteBuf packetByteBuf) {
		Identifier identifier = packetByteBuf.readIdentifier();
		ArgumentTypes.Entry<?> entry = byId(identifier);
		if (entry == null) {
			LOGGER.error("Could not deserialize {}", identifier);
			return null;
		} else {
			return entry.serializer.fromPacket(packetByteBuf);
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
