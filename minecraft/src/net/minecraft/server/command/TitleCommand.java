package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.function.Function;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.command.argument.TimeArgumentType;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class TitleCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("title")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("targets", EntityArgumentType.players())
						.then(CommandManager.literal("clear").executes(context -> executeClear(context.getSource(), EntityArgumentType.getPlayers(context, "targets"))))
						.then(CommandManager.literal("reset").executes(context -> executeReset(context.getSource(), EntityArgumentType.getPlayers(context, "targets"))))
						.then(
							CommandManager.literal("title")
								.then(
									CommandManager.argument("title", TextArgumentType.text())
										.executes(
											context -> executeTitle(
													context.getSource(),
													EntityArgumentType.getPlayers(context, "targets"),
													TextArgumentType.getTextArgument(context, "title"),
													"title",
													TitleS2CPacket::new
												)
										)
								)
						)
						.then(
							CommandManager.literal("subtitle")
								.then(
									CommandManager.argument("title", TextArgumentType.text())
										.executes(
											context -> executeTitle(
													context.getSource(),
													EntityArgumentType.getPlayers(context, "targets"),
													TextArgumentType.getTextArgument(context, "title"),
													"subtitle",
													SubtitleS2CPacket::new
												)
										)
								)
						)
						.then(
							CommandManager.literal("actionbar")
								.then(
									CommandManager.argument("title", TextArgumentType.text())
										.executes(
											context -> executeTitle(
													context.getSource(),
													EntityArgumentType.getPlayers(context, "targets"),
													TextArgumentType.getTextArgument(context, "title"),
													"actionbar",
													OverlayMessageS2CPacket::new
												)
										)
								)
						)
						.then(
							CommandManager.literal("times")
								.then(
									CommandManager.argument("fadeIn", TimeArgumentType.time())
										.then(
											CommandManager.argument("stay", TimeArgumentType.time())
												.then(
													CommandManager.argument("fadeOut", TimeArgumentType.time())
														.executes(
															context -> executeTimes(
																	context.getSource(),
																	EntityArgumentType.getPlayers(context, "targets"),
																	IntegerArgumentType.getInteger(context, "fadeIn"),
																	IntegerArgumentType.getInteger(context, "stay"),
																	IntegerArgumentType.getInteger(context, "fadeOut")
																)
														)
												)
										)
								)
						)
				)
		);
	}

	private static int executeClear(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
		ClearTitleS2CPacket clearTitleS2CPacket = new ClearTitleS2CPacket(false);

		for (ServerPlayerEntity serverPlayerEntity : targets) {
			serverPlayerEntity.networkHandler.sendPacket(clearTitleS2CPacket);
		}

		if (targets.size() == 1) {
			source.sendFeedback(() -> Text.translatable("commands.title.cleared.single", ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()), true);
		} else {
			source.sendFeedback(() -> Text.translatable("commands.title.cleared.multiple", targets.size()), true);
		}

		return targets.size();
	}

	private static int executeReset(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
		ClearTitleS2CPacket clearTitleS2CPacket = new ClearTitleS2CPacket(true);

		for (ServerPlayerEntity serverPlayerEntity : targets) {
			serverPlayerEntity.networkHandler.sendPacket(clearTitleS2CPacket);
		}

		if (targets.size() == 1) {
			source.sendFeedback(() -> Text.translatable("commands.title.reset.single", ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()), true);
		} else {
			source.sendFeedback(() -> Text.translatable("commands.title.reset.multiple", targets.size()), true);
		}

		return targets.size();
	}

	private static int executeTitle(
		ServerCommandSource source, Collection<ServerPlayerEntity> targets, Text title, String titleType, Function<Text, Packet<?>> constructor
	) throws CommandSyntaxException {
		for (ServerPlayerEntity serverPlayerEntity : targets) {
			serverPlayerEntity.networkHandler.sendPacket((Packet<?>)constructor.apply(Texts.parse(source, title, serverPlayerEntity, 0)));
		}

		if (targets.size() == 1) {
			source.sendFeedback(
				() -> Text.translatable("commands.title.show." + titleType + ".single", ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()), true
			);
		} else {
			source.sendFeedback(() -> Text.translatable("commands.title.show." + titleType + ".multiple", targets.size()), true);
		}

		return targets.size();
	}

	private static int executeTimes(ServerCommandSource source, Collection<ServerPlayerEntity> targets, int fadeIn, int stay, int fadeOut) {
		TitleFadeS2CPacket titleFadeS2CPacket = new TitleFadeS2CPacket(fadeIn, stay, fadeOut);

		for (ServerPlayerEntity serverPlayerEntity : targets) {
			serverPlayerEntity.networkHandler.sendPacket(titleFadeS2CPacket);
		}

		if (targets.size() == 1) {
			source.sendFeedback(() -> Text.translatable("commands.title.times.single", ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()), true);
		} else {
			source.sendFeedback(() -> Text.translatable("commands.title.times.multiple", targets.size()), true);
		}

		return targets.size();
	}
}
