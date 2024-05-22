package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class DrownedEntityRenderer extends ZombieBaseEntityRenderer<DrownedEntity, DrownedEntityModel<DrownedEntity>> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/zombie/drowned.png");

	public DrownedEntityRenderer(EntityRendererFactory.Context context) {
		super(
			context,
			new DrownedEntityModel<>(context.getPart(EntityModelLayers.DROWNED)),
			new DrownedEntityModel<>(context.getPart(EntityModelLayers.DROWNED_INNER_ARMOR)),
			new DrownedEntityModel<>(context.getPart(EntityModelLayers.DROWNED_OUTER_ARMOR))
		);
		this.addFeature(new DrownedOverlayFeatureRenderer<>(this, context.getModelLoader()));
	}

	@Override
	public Identifier getTexture(ZombieEntity zombieEntity) {
		return TEXTURE;
	}

	protected void setupTransforms(DrownedEntity drownedEntity, MatrixStack matrixStack, float f, float g, float h, float i) {
		super.setupTransforms(drownedEntity, matrixStack, f, g, h, i);
		float j = drownedEntity.getLeaningPitch(h);
		if (j > 0.0F) {
			float k = -10.0F - drownedEntity.getPitch();
			float l = MathHelper.lerp(j, 0.0F, k);
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(l), 0.0F, drownedEntity.getHeight() / 2.0F / i, 0.0F);
		}
	}
}
