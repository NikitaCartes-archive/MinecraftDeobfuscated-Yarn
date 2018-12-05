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
import net.minecraft.class_2268;
import net.minecraft.class_2280;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.Vec3d;

public class Vec3ArgumentType implements ArgumentType<class_2267> {
	private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "0.1 -0.5 .9", "~0.5 ~1 ~-5");
	public static final SimpleCommandExceptionType INCOMPLETE_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("argument.pos3d.incomplete")
	);
	public static final SimpleCommandExceptionType MIXED_COORDINATE_EXCEPTION = new SimpleCommandExceptionType(new TranslatableTextComponent("argument.pos.mixed"));
	private final boolean field_10756;

	public Vec3ArgumentType(boolean bl) {
		this.field_10756 = bl;
	}

	public static Vec3ArgumentType create() {
		return new Vec3ArgumentType(true);
	}

	public static Vec3ArgumentType create(boolean bl) {
		return new Vec3ArgumentType(bl);
	}

	public static Vec3d getVec3Argument(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<class_2267>getArgument(string, class_2267.class).method_9708(commandContext.getSource());
	}

	public static class_2267 method_9734(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.getArgument(string, class_2267.class);
	}

	public class_2267 method_9738(StringReader stringReader) throws CommandSyntaxException {
		return (class_2267)(stringReader.canRead() && stringReader.peek() == '^'
			? class_2268.method_9711(stringReader)
			: class_2280.method_9750(stringReader, this.field_10756));
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
				collection = ((CommandSource)commandContext.getSource()).method_9274(true);
			}

			return CommandSource.method_9260(string, collection, suggestionsBuilder, ServerCommandManager.getCommandValidator(this::method_9738));
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
