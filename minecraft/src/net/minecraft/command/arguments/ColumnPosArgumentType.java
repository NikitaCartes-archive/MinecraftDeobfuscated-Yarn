package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_2267;
import net.minecraft.class_2278;
import net.minecraft.class_2280;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.BlockPos;

public class ColumnPosArgumentType implements ArgumentType<class_2267> {
	private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "~1 ~-2", "^ ^", "^-1 ^0");
	public static final SimpleCommandExceptionType field_10706 = new SimpleCommandExceptionType(new TranslatableTextComponent("argument.pos2d.incomplete"));

	public static ColumnPosArgumentType create() {
		return new ColumnPosArgumentType();
	}

	public static ColumnPosArgumentType.class_2265 method_9702(CommandContext<ServerCommandSource> commandContext, String string) {
		BlockPos blockPos = commandContext.<class_2267>getArgument(string, class_2267.class).method_9704(commandContext.getSource());
		return new ColumnPosArgumentType.class_2265(blockPos.getX(), blockPos.getZ());
	}

	public class_2267 method_9703(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();
		if (!stringReader.canRead()) {
			throw field_10706.createWithContext(stringReader);
		} else {
			class_2278 lv = class_2278.method_9739(stringReader);
			if (stringReader.canRead() && stringReader.peek() == ' ') {
				stringReader.skip();
				class_2278 lv2 = class_2278.method_9739(stringReader);
				return new class_2280(lv, new class_2278(true, 0.0), lv2);
			} else {
				stringReader.setCursor(i);
				throw field_10706.createWithContext(stringReader);
			}
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		if (!(commandContext.getSource() instanceof CommandSource)) {
			return Suggestions.empty();
		} else {
			String string = suggestionsBuilder.getRemaining();
			Collection<CommandSource.RelativePosition> collection;
			if (!string.isEmpty() && string.charAt(0) == '^') {
				collection = Collections.singleton(CommandSource.RelativePosition.ZERO_LOCAL);
			} else {
				collection = ((CommandSource)commandContext.getSource()).method_9274(false);
			}

			return CommandSource.method_9252(string, collection, suggestionsBuilder, ServerCommandManager.getCommandValidator(this::method_9703));
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public static class class_2265 {
		public final int field_10708;
		public final int field_10707;

		public class_2265(int i, int j) {
			this.field_10708 = i;
			this.field_10707 = j;
		}

		public String toString() {
			return "[" + this.field_10708 + ", " + this.field_10707 + "]";
		}
	}
}
