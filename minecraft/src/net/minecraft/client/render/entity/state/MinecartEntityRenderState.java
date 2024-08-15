package net.minecraft.client.render.entity.state;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class MinecartEntityRenderState extends EntityRenderState {
	public float lerpedPitch;
	public float lerpedYaw;
	public long hash;
	public int damageWobbleSide;
	public float damageWobbleTicks;
	public float damageWobbleStrength;
	public int blockOffset;
	public BlockState containedBlock = Blocks.AIR.getDefaultState();
	public boolean usesExperimentalController;
	@Nullable
	public Vec3d lerpedPos;
	@Nullable
	public Vec3d presentPos;
	@Nullable
	public Vec3d futurePos;
	@Nullable
	public Vec3d pastPos;
}
