package net.minecraft;

import java.util.function.Predicate;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public interface class_5630 {
	class_5630 field_27860 = new class_5630() {
		@Override
		public ItemStack method_32327() {
			return ItemStack.EMPTY;
		}

		@Override
		public boolean method_32332(ItemStack itemStack) {
			return false;
		}
	};

	static class_5630 method_32329(Inventory inventory, int i, Predicate<ItemStack> predicate) {
		return new class_5630() {
			@Override
			public ItemStack method_32327() {
				return inventory.getStack(i);
			}

			@Override
			public boolean method_32332(ItemStack itemStack) {
				if (!predicate.test(itemStack)) {
					return false;
				} else {
					inventory.setStack(i, itemStack);
					return true;
				}
			}
		};
	}

	static class_5630 method_32328(Inventory inventory, int i) {
		return method_32329(inventory, i, itemStack -> true);
	}

	static class_5630 method_32331(LivingEntity livingEntity, EquipmentSlot equipmentSlot, Predicate<ItemStack> predicate) {
		return new class_5630() {
			@Override
			public ItemStack method_32327() {
				return livingEntity.getEquippedStack(equipmentSlot);
			}

			@Override
			public boolean method_32332(ItemStack itemStack) {
				if (!predicate.test(itemStack)) {
					return false;
				} else {
					livingEntity.equipStack(equipmentSlot, itemStack);
					return true;
				}
			}
		};
	}

	static class_5630 method_32330(LivingEntity livingEntity, EquipmentSlot equipmentSlot) {
		return method_32331(livingEntity, equipmentSlot, itemStack -> true);
	}

	ItemStack method_32327();

	boolean method_32332(ItemStack itemStack);
}
