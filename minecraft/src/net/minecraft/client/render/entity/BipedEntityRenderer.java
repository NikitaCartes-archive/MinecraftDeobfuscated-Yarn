package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;

@Environment(EnvType.CLIENT)
public abstract class BipedEntityRenderer<T extends MobEntity, S extends BipedEntityRenderState, M extends BipedEntityModel<S>>
	extends AgeableMobEntityRenderer<T, S, M> {
	public BipedEntityRenderer(EntityRendererFactory.Context context, M model, float shadowRadius) {
		this(context, model, model, shadowRadius);
	}

	public BipedEntityRenderer(EntityRendererFactory.Context context, M model, M babyModel, float scale) {
		this(context, model, babyModel, scale, HeadFeatureRenderer.HeadTransformation.DEFAULT);
	}

	public BipedEntityRenderer(EntityRendererFactory.Context context, M model, M babyModel, float scale, HeadFeatureRenderer.HeadTransformation headTransformation) {
		super(context, model, babyModel, scale);
		this.addFeature(new HeadFeatureRenderer<>(this, context.getModelLoader(), headTransformation, context.getItemRenderer()));
		this.addFeature(new ElytraFeatureRenderer<>(this, context.getModelLoader(), context.getEquipmentRenderer()));
		this.addFeature(new HeldItemFeatureRenderer<>(this, context.getItemRenderer()));
	}

	public void updateRenderState(T mobEntity, S bipedEntityRenderState, float f) {
		super.updateRenderState(mobEntity, bipedEntityRenderState, f);
		updateBipedRenderState(mobEntity, bipedEntityRenderState, f);
	}

	public static void updateBipedRenderState(LivingEntity entity, BipedEntityRenderState state, float tickDelta) {
		state.isInSneakingPose = entity.isInSneakingPose();
		state.isGliding = entity.isGliding();
		state.isSwimming = entity.isInSwimmingPose();
		state.hasVehicle = entity.hasVehicle();
		state.limbAmplitudeInverse = 1.0F;
		if (state.isGliding) {
			state.limbAmplitudeInverse = (float)entity.getVelocity().lengthSquared();
			state.limbAmplitudeInverse /= 0.2F;
			state.limbAmplitudeInverse = state.limbAmplitudeInverse * state.limbAmplitudeInverse * state.limbAmplitudeInverse;
		}

		if (state.limbAmplitudeInverse < 1.0F) {
			state.limbAmplitudeInverse = 1.0F;
		}

		state.handSwingProgress = entity.getHandSwingProgress(tickDelta);
		state.leaningPitch = entity.getLeaningPitch(tickDelta);
		state.preferredArm = getPreferredArm(entity);
		state.activeHand = entity.getActiveHand();
		state.crossbowPullTime = (float)CrossbowItem.getPullTime(entity.getActiveItem(), entity);
		state.itemUseTime = entity.getItemUseTime();
		state.isUsingItem = entity.isUsingItem();
		state.leftWingPitch = entity.elytraFlightController.leftWingPitch(tickDelta);
		state.leftWingYaw = entity.elytraFlightController.leftWingYaw(tickDelta);
		state.leftWingRoll = entity.elytraFlightController.leftWingRoll(tickDelta);
		state.equippedChestStack = entity.getEquippedStack(EquipmentSlot.CHEST).copy();
		state.equippedLegsStack = entity.getEquippedStack(EquipmentSlot.LEGS).copy();
		state.equippedFeetStack = entity.getEquippedStack(EquipmentSlot.FEET).copy();
	}

	private static Arm getPreferredArm(LivingEntity entity) {
		Arm arm = entity.getMainArm();
		return entity.preferredHand == Hand.MAIN_HAND ? arm : arm.getOpposite();
	}
}
