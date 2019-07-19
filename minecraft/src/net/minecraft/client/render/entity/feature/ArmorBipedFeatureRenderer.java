package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class ArmorBipedFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>>
	extends ArmorFeatureRenderer<T, M, A> {
	public ArmorBipedFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext, A bipedEntityModel, A bipedEntityModel2) {
		super(featureRendererContext, bipedEntityModel, bipedEntityModel2);
	}

	@Override
	protected void method_4170(A bipedEntityModel, EquipmentSlot equipmentSlot) {
		this.method_4190(bipedEntityModel);
		switch (equipmentSlot) {
			case HEAD:
				bipedEntityModel.head.visible = true;
				bipedEntityModel.helmet.visible = true;
				break;
			case CHEST:
				bipedEntityModel.torso.visible = true;
				bipedEntityModel.rightArm.visible = true;
				bipedEntityModel.leftArm.visible = true;
				break;
			case LEGS:
				bipedEntityModel.torso.visible = true;
				bipedEntityModel.rightLeg.visible = true;
				bipedEntityModel.leftLeg.visible = true;
				break;
			case FEET:
				bipedEntityModel.rightLeg.visible = true;
				bipedEntityModel.leftLeg.visible = true;
		}
	}

	@Override
	protected void method_4190(A bipedEntityModel) {
		bipedEntityModel.setVisible(false);
	}
}
