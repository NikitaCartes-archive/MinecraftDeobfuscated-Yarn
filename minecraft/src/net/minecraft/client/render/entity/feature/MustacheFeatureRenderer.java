package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_8293;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.model.MustacheEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MustacheFeatureRenderer<T extends LivingEntity, M extends EntityModel<T> & ModelWithHead> extends FeatureRenderer<T, M> {
	private static final Identifier TEXTURE = new Identifier("textures/effect/mustache.png");
	private final MustacheEntityModel<T> model;

	public MustacheFeatureRenderer(FeatureRendererContext<T, M> context, EntityModelLoader loader) {
		super(context);
		this.model = new MustacheEntityModel<>(loader.getModelPart(EntityModelLayers.MUSTACHE));
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l
	) {
		if (class_8293.field_43576.method_50116()) {
			matrixStack.push();
			if (livingEntity.isBaby() && !(livingEntity instanceof VillagerEntity)) {
				float m = 2.0F;
				float n = 1.4F;
				matrixStack.translate(0.0F, 0.03125F, 0.0F);
				matrixStack.scale(0.7F, 0.7F, 0.7F);
				matrixStack.translate(0.0F, 1.0F, 0.0F);
			}

			this.getContextModel().getHead().rotate(matrixStack);
			if (livingEntity instanceof PiglinEntity) {
				matrixStack.translate(0.0F, 0.0F, -0.03125F);
			}

			this.model.setAngles(livingEntity, f, g, j, k, l);
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(TEXTURE));
			this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
			matrixStack.pop();
		}
	}
}
