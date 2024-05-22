package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Locale;
import net.minecraft.command.CommandExecutionContext;
import net.minecraft.command.CommandFunctionAction;
import net.minecraft.command.ControlFlowAware;
import net.minecraft.command.ExecutionControl;
import net.minecraft.command.ExecutionFlags;
import net.minecraft.command.Frame;
import net.minecraft.command.ReturnValueConsumer;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.MacroException;
import net.minecraft.server.function.Procedure;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.ProfileResult;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class DebugCommand {
	static final Logger LOGGER = LogUtils.getLogger();
	private static final SimpleCommandExceptionType NOT_RUNNING_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.debug.notRunning"));
	private static final SimpleCommandExceptionType ALREADY_RUNNING_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.debug.alreadyRunning"));
	static final SimpleCommandExceptionType NO_RECURSION_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.debug.function.noRecursion"));
	static final SimpleCommandExceptionType NO_RETURN_RUN_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.debug.function.noReturnRun"));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("debug")
				.requires(source -> source.hasPermissionLevel(3))
				.then(CommandManager.literal("start").executes(context -> executeStart(context.getSource())))
				.then(CommandManager.literal("stop").executes(context -> executeStop(context.getSource())))
				.then(
					CommandManager.literal("function")
						.requires(source -> source.hasPermissionLevel(3))
						.then(
							CommandManager.argument("name", CommandFunctionArgumentType.commandFunction())
								.suggests(FunctionCommand.SUGGESTION_PROVIDER)
								.executes(new DebugCommand.Command())
						)
				)
		);
	}

	private static int executeStart(ServerCommandSource source) throws CommandSyntaxException {
		MinecraftServer minecraftServer = source.getServer();
		if (minecraftServer.isDebugRunning()) {
			throw ALREADY_RUNNING_EXCEPTION.create();
		} else {
			minecraftServer.startDebug();
			source.sendFeedback(() -> Text.translatable("commands.debug.started"), true);
			return 0;
		}
	}

	private static int executeStop(ServerCommandSource source) throws CommandSyntaxException {
		MinecraftServer minecraftServer = source.getServer();
		if (!minecraftServer.isDebugRunning()) {
			throw NOT_RUNNING_EXCEPTION.create();
		} else {
			ProfileResult profileResult = minecraftServer.stopDebug();
			double d = (double)profileResult.getTimeSpan() / (double)TimeHelper.SECOND_IN_NANOS;
			double e = (double)profileResult.getTickSpan() / d;
			source.sendFeedback(
				() -> Text.translatable("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", d), profileResult.getTickSpan(), String.format(Locale.ROOT, "%.2f", e)),
				true
			);
			return (int)e;
		}
	}

	static class Command extends ControlFlowAware.Helper<ServerCommandSource> implements ControlFlowAware.Command<ServerCommandSource> {
		public void executeInner(
			ServerCommandSource serverCommandSource,
			ContextChain<ServerCommandSource> contextChain,
			ExecutionFlags executionFlags,
			ExecutionControl<ServerCommandSource> executionControl
		) throws CommandSyntaxException {
			if (executionFlags.isInsideReturnRun()) {
				throw DebugCommand.NO_RETURN_RUN_EXCEPTION.create();
			} else if (executionControl.getTracer() != null) {
				throw DebugCommand.NO_RECURSION_EXCEPTION.create();
			} else {
				CommandContext<ServerCommandSource> commandContext = contextChain.getTopContext();
				Collection<CommandFunction<ServerCommandSource>> collection = CommandFunctionArgumentType.getFunctions(commandContext, "name");
				MinecraftServer minecraftServer = serverCommandSource.getServer();
				String string = "debug-trace-" + Util.getFormattedCurrentTime() + ".txt";
				CommandDispatcher<ServerCommandSource> commandDispatcher = serverCommandSource.getServer().getCommandFunctionManager().getDispatcher();
				int i = 0;

				try {
					Path path = minecraftServer.getPath("debug");
					Files.createDirectories(path);
					final PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(path.resolve(string), StandardCharsets.UTF_8));
					DebugCommand.Tracer tracer = new DebugCommand.Tracer(printWriter);
					executionControl.setTracer(tracer);

					for (final CommandFunction<ServerCommandSource> commandFunction : collection) {
						try {
							ServerCommandSource serverCommandSource2 = serverCommandSource.withOutput(tracer).withMaxLevel(2);
							Procedure<ServerCommandSource> procedure = commandFunction.withMacroReplaced(null, commandDispatcher);
							executionControl.enqueueAction((new CommandFunctionAction<ServerCommandSource>(procedure, ReturnValueConsumer.EMPTY, false) {
								public void execute(ServerCommandSource serverCommandSource, CommandExecutionContext<ServerCommandSource> commandExecutionContext, Frame frame) {
									printWriter.println(commandFunction.id());
									super.execute(serverCommandSource, commandExecutionContext, frame);
								}
							}).bind(serverCommandSource2));
							i += procedure.entries().size();
						} catch (MacroException var18) {
							serverCommandSource.sendError(var18.getMessage());
						}
					}
				} catch (IOException | UncheckedIOException var19) {
					DebugCommand.LOGGER.warn("Tracing failed", (Throwable)var19);
					serverCommandSource.sendError(Text.translatable("commands.debug.function.traceFailed"));
				}

				int j = i;
				executionControl.enqueueAction(
					(context, frame) -> {
						if (collection.size() == 1) {
							serverCommandSource.sendFeedback(
								() -> Text.translatable("commands.debug.function.success.single", j, Text.of(((CommandFunction)collection.iterator().next()).id()), string), true
							);
						} else {
							serverCommandSource.sendFeedback(() -> Text.translatable("commands.debug.function.success.multiple", j, collection.size(), string), true);
						}
					}
				);
			}
		}
	}

	static class Tracer implements CommandOutput, net.minecraft.server.function.Tracer {
		public static final int MARGIN = 1;
		private final PrintWriter writer;
		private int lastIndentWidth;
		private boolean expectsCommandResult;

		Tracer(PrintWriter writer) {
			this.writer = writer;
		}

		private void writeIndent(int width) {
			this.writeIndentWithoutRememberingWidth(width);
			this.lastIndentWidth = width;
		}

		private void writeIndentWithoutRememberingWidth(int width) {
			for (int i = 0; i < width + 1; i++) {
				this.writer.write("    ");
			}
		}

		private void writeNewLine() {
			if (this.expectsCommandResult) {
				this.writer.println();
				this.expectsCommandResult = false;
			}
		}

		@Override
		public void traceCommandStart(int depth, String command) {
			this.writeNewLine();
			this.writeIndent(depth);
			this.writer.print("[C] ");
			this.writer.print(command);
			this.expectsCommandResult = true;
		}

		@Override
		public void traceCommandEnd(int depth, String command, int result) {
			if (this.expectsCommandResult) {
				this.writer.print(" -> ");
				this.writer.println(result);
				this.expectsCommandResult = false;
			} else {
				this.writeIndent(depth);
				this.writer.print("[R = ");
				this.writer.print(result);
				this.writer.print("] ");
				this.writer.println(command);
			}
		}

		@Override
		public void traceFunctionCall(int depth, Identifier function, int size) {
			this.writeNewLine();
			this.writeIndent(depth);
			this.writer.print("[F] ");
			this.writer.print(function);
			this.writer.print(" size=");
			this.writer.println(size);
		}

		@Override
		public void traceError(String message) {
			this.writeNewLine();
			this.writeIndent(this.lastIndentWidth + 1);
			this.writer.print("[E] ");
			this.writer.print(message);
		}

		@Override
		public void sendMessage(Text message) {
			this.writeNewLine();
			this.writeIndentWithoutRememberingWidth(this.lastIndentWidth + 1);
			this.writer.print("[M] ");
			this.writer.println(message.getString());
		}

		@Override
		public boolean shouldReceiveFeedback() {
			return true;
		}

		@Override
		public boolean shouldTrackOutput() {
			return true;
		}

		@Override
		public boolean shouldBroadcastConsoleToOps() {
			return false;
		}

		@Override
		public boolean cannotBeSilenced() {
			return true;
		}

		@Override
		public void close() {
			IOUtils.closeQuietly(this.writer);
		}
	}
}
