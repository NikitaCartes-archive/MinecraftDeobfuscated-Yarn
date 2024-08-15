package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

/**
 * A renderer that does not render anything. Used for markers and area effect clouds.
 */
@Environment(EnvType.CLIENT)
public class EmptyEntityRenderer<T extends Entity> extends EntityRenderer<T, EntityRenderState> {
	public EmptyEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	@Override
	public Identifier getTexture(EntityRenderState state) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}

	@Override
	public EntityRenderState getRenderState() {
		return new EntityRenderState();
	}
}
