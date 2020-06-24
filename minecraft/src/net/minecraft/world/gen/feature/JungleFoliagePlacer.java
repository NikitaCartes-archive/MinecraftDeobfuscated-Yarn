package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class JungleFoliagePlacer extends FoliagePlacer {
	public static final Codec<JungleFoliagePlacer> CODEC = RecordCodecBuilder.create(
		instance -> method_28846(instance)
				.and(Codec.INT.fieldOf("height").forGetter(jungleFoliagePlacer -> jungleFoliagePlacer.height))
				.apply(instance, JungleFoliagePlacer::new)
	);
	protected final int height;

	public JungleFoliagePlacer(int i, int j, int k, int l, int m) {
		super(i, j, k, l);
		this.height = m;
	}

	@Override
	protected FoliagePlacerType<?> getType() {
		return FoliagePlacerType.JUNGLE_FOLIAGE_PLACER;
	}

	@Override
	protected void generate(
		ModifiableTestableWorld world,
		Random random,
		TreeFeatureConfig config,
		int trunkHeight,
		FoliagePlacer.TreeNode treeNode,
		int foliageHeight,
		int radius,
		Set<BlockPos> leaves,
		int i,
		BlockBox blockBox
	) {
		int j = treeNode.isGiantTrunk() ? foliageHeight : 1 + random.nextInt(2);

		for (int k = i; k >= i - j; k--) {
			int l = radius + treeNode.getFoliageRadius() + 1 - k;
			this.generate(world, random, config, treeNode.getCenter(), l, leaves, k, treeNode.isGiantTrunk(), blockBox);
		}
	}

	@Override
	public int getHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		return this.height;
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
		return baseHeight + dy >= 7 ? true : baseHeight * baseHeight + dy * dy > dz * dz;
	}
}
