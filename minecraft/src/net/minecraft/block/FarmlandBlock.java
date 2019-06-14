package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class FarmlandBlock extends Block {
	public static final IntProperty field_11009 = Properties.field_12510;
	protected static final VoxelShape field_11010 = Block.method_9541(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);

	protected FarmlandBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11009, Integer.valueOf(0)));
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if (direction == Direction.field_11036 && !blockState.canPlaceAt(iWorld, blockPos)) {
			iWorld.method_8397().schedule(blockPos, this, 1);
		}

		return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockState blockState2 = viewableWorld.method_8320(blockPos.up());
		return !blockState2.method_11620().isSolid() || blockState2.getBlock() instanceof FenceGateBlock;
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return !this.method_9564().canPlaceAt(itemPlacementContext.method_8045(), itemPlacementContext.getBlockPos())
			? Blocks.field_10566.method_9564()
			: super.method_9605(itemPlacementContext);
	}

	@Override
	public boolean method_9526(BlockState blockState) {
		return true;
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return field_11010;
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!blockState.canPlaceAt(world, blockPos)) {
			method_10125(blockState, world, blockPos);
		} else {
			int i = (Integer)blockState.method_11654(field_11009);
			if (!isWaterNearby(world, blockPos) && !world.hasRain(blockPos.up())) {
				if (i > 0) {
					world.method_8652(blockPos, blockState.method_11657(field_11009, Integer.valueOf(i - 1)), 2);
				} else if (!hasCrop(world, blockPos)) {
					method_10125(blockState, world, blockPos);
				}
			} else if (i < 7) {
				world.method_8652(blockPos, blockState.method_11657(field_11009, Integer.valueOf(7)), 2);
			}
		}
	}

	@Override
	public void onLandedUpon(World world, BlockPos blockPos, Entity entity, float f) {
		if (!world.isClient
			&& world.random.nextFloat() < f - 0.5F
			&& entity instanceof LivingEntity
			&& (entity instanceof PlayerEntity || world.getGameRules().getBoolean(GameRules.field_19388))
			&& entity.getWidth() * entity.getWidth() * entity.getHeight() > 0.512F) {
			method_10125(world.method_8320(blockPos), world, blockPos);
		}

		super.onLandedUpon(world, blockPos, entity, f);
	}

	public static void method_10125(BlockState blockState, World world, BlockPos blockPos) {
		world.method_8501(blockPos, method_9582(blockState, Blocks.field_10566.method_9564(), world, blockPos));
	}

	private static boolean hasCrop(BlockView blockView, BlockPos blockPos) {
		Block block = blockView.method_8320(blockPos.up()).getBlock();
		return block instanceof CropBlock || block instanceof StemBlock || block instanceof AttachedStemBlock;
	}

	private static boolean isWaterNearby(ViewableWorld viewableWorld, BlockPos blockPos) {
		for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-4, 0, -4), blockPos.add(4, 1, 4))) {
			if (viewableWorld.method_8316(blockPos2).matches(FluidTags.field_15517)) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11009);
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
