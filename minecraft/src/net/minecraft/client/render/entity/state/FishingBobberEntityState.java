package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class FishingBobberEntityState extends EntityRenderState {
	public Vec3d pos = Vec3d.ZERO;
}
