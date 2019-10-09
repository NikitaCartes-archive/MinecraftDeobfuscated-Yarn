package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class DrownedOverlayFeatureRenderer<T extends ZombieEntity> extends FeatureRenderer<T, DrownedEntityModel<T>> {
	private static final Identifier SKIN = new Identifier("textures/entity/zombie/drowned_outer_layer.png");
	private final DrownedEntityModel<T> model = new DrownedEntityModel<>(0.25F, 0.0F, 64, 64);

	public DrownedOverlayFeatureRenderer(FeatureRendererContext<T, DrownedEntityModel<T>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4182(
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		T zombieEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l,
		float m
	) {
		method_23196(this.getModel(), this.model, SKIN, matrixStack, layeredVertexConsumerStorage, i, zombieEntity, f, g, j, k, l, m, h, 1.0F, 1.0F, 1.0F);
	}
}
