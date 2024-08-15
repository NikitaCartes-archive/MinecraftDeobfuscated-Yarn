package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.TropicalFishEntity;

@Environment(EnvType.CLIENT)
public class TropicalFishEntityRenderState extends LivingEntityRenderState {
	public TropicalFishEntity.Variety variety = TropicalFishEntity.Variety.FLOPPER;
	public int baseColor = -1;
	public int patternColor = -1;
}
