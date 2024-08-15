package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DrownedOverlayFeatureRenderer extends FeatureRenderer<ZombieEntityRenderState, DrownedEntityModel> {
	private static final Identifier SKIN = Identifier.ofVanilla("textures/entity/zombie/drowned_outer_layer.png");
	private final DrownedEntityModel model;
	private final DrownedEntityModel babyModel;

	public DrownedOverlayFeatureRenderer(FeatureRendererContext<ZombieEntityRenderState, DrownedEntityModel> context, EntityModelLoader loader) {
		super(context);
		this.model = new DrownedEntityModel(loader.getModelPart(EntityModelLayers.DROWNED_OUTER));
		this.babyModel = new DrownedEntityModel(loader.getModelPart(EntityModelLayers.DROWNED_BABY_OUTER));
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, ZombieEntityRenderState zombieEntityRenderState, float f, float g
	) {
		DrownedEntityModel drownedEntityModel = zombieEntityRenderState.baby ? this.babyModel : this.model;
		render(drownedEntityModel, SKIN, matrixStack, vertexConsumerProvider, i, zombieEntityRenderState, -1);
	}
}
