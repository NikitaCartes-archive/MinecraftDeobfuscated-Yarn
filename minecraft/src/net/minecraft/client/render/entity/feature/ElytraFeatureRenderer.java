package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ElytraFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	private static final Identifier field_4850 = new Identifier("textures/entity/elytra.png");
	private final ElytraEntityModel<T> field_4852 = new ElytraEntityModel<>();

	public ElytraFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_17161(T livingEntity, float f, float g, float h, float i, float j, float k, float l) {
		ItemStack itemStack = livingEntity.method_6118(EquipmentSlot.CHEST);
		if (itemStack.getItem() == Items.field_8833) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			if (livingEntity instanceof AbstractClientPlayerEntity) {
				AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)livingEntity;
				if (abstractClientPlayerEntity.method_3126() && abstractClientPlayerEntity.method_3122() != null) {
					this.method_17164(abstractClientPlayerEntity.method_3122());
				} else if (abstractClientPlayerEntity.method_3125()
					&& abstractClientPlayerEntity.method_3119() != null
					&& abstractClientPlayerEntity.method_7348(PlayerModelPart.CAPE)) {
					this.method_17164(abstractClientPlayerEntity.method_3119());
				} else {
					this.method_17164(field_4850);
				}
			} else {
				this.method_17164(field_4850);
			}

			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, 0.0F, 0.125F);
			this.field_4852.method_17079(livingEntity, f, g, i, j, k, l);
			this.field_4852.method_17078(livingEntity, f, g, i, j, k, l);
			if (itemStack.hasEnchantments()) {
				ArmorFeatureRenderer.renderEnchantedGlint(this::method_17164, livingEntity, this.field_4852, f, g, h, i, j, k, l);
			}

			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
