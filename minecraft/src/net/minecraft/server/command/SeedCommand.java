package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class SeedCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
		dispatcher.register(CommandManager.literal("seed").requires(source -> !dedicated || source.hasPermissionLevel(2)).executes(context -> {
			long l = context.getSource().getWorld().getSeed();
			Text text = Texts.bracketedCopyable(String.valueOf(l));
			context.getSource().sendFeedback(() -> Text.translatable("commands.seed.success", text), false);
			return (int)l;
		}));
	}
}
