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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

public class ExperienceCommand {
	private static final SimpleCommandExceptionType SET_POINT_INVALID_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.experience.set.points.invalid")
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		LiteralCommandNode<ServerCommandSource> literalCommandNode = commandDispatcher.register(
			CommandManager.literal("experience")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.literal("add")
						.then(
							CommandManager.argument("targets", EntityArgumentType.players())
								.then(
									CommandManager.argument("amount", IntegerArgumentType.integer())
										.executes(
											commandContext -> executeAdd(
													commandContext.getSource(),
													EntityArgumentType.getPlayers(commandContext, "targets"),
													IntegerArgumentType.getInteger(commandContext, "amount"),
													ExperienceCommand.Component.field_13644
												)
										)
										.then(
											CommandManager.literal("points")
												.executes(
													commandContext -> executeAdd(
															commandContext.getSource(),
															EntityArgumentType.getPlayers(commandContext, "targets"),
															IntegerArgumentType.getInteger(commandContext, "amount"),
															ExperienceCommand.Component.field_13644
														)
												)
										)
										.then(
											CommandManager.literal("levels")
												.executes(
													commandContext -> executeAdd(
															commandContext.getSource(),
															EntityArgumentType.getPlayers(commandContext, "targets"),
															IntegerArgumentType.getInteger(commandContext, "amount"),
															ExperienceCommand.Component.field_13641
														)
												)
										)
								)
						)
				)
				.then(
					CommandManager.literal("set")
						.then(
							CommandManager.argument("targets", EntityArgumentType.players())
								.then(
									CommandManager.argument("amount", IntegerArgumentType.integer(0))
										.executes(
											commandContext -> executeSet(
													commandContext.getSource(),
													EntityArgumentType.getPlayers(commandContext, "targets"),
													IntegerArgumentType.getInteger(commandContext, "amount"),
													ExperienceCommand.Component.field_13644
												)
										)
										.then(
											CommandManager.literal("points")
												.executes(
													commandContext -> executeSet(
															commandContext.getSource(),
															EntityArgumentType.getPlayers(commandContext, "targets"),
															IntegerArgumentType.getInteger(commandContext, "amount"),
															ExperienceCommand.Component.field_13644
														)
												)
										)
										.then(
											CommandManager.literal("levels")
												.executes(
													commandContext -> executeSet(
															commandContext.getSource(),
															EntityArgumentType.getPlayers(commandContext, "targets"),
															IntegerArgumentType.getInteger(commandContext, "amount"),
															ExperienceCommand.Component.field_13641
														)
												)
										)
								)
						)
				)
				.then(
					CommandManager.literal("query")
						.then(
							CommandManager.argument("targets", EntityArgumentType.player())
								.then(
									CommandManager.literal("points")
										.executes(
											commandContext -> executeQuery(
													commandContext.getSource(), EntityArgumentType.getPlayer(commandContext, "targets"), ExperienceCommand.Component.field_13644
												)
										)
								)
								.then(
									CommandManager.literal("levels")
										.executes(
											commandContext -> executeQuery(
													commandContext.getSource(), EntityArgumentType.getPlayer(commandContext, "targets"), ExperienceCommand.Component.field_13641
												)
										)
								)
						)
				)
		);
		commandDispatcher.register(
			CommandManager.literal("xp").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).redirect(literalCommandNode)
		);
	}

	private static int executeQuery(ServerCommandSource serverCommandSource, ServerPlayerEntity serverPlayerEntity, ExperienceCommand.Component component) {
		int i = component.getter.applyAsInt(serverPlayerEntity);
		serverCommandSource.method_9226(new TranslatableText("commands.experience.query." + component.name, serverPlayerEntity.method_5476(), i), false);
		return i;
	}

	private static int executeAdd(
		ServerCommandSource serverCommandSource, Collection<? extends ServerPlayerEntity> collection, int i, ExperienceCommand.Component component
	) {
		for (ServerPlayerEntity serverPlayerEntity : collection) {
			component.adder.accept(serverPlayerEntity, i);
		}

		if (collection.size() == 1) {
			serverCommandSource.method_9226(
				new TranslatableText("commands.experience.add." + component.name + ".success.single", i, ((ServerPlayerEntity)collection.iterator().next()).method_5476()),
				true
			);
		} else {
			serverCommandSource.method_9226(new TranslatableText("commands.experience.add." + component.name + ".success.multiple", i, collection.size()), true);
		}

		return collection.size();
	}

	private static int executeSet(
		ServerCommandSource serverCommandSource, Collection<? extends ServerPlayerEntity> collection, int i, ExperienceCommand.Component component
	) throws CommandSyntaxException {
		int j = 0;

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			if (component.setter.test(serverPlayerEntity, i)) {
				j++;
			}
		}

		if (j == 0) {
			throw SET_POINT_INVALID_EXCEPTION.create();
		} else {
			if (collection.size() == 1) {
				serverCommandSource.method_9226(
					new TranslatableText("commands.experience.set." + component.name + ".success.single", i, ((ServerPlayerEntity)collection.iterator().next()).method_5476()),
					true
				);
			} else {
				serverCommandSource.method_9226(new TranslatableText("commands.experience.set." + component.name + ".success.multiple", i, collection.size()), true);
			}

			return collection.size();
		}
	}

	static enum Component {
		field_13644("points", PlayerEntity::addExperience, (serverPlayerEntity, integer) -> {
			if (integer >= serverPlayerEntity.getNextLevelExperience()) {
				return false;
			} else {
				serverPlayerEntity.setExperiencePoints(integer);
				return true;
			}
		}, serverPlayerEntity -> MathHelper.floor(serverPlayerEntity.experienceProgress * (float)serverPlayerEntity.getNextLevelExperience())),
		field_13641("levels", ServerPlayerEntity::addExperienceLevels, (serverPlayerEntity, integer) -> {
			serverPlayerEntity.setExperienceLevel(integer);
			return true;
		}, serverPlayerEntity -> serverPlayerEntity.experienceLevel);

		public final BiConsumer<ServerPlayerEntity, Integer> adder;
		public final BiPredicate<ServerPlayerEntity, Integer> setter;
		public final String name;
		private final ToIntFunction<ServerPlayerEntity> getter;

		private Component(
			String string2,
			BiConsumer<ServerPlayerEntity, Integer> biConsumer,
			BiPredicate<ServerPlayerEntity, Integer> biPredicate,
			ToIntFunction<ServerPlayerEntity> toIntFunction
		) {
			this.adder = biConsumer;
			this.name = string2;
			this.setter = biPredicate;
			this.getter = toIntFunction;
		}
	}
}
