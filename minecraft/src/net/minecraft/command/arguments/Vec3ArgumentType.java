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
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.Vec3d;

public class Vec3ArgumentType implements ArgumentType<PosArgument> {
	private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "0.1 -0.5 .9", "~0.5 ~1 ~-5");
	public static final SimpleCommandExceptionType INCOMPLETE_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("argument.pos3d.incomplete")
	);
	public static final SimpleCommandExceptionType MIXED_COORDINATE_EXCEPTION = new SimpleCommandExceptionType(new TranslatableTextComponent("argument.pos.mixed"));
	private final boolean centerIntegers;

	public Vec3ArgumentType(boolean bl) {
		this.centerIntegers = bl;
	}

	public static Vec3ArgumentType create() {
		return new Vec3ArgumentType(true);
	}

	public static Vec3ArgumentType create(boolean bl) {
		return new Vec3ArgumentType(bl);
	}

	public static Vec3d getVec3(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<PosArgument>getArgument(string, PosArgument.class).toAbsolutePos(commandContext.getSource());
	}

	public static PosArgument getPosArgument(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.getArgument(string, PosArgument.class);
	}

	public PosArgument method_9738(StringReader stringReader) throws CommandSyntaxException {
		return (PosArgument)(stringReader.canRead() && stringReader.peek() == '^'
			? LookingPosArgument.parse(stringReader)
			: DefaultPosArgument.parse(stringReader, this.centerIntegers));
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
				collection = ((CommandSource)commandContext.getSource()).getPositionSuggestions();
			}

			return CommandSource.suggestPositions(string, collection, suggestionsBuilder, CommandManager.getCommandValidator(this::method_9738));
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
