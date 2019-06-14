package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
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
		GlStateManager.pushMatrix();
		renderBox(entity.method_5829(), d - entity.prevRenderX, e - entity.prevRenderY, f - entity.prevRenderZ);
		GlStateManager.popMatrix();
		super.render(entity, d, e, f, g, h);
	}

	@Nullable
	@Override
	protected Identifier getTexture(Entity entity) {
		return null;
	}
}
