package net.minecraft.client.render.entity.state;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class EndCrystalEntityRenderState extends EntityRenderState {
	public boolean baseVisible = true;
	@Nullable
	public Vec3d beamOffset;
}
