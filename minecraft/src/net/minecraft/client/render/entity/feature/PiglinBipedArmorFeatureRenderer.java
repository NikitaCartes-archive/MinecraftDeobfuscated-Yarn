package net.minecraft.client.render.entity.feature;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PiglinBipedArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>>
	extends ArmorBipedFeatureRenderer<T, M, A> {
	private final A helmetModel;

	public PiglinBipedArmorFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext, A bipedEntityModel, A bipedEntityModel2, A helmetModel) {
		super(featureRendererContext, bipedEntityModel, bipedEntityModel2);
		this.helmetModel = helmetModel;
	}

	@Override
	public A getArmor(EquipmentSlot slot) {
		return slot == EquipmentSlot.HEAD ? this.helmetModel : super.getArmor(slot);
	}

	@Override
	protected Identifier getArmorTexture(EquipmentSlot slot, ArmorItem item, boolean secondLayer, @Nullable String suffix) {
		if (slot == EquipmentSlot.HEAD) {
			String string = suffix == null ? "" : "_" + suffix;
			String string2 = "textures/models/armor/" + item.getMaterial().getName() + "_piglin_helmet" + string + ".png";
			return (Identifier)ARMOR_TEXTURE_CACHE.computeIfAbsent(string2, Identifier::new);
		} else {
			return super.getArmorTexture(slot, item, secondLayer, suffix);
		}
	}
}
