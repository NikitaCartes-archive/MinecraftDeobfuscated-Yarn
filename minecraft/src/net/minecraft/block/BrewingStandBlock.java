package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sortme.ItemScatterer;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BrewingStandBlock extends BlockWithEntity {
	public static final BooleanProperty[] BOTTLE_PROPERTIES = new BooleanProperty[]{Properties.HAS_BOTTLE_0, Properties.HAS_BOTTLE_1, Properties.HAS_BOTTLE_2};
	protected static final VoxelShape field_10701 = VoxelShapes.union(
		Block.createCubeShape(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), Block.createCubeShape(7.0, 0.0, 7.0, 9.0, 14.0, 9.0)
	);

	public BrewingStandBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory
				.getDefaultState()
				.with(BOTTLE_PROPERTIES[0], Boolean.valueOf(false))
				.with(BOTTLE_PROPERTIES[1], Boolean.valueOf(false))
				.with(BOTTLE_PROPERTIES[2], Boolean.valueOf(false))
		);
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new BrewingStandBlockEntity();
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return field_10701;
	}

	@Override
	public boolean activate(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof BrewingStandBlockEntity) {
				playerEntity.openInventory((BrewingStandBlockEntity)blockEntity);
				playerEntity.increaseStat(Stats.field_15407);
			}

			return true;
		}
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		if (itemStack.hasDisplayName()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof BrewingStandBlockEntity) {
				((BrewingStandBlockEntity)blockEntity).setCustomName(itemStack.getDisplayName());
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		double d = (double)((float)blockPos.getX() + 0.4F + random.nextFloat() * 0.2F);
		double e = (double)((float)blockPos.getY() + 0.7F + random.nextFloat() * 0.3F);
		double f = (double)((float)blockPos.getZ() + 0.4F + random.nextFloat() * 0.2F);
		world.method_8406(ParticleTypes.field_11251, d, e, f, 0.0, 0.0, 0.0);
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof BrewingStandBlockEntity) {
				ItemScatterer.spawn(world, blockPos, (BrewingStandBlockEntity)blockEntity);
			}

			super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
		}
	}

	@Override
	public boolean hasComparatorOutput(BlockState blockState) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
		return Container.calculateComparatorOutput(world.getBlockEntity(blockPos));
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(BOTTLE_PROPERTIES[0], BOTTLE_PROPERTIES[1], BOTTLE_PROPERTIES[2]);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, PlacementEnvironment placementEnvironment) {
		return false;
	}
}
