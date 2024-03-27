package net.minecraft.entity;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.item.Equipment;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface EquipmentHolder {
	void equipStack(EquipmentSlot slot, ItemStack stack);

	ItemStack getEquippedStack(EquipmentSlot slot);

	void setEquipmentDropChance(EquipmentSlot slot, float dropChance);

	default void setEquipmentFromLootTable(Identifier lootTableId, LootContextParameterSet parameters) {
		this.setEquipmentFromLootTable(lootTableId, parameters, 0L);
	}

	default void setEquipmentFromLootTable(Identifier lootTableId, LootContextParameterSet parameters, long seed) {
		RegistryKey<LootTable> registryKey = RegistryKey.of(RegistryKeys.LOOT_TABLE, lootTableId);
		if (!registryKey.equals(LootTables.EMPTY)) {
			LootTable lootTable = parameters.getWorld().getServer().getReloadableRegistries().getLootTable(registryKey);
			if (lootTable != LootTable.EMPTY) {
				List<ItemStack> list = lootTable.generateLoot(parameters, seed);
				List<EquipmentSlot> list2 = new ArrayList();

				for (ItemStack itemStack : list) {
					EquipmentSlot equipmentSlot = this.getSlotForStack(itemStack, list2);
					if (equipmentSlot != null) {
						ItemStack itemStack2 = equipmentSlot.isArmorSlot() ? itemStack.copyWithCount(1) : itemStack;
						this.equipStack(equipmentSlot, itemStack2);
						this.setEquipmentDropChance(equipmentSlot, 0.085F);
						list2.add(equipmentSlot);
					}
				}
			}
		}
	}

	@Nullable
	default EquipmentSlot getSlotForStack(ItemStack stack, List<EquipmentSlot> slotBlacklist) {
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
