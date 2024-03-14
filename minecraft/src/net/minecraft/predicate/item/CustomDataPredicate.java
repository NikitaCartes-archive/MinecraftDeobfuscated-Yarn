package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NbtPredicate;

public record CustomDataPredicate(NbtPredicate value) implements ItemSubPredicate {
	public static final Codec<CustomDataPredicate> CODEC = NbtPredicate.CODEC.xmap(CustomDataPredicate::new, CustomDataPredicate::value);

	@Override
	public boolean test(ItemStack stack) {
		return this.value.test(stack);
	}

	public static CustomDataPredicate customData(NbtPredicate value) {
		return new CustomDataPredicate(value);
	}
}
