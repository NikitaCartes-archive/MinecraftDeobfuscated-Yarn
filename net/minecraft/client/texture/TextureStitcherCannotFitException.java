/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;

@Environment(value=EnvType.CLIENT)
public class TextureStitcherCannotFitException
extends RuntimeException {
    private final Collection<Sprite.Info> sprites;

    public TextureStitcherCannotFitException(Sprite.Info sprite, Collection<Sprite.Info> sprites) {
        super(String.format("Unable to fit: %s - size: %dx%d - Maybe try a lower resolution resourcepack?", sprite.getId(), sprite.getWidth(), sprite.getHeight()));
        this.sprites = sprites;
    }

    public Collection<Sprite.Info> getSprites() {
        return this.sprites;
    }
}

