package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.tag.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class FunctionArgumentType implements ArgumentType<FunctionArgumentType.FunctionArgument> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "#foo");
	private static final DynamicCommandExceptionType UNKNOWN_FUNCTION_TAG_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("arguments.function.tag.unknown", object)
	);
	private static final DynamicCommandExceptionType UNKNOWN_FUNCTION_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("arguments.function.unknown", object)
	);

	public static FunctionArgumentType function() {
		return new FunctionArgumentType();
	}

	public FunctionArgumentType.FunctionArgument method_9764(StringReader stringReader) throws CommandSyntaxException {
		if (stringReader.canRead() && stringReader.peek() == '#') {
			stringReader.skip();
			final Identifier identifier = Identifier.fromCommandInput(stringReader);
			return new FunctionArgumentType.FunctionArgument() {
				@Override
				public Collection<CommandFunction> getFunctions(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
					Tag<CommandFunction> tag = FunctionArgumentType.getFunctionTag(commandContext, identifier);
					return tag.values();
				}

				@Override
				public Either<CommandFunction, Tag<CommandFunction>> getFunctionOrTag(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
					return Either.right(FunctionArgumentType.getFunctionTag(commandContext, identifier));
				}
			};
		} else {
			final Identifier identifier = Identifier.fromCommandInput(stringReader);
			return new FunctionArgumentType.FunctionArgument() {
				@Override
				public Collection<CommandFunction> getFunctions(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
					return Collections.singleton(FunctionArgumentType.getFunction(commandContext, identifier));
				}

				@Override
				public Either<CommandFunction, Tag<CommandFunction>> getFunctionOrTag(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
					return Either.left(FunctionArgumentType.getFunction(commandContext, identifier));
				}
			};
		}
	}

	private static CommandFunction getFunction(CommandContext<ServerCommandSource> commandContext, Identifier identifier) throws CommandSyntaxException {
		return (CommandFunction)commandContext.getSource()
			.getMinecraftServer()
			.getCommandFunctionManager()
			.getFunction(identifier)
			.orElseThrow(() -> UNKNOWN_FUNCTION_EXCEPTION.create(identifier.toString()));
	}

	private static Tag<CommandFunction> getFunctionTag(CommandContext<ServerCommandSource> commandContext, Identifier identifier) throws CommandSyntaxException {
		Tag<CommandFunction> tag = commandContext.getSource().getMinecraftServer().getCommandFunctionManager().getTags().get(identifier);
		if (tag == null) {
			throw UNKNOWN_FUNCTION_TAG_EXCEPTION.create(identifier.toString());
		} else {
			return tag;
		}
	}

	public static Collection<CommandFunction> getFunctions(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<FunctionArgumentType.FunctionArgument>getArgument(string, FunctionArgumentType.FunctionArgument.class).getFunctions(commandContext);
	}

	public static Either<CommandFunction, Tag<CommandFunction>> getFunctionOrTag(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<FunctionArgumentType.FunctionArgument>getArgument(string, FunctionArgumentType.FunctionArgument.class)
			.getFunctionOrTag(commandContext);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public interface FunctionArgument {
		Collection<CommandFunction> getFunctions(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException;

		Either<CommandFunction, Tag<CommandFunction>> getFunctionOrTag(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException;
	}
}
