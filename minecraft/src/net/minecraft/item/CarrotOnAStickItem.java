package net.minecraft.item;

import net.minecraft.class_4981;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CarrotOnAStickItem<T extends Entity & class_4981> extends Item {
	private final EntityType<T> field_23253;

	public CarrotOnAStickItem(Item.Settings settings, EntityType<T> entityType) {
		super(settings);
		this.field_23253 = entityType;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		if (world.isClient) {
			return TypedActionResult.pass(itemStack);
		} else {
			Entity entity = user.getVehicle();
			if (user.hasVehicle() && entity instanceof class_4981 && entity.getType() == this.field_23253) {
				class_4981 lv = (class_4981)entity;
				if (lv.method_6577()) {
					itemStack.damage(7, user, p -> p.sendToolBreakStatus(hand));
					user.swingHand(hand, true);
					if (itemStack.isEmpty()) {
						ItemStack itemStack2 = new ItemStack(Items.FISHING_ROD);
						itemStack2.setTag(itemStack.getTag());
						return TypedActionResult.consume(itemStack2);
					}

					return TypedActionResult.consume(itemStack);
				}
			}

			user.incrementStat(Stats.USED.getOrCreateStat(this));
			return TypedActionResult.pass(itemStack);
		}
	}
}
