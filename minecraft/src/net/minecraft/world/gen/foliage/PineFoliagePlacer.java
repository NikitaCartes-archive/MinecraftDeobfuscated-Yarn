package net.minecraft.world.gen.foliage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class PineFoliagePlacer extends FoliagePlacer {
	public static final Codec<PineFoliagePlacer> CODEC = RecordCodecBuilder.create(
		instance -> method_28846(instance)
				.<Integer, Integer>and(
					instance.group(
						Codec.INT.fieldOf("height").forGetter(pineFoliagePlacer -> pineFoliagePlacer.height),
						Codec.INT.fieldOf("height_random").forGetter(pineFoliagePlacer -> pineFoliagePlacer.randomHeight)
					)
				)
				.apply(instance, PineFoliagePlacer::new)
	);
	private final int height;
	private final int randomHeight;

	public PineFoliagePlacer(int i, int j, int k, int l, int m, int n) {
		super(i, j, k, l);
		this.height = m;
		this.randomHeight = n;
	}

	@Override
	protected FoliagePlacerType<?> method_28843() {
		return FoliagePlacerType.PINE_FOLIAGE_PLACER;
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
		int i
	) {
		int j = 0;

		for (int k = i; k >= i - foliageHeight; k--) {
			this.generate(world, random, config, treeNode.getCenter(), j, leaves, k, treeNode.isGiantTrunk());
			if (j >= 1 && k == i - foliageHeight + 1) {
				j--;
			} else if (j < radius + treeNode.getFoliageRadius()) {
				j++;
			}
		}
	}

	@Override
	public int getRadius(Random random, int baseHeight) {
		return super.getRadius(random, baseHeight) + random.nextInt(baseHeight + 1);
	}

	@Override
	public int getHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		return this.height + random.nextInt(this.randomHeight + 1);
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
		return baseHeight == dz && dy == dz && dz > 0;
	}
}
