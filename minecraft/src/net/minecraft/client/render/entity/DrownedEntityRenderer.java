package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class DrownedEntityRenderer extends ZombieBaseEntityRenderer<DrownedEntity, ZombieEntityRenderState, DrownedEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/zombie/drowned.png");

	public DrownedEntityRenderer(EntityRendererFactory.Context context) {
		super(
			context,
			new DrownedEntityModel(context.getPart(EntityModelLayers.DROWNED)),
			new DrownedEntityModel(context.getPart(EntityModelLayers.DROWNED_BABY)),
			new DrownedEntityModel(context.getPart(EntityModelLayers.DROWNED_INNER_ARMOR)),
			new DrownedEntityModel(context.getPart(EntityModelLayers.DROWNED_OUTER_ARMOR)),
			new DrownedEntityModel(context.getPart(EntityModelLayers.DROWNED_BABY_INNER_ARMOR)),
			new DrownedEntityModel(context.getPart(EntityModelLayers.DROWNED_BABY_OUTER_ARMOR))
		);
		this.addFeature(new DrownedOverlayFeatureRenderer(this, context.getModelLoader()));
	}

	public ZombieEntityRenderState createRenderState() {
		return new ZombieEntityRenderState();
	}

	@Override
	public Identifier getTexture(ZombieEntityRenderState zombieEntityRenderState) {
		return TEXTURE;
	}

	protected void setupTransforms(ZombieEntityRenderState zombieEntityRenderState, MatrixStack matrixStack, float f, float g) {
		super.setupTransforms(zombieEntityRenderState, matrixStack, f, g);
		float h = zombieEntityRenderState.leaningPitch;
		if (h > 0.0F) {
			float i = -10.0F - zombieEntityRenderState.pitch;
			float j = MathHelper.lerp(h, 0.0F, i);
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(j), 0.0F, zombieEntityRenderState.height / 2.0F / g, 0.0F);
		}
	}
}
