package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class PotatoesBlock extends CropBlock {
	public static final MapCodec<PotatoesBlock> CODEC = createCodec(PotatoesBlock::new);
	public static final IntProperty field_50866 = IntProperty.of("tater_boost", 0, 2);
	private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 3.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 5.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 7.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 9.0, 16.0)
	};

	@Override
	public MapCodec<PotatoesBlock> getCodec() {
		return CODEC;
	}

	public PotatoesBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(field_50866);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		World world = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos().method_59389(Direction.DOWN);
		BlockState blockState = world.getBlockState(blockPos);
		BlockState blockState2 = super.getPlacementState(ctx);
		return blockState2 == null ? null : method_59136(blockState2, blockState);
	}

	@Override
	protected ItemConvertible getSeedsItem() {
		return Items.POTATO;
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return AGE_TO_SHAPE[this.getAge(state)];
	}

	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
		return floor.isIn(BlockTags.GROWS_POTATOES);
	}

	@Override
	protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
		if (pos.method_59389(Direction.DOWN).equals(sourcePos)) {
			BlockState blockState = world.getBlockState(sourcePos);
			BlockState blockState2 = world.getBlockState(pos);
			if (blockState2.getBlock() instanceof PotatoesBlock) {
				world.setBlockState(pos, method_59136(blockState2, blockState), Block.NOTIFY_ALL);
			}
		}
	}

	@Override
	public BlockState withAge(int age, BlockState blockState) {
		return blockState.with(this.getAgeProperty(), Integer.valueOf(age));
	}

	public static BlockState method_59136(BlockState blockState, BlockState blockState2) {
		return blockState.with(field_50866, Integer.valueOf(method_59137(blockState2)));
	}

	@Override
	protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		if (world.isPotato()) {
			BlockPos blockPos = pos.down();
			return this.canPlantOnTop(world.getBlockState(blockPos), world, blockPos);
		} else {
			return super.canPlaceAt(state, world, pos);
		}
	}

	private static int method_59137(BlockState blockState) {
		if (blockState.isOf(Blocks.PEELGRASS_BLOCK)) {
			return 1;
		} else {
			return blockState.isOf(Blocks.CORRUPTED_PEELGRASS_BLOCK) ? 2 : 0;
		}
	}
}
