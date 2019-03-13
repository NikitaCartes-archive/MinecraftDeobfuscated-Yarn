package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.event.ClickEvent;
import net.minecraft.text.event.HoverEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class LocateCommand {
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.locate.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("locate")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(ServerCommandManager.literal("Pillager_Outpost").executes(commandContext -> method_13457(commandContext.getSource(), "Pillager_Outpost")))
				.then(ServerCommandManager.literal("Mineshaft").executes(commandContext -> method_13457(commandContext.getSource(), "Mineshaft")))
				.then(ServerCommandManager.literal("Mansion").executes(commandContext -> method_13457(commandContext.getSource(), "Mansion")))
				.then(ServerCommandManager.literal("Igloo").executes(commandContext -> method_13457(commandContext.getSource(), "Igloo")))
				.then(ServerCommandManager.literal("Desert_Pyramid").executes(commandContext -> method_13457(commandContext.getSource(), "Desert_Pyramid")))
				.then(ServerCommandManager.literal("Jungle_Pyramid").executes(commandContext -> method_13457(commandContext.getSource(), "Jungle_Pyramid")))
				.then(ServerCommandManager.literal("Swamp_Hut").executes(commandContext -> method_13457(commandContext.getSource(), "Swamp_Hut")))
				.then(ServerCommandManager.literal("Stronghold").executes(commandContext -> method_13457(commandContext.getSource(), "Stronghold")))
				.then(ServerCommandManager.literal("Monument").executes(commandContext -> method_13457(commandContext.getSource(), "Monument")))
				.then(ServerCommandManager.literal("Fortress").executes(commandContext -> method_13457(commandContext.getSource(), "Fortress")))
				.then(ServerCommandManager.literal("EndCity").executes(commandContext -> method_13457(commandContext.getSource(), "EndCity")))
				.then(ServerCommandManager.literal("Ocean_Ruin").executes(commandContext -> method_13457(commandContext.getSource(), "Ocean_Ruin")))
				.then(ServerCommandManager.literal("Buried_Treasure").executes(commandContext -> method_13457(commandContext.getSource(), "Buried_Treasure")))
				.then(ServerCommandManager.literal("Shipwreck").executes(commandContext -> method_13457(commandContext.getSource(), "Shipwreck")))
				.then(ServerCommandManager.literal("Village").executes(commandContext -> method_13457(commandContext.getSource(), "Village")))
		);
	}

	private static int method_13457(ServerCommandSource serverCommandSource, String string) throws CommandSyntaxException {
		BlockPos blockPos = new BlockPos(serverCommandSource.method_9222());
		BlockPos blockPos2 = serverCommandSource.method_9225().method_8487(string, blockPos, 100, false);
		if (blockPos2 == null) {
			throw FAILED_EXCEPTION.create();
		} else {
			int i = MathHelper.floor(method_13439(blockPos.getX(), blockPos.getZ(), blockPos2.getX(), blockPos2.getZ()));
			TextComponent textComponent = TextFormatter.bracketed(new TranslatableTextComponent("chat.coordinates", blockPos2.getX(), "~", blockPos2.getZ()))
				.modifyStyle(
					style -> style.setColor(TextFormat.field_1060)
							.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + blockPos2.getX() + " ~ " + blockPos2.getZ()))
							.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableTextComponent("chat.coordinates.tooltip")))
				);
			serverCommandSource.method_9226(new TranslatableTextComponent("commands.locate.success", string, textComponent, i), false);
			return i;
		}
	}

	private static float method_13439(int i, int j, int k, int l) {
		int m = k - i;
		int n = l - j;
		return MathHelper.sqrt((float)(m * m + n * n));
	}
}
