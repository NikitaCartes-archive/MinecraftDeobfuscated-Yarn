package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.VexEntityModel;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VexEntityRenderer extends BipedEntityRenderer<VexEntity, VexEntityModel> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/illager/vex.png");
	private static final Identifier CHARGING_TEXTURE = new Identifier("textures/entity/illager/vex_charging.png");

	public VexEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new VexEntityModel(), 0.3F);
	}

	protected Identifier getTexture(VexEntity vexEntity) {
		return vexEntity.isCharging() ? CHARGING_TEXTURE : TEXTURE;
	}

	protected void scale(VexEntity vexEntity, float f) {
		GlStateManager.scalef(0.4F, 0.4F, 0.4F);
	}
}
