package net.minecraft.command.arguments;

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
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.SystemUtil;

public class ItemSlotArgumentType implements ArgumentType<Integer> {
	private static final Collection<String> EXAMPLES = Arrays.asList("container.5", "12", "weapon");
	private static final DynamicCommandExceptionType UNKNOWN_SLOT_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("slot.unknown", object)
	);
	private static final Map<String, Integer> slotNamesToSlotCommandId = SystemUtil.consume(Maps.<String, Integer>newHashMap(), hashMap -> {
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
		hashMap.put("armor.head", 100 + EquipmentSlot.field_6169.getEntitySlotId());
		hashMap.put("armor.chest", 100 + EquipmentSlot.field_6174.getEntitySlotId());
		hashMap.put("armor.legs", 100 + EquipmentSlot.field_6172.getEntitySlotId());
		hashMap.put("armor.feet", 100 + EquipmentSlot.field_6166.getEntitySlotId());
		hashMap.put("horse.saddle", 400);
		hashMap.put("horse.armor", 401);
		hashMap.put("horse.chest", 499);
	});

	public static ItemSlotArgumentType itemSlot() {
		return new ItemSlotArgumentType();
	}

	public static int getItemSlot(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.<Integer>getArgument(string, Integer.class);
	}

	public Integer method_9470(StringReader stringReader) throws CommandSyntaxException {
		String string = stringReader.readUnquotedString();
		if (!slotNamesToSlotCommandId.containsKey(string)) {
			throw UNKNOWN_SLOT_EXCEPTION.create(string);
		} else {
			return (Integer)slotNamesToSlotCommandId.get(string);
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return CommandSource.suggestMatching(slotNamesToSlotCommandId.keySet(), suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
