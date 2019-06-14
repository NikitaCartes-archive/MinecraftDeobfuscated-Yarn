package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
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

public class WallRedstoneTorchBlock extends RedstoneTorchBlock {
	public static final DirectionProperty field_11443 = HorizontalFacingBlock.field_11177;
	public static final BooleanProperty field_11444 = RedstoneTorchBlock.field_11446;

	protected WallRedstoneTorchBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11443, Direction.field_11043).method_11657(field_11444, Boolean.valueOf(true)));
	}

	@Override
	public String getTranslationKey() {
		return this.asItem().getTranslationKey();
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return WallTorchBlock.method_10841(blockState);
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return Blocks.field_10099.method_9558(blockState, viewableWorld, blockPos);
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return Blocks.field_10099.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = Blocks.field_10099.method_9605(itemPlacementContext);
		return blockState == null ? null : this.method_9564().method_11657(field_11443, blockState.method_11654(field_11443));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.method_11654(field_11444)) {
			Direction direction = ((Direction)blockState.method_11654(field_11443)).getOpposite();
			double d = 0.27;
			double e = (double)blockPos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2 + 0.27 * (double)direction.getOffsetX();
			double f = (double)blockPos.getY() + 0.7 + (random.nextDouble() - 0.5) * 0.2 + 0.22;
			double g = (double)blockPos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2 + 0.27 * (double)direction.getOffsetZ();
			world.addParticle(DustParticleEffect.RED, e, f, g, 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected boolean method_10488(World world, BlockPos blockPos, BlockState blockState) {
		Direction direction = ((Direction)blockState.method_11654(field_11443)).getOpposite();
		return world.isEmittingRedstonePower(blockPos.offset(direction), direction);
	}

	@Override
	public int method_9524(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.method_11654(field_11444) && blockState.method_11654(field_11443) != direction ? 15 : 0;
	}

	@Override
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		return Blocks.field_10099.method_9598(blockState, blockRotation);
	}

	@Override
	public BlockState method_9569(BlockState blockState, BlockMirror blockMirror) {
		return Blocks.field_10099.method_9569(blockState, blockMirror);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11443, field_11444);
	}
}
