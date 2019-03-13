package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.ItemEnchantmentArgumentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableTextComponent;

public class EnchantCommand {
	private static final DynamicCommandExceptionType FAILED_ENTITY_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("commands.enchant.failed.entity", object)
	);
	private static final DynamicCommandExceptionType FAILED_ITEMLESS_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("commands.enchant.failed.itemless", object)
	);
	private static final DynamicCommandExceptionType FAILED_INCOMPATIBLE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("commands.enchant.failed.incompatible", object)
	);
	private static final Dynamic2CommandExceptionType FAILED_LEVEL_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableTextComponent("commands.enchant.failed.level", object, object2)
	);
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableTextComponent("commands.enchant.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("enchant")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.argument("targets", EntityArgumentType.multipleEntities())
						.then(
							ServerCommandManager.argument("enchantment", ItemEnchantmentArgumentType.create())
								.executes(
									commandContext -> method_13241(
											commandContext.getSource(),
											EntityArgumentType.method_9317(commandContext, "targets"),
											ItemEnchantmentArgumentType.method_9334(commandContext, "enchantment"),
											1
										)
								)
								.then(
									ServerCommandManager.argument("level", IntegerArgumentType.integer(0))
										.executes(
											commandContext -> method_13241(
													commandContext.getSource(),
													EntityArgumentType.method_9317(commandContext, "targets"),
													ItemEnchantmentArgumentType.method_9334(commandContext, "enchantment"),
													IntegerArgumentType.getInteger(commandContext, "level")
												)
										)
								)
						)
				)
		);
	}

	private static int method_13241(ServerCommandSource serverCommandSource, Collection<? extends Entity> collection, Enchantment enchantment, int i) throws CommandSyntaxException {
		if (i > enchantment.getMaximumLevel()) {
			throw FAILED_LEVEL_EXCEPTION.create(i, enchantment.getMaximumLevel());
		} else {
			int j = 0;

			for (Entity entity : collection) {
				if (entity instanceof LivingEntity) {
					LivingEntity livingEntity = (LivingEntity)entity;
					ItemStack itemStack = livingEntity.method_6047();
					if (!itemStack.isEmpty()) {
						if (enchantment.isAcceptableItem(itemStack) && EnchantmentHelper.contains(EnchantmentHelper.getEnchantments(itemStack).keySet(), enchantment)) {
							itemStack.method_7978(enchantment, i);
							j++;
						} else if (collection.size() == 1) {
							throw FAILED_INCOMPATIBLE_EXCEPTION.create(itemStack.getItem().method_7864(itemStack).getString());
						}
					} else if (collection.size() == 1) {
						throw FAILED_ITEMLESS_EXCEPTION.create(livingEntity.method_5477().getString());
					}
				} else if (collection.size() == 1) {
					throw FAILED_ENTITY_EXCEPTION.create(entity.method_5477().getString());
				}
			}

			if (j == 0) {
				throw FAILED_EXCEPTION.create();
			} else {
				if (collection.size() == 1) {
					serverCommandSource.method_9226(
						new TranslatableTextComponent("commands.enchant.success.single", enchantment.method_8179(i), ((Entity)collection.iterator().next()).method_5476()), true
					);
				} else {
					serverCommandSource.method_9226(new TranslatableTextComponent("commands.enchant.success.multiple", enchantment.method_8179(i), collection.size()), true);
				}

				return j;
			}
		}
	}
}
