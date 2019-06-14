package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class WallTorchBlock extends TorchBlock {
	public static final DirectionProperty field_11731 = HorizontalFacingBlock.field_11177;
	private static final Map<Direction, VoxelShape> BOUNDING_SHAPES = Maps.newEnumMap(
		ImmutableMap.of(
			Direction.field_11043,
			Block.method_9541(5.5, 3.0, 11.0, 10.5, 13.0, 16.0),
			Direction.field_11035,
			Block.method_9541(5.5, 3.0, 0.0, 10.5, 13.0, 5.0),
			Direction.field_11039,
			Block.method_9541(11.0, 3.0, 5.5, 16.0, 13.0, 10.5),
			Direction.field_11034,
			Block.method_9541(0.0, 3.0, 5.5, 5.0, 13.0, 10.5)
		)
	);

	protected WallTorchBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11731, Direction.field_11043));
	}

	@Override
	public String getTranslationKey() {
		return this.asItem().getTranslationKey();
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return method_10841(blockState);
	}

	public static VoxelShape method_10841(BlockState blockState) {
		return (VoxelShape)BOUNDING_SHAPES.get(blockState.method_11654(field_11731));
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		Direction direction = blockState.method_11654(field_11731);
		BlockPos blockPos2 = blockPos.offset(direction.getOpposite());
		BlockState blockState2 = viewableWorld.method_8320(blockPos2);
		return Block.method_20045(blockState2, viewableWorld, blockPos2, direction);
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = this.method_9564();
		ViewableWorld viewableWorld = itemPlacementContext.method_8045();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		Direction[] directions = itemPlacementContext.getPlacementDirections();

		for (Direction direction : directions) {
			if (direction.getAxis().isHorizontal()) {
				Direction direction2 = direction.getOpposite();
				blockState = blockState.method_11657(field_11731, direction2);
				if (blockState.canPlaceAt(viewableWorld, blockPos)) {
					return blockState;
				}
			}
		}

		return null;
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return direction.getOpposite() == blockState.method_11654(field_11731) && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.method_9564()
			: blockState;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(BlockState blockState, World world, BlockPos blockPos, Random random) {
		Direction direction = blockState.method_11654(field_11731);
		double d = (double)blockPos.getX() + 0.5;
		double e = (double)blockPos.getY() + 0.7;
		double f = (double)blockPos.getZ() + 0.5;
		double g = 0.22;
		double h = 0.27;
		Direction direction2 = direction.getOpposite();
		world.addParticle(ParticleTypes.field_11251, d + 0.27 * (double)direction2.getOffsetX(), e + 0.22, f + 0.27 * (double)direction2.getOffsetZ(), 0.0, 0.0, 0.0);
		world.addParticle(ParticleTypes.field_11240, d + 0.27 * (double)direction2.getOffsetX(), e + 0.22, f + 0.27 * (double)direction2.getOffsetZ(), 0.0, 0.0, 0.0);
	}

	@Override
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		return blockState.method_11657(field_11731, blockRotation.rotate(blockState.method_11654(field_11731)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, BlockMirror blockMirror) {
		return blockState.rotate(blockMirror.method_10345(blockState.method_11654(field_11731)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11731);
	}
}
