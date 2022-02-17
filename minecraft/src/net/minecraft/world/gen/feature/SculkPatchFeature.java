package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.AbstractLichenBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.data.client.model.BlockStateVariantMap;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.util.FeatureContext;
import org.apache.logging.log4j.util.TriConsumer;

public class SculkPatchFeature extends Feature<SculkPatchFeatureConfig> {
	public SculkPatchFeature(Codec<SculkPatchFeatureConfig> codec) {
		super(codec);
	}

	private BlockPos method_40850(StructureWorldAccess structureWorldAccess, BlockPos blockPos, Direction direction, Direction direction2, int i) {
		BlockPos.Mutable mutable = blockPos.mutableCopy();

		for (int j = 0; structureWorldAccess.testBlockState(mutable, blockState -> this.method_40844(structureWorldAccess, blockState, blockPos)) && j < i; j++) {
			mutable.move(direction);
		}

		for (int var8 = 0;
			structureWorldAccess.testBlockState(mutable, blockState -> !this.method_40844(structureWorldAccess, blockState, blockPos)) && var8 < i;
			var8++
		) {
			mutable.move(direction2);
		}

		return mutable;
	}

	@Override
	public boolean generate(FeatureContext<SculkPatchFeatureConfig> context) {
		StructureWorldAccess structureWorldAccess = context.getWorld();
		SculkPatchFeatureConfig sculkPatchFeatureConfig = context.getConfig();
		Random random = context.getRandom();
		BlockPos blockPos = context.getOrigin();
		Predicate<BlockState> predicate = method_40852(sculkPatchFeatureConfig);
		int i = sculkPatchFeatureConfig.xzRadius.get(random) + 1;
		int j = sculkPatchFeatureConfig.xzRadius.get(random) + 1;
		Set<BlockPos> set = this.method_40847(structureWorldAccess, sculkPatchFeatureConfig, random, blockPos, predicate, i, j);
		this.method_40851(context, structureWorldAccess, sculkPatchFeatureConfig, random, set);
		return !set.isEmpty();
	}

	protected void method_40851(
		FeatureContext<SculkPatchFeatureConfig> featureContext,
		StructureWorldAccess structureWorldAccess,
		SculkPatchFeatureConfig sculkPatchFeatureConfig,
		Random random,
		Set<BlockPos> set
	) {
		for (BlockPos blockPos : set) {
			if (sculkPatchFeatureConfig.growthChance > 0.0F && random.nextFloat() < sculkPatchFeatureConfig.growthChance) {
				this.method_40846(structureWorldAccess, sculkPatchFeatureConfig, featureContext.getGenerator(), random, blockPos);
			}
		}
	}

	protected boolean method_40846(
		StructureWorldAccess structureWorldAccess, SculkPatchFeatureConfig sculkPatchFeatureConfig, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos
	) {
		return ((ConfiguredFeature)sculkPatchFeatureConfig.growthFeature.get())
			.generate(structureWorldAccess, chunkGenerator, random, blockPos.offset(sculkPatchFeatureConfig.surface.getDirection().getOpposite()));
	}

