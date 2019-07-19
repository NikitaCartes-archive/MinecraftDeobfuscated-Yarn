/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.ModelRotation;

@Environment(value=EnvType.CLIENT)
public interface ModelBakeSettings {
    default public ModelRotation getRotation() {
        return ModelRotation.X0_Y0;
    }

    default public boolean isShaded() {
        return false;
    }
}

