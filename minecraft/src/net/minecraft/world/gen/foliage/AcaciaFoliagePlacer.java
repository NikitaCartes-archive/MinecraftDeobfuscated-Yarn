package net.minecraft.world.gen.foliage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.class_5428;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class AcaciaFoliagePlacer extends FoliagePlacer {
	public static final Codec<AcaciaFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> method_30411(instance).apply(instance, AcaciaFoliagePlacer::new));

	public AcaciaFoliagePlacer(class_5428 arg, class_5428 arg2) {
		super(arg, arg2);
	}

	@Override
	protected FoliagePlacerType<?> getType() {
		return FoliagePlacerType.ACACIA_FOLIAGE_PLACER;
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
		boolean bl = treeNode.isGiantTrunk();
		BlockPos blockPos = treeNode.getCenter().up(i);
		this.generate(world, random, config, blockPos, radius + treeNode.getFoliageRadius(), leaves, -1 - foliageHeight, bl, blockBox);
		this.generate(world, random, config, blockPos, radius - 1, leaves, -foliageHeight, bl, blockBox);
		this.generate(world, random, config, blockPos, radius + treeNode.getFoliageRadius() - 1, leaves, 0, bl, blockBox);
	}

	@Override
	public int getHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
		return 0;
	}

	@Override
	protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
		return dx == 0 ? (baseHeight > 1 || dy > 1) && baseHeight != 0 && dy != 0 : baseHeight == dz && dy == dz && dz > 0;
	}
}
