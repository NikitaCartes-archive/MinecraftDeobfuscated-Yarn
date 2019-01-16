package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.class_2710;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class WitherSkullBlock extends SkullBlock {
	private static BlockPattern field_11765;
	private static BlockPattern field_11764;

	protected WitherSkullBlock(Block.Settings settings) {
		super(SkullBlock.Type.WITHER_SKELETON, settings);
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
		super.onPlaced(world, blockPos, blockState, livingEntity, itemStack);
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof SkullBlockEntity) {
			method_10898(world, blockPos, (SkullBlockEntity)blockEntity);
		}
	}

	public static void method_10898(World world, BlockPos blockPos, SkullBlockEntity skullBlockEntity) {
		Block block = skullBlockEntity.getCachedState().getBlock();
		boolean bl = block == Blocks.field_10177 || block == Blocks.field_10101;
		if (bl && blockPos.getY() >= 2 && world.getDifficulty() != Difficulty.PEACEFUL && !world.isClient) {
			BlockPattern blockPattern = getWitherBossPattern();
			BlockPattern.Result result = blockPattern.searchAround(world, blockPos);
			if (result != null) {
				for (int i = 0; i < blockPattern.getWidth(); i++) {
					for (int j = 0; j < blockPattern.getHeight(); j++) {
						world.setBlockState(result.translate(i, j, 0).getBlockPos(), Blocks.field_10124.getDefaultState(), 2);
					}
				}

				BlockPos blockPos2 = result.translate(1, 0, 0).getBlockPos();
				EntityWither entityWither = new EntityWither(world);
				BlockPos blockPos3 = result.translate(1, 2, 0).getBlockPos();
				entityWither.setPositionAndAngles(
					(double)blockPos3.getX() + 0.5,
					(double)blockPos3.getY() + 0.55,
					(double)blockPos3.getZ() + 0.5,
					result.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F,
					0.0F
				);
				entityWither.field_6283 = result.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F;
				entityWither.method_6885();

				for (ServerPlayerEntity serverPlayerEntity : world.getVisibleEntities(ServerPlayerEntity.class, entityWither.getBoundingBox().expand(50.0))) {
					Criterions.SUMMONED_ENTITY.handle(serverPlayerEntity, entityWither);
				}

				world.spawnEntity(entityWither);

				for (int k = 0; k < 120; k++) {
					world.addParticle(
						ParticleTypes.field_11230,
						(double)blockPos2.getX() + world.random.nextDouble(),
						(double)(blockPos2.getY() - 2) + world.random.nextDouble() * 3.9,
						(double)blockPos2.getZ() + world.random.nextDouble(),
						0.0,
						0.0,
						0.0
					);
				}

				for (int k = 0; k < blockPattern.getWidth(); k++) {
					for (int l = 0; l < blockPattern.getHeight(); l++) {
						world.updateNeighbors(result.translate(k, l, 0).getBlockPos(), Blocks.field_10124);
					}
				}
			}
		}
	}

	public static boolean method_10899(World world, BlockPos blockPos, ItemStack itemStack) {
		return itemStack.getItem() == Items.field_8791 && blockPos.getY() >= 2 && world.getDifficulty() != Difficulty.PEACEFUL && !world.isClient
			? getWitherDispenserPattern().searchAround(world, blockPos) != null
			: false;
	}

	protected static BlockPattern getWitherBossPattern() {
		if (field_11765 == null) {
			field_11765 = BlockPatternBuilder.start()
				.aisle("^^^", "###", "~#~")
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.field_10114)))
				.where('^', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.field_10177).or(BlockStatePredicate.forBlock(Blocks.field_10101))))
				.where('~', CachedBlockPosition.matchesBlockState(class_2710.method_11746(Material.AIR)))
				.build();
		}

		return field_11765;
	}

	protected static BlockPattern getWitherDispenserPattern() {
		if (field_11764 == null) {
			field_11764 = BlockPatternBuilder.start()
				.aisle("   ", "###", "~#~")
				.where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.field_10114)))
				.where('~', CachedBlockPosition.matchesBlockState(class_2710.method_11746(Material.AIR)))
				.build();
		}

		return field_11764;
	}
}
