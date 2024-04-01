package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.MegaSpudArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.MegaSpudOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.MegaSpudEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MegaSpudEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class MegaSpudEntityRenderer extends MobEntityRenderer<MegaSpudEntity, MegaSpudEntityModel<MegaSpudEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/slime/mega_spud.png");

	public MegaSpudEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new MegaSpudEntityModel<>(context.getPart(EntityModelLayers.MEGA_SPUD)), 0.25F);
		this.addFeature(new MegaSpudOverlayFeatureRenderer<>(this, context.getModelLoader()));
		this.addFeature(new MegaSpudArmorFeatureRenderer(this, context.getModelLoader()));
	}

	public void render(MegaSpudEntity megaSpudEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		this.shadowRadius = 0.25F * (float)megaSpudEntity.getSize();
		super.render(megaSpudEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	protected void scale(MegaSpudEntity megaSpudEntity, MatrixStack matrixStack, float f) {
		float g = 0.999F;
		matrixStack.scale(0.999F, 0.999F, 0.999F);
		matrixStack.translate(0.0F, 0.001F, 0.0F);
		float h = (float)megaSpudEntity.getSize();
		float i = MathHelper.lerp(f, megaSpudEntity.field_50445, megaSpudEntity.field_50435) / (h * 0.5F + 1.0F);
		float j = 1.0F / (i + 1.0F);
		matrixStack.scale(j * h, 1.5F / j * h, j * h);
	}

	public Identifier getTexture(MegaSpudEntity megaSpudEntity) {
		return TEXTURE;
	}
}
