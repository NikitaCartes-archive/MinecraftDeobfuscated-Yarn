package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class SaplingBambooBlock extends Block implements Fertilizable {
	protected static final VoxelShape field_9897 = Block.createCubeShape(4.0, 0.0, 4.0, 12.0, 12.0, 12.0);

	public SaplingBambooBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public Block.OffsetType getOffsetType() {
		return Block.OffsetType.XZ;
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		Vec3d vec3d = blockState.getOffsetPos(blockView, blockPos);
		return field_9897.method_1096(vec3d.x, vec3d.y, vec3d.z);
	}

	@Override
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (random.nextInt(3) == 0 && world.isAir(blockPos.up()) && world.getLightLevel(blockPos.up(), 0) >= 9) {
			this.method_9351(world, blockPos);
		}
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return viewableWorld.getBlockState(blockPos.down()).matches(BlockTags.field_15497);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (!blockState.canPlaceAt(iWorld, blockPos)) {
			return Blocks.field_10124.getDefaultState();
		} else {
			if (direction == Direction.UP && blockState2.getBlock() == Blocks.field_10211) {
				iWorld.setBlockState(blockPos, Blocks.field_10211.getDefaultState(), 2);
			}

			return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Items.field_8648);
	}

	@Override
	public boolean isFertilizable(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
		return blockView.getBlockState(blockPos.up()).isAir();
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		return true;
	}

	@Override
	public void grow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		this.method_9351(world, blockPos);
	}

	@Override
	public float calcBlockBreakingDelta(BlockState blockState, PlayerEntity playerEntity, BlockView blockView, BlockPos blockPos) {
		return playerEntity.getMainHandStack().getItem() instanceof SwordItem ? 1.0F : super.calcBlockBreakingDelta(blockState, playerEntity, blockView, blockPos);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	protected void method_9351(World world, BlockPos blockPos) {
		world.setBlockState(blockPos.up(), Blocks.field_10211.getDefaultState().with(BambooBlock.field_9917, BambooLeaves.field_12466), 3);
	}
}
