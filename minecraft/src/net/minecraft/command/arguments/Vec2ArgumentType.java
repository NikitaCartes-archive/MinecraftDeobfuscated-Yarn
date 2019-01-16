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
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class Vec2ArgumentType implements ArgumentType<PosArgument> {
	private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "0.1 -0.5", "~1 ~-2");
	public static final SimpleCommandExceptionType INCOMPLETE_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("argument.pos2d.incomplete")
	);
	private final boolean field_10744;

	public Vec2ArgumentType(boolean bl) {
		this.field_10744 = bl;
	}

	public static Vec2ArgumentType create() {
		return new Vec2ArgumentType(true);
	}

	public static Vec2f getVec2Argument(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		Vec3d vec3d = commandContext.<PosArgument>getArgument(string, PosArgument.class).toAbsolutePos(commandContext.getSource());
		return new Vec2f((float)vec3d.x, (float)vec3d.z);
	}

	public PosArgument method_9725(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();
		if (!stringReader.canRead()) {
			throw INCOMPLETE_EXCEPTION.createWithContext(stringReader);
		} else {
			CoordinateArgument coordinateArgument = CoordinateArgument.parse(stringReader, this.field_10744);
			if (stringReader.canRead() && stringReader.peek() == ' ') {
				stringReader.skip();
				CoordinateArgument coordinateArgument2 = CoordinateArgument.parse(stringReader, this.field_10744);
				return new DefaultPosArgument(coordinateArgument, new CoordinateArgument(true, 0.0), coordinateArgument2);
			} else {
				stringReader.setCursor(i);
				throw INCOMPLETE_EXCEPTION.createWithContext(stringReader);
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
				collection = ((CommandSource)commandContext.getSource()).method_17772();
			}

			return CommandSource.method_9252(string, collection, suggestionsBuilder, ServerCommandManager.getCommandValidator(this::method_9725));
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
