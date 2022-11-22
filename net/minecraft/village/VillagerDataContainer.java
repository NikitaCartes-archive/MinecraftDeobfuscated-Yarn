/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.village;

import net.minecraft.entity.VariantHolder;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerType;

public interface VillagerDataContainer
extends VariantHolder<VillagerType> {
    public VillagerData getVillagerData();

    public void setVillagerData(VillagerData var1);

    @Override
    default public VillagerType getVariant() {
        return this.getVillagerData().getType();
    }

    @Override
    default public void setVariant(VillagerType villagerType) {
        this.setVillagerData(this.getVillagerData().withType(villagerType));
    }

    @Override
    default public /* synthetic */ Object getVariant() {
        return this.getVariant();
    }
}

