package net.minecraft.client.render.entity.feature;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class ArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
	protected final A modelLeggings;
	protected final A modelBody;
	private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = Maps.<String, Identifier>newHashMap();

	protected ArmorFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext, A bipedEntityModel, A bipedEntityModel2) {
		super(featureRendererContext);
		this.modelLeggings = bipedEntityModel;
		this.modelBody = bipedEntityModel2;
	}

	public void method_17157(class_4587 arg, class_4597 arg2, int i, T livingEntity, float f, float g, float h, float j, float k, float l, float m) {
		this.renderArmor(arg, arg2, livingEntity, f, g, h, j, k, l, m, EquipmentSlot.CHEST, i);
		this.renderArmor(arg, arg2, livingEntity, f, g, h, j, k, l, m, EquipmentSlot.LEGS, i);
		this.renderArmor(arg, arg2, livingEntity, f, g, h, j, k, l, m, EquipmentSlot.FEET, i);
		this.renderArmor(arg, arg2, livingEntity, f, g, h, j, k, l, m, EquipmentSlot.HEAD, i);
	}

	private void renderArmor(
		class_4587 arg, class_4597 arg2, T livingEntity, float f, float g, float h, float i, float j, float k, float l, EquipmentSlot equipmentSlot, int m
	) {
		ItemStack itemStack = livingEntity.getEquippedStack(equipmentSlot);
		if (itemStack.getItem() instanceof ArmorItem) {
			ArmorItem armorItem = (ArmorItem)itemStack.getItem();
			if (armorItem.getSlotType() == equipmentSlot) {
				A bipedEntityModel = this.getArmor(equipmentSlot);
				this.getModel().setAttributes(bipedEntityModel);
				bipedEntityModel.method_17086(livingEntity, f, g, h);
				this.method_4170(bipedEntityModel, equipmentSlot);
				bipedEntityModel.method_17087(livingEntity, f, g, i, j, k, l);
				boolean bl = this.isLegs(equipmentSlot);
				boolean bl2 = itemStack.hasEnchantmentGlint();
				if (armorItem instanceof DyeableArmorItem) {
					int n = ((DyeableArmorItem)armorItem).getColor(itemStack);
					float o = (float)(n >> 16 & 0xFF) / 255.0F;
					float p = (float)(n >> 8 & 0xFF) / 255.0F;
					float q = (float)(n & 0xFF) / 255.0F;
					this.method_23192(arg, arg2, m, armorItem, bl2, bipedEntityModel, bl, o, p, q, null);
					this.method_23192(arg, arg2, m, armorItem, bl2, bipedEntityModel, bl, 1.0F, 1.0F, 1.0F, "overlay");
				} else {
					this.method_23192(arg, arg2, m, armorItem, bl2, bipedEntityModel, bl, 1.0F, 1.0F, 1.0F, null);
				}
			}
		}
	}

	private void method_23192(
		class_4587 arg, class_4597 arg2, int i, ArmorItem armorItem, boolean bl, A bipedEntityModel, boolean bl2, float f, float g, float h, @Nullable String string
	) {
		class_4588 lv = ItemRenderer.method_23181(arg2, this.method_4174(armorItem, bl2, string), false, bl, false);
		class_4608.method_23211(lv);
		bipedEntityModel.method_17116(arg, lv, i, f, g, h);
		lv.method_22923();
	}

	public A getArmor(EquipmentSlot equipmentSlot) {
		return this.isLegs(equipmentSlot) ? this.modelLeggings : this.modelBody;
	}

	private boolean isLegs(EquipmentSlot equipmentSlot) {
		return equipmentSlot == EquipmentSlot.LEGS;
	}

	private Identifier method_4174(ArmorItem armorItem, boolean bl, @Nullable String string) {
		String string2 = "textures/models/armor/" + armorItem.getMaterial().getName() + "_layer_" + (bl ? 2 : 1) + (string == null ? "" : "_" + string) + ".png";
		return (Identifier)ARMOR_TEXTURE_CACHE.computeIfAbsent(string2, Identifier::new);
	}

	protected abstract void method_4170(A bipedEntityModel, EquipmentSlot equipmentSlot);

	protected abstract void method_4190(A bipedEntityModel);
}
