/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4590;

@Environment(value=EnvType.CLIENT)
public interface ModelBakeSettings {
    default public class_4590 getRotation() {
        return class_4590.method_22931();
    }

    default public boolean isUvLocked() {
        return false;
    }
}

