package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.inventory.SlotRange;
import net.minecraft.inventory.SlotRanges;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.JsonReaderUtils;

public class SlotRangeArgumentType implements ArgumentType<SlotRange> {
	private static final Collection<String> EXAMPLES = List.of("container.*", "container.5", "weapon");
	private static final DynamicCommandExceptionType UNKNOWN_SLOT_EXCEPTION = new DynamicCommandExceptionType(
		slotRange -> Text.stringifiedTranslatable("slot.unknown", slotRange)
	);

	public static SlotRangeArgumentType slotRange() {
		return new SlotRangeArgumentType();
	}

	public static SlotRange getSlotRange(CommandContext<ServerCommandSource> context, String name) {
		return context.getArgument(name, SlotRange.class);
	}

	public SlotRange parse(StringReader stringReader) throws CommandSyntaxException {
		String string = JsonReaderUtils.readWhileMatching(stringReader, c -> c != ' ');
		SlotRange slotRange = SlotRanges.fromName(string);
		if (slotRange == null) {
			throw UNKNOWN_SLOT_EXCEPTION.createWithContext(stringReader, string);
		} else {
			return slotRange;
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder suggestionsBuilder) {
		return CommandSource.suggestMatching(SlotRanges.streamNames(), suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
