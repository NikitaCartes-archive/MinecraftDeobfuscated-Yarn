package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class EndPortalBlock extends BlockWithEntity {
	protected static final VoxelShape field_10959 = Block.method_9541(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

	protected EndPortalBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity method_10123(BlockView blockView) {
		return new EndPortalBlockEntity();
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return field_10959;
	}

	@Override
	public void method_9548(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		if (!world.isClient
			&& !entity.hasVehicle()
			&& !entity.hasPassengers()
			&& entity.canUsePortals()
			&& VoxelShapes.method_1074(
				VoxelShapes.method_1078(entity.method_5829().offset((double)(-blockPos.getX()), (double)(-blockPos.getY()), (double)(-blockPos.getZ()))),
				blockState.method_17770(world, blockPos),
				BooleanBiFunction.AND
			)) {
			entity.method_5731(world.field_9247.method_12460() == DimensionType.field_13078 ? DimensionType.field_13072 : DimensionType.field_13078);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(BlockState blockState, World world, BlockPos blockPos, Random random) {
		double d = (double)((float)blockPos.getX() + random.nextFloat());
		double e = (double)((float)blockPos.getY() + 0.8F);
		double f = (double)((float)blockPos.getZ() + random.nextFloat());
		double g = 0.0;
		double h = 0.0;
		double i = 0.0;
		world.addParticle(ParticleTypes.field_11251, d, e, f, 0.0, 0.0, 0.0);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack method_9574(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return ItemStack.EMPTY;
	}
}
