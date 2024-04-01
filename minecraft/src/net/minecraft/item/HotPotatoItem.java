package net.minecraft.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.HeatComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class HotPotatoItem extends Item {
	public static final int field_50588 = 1000000;
	public static final int field_50589 = 20;
	public static final int field_50590 = 200;

	public HotPotatoItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		user.setFireTicks(user.getFireTicks() + 1000000);
		return super.finishUsing(stack, world, user);
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);
		HeatComponent heatComponent = stack.get(DataComponentTypes.HEAT);
		if (heatComponent != null && entity.getUuid() == heatComponent.owner() && slot == heatComponent.slot()) {
			if (heatComponent.heat() < 200) {
				stack.set(DataComponentTypes.HEAT, new HeatComponent(entity.getUuid(), slot, heatComponent.heat() + 1));
			}

			int i = MathHelper.lerpPositive((float)(heatComponent.heat() - 20) / 180.0F, 0, 5);
			if (i > 0) {
				entity.damage(entity.getDamageSources().potatoHeat(), (float)i);
			}
		} else {
			stack.set(DataComponentTypes.HEAT, new HeatComponent(entity.getUuid(), slot, 0));
		}
	}
}
