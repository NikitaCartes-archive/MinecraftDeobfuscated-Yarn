package net.minecraft.command.argument;

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
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class ItemSlotArgumentType implements ArgumentType<Integer> {
	private static final Collection<String> EXAMPLES = Arrays.asList("container.5", "12", "weapon");
	private static final DynamicCommandExceptionType UNKNOWN_SLOT_EXCEPTION = new DynamicCommandExceptionType(name -> Text.translatable("slot.unknown", name));
	private static final Map<String, Integer> SLOT_NAMES_TO_SLOT_COMMAND_ID = Util.make(Maps.<String, Integer>newHashMap(), map -> {
		for (int i = 0; i < 54; i++) {
			map.put("container." + i, i);
		}

		for (int i = 0; i < 9; i++) {
			map.put("hotbar." + i, i);
		}

		for (int i = 0; i < 27; i++) {
			map.put("inventory." + i, 9 + i);
		}

		for (int i = 0; i < 27; i++) {
			map.put("enderchest." + i, 200 + i);
		}

		for (int i = 0; i < 8; i++) {
			map.put("villager." + i, 300 + i);
		}

		for (int i = 0; i < 15; i++) {
			map.put("horse." + i, 500 + i);
		}

		map.put("weapon", EquipmentSlot.MAINHAND.getOffsetEntitySlotId(98));
		map.put("weapon.mainhand", EquipmentSlot.MAINHAND.getOffsetEntitySlotId(98));
		map.put("weapon.offhand", EquipmentSlot.OFFHAND.getOffsetEntitySlotId(98));
		map.put("armor.head", EquipmentSlot.HEAD.getOffsetEntitySlotId(100));
		map.put("armor.chest", EquipmentSlot.CHEST.getOffsetEntitySlotId(100));
		map.put("armor.legs", EquipmentSlot.LEGS.getOffsetEntitySlotId(100));
		map.put("armor.feet", EquipmentSlot.FEET.getOffsetEntitySlotId(100));
		map.put("horse.saddle", 400);
		map.put("horse.armor", 401);
		map.put("horse.chest", 499);
	});

	public static ItemSlotArgumentType itemSlot() {
		return new ItemSlotArgumentType();
	}

	public static int getItemSlot(CommandContext<ServerCommandSource> context, String name) {
		return context.<Integer>getArgument(name, Integer.class);
	}

	public Integer parse(StringReader stringReader) throws CommandSyntaxException {
		String string = stringReader.readUnquotedString();
		if (!SLOT_NAMES_TO_SLOT_COMMAND_ID.containsKey(string)) {
			throw UNKNOWN_SLOT_EXCEPTION.create(string);
		} else {
			return (Integer)SLOT_NAMES_TO_SLOT_COMMAND_ID.get(string);
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestMatching(SLOT_NAMES_TO_SLOT_COMMAND_ID.keySet(), builder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
