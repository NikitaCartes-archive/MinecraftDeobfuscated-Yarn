package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;

public class SeedCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
		dispatcher.register(
			CommandManager.literal("seed")
				.requires(source -> !dedicated || source.hasPermissionLevel(2))
				.executes(
					context -> {
						long l = context.getSource().getWorld().getSeed();
						Text text = Texts.bracketed(
							Text.literal(String.valueOf(l))
								.styled(
									style -> style.withColor(Formatting.GREEN)
											.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(l)))
											.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("chat.copy.click")))
											.withInsertion(String.valueOf(l))
								)
						);
						context.getSource().sendFeedback(Text.translatable("commands.seed.success", text), false);
						return (int)l;
					}
				)
		);
	}
}
