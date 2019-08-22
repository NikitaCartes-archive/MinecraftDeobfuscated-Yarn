/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.LayerSampleContext;
import net.minecraft.world.biome.layer.LayerSampler;
import net.minecraft.world.biome.layer.ParentedLayer;

public enum ScaleLayer implements ParentedLayer
{
    NORMAL,
    FUZZY{

        @Override
        protected int sample(LayerSampleContext<?> layerSampleContext, int i, int j, int k, int l) {
            return layerSampleContext.choose(i, j, k, l);
        }
    };


    @Override
    public int transformX(int i) {
        return i >> 1;
    }

    @Override
    public int transformZ(int i) {
        return i >> 1;
    }

    @Override
    public int sample(LayerSampleContext<?> layerSampleContext, LayerSampler layerSampler, int i, int j) {
        int k = layerSampler.sample(this.transformX(i), this.transformZ(j));
        layerSampleContext.initSeed(i >> 1 << 1, j >> 1 << 1);
        int l = i & 1;
        int m = j & 1;
        if (l == 0 && m == 0) {
            return k;
        }
        int n = layerSampler.sample(this.transformX(i), this.transformZ(j + 1));
        int o = layerSampleContext.choose(k, n);
        if (l == 0 && m == 1) {
            return o;
        }
        int p = layerSampler.sample(this.transformX(i + 1), this.transformZ(j));
        int q = layerSampleContext.choose(k, p);
        if (l == 1 && m == 0) {
            return q;
        }
        int r = layerSampler.sample(this.transformX(i + 1), this.transformZ(j + 1));
        return this.sample(layerSampleContext, k, p, n, r);
    }

    protected int sample(LayerSampleContext<?> layerSampleContext, int i, int j, int k, int l) {
        if (j == k && k == l) {
            return j;
        }
        if (i == j && i == k) {
            return i;
        }
        if (i == j && i == l) {
            return i;
        }
        if (i == k && i == l) {
            return i;
        }
        if (i == j && k != l) {
            return i;
        }
        if (i == k && j != l) {
            return i;
        }
        if (i == l && j != k) {
            return i;
        }
        if (j == k && i != l) {
            return j;
        }
        if (j == l && i != k) {
            return j;
        }
        if (k == l && i != j) {
            return k;
        }
        return layerSampleContext.choose(i, j, k, l);
    }
}

