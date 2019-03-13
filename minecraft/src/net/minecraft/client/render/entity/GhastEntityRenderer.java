package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.GhastEntityModel;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GhastEntityRenderer extends MobEntityRenderer<GhastEntity, GhastEntityModel<GhastEntity>> {
	private static final Identifier field_4705 = new Identifier("textures/entity/ghast/ghast.png");
	private static final Identifier field_4706 = new Identifier("textures/entity/ghast/ghast_shooting.png");

	public GhastEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new GhastEntityModel<>(), 1.5F);
	}

	protected Identifier method_3972(GhastEntity ghastEntity) {
		return ghastEntity.isShooting() ? field_4706 : field_4705;
	}

	protected void method_3973(GhastEntity ghastEntity, float f) {
		float g = 1.0F;
		float h = 4.5F;
		float i = 4.5F;
		GlStateManager.scalef(4.5F, 4.5F, 4.5F);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
