package net.minecraft.inventory;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.DefaultedList;

public class Inventories {
	public static ItemStack method_5430(List<ItemStack> list, int i, int j) {
		return i >= 0 && i < list.size() && !((ItemStack)list.get(i)).isEmpty() && j > 0 ? ((ItemStack)list.get(i)).split(j) : ItemStack.EMPTY;
	}

	public static ItemStack method_5428(List<ItemStack> list, int i) {
		return i >= 0 && i < list.size() ? (ItemStack)list.set(i, ItemStack.EMPTY) : ItemStack.EMPTY;
	}

	public static CompoundTag method_5426(CompoundTag compoundTag, DefaultedList<ItemStack> defaultedList) {
		return method_5427(compoundTag, defaultedList, true);
	}

	public static CompoundTag method_5427(CompoundTag compoundTag, DefaultedList<ItemStack> defaultedList, boolean bl) {
		ListTag listTag = new ListTag();

		for (int i = 0; i < defaultedList.size(); i++) {
			ItemStack itemStack = defaultedList.get(i);
			if (!itemStack.isEmpty()) {
				CompoundTag compoundTag2 = new CompoundTag();
				compoundTag2.putByte("Slot", (byte)i);
				itemStack.method_7953(compoundTag2);
				listTag.add(compoundTag2);
			}
		}

		if (!listTag.isEmpty() || bl) {
			compoundTag.method_10566("Items", listTag);
		}

		return compoundTag;
	}

	public static void method_5429(CompoundTag compoundTag, DefaultedList<ItemStack> defaultedList) {
		ListTag listTag = compoundTag.method_10554("Items", 10);

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag2 = listTag.getCompoundTag(i);
			int j = compoundTag2.getByte("Slot") & 255;
			if (j >= 0 && j < defaultedList.size()) {
				defaultedList.set(j, ItemStack.method_7915(compoundTag2));
			}
		}
	}
}
