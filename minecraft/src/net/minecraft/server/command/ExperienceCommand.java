package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.MathHelper;

public class ExperienceCommand {
	private static final SimpleCommandExceptionType SET_POINT_INVALID_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.experience.set.points.invalid")
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		LiteralCommandNode<ServerCommandSource> literalCommandNode = commandDispatcher.register(
			ServerCommandManager.literal("experience")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.literal("add")
						.then(
							ServerCommandManager.argument("targets", EntityArgumentType.method_9308())
								.then(
									ServerCommandManager.argument("amount", IntegerArgumentType.integer())
										.executes(
											commandContext -> method_13326(
													commandContext.getSource(),
													EntityArgumentType.method_9312(commandContext, "targets"),
													IntegerArgumentType.getInteger(commandContext, "amount"),
													ExperienceCommand.class_3055.field_13644
												)
										)
										.then(
											ServerCommandManager.literal("points")
												.executes(
													commandContext -> method_13326(
															commandContext.getSource(),
															EntityArgumentType.method_9312(commandContext, "targets"),
															IntegerArgumentType.getInteger(commandContext, "amount"),
															ExperienceCommand.class_3055.field_13644
														)
												)
										)
										.then(
											ServerCommandManager.literal("levels")
												.executes(
													commandContext -> method_13326(
															commandContext.getSource(),
															EntityArgumentType.method_9312(commandContext, "targets"),
															IntegerArgumentType.getInteger(commandContext, "amount"),
															ExperienceCommand.class_3055.field_13641
														)
												)
										)
								)
						)
				)
				.then(
					ServerCommandManager.literal("set")
						.then(
							ServerCommandManager.argument("targets", EntityArgumentType.method_9308())
								.then(
									ServerCommandManager.argument("amount", IntegerArgumentType.integer(0))
										.executes(
											commandContext -> method_13333(
													commandContext.getSource(),
													EntityArgumentType.method_9312(commandContext, "targets"),
													IntegerArgumentType.getInteger(commandContext, "amount"),
													ExperienceCommand.class_3055.field_13644
												)
										)
										.then(
											ServerCommandManager.literal("points")
												.executes(
													commandContext -> method_13333(
															commandContext.getSource(),
															EntityArgumentType.method_9312(commandContext, "targets"),
															IntegerArgumentType.getInteger(commandContext, "amount"),
															ExperienceCommand.class_3055.field_13644
														)
												)
										)
										.then(
											ServerCommandManager.literal("levels")
												.executes(
													commandContext -> method_13333(
															commandContext.getSource(),
															EntityArgumentType.method_9312(commandContext, "targets"),
															IntegerArgumentType.getInteger(commandContext, "amount"),
															ExperienceCommand.class_3055.field_13641
														)
												)
										)
								)
						)
				)
				.then(
					ServerCommandManager.literal("query")
						.then(
							ServerCommandManager.argument("targets", EntityArgumentType.method_9305())
								.then(
									ServerCommandManager.literal("points")
										.executes(
											commandContext -> method_13328(
													commandContext.getSource(), EntityArgumentType.method_9315(commandContext, "targets"), ExperienceCommand.class_3055.field_13644
												)
										)
								)
								.then(
									ServerCommandManager.literal("levels")
										.executes(
											commandContext -> method_13328(
													commandContext.getSource(), EntityArgumentType.method_9315(commandContext, "targets"), ExperienceCommand.class_3055.field_13641
												)
										)
								)
						)
				)
		);
		commandDispatcher.register(
			ServerCommandManager.literal("xp").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).redirect(literalCommandNode)
		);
	}

	private static int method_13328(ServerCommandSource serverCommandSource, ServerPlayerEntity serverPlayerEntity, ExperienceCommand.class_3055 arg) {
		int i = arg.field_13645.applyAsInt(serverPlayerEntity);
		serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.experience.query." + arg.field_13643, serverPlayerEntity.getDisplayName(), i), false);
		return i;
	}

	private static int method_13326(
		ServerCommandSource serverCommandSource, Collection<? extends ServerPlayerEntity> collection, int i, ExperienceCommand.class_3055 arg
	) {
		for (ServerPlayerEntity serverPlayerEntity : collection) {
			arg.field_13639.accept(serverPlayerEntity, i);
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent(
					"commands.experience.add." + arg.field_13643 + ".success.single", i, ((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
				),
				true
			);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent("commands.experience.add." + arg.field_13643 + ".success.multiple", i, collection.size()), true
			);
		}

		return collection.size();
	}

	private static int method_13333(
		ServerCommandSource serverCommandSource, Collection<? extends ServerPlayerEntity> collection, int i, ExperienceCommand.class_3055 arg
	) throws CommandSyntaxException {
		int j = 0;

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			if (arg.field_13642.test(serverPlayerEntity, i)) {
				j++;
			}
		}

		if (j == 0) {
			throw SET_POINT_INVALID_EXCEPTION.create();
		} else {
			if (collection.size() == 1) {
				serverCommandSource.sendFeedback(
					new TranslatableTextComponent(
						"commands.experience.set." + arg.field_13643 + ".success.single", i, ((ServerPlayerEntity)collection.iterator().next()).getDisplayName()
					),
					true
				);
			} else {
				serverCommandSource.sendFeedback(
					new TranslatableTextComponent("commands.experience.set." + arg.field_13643 + ".success.multiple", i, collection.size()), true
				);
			}

			return collection.size();
		}
	}

	static enum class_3055 {
		field_13644("points", PlayerEntity::addExperience, (serverPlayerEntity, integer) -> {
			if (integer >= serverPlayerEntity.method_7349()) {
				return false;
			} else {
				serverPlayerEntity.method_14228(integer);
				return true;
			}
		}, serverPlayerEntity -> MathHelper.floor(serverPlayerEntity.experienceBarProgress * (float)serverPlayerEntity.method_7349())),
		field_13641("levels", ServerPlayerEntity::method_7316, (serverPlayerEntity, integer) -> {
			serverPlayerEntity.method_14252(integer);
			return true;
		}, serverPlayerEntity -> serverPlayerEntity.experience);

		public final BiConsumer<ServerPlayerEntity, Integer> field_13639;
		public final BiPredicate<ServerPlayerEntity, Integer> field_13642;
		public final String field_13643;
		private final ToIntFunction<ServerPlayerEntity> field_13645;

		private class_3055(
			String string2,
			BiConsumer<ServerPlayerEntity, Integer> biConsumer,
			BiPredicate<ServerPlayerEntity, Integer> biPredicate,
			ToIntFunction<ServerPlayerEntity> toIntFunction
		) {
			this.field_13639 = biConsumer;
			this.field_13643 = string2;
			this.field_13642 = biPredicate;
			this.field_13645 = toIntFunction;
		}
	}
}
