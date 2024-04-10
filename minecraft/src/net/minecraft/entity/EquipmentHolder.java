package net.minecraft.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.item.Equipment;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.registry.RegistryKey;

public interface EquipmentHolder {
	void equipStack(EquipmentSlot slot, ItemStack stack);

	ItemStack getEquippedStack(EquipmentSlot slot);

	void setEquipmentDropChance(EquipmentSlot slot, float dropChance);

	default void setEquipmentFromTable(EquipmentTable equipmentTable, LootContextParameterSet parameters) {
		this.setEquipmentFromTable(equipmentTable.lootTable(), parameters, equipmentTable.slotDropChances());
	}

	default void setEquipmentFromTable(RegistryKey<LootTable> lootTable, LootContextParameterSet parameters, Map<EquipmentSlot, Float> slotDropChances) {
		this.setEquipmentFromTable(lootTable, parameters, 0L, slotDropChances);
	}

	default void setEquipmentFromTable(RegistryKey<LootTable> lootTable, LootContextParameterSet parameters, long seed, Map<EquipmentSlot, Float> slotDropChances) {
		if (!lootTable.equals(LootTables.EMPTY)) {
			LootTable lootTable2 = parameters.getWorld().getServer().getReloadableRegistries().getLootTable(lootTable);
			if (lootTable2 != LootTable.EMPTY) {
				List<ItemStack> list = lootTable2.generateLoot(parameters, seed);
				List<EquipmentSlot> list2 = new ArrayList();

				for (ItemStack itemStack : list) {
					EquipmentSlot equipmentSlot = this.getSlotForStack(itemStack, list2);
					if (equipmentSlot != null) {
						ItemStack itemStack2 = equipmentSlot.isArmorSlot() ? itemStack.copyWithCount(1) : itemStack;
						this.equipStack(equipmentSlot, itemStack2);
						Float float_ = (Float)slotDropChances.get(equipmentSlot);
						if (float_ != null) {
							this.setEquipmentDropChance(equipmentSlot, float_);
						}

						list2.add(equipmentSlot);
					}
				}
			}
		}
	}

	@Nullable
	default EquipmentSlot getSlotForStack(ItemStack stack, List<EquipmentSlot> slotBlacklist) {
		if (stack.isEmpty()) {
			return null;
		} else {
			Equipment equipment = Equipment.fromStack(stack);
			if (equipment != null) {
				EquipmentSlot equipmentSlot = equipment.getSlotType();
				if (!slotBlacklist.contains(equipmentSlot)) {
					return equipmentSlot;
				}
			} else if (!slotBlacklist.contains(EquipmentSlot.MAINHAND)) {
				return EquipmentSlot.MAINHAND;
			}

			return null;
		}
	}
}
