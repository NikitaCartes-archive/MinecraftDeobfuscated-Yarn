package net.minecraft.world.gen.foliage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class SpruceFoliagePlacer extends FoliagePlacer {
	public static final Codec<SpruceFoliagePlacer> field_24936 = RecordCodecBuilder.create(
		instance -> method_28846(instance)
				.<Integer, Integer>and(
					instance.group(
						Codec.INT.fieldOf("trunk_height").forGetter(spruceFoliagePlacer -> spruceFoliagePlacer.trunkHeight),
						Codec.INT.fieldOf("trunk_height_random").forGetter(spruceFoliagePlacer -> spruceFoliagePlacer.randomTrunkHeight)
					)
				)
				.apply(instance, SpruceFoliagePlacer::new)
	);
	private final int trunkHeight;
	private final int randomTrunkHeight;

	public SpruceFoliagePlacer(int i, int j, int k, int l, int m, int n) {
		super(i, j, k, l);
		this.trunkHeight = m;
		this.randomTrunkHeight = n;
	}

	@Override
	protected FoliagePlacerType<?> getType() {
		return FoliagePlacerType.SPRUCE_FOLIAGE_PLACER;
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
		BlockPos blockPos = treeNode.getCenter();
		int j = random.nextInt(2);
		int k = 1;
		int l = 0;

		for (int m = i; m >= -foliageHeight; m--) {
			this.generate(world, random, config, blockPos, j, leaves, m, treeNode.isGiantTrunk(), blockBox);
			if (j >= k) {
				j = l;
				l = 1;
				k = Math.min(k + 1, radius + treeNode.getFoliageRadius());
			} else {
				j++;
			}
		}
	}

	@Override
	public int getHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		return Math.max(4, trunkHeight - this.trunkHeight - random.nextInt(this.randomTrunkHeight + 1));
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
		return baseHeight == dz && dy == dz && dz > 0;
	}
}
