package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;

@Environment(EnvType.CLIENT)
public class ArmorBipedEntityRenderer extends ArmorEntityRenderer<BipedEntityModel> {
	public ArmorBipedEntityRenderer(LivingEntityRenderer<?> livingEntityRenderer) {
		super(livingEntityRenderer);
	}

	@Override
	protected void init() {
		this.modelLeggings = new BipedEntityModel(0.5F);
		this.modelBody = new BipedEntityModel(1.0F);
	}

	protected void handleModelAdjustments(BipedEntityModel bipedEntityModel, EquipmentSlot equipmentSlot) {
		this.hideModel(bipedEntityModel);
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

	protected void hideModel(BipedEntityModel bipedEntityModel) {
		bipedEntityModel.setVisible(false);
	}
}
