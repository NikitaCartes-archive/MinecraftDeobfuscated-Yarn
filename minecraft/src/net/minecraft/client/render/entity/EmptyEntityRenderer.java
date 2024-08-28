package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;

/**
 * A renderer that does not render anything. Used for markers and area effect clouds.
 */
@Environment(EnvType.CLIENT)
public class EmptyEntityRenderer<T extends Entity> extends EntityRenderer<T, EntityRenderState> {
	public EmptyEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	@Override
	public EntityRenderState getRenderState() {
		return new EntityRenderState();
	}
}
