package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.ChatFormat;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Components;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class LocateCommand {
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.locate.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("locate")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(CommandManager.literal("Pillager_Outpost").executes(commandContext -> execute(commandContext.getSource(), "Pillager_Outpost")))
				.then(CommandManager.literal("Mineshaft").executes(commandContext -> execute(commandContext.getSource(), "Mineshaft")))
				.then(CommandManager.literal("Mansion").executes(commandContext -> execute(commandContext.getSource(), "Mansion")))
				.then(CommandManager.literal("Igloo").executes(commandContext -> execute(commandContext.getSource(), "Igloo")))
				.then(CommandManager.literal("Desert_Pyramid").executes(commandContext -> execute(commandContext.getSource(), "Desert_Pyramid")))
				.then(CommandManager.literal("Jungle_Pyramid").executes(commandContext -> execute(commandContext.getSource(), "Jungle_Pyramid")))
				.then(CommandManager.literal("Swamp_Hut").executes(commandContext -> execute(commandContext.getSource(), "Swamp_Hut")))
				.then(CommandManager.literal("Stronghold").executes(commandContext -> execute(commandContext.getSource(), "Stronghold")))
				.then(CommandManager.literal("Monument").executes(commandContext -> execute(commandContext.getSource(), "Monument")))
				.then(CommandManager.literal("Fortress").executes(commandContext -> execute(commandContext.getSource(), "Fortress")))
				.then(CommandManager.literal("EndCity").executes(commandContext -> execute(commandContext.getSource(), "EndCity")))
				.then(CommandManager.literal("Ocean_Ruin").executes(commandContext -> execute(commandContext.getSource(), "Ocean_Ruin")))
				.then(CommandManager.literal("Buried_Treasure").executes(commandContext -> execute(commandContext.getSource(), "Buried_Treasure")))
				.then(CommandManager.literal("Shipwreck").executes(commandContext -> execute(commandContext.getSource(), "Shipwreck")))
				.then(CommandManager.literal("Village").executes(commandContext -> execute(commandContext.getSource(), "Village")))
		);
	}

	private static int execute(ServerCommandSource serverCommandSource, String string) throws CommandSyntaxException {
		BlockPos blockPos = new BlockPos(serverCommandSource.getPosition());
		BlockPos blockPos2 = serverCommandSource.getWorld().locateStructure(string, blockPos, 100, false);
		if (blockPos2 == null) {
			throw FAILED_EXCEPTION.create();
		} else {
			int i = MathHelper.floor(getDistance(blockPos.getX(), blockPos.getZ(), blockPos2.getX(), blockPos2.getZ()));
			Component component = Components.bracketed(new TranslatableComponent("chat.coordinates", blockPos2.getX(), "~", blockPos2.getZ()))
				.modifyStyle(
					style -> style.setColor(ChatFormat.field_1060)
							.setClickEvent(new ClickEvent(ClickEvent.Action.field_11745, "/tp @s " + blockPos2.getX() + " ~ " + blockPos2.getZ()))
							.setHoverEvent(new HoverEvent(HoverEvent.Action.field_11762, new TranslatableComponent("chat.coordinates.tooltip")))
				);
			serverCommandSource.sendFeedback(new TranslatableComponent("commands.locate.success", string, component, i), false);
			return i;
		}
	}

	private static float getDistance(int i, int j, int k, int l) {
		int m = k - i;
		int n = l - j;
		return MathHelper.sqrt((float)(m * m + n * n));
	}
}
