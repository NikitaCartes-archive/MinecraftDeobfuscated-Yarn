package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;

public class EnchantCommand {
	private static final DynamicCommandExceptionType FAILED_ENTITY_EXCEPTION = new DynamicCommandExceptionType(
		entityName -> Text.translatable("commands.enchant.failed.entity", entityName)
	);
	private static final DynamicCommandExceptionType FAILED_ITEMLESS_EXCEPTION = new DynamicCommandExceptionType(
		entityName -> Text.translatable("commands.enchant.failed.itemless", entityName)
	);
	private static final DynamicCommandExceptionType FAILED_INCOMPATIBLE_EXCEPTION = new DynamicCommandExceptionType(
		itemName -> Text.translatable("commands.enchant.failed.incompatible", itemName)
	);
	private static final Dynamic2CommandExceptionType FAILED_LEVEL_EXCEPTION = new Dynamic2CommandExceptionType(
		(level, maxLevel) -> Text.translatable("commands.enchant.failed.level", level, maxLevel)
	);
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.enchant.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		dispatcher.register(
			CommandManager.literal("enchant")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("targets", EntityArgumentType.entities())
						.then(
							CommandManager.argument("enchantment", RegistryEntryArgumentType.registryEntry(registryAccess, RegistryKeys.ENCHANTMENT))
								.executes(
									context -> execute(
											context.getSource(), EntityArgumentType.getEntities(context, "targets"), RegistryEntryArgumentType.getEnchantment(context, "enchantment"), 1
										)
								)
								.then(
									CommandManager.argument("level", IntegerArgumentType.integer(0))
										.executes(
											context -> execute(
													context.getSource(),
													EntityArgumentType.getEntities(context, "targets"),
													RegistryEntryArgumentType.getEnchantment(context, "enchantment"),
													IntegerArgumentType.getInteger(context, "level")
												)
										)
								)
						)
				)
		);
	}

	private static int execute(ServerCommandSource source, Collection<? extends Entity> targets, RegistryEntry<Enchantment> enchantment, int level) throws CommandSyntaxException {
		Enchantment enchantment2 = enchantment.value();
		if (level > enchantment2.getMaxLevel()) {
			throw FAILED_LEVEL_EXCEPTION.create(level, enchantment2.getMaxLevel());
		} else {
			int i = 0;

			for (Entity entity : targets) {
				if (entity instanceof LivingEntity) {
					LivingEntity livingEntity = (LivingEntity)entity;
					ItemStack itemStack = livingEntity.getMainHandStack();
					if (!itemStack.isEmpty()) {
						if (enchantment2.isAcceptableItem(itemStack) && EnchantmentHelper.isCompatible(EnchantmentHelper.get(itemStack).keySet(), enchantment2)) {
							itemStack.addEnchantment(enchantment2, level);
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
						() -> Text.translatable("commands.enchant.success.single", enchantment2.getName(level), ((Entity)targets.iterator().next()).getDisplayName()), true
					);
				} else {
					source.sendFeedback(() -> Text.translatable("commands.enchant.success.multiple", enchantment2.getName(level), targets.size()), true);
				}

				return i;
			}
		}
	}
}
