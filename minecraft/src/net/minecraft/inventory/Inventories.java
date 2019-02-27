package net.minecraft.inventory;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.DefaultedList;

public class Inventories {
	public static ItemStack splitStack(List<ItemStack> list, int i, int j) {
		return i >= 0 && i < list.size() && !((ItemStack)list.get(i)).isEmpty() && j > 0 ? ((ItemStack)list.get(i)).split(j) : ItemStack.EMPTY;
	}

	public static ItemStack removeStack(List<ItemStack> list, int i) {
		return i >= 0 && i < list.size() ? (ItemStack)list.set(i, ItemStack.EMPTY) : ItemStack.EMPTY;
	}

	public static CompoundTag toTag(CompoundTag compoundTag, DefaultedList<ItemStack> defaultedList) {
		return toTag(compoundTag, defaultedList, true);
	}

	public static CompoundTag toTag(CompoundTag compoundTag, DefaultedList<ItemStack> defaultedList, boolean bl) {
		ListTag listTag = new ListTag();

		for (int i = 0; i < defaultedList.size(); i++) {
			ItemStack itemStack = defaultedList.get(i);
			if (!itemStack.isEmpty()) {
				CompoundTag compoundTag2 = new CompoundTag();
				compoundTag2.putByte("Slot", (byte)i);
				itemStack.toTag(compoundTag2);
				listTag.add(compoundTag2);
			}
		}

		if (!listTag.isEmpty() || bl) {
			compoundTag.put("Items", listTag);
		}

		return compoundTag;
	}

	public static void fromTag(CompoundTag compoundTag, DefaultedList<ItemStack> defaultedList) {
		ListTag listTag = compoundTag.getList("Items", 10);

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag2 = listTag.getCompoundTag(i);
			int j = compoundTag2.getByte("Slot") & 255;
			if (j >= 0 && j < defaultedList.size()) {
				defaultedList.set(j, ItemStack.fromTag(compoundTag2));
			}
		}
	}
}
