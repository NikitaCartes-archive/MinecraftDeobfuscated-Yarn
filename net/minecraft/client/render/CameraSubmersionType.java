/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * This class contains the various "fluids" and is used for camera rendering. @see Camera.getSubmersionType
 */
@Environment(value=EnvType.CLIENT)
public enum CameraSubmersionType {
    LAVA,
    WATER,
    POWDER_SNOW,
    NONE;

}

