package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentModel;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ArmorFeatureRenderer<S extends BipedEntityRenderState, M extends BipedEntityModel<S>, A extends BipedEntityModel<S>> extends FeatureRenderer<S, M> {
	private final A innerModel;
	private final A outerModel;
	private final A babyInnerModel;
	private final A babyOuterModel;
	private final EquipmentRenderer equipmentRenderer;

	public ArmorFeatureRenderer(FeatureRendererContext<S, M> context, A innerModel, A outerModel, EquipmentRenderer equipmentRenderer) {
		this(context, innerModel, outerModel, innerModel, outerModel, equipmentRenderer);
	}

	public ArmorFeatureRenderer(
		FeatureRendererContext<S, M> context, A innerModel, A outerModel, A babyInnerModel, A babyOuterModel, EquipmentRenderer equipmentRenderer
	) {
		super(context);
		this.innerModel = innerModel;
		this.outerModel = outerModel;
		this.babyInnerModel = babyInnerModel;
		this.babyOuterModel = babyOuterModel;
		this.equipmentRenderer = equipmentRenderer;
	}

	public static boolean hasModel(ItemStack stack, EquipmentSlot slot) {
		EquippableComponent equippableComponent = stack.get(DataComponentTypes.EQUIPPABLE);
		return equippableComponent != null && hasModel(equippableComponent, slot);
	}

	private static boolean hasModel(EquippableComponent component, EquipmentSlot slot) {
		return component.model().isPresent() && component.slot() == slot;
	}

	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, S bipedEntityRenderState, float f, float g) {
		this.renderArmor(
			matrixStack,
			vertexConsumerProvider,
			bipedEntityRenderState.equippedChestStack,
			EquipmentSlot.CHEST,
			i,
			this.getModel(bipedEntityRenderState, EquipmentSlot.CHEST)
		);
		this.renderArmor(
			matrixStack,
			vertexConsumerProvider,
			bipedEntityRenderState.equippedLegsStack,
			EquipmentSlot.LEGS,
			i,
			this.getModel(bipedEntityRenderState, EquipmentSlot.LEGS)
		);
		this.renderArmor(
			matrixStack,
			vertexConsumerProvider,
			bipedEntityRenderState.equippedFeetStack,
			EquipmentSlot.FEET,
			i,
			this.getModel(bipedEntityRenderState, EquipmentSlot.FEET)
		);
		this.renderArmor(
			matrixStack,
			vertexConsumerProvider,
			bipedEntityRenderState.equippedHeadStack,
			EquipmentSlot.HEAD,
			i,
			this.getModel(bipedEntityRenderState, EquipmentSlot.HEAD)
		);
	}

	private void renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, EquipmentSlot slot, int light, A armorModel) {
		EquippableComponent equippableComponent = stack.get(DataComponentTypes.EQUIPPABLE);
		if (equippableComponent != null && hasModel(equippableComponent, slot)) {
			this.getContextModel().copyTransforms(armorModel);
			this.setVisible(armorModel, slot);
			Identifier identifier = (Identifier)equippableComponent.model().orElseThrow();
			EquipmentModel.LayerType layerType = this.usesInnerModel(slot) ? EquipmentModel.LayerType.HUMANOID_LEGGINGS : EquipmentModel.LayerType.HUMANOID;
			this.equipmentRenderer.render(layerType, identifier, armorModel, stack, matrices, vertexConsumers, light);
		}
	}

	protected void setVisible(A bipedModel, EquipmentSlot slot) {
		bipedModel.setVisible(false);
		switch (slot) {
			case HEAD:
				bipedModel.head.visible = true;
				bipedModel.hat.visible = true;
				break;
			case CHEST:
				bipedModel.body.visible = true;
				bipedModel.rightArm.visible = true;
				bipedModel.leftArm.visible = true;
				break;
			case LEGS:
				bipedModel.body.visible = true;
				bipedModel.rightLeg.visible = true;
				bipedModel.leftLeg.visible = true;
				break;
			case FEET:
				bipedModel.rightLeg.visible = true;
				bipedModel.leftLeg.visible = true;
		}
	}

	private A getModel(S state, EquipmentSlot slot) {
		if (this.usesInnerModel(slot)) {
			return state.baby ? this.babyInnerModel : this.innerModel;
		} else {
			return state.baby ? this.babyOuterModel : this.outerModel;
		}
	}

	private boolean usesInnerModel(EquipmentSlot slot) {
		return slot == EquipmentSlot.LEGS;
	}
}