	protected Set<BlockPos> method_40847(
		StructureWorldAccess structureWorldAccess,
		SculkPatchFeatureConfig sculkPatchFeatureConfig,
		Random random,
		BlockPos blockPos,
		Predicate<BlockState> predicate,
		int i,
		int j
	) {
		int k = (i - 1) * (i - 1);
		int l = (j - 1) * (j - 1);
		Direction direction = sculkPatchFeatureConfig.surface.getDirection();
		Direction direction2 = direction.getOpposite();
		Set<BlockPos> set = new HashSet();
		int m = 2 * i + 1;
		int n = 2 * j + 1;
		SculkPatchFeature.class_7004[][] lvs = new SculkPatchFeature.class_7004[m][n];

		for (int o = 0; o < m; o++) {
			for (int p = 0; p < n; p++) {
				lvs[o][p] = new SculkPatchFeature.class_7004();
			}
		}

		for (int o = -i + 1; o <= i - 1; o++) {
			for (int p = -j + 1; p <= j - 1; p++) {
				float f = (float)(o * o);
				float g = (float)(p * p);
				float h = f / (float)k + g / (float)l + MathHelper.nextBetween(random, -0.5F, 0.5F);
				if (h <= 1.0F) {
					SculkPatchFeature.class_7004 lv = lvs[o + i][p + j];
					lv.field_36907 = SculkPatchFeature.class_7005.SCULK;
					lv.field_36905 = this.method_40850(structureWorldAccess, blockPos.add(o, 0, p), direction, direction2, sculkPatchFeatureConfig.verticalRange).getY();
				}
			}
		}

		TriConsumer<SculkPatchFeature.class_7004, Integer, Integer> triConsumer = (arg, integer, integer2) -> {
			SculkPatchFeature.class_7004 lv = lvs[integer][integer2];
			if (lv.field_36907 == SculkPatchFeature.class_7005.EMPTY) {
				if (lv.field_36906 == Integer.MAX_VALUE) {
					lv.field_36905 = this.method_40850(
							structureWorldAccess, blockPos.add(integer - i, 0, integer2 - j), direction, direction2, sculkPatchFeatureConfig.verticalRange
						)
						.getY();
				}

				if (lv.field_36905 <= arg.field_36905) {
					lv.field_36907 = SculkPatchFeature.class_7005.SCULK_VEIN;
				}
			}

			if (direction == Direction.DOWN) {
				arg.field_36906 = Math.min(arg.field_36906, lv.field_36905);
			} else {
				arg.field_36906 = Math.max(arg.field_36906, lv.field_36905);
			}
		};

		for (int px = 0; px < m; px++) {
			for (int q = 0; q < n; q++) {
				SculkPatchFeature.class_7004 lv2 = lvs[px][q];
				if (lvs[px][q].field_36907 == SculkPatchFeature.class_7005.SCULK) {
					if (px > 0) {
						triConsumer.accept(lv2, px - 1, q);
					}

					if (px < 2 * i) {
						triConsumer.accept(lv2, px + 1, q);
					}

					if (q > 0) {
						triConsumer.accept(lv2, px, q - 1);
					}

					if (q < 2 * j) {
						triConsumer.accept(lv2, px, q + 1);
					}
				}
			}
		}

		BlockStateVariantMap.TriFunction<BlockState, BlockPos, Direction, BlockState> triFunction = (blockStatex, blockPosx, directionx) -> {
			BlockState blockState2x = structureWorldAccess.getBlockState(blockPosx.offset(directionx));
			if (!blockState2x.isOf(Blocks.SCULK) && !blockState2x.isOf(Blocks.SCULK_CATALYST)) {
				AbstractLichenBlock abstractLichenBlock = (AbstractLichenBlock)Blocks.SCULK_VEIN;
				BlockState blockState3x = abstractLichenBlock.withDirection(blockStatex, structureWorldAccess, blockPosx, directionx);
				if (blockState3x != null) {
					return blockState3x;
				}
			}

			return blockStatex;
		};
		BlockPos.Mutable mutable = blockPos.mutableCopy();
		BlockPos.Mutable mutable2 = mutable.mutableCopy();

		for (int r = 0; r < m; r++) {
			for (int s = 0; s < n; s++) {
				SculkPatchFeature.class_7004 lv3 = lvs[r][s];
				if (lv3.field_36907 != SculkPatchFeature.class_7005.EMPTY) {
					mutable.set(blockPos, r - i, 0, s - j);
					mutable.setY(lv3.field_36905);
					mutable2.set(mutable, sculkPatchFeatureConfig.surface.getDirection());
					BlockState blockState = structureWorldAccess.getBlockState(mutable2);
					if (this.method_40844(structureWorldAccess, structureWorldAccess.getBlockState(mutable), mutable)
						|| !blockState.isSideSolidFullSquare(structureWorldAccess, mutable2, sculkPatchFeatureConfig.surface.getDirection().getOpposite())) {
						if (lv3.field_36907 == SculkPatchFeature.class_7005.SCULK) {
							int t = lv3.field_36905 - lv3.field_36906;
							int u = MathHelper.clamp(direction == Direction.DOWN ? t : -t, 1, sculkPatchFeatureConfig.verticalRange);
							BlockPos blockPos2 = mutable2.toImmutable();
							boolean bl = this.method_40848(structureWorldAccess, sculkPatchFeatureConfig, predicate, random, mutable2, u);
							if (bl) {
								set.add(blockPos2);
							}
						}

						if (lv3.field_36907 == SculkPatchFeature.class_7005.SCULK_VEIN || lv3.field_36907 == SculkPatchFeature.class_7005.SCULK) {
							BlockState blockState2 = structureWorldAccess.getBlockState(mutable);
							if (blockState2.isAir()
								&& !blockState2.isOf(Blocks.SCULK)
								&& !blockState2.isOf(Blocks.SCULK_CATALYST)
								&& blockState.isSideSolidFullSquare(structureWorldAccess, mutable2, direction2)) {
								BlockState blockState3 = blockState2;
								if (r > 0 && lvs[r - 1][s].field_36907 != SculkPatchFeature.class_7005.SCULK) {
									blockState3 = triFunction.apply(blockState2, mutable, Direction.WEST);
								}

								if (r < 2 * i && lvs[r + 1][s].field_36907 != SculkPatchFeature.class_7005.SCULK) {
									blockState3 = triFunction.apply(blockState3, mutable, Direction.EAST);
								}

								if (s > 0 && lvs[r][s - 1].field_36907 != SculkPatchFeature.class_7005.SCULK) {
									blockState3 = triFunction.apply(blockState3, mutable, Direction.NORTH);
								}

								if (s < 2 * j && lvs[r][s + 1].field_36907 != SculkPatchFeature.class_7005.SCULK) {
									blockState3 = triFunction.apply(blockState3, mutable, Direction.SOUTH);
								}

								blockState3 = triFunction.apply(blockState3, mutable, Direction.UP);
								blockState3 = triFunction.apply(blockState3, mutable, Direction.DOWN);
								if (blockState3 != blockState2 && blockState3 != Blocks.SCULK_VEIN.getDefaultState()) {
									structureWorldAccess.setBlockState(mutable, blockState3, Block.NOTIFY_ALL);
								}
							}
						}
					}
				}
			}
		}

		return set;
	}

