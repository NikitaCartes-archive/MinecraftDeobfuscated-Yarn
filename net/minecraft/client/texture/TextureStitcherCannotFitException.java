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
    private final Collection<Sprite.class_4727> sprites;

    public TextureStitcherCannotFitException(Sprite.class_4727 arg, Collection<Sprite.class_4727> collection) {
        super(String.format("Unable to fit: %s - size: %dx%d - Maybe try a lower resolution resourcepack?", arg.method_24121(), arg.method_24123(), arg.method_24125()));
        this.sprites = collection;
    }

    public Collection<Sprite.class_4727> getSprites() {
        return this.sprites;
    }
}

