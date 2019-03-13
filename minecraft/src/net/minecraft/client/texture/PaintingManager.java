package net.minecraft.client.texture;

import com.google.common.collect.Iterables;
import java.util.Collections;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class PaintingManager extends SpriteAtlasHolder {
	private static final Identifier field_18032 = new Identifier("back");

	public PaintingManager(TextureManager textureManager) {
		super(textureManager, SpriteAtlasTexture.field_18031, "textures/painting");
	}

	@Override
	protected Iterable<Identifier> getSprites() {
		return Iterables.concat(Registry.MOTIVE.getIds(), Collections.singleton(field_18032));
	}

	public Sprite getPaintingSprite(PaintingMotive paintingMotive) {
		return this.method_18667(Registry.MOTIVE.method_10221(paintingMotive));
	}

	public Sprite getBackSprite() {
		return this.method_18667(field_18032);
	}
}
