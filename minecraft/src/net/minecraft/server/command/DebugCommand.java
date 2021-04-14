package net.minecraft.server.command;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.ProfileResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugCommand {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final SimpleCommandExceptionType NOT_RUNNING_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.debug.notRunning"));
	private static final SimpleCommandExceptionType ALREADY_RUNNING_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.debug.alreadyRunning")
	);
	@Nullable
	private static final FileSystemProvider FILE_SYSTEM_PROVIDER = (FileSystemProvider)FileSystemProvider.installedProviders()
		.stream()
		.filter(fileSystemProvider -> fileSystemProvider.getScheme().equalsIgnoreCase("jar"))
		.findFirst()
		.orElse(null);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("debug")
				.requires(source -> source.hasPermissionLevel(3))
				.then(CommandManager.literal("start").executes(context -> executeStart(context.getSource())))
				.then(CommandManager.literal("stop").executes(context -> executeStop(context.getSource())))
				.then(CommandManager.literal("report").executes(context -> createDebugReport(context.getSource())))
				.then(
					CommandManager.literal("function")
						.then(
							CommandManager.argument("name", CommandFunctionArgumentType.commandFunction())
								.suggests(FunctionCommand.SUGGESTION_PROVIDER)
								.executes(commandContext -> method_36354(commandContext.getSource(), CommandFunctionArgumentType.getFunctions(commandContext, "name")))
						)
				)
		);
	}

	private static int executeStart(ServerCommandSource source) throws CommandSyntaxException {
		MinecraftServer minecraftServer = source.getMinecraftServer();
		if (minecraftServer.isDebugRunning()) {
			throw ALREADY_RUNNING_EXCEPTION.create();
		} else {
			minecraftServer.enableProfiler();
			source.sendFeedback(new TranslatableText("commands.debug.started", "Started the debug profiler. Type '/debug stop' to stop it."), true);
			return 0;
		}
	}

	private static int executeStop(ServerCommandSource source) throws CommandSyntaxException {
		MinecraftServer minecraftServer = source.getMinecraftServer();
		if (!minecraftServer.isDebugRunning()) {
			throw NOT_RUNNING_EXCEPTION.create();
		} else {
			ProfileResult profileResult = minecraftServer.stopDebug();
			File file = new File(minecraftServer.getFile("debug"), "profile-results-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt");
			profileResult.save(file.toPath());
			float f = (float)profileResult.getTimeSpan() / 1.0E9F;
			float g = (float)profileResult.getTickSpan() / f;
			source.sendFeedback(
				new TranslatableText("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", f), profileResult.getTickSpan(), String.format("%.2f", g)), true
			);
			return MathHelper.floor(g);
		}
	}

	private static int createDebugReport(ServerCommandSource source) {
		MinecraftServer minecraftServer = source.getMinecraftServer();
		String string = "debug-report-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());

		try {
			Path path = minecraftServer.getFile("debug").toPath();
			Files.createDirectories(path);
			if (!SharedConstants.isDevelopment && FILE_SYSTEM_PROVIDER != null) {
				Path path2 = path.resolve(string + ".zip");
				FileSystem fileSystem = FILE_SYSTEM_PROVIDER.newFileSystem(path2, ImmutableMap.of("create", "true"));
				Throwable var6 = null;

				try {
					minecraftServer.dump(fileSystem.getPath("/"));
				} catch (Throwable var16) {
					var6 = var16;
					throw var16;
				} finally {
					if (fileSystem != null) {
						if (var6 != null) {
							try {
								fileSystem.close();
							} catch (Throwable var15) {
								var6.addSuppressed(var15);
							}
						} else {
							fileSystem.close();
						}
					}
				}
			} else {
				Path path2 = path.resolve(string);
				minecraftServer.dump(path2);
			}

			source.sendFeedback(new TranslatableText("commands.debug.reportSaved", string), false);
			return 1;
		} catch (IOException var18) {
			LOGGER.error("Failed to save debug dump", (Throwable)var18);
			source.sendError(new TranslatableText("commands.debug.reportFailed"));
			return 0;
		}
	}

	private static int method_36354(ServerCommandSource serverCommandSource, Collection<CommandFunction> collection) {
		int i = 0;
		MinecraftServer minecraftServer = serverCommandSource.getMinecraftServer();
		String string = "debug-trace-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt";

		try {
			Path path = minecraftServer.getFile("debug").toPath();
			Files.createDirectories(path);
			Writer writer = Files.newBufferedWriter(path.resolve(string), StandardCharsets.UTF_8);
			Throwable var7 = null;

			try {
				PrintWriter printWriter = new PrintWriter(writer);

				for (CommandFunction commandFunction : collection) {
					printWriter.println(commandFunction.getId());
					DebugCommand.class_6347 lv = new DebugCommand.class_6347(printWriter);
					i += serverCommandSource.getMinecraftServer()
						.getCommandFunctionManager()
						.method_36341(commandFunction, serverCommandSource.method_36321(lv).withMaxLevel(2), lv);
				}
			} catch (Throwable var20) {
				var7 = var20;
				throw var20;
			} finally {
				if (writer != null) {
					if (var7 != null) {
						try {
							writer.close();
						} catch (Throwable var19) {
							var7.addSuppressed(var19);
						}
					} else {
						writer.close();
					}
				}
			}
		} catch (IOException | UncheckedIOException var22) {
			LOGGER.warn("Tracing failed", (Throwable)var22);
			serverCommandSource.sendError(new TranslatableText("commands.debug.function.traceFailed"));
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.debug.function.success.single", i, ((CommandFunction)collection.iterator().next()).getId(), string), true
			);
		} else {
			serverCommandSource.sendFeedback(new TranslatableText("commands.debug.function.success.multiple", i, collection.size(), string), true);
		}

		return i;
	}

	static class class_6347 implements CommandOutput, CommandFunctionManager.class_6346 {
		public static final int field_33550 = 1;
		private final PrintWriter field_33551;
		private int field_33552;
		private boolean field_33553;

		private class_6347(PrintWriter printWriter) {
			this.field_33551 = printWriter;
		}

		private void method_36355(int i) {
			this.method_36356(i);
			this.field_33552 = i;
		}

		private void method_36356(int i) {
			for (int j = 0; j < i + 1; j++) {
				this.field_33551.write("    ");
			}
		}

		private void method_36357() {
			if (this.field_33553) {
				this.field_33551.println();
				this.field_33553 = false;
			}
		}

		@Override
		public void method_36349(int i, String string) {
			this.method_36357();
			this.method_36355(i);
			this.field_33551.print("[C] ");
			this.field_33551.print(string);
			this.field_33553 = true;
		}

		@Override
		public void method_36350(int i, String string, int j) {
			if (this.field_33553) {
				this.field_33551.print(" -> ");
				this.field_33551.println(j);
				this.field_33553 = false;
			} else {
				this.method_36355(i);
				this.field_33551.print("[R = ");
				this.field_33551.print(j);
				this.field_33551.print("] ");
				this.field_33551.println(string);
			}
		}

		@Override
		public void method_36351(int i, Identifier identifier, int j) {
			this.method_36357();
			this.method_36355(i);
			this.field_33551.print("[F] ");
			this.field_33551.print(identifier);
			this.field_33551.print(" size=");
			this.field_33551.println(j);
		}

		@Override
		public void method_36352(int i, String string) {
			this.method_36357();
			this.method_36355(i + 1);
			this.field_33551.print("[E] ");
			this.field_33551.print(string);
		}

		@Override
		public void sendSystemMessage(Text message, UUID sender) {
			this.method_36357();
			this.method_36356(this.field_33552 + 1);
			this.field_33551.print("[M] ");
			if (sender != Util.NIL_UUID) {
				this.field_33551.print(sender);
				this.field_33551.print(": ");
			}

			this.field_33551.println(message.getString());
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
		public boolean method_36320() {
			return true;
		}
	}
}
