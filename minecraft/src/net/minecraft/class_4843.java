package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class class_4843<T extends class_4836, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends ArmorBipedFeatureRenderer<T, M, A> {
	private final A field_22411;

	public class_4843(FeatureRendererContext<T, M> featureRendererContext, A bipedEntityModel, A bipedEntityModel2, A bipedEntityModel3) {
		super(featureRendererContext, bipedEntityModel, bipedEntityModel2);
		this.field_22411 = bipedEntityModel3;
	}

	@Override
	public A getArmor(EquipmentSlot equipmentSlot) {
		return equipmentSlot == EquipmentSlot.HEAD ? this.field_22411 : super.getArmor(equipmentSlot);
	}

	@Override
	protected Identifier getArmorTexture(EquipmentSlot equipmentSlot, ArmorItem armorItem, boolean bl, @Nullable String string) {
		if (equipmentSlot == EquipmentSlot.HEAD) {
			String string2 = string == null ? "" : "_" + string;
			String string3 = "textures/models/armor/" + armorItem.getMaterial().getName() + "_piglin_helmet" + string2 + ".png";
			return (Identifier)ARMOR_TEXTURE_CACHE.computeIfAbsent(string3, Identifier::new);
		} else {
			return super.getArmorTexture(equipmentSlot, armorItem, bl, string);
		}
	}
}
