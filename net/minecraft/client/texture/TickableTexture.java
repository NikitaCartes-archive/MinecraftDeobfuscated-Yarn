/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Texture;
import net.minecraft.client.texture.TextureTickListener;

@Environment(value=EnvType.CLIENT)
public interface TickableTexture
extends Texture,
TextureTickListener {
}

