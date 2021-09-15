package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.nio.file.Path;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.profiling.jfr.JfrProfiler;

public class JfrCommand {
	private static final SimpleCommandExceptionType JFR_START_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.jfr.start.failed"));
	private static final DynamicCommandExceptionType JFR_DUMP_FAILED_EXCEPTION = new DynamicCommandExceptionType(
		message -> new TranslatableText("commands.jfr.dump.failed", message)
	);

	private JfrCommand() {
	}

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("jfr")
				.requires(source -> source.hasPermissionLevel(4))
				.then(CommandManager.literal("start").executes(context -> executeStart(context.getSource())))
				.then(CommandManager.literal("stop").executes(context -> executeStop(context.getSource())))
		);
	}

	private static int executeStart(ServerCommandSource source) throws CommandSyntaxException {
		JfrProfiler.InstanceType instanceType = JfrProfiler.InstanceType.get(source.getServer());
		if (!JfrProfiler.start(instanceType)) {
			throw JFR_START_FAILED_EXCEPTION.create();
		} else {
			source.sendFeedback(new TranslatableText("commands.jfr.started"), false);
			return 1;
		}
	}

	private static int executeStop(ServerCommandSource source) throws CommandSyntaxException {
		try {
			Path path = JfrProfiler.stop();
			source.sendFeedback(new TranslatableText("commands.jfr.stopped", path), false);
			return 1;
		} catch (Throwable var2) {
			throw JFR_DUMP_FAILED_EXCEPTION.create(var2.getMessage());
		}
	}
}
