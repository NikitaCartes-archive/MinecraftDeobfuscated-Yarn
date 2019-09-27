package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ElytraFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	private static final Identifier SKIN = new Identifier("textures/entity/elytra.png");
	private final ElytraEntityModel<T> elytra = new ElytraEntityModel<>();

	public ElytraFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_17161(class_4587 arg, class_4597 arg2, int i, T livingEntity, float f, float g, float h, float j, float k, float l, float m) {
		ItemStack itemStack = livingEntity.getEquippedStack(EquipmentSlot.CHEST);
		if (itemStack.getItem() == Items.ELYTRA) {
			Identifier identifier;
			if (livingEntity instanceof AbstractClientPlayerEntity) {
				AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)livingEntity;
				if (abstractClientPlayerEntity.canRenderElytraTexture() && abstractClientPlayerEntity.getElytraTexture() != null) {
					identifier = abstractClientPlayerEntity.getElytraTexture();
				} else if (abstractClientPlayerEntity.canRenderCapeTexture()
					&& abstractClientPlayerEntity.getCapeTexture() != null
					&& abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.CAPE)) {
					identifier = abstractClientPlayerEntity.getCapeTexture();
				} else {
					identifier = SKIN;
				}
			} else {
				identifier = SKIN;
			}

			arg.method_22903();
			arg.method_22904(0.0, 0.0, 0.125);
			this.getModel().copyStateTo(this.elytra);
			this.elytra.method_17079(livingEntity, f, g, j, k, l, m);
			class_4588 lv = ItemRenderer.method_23181(arg2, identifier, false, itemStack.hasEnchantmentGlint(), false);
			class_4608.method_23211(lv);
			this.elytra.method_22957(arg, lv, i);
			lv.method_22923();
			arg.method_22909();
		}
	}
}
