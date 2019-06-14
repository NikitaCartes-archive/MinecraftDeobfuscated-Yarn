package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class AttachedStemBlock extends PlantBlock {
	public static final DirectionProperty field_9873 = HorizontalFacingBlock.field_11177;
	private final GourdBlock field_9875;
	private static final Map<Direction, VoxelShape> FACING_TO_SHAPE = Maps.newEnumMap(
		ImmutableMap.of(
			Direction.field_11035,
			Block.method_9541(6.0, 0.0, 6.0, 10.0, 10.0, 16.0),
			Direction.field_11039,
			Block.method_9541(0.0, 0.0, 6.0, 10.0, 10.0, 10.0),
			Direction.field_11043,
			Block.method_9541(6.0, 0.0, 0.0, 10.0, 10.0, 10.0),
			Direction.field_11034,
			Block.method_9541(6.0, 0.0, 6.0, 16.0, 10.0, 10.0)
		)
	);

	protected AttachedStemBlock(GourdBlock gourdBlock, Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_9873, Direction.field_11043));
		this.field_9875 = gourdBlock;
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return (VoxelShape)FACING_TO_SHAPE.get(blockState.method_11654(field_9873));
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return blockState2.getBlock() != this.field_9875 && direction == blockState.method_11654(field_9873)
			? this.field_9875.getStem().method_9564().method_11657(StemBlock.field_11584, Integer.valueOf(7))
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	protected boolean method_9695(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.getBlock() == Blocks.field_10362;
	}

	@Environment(EnvType.CLIENT)
	protected Item getSeeds() {
		if (this.field_9875 == Blocks.field_10261) {
			return Items.field_8706;
		} else {
			return this.field_9875 == Blocks.field_10545 ? Items.field_8188 : Items.AIR;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack method_9574(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(this.getSeeds());
	}

	@Override
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		return blockState.method_11657(field_9873, blockRotation.rotate(blockState.method_11654(field_9873)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, BlockMirror blockMirror) {
		return blockState.rotate(blockMirror.method_10345(blockState.method_11654(field_9873)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_9873);
	}
}
