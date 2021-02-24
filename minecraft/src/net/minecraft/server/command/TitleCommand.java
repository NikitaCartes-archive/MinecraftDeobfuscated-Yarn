package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.function.Function;
import net.minecraft.class_5888;
import net.minecraft.class_5894;
import net.minecraft.class_5903;
import net.minecraft.class_5904;
import net.minecraft.class_5905;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

public class TitleCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("title")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.argument("targets", EntityArgumentType.players())
						.then(
							CommandManager.literal("clear")
								.executes(commandContext -> executeClear(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets")))
						)
						.then(
							CommandManager.literal("reset")
								.executes(commandContext -> executeReset(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets")))
						)
						.then(
							CommandManager.literal("title")
								.then(
									CommandManager.argument("title", TextArgumentType.text())
										.executes(
											commandContext -> executeTitle(
													commandContext.getSource(),
													EntityArgumentType.getPlayers(commandContext, "targets"),
													TextArgumentType.getTextArgument(commandContext, "title"),
													"title",
													class_5904::new
												)
										)
								)
						)
						.then(
							CommandManager.literal("subtitle")
								.then(
									CommandManager.argument("title", TextArgumentType.text())
										.executes(
											commandContext -> executeTitle(
													commandContext.getSource(),
													EntityArgumentType.getPlayers(commandContext, "targets"),
													TextArgumentType.getTextArgument(commandContext, "title"),
													"subtitle",
													class_5903::new
												)
										)
								)
						)
						.then(
							CommandManager.literal("actionbar")
								.then(
									CommandManager.argument("title", TextArgumentType.text())
										.executes(
											commandContext -> executeTitle(
													commandContext.getSource(),
													EntityArgumentType.getPlayers(commandContext, "targets"),
													TextArgumentType.getTextArgument(commandContext, "title"),
													"actionbar",
													class_5894::new
												)
										)
								)
						)
						.then(
							CommandManager.literal("times")
								.then(
									CommandManager.argument("fadeIn", IntegerArgumentType.integer(0))
										.then(
											CommandManager.argument("stay", IntegerArgumentType.integer(0))
												.then(
													CommandManager.argument("fadeOut", IntegerArgumentType.integer(0))
														.executes(
															commandContext -> executeTimes(
																	commandContext.getSource(),
																	EntityArgumentType.getPlayers(commandContext, "targets"),
																	IntegerArgumentType.getInteger(commandContext, "fadeIn"),
																	IntegerArgumentType.getInteger(commandContext, "stay"),
																	IntegerArgumentType.getInteger(commandContext, "fadeOut")
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
		class_5888 lv = new class_5888(false);

		for (ServerPlayerEntity serverPlayerEntity : targets) {
			serverPlayerEntity.networkHandler.sendPacket(lv);
		}

		if (targets.size() == 1) {
			source.sendFeedback(new TranslatableText("commands.title.cleared.single", ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()), true);
		} else {
			source.sendFeedback(new TranslatableText("commands.title.cleared.multiple", targets.size()), true);
		}

		return targets.size();
	}

	private static int executeReset(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
		class_5888 lv = new class_5888(true);

		for (ServerPlayerEntity serverPlayerEntity : targets) {
			serverPlayerEntity.networkHandler.sendPacket(lv);
		}

		if (targets.size() == 1) {
			source.sendFeedback(new TranslatableText("commands.title.reset.single", ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()), true);
		} else {
			source.sendFeedback(new TranslatableText("commands.title.reset.multiple", targets.size()), true);
		}

		return targets.size();
	}

	private static int executeTitle(
		ServerCommandSource source, Collection<ServerPlayerEntity> targets, Text title, String string, Function<Text, Packet<?>> function
	) throws CommandSyntaxException {
		for (ServerPlayerEntity serverPlayerEntity : targets) {
			serverPlayerEntity.networkHandler.sendPacket((Packet<?>)function.apply(Texts.parse(source, title, serverPlayerEntity, 0)));
		}

		if (targets.size() == 1) {
			source.sendFeedback(
				new TranslatableText("commands.title.show." + string + ".single", ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()), true
			);
		} else {
			source.sendFeedback(new TranslatableText("commands.title.show." + string + ".multiple", targets.size()), true);
		}

		return targets.size();
	}

	private static int executeTimes(ServerCommandSource source, Collection<ServerPlayerEntity> targets, int fadeIn, int stay, int fadeOut) {
		class_5905 lv = new class_5905(fadeIn, stay, fadeOut);

		for (ServerPlayerEntity serverPlayerEntity : targets) {
			serverPlayerEntity.networkHandler.sendPacket(lv);
		}

		if (targets.size() == 1) {
			source.sendFeedback(new TranslatableText("commands.title.times.single", ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()), true);
		} else {
			source.sendFeedback(new TranslatableText("commands.title.times.multiple", targets.size()), true);
		}

		return targets.size();
	}
}
