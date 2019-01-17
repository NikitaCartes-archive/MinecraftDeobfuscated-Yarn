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
				bipedEntityModel.headwear.visible = true;
				break;
			case CHEST:
				bipedEntityModel.body.visible = true;
				bipedEntityModel.armRight.visible = true;
				bipedEntityModel.armLeft.visible = true;
				break;
			case LEGS:
				bipedEntityModel.body.visible = true;
				bipedEntityModel.legRight.visible = true;
				bipedEntityModel.legLeft.visible = true;
				break;
			case FEET:
				bipedEntityModel.legRight.visible = true;
				bipedEntityModel.legLeft.visible = true;
		}
	}

	@Override
	protected void method_4190(A bipedEntityModel) {
		bipedEntityModel.setVisible(false);
	}
}
