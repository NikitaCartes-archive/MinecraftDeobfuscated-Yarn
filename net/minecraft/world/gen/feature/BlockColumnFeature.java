/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.BlockColumnFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class BlockColumnFeature
extends Feature<BlockColumnFeatureConfig> {
    public BlockColumnFeature(Codec<BlockColumnFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<BlockColumnFeatureConfig> context) {
        int l;
        StructureWorldAccess structureWorldAccess = context.getWorld();
        BlockColumnFeatureConfig blockColumnFeatureConfig = context.getConfig();
        Random random = context.getRandom();
        int i = blockColumnFeatureConfig.layers().size();
        int[] is = new int[i];
        int j = 0;
        for (int k = 0; k < i; ++k) {
            is[k] = blockColumnFeatureConfig.layers().get(k).height().get(random);
            j += is[k];
        }
        if (j == 0) {
            return false;
        }
        BlockPos.Mutable mutable = context.getOrigin().mutableCopy();
        BlockPos.Mutable mutable2 = mutable.mutableCopy().move(blockColumnFeatureConfig.direction());
        for (l = 0; l < j; ++l) {
            if (!blockColumnFeatureConfig.allowedPlacement().test(structureWorldAccess, mutable2)) {
                BlockColumnFeature.method_38906(is, j, l, blockColumnFeatureConfig.prioritizeTip());
                break;
            }
            mutable2.move(blockColumnFeatureConfig.direction());
        }
        for (l = 0; l < i; ++l) {
            int m = is[l];
            if (m == 0) continue;
            BlockColumnFeatureConfig.Layer layer = blockColumnFeatureConfig.layers().get(l);
            for (int n = 0; n < m; ++n) {
                structureWorldAccess.setBlockState(mutable, layer.state().get(random, mutable), Block.NOTIFY_LISTENERS);
                mutable.move(blockColumnFeatureConfig.direction());
            }
        }
        return true;
    }

    private static void method_38906(int[] is, int i, int j, boolean bl) {
        int q;
        int k = i - j;
        int l = bl ? 1 : -1;
        int m = bl ? 0 : is.length - 1;
        int n = bl ? is.length : -1;
        for (int o = m; o != n && k > 0; k -= q, o += l) {
            int p = is[o];
            q = Math.min(p, k);
            int n2 = o;
            is[n2] = is[n2] - q;
        }
    }
}

