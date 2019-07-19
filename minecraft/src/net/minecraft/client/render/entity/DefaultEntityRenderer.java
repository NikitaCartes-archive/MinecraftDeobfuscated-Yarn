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
	public void render(Entity entity, double x, double y, double z, float yaw, float tickDelta) {
		GlStateManager.pushMatrix();
		renderBox(entity.getBoundingBox(), x - entity.lastRenderX, y - entity.lastRenderY, z - entity.lastRenderZ);
		GlStateManager.popMatrix();
		super.render(entity, x, y, z, yaw, tickDelta);
	}

	@Nullable
	@Override
	protected Identifier getTexture(Entity entity) {
		return null;
	}
}
