package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.DyeColor;

@Environment(EnvType.CLIENT)
public class SheepEntityRenderState extends LivingEntityRenderState {
	public float neckAngle;
	public float headAngle;
	public boolean sheared;
	public DyeColor color = DyeColor.WHITE;
	public int id;
}
