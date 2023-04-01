package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.class_8293;

public class DupeHackItem extends Item {
	public DupeHackItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public boolean hasRecipeRemainder() {
		return (double)((Integer)class_8293.field_43676.method_50171()).intValue() / 100.0 < Math.random();
	}

	@Nullable
	@Override
	public Item getRecipeRemainder() {
		return this;
	}
}
