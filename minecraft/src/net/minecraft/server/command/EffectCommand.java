package net.minecraft.server.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.MobEffectArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.TranslatableText;

public class EffectCommand {
	private static final SimpleCommandExceptionType GIVE_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.effect.give.failed"));
	private static final SimpleCommandExceptionType CLEAR_EVERYTHING_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.effect.clear.everything.failed")
	);
	private static final SimpleCommandExceptionType CLEAR_SPECIFIC_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.effect.clear.specific.failed")
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("effect")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.literal("clear")
						.executes(commandContext -> executeClear(commandContext.getSource(), ImmutableList.of(commandContext.getSource().getEntityOrThrow())))
						.then(
							CommandManager.argument("targets", EntityArgumentType.entities())
								.executes(commandContext -> executeClear(commandContext.getSource(), EntityArgumentType.getEntities(commandContext, "targets")))
								.then(
									CommandManager.argument("effect", MobEffectArgumentType.mobEffect())
										.executes(
											commandContext -> executeClear(
													commandContext.getSource(),
													EntityArgumentType.getEntities(commandContext, "targets"),
													MobEffectArgumentType.getMobEffect(commandContext, "effect")
												)
										)
								)
						)
				)
				.then(
					CommandManager.literal("give")
						.then(
							CommandManager.argument("targets", EntityArgumentType.entities())
								.then(
									CommandManager.argument("effect", MobEffectArgumentType.mobEffect())
										.executes(
											commandContext -> executeGive(
													commandContext.getSource(),
													EntityArgumentType.getEntities(commandContext, "targets"),
													MobEffectArgumentType.getMobEffect(commandContext, "effect"),
													null,
													0,
													true
												)
										)
										.then(
											CommandManager.argument("seconds", IntegerArgumentType.integer(1, 1000000))
												.executes(
													commandContext -> executeGive(
															commandContext.getSource(),
															EntityArgumentType.getEntities(commandContext, "targets"),
															MobEffectArgumentType.getMobEffect(commandContext, "effect"),
															IntegerArgumentType.getInteger(commandContext, "seconds"),
															0,
															true
														)
												)
												.then(
													CommandManager.argument("amplifier", IntegerArgumentType.integer(0, 255))
														.executes(
															commandContext -> executeGive(
																	commandContext.getSource(),
																	EntityArgumentType.getEntities(commandContext, "targets"),
																	MobEffectArgumentType.getMobEffect(commandContext, "effect"),
																	IntegerArgumentType.getInteger(commandContext, "seconds"),
																	IntegerArgumentType.getInteger(commandContext, "amplifier"),
																	true
																)
														)
														.then(
															CommandManager.argument("hideParticles", BoolArgumentType.bool())
																.executes(
																	commandContext -> executeGive(
																			commandContext.getSource(),
																			EntityArgumentType.getEntities(commandContext, "targets"),
																			MobEffectArgumentType.getMobEffect(commandContext, "effect"),
																			IntegerArgumentType.getInteger(commandContext, "seconds"),
																			IntegerArgumentType.getInteger(commandContext, "amplifier"),
																			!BoolArgumentType.getBool(commandContext, "hideParticles")
																		)
																)
														)
												)
										)
								)
						)
				)
		);
	}

	private static int executeGive(
		ServerCommandSource serverCommandSource, Collection<? extends Entity> collection, StatusEffect statusEffect, @Nullable Integer integer, int i, boolean bl
	) throws CommandSyntaxException {
		int j = 0;
		int k;
		if (integer != null) {
			if (statusEffect.isInstant()) {
				k = integer;
			} else {
				k = integer * 20;
			}
		} else if (statusEffect.isInstant()) {
			k = 1;
		} else {
			k = 600;
		}

		for (Entity entity : collection) {
			if (entity instanceof LivingEntity) {
				StatusEffectInstance statusEffectInstance = new StatusEffectInstance(statusEffect, k, i, false, bl);
				if (((LivingEntity)entity).addStatusEffect(statusEffectInstance)) {
					j++;
				}
			}
		}

		if (j == 0) {
			throw GIVE_FAILED_EXCEPTION.create();
		} else {
			if (collection.size() == 1) {
				serverCommandSource.sendFeedback(
					new TranslatableText("commands.effect.give.success.single", statusEffect.getName(), ((Entity)collection.iterator().next()).getDisplayName(), k / 20), true
				);
			} else {
				serverCommandSource.sendFeedback(new TranslatableText("commands.effect.give.success.multiple", statusEffect.getName(), collection.size(), k / 20), true);
			}

			return j;
		}
	}

	private static int executeClear(ServerCommandSource serverCommandSource, Collection<? extends Entity> collection) throws CommandSyntaxException {
		int i = 0;

		for (Entity entity : collection) {
			if (entity instanceof LivingEntity && ((LivingEntity)entity).clearStatusEffects()) {
				i++;
			}
		}

		if (i == 0) {
			throw CLEAR_EVERYTHING_FAILED_EXCEPTION.create();
		} else {
			if (collection.size() == 1) {
				serverCommandSource.sendFeedback(
					new TranslatableText("commands.effect.clear.everything.success.single", ((Entity)collection.iterator().next()).getDisplayName()), true
				);
			} else {
				serverCommandSource.sendFeedback(new TranslatableText("commands.effect.clear.everything.success.multiple", collection.size()), true);
			}

			return i;
		}
	}

	private static int executeClear(ServerCommandSource serverCommandSource, Collection<? extends Entity> collection, StatusEffect statusEffect) throws CommandSyntaxException {
		int i = 0;

		for (Entity entity : collection) {
			if (entity instanceof LivingEntity && ((LivingEntity)entity).tryRemoveStatusEffect(statusEffect)) {
				i++;
			}
		}

		if (i == 0) {
			throw CLEAR_SPECIFIC_FAILED_EXCEPTION.create();
		} else {
			if (collection.size() == 1) {
				serverCommandSource.sendFeedback(
					new TranslatableText("commands.effect.clear.specific.success.single", statusEffect.getName(), ((Entity)collection.iterator().next()).getDisplayName()),
					true
				);
			} else {
				serverCommandSource.sendFeedback(new TranslatableText("commands.effect.clear.specific.success.multiple", statusEffect.getName(), collection.size()), true);
			}

			return i;
		}
	}
}
