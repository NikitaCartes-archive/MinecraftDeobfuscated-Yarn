package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public abstract class SpriteAtlasHolder extends SinglePreparationResourceReloadListener<SpriteAtlasTexture.Data> implements AutoCloseable {
	private final SpriteAtlasTexture atlas;

	public SpriteAtlasHolder(TextureManager textureManager, Identifier identifier, String string) {
		this.atlas = new SpriteAtlasTexture(string);
		textureManager.registerTextureUpdateable(identifier, this.atlas);
	}

	protected abstract Iterable<Identifier> getSprites();

	protected Sprite getSprite(Identifier objectId) {
		return this.atlas.getSprite(objectId);
	}

	protected SpriteAtlasTexture.Data prepare(ResourceManager resourceManager, Profiler profiler) {
		profiler.startTick();
		profiler.push("stitching");
		SpriteAtlasTexture.Data data = this.atlas.stitch(resourceManager, this.getSprites(), profiler);
		profiler.pop();
		profiler.endTick();
		return data;
	}

	protected void apply(SpriteAtlasTexture.Data data, ResourceManager resourceManager, Profiler profiler) {
		profiler.startTick();
		profiler.push("upload");
		this.atlas.upload(data);
		profiler.pop();
		profiler.endTick();
	}

	public void close() {
		this.atlas.clear();
	}
}
