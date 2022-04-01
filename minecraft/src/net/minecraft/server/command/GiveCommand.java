package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import net.minecraft.class_7320;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;

public class GiveCommand {
	public static final int MAX_STACKS = 100;

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("give")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.argument("targets", EntityArgumentType.players())
						.then(
							CommandManager.argument("item", ItemStackArgumentType.itemStack())
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
		if (count > j) {
			source.sendError(new TranslatableText("commands.give.failed.toomanyitems", j, item.createStack(count, false).toHoverableText()));
			return 0;
		} else {
			for (ServerPlayerEntity serverPlayerEntity : targets) {
				int k = count;

				while (k > 0) {
					int l = Math.min(i, k);
					k -= l;
					ItemStack itemStack = item.createStack(l, false);
					if (serverPlayerEntity.method_42803() == LivingEntity.class_7316.NONE && itemStack.getCount() == 1) {
						serverPlayerEntity.method_42838((BlockState)class_7320.method_42858(itemStack).orElse(null));
					} else if (!itemStack.isEmpty()) {
						serverPlayerEntity.dropItem(itemStack, false);
						serverPlayerEntity.world
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
					}
				}
			}

			if (targets.size() == 1) {
				source.sendFeedback(
					new TranslatableText(
						"commands.give.success.single", count, item.createStack(count, false).toHoverableText(), ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()
					),
					true
				);
			} else {
				source.sendFeedback(new TranslatableText("commands.give.success.single", count, item.createStack(count, false).toHoverableText(), targets.size()), true);
			}

			return targets.size();
		}
	}
}
