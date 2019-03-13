package net.minecraft.block;

import com.google.common.base.Predicates;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EndPortalFrameBlock extends Block {
	public static final DirectionProperty field_10954 = HorizontalFacingBlock.field_11177;
	public static final BooleanProperty field_10958 = Properties.field_12488;
	protected static final VoxelShape field_10956 = Block.method_9541(0.0, 0.0, 0.0, 16.0, 13.0, 16.0);
	protected static final VoxelShape field_10953 = Block.method_9541(4.0, 13.0, 4.0, 12.0, 16.0, 12.0);
	protected static final VoxelShape field_10955 = VoxelShapes.method_1084(field_10956, field_10953);
	private static BlockPattern field_10957;

	public EndPortalFrameBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10954, Direction.NORTH).method_11657(field_10958, Boolean.valueOf(false)));
	}

	@Override
	public boolean method_9526(BlockState blockState) {
		return true;
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return blockState.method_11654(field_10958) ? field_10955 : field_10956;
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return this.method_9564().method_11657(field_10954, itemPlacementContext.method_8042().getOpposite()).method_11657(field_10958, Boolean.valueOf(false));
	}

	@Override
	public boolean method_9498(BlockState blockState) {
		return true;
	}

	@Override
	public int method_9572(BlockState blockState, World world, BlockPos blockPos) {
		return blockState.method_11654(field_10958) ? 15 : 0;
	}

	@Override
	public BlockState method_9598(BlockState blockState, Rotation rotation) {
		return blockState.method_11657(field_10954, rotation.method_10503(blockState.method_11654(field_10954)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, Mirror mirror) {
		return blockState.rotate(mirror.method_10345(blockState.method_11654(field_10954)));
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_10954, field_10958);
	}

	public static BlockPattern method_10054() {
		if (field_10957 == null) {
			field_10957 = BlockPatternBuilder.start()
				.aisle("?vvv?", ">???<", ">???<", ">???<", "?^^^?")
				.where('?', CachedBlockPosition.matchesBlockState(BlockStatePredicate.ANY))
				.where(
					'^',
					CachedBlockPosition.matchesBlockState(
						BlockStatePredicate.forBlock(Blocks.field_10398)
							.method_11762(field_10958, Predicates.equalTo(true))
							.method_11762(field_10954, Predicates.equalTo(Direction.SOUTH))
					)
				)
				.where(
					'>',
					CachedBlockPosition.matchesBlockState(
						BlockStatePredicate.forBlock(Blocks.field_10398)
							.method_11762(field_10958, Predicates.equalTo(true))
							.method_11762(field_10954, Predicates.equalTo(Direction.WEST))
					)
				)
				.where(
					'v',
					CachedBlockPosition.matchesBlockState(
						BlockStatePredicate.forBlock(Blocks.field_10398)
							.method_11762(field_10958, Predicates.equalTo(true))
							.method_11762(field_10954, Predicates.equalTo(Direction.NORTH))
					)
				)
				.where(
					'<',
					CachedBlockPosition.matchesBlockState(
						BlockStatePredicate.forBlock(Blocks.field_10398)
							.method_11762(field_10958, Predicates.equalTo(true))
							.method_11762(field_10954, Predicates.equalTo(Direction.EAST))
					)
				)
				.build();
		}

		return field_10957;
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
