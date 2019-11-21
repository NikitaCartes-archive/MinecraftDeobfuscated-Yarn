package net.minecraft;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class class_4724 implements AutoCloseable {
	private final Map<Identifier, SpriteAtlasTexture> field_21746;

	public class_4724(Collection<SpriteAtlasTexture> collection) {
		this.field_21746 = (Map<Identifier, SpriteAtlasTexture>)collection.stream().collect(Collectors.toMap(SpriteAtlasTexture::method_24106, Function.identity()));
	}

	public SpriteAtlasTexture method_24098(Identifier identifier) {
		return (SpriteAtlasTexture)this.field_21746.get(identifier);
	}

	public Sprite method_24097(class_4730 arg) {
		return ((SpriteAtlasTexture)this.field_21746.get(arg.method_24144())).getSprite(arg.method_24147());
	}

	public void close() {
		this.field_21746.values().forEach(SpriteAtlasTexture::clear);
		this.field_21746.clear();
	}

	public void method_24096(TextureManager textureManager, int i) {
		this.field_21746.values().forEach(spriteAtlasTexture -> {
			spriteAtlasTexture.setMipLevel(i);
			textureManager.bindTexture(spriteAtlasTexture.method_24106());
			spriteAtlasTexture.setFilter(false, i > 0);
		});
	}
}
