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
	protected void setVisible(A bipedEntityModel, EquipmentSlot equipmentSlot) {
		this.setInvisible(bipedEntityModel);
		switch (equipmentSlot) {
			case HEAD:
				bipedEntityModel.head.visible = true;
				bipedEntityModel.headwear.visible = true;
				break;
			case CHEST:
				bipedEntityModel.body.visible = true;
				bipedEntityModel.rightArm.visible = true;
				bipedEntityModel.leftArm.visible = true;
				break;
			case LEGS:
				bipedEntityModel.body.visible = true;
				bipedEntityModel.rightLeg.visible = true;
				bipedEntityModel.leftLeg.visible = true;
				break;
			case FEET:
				bipedEntityModel.rightLeg.visible = true;
				bipedEntityModel.leftLeg.visible = true;
		}
	}

	@Override
	protected void setInvisible(A bipedEntityModel) {
		bipedEntityModel.setVisible(false);
	}
}
