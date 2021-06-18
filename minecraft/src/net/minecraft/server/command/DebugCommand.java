package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.ProfileResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugCommand {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final SimpleCommandExceptionType NOT_RUNNING_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.debug.notRunning"));
	private static final SimpleCommandExceptionType ALREADY_RUNNING_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.debug.alreadyRunning")
	);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("debug")
				.requires(source -> source.hasPermissionLevel(3))
				.then(CommandManager.literal("start").executes(context -> executeStart(context.getSource())))
				.then(CommandManager.literal("stop").executes(context -> executeStop(context.getSource())))
				.then(
					CommandManager.literal("function")
						.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))
						.then(
							CommandManager.argument("name", CommandFunctionArgumentType.commandFunction())
								.suggests(FunctionCommand.SUGGESTION_PROVIDER)
								.executes(context -> executeFunction(context.getSource(), CommandFunctionArgumentType.getFunctions(context, "name")))
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
			source.sendFeedback(new TranslatableText("commands.debug.started"), true);
			return 0;
		}
	}

	private static int executeStop(ServerCommandSource source) throws CommandSyntaxException {
		MinecraftServer minecraftServer = source.getServer();
		if (!minecraftServer.isDebugRunning()) {
			throw NOT_RUNNING_EXCEPTION.create();
		} else {
			ProfileResult profileResult = minecraftServer.stopDebug();
			double d = (double)profileResult.getTimeSpan() / (double)TimeHelper.SECOND_IN_MILLIS;
			double e = (double)profileResult.getTickSpan() / d;
			source.sendFeedback(
				new TranslatableText("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", d), profileResult.getTickSpan(), String.format("%.2f", e)), true
			);
			return (int)e;
		}
	}

	private static int executeFunction(ServerCommandSource source, Collection<CommandFunction> functions) {
		int i = 0;
		MinecraftServer minecraftServer = source.getServer();
		String string = "debug-trace-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt";

		try {
			Path path = minecraftServer.getFile("debug").toPath();
			Files.createDirectories(path);
			Writer writer = Files.newBufferedWriter(path.resolve(string), StandardCharsets.UTF_8);

			try {
				PrintWriter printWriter = new PrintWriter(writer);

				for (CommandFunction commandFunction : functions) {
					printWriter.println(commandFunction.getId());
					DebugCommand.Tracer tracer = new DebugCommand.Tracer(printWriter);
					i += source.getServer().getCommandFunctionManager().execute(commandFunction, source.withOutput(tracer).withMaxLevel(2), tracer);
				}
			} catch (Throwable var12) {
				if (writer != null) {
					try {
						writer.close();
					} catch (Throwable var11) {
						var12.addSuppressed(var11);
					}
				}

				throw var12;
			}

			if (writer != null) {
				writer.close();
			}
		} catch (IOException | UncheckedIOException var13) {
			LOGGER.warn("Tracing failed", (Throwable)var13);
			source.sendError(new TranslatableText("commands.debug.function.traceFailed"));
		}

		if (functions.size() == 1) {
			source.sendFeedback(new TranslatableText("commands.debug.function.success.single", i, ((CommandFunction)functions.iterator().next()).getId(), string), true);
		} else {
			source.sendFeedback(new TranslatableText("commands.debug.function.success.multiple", i, functions.size(), string), true);
		}

		return i;
	}

	static class Tracer implements CommandOutput, CommandFunctionManager.Tracer {
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
		public void traceError(int depth, String message) {
			this.writeNewLine();
			this.writeIndent(depth + 1);
			this.writer.print("[E] ");
			this.writer.print(message);
		}

		@Override
		public void sendSystemMessage(Text message, UUID sender) {
			this.writeNewLine();
			this.writeIndentWithoutRememberingWidth(this.lastIndentWidth + 1);
			this.writer.print("[M] ");
			if (sender != Util.NIL_UUID) {
				this.writer.print(sender);
				this.writer.print(": ");
			}

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
	}
}
