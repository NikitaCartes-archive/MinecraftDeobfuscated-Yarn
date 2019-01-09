package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class class_2240 implements ArgumentType<Integer> {
	private static final Collection<String> field_9956 = Arrays.asList("container.5", "12", "weapon");
	private static final DynamicCommandExceptionType field_9955 = new DynamicCommandExceptionType(object -> new class_2588("slot.unknown", object));
	private static final Map<String, Integer> field_9957 = class_156.method_654(Maps.<String, Integer>newHashMap(), hashMap -> {
		for (int i = 0; i < 54; i++) {
			hashMap.put("container." + i, i);
		}

		for (int i = 0; i < 9; i++) {
			hashMap.put("hotbar." + i, i);
		}

		for (int i = 0; i < 27; i++) {
			hashMap.put("inventory." + i, 9 + i);
		}

		for (int i = 0; i < 27; i++) {
			hashMap.put("enderchest." + i, 200 + i);
		}

		for (int i = 0; i < 8; i++) {
			hashMap.put("villager." + i, 300 + i);
		}

		for (int i = 0; i < 15; i++) {
			hashMap.put("horse." + i, 500 + i);
		}

		hashMap.put("weapon", 98);
		hashMap.put("weapon.mainhand", 98);
		hashMap.put("weapon.offhand", 99);
		hashMap.put("armor.head", 100 + class_1304.field_6169.method_5927());
		hashMap.put("armor.chest", 100 + class_1304.field_6174.method_5927());
		hashMap.put("armor.legs", 100 + class_1304.field_6172.method_5927());
		hashMap.put("armor.feet", 100 + class_1304.field_6166.method_5927());
		hashMap.put("horse.saddle", 400);
		hashMap.put("horse.armor", 401);
		hashMap.put("horse.chest", 499);
	});

	public static class_2240 method_9473() {
		return new class_2240();
	}

	public static int method_9469(CommandContext<class_2168> commandContext, String string) {
		return commandContext.<Integer>getArgument(string, Integer.class);
	}

	public Integer method_9470(StringReader stringReader) throws CommandSyntaxException {
		String string = stringReader.readUnquotedString();
		if (!field_9957.containsKey(string)) {
			throw field_9955.create(string);
		} else {
			return (Integer)field_9957.get(string);
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return class_2172.method_9265(field_9957.keySet(), suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return field_9956;
	}
}
