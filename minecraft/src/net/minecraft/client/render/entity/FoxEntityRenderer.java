package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.feature.FoxHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class FoxEntityRenderer extends MobEntityRenderer<FoxEntity, FoxEntityModel<FoxEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/fox/fox.png");
	private static final Identifier SLEEPING_TEXTURE = new Identifier("textures/entity/fox/fox_sleep.png");
	private static final Identifier SNOW_TEXTURE = new Identifier("textures/entity/fox/snow_fox.png");
	private static final Identifier SLEEPING_SNOW_TEXTURE = new Identifier("textures/entity/fox/snow_fox_sleep.png");

	public FoxEntityRenderer(class_5617.class_5618 arg) {
		super(arg, new FoxEntityModel<>(arg.method_32167(EntityModelLayers.FOX)), 0.4F);
		this.addFeature(new FoxHeldItemFeatureRenderer(this));
	}

	protected void setupTransforms(FoxEntity foxEntity, MatrixStack matrixStack, float f, float g, float h) {
		super.setupTransforms(foxEntity, matrixStack, f, g, h);
		if (foxEntity.isChasing() || foxEntity.isWalking()) {
			float i = -MathHelper.lerp(h, foxEntity.prevPitch, foxEntity.pitch);
			matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(i));
		}
	}

	public Identifier getTexture(FoxEntity foxEntity) {
		if (foxEntity.getFoxType() == FoxEntity.Type.RED) {
			return foxEntity.isSleeping() ? SLEEPING_TEXTURE : TEXTURE;
		} else {
			return foxEntity.isSleeping() ? SLEEPING_SNOW_TEXTURE : SNOW_TEXTURE;
		}
	}
}
