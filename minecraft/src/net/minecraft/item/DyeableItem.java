package net.minecraft.item;

import java.util.List;
import net.minecraft.nbt.CompoundTag;

public interface DyeableItem {
	default boolean hasColor(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getSubTag("display");
		return compoundTag != null && compoundTag.containsKey("color", 99);
	}

	default int getColor(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getSubTag("display");
		return compoundTag != null && compoundTag.containsKey("color", 99) ? compoundTag.getInt("color") : 10511680;
	}

	default void removeColor(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getSubTag("display");
		if (compoundTag != null && compoundTag.containsKey("color")) {
			compoundTag.remove("color");
		}
	}

	default void setColor(ItemStack itemStack, int i) {
		itemStack.getOrCreateSubTag("display").putInt("color", i);
	}

	static ItemStack blendAndSetColor(ItemStack itemStack, List<DyeItem> list) {
		ItemStack itemStack2 = ItemStack.EMPTY;
		int[] is = new int[3];
		int i = 0;
		int j = 0;
		DyeableItem dyeableItem = null;
		Item item = itemStack.getItem();
		if (item instanceof DyeableItem) {
			dyeableItem = (DyeableItem)item;
			itemStack2 = itemStack.copy();
			itemStack2.setCount(1);
			if (dyeableItem.hasColor(itemStack)) {
				int k = dyeableItem.getColor(itemStack2);
				float f = (float)(k >> 16 & 0xFF) / 255.0F;
				float g = (float)(k >> 8 & 0xFF) / 255.0F;
				float h = (float)(k & 0xFF) / 255.0F;
				i = (int)((float)i + Math.max(f, Math.max(g, h)) * 255.0F);
				is[0] = (int)((float)is[0] + f * 255.0F);
				is[1] = (int)((float)is[1] + g * 255.0F);
				is[2] = (int)((float)is[2] + h * 255.0F);
				j++;
			}

			for (DyeItem dyeItem : list) {
				float[] fs = dyeItem.getColor().getColorComponents();
				int l = (int)(fs[0] * 255.0F);
				int m = (int)(fs[1] * 255.0F);
				int n = (int)(fs[2] * 255.0F);
				i += Math.max(l, Math.max(m, n));
				is[0] += l;
				is[1] += m;
				is[2] += n;
				j++;
			}
		}

		if (dyeableItem == null) {
			return ItemStack.EMPTY;
		} else {
			int k = is[0] / j;
			int o = is[1] / j;
			int p = is[2] / j;
			float h = (float)i / (float)j;
			float q = (float)Math.max(k, Math.max(o, p));
			k = (int)((float)k * h / q);
			o = (int)((float)o * h / q);
			p = (int)((float)p * h / q);
			int var26 = (k << 8) + o;
			var26 = (var26 << 8) + p;
			dyeableItem.setColor(itemStack2, var26);
			return itemStack2;
		}
	}
}
