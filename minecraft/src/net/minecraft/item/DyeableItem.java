package net.minecraft.item;

import java.util.List;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public interface DyeableItem {
	String COLOR_KEY = "color";
	String DISPLAY_KEY = "display";
	int DEFAULT_COLOR = 10511680;

	default boolean hasColor(ItemStack stack) {
		NbtCompound nbtCompound = stack.getSubNbt("display");
		return nbtCompound != null && nbtCompound.contains("color", NbtElement.NUMBER_TYPE);
	}

	default int getColor(ItemStack stack) {
		NbtCompound nbtCompound = stack.getSubNbt("display");
		return nbtCompound != null && nbtCompound.contains("color", NbtElement.NUMBER_TYPE) ? nbtCompound.getInt("color") : 10511680;
	}

	default void removeColor(ItemStack stack) {
		NbtCompound nbtCompound = stack.getSubNbt("display");
		if (nbtCompound != null && nbtCompound.contains("color")) {
			nbtCompound.remove("color");
		}
	}

	default void setColor(ItemStack stack, int color) {
		stack.getOrCreateSubNbt("display").putInt("color", color);
	}

	static ItemStack blendAndSetColor(ItemStack stack, List<DyeItem> colors) {
		ItemStack itemStack = ItemStack.EMPTY;
		int[] is = new int[3];
		int i = 0;
		int j = 0;
		DyeableItem dyeableItem = null;
		Item item = stack.getItem();
		if (item instanceof DyeableItem) {
			dyeableItem = (DyeableItem)item;
			itemStack = stack.copyWithCount(1);
			if (dyeableItem.hasColor(stack)) {
				int k = dyeableItem.getColor(itemStack);
				float f = (float)(k >> 16 & 0xFF) / 255.0F;
				float g = (float)(k >> 8 & 0xFF) / 255.0F;
				float h = (float)(k & 0xFF) / 255.0F;
				i += (int)(Math.max(f, Math.max(g, h)) * 255.0F);
				is[0] += (int)(f * 255.0F);
				is[1] += (int)(g * 255.0F);
				is[2] += (int)(h * 255.0F);
				j++;
			}

			for (DyeItem dyeItem : colors) {
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
			dyeableItem.setColor(itemStack, var26);
			return itemStack;
		}
	}
}
