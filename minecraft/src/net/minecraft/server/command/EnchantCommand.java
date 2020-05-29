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
import net.minecraft.text.TranslatableText;

public class EnchantCommand {
	private static final DynamicCommandExceptionType FAILED_ENTITY_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.enchant.failed.entity", object)
	);
	private static final DynamicCommandExceptionType FAILED_ITEMLESS_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.enchant.failed.itemless", object)
	);
	private static final DynamicCommandExceptionType FAILED_INCOMPATIBLE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.enchant.failed.incompatible", object)
	);
	private static final Dynamic2CommandExceptionType FAILED_LEVEL_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("commands.enchant.failed.level", object, object2)
	);
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.enchant.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("enchant")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.argument("targets", EntityArgumentType.entities())
						.then(
							CommandManager.argument("enchantment", ItemEnchantmentArgumentType.itemEnchantment())
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

	private static int execute(ServerCommandSource source, Collection<? extends Entity> targets, Enchantment enchantment, int level) throws CommandSyntaxException {
		if (level > enchantment.getMaxLevel()) {
			throw FAILED_LEVEL_EXCEPTION.create(level, enchantment.getMaxLevel());
		} else {
			int i = 0;

			for (Entity entity : targets) {
				if (entity instanceof LivingEntity) {
					LivingEntity livingEntity = (LivingEntity)entity;
					ItemStack itemStack = livingEntity.getMainHandStack();
					if (!itemStack.isEmpty()) {
						if (enchantment.isAcceptableItem(itemStack) && EnchantmentHelper.isCompatible(EnchantmentHelper.get(itemStack).keySet(), enchantment)) {
							itemStack.addEnchantment(enchantment, level);
							i++;
						} else if (targets.size() == 1) {
							throw FAILED_INCOMPATIBLE_EXCEPTION.create(itemStack.getItem().getName(itemStack).getString());
						}
					} else if (targets.size() == 1) {
						throw FAILED_ITEMLESS_EXCEPTION.create(livingEntity.getName().getString());
					}
				} else if (targets.size() == 1) {
					throw FAILED_ENTITY_EXCEPTION.create(entity.getName().getString());
				}
			}

			if (i == 0) {
				throw FAILED_EXCEPTION.create();
			} else {
				if (targets.size() == 1) {
					source.sendFeedback(
						new TranslatableText("commands.enchant.success.single", enchantment.getName(level), ((Entity)targets.iterator().next()).getDisplayName()), true
					);
				} else {
					source.sendFeedback(new TranslatableText("commands.enchant.success.multiple", enchantment.getName(level), targets.size()), true);
				}

				return i;
			}
		}
	}
}
