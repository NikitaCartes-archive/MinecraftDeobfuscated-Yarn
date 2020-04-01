package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;

public class class_5039 extends OverworldDimension {
	public class_5039(World world, DimensionType dimensionType) {
		super(world, dimensionType);
	}

	@Environment(EnvType.CLIENT)
	private static Vector3f method_26544(BlockPos blockPos) {
		double d = (double)blockPos.getX();
		double e = (double)blockPos.getZ();
		double f = Math.sqrt(d * d + e * e);
		float g = (float)MathHelper.clamp(1.0 - MathHelper.getLerpProgress(f, 50.0, 100.0), 0.0, 1.0);
		return new Vector3f(g, g, g);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vector3f method_26495(BlockState blockState, BlockPos blockPos) {
		return method_26544(blockPos);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public <T extends LivingEntity> Vector3f method_26494(T livingEntity) {
		return method_26544(livingEntity.getBlockPos());
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vec3d modifyFogColor(Vec3d vec3d, float tickDelta) {
		return Vec3d.ZERO;
	}
}
