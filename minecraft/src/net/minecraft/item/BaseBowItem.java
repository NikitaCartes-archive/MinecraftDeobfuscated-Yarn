package net.minecraft.item;

import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Hand;

public abstract class BaseBowItem extends Item {
	public static final Predicate<ItemStack> field_18281 = itemStack -> itemStack.getItem().method_7855(ItemTags.field_18317);
	public static final Predicate<ItemStack> field_18282 = field_18281.or(itemStack -> itemStack.getItem() == Items.field_8639);

	public BaseBowItem(Item.Settings settings) {
		super(settings);
	}

	public abstract Predicate<ItemStack> method_19268();

	public static ItemStack method_18815(LivingEntity livingEntity, Predicate<ItemStack> predicate) {
		if (predicate.test(livingEntity.method_5998(Hand.OFF))) {
			return livingEntity.method_5998(Hand.OFF);
		} else {
			return predicate.test(livingEntity.method_5998(Hand.MAIN)) ? livingEntity.method_5998(Hand.MAIN) : ItemStack.EMPTY;
		}
	}

	@Override
	public int getEnchantability() {
		return 1;
	}
}
