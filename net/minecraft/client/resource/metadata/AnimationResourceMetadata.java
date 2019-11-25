/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.resource.metadata;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadataReader;

@Environment(value=EnvType.CLIENT)
public class AnimationResourceMetadata {
    public static final AnimationResourceMetadataReader READER = new AnimationResourceMetadataReader();
    public static final AnimationResourceMetadata EMPTY = new AnimationResourceMetadata((List)Lists.newArrayList(), -1, -1, 1, false){

        @Override
        public Pair<Integer, Integer> method_24141(int i, int j) {
            return Pair.of(i, j);
        }
    };
    private final List<AnimationFrameResourceMetadata> frames;
    private final int width;
    private final int height;
    private final int defaultFrameTime;
    private final boolean interpolate;

    public AnimationResourceMetadata(List<AnimationFrameResourceMetadata> list, int i, int j, int k, boolean bl) {
        this.frames = list;
        this.width = i;
        this.height = j;
        this.defaultFrameTime = k;
        this.interpolate = bl;
    }

    private static boolean method_24142(int i, int j) {
        return i / j * j == i;
    }

    public Pair<Integer, Integer> method_24141(int i, int j) {
        Pair<Integer, Integer> pair = this.method_24143(i, j);
        int k = pair.getFirst();
        int l = pair.getSecond();
        if (!AnimationResourceMetadata.method_24142(i, k) || !AnimationResourceMetadata.method_24142(j, l)) {
            throw new IllegalArgumentException(String.format("Image size %s,%s is not multiply of frame size %s,%s", i, j, k, l));
        }
        return pair;
    }

    private Pair<Integer, Integer> method_24143(int i, int j) {
        if (this.width != -1) {
            if (this.height != -1) {
                return Pair.of(this.width, this.height);
            }
            return Pair.of(this.width, j);
        }
        if (this.height != -1) {
            return Pair.of(i, this.height);
        }
        int k = Math.min(i, j);
        return Pair.of(k, k);
    }

    public int getHeight(int i) {
        return this.height == -1 ? i : this.height;
    }

    public int getWidth(int i) {
        return this.width == -1 ? i : this.width;
    }

    public int getFrameCount() {
        return this.frames.size();
    }

    public int getDefaultFrameTime() {
        return this.defaultFrameTime;
    }

    public boolean shouldInterpolate() {
        return this.interpolate;
    }

    private AnimationFrameResourceMetadata getFrame(int i) {
        return this.frames.get(i);
    }

    public int getFrameTime(int i) {
        AnimationFrameResourceMetadata animationFrameResourceMetadata = this.getFrame(i);
        if (animationFrameResourceMetadata.usesDefaultFrameTime()) {
            return this.defaultFrameTime;
        }
        return animationFrameResourceMetadata.getTime();
    }

    public int getFrameIndex(int i) {
        return this.frames.get(i).getIndex();
    }

    public Set<Integer> getFrameIndexSet() {
        HashSet<Integer> set = Sets.newHashSet();
        for (AnimationFrameResourceMetadata animationFrameResourceMetadata : this.frames) {
            set.add(animationFrameResourceMetadata.getIndex());
        }
        return set;
    }
}

