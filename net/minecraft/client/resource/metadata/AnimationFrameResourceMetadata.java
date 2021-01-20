/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.resource.metadata;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class AnimationFrameResourceMetadata {
    private final int index;
    private final int time;

    public AnimationFrameResourceMetadata(int index) {
        this(index, -1);
    }

    public AnimationFrameResourceMetadata(int index, int time) {
        this.index = index;
        this.time = time;
    }

    public int getTime(int defaultTime) {
        return this.time == -1 ? defaultTime : this.time;
    }

    public int getIndex() {
        return this.index;
    }
}

