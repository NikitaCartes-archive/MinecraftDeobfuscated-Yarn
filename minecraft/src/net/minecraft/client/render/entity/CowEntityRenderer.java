package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CowEntityRenderer extends MobEntityRenderer<CowEntity, CowEntityModel<CowEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/cow/cow.png");

	public CowEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new CowEntityModel<>(context.getPart(EntityModelLayers.COW)), 0.7F);
	}

	public Identifier getTexture(CowEntity cowEntity) {
		return TEXTURE;
	}

	protected void scale(CowEntity cowEntity, MatrixStack matrixStack, float f) {
		float g = cowEntity.getBloatScale();
		matrixStack.scale(g, g, g);
	}
}
