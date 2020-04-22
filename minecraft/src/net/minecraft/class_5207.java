package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class class_5207 extends BlobFoliagePlacer {
	public class_5207(int i, int j, int k, int l, int m) {
		super(i, j, k, l, m, FoliagePlacerType.FANCY_FOLIAGE_PLACER);
	}

	public <T> class_5207(Dynamic<T> dynamic) {
		this(
			dynamic.get("radius").asInt(0),
			dynamic.get("radius_random").asInt(0),
			dynamic.get("offset").asInt(0),
			dynamic.get("offset_random").asInt(0),
			dynamic.get("height").asInt(0)
		);
	}

	@Override
	protected void generate(
		ModifiableTestableWorld world,
		Random random,
		TreeFeatureConfig treeFeatureConfig,
		int trunkHeight,
		FoliagePlacer.class_5208 arg,
		int foliageHeight,
		int radius,
		Set<BlockPos> leaves,
		int i
	) {
		for (int j = i; j >= i - foliageHeight; j--) {
			int k = radius + (j != i && j != i - foliageHeight ? 1 : 0);
			this.generate(world, random, treeFeatureConfig, arg.method_27388(), k, leaves, j, arg.method_27390());
		}
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
		return MathHelper.method_27285((float)baseHeight + 0.5F) + MathHelper.method_27285((float)dy + 0.5F) > (float)(dz * dz);
	}
}
