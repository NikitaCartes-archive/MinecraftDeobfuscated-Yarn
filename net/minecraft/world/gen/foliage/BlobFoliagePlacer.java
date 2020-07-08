/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.class_5428;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class BlobFoliagePlacer
extends FoliagePlacer {
    public static final Codec<BlobFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> BlobFoliagePlacer.method_28838(instance).apply((Applicative)instance, BlobFoliagePlacer::new));
    protected final int height;

    protected static <P extends BlobFoliagePlacer> Products.P3<RecordCodecBuilder.Mu<P>, class_5428, class_5428, Integer> method_28838(RecordCodecBuilder.Instance<P> instance) {
        return BlobFoliagePlacer.method_30411(instance).and(((MapCodec)Codec.intRange(0, 16).fieldOf("height")).forGetter(blobFoliagePlacer -> blobFoliagePlacer.height));
    }

    public BlobFoliagePlacer(class_5428 arg, class_5428 arg2, int i) {
        super(arg, arg2);
        this.height = i;
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return FoliagePlacerType.BLOB_FOLIAGE_PLACER;
    }

    @Override
    protected void generate(ModifiableTestableWorld world, Random random, TreeFeatureConfig config, int trunkHeight, FoliagePlacer.TreeNode treeNode, int foliageHeight, int radius, Set<BlockPos> leaves, int i, BlockBox blockBox) {
        for (int j = i; j >= i - foliageHeight; --j) {
            int k = Math.max(radius + treeNode.getFoliageRadius() - 1 - j / 2, 0);
            this.generate(world, random, config, treeNode.getCenter(), k, leaves, j, treeNode.isGiantTrunk(), blockBox);
        }
    }

    @Override
    public int getHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
        return this.height;
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int baseHeight, int dx, int dy, int dz, boolean bl) {
        return baseHeight == dz && dy == dz && (random.nextInt(2) == 0 || dx == 0);
    }
}

