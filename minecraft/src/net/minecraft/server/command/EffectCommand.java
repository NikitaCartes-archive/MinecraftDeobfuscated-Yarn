package net.minecraft.server.command;

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
import net.minecraft.text.TranslatableTextComponent;

public class EffectCommand {
	private static final SimpleCommandExceptionType GIVE_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.effect.give.failed")
	);
	private static final SimpleCommandExceptionType CLEAR_EVERYTHING_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.effect.clear.everything.failed")
	);
	private static final SimpleCommandExceptionType CLEAR_SPECIFIC_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableTextComponent("commands.effect.clear.specific.failed")
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("effect")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.literal("clear")
						.then(
							ServerCommandManager.argument("targets", EntityArgumentType.method_9306())
								.executes(commandContext -> method_13230(commandContext.getSource(), EntityArgumentType.method_9317(commandContext, "targets")))
								.then(
									ServerCommandManager.argument("effect", MobEffectArgumentType.create())
										.executes(
											commandContext -> method_13231(
													commandContext.getSource(),
													EntityArgumentType.method_9317(commandContext, "targets"),
													MobEffectArgumentType.getEffectArgument(commandContext, "effect")
												)
										)
								)
						)
				)
				.then(
					ServerCommandManager.literal("give")
						.then(
							ServerCommandManager.argument("targets", EntityArgumentType.method_9306())
								.then(
									ServerCommandManager.argument("effect", MobEffectArgumentType.create())
										.executes(
											commandContext -> method_13227(
													commandContext.getSource(),
													EntityArgumentType.method_9317(commandContext, "targets"),
													MobEffectArgumentType.getEffectArgument(commandContext, "effect"),
													null,
													0,
													true
												)
										)
										.then(
											ServerCommandManager.argument("seconds", IntegerArgumentType.integer(1, 1000000))
												.executes(
													commandContext -> method_13227(
															commandContext.getSource(),
															EntityArgumentType.method_9317(commandContext, "targets"),
															MobEffectArgumentType.getEffectArgument(commandContext, "effect"),
															IntegerArgumentType.getInteger(commandContext, "seconds"),
															0,
															true
														)
												)
												.then(
													ServerCommandManager.argument("amplifier", IntegerArgumentType.integer(0, 255))
														.executes(
															commandContext -> method_13227(
																	commandContext.getSource(),
																	EntityArgumentType.method_9317(commandContext, "targets"),
																	MobEffectArgumentType.getEffectArgument(commandContext, "effect"),
																	IntegerArgumentType.getInteger(commandContext, "seconds"),
																	IntegerArgumentType.getInteger(commandContext, "amplifier"),
																	true
																)
														)
														.then(
															ServerCommandManager.argument("hideParticles", BoolArgumentType.bool())
																.executes(
																	commandContext -> method_13227(
																			commandContext.getSource(),
																			EntityArgumentType.method_9317(commandContext, "targets"),
																			MobEffectArgumentType.getEffectArgument(commandContext, "effect"),
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

	private static int method_13227(
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
				if (((LivingEntity)entity).addPotionEffect(statusEffectInstance)) {
					j++;
				}
			}
		}

		if (j == 0) {
			throw GIVE_FAILED_EXCEPTION.create();
		} else {
			if (collection.size() == 1) {
				serverCommandSource.sendFeedback(
					new TranslatableTextComponent(
						"commands.effect.give.success.single", statusEffect.method_5560(), ((Entity)collection.iterator().next()).getDisplayName(), k / 20
					),
					true
				);
			} else {
				serverCommandSource.sendFeedback(
					new TranslatableTextComponent("commands.effect.give.success.multiple", statusEffect.method_5560(), collection.size(), k / 20), true
				);
			}

			return j;
		}
	}

	private static int method_13230(ServerCommandSource serverCommandSource, Collection<? extends Entity> collection) throws CommandSyntaxException {
		int i = 0;

		for (Entity entity : collection) {
			if (entity instanceof LivingEntity && ((LivingEntity)entity).clearPotionEffects()) {
				i++;
			}
		}

		if (i == 0) {
			throw CLEAR_EVERYTHING_FAILED_EXCEPTION.create();
		} else {
			if (collection.size() == 1) {
				serverCommandSource.sendFeedback(
					new TranslatableTextComponent("commands.effect.clear.everything.success.single", ((Entity)collection.iterator().next()).getDisplayName()), true
				);
			} else {
				serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.effect.clear.everything.success.multiple", collection.size()), true);
			}

			return i;
		}
	}

	private static int method_13231(ServerCommandSource serverCommandSource, Collection<? extends Entity> collection, StatusEffect statusEffect) throws CommandSyntaxException {
		int i = 0;

		for (Entity entity : collection) {
			if (entity instanceof LivingEntity && ((LivingEntity)entity).method_6016(statusEffect)) {
				i++;
			}
		}

		if (i == 0) {
			throw CLEAR_SPECIFIC_FAILED_EXCEPTION.create();
		} else {
			if (collection.size() == 1) {
				serverCommandSource.sendFeedback(
					new TranslatableTextComponent(
						"commands.effect.clear.specific.success.single", statusEffect.method_5560(), ((Entity)collection.iterator().next()).getDisplayName()
					),
					true
				);
			} else {
				serverCommandSource.sendFeedback(
					new TranslatableTextComponent("commands.effect.clear.specific.success.multiple", statusEffect.method_5560(), collection.size()), true
				);
			}

			return i;
		}
	}
}
