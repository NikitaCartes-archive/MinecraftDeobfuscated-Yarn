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
import net.minecraft.network.chat.TranslatableComponent;

public class EnchantCommand {
	private static final DynamicCommandExceptionType FAILED_ENTITY_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableComponent("commands.enchant.failed.entity", object)
	);
	private static final DynamicCommandExceptionType FAILED_ITEMLESS_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableComponent("commands.enchant.failed.itemless", object)
	);
	private static final DynamicCommandExceptionType FAILED_INCOMPATIBLE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableComponent("commands.enchant.failed.incompatible", object)
	);
	private static final Dynamic2CommandExceptionType FAILED_LEVEL_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableComponent("commands.enchant.failed.level", object, object2)
	);
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.enchant.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("enchant")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.argument("targets", EntityArgumentType.entities())
						.then(
							CommandManager.argument("enchantment", ItemEnchantmentArgumentType.create())
								.executes(
									commandContext -> execute(
											commandContext.getSource(),
											EntityArgumentType.getEntities(commandContext, "targets"),
											ItemEnchantmentArgumentType.getEnchantment(commandContext, "enchantment"),
											1
										)
								)
								.then(
									CommandManager.argument("level", IntegerArgumentType.integer(0))
										.executes(
											commandContext -> execute(
													commandContext.getSource(),
													EntityArgumentType.getEntities(commandContext, "targets"),
													ItemEnchantmentArgumentType.getEnchantment(commandContext, "enchantment"),
													IntegerArgumentType.getInteger(commandContext, "level")
												)
										)
								)
						)
				)
		);
	}

	private static int execute(ServerCommandSource serverCommandSource, Collection<? extends Entity> collection, Enchantment enchantment, int i) throws CommandSyntaxException {
		if (i > enchantment.getMaximumLevel()) {
			throw FAILED_LEVEL_EXCEPTION.create(i, enchantment.getMaximumLevel());
		} else {
			int j = 0;

			for (Entity entity : collection) {
				if (entity instanceof LivingEntity) {
					LivingEntity livingEntity = (LivingEntity)entity;
					ItemStack itemStack = livingEntity.getMainHandStack();
					if (!itemStack.isEmpty()) {
						if (enchantment.isAcceptableItem(itemStack) && EnchantmentHelper.contains(EnchantmentHelper.getEnchantments(itemStack).keySet(), enchantment)) {
							itemStack.addEnchantment(enchantment, i);
							j++;
						} else if (collection.size() == 1) {
							throw FAILED_INCOMPATIBLE_EXCEPTION.create(itemStack.getItem().getTranslatedNameTrimmed(itemStack).getString());
						}
					} else if (collection.size() == 1) {
						throw FAILED_ITEMLESS_EXCEPTION.create(livingEntity.getName().getString());
					}
				} else if (collection.size() == 1) {
					throw FAILED_ENTITY_EXCEPTION.create(entity.getName().getString());
				}
			}

			if (j == 0) {
				throw FAILED_EXCEPTION.create();
			} else {
				if (collection.size() == 1) {
					serverCommandSource.sendFeedback(
						new TranslatableComponent("commands.enchant.success.single", enchantment.getTextComponent(i), ((Entity)collection.iterator().next()).getDisplayName()),
						true
					);
				} else {
					serverCommandSource.sendFeedback(new TranslatableComponent("commands.enchant.success.multiple", enchantment.getTextComponent(i), collection.size()), true);
				}

				return j;
			}
		}
	}
}
