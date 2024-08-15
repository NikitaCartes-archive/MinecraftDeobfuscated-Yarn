package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.PandaEntity;

@Environment(EnvType.CLIENT)
public class PandaEntityRenderState extends LivingEntityRenderState {
	public PandaEntity.Gene gene = PandaEntity.Gene.NORMAL;
	public boolean askingForBamboo;
	public boolean sneezing;
	public int sneezeProgress;
	public boolean eating;
	public boolean scaredByThunderstorm;
	public boolean sitting;
	public float sittingAnimationProgress;
	public float lieOnBackAnimationProgress;
	public float rollOverAnimationProgress;
	public float playingTicks;
}
