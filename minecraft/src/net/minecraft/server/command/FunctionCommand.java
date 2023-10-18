package net.minecraft.server.command;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.command.CommandFunctionAction;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ControlFlowAware;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.ExecutionControl;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.server.function.MacroException;
import net.minecraft.server.function.Procedure;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.mutable.MutableInt;

public class FunctionCommand {
	private static final DynamicCommandExceptionType ARGUMENT_NOT_COMPOUND_EXCEPTION = new DynamicCommandExceptionType(
		argument -> Text.stringifiedTranslatable("commands.function.error.argument_not_compound", argument)
	);
	static final DynamicCommandExceptionType NO_FUNCTIONS_EXCEPTION = new DynamicCommandExceptionType(
		argument -> Text.stringifiedTranslatable("commands.function.scheduled.no_functions", argument)
	);
	@VisibleForTesting
	public static final Dynamic2CommandExceptionType INSTANTIATION_FAILURE_EXCEPTION = new Dynamic2CommandExceptionType(
		(argument, argument2) -> Text.stringifiedTranslatable("commands.function.instantiationFailure", argument, argument2)
	);
	public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> {
		CommandFunctionManager commandFunctionManager = context.getSource().getServer().getCommandFunctionManager();
		CommandSource.suggestIdentifiers(commandFunctionManager.getFunctionTags(), builder, "#");
		return CommandSource.suggestIdentifiers(commandFunctionManager.getAllFunctions(), builder);
	};
	static final FunctionCommand.ResultConsumer<ServerCommandSource> RESULT_REPORTER = new FunctionCommand.ResultConsumer<ServerCommandSource>() {
		public void accept(ServerCommandSource serverCommandSource, Identifier identifier, int i) {
			serverCommandSource.sendFeedback(() -> Text.translatable("commands.function.result", Text.of(identifier), i), true);
		}
	};

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("with");

		for (DataCommand.ObjectType objectType : DataCommand.SOURCE_OBJECT_TYPES) {
			objectType.addArgumentsToBuilder(literalArgumentBuilder, builder -> builder.executes(new FunctionCommand.Command() {
					@Override
					protected NbtCompound getArguments(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
						return objectType.getObject(context).getNbt();
					}
				}).then(CommandManager.argument("path", NbtPathArgumentType.nbtPath()).executes(new FunctionCommand.Command() {
					@Override
					protected NbtCompound getArguments(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
						return FunctionCommand.getArgument(NbtPathArgumentType.getNbtPath(context, "path"), objectType.getObject(context));
					}
				})));
		}

		dispatcher.register(
			CommandManager.literal("function")
				.requires(source -> source.hasPermissionLevel(2))
				.then(CommandManager.argument("name", CommandFunctionArgumentType.commandFunction()).suggests(SUGGESTION_PROVIDER).executes(new FunctionCommand.Command() {
					@Nullable
					@Override
					protected NbtCompound getArguments(CommandContext<ServerCommandSource> context) {
						return null;
					}
				}).then(CommandManager.argument("arguments", NbtCompoundArgumentType.nbtCompound()).executes(new FunctionCommand.Command() {
					@Override
					protected NbtCompound getArguments(CommandContext<ServerCommandSource> context) {
						return NbtCompoundArgumentType.getNbtCompound(context, "arguments");
					}
				})).then(literalArgumentBuilder))
		);
	}

	static NbtCompound getArgument(NbtPathArgumentType.NbtPath path, DataCommandObject object) throws CommandSyntaxException {
		NbtElement nbtElement = DataCommand.getNbt(path, object);
		if (nbtElement instanceof NbtCompound) {
			return (NbtCompound)nbtElement;
		} else {
			throw ARGUMENT_NOT_COMPOUND_EXCEPTION.create(nbtElement.getNbtType().getCrashReportName());
		}
	}

	public static ServerCommandSource createFunctionCommandSource(ServerCommandSource source) {
		return source.withSilent().withMaxLevel(2);
	}

	public static <T extends AbstractServerCommandSource<T>> void enqueueAction(
		Collection<CommandFunction<T>> commandFunctions,
		@Nullable NbtCompound args,
		T source,
		T functionSource,
		ExecutionControl<T> control,
		FunctionCommand.ResultConsumer<T> resultConsumer
	) throws CommandSyntaxException {
		CommandDispatcher<T> commandDispatcher = source.getDispatcher();
		MutableInt mutableInt = new MutableInt();

		for (CommandFunction<T> commandFunction : commandFunctions) {
			Identifier identifier = commandFunction.id();

			try {
				T abstractServerCommandSource = functionSource.withDummyResultStorer().withReturnValueConsumer(result -> {
					int i = mutableInt.addAndGet(result);
					resultConsumer.accept(source, identifier, i);
					source.consumeResult(true, i);
				});
				Procedure<T> procedure = commandFunction.withMacroReplaced(args, commandDispatcher, abstractServerCommandSource);
				control.enqueueAction(new CommandFunctionAction<>(procedure).bind(abstractServerCommandSource));
			} catch (MacroException var13) {
				throw INSTANTIATION_FAILURE_EXCEPTION.create(identifier, var13.getMessage());
			}
		}
	}

	abstract static class Command extends ControlFlowAware.Helper<ServerCommandSource> implements ControlFlowAware.Command<ServerCommandSource> {
		@Nullable
		protected abstract NbtCompound getArguments(CommandContext<ServerCommandSource> context) throws CommandSyntaxException;

		public void executeInner(
			ServerCommandSource serverCommandSource, ContextChain<ServerCommandSource> contextChain, boolean bl, ExecutionControl<ServerCommandSource> executionControl
		) throws CommandSyntaxException {
			CommandContext<ServerCommandSource> commandContext = contextChain.getTopContext().copyFor(serverCommandSource);
			Pair<Identifier, Collection<CommandFunction<ServerCommandSource>>> pair = CommandFunctionArgumentType.getFunctionOrTag(commandContext, "name")
				.mapSecond(functionOrTag -> functionOrTag.map(Collections::singleton, Function.identity()));
			Collection<CommandFunction<ServerCommandSource>> collection = pair.getSecond();
			if (collection.isEmpty()) {
				throw FunctionCommand.NO_FUNCTIONS_EXCEPTION.create(Text.of(pair.getFirst()));
			} else {
				NbtCompound nbtCompound = this.getArguments(commandContext);
				ServerCommandSource serverCommandSource2 = FunctionCommand.createFunctionCommandSource(serverCommandSource);
				if (collection.size() == 1) {
					serverCommandSource.sendFeedback(
						() -> Text.translatable("commands.function.scheduled.single", Text.of(((CommandFunction)collection.iterator().next()).id())), true
					);
				} else {
					serverCommandSource.sendFeedback(
						() -> Text.translatable("commands.function.scheduled.multiple", Texts.join(collection.stream().map(CommandFunction::id).toList(), Text::of)), true
					);
				}

				FunctionCommand.enqueueAction(collection, nbtCompound, serverCommandSource, serverCommandSource2, executionControl, FunctionCommand.RESULT_REPORTER);
			}
		}
	}

	public interface ResultConsumer<T> {
		void accept(T source, Identifier id, int result);
	}
}
