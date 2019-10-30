package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DrownedOverlayFeatureRenderer<T extends ZombieEntity> extends FeatureRenderer<T, DrownedEntityModel<T>> {
	private static final Identifier SKIN = new Identifier("textures/entity/zombie/drowned_outer_layer.png");
	private final DrownedEntityModel<T> model = new DrownedEntityModel<>(0.25F, 0.0F, 64, 64);

	public DrownedOverlayFeatureRenderer(FeatureRendererContext<T, DrownedEntityModel<T>> context) {
		super(context);
	}

	public void method_4182(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T zombieEntity, float f, float g, float h, float j, float k, float l, float m
	) {
		render(this.getModel(), this.model, SKIN, matrixStack, vertexConsumerProvider, i, zombieEntity, f, g, j, k, l, m, h, 1.0F, 1.0F, 1.0F);
	}
}
