package net.minecraft.block;

import java.util.function.Predicate;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SnowmanEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
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
	private BlockPattern field_10749;
	private BlockPattern field_10750;
	private BlockPattern field_10752;
	private BlockPattern field_10753;
	private static final Predicate<BlockState> IS_PUMPKIN_PREDICATE = blockState -> blockState != null
			&& (blockState.getBlock() == Blocks.field_10147 || blockState.getBlock() == Blocks.field_10009);

	protected CarvedPumpkinBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10748, Direction.NORTH));
	}

	@Override
	public void method_9615(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		if (blockState2.getBlock() != blockState.getBlock()) {
			this.method_9731(world, blockPos);
		}
	}

	public boolean method_9733(ViewableWorld viewableWorld, BlockPos blockPos) {
		return this.method_9732().method_11708(viewableWorld, blockPos) != null || this.method_9727().method_11708(viewableWorld, blockPos) != null;
	}

	private void method_9731(World world, BlockPos blockPos) {
		BlockPattern.Result result = this.method_9729().method_11708(world, blockPos);
		if (result != null) {
			for (int i = 0; i < this.method_9729().getHeight(); i++) {
				CachedBlockPosition cachedBlockPosition = result.translate(0, i, 0);
				world.method_8652(cachedBlockPosition.method_11683(), Blocks.field_10124.method_9564(), 2);
			}

			SnowmanEntity snowmanEntity = EntityType.SNOW_GOLEM.method_5883(world);
			BlockPos blockPos2 = result.translate(0, 2, 0).method_11683();
			snowmanEntity.setPositionAndAngles((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.05, (double)blockPos2.getZ() + 0.5, 0.0F, 0.0F);
			world.spawnEntity(snowmanEntity);

			for (ServerPlayerEntity serverPlayerEntity : world.method_18467(ServerPlayerEntity.class, snowmanEntity.method_5829().expand(5.0))) {
				Criterions.SUMMONED_ENTITY.method_9124(serverPlayerEntity, snowmanEntity);
			}

			int j = Block.method_9507(Blocks.field_10491.method_9564());
			world.method_8535(2001, blockPos2, j);
			world.method_8535(2001, blockPos2.up(), j);

			for (int k = 0; k < this.method_9729().getHeight(); k++) {
				CachedBlockPosition cachedBlockPosition2 = result.translate(0, k, 0);
				world.method_8408(cachedBlockPosition2.method_11683(), Blocks.field_10124);
			}
		} else {
			result = this.method_9730().method_11708(world, blockPos);
			if (result != null) {
				for (int i = 0; i < this.method_9730().getWidth(); i++) {
					for (int l = 0; l < this.method_9730().getHeight(); l++) {
						world.method_8652(result.translate(i, l, 0).method_11683(), Blocks.field_10124.method_9564(), 2);
					}
				}

				BlockPos blockPos3 = result.translate(1, 2, 0).method_11683();
				IronGolemEntity ironGolemEntity = EntityType.IRON_GOLEM.method_5883(world);
				ironGolemEntity.setPlayerCreated(true);
				ironGolemEntity.setPositionAndAngles((double)blockPos3.getX() + 0.5, (double)blockPos3.getY() + 0.05, (double)blockPos3.getZ() + 0.5, 0.0F, 0.0F);
				world.spawnEntity(ironGolemEntity);

				for (ServerPlayerEntity serverPlayerEntity : world.method_18467(ServerPlayerEntity.class, ironGolemEntity.method_5829().expand(5.0))) {
					Criterions.SUMMONED_ENTITY.method_9124(serverPlayerEntity, ironGolemEntity);
				}

				for (int j = 0; j < 120; j++) {
					world.method_8406(
						ParticleTypes.field_11230,
						(double)blockPos3.getX() + world.random.nextDouble(),
						(double)blockPos3.getY() + world.random.nextDouble() * 3.9,
						(double)blockPos3.getZ() + world.random.nextDouble(),
						0.0,
						0.0,
						0.0
					);
				}

				for (int j = 0; j < this.method_9730().getWidth(); j++) {
					for (int k = 0; k < this.method_9730().getHeight(); k++) {
						CachedBlockPosition cachedBlockPosition2 = result.translate(j, k, 0);
						world.method_8408(cachedBlockPosition2.method_11683(), Blocks.field_10124);
					}
				}
			}
		}
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return this.method_9564().method_11657(field_10748, itemPlacementContext.method_8042().getOpposite());
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_10748);
	}

	protected BlockPattern method_9732() {
		if (this.field_10749 == null) {
			this.field_10749 = BlockPatternBuilder.start()
				.aisle(" ", "#", "#")
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.field_10491)))
				.build();
		}

		return this.field_10749;
	}

	protected BlockPattern method_9729() {
		if (this.field_10750 == null) {
			this.field_10750 = BlockPatternBuilder.start()
				.aisle("^", "#", "#")
				.where('^', CachedBlockPosition.matchesBlockState(IS_PUMPKIN_PREDICATE))
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.field_10491)))
				.build();
		}

		return this.field_10750;
	}

	protected BlockPattern method_9727() {
		if (this.field_10752 == null) {
			this.field_10752 = BlockPatternBuilder.start()
				.aisle("~ ~", "###", "~#~")
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.field_10085)))
				.where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.method_11746(Material.AIR)))
				.build();
		}

		return this.field_10752;
	}

	protected BlockPattern method_9730() {
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
