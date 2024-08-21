package net.minecraft.client.render.entity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class ArmorStandEntityRenderer extends LivingEntityRenderer<ArmorStandEntity, ArmorStandEntityRenderState, ArmorStandArmorEntityModel> {
	public static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/armorstand/wood.png");
	private final ArmorStandArmorEntityModel mainModel = this.getModel();
	private final ArmorStandArmorEntityModel smallModel;

	public ArmorStandEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new ArmorStandEntityModel(context.getPart(EntityModelLayers.ARMOR_STAND)), 0.0F);
		this.smallModel = new ArmorStandEntityModel(context.getPart(EntityModelLayers.ARMOR_STAND_SMALL));
		this.addFeature(
			new ArmorFeatureRenderer<>(
				this,
				new ArmorStandArmorEntityModel(context.getPart(EntityModelLayers.ARMOR_STAND_INNER_ARMOR)),
				new ArmorStandArmorEntityModel(context.getPart(EntityModelLayers.ARMOR_STAND_OUTER_ARMOR)),
				new ArmorStandArmorEntityModel(context.getPart(EntityModelLayers.ARMOR_STAND_SMALL_INNER_ARMOR)),
				new ArmorStandArmorEntityModel(context.getPart(EntityModelLayers.ARMOR_STAND_SMALL_OUTER_ARMOR)),
				context.getModelManager()
			)
		);
		this.addFeature(new HeldItemFeatureRenderer<>(this, context.getItemRenderer()));
		this.addFeature(new ElytraFeatureRenderer<>(this, context.getModelLoader()));
		this.addFeature(new HeadFeatureRenderer<>(this, context.getModelLoader(), context.getItemRenderer()));
	}

	public Identifier getTexture(ArmorStandEntityRenderState armorStandEntityRenderState) {
		return TEXTURE;
	}

	public ArmorStandEntityRenderState getRenderState() {
		return new ArmorStandEntityRenderState();
	}

	public void updateRenderState(ArmorStandEntity armorStandEntity, ArmorStandEntityRenderState armorStandEntityRenderState, float f) {
		super.updateRenderState(armorStandEntity, armorStandEntityRenderState, f);
		BipedEntityRenderer.updateBipedRenderState(armorStandEntity, armorStandEntityRenderState, f);
		armorStandEntityRenderState.yaw = MathHelper.lerpAngleDegrees(f, armorStandEntity.prevYaw, armorStandEntity.getYaw());
		armorStandEntityRenderState.marker = armorStandEntity.isMarker();
		armorStandEntityRenderState.small = armorStandEntity.isSmall();
		armorStandEntityRenderState.showArms = armorStandEntity.shouldShowArms();
		armorStandEntityRenderState.showBasePlate = armorStandEntity.shouldShowBasePlate();
		armorStandEntityRenderState.bodyRotation = armorStandEntity.getBodyRotation();
		armorStandEntityRenderState.headRotation = armorStandEntity.getHeadRotation();
		armorStandEntityRenderState.leftArmRotation = armorStandEntity.getLeftArmRotation();
		armorStandEntityRenderState.rightArmRotation = armorStandEntity.getRightArmRotation();
		armorStandEntityRenderState.leftLegRotation = armorStandEntity.getLeftLegRotation();
		armorStandEntityRenderState.rightLegRotation = armorStandEntity.getRightLegRotation();
		armorStandEntityRenderState.timeSinceLastHit = (float)(armorStandEntity.getWorld().getTime() - armorStandEntity.lastHitTime) + f;
	}

	public void render(ArmorStandEntityRenderState armorStandEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		this.model = armorStandEntityRenderState.small ? this.smallModel : this.mainModel;
		super.render(armorStandEntityRenderState, matrixStack, vertexConsumerProvider, i);
	}

	protected void setupTransforms(ArmorStandEntityRenderState armorStandEntityRenderState, MatrixStack matrixStack, float f, float g) {
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - f));
		if (armorStandEntityRenderState.timeSinceLastHit < 5.0F) {
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.sin(armorStandEntityRenderState.timeSinceLastHit / 1.5F * (float) Math.PI) * 3.0F));
		}
	}

	protected boolean hasLabel(ArmorStandEntity armorStandEntity, double d) {
		return armorStandEntity.isCustomNameVisible();
	}

	@Nullable
	protected RenderLayer getRenderLayer(ArmorStandEntityRenderState armorStandEntityRenderState, boolean bl, boolean bl2, boolean bl3) {
		if (!armorStandEntityRenderState.marker) {
			return super.getRenderLayer(armorStandEntityRenderState, bl, bl2, bl3);
		} else {
			Identifier identifier = this.getTexture(armorStandEntityRenderState);
			if (bl2) {
				return RenderLayer.getEntityTranslucent(identifier, false);
			} else {
				return bl ? RenderLayer.getEntityCutoutNoCull(identifier, false) : null;
			}
		}
	}
}
