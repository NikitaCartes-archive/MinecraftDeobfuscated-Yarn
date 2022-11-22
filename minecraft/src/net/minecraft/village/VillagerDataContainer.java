package net.minecraft.village;

import net.minecraft.entity.VariantHolder;

public interface VillagerDataContainer extends VariantHolder<VillagerType> {
	VillagerData getVillagerData();

	void setVillagerData(VillagerData villagerData);

	default VillagerType getVariant() {
		return this.getVillagerData().getType();
	}

	default void setVariant(VillagerType villagerType) {
		this.setVillagerData(this.getVillagerData().withType(villagerType));
	}
}
