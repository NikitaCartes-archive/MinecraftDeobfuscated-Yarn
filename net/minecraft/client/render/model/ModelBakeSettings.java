/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Rotation3;

@Environment(value=EnvType.CLIENT)
public interface ModelBakeSettings {
    default public Rotation3 getRotation() {
        return Rotation3.identity();
    }

    default public boolean isUvLocked() {
        return false;
    }
}

