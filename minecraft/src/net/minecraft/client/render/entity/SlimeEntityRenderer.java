package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.SlimeOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SlimeEntityRenderer extends MobEntityRenderer<SlimeEntity, SlimeEntityModel<SlimeEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/slime/slime.png");

	public SlimeEntityRenderer(class_5617.class_5618 arg) {
		super(arg, new SlimeEntityModel<>(arg.method_32167(EntityModelLayers.SLIME)), 0.25F);
		this.addFeature(new SlimeOverlayFeatureRenderer<>(this, arg.method_32170()));
	}

	public void render(SlimeEntity slimeEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		this.shadowRadius = 0.25F * (float)slimeEntity.getSize();
		super.render(slimeEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	protected void scale(SlimeEntity slimeEntity, MatrixStack matrixStack, float f) {
		float g = 0.999F;
		matrixStack.scale(0.999F, 0.999F, 0.999F);
		matrixStack.translate(0.0, 0.001F, 0.0);
		float h = (float)slimeEntity.getSize();
		float i = MathHelper.lerp(f, slimeEntity.lastStretch, slimeEntity.stretch) / (h * 0.5F + 1.0F);
		float j = 1.0F / (i + 1.0F);
		matrixStack.scale(j * h, 1.0F / j * h, j * h);
	}

	public Identifier getTexture(SlimeEntity slimeEntity) {
		return TEXTURE;
	}
}
