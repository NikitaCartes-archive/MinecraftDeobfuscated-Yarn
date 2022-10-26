package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.function.MaterialPredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class WitherSkullBlock extends SkullBlock {
	@Nullable
	private static BlockPattern witherBossPattern;
	@Nullable
	private static BlockPattern witherDispenserPattern;

	protected WitherSkullBlock(AbstractBlock.Settings settings) {
		super(SkullBlock.Type.WITHER_SKELETON, settings);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof SkullBlockEntity) {
			onPlaced(world, pos, (SkullBlockEntity)blockEntity);
		}
	}

	public static void onPlaced(World world, BlockPos pos, SkullBlockEntity blockEntity) {
		if (!world.isClient) {
			BlockState blockState = blockEntity.getCachedState();
			boolean bl = blockState.isOf(Blocks.WITHER_SKELETON_SKULL) || blockState.isOf(Blocks.WITHER_SKELETON_WALL_SKULL);
			if (bl && pos.getY() >= world.getBottomY() && world.getDifficulty() != Difficulty.PEACEFUL) {
				BlockPattern.Result result = getWitherBossPattern().searchAround(world, pos);
				if (result != null) {
					WitherEntity witherEntity = EntityType.WITHER.create(world);
					if (witherEntity != null) {
						CarvedPumpkinBlock.breakPatternBlocks(world, result);
						BlockPos blockPos = result.translate(1, 2, 0).getBlockPos();
						witherEntity.refreshPositionAndAngles(
							(double)blockPos.getX() + 0.5,
							(double)blockPos.getY() + 0.55,
							(double)blockPos.getZ() + 0.5,
							result.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F,
							0.0F
						);
						witherEntity.bodyYaw = result.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F;
						witherEntity.onSummoned();

						for (ServerPlayerEntity serverPlayerEntity : world.getNonSpectatingEntities(ServerPlayerEntity.class, witherEntity.getBoundingBox().expand(50.0))) {
							Criteria.SUMMONED_ENTITY.trigger(serverPlayerEntity, witherEntity);
						}

						world.spawnEntity(witherEntity);
						CarvedPumpkinBlock.updatePatternBlocks(world, result);
					}
				}
			}
		}
	}

	public static boolean canDispense(World world, BlockPos pos, ItemStack stack) {
		return stack.isOf(Items.WITHER_SKELETON_SKULL) && pos.getY() >= world.getBottomY() + 2 && world.getDifficulty() != Difficulty.PEACEFUL && !world.isClient
			? getWitherDispenserPattern().searchAround(world, pos) != null
			: false;
	}

	private static BlockPattern getWitherBossPattern() {
		if (witherBossPattern == null) {
			witherBossPattern = BlockPatternBuilder.start()
				.aisle("^^^", "###", "~#~")
				.where('#', pos -> pos.getBlockState().isIn(BlockTags.WITHER_SUMMON_BASE_BLOCKS))
				.where(
					'^',
					CachedBlockPosition.matchesBlockState(
						BlockStatePredicate.forBlock(Blocks.WITHER_SKELETON_SKULL).or(BlockStatePredicate.forBlock(Blocks.WITHER_SKELETON_WALL_SKULL))
					)
				)
				.where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR)))
				.build();
		}

		return witherBossPattern;
	}

	private static BlockPattern getWitherDispenserPattern() {
		if (witherDispenserPattern == null) {
			witherDispenserPattern = BlockPatternBuilder.start()
				.aisle("   ", "###", "~#~")
				.where('#', pos -> pos.getBlockState().isIn(BlockTags.WITHER_SUMMON_BASE_BLOCKS))
				.where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR)))
				.build();
		}

		return witherDispenserPattern;
	}
}
