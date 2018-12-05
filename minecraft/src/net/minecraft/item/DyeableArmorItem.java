package net.minecraft.item;

import net.minecraft.class_1741;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.nbt.CompoundTag;

public class DyeableArmorItem extends ArmorItem {
	public DyeableArmorItem(class_1741 arg, EquipmentSlot equipmentSlot, Item.Settings settings) {
		super(arg, equipmentSlot, settings);
	}

	public boolean hasColor(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getSubCompoundTag("display");
		return compoundTag != null && compoundTag.containsKey("color", 99);
	}

	public int getColor(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getSubCompoundTag("display");
		return compoundTag != null && compoundTag.containsKey("color", 99) ? compoundTag.getInt("color") : 10511680;
	}

	public void removeColor(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getSubCompoundTag("display");
		if (compoundTag != null && compoundTag.containsKey("color")) {
			compoundTag.remove("color");
		}
	}

	public void setColor(ItemStack itemStack, int i) {
		itemStack.getOrCreateSubCompoundTag("display").putInt("color", i);
	}
}
