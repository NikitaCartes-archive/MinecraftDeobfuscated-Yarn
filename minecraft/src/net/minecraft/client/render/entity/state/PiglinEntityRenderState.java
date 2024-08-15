package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.PiglinActivity;

@Environment(EnvType.CLIENT)
public class PiglinEntityRenderState extends BipedEntityRenderState {
	public boolean brute;
	public boolean shouldZombify;
	public float piglinCrossbowPullTime;
	public PiglinActivity activity = PiglinActivity.DEFAULT;
}
