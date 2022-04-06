/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class MegaPineFoliagePlacer
extends FoliagePlacer {
    public static final Codec<MegaPineFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> MegaPineFoliagePlacer.fillFoliagePlacerFields(instance).and(((MapCodec)IntProvider.createValidatingCodec(0, 24).fieldOf("crown_height")).forGetter(placer -> placer.crownHeight)).apply((Applicative<MegaPineFoliagePlacer, ?>)instance, MegaPineFoliagePlacer::new));
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
    protected void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, AbstractRandom abstractRandom, TreeFeatureConfig config, int trunkHeight, FoliagePlacer.TreeNode treeNode, int foliageHeight, int radius, int offset) {
        BlockPos blockPos = treeNode.getCenter();
        int i = 0;
        for (int j = blockPos.getY() - foliageHeight + offset; j <= blockPos.getY() + offset; ++j) {
            int k = blockPos.getY() - j;
            int l = radius + treeNode.getFoliageRadius() + MathHelper.floor((float)k / (float)foliageHeight * 3.5f);
            int m = k > 0 && l == i && (j & 1) == 0 ? l + 1 : l;
            this.generateSquare(world, replacer, abstractRandom, config, new BlockPos(blockPos.getX(), j, blockPos.getZ()), m, 0, treeNode.isGiantTrunk());
            i = l;
        }
    }

    @Override
    public int getRandomHeight(AbstractRandom abstractRandom, int trunkHeight, TreeFeatureConfig config) {
        return this.crownHeight.get(abstractRandom);
    }

    @Override
    protected boolean isInvalidForLeaves(AbstractRandom abstractRandom, int dx, int y, int dz, int radius, boolean giantTrunk) {
        if (dx + dz >= 7) {
            return true;
        }
        return dx * dx + dz * dz > radius * radius;
    }
}

