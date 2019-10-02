package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class ArmorStandEntityRenderer extends LivingEntityRenderer<ArmorStandEntity, ArmorStandArmorEntityModel> {
	public static final Identifier SKIN = new Identifier("textures/entity/armorstand/wood.png");

	public ArmorStandEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new ArmorStandEntityModel(), 0.0F);
		this.addFeature(new ArmorBipedFeatureRenderer<>(this, new ArmorStandArmorEntityModel(0.5F), new ArmorStandArmorEntityModel(1.0F)));
		this.addFeature(new HeldItemFeatureRenderer<>(this));
		this.addFeature(new ElytraFeatureRenderer<>(this));
		this.addFeature(new HeadFeatureRenderer<>(this));
	}

	public Identifier method_3880(ArmorStandEntity armorStandEntity) {
		return SKIN;
	}

	protected void method_3877(ArmorStandEntity armorStandEntity, MatrixStack matrixStack, float f, float g, float h) {
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0F - g, true));
		float i = (float)(armorStandEntity.world.getTime() - armorStandEntity.field_7112) + h;
		if (i < 5.0F) {
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(MathHelper.sin(i / 1.5F * (float) Math.PI) * 3.0F, true));
		}
	}

	protected boolean method_3878(ArmorStandEntity armorStandEntity) {
		double d = this.renderManager.method_23168(armorStandEntity);
		float f = armorStandEntity.isInSneakingPose() ? 32.0F : 64.0F;
		return d >= (double)(f * f) ? false : armorStandEntity.isCustomNameVisible();
	}

	protected boolean method_23152(ArmorStandEntity armorStandEntity, boolean bl) {
		return armorStandEntity.isMarker() ? !armorStandEntity.isInvisible() && !bl : !armorStandEntity.isInvisible() || bl;
	}
}
