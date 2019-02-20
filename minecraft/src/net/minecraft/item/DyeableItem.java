package net.minecraft.item;

import net.minecraft.nbt.CompoundTag;

public interface DyeableItem {
	default boolean hasColor(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getSubCompoundTag("display");
		return compoundTag != null && compoundTag.containsKey("color", 99);
	}

	default int getColor(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getSubCompoundTag("display");
		return compoundTag != null && compoundTag.containsKey("color", 99) ? compoundTag.getInt("color") : 10511680;
	}

	default void removeColor(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getSubCompoundTag("display");
		if (compoundTag != null && compoundTag.containsKey("color")) {
			compoundTag.remove("color");
		}
	}

	default void setColor(ItemStack itemStack, int i) {
		itemStack.getOrCreateSubCompoundTag("display").putInt("color", i);
	}
}
