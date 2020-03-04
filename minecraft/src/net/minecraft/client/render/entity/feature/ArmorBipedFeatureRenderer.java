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
	protected void setVisible(A bipedModel, EquipmentSlot slot) {
		this.setInvisible(bipedModel);
		switch (slot) {
			case HEAD:
				bipedModel.head.visible = true;
				bipedModel.helmet.visible = true;
				break;
			case CHEST:
				bipedModel.torso.visible = true;
				bipedModel.rightArm.visible = true;
				bipedModel.leftArm.visible = true;
				break;
			case LEGS:
				bipedModel.torso.visible = true;
				bipedModel.rightLeg.visible = true;
				bipedModel.leftLeg.visible = true;
				break;
			case FEET:
				bipedModel.rightLeg.visible = true;
				bipedModel.leftLeg.visible = true;
		}
	}

	@Override
	protected void setInvisible(A bipedModel) {
		bipedModel.setVisible(false);
	}
}
