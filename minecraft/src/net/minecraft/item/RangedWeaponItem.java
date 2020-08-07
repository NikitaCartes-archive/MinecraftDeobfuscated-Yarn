package net.minecraft.item;

import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Hand;

public abstract class RangedWeaponItem extends Item {
	public static final Predicate<ItemStack> BOW_PROJECTILES = stack -> stack.getItem().isIn(ItemTags.field_18317);
	public static final Predicate<ItemStack> CROSSBOW_HELD_PROJECTILES = BOW_PROJECTILES.or(stack -> stack.getItem() == Items.field_8639);

	public RangedWeaponItem(Item.Settings settings) {
		super(settings);
	}

	public Predicate<ItemStack> getHeldProjectiles() {
		return this.getProjectiles();
	}

	public abstract Predicate<ItemStack> getProjectiles();

	public static ItemStack getHeldProjectile(LivingEntity entity, Predicate<ItemStack> predicate) {
		if (predicate.test(entity.getStackInHand(Hand.field_5810))) {
			return entity.getStackInHand(Hand.field_5810);
		} else {
			return predicate.test(entity.getStackInHand(Hand.field_5808)) ? entity.getStackInHand(Hand.field_5808) : ItemStack.EMPTY;
		}
	}

	@Override
	public int getEnchantability() {
		return 1;
	}

	public abstract int getRange();
}
