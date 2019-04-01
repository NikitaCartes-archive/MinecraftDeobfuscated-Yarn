package net.minecraft;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2316 {
	private static final Logger field_10923 = LogManager.getLogger();
	private static final Map<Class<?>, class_2316.class_2317<?>> field_10921 = Maps.<Class<?>, class_2316.class_2317<?>>newHashMap();
	private static final Map<class_2960, class_2316.class_2317<?>> field_10922 = Maps.<class_2960, class_2316.class_2317<?>>newHashMap();

	public static <T extends ArgumentType<?>> void method_10017(String string, Class<T> class_, class_2314<T> arg) {
		class_2960 lv = new class_2960(string);
		if (field_10921.containsKey(class_)) {
			throw new IllegalArgumentException("Class " + class_.getName() + " already has a serializer!");
		} else if (field_10922.containsKey(lv)) {
			throw new IllegalArgumentException("'" + lv + "' is already a registered serializer!");
		} else {
			class_2316.class_2317<T> lv2 = new class_2316.class_2317<>(class_, arg, lv);
			field_10921.put(class_, lv2);
			field_10922.put(lv, lv2);
		}
	}

	public static void method_10015() {
		class_2324.method_10040();
		method_10017("entity", class_2186.class, new class_2186.class_2187());
		method_10017("game_profile", class_2191.class, new class_2319(class_2191::method_9329));
		method_10017("block_pos", class_2262.class, new class_2319(class_2262::method_9698));
		method_10017("column_pos", class_2264.class, new class_2319(class_2264::method_9701));
		method_10017("vec3", class_2277.class, new class_2319(class_2277::method_9737));
		method_10017("vec2", class_2274.class, new class_2319(class_2274::method_9723));
		method_10017("block_state", class_2257.class, new class_2319(class_2257::method_9653));
		method_10017("block_predicate", class_2252.class, new class_2319(class_2252::method_9645));
		method_10017("item_stack", class_2287.class, new class_2319(class_2287::method_9776));
		method_10017("item_predicate", class_2293.class, new class_2319(class_2293::method_9801));
		method_10017("color", class_2177.class, new class_2319(class_2177::method_9276));
		method_10017("component", class_2178.class, new class_2319(class_2178::method_9281));
		method_10017("message", class_2196.class, new class_2319(class_2196::method_9340));
		method_10017("nbt_compound_tag", class_2179.class, new class_2319(class_2179::method_9284));
		method_10017("nbt_tag", class_2212.class, new class_2319(class_2212::method_9389));
		method_10017("nbt_path", class_2203.class, new class_2319(class_2203::method_9360));
		method_10017("objective", class_2214.class, new class_2319(class_2214::method_9391));
		method_10017("objective_criteria", class_2216.class, new class_2319(class_2216::method_9399));
		method_10017("operation", class_2218.class, new class_2319(class_2218::method_9404));
		method_10017("particle", class_2223.class, new class_2319(class_2223::method_9417));
		method_10017("rotation", class_2270.class, new class_2319(class_2270::method_9717));
		method_10017("scoreboard_slot", class_2239.class, new class_2319(class_2239::method_9468));
		method_10017("score_holder", class_2233.class, new class_2233.class_2236());
		method_10017("swizzle", class_2273.class, new class_2319(class_2273::method_9721));
		method_10017("team", class_2243.class, new class_2319(class_2243::method_9482));
		method_10017("item_slot", class_2240.class, new class_2319(class_2240::method_9473));
		method_10017("resource_location", class_2232.class, new class_2319(class_2232::method_9441));
		method_10017("mob_effect", class_2201.class, new class_2319(class_2201::method_9350));
		method_10017("function", class_2284.class, new class_2319(class_2284::method_9760));
		method_10017("entity_anchor", class_2183.class, new class_2319(class_2183::method_9295));
		method_10017("int_range", class_2224.class_2227.class, new class_2224.class_2227.class_2228());
		method_10017("float_range", class_2224.class_2225.class, new class_2224.class_2225.class_2226());
		method_10017("item_enchantment", class_2194.class, new class_2319(class_2194::method_9336));
		method_10017("entity_summon", class_2188.class, new class_2319(class_2188::method_9324));
		method_10017("dimension", class_2181.class, new class_2319(class_2181::method_9288));
		method_10017("time", class_2245.class, new class_2319(class_2245::method_9489));
	}

	@Nullable
	private static class_2316.class_2317<?> method_10018(class_2960 arg) {
		return (class_2316.class_2317<?>)field_10922.get(arg);
	}

	@Nullable
	private static class_2316.class_2317<?> method_10013(ArgumentType<?> argumentType) {
		return (class_2316.class_2317<?>)field_10921.get(argumentType.getClass());
	}

	public static <T extends ArgumentType<?>> void method_10019(class_2540 arg, T argumentType) {
		class_2316.class_2317<T> lv = (class_2316.class_2317<T>)method_10013(argumentType);
		if (lv == null) {
			field_10923.error("Could not serialize {} ({}) - will not be sent to client!", argumentType, argumentType.getClass());
			arg.method_10812(new class_2960(""));
		} else {
			arg.method_10812(lv.field_10925);
			lv.field_10926.method_10007(argumentType, arg);
		}
	}

	@Nullable
	public static ArgumentType<?> method_10014(class_2540 arg) {
		class_2960 lv = arg.method_10810();
		class_2316.class_2317<?> lv2 = method_10018(lv);
		if (lv2 == null) {
			field_10923.error("Could not deserialize {}", lv);
			return null;
		} else {
			return lv2.field_10926.method_10005(arg);
		}
	}

	private static <T extends ArgumentType<?>> void method_10020(JsonObject jsonObject, T argumentType) {
		class_2316.class_2317<T> lv = (class_2316.class_2317<T>)method_10013(argumentType);
		if (lv == null) {
			field_10923.error("Could not serialize argument {} ({})!", argumentType, argumentType.getClass());
			jsonObject.addProperty("type", "unknown");
		} else {
			jsonObject.addProperty("type", "argument");
			jsonObject.addProperty("parser", lv.field_10925.toString());
			JsonObject jsonObject2 = new JsonObject();
			lv.field_10926.method_10006(argumentType, jsonObject2);
			if (jsonObject2.size() > 0) {
				jsonObject.add("properties", jsonObject2);
			}
		}
	}

	public static <S> JsonObject method_10016(CommandDispatcher<S> commandDispatcher, CommandNode<S> commandNode) {
		JsonObject jsonObject = new JsonObject();
		if (commandNode instanceof RootCommandNode) {
			jsonObject.addProperty("type", "root");
		} else if (commandNode instanceof LiteralCommandNode) {
			jsonObject.addProperty("type", "literal");
		} else if (commandNode instanceof ArgumentCommandNode) {
			method_10020(jsonObject, ((ArgumentCommandNode)commandNode).getType());
		} else {
			field_10923.error("Could not serialize node {} ({})!", commandNode, commandNode.getClass());
			jsonObject.addProperty("type", "unknown");
		}

		JsonObject jsonObject2 = new JsonObject();

		for (CommandNode<S> commandNode2 : commandNode.getChildren()) {
			jsonObject2.add(commandNode2.getName(), method_10016(commandDispatcher, commandNode2));
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

	static class class_2317<T extends ArgumentType<?>> {
		public final Class<T> field_10924;
		public final class_2314<T> field_10926;
		public final class_2960 field_10925;

		private class_2317(Class<T> class_, class_2314<T> arg, class_2960 arg2) {
			this.field_10924 = class_;
			this.field_10926 = arg;
			this.field_10925 = arg2;
		}
	}
}
