package net.minecraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemSteerable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class OnAStickItem<T extends Entity & ItemSteerable> extends Item {
	private final EntityType<T> target;
	private final int damagePerUse;

	public OnAStickItem(Item.Settings settings, EntityType<T> target, int damagePerUse) {
		super(settings);
		this.target = target;
		this.damagePerUse = damagePerUse;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		if (world.isClient) {
			return TypedActionResult.pass(itemStack);
		} else {
			Entity entity = user.getVehicle();
			if (user.hasVehicle() && entity instanceof ItemSteerable && entity.getType() == this.target) {
				ItemSteerable itemSteerable = (ItemSteerable)entity;
				if (itemSteerable.consumeOnAStickItem()) {
					itemStack.damage(this.damagePerUse, user, p -> p.sendToolBreakStatus(hand));
					if (itemStack.isEmpty()) {
						ItemStack itemStack2 = new ItemStack(Items.FISHING_ROD);
						itemStack2.setTag(itemStack.getTag());
						return TypedActionResult.success(itemStack2);
					}

					return TypedActionResult.success(itemStack);
				}
			}

			user.incrementStat(Stats.USED.getOrCreateStat(this));
			return TypedActionResult.pass(itemStack);
		}
	}
}
