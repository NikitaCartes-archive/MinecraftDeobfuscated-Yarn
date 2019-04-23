/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;

@Environment(value=EnvType.CLIENT)
public interface SpriteProvider {
    public Sprite getSprite(int var1, int var2);

    public Sprite getSprite(Random var1);
}

