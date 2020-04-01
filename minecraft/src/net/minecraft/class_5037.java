package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;

public class class_5037 extends OverworldDimension {
	private static final Vector3f field_23523 = new Vector3f(1.0F, 1.0F, 1.0F);
	private static final Vector3f field_23524 = new Vector3f(0.0F, 0.0F, 0.0F);

	public class_5037(World world, DimensionType dimensionType) {
		super(world, dimensionType);
	}

	@Environment(EnvType.CLIENT)
	private static Vector3f method_26543(BlockPos blockPos) {
		return ((blockPos.getX() ^ blockPos.getY() ^ blockPos.getZ()) & 1) == 0 ? field_23523 : field_23524;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vector3f method_26495(BlockState blockState, BlockPos blockPos) {
		return method_26543(blockPos);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public <T extends LivingEntity> Vector3f method_26494(T livingEntity) {
		return method_26543(livingEntity.getBlockPos());
	}
}
