package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class StrayOverlayFeatureRenderer<T extends MobEntity & RangedAttackMob, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	private static final Identifier SKIN = new Identifier("textures/entity/skeleton/stray_overlay.png");
	private final SkeletonEntityModel<T> model;

	public StrayOverlayFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext, EntityModelLoader entityModelLoader) {
		super(featureRendererContext);
		this.model = new SkeletonEntityModel<>(entityModelLoader.getModelPart(EntityModelLayers.STRAY_OUTER));
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T mobEntity, float f, float g, float h, float j, float k, float l
	) {
		render(this.getContextModel(), this.model, SKIN, matrixStack, vertexConsumerProvider, i, mobEntity, f, g, j, k, l, h, 1.0F, 1.0F, 1.0F);
	}
}
