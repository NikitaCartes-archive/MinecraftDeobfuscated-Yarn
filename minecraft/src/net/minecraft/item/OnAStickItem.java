package net.minecraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemSteerable;
import net.minecraft.entity.LivingEntity;
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
			Entity entity = user.getControllingVehicle();
			if (user.hasVehicle() && entity instanceof ItemSteerable itemSteerable && entity.getType() == this.target && itemSteerable.consumeOnAStickItem()) {
				EquipmentSlot equipmentSlot = LivingEntity.getSlotForHand(hand);
				ItemStack itemStack2 = itemStack.damage(this.damagePerUse, Items.FISHING_ROD, user, equipmentSlot);
				return TypedActionResult.success(itemStack2);
			}

			user.incrementStat(Stats.USED.getOrCreateStat(this));
			return TypedActionResult.pass(itemStack);
		}
	}
}
