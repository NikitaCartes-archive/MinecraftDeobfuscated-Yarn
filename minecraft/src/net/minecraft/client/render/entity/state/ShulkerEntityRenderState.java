package net.minecraft.client.render.entity.state;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ShulkerEntityRenderState extends LivingEntityRenderState {
	public Vec3d renderPositionOffset = Vec3d.ZERO;
	@Nullable
	public DyeColor color;
	public float openProgress;
	public float headYaw;
	public float shellYaw;
	public Direction facing = Direction.DOWN;
}
