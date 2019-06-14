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
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.MaterialPredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class CarvedPumpkinBlock extends HorizontalFacingBlock {
	public static final DirectionProperty field_10748 = HorizontalFacingBlock.field_11177;
	@Nullable
	private BlockPattern field_10749;
	@Nullable
	private BlockPattern field_10750;
	@Nullable
	private BlockPattern field_10752;
	@Nullable
	private BlockPattern field_10753;
	private static final Predicate<BlockState> IS_PUMPKIN_PREDICATE = blockState -> blockState != null
			&& (blockState.getBlock() == Blocks.field_10147 || blockState.getBlock() == Blocks.field_10009);

	protected CarvedPumpkinBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10748, Direction.field_11043));
	}

	@Override
	public void method_9615(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState2.getBlock() != blockState.getBlock()) {
			this.trySpawnEntity(world, blockPos);
		}
	}

	public boolean canDispense(ViewableWorld viewableWorld, BlockPos blockPos) {
		return this.method_9732().searchAround(viewableWorld, blockPos) != null || this.method_9727().searchAround(viewableWorld, blockPos) != null;
	}

	private void trySpawnEntity(World world, BlockPos blockPos) {
		BlockPattern.Result result = this.method_9729().searchAround(world, blockPos);
		if (result != null) {
			for (int i = 0; i < this.method_9729().getHeight(); i++) {
				CachedBlockPosition cachedBlockPosition = result.translate(0, i, 0);
				world.method_8652(cachedBlockPosition.getBlockPos(), Blocks.field_10124.method_9564(), 2);
				world.playLevelEvent(2001, cachedBlockPosition.getBlockPos(), Block.method_9507(cachedBlockPosition.getBlockState()));
			}

			SnowGolemEntity snowGolemEntity = EntityType.field_6047.method_5883(world);
			BlockPos blockPos2 = result.translate(0, 2, 0).getBlockPos();
			snowGolemEntity.setPositionAndAngles((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.05, (double)blockPos2.getZ() + 0.5, 0.0F, 0.0F);
			world.spawnEntity(snowGolemEntity);

			for (ServerPlayerEntity serverPlayerEntity : world.method_18467(ServerPlayerEntity.class, snowGolemEntity.method_5829().expand(5.0))) {
				Criterions.SUMMONED_ENTITY.handle(serverPlayerEntity, snowGolemEntity);
			}

			for (int j = 0; j < this.method_9729().getHeight(); j++) {
				CachedBlockPosition cachedBlockPosition2 = result.translate(0, j, 0);
				world.method_8408(cachedBlockPosition2.getBlockPos(), Blocks.field_10124);
			}
		} else {
			result = this.method_9730().searchAround(world, blockPos);
			if (result != null) {
				for (int i = 0; i < this.method_9730().getWidth(); i++) {
					for (int k = 0; k < this.method_9730().getHeight(); k++) {
						CachedBlockPosition cachedBlockPosition3 = result.translate(i, k, 0);
						world.method_8652(cachedBlockPosition3.getBlockPos(), Blocks.field_10124.method_9564(), 2);
						world.playLevelEvent(2001, cachedBlockPosition3.getBlockPos(), Block.method_9507(cachedBlockPosition3.getBlockState()));
					}
				}

				BlockPos blockPos3 = result.translate(1, 2, 0).getBlockPos();
				IronGolemEntity ironGolemEntity = EntityType.field_6147.method_5883(world);
				ironGolemEntity.setPlayerCreated(true);
				ironGolemEntity.setPositionAndAngles((double)blockPos3.getX() + 0.5, (double)blockPos3.getY() + 0.05, (double)blockPos3.getZ() + 0.5, 0.0F, 0.0F);
				world.spawnEntity(ironGolemEntity);

				for (ServerPlayerEntity serverPlayerEntity : world.method_18467(ServerPlayerEntity.class, ironGolemEntity.method_5829().expand(5.0))) {
					Criterions.SUMMONED_ENTITY.handle(serverPlayerEntity, ironGolemEntity);
				}

				for (int j = 0; j < this.method_9730().getWidth(); j++) {
					for (int l = 0; l < this.method_9730().getHeight(); l++) {
						CachedBlockPosition cachedBlockPosition4 = result.translate(j, l, 0);
						world.method_8408(cachedBlockPosition4.getBlockPos(), Blocks.field_10124);
					}
				}
			}
		}
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return this.method_9564().method_11657(field_10748, itemPlacementContext.getPlayerFacing().getOpposite());
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_10748);
	}

	private BlockPattern method_9732() {
		if (this.field_10749 == null) {
			this.field_10749 = BlockPatternBuilder.start()
				.aisle(" ", "#", "#")
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.field_10491)))
				.build();
		}

		return this.field_10749;
	}

	private BlockPattern method_9729() {
		if (this.field_10750 == null) {
			this.field_10750 = BlockPatternBuilder.start()
				.aisle("^", "#", "#")
				.where('^', CachedBlockPosition.matchesBlockState(IS_PUMPKIN_PREDICATE))
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.field_10491)))
				.build();
		}

		return this.field_10750;
	}

	private BlockPattern method_9727() {
		if (this.field_10752 == null) {
			this.field_10752 = BlockPatternBuilder.start()
				.aisle("~ ~", "###", "~#~")
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.field_10085)))
				.where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.method_11746(Material.AIR)))
				.build();
		}

		return this.field_10752;
	}

	private BlockPattern method_9730() {
		if (this.field_10753 == null) {
			this.field_10753 = BlockPatternBuilder.start()
				.aisle("~^~", "###", "~#~")
				.where('^', CachedBlockPosition.matchesBlockState(IS_PUMPKIN_PREDICATE))
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.field_10085)))
				.where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.method_11746(Material.AIR)))
				.build();
		}

		return this.field_10753;
	}
}
