package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.village.VillagerData;

@Environment(EnvType.CLIENT)
public interface VillagerDataRenderState {
	VillagerData getVillagerData();
}
