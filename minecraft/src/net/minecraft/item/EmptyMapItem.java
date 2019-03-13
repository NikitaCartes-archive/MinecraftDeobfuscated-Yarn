package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EmptyMapItem extends MapItem {
	public EmptyMapItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> method_7836(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = FilledMapItem.method_8005(world, MathHelper.floor(playerEntity.x), MathHelper.floor(playerEntity.z), (byte)0, true, false);
		ItemStack itemStack2 = playerEntity.method_5998(hand);
		if (!playerEntity.abilities.creativeMode) {
			itemStack2.subtractAmount(1);
		}

		if (itemStack2.isEmpty()) {
			return new TypedActionResult<>(ActionResult.field_5812, itemStack);
		} else {
			if (!playerEntity.inventory.method_7394(itemStack.copy())) {
				playerEntity.method_7328(itemStack, false);
			}

			playerEntity.method_7259(Stats.field_15372.getOrCreateStat(this));
			return new TypedActionResult<>(ActionResult.field_5812, itemStack2);
		}
	}
}
