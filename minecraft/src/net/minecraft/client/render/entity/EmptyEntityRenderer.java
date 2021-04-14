package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

/**
 * A renderer that does not render anything. Used for markers and area effect clouds.
 */
@Environment(EnvType.CLIENT)
public class EmptyEntityRenderer<T extends Entity> extends EntityRenderer<T> {
	public EmptyEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	@Override
	public Identifier getTexture(T entity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}
}
