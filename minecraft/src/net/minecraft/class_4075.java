package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public abstract class class_4075 extends class_4080<SpriteAtlasTexture.Data> implements AutoCloseable {
	private final SpriteAtlasTexture field_18230;

	public class_4075(TextureManager textureManager, Identifier identifier, String string) {
		this.field_18230 = new SpriteAtlasTexture(string);
		textureManager.registerTextureUpdateable(identifier, this.field_18230);
	}

	protected abstract Iterable<Identifier> method_18665();

	protected Sprite method_18667(Identifier identifier) {
		return this.field_18230.getSprite(identifier);
	}

	protected SpriteAtlasTexture.Data method_18668(ResourceManager resourceManager, Profiler profiler) {
		profiler.startTick();
		profiler.push("stitching");
		SpriteAtlasTexture.Data data = this.field_18230.stitch(resourceManager, this.method_18665(), profiler);
		profiler.pop();
		profiler.endTick();
		return data;
	}

	protected void method_18666(SpriteAtlasTexture.Data data, ResourceManager resourceManager, Profiler profiler) {
		profiler.startTick();
		profiler.push("upload");
		this.field_18230.upload(data);
		profiler.pop();
		profiler.endTick();
	}

	public void close() {
		this.field_18230.clear();
	}
}
