package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class GiveCommand {
	public static final int MAX_STACKS = 100;

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
		dispatcher.register(
			CommandManager.literal("give")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("targets", EntityArgumentType.players())
						.then(
							CommandManager.argument("item", ItemStackArgumentType.itemStack(commandRegistryAccess))
								.executes(
									context -> execute(
											context.getSource(), ItemStackArgumentType.getItemStackArgument(context, "item"), EntityArgumentType.getPlayers(context, "targets"), 1
										)
								)
								.then(
									CommandManager.argument("count", IntegerArgumentType.integer(1))
										.executes(
											context -> execute(
													context.getSource(),
													ItemStackArgumentType.getItemStackArgument(context, "item"),
													EntityArgumentType.getPlayers(context, "targets"),
													IntegerArgumentType.getInteger(context, "count")
												)
										)
								)
						)
				)
		);
	}

	private static int execute(ServerCommandSource source, ItemStackArgument item, Collection<ServerPlayerEntity> targets, int count) throws CommandSyntaxException {
		int i = item.getItem().getMaxCount();
		int j = i * 100;
		ItemStack itemStack = item.createStack(count, false);
		if (count > j) {
			source.sendError(Text.translatable("commands.give.failed.toomanyitems", j, itemStack.toHoverableText()));
			return 0;
		} else {
			for (ServerPlayerEntity serverPlayerEntity : targets) {
				int k = count;

				while (k > 0) {
					int l = Math.min(i, k);
					k -= l;
					ItemStack itemStack2 = item.createStack(l, false);
					boolean bl = serverPlayerEntity.getInventory().insertStack(itemStack2);
					if (bl && itemStack2.isEmpty()) {
						itemStack2.setCount(1);
						ItemEntity itemEntity = serverPlayerEntity.dropItem(itemStack2, false);
						if (itemEntity != null) {
							itemEntity.setDespawnImmediately();
						}

						serverPlayerEntity.getWorld()
							.playSound(
								null,
								serverPlayerEntity.getX(),
								serverPlayerEntity.getY(),
								serverPlayerEntity.getZ(),
								SoundEvents.ENTITY_ITEM_PICKUP,
								SoundCategory.PLAYERS,
								0.2F,
								((serverPlayerEntity.getRandom().nextFloat() - serverPlayerEntity.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F
							);
						serverPlayerEntity.currentScreenHandler.sendContentUpdates();
					} else {
						ItemEntity itemEntity = serverPlayerEntity.dropItem(itemStack2, false);
						if (itemEntity != null) {
							itemEntity.resetPickupDelay();
							itemEntity.setOwner(serverPlayerEntity.getUuid());
						}
					}
				}
			}

			if (targets.size() == 1) {
				source.sendFeedback(
					() -> Text.translatable(
							"commands.give.success.single", count, itemStack.toHoverableText(), ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()
						),
					true
				);
			} else {
				source.sendFeedback(() -> Text.translatable("commands.give.success.single", count, itemStack.toHoverableText(), targets.size()), true);
			}

			return targets.size();
		}
	}
}
