/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;

public enum ScaleLayer implements ParentedLayer
{
    NORMAL,
    FUZZY{

        @Override
        protected int sample(LayerSampleContext<?> context, int tl, int tr, int bl, int br) {
            return context.choose(tl, tr, bl, br);
        }
    };


    @Override
    public int transformX(int x) {
        return x >> 1;
    }

    @Override
    public int transformZ(int y) {
        return y >> 1;
    }

    @Override
    public int sample(LayerSampleContext<?> context, LayerSampler parent, int x, int z) {
        int i = parent.sample(this.transformX(x), this.transformZ(z));
        context.initSeed(x >> 1 << 1, z >> 1 << 1);
        int j = x & 1;
        int k = z & 1;
        if (j == 0 && k == 0) {
            return i;
        }
        int l = parent.sample(this.transformX(x), this.transformZ(z + 1));
        int m = context.choose(i, l);
        if (j == 0 && k == 1) {
            return m;
        }
        int n = parent.sample(this.transformX(x + 1), this.transformZ(z));
        int o = context.choose(i, n);
        if (j == 1 && k == 0) {
            return o;
        }
        int p = parent.sample(this.transformX(x + 1), this.transformZ(z + 1));
        return this.sample(context, i, n, l, p);
    }

    protected int sample(LayerSampleContext<?> context, int tl, int tr, int bl, int br) {
        if (tr == bl && bl == br) {
            return tr;
        }
        if (tl == tr && tl == bl) {
            return tl;
        }
        if (tl == tr && tl == br) {
            return tl;
        }
        if (tl == bl && tl == br) {
            return tl;
        }
        if (tl == tr && bl != br) {
            return tl;
        }
        if (tl == bl && tr != br) {
            return tl;
        }
        if (tl == br && tr != bl) {
            return tl;
        }
        if (tr == bl && tl != br) {
            return tr;
        }
        if (tr == br && tl != bl) {
            return tr;
        }
        if (bl == br && tl != tr) {
            return bl;
        }
        return context.choose(tl, tr, bl, br);
    }
}

