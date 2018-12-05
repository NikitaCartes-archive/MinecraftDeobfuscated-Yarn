package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SweetBerryBushBlock extends PlantBlock implements Fertilizable {
	public static final IntegerProperty AGE = Properties.AGE_3;
	private static final VoxelShape SMALL_SHAPE = Block.createCubeShape(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);
	private static final VoxelShape LARGE_SHAPE = Block.createCubeShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

	public SweetBerryBushBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(AGE, Integer.valueOf(0)));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Items.field_16998);
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		if ((Integer)blockState.get(AGE) == 0) {
			return SMALL_SHAPE;
		} else {
			return blockState.get(AGE) < 3 ? LARGE_SHAPE : super.getBoundingShape(blockState, blockView, blockPos);
		}
	}

	@Override
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		super.scheduledTick(blockState, world, blockPos, random);
		int i = (Integer)blockState.get(AGE);
		if (i < 3 && random.nextInt(5) == 0 && world.method_8624(blockPos.up(), 0) >= 9) {
			world.setBlockState(blockPos, blockState.with(AGE, Integer.valueOf(i + 1)), 2);
		}
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		entity.slowMovement(blockState, 0.8F, 0.75F, 0.8F);
		if (!world.isRemote && (Integer)blockState.get(AGE) > 0 && (entity.prevRenderX != entity.x || entity.prevRenderZ != entity.z)) {
			double d = Math.abs(entity.x - entity.prevRenderX);
			double e = Math.abs(entity.z - entity.prevRenderZ);
			if (d >= 0.003F || e >= 0.003F) {
				entity.damage(DamageSource.SWEET_BERRY_BUSH, 1.0F);
			}
		}
	}

	@Override
	public boolean method_9534(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		int i = (Integer)blockState.get(AGE);
		boolean bl = i == 3;
		if (!bl && playerEntity.getStackInHand(hand).getItem() == Items.field_8324) {
			return false;
		} else if (i > 1) {
			int j = world.random.nextInt(2) + (bl ? 2 : 1);
			dropStack(world, blockPos, new ItemStack(Items.field_16998, j));
			world.setBlockState(blockPos, blockState.with(AGE, Integer.valueOf(1)), 2);
			return true;
		} else {
			return super.method_9534(blockState, world, blockPos, playerEntity, hand, direction, f, g, h);
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(AGE);
	}

	@Override
	public boolean isFertilizable(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
		return (Integer)blockState.get(AGE) < 3;
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		return true;
	}

	@Override
	public void grow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		int i = Math.min(3, (Integer)blockState.get(AGE) + 1);
		world.setBlockState(blockPos, blockState.with(AGE, Integer.valueOf(i)), 2);
	}
}
