package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criterions;
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
import net.minecraft.util.MaterialPredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class WitherSkullBlock extends SkullBlock {
	@Nullable
	private static BlockPattern witherBossPattern;
	@Nullable
	private static BlockPattern witherDispenserPattern;

	protected WitherSkullBlock(Block.Settings settings) {
		super(SkullBlock.Type.WITHER_SKELETON, settings);
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
		super.onPlaced(world, blockPos, blockState, livingEntity, itemStack);
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof SkullBlockEntity) {
			onPlaced(world, blockPos, (SkullBlockEntity)blockEntity);
		}
	}

	public static void onPlaced(World world, BlockPos blockPos, SkullBlockEntity skullBlockEntity) {
		if (!world.isClient) {
			Block block = skullBlockEntity.getCachedState().getBlock();
			boolean bl = block == Blocks.WITHER_SKELETON_SKULL || block == Blocks.WITHER_SKELETON_WALL_SKULL;
			if (bl && blockPos.getY() >= 2 && world.getDifficulty() != Difficulty.PEACEFUL) {
				BlockPattern blockPattern = getWitherBossPattern();
				BlockPattern.Result result = blockPattern.searchAround(world, blockPos);
				if (result != null) {
					for (int i = 0; i < blockPattern.getWidth(); i++) {
						for (int j = 0; j < blockPattern.getHeight(); j++) {
							CachedBlockPosition cachedBlockPosition = result.translate(i, j, 0);
							world.setBlockState(cachedBlockPosition.getBlockPos(), Blocks.AIR.getDefaultState(), 2);
							world.playLevelEvent(2001, cachedBlockPosition.getBlockPos(), Block.getRawIdFromState(cachedBlockPosition.getBlockState()));
						}
					}

					WitherEntity witherEntity = EntityType.WITHER.create(world);
					BlockPos blockPos2 = result.translate(1, 2, 0).getBlockPos();
					witherEntity.setPositionAndAngles(
						(double)blockPos2.getX() + 0.5,
						(double)blockPos2.getY() + 0.55,
						(double)blockPos2.getZ() + 0.5,
						result.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F,
						0.0F
					);
					witherEntity.bodyYaw = result.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F;
					witherEntity.method_6885();

					for (ServerPlayerEntity serverPlayerEntity : world.getNonSpectatingEntities(ServerPlayerEntity.class, witherEntity.getBoundingBox().expand(50.0))) {
						Criterions.SUMMONED_ENTITY.handle(serverPlayerEntity, witherEntity);
					}

					world.spawnEntity(witherEntity);

					for (int k = 0; k < blockPattern.getWidth(); k++) {
						for (int l = 0; l < blockPattern.getHeight(); l++) {
							world.updateNeighbors(result.translate(k, l, 0).getBlockPos(), Blocks.AIR);
						}
					}
				}
			}
		}
	}

	public static boolean canDispense(World world, BlockPos blockPos, ItemStack itemStack) {
		return itemStack.getItem() == Items.WITHER_SKELETON_SKULL && blockPos.getY() >= 2 && world.getDifficulty() != Difficulty.PEACEFUL && !world.isClient
			? getWitherDispenserPattern().searchAround(world, blockPos) != null
			: false;
	}

	private static BlockPattern getWitherBossPattern() {
		if (witherBossPattern == null) {
			witherBossPattern = BlockPatternBuilder.start()
				.aisle("^^^", "###", "~#~")
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.SOUL_SAND)))
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
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.SOUL_SAND)))
				.where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.create(Material.AIR)))
				.build();
		}

		return witherDispenserPattern;
	}
}
