package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.class_5428;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class MegaPineFoliagePlacer extends FoliagePlacer {
	public static final Codec<MegaPineFoliagePlacer> CODEC = RecordCodecBuilder.create(
		instance -> method_30411(instance)
				.and(class_5428.method_30316(0, 16, 8).fieldOf("crown_height").forGetter(megaPineFoliagePlacer -> megaPineFoliagePlacer.crownHeight))
				.apply(instance, MegaPineFoliagePlacer::new)
	);
	private final class_5428 crownHeight;

	public MegaPineFoliagePlacer(class_5428 arg, class_5428 arg2, class_5428 arg3) {
		super(arg, arg2);
		this.crownHeight = arg3;
	}

	@Override
	protected FoliagePlacerType<?> getType() {
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
		int i,
		BlockBox blockBox
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

			this.generate(world, random, config, new BlockPos(blockPos.getX(), k, blockPos.getZ()), n, leaves, 0, treeNode.isGiantTrunk(), blockBox);
			j = m;
		}
	}

	@Override
	public int getHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		return this.crownHeight.method_30321(random);
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
		return baseHeight + dy >= 7 ? true : baseHeight * baseHeight + dy * dy > dz * dz;
	}
}
