package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import java.io.IOException;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.profiling.jfr.JfrProfileRecorder;
import net.minecraft.util.profiling.jfr.JfrProfiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JfrCommand {
	private static final Logger LOGGER = LogManager.getLogger();

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("jfr")
				.requires(source -> source.hasPermissionLevel(4))
				.then(CommandManager.literal("start").executes(context -> executeStart(context.getSource())))
				.then(CommandManager.literal("stop").executes(context -> executeStop(context.getSource())))
		);
	}

	private static int executeStart(ServerCommandSource source) {
		JfrProfiler.InstanceType instanceType = JfrProfiler.InstanceType.get(source.getServer());
		if (JfrProfiler.start(instanceType)) {
			source.sendFeedback(new TranslatableText("commands.jfr.started"), false);
			return 0;
		} else {
			source.sendError(new TranslatableText("commands.jfr.start.failed"));
			return 1;
		}
	}

	private static int executeStop(ServerCommandSource source) {
		return JfrProfiler.stop().<Integer>map(path -> {
			source.sendFeedback(new TranslatableText("commands.jfr.stopped", path.toString()), false);

			try {
				String string = JfrProfileRecorder.readProfile(path).collect();
				LOGGER.info(string);
			} catch (IOException var3) {
				LOGGER.warn("Failed to collect stats", (Throwable)var3);
			}

			return 0;
		}, exception -> {
			source.sendError(new TranslatableText("commands.jfr.dump.failed", exception.getMessage()));
			return 1;
		});
	}
}
