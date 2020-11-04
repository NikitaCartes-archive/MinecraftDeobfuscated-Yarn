package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VindicatorEntityRenderer extends IllagerEntityRenderer<VindicatorEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/illager/vindicator.png");

	public VindicatorEntityRenderer(class_5617.class_5618 arg) {
		super(arg, new IllagerEntityModel<>(arg.method_32167(EntityModelLayers.VINDICATOR)), 0.5F);
		this.addFeature(
			new HeldItemFeatureRenderer<VindicatorEntity, IllagerEntityModel<VindicatorEntity>>(this) {
				public void render(
					MatrixStack matrixStack,
					VertexConsumerProvider vertexConsumerProvider,
					int i,
					VindicatorEntity vindicatorEntity,
					float f,
					float g,
					float h,
					float j,
					float k,
					float l
				) {
					if (vindicatorEntity.isAttacking()) {
						super.render(matrixStack, vertexConsumerProvider, i, vindicatorEntity, f, g, h, j, k, l);
					}
				}
			}
		);
	}

	public Identifier getTexture(VindicatorEntity vindicatorEntity) {
		return TEXTURE;
	}
}
