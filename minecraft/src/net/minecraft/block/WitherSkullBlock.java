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
	private static BlockPattern field_11765;
	@Nullable
	private static BlockPattern field_11764;

	protected WitherSkullBlock(Block.Settings settings) {
		super(SkullBlock.Type.field_11513, settings);
	}

	@Override
	public void method_9567(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
		super.method_9567(world, blockPos, blockState, livingEntity, itemStack);
		BlockEntity blockEntity = world.method_8321(blockPos);
		if (blockEntity instanceof SkullBlockEntity) {
			method_10898(world, blockPos, (SkullBlockEntity)blockEntity);
		}
	}

	public static void method_10898(World world, BlockPos blockPos, SkullBlockEntity skullBlockEntity) {
		if (!world.isClient) {
			Block block = skullBlockEntity.method_11010().getBlock();
			boolean bl = block == Blocks.field_10177 || block == Blocks.field_10101;
			if (bl && blockPos.getY() >= 2 && world.getDifficulty() != Difficulty.field_5801) {
				BlockPattern blockPattern = method_10900();
				BlockPattern.Result result = blockPattern.searchAround(world, blockPos);
				if (result != null) {
					for (int i = 0; i < blockPattern.getWidth(); i++) {
						for (int j = 0; j < blockPattern.getHeight(); j++) {
							CachedBlockPosition cachedBlockPosition = result.translate(i, j, 0);
							world.method_8652(cachedBlockPosition.getBlockPos(), Blocks.field_10124.method_9564(), 2);
							world.playLevelEvent(2001, cachedBlockPosition.getBlockPos(), Block.method_9507(cachedBlockPosition.getBlockState()));
						}
					}

					WitherEntity witherEntity = EntityType.field_6119.method_5883(world);
					BlockPos blockPos2 = result.translate(1, 2, 0).getBlockPos();
					witherEntity.setPositionAndAngles(
						(double)blockPos2.getX() + 0.5,
						(double)blockPos2.getY() + 0.55,
						(double)blockPos2.getZ() + 0.5,
						result.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F,
						0.0F
					);
					witherEntity.field_6283 = result.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F;
					witherEntity.method_6885();

					for (ServerPlayerEntity serverPlayerEntity : world.method_18467(ServerPlayerEntity.class, witherEntity.method_5829().expand(50.0))) {
						Criterions.SUMMONED_ENTITY.handle(serverPlayerEntity, witherEntity);
					}

					world.spawnEntity(witherEntity);

					for (int k = 0; k < blockPattern.getWidth(); k++) {
						for (int l = 0; l < blockPattern.getHeight(); l++) {
							world.method_8408(result.translate(k, l, 0).getBlockPos(), Blocks.field_10124);
						}
					}
				}
			}
		}
	}

	public static boolean canDispense(World world, BlockPos blockPos, ItemStack itemStack) {
		return itemStack.getItem() == Items.WITHER_SKELETON_SKULL && blockPos.getY() >= 2 && world.getDifficulty() != Difficulty.field_5801 && !world.isClient
			? method_10897().searchAround(world, blockPos) != null
			: false;
	}

	private static BlockPattern method_10900() {
		if (field_11765 == null) {
			field_11765 = BlockPatternBuilder.start()
				.aisle("^^^", "###", "~#~")
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.field_10114)))
				.where('^', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.field_10177).or(BlockStatePredicate.forBlock(Blocks.field_10101))))
				.where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.method_11746(Material.AIR)))
				.build();
		}

		return field_11765;
	}

	private static BlockPattern method_10897() {
		if (field_11764 == null) {
			field_11764 = BlockPatternBuilder.start()
				.aisle("   ", "###", "~#~")
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.field_10114)))
				.where('~', CachedBlockPosition.matchesBlockState(MaterialPredicate.method_11746(Material.AIR)))
				.build();
		}

		return field_11764;
	}
}
