package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class SeedCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
		dispatcher.register(
			CommandManager.literal("seed")
				.requires(serverCommandSource -> !dedicated || serverCommandSource.hasPermissionLevel(2))
				.executes(
					commandContext -> {
						long l = commandContext.getSource().getWorld().getSeed();
						Text text = Texts.bracketed(
							new LiteralText(String.valueOf(l))
								.styled(
									style -> style.withColor(Formatting.GREEN)
											.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(l)))
											.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("chat.copy.click")))
											.withInsertion(String.valueOf(l))
								)
						);
						commandContext.getSource().sendFeedback(new TranslatableText("commands.seed.success", text), false);
						return (int)l;
					}
				)
		);
	}
}
