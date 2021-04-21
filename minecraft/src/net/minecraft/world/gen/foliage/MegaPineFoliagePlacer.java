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
				.and(IntProvider.createValidatingCodec(0, 24).fieldOf("crown_height").forGetter(placer -> placer.crownHeight))
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
		TestableWorld world,
		BiConsumer<BlockPos, BlockState> replacer,
		Random random,
		TreeFeatureConfig config,
		int trunkHeight,
		FoliagePlacer.TreeNode treeNode,
		int foliageHeight,
		int radius,
		int offset
	) {
		BlockPos blockPos = treeNode.getCenter();
		int i = 0;

		for (int j = blockPos.getY() - foliageHeight + offset; j <= blockPos.getY() + offset; j++) {
			int k = blockPos.getY() - j;
			int l = radius + treeNode.getFoliageRadius() + MathHelper.floor((float)k / (float)foliageHeight * 3.5F);
			int m;
			if (k > 0 && l == i && (j & 1) == 0) {
				m = l + 1;
			} else {
				m = l;
			}

			this.generateSquare(world, replacer, random, config, new BlockPos(blockPos.getX(), j, blockPos.getZ()), m, 0, treeNode.isGiantTrunk());
			i = l;
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
