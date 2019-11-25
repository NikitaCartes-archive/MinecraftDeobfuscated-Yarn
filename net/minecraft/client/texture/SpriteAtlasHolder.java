/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(value=EnvType.CLIENT)
public abstract class SpriteAtlasHolder
extends SinglePreparationResourceReloadListener<SpriteAtlasTexture.Data>
implements AutoCloseable {
    private final SpriteAtlasTexture atlas;
    private final String pathPrefix;

    public SpriteAtlasHolder(TextureManager textureManager, Identifier identifier, String string) {
        this.pathPrefix = string;
        this.atlas = new SpriteAtlasTexture(identifier);
        textureManager.registerTexture(this.atlas.getId(), this.atlas);
    }

    protected abstract Stream<Identifier> getSprites();

    protected Sprite getSprite(Identifier identifier) {
        return this.atlas.getSprite(this.toSpriteId(identifier));
    }

    private Identifier toSpriteId(Identifier identifier) {
        return new Identifier(identifier.getNamespace(), this.pathPrefix + "/" + identifier.getPath());
    }

    @Override
    protected SpriteAtlasTexture.Data prepare(ResourceManager resourceManager, Profiler profiler) {
        profiler.startTick();
        profiler.push("stitching");
        SpriteAtlasTexture.Data data = this.atlas.stitch(resourceManager, this.getSprites().map(this::toSpriteId), profiler, 0);
        profiler.pop();
        profiler.endTick();
        return data;
    }

    @Override
    protected void apply(SpriteAtlasTexture.Data data, ResourceManager resourceManager, Profiler profiler) {
        profiler.startTick();
        profiler.push("upload");
        this.atlas.upload(data);
        profiler.pop();
        profiler.endTick();
    }

    @Override
    public void close() {
        this.atlas.clear();
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager resourceManager, Profiler profiler) {
        return this.prepare(resourceManager, profiler);
    }
}