	protected boolean method_40848(
		StructureWorldAccess structureWorldAccess,
		SculkPatchFeatureConfig sculkPatchFeatureConfig,
		Predicate<BlockState> predicate,
		Random random,
		BlockPos.Mutable mutable,
		int i
	) {
		BlockState blockState = sculkPatchFeatureConfig.groundState.getBlockState(random, mutable);

		for (int j = 0; j < i; j++) {
			BlockState blockState2 = structureWorldAccess.getBlockState(mutable);
			if (blockState.isOf(blockState2.getBlock()) || !predicate.test(blockState2)) {
				return false;
			}

			BlockState blockState3 = structureWorldAccess.getBlockState(mutable.up());
			if (blockState3.isOf(Blocks.SCULK_VEIN)) {
				structureWorldAccess.setBlockState(mutable.up(), Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
			}

			structureWorldAccess.setBlockState(mutable, blockState, Block.NOTIFY_LISTENERS);
			mutable.move(sculkPatchFeatureConfig.surface.getDirection());
		}

		return true;
	}

	protected boolean method_40844(WorldAccess worldAccess, BlockState blockState, BlockPos blockPos) {
		return !Block.isFaceFullSquare(blockState.getCollisionShape(worldAccess, blockPos, ShapeContext.absent()), Direction.DOWN);
	}

	private static Predicate<BlockState> method_40852(SculkPatchFeatureConfig sculkPatchFeatureConfig) {
		Tag<Block> tag = BlockTags.getTagGroup().getTag(sculkPatchFeatureConfig.replaceable);
		return tag == null ? blockState -> true : blockState -> blockState.isIn(tag);
	}

	static class class_7004 {
		int field_36905 = Integer.MAX_VALUE;
		int field_36906 = Integer.MAX_VALUE;
		SculkPatchFeature.class_7005 field_36907 = SculkPatchFeature.class_7005.EMPTY;
	}

	static enum class_7005 {
		EMPTY,
		SCULK,
		SCULK_VEIN;
	}
}
