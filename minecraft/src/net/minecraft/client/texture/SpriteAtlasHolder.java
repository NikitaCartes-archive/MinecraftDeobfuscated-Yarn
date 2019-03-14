package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SupplyingResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public abstract class SpriteAtlasHolder extends SupplyingResourceReloadListener<SpriteAtlasTexture.Data> implements AutoCloseable {
	private final SpriteAtlasTexture atlas;

	public SpriteAtlasHolder(TextureManager textureManager, Identifier identifier, String string) {
		this.atlas = new SpriteAtlasTexture(string);
		textureManager.registerTextureUpdateable(identifier, this.atlas);
	}

	protected abstract Iterable<Identifier> getSprites();

	protected Sprite getSprite(Identifier identifier) {
		return this.atlas.getSprite(identifier);
	}

	protected SpriteAtlasTexture.Data method_18668(ResourceManager resourceManager, Profiler profiler) {
		profiler.startTick();
		profiler.push("stitching");
		SpriteAtlasTexture.Data data = this.atlas.stitch(resourceManager, this.getSprites(), profiler);
		profiler.pop();
		profiler.endTick();
		return data;
	}

	protected void method_18666(SpriteAtlasTexture.Data data, ResourceManager resourceManager, Profiler profiler) {
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
