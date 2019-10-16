package net.minecraft.block;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.MaterialPredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class CarvedPumpkinBlock extends HorizontalFacingBlock {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	@Nullable
	private BlockPattern snowGolemDispenserPattern;
	@Nullable
	private BlockPattern snowGolemPattern;
	@Nullable
	private BlockPattern ironGolemDispenserPattern;
	@Nullable
	private BlockPattern ironGolemPattern;
	private static final Predicate<BlockState> IS_PUMPKIN_PREDICATE = blockState -> blockState != null
			&& (blockState.getBlock() == Blocks.CARVED_PUMPKIN || blockState.getBlock() == Blocks.JACK_O_LANTERN);

	protected CarvedPumpkinBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState2.getBlock() != blockState.getBlock()) {
			this.trySpawnEntity(world, blockPos);
		}
	}

	public boolean canDispense(WorldView worldView, BlockPos blockPos) {
		return this.getSnowGolemDispenserPattern().searchAround(worldView, blockPos) != null
			|| this.getIronGolemDispenserPattern().searchAround(worldView, blockPos) != null;
	}

	private void trySpawnEntity(World world, BlockPos blockPos) {
		BlockPattern.Result result = this.getSnowGolemPattern().searchAround(world, blockPos);
		if (result != null) {
			for (int i = 0; i < this.getSnowGolemPattern().getHeight(); i++) {
				CachedBlockPosition cachedBlockPosition = result.translate(0, i, 0);
				world.setBlockState(cachedBlockPosition.getBlockPos(), Blocks.AIR.getDefaultState(), 2);
				world.playLevelEvent(2001, cachedBlockPosition.getBlockPos(), Block.getRawIdFromState(cachedBlockPosition.getBlockState()));
			}

			SnowGolemEntity snowGolemEntity = EntityType.SNOW_GOLEM.create(world);
			BlockPos blockPos2 = result.translate(0, 2, 0).getBlockPos();
			snowGolemEntity.setPositionAndAngles((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.05, (double)blockPos2.getZ() + 0.5, 0.0F, 0.0F);
			world.spawnEntity(snowGolemEntity);

			for (ServerPlayerEntity serverPlayerEntity : world.getNonSpectatingEntities(ServerPlayerEntity.class, snowGolemEntity.getBoundingBox().expand(5.0))) {
				Criterions.SUMMONED_ENTITY.trigger(serverPlayerEntity, snowGolemEntity);
			}

			for (int j = 0; j < this.getSnowGolemPattern().getHeight(); j++) {
				CachedBlockPosition cachedBlockPosition2 = result.translate(0, j, 0);
				world.updateNeighbors(cachedBlockPosition2.getBlockPos(), Blocks.AIR);
			}
		} else {
			result = this.getIronGolemPattern().searchAround(world, blockPos);
			if (result != null) {
				for (int i = 0; i < this.getIronGolemPattern().getWidth(); i++) {
					for (int k = 0; k < this.getIronGolemPattern().getHeight(); k++) {
						CachedBlockPosition cachedBlockPosition3 = result.translate(i, k, 0);
						world.setBlockState(cachedBlockPosition3.getBlockPos(), Blocks.AIR.getDefaultState(), 2);
						world.playLevelEvent(2001, cachedBlockPosition3.getBlockPos(), Block.getRawIdFromState(cachedBlockPosition3.getBlockState()));
					}
				}

				BlockPos blockPos3 = result.translate(1, 2, 0).getBlockPos();
				IronGolemEntity ironGolemEntity = EntityType.IRON_GOLEM.create(world);
				ironGolemEntity.setPlayerCreated(true);
				ironGolemEntity.setPositionAndAngles((double)blockPos3.getX() + 0.5, (double)blockPos3.getY() + 0.05, (double)blockPos3.getZ() + 0.5, 0.0F, 0.0F);
				world.spawnEntity(ironGolemEntity);

				for (ServerPlayerEntity serverPlayerEntity : world.getNonSpectatingEntities(ServerPlayerEntity.class, ironGolemEntity.getBoundingBox().expand(5.0))) {
					Criterions.SUMMONED_ENTITY.trigger(serverPlayerEntity, ironGolemEntity);
				}

				for (int j = 0; j < this.getIronGolemPattern().getWidth(); j++) {
					for (int l = 0; l < this.getIronGolemPattern().getHeight(); l++) {
						CachedBlockPosition cachedBlockPosition4 = result.translate(j, l, 0);
						world.updateNeighbors(cachedBlockPosition4.getBlockPos(), Blocks.AIR);
					}
				}
			}
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(FACING, itemPlacementContext.getPlayerFacing().getOpposite());
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	private BlockPattern getSnowGolemDispenserPattern() {
		if (this.snowGolemDispenserPattern == null) {
			this.snowGolemDispenserPattern = BlockPatternBuilder.start()
				.aisle(" ", "#", "#")
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK)))
				.build();
		}

		return this.snowGolemDispenserPattern;
	}

	private BlockPattern getSnowGolemPattern() {
		if (this.snowGolemPattern == null) {
			this.snowGolemPattern = BlockPatternBuilder.start()
				.aisle("^", "#", "#")
				.where('^', CachedBlockPosition.matchesBlockState(IS_PUMPKIN_PREDICATE))
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK)))
				.build();
		}

		return this.snowGolemPattern;
	}

	private BlockPattern getIronGolemDispenserPattern() {
		if (this.ironGolemDispenserPattern == null) {
			this.ironGolemDispenserPattern = BlockPatternBuilder.start()
				.aisle("~ ~", "###", "~#~")
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK)))
				.where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR)))
				.build();
		}

		return this.ironGolemDispenserPattern;
	}

	private BlockPattern getIronGolemPattern() {
		if (this.ironGolemPattern == null) {
			this.ironGolemPattern = BlockPatternBuilder.start()
				.aisle("~^~", "###", "~#~")
				.where('^', CachedBlockPosition.matchesBlockState(IS_PUMPKIN_PREDICATE))
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK)))
				.where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR)))
				.build();
		}

		return this.ironGolemPattern;
	}

	@Override
	public boolean allowsSpawning(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityType<?> entityType) {
		return true;
	}
}
