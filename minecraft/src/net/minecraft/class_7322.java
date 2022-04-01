package net.minecraft;

import com.google.common.base.Predicates;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.ConfiguredStructureFeatureTags;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatureKeys;

public class class_7322 extends Block {
	public static final DirectionProperty field_38553 = Properties.FACING;
	protected static final VoxelShape field_38554 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 13.0, 16.0);
	protected static final VoxelShape field_38555 = Block.createCuboidShape(4.0, 13.0, 4.0, 12.0, 16.0, 12.0);
	protected static final VoxelShape field_38556 = VoxelShapes.union(field_38554, field_38555);
	@Nullable
	private static BlockPattern field_38557;

	public class_7322(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(field_38553, Direction.NORTH));
	}

	@Override
	public boolean hasSidedTransparency(BlockState state) {
		return true;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		World world = ctx.getWorld();
		if (world instanceof ServerWorld serverWorld) {
			BlockPos blockPos = ctx.getBlockPos();
			if (serverWorld.getStructureAccessor().method_41034(blockPos, ConfiguredStructureFeatureKeys.STRONGHOLD).hasChildren()) {
				return this.getDefaultState().with(field_38553, Direction.UP);
			}

			BlockPos blockPos2 = serverWorld.locateStructure(ConfiguredStructureFeatureTags.EYE_OF_ENDER_LOCATED, blockPos, 100, false);
			if (blockPos2 != null) {
				BlockPos blockPos3 = blockPos2.subtract(blockPos);
				Direction direction = Direction.getFacing((float)blockPos3.getX(), (float)blockPos3.getY(), (float)blockPos3.getZ());
				return this.getDefaultState().with(field_38553, direction);
			}
		}

		return this.getDefaultState().with(field_38553, Direction.random(world.random));
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);
		BlockPattern.Result result = method_42875().searchAround(world, pos);
		if (result != null) {
			BlockPos blockPos = result.getFrontTopLeft().add(-3, 0, -3);

			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					world.setBlockState(blockPos.add(i, 0, j), Blocks.END_PORTAL.getDefaultState(), Block.NOTIFY_LISTENERS);
				}
			}

			world.syncGlobalEvent(WorldEvents.END_PORTAL_OPENED, blockPos.add(1, 0, 1), 0);
		}
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(field_38553, rotation.rotate(state.get(field_38553)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(field_38553)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(field_38553);
	}

	public static BlockPattern method_42875() {
		if (field_38557 == null) {
			field_38557 = BlockPatternBuilder.start()
				.aisle("?xxx?", "x???x", "x???x", "x???x", "?xxx?")
				.where('?', CachedBlockPosition.matchesBlockState(BlockStatePredicate.ANY))
				.where(
					'x', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.END_PORTAL_FRAME).with(field_38553, Predicates.equalTo(Direction.UP)))
				)
				.build();
		}

		return field_38557;
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}
}
