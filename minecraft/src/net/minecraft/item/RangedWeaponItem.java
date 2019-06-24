package net.minecraft.item;

import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Hand;

public abstract class RangedWeaponItem extends Item {
	public static final Predicate<ItemStack> BOW_PROJECTILES = itemStack -> itemStack.getItem().isIn(ItemTags.ARROWS);
	public static final Predicate<ItemStack> CROSSBOW_HELD_PROJECTILES = BOW_PROJECTILES.or(itemStack -> itemStack.getItem() == Items.FIREWORK_ROCKET);

	public RangedWeaponItem(Item.Settings settings) {
		super(settings);
	}

	public Predicate<ItemStack> getHeldProjectiles() {
		return this.getProjectiles();
	}

	public abstract Predicate<ItemStack> getProjectiles();

	public static ItemStack getHeldProjectile(LivingEntity livingEntity, Predicate<ItemStack> predicate) {
		if (predicate.test(livingEntity.getStackInHand(Hand.OFF_HAND))) {
			return livingEntity.getStackInHand(Hand.OFF_HAND);
		} else {
			return predicate.test(livingEntity.getStackInHand(Hand.MAIN_HAND)) ? livingEntity.getStackInHand(Hand.MAIN_HAND) : ItemStack.EMPTY;
		}
	}

	@Override
	public int getEnchantability() {
		return 1;
	}
}
