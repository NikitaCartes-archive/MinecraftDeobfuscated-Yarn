package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class StemBlock extends PlantBlock implements Fertilizable {
	public static final IntProperty AGE = Properties.AGE_7;
	protected static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 2.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 4.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 6.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 8.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 10.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 12.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 14.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 16.0, 9.0)
	};
	private final GourdBlock gourdBlock;

	protected StemBlock(GourdBlock gourdBlock, Block.Settings settings) {
		super(settings);
		this.gourdBlock = gourdBlock;
		this.setDefaultState(this.stateFactory.getDefaultState().with(AGE, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return AGE_TO_SHAPE[blockState.get(AGE)];
	}

	@Override
	protected boolean canPlantOnTop(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.getBlock() == Blocks.FARMLAND;
	}

	@Override
	public void scheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
		super.scheduledTick(blockState, serverWorld, blockPos, random);
		if (serverWorld.getBaseLightLevel(blockPos, 0) >= 9) {
			float f = CropBlock.getAvailableMoisture(this, serverWorld, blockPos);
			if (random.nextInt((int)(25.0F / f) + 1) == 0) {
				int i = (Integer)blockState.get(AGE);
				if (i < 7) {
					blockState = blockState.with(AGE, Integer.valueOf(i + 1));
					serverWorld.setBlockState(blockPos, blockState, 2);
				} else {
					Direction direction = Direction.Type.HORIZONTAL.random(random);
					BlockPos blockPos2 = blockPos.offset(direction);
					Block block = serverWorld.getBlockState(blockPos2.method_10074()).getBlock();
					if (serverWorld.getBlockState(blockPos2).isAir()
						&& (block == Blocks.FARMLAND || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.GRASS_BLOCK)) {
						serverWorld.setBlockState(blockPos2, this.gourdBlock.getDefaultState());
						serverWorld.setBlockState(blockPos, this.gourdBlock.getAttachedStem().getDefaultState().with(HorizontalFacingBlock.FACING, direction));
					}
				}
			}
		}
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	protected Item getPickItem() {
		if (this.gourdBlock == Blocks.PUMPKIN) {
			return Items.PUMPKIN_SEEDS;
		} else {
			return this.gourdBlock == Blocks.MELON ? Items.MELON_SEEDS : null;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		Item item = this.getPickItem();
		return item == null ? ItemStack.EMPTY : new ItemStack(item);
	}

	@Override
	public boolean isFertilizable(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
		return (Integer)blockState.get(AGE) != 7;
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		return true;
	}

	@Override
	public void grow(ServerWorld serverWorld, Random random, BlockPos blockPos, BlockState blockState) {
		int i = Math.min(7, (Integer)blockState.get(AGE) + MathHelper.nextInt(serverWorld.random, 2, 5));
		BlockState blockState2 = blockState.with(AGE, Integer.valueOf(i));
		serverWorld.setBlockState(blockPos, blockState2, 2);
		if (i == 7) {
			blockState2.scheduledTick(serverWorld, blockPos, serverWorld.random);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}

	public GourdBlock getGourdBlock() {
		return this.gourdBlock;
	}
}
