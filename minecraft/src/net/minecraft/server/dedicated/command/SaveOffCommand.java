package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class SaveOffCommand {
	private static final SimpleCommandExceptionType ALREADY_OFF_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.save.alreadyOff"));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("save-off").requires(source -> source.hasPermissionLevel(4)).executes(context -> {
			ServerCommandSource serverCommandSource = context.getSource();
			boolean bl = false;

			for (ServerWorld serverWorld : serverCommandSource.getServer().getWorlds()) {
				if (serverWorld != null && !serverWorld.savingDisabled) {
					serverWorld.savingDisabled = true;
					bl = true;
				}
			}

			if (!bl) {
				throw ALREADY_OFF_EXCEPTION.create();
			} else {
				serverCommandSource.sendFeedback(() -> Text.translatable("commands.save.disabled"), true);
				return 1;
			}
		}));
	}
}
