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
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;

public class FunctionArgumentType implements ArgumentType<FunctionArgumentType.class_2285> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "#foo");
	private static final DynamicCommandExceptionType field_10782 = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("arguments.function.tag.unknown", object)
	);
	private static final DynamicCommandExceptionType field_10784 = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("arguments.function.unknown", object)
	);

	public static FunctionArgumentType create() {
		return new FunctionArgumentType();
	}

	public FunctionArgumentType.class_2285 method_9764(StringReader stringReader) throws CommandSyntaxException {
		if (stringReader.canRead() && stringReader.peek() == '#') {
			stringReader.skip();
			final Identifier identifier = Identifier.parse(stringReader);
			return new FunctionArgumentType.class_2285() {
				@Override
				public Collection<CommandFunction> method_9771(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
					Tag<CommandFunction> tag = FunctionArgumentType.method_9767(commandContext, identifier);
					return tag.values();
				}

				@Override
				public Either<CommandFunction, Tag<CommandFunction>> method_9770(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
					return Either.right(FunctionArgumentType.method_9767(commandContext, identifier));
				}
			};
		} else {
			final Identifier identifier = Identifier.parse(stringReader);
			return new FunctionArgumentType.class_2285() {
				@Override
				public Collection<CommandFunction> method_9771(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
					return Collections.singleton(FunctionArgumentType.method_9761(commandContext, identifier));
				}

				@Override
				public Either<CommandFunction, Tag<CommandFunction>> method_9770(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
					return Either.left(FunctionArgumentType.method_9761(commandContext, identifier));
				}
			};
		}
	}

	private static CommandFunction method_9761(CommandContext<ServerCommandSource> commandContext, Identifier identifier) throws CommandSyntaxException {
		CommandFunction commandFunction = commandContext.getSource().getMinecraftServer().getCommandFunctionManager().getFunction(identifier);
		if (commandFunction == null) {
			throw field_10784.create(identifier.toString());
		} else {
			return commandFunction;
		}
	}

	private static Tag<CommandFunction> method_9767(CommandContext<ServerCommandSource> commandContext, Identifier identifier) throws CommandSyntaxException {
		Tag<CommandFunction> tag = commandContext.getSource().getMinecraftServer().getCommandFunctionManager().getTags().get(identifier);
		if (tag == null) {
			throw field_10782.create(identifier.toString());
		} else {
			return tag;
		}
	}

	public static Collection<CommandFunction> method_9769(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<FunctionArgumentType.class_2285>getArgument(string, FunctionArgumentType.class_2285.class).method_9771(commandContext);
	}

	public static Either<CommandFunction, Tag<CommandFunction>> method_9768(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		return commandContext.<FunctionArgumentType.class_2285>getArgument(string, FunctionArgumentType.class_2285.class).method_9770(commandContext);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public interface class_2285 {
		Collection<CommandFunction> method_9771(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException;

		Either<CommandFunction, Tag<CommandFunction>> method_9770(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException;
	}
}
