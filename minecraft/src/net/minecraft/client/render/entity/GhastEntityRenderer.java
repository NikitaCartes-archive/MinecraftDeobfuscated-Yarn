package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.GhastEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GhastEntityRenderer extends MobEntityRenderer<GhastEntity, GhastEntityModel<GhastEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/ghast/ghast.png");
	private static final Identifier ANGRY_SKIN = new Identifier("textures/entity/ghast/ghast_shooting.png");

	public GhastEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new GhastEntityModel<>(), 1.5F);
	}

	public Identifier method_3972(GhastEntity ghastEntity) {
		return ghastEntity.isShooting() ? ANGRY_SKIN : SKIN;
	}

	protected void method_3973(GhastEntity ghastEntity, MatrixStack matrixStack, float f) {
		float g = 1.0F;
		float h = 4.5F;
		float i = 4.5F;
		matrixStack.scale(4.5F, 4.5F, 4.5F);
	}
}
