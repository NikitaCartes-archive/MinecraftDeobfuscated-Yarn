package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class IllusionerEntityRenderState extends IllagerEntityRenderState {
	public Vec3d[] mirrorCopyOffsets = new Vec3d[0];
	public boolean spellcasting;
}
