package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DefaultEntityRenderer extends EntityRenderer<Entity> {
	public DefaultEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	@Override
	public void render(Entity entity, double d, double e, double f, float g, float h) {
		RenderSystem.pushMatrix();
		renderBox(entity.getBoundingBox(), d - entity.prevRenderX, e - entity.prevRenderY, f - entity.prevRenderZ);
		RenderSystem.popMatrix();
		super.render(entity, d, e, f, g, h);
	}

	@Nullable
	@Override
	protected Identifier getTexture(Entity entity) {
		return null;
	}
}
