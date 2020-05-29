package net.minecraft.world.gen.foliage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class DarkOakFoliagePlacer extends FoliagePlacer {
	public static final Codec<DarkOakFoliagePlacer> CODEC = RecordCodecBuilder.create(
		instance -> method_28846(instance).apply(instance, DarkOakFoliagePlacer::new)
	);

	public DarkOakFoliagePlacer(int i, int j, int k, int l) {
		super(i, j, k, l);
	}

	@Override
	protected FoliagePlacerType<?> method_28843() {
		return FoliagePlacerType.DARK_OAK_FOLIAGE_PLACER;
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
		BlockPos blockPos = treeNode.getCenter().up(i);
		boolean bl = treeNode.isGiantTrunk();
		if (bl) {
			this.generate(world, random, config, blockPos, radius + 2, leaves, -1, bl, blockBox);
			this.generate(world, random, config, blockPos, radius + 3, leaves, 0, bl, blockBox);
			this.generate(world, random, config, blockPos, radius + 2, leaves, 1, bl, blockBox);
			if (random.nextBoolean()) {
				this.generate(world, random, config, blockPos, radius, leaves, 2, bl, blockBox);
			}
		} else {
			this.generate(world, random, config, blockPos, radius + 2, leaves, -1, bl, blockBox);
			this.generate(world, random, config, blockPos, radius + 1, leaves, 0, bl, blockBox);
		}
	}

	@Override
	public int getHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		return 4;
	}

	@Override
	protected boolean method_27387(Random random, int i, int j, int k, int l, boolean bl) {
		return j != 0 || !bl || i != -l && i < l || k != -l && k < l ? super.method_27387(random, i, j, k, l, bl) : true;
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
		if (dx == -1 && !bl) {
			return baseHeight == dz && dy == dz;
		} else {
			return dx == 1 ? baseHeight + dy > dz * 2 - 2 : false;
		}
	}
}
