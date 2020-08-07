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
		super(SkullBlock.Type.field_11513, settings);
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
			boolean bl = blockState.isOf(Blocks.field_10177) || blockState.isOf(Blocks.field_10101);
			if (bl && pos.getY() >= 0 && world.getDifficulty() != Difficulty.field_5801) {
				BlockPattern blockPattern = getWitherBossPattern();
				BlockPattern.Result result = blockPattern.searchAround(world, pos);
				if (result != null) {
					for (int i = 0; i < blockPattern.getWidth(); i++) {
						for (int j = 0; j < blockPattern.getHeight(); j++) {
							CachedBlockPosition cachedBlockPosition = result.translate(i, j, 0);
							world.setBlockState(cachedBlockPosition.getBlockPos(), Blocks.field_10124.getDefaultState(), 2);
							world.syncWorldEvent(2001, cachedBlockPosition.getBlockPos(), Block.getRawIdFromState(cachedBlockPosition.getBlockState()));
						}
					}

					WitherEntity witherEntity = EntityType.field_6119.create(world);
					BlockPos blockPos = result.translate(1, 2, 0).getBlockPos();
					witherEntity.refreshPositionAndAngles(
						(double)blockPos.getX() + 0.5,
						(double)blockPos.getY() + 0.55,
						(double)blockPos.getZ() + 0.5,
						result.getForwards().getAxis() == Direction.Axis.field_11048 ? 0.0F : 90.0F,
						0.0F
					);
					witherEntity.bodyYaw = result.getForwards().getAxis() == Direction.Axis.field_11048 ? 0.0F : 90.0F;
					witherEntity.method_6885();

					for (ServerPlayerEntity serverPlayerEntity : world.getNonSpectatingEntities(ServerPlayerEntity.class, witherEntity.getBoundingBox().expand(50.0))) {
						Criteria.SUMMONED_ENTITY.trigger(serverPlayerEntity, witherEntity);
					}

					world.spawnEntity(witherEntity);

					for (int k = 0; k < blockPattern.getWidth(); k++) {
						for (int l = 0; l < blockPattern.getHeight(); l++) {
							world.updateNeighbors(result.translate(k, l, 0).getBlockPos(), Blocks.field_10124);
						}
					}
				}
			}
		}
	}

	public static boolean canDispense(World world, BlockPos pos, ItemStack stack) {
		return stack.getItem() == Items.WITHER_SKELETON_SKULL && pos.getY() >= 2 && world.getDifficulty() != Difficulty.field_5801 && !world.isClient
			? getWitherDispenserPattern().searchAround(world, pos) != null
			: false;
	}

	private static BlockPattern getWitherBossPattern() {
		if (witherBossPattern == null) {
			witherBossPattern = BlockPatternBuilder.start()
				.aisle("^^^", "###", "~#~")
				.where('#', cachedBlockPosition -> cachedBlockPosition.getBlockState().isIn(BlockTags.field_22274))
				.where('^', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.field_10177).or(BlockStatePredicate.forBlock(Blocks.field_10101))))
				.where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR)))
				.build();
		}

		return witherBossPattern;
	}

	private static BlockPattern getWitherDispenserPattern() {
		if (witherDispenserPattern == null) {
			witherDispenserPattern = BlockPatternBuilder.start()
				.aisle("   ", "###", "~#~")
				.where('#', cachedBlockPosition -> cachedBlockPosition.getBlockState().isIn(BlockTags.field_22274))
				.where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR)))
				.build();
		}

		return witherDispenserPattern;
	}
}
