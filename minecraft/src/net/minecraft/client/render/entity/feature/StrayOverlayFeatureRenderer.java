package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.StrayEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class StrayOverlayFeatureRenderer<T extends MobEntity & RangedAttackMob, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	private static final Identifier SKIN = new Identifier("textures/entity/skeleton/stray_overlay.png");
	private final StrayEntityModel<T> model = new StrayEntityModel<>(0.25F, true);

	public StrayOverlayFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_23204(
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		T mobEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l,
		float m
	) {
		render(this.getModel(), this.model, SKIN, matrixStack, layeredVertexConsumerStorage, i, mobEntity, f, g, j, k, l, m, h, 1.0F, 1.0F, 1.0F);
	}
}
