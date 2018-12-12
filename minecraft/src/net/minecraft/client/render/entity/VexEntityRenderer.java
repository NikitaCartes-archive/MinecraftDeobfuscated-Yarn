package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.VexEntityModel;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VexEntityRenderer extends BipedEntityRenderer<VexEntity, VexEntityModel> {
	private static final Identifier field_4801 = new Identifier("textures/entity/illager/vex.png");
	private static final Identifier field_4802 = new Identifier("textures/entity/illager/vex_charging.png");

	public VexEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new VexEntityModel(), 0.3F);
	}

	protected Identifier getTexture(VexEntity vexEntity) {
		return vexEntity.method_7176() ? field_4802 : field_4801;
	}

	protected void method_4143(VexEntity vexEntity, float f) {
		GlStateManager.scalef(0.4F, 0.4F, 0.4F);
	}
}
