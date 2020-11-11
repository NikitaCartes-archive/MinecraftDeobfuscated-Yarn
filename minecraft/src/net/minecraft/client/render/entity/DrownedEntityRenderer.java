package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DrownedEntityRenderer extends ZombieBaseEntityRenderer<DrownedEntity, DrownedEntityModel<DrownedEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/zombie/drowned.png");

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

	protected void setupTransforms(DrownedEntity drownedEntity, MatrixStack matrixStack, float f, float g, float h) {
		super.setupTransforms(drownedEntity, matrixStack, f, g, h);
		float i = drownedEntity.getLeaningPitch(h);
		if (i > 0.0F) {
			matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(MathHelper.lerp(i, drownedEntity.pitch, -10.0F - drownedEntity.pitch)));
		}
	}
}
