package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class MegaPineFoliagePlacer extends FoliagePlacer {
	public static final Codec<MegaPineFoliagePlacer> CODEC = RecordCodecBuilder.create(
		instance -> method_28846(instance)
				.<Integer, Integer>and(
					instance.group(
						Codec.INT.fieldOf("height_random").forGetter(megaPineFoliagePlacer -> megaPineFoliagePlacer.heightRange),
						Codec.INT.fieldOf("crown_height").forGetter(megaPineFoliagePlacer -> megaPineFoliagePlacer.crownHeight)
					)
				)
				.apply(instance, MegaPineFoliagePlacer::new)
	);
	private final int heightRange;
	private final int crownHeight;

	public MegaPineFoliagePlacer(int i, int j, int k, int l, int m, int n) {
		super(i, j, k, l);
		this.heightRange = m;
		this.crownHeight = n;
	}

	@Override
	protected FoliagePlacerType<?> method_28843() {
		return FoliagePlacerType.MEGA_PINE_FOLIAGE_PLACER;
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
		BlockPos blockPos = treeNode.getCenter();
		int j = 0;

		for (int k = blockPos.getY() - foliageHeight + i; k <= blockPos.getY() + i; k++) {
			int l = blockPos.getY() - k;
			int m = radius + treeNode.getFoliageRadius() + MathHelper.floor((float)l / (float)foliageHeight * 3.5F);
			int n;
			if (l > 0 && m == j && (k & 1) == 0) {
				n = m + 1;
			} else {
				n = m;
			}

			this.generate(world, random, config, new BlockPos(blockPos.getX(), k, blockPos.getZ()), n, leaves, 0, treeNode.isGiantTrunk());
			j = m;
		}
	}

	@Override
	public int getHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		return random.nextInt(this.heightRange + 1) + this.crownHeight;
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
		return baseHeight + dy >= 7 ? true : baseHeight * baseHeight + dy * dy > dz * dz;
	}
}
