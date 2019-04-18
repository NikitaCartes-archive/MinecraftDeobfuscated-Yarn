package net.minecraft.item;

import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Hand;

public abstract class BaseBowItem extends Item {
	public static final Predicate<ItemStack> IS_BOW_PROJECTILE = itemStack -> itemStack.getItem().matches(ItemTags.field_18317);
	public static final Predicate<ItemStack> IS_CROSSBOW_PROJECTILE = IS_BOW_PROJECTILE.or(itemStack -> itemStack.getItem() == Items.field_8639);

	public BaseBowItem(Item.Settings settings) {
		super(settings);
	}

	public Predicate<ItemStack> getHeldProjectilePredicate() {
		return this.getInventoryProjectilePredicate();
	}

	public abstract Predicate<ItemStack> getInventoryProjectilePredicate();

	public static ItemStack getItemHeld(LivingEntity livingEntity, Predicate<ItemStack> predicate) {
		if (predicate.test(livingEntity.getStackInHand(Hand.OFF))) {
			return livingEntity.getStackInHand(Hand.OFF);
		} else {
			return predicate.test(livingEntity.getStackInHand(Hand.MAIN)) ? livingEntity.getStackInHand(Hand.MAIN) : ItemStack.EMPTY;
		}
	}

	@Override
	public int getEnchantability() {
		return 1;
	}
}
