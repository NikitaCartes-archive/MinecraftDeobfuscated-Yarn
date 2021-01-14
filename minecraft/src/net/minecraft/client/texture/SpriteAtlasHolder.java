package net.minecraft.client.texture;

import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public abstract class SpriteAtlasHolder extends SinglePreparationResourceReloader<SpriteAtlasTexture.Data> implements AutoCloseable {
	private final SpriteAtlasTexture atlas;
	private final String pathPrefix;

	public SpriteAtlasHolder(TextureManager textureManager, Identifier atlasId, String pathPrefix) {
		this.pathPrefix = pathPrefix;
		this.atlas = new SpriteAtlasTexture(atlasId);
		textureManager.registerTexture(this.atlas.getId(), this.atlas);
	}

	protected abstract Stream<Identifier> getSprites();

	protected Sprite getSprite(Identifier objectId) {
		return this.atlas.getSprite(this.toSpriteId(objectId));
	}

	private Identifier toSpriteId(Identifier objectId) {
		return new Identifier(objectId.getNamespace(), this.pathPrefix + "/" + objectId.getPath());
	}

	protected SpriteAtlasTexture.Data prepare(ResourceManager resourceManager, Profiler profiler) {
		profiler.startTick();
		profiler.push("stitching");
		SpriteAtlasTexture.Data data = this.atlas.stitch(resourceManager, this.getSprites().map(this::toSpriteId), profiler, 0);
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
