/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.model;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SpriteAtlasManager
implements AutoCloseable {
    private final Map<Identifier, SpriteAtlasTexture> atlases;

    public SpriteAtlasManager(Collection<SpriteAtlasTexture> collection) {
        this.atlases = collection.stream().collect(Collectors.toMap(SpriteAtlasTexture::getId, Function.identity()));
    }

    public SpriteAtlasTexture getAtlas(Identifier identifier) {
        return this.atlases.get(identifier);
    }

    public Sprite getSprite(SpriteIdentifier spriteIdentifier) {
        return this.atlases.get(spriteIdentifier.getAtlasId()).getSprite(spriteIdentifier.getTextureId());
    }

    @Override
    public void close() {
        this.atlases.values().forEach(SpriteAtlasTexture::clear);
        this.atlases.clear();
    }
}

