package net.minecraft.world.gen.foliage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class MegaPineFoliagePlacer extends FoliagePlacer {
	public static final Codec<MegaPineFoliagePlacer> CODEC = RecordCodecBuilder.create(
		instance -> fillFoliagePlacerFields(instance)
				.and(IntProvider.createValidatingCodec(0, 24).fieldOf("crown_height").forGetter(megaPineFoliagePlacer -> megaPineFoliagePlacer.crownHeight))
				.apply(instance, MegaPineFoliagePlacer::new)
	);
	private final IntProvider crownHeight;

	public MegaPineFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider crownHeight) {
		super(radius, offset);
		this.crownHeight = crownHeight;
	}

	@Override
	protected FoliagePlacerType<?> getType() {
		return FoliagePlacerType.MEGA_PINE_FOLIAGE_PLACER;
	}

	@Override
	protected void generate(
		TestableWorld testableWorld,
		BiConsumer<BlockPos, BlockState> biConsumer,
		Random random,
		TreeFeatureConfig treeFeatureConfig,
		int i,
		FoliagePlacer.TreeNode treeNode,
		int radius,
		int j,
		int offset
	) {
		BlockPos blockPos = treeNode.getCenter();
		int k = 0;

		for (int l = blockPos.getY() - radius + offset; l <= blockPos.getY() + offset; l++) {
			int m = blockPos.getY() - l;
			int n = j + treeNode.getFoliageRadius() + MathHelper.floor((float)m / (float)radius * 3.5F);
			int o;
			if (m > 0 && n == k && (l & 1) == 0) {
				o = n + 1;
			} else {
				o = n;
			}

			this.generateSquare(testableWorld, biConsumer, random, treeFeatureConfig, new BlockPos(blockPos.getX(), l, blockPos.getZ()), o, 0, treeNode.isGiantTrunk());
			k = n;
		}
	}

	@Override
	public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		return this.crownHeight.get(random);
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
		return dx + dz >= 7 ? true : dx * dx + dz * dz > radius * radius;
	}
}
