package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;

@Environment(EnvType.CLIENT)
public class ZombieVillagerRenderState extends ZombieEntityRenderState implements VillagerDataRenderState {
	public VillagerData villagerData = new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1);

	@Override
	public VillagerData getVillagerData() {
		return this.villagerData;
	}
}
