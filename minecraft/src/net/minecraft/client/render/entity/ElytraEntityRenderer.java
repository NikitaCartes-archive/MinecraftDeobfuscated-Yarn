package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ElytraEntityRenderer implements LayerEntityRenderer<LivingEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/elytra.png");
	protected final LivingEntityRenderer<?> field_4851;
	private final ElytraEntityModel field_4852 = new ElytraEntityModel();

	public ElytraEntityRenderer(LivingEntityRenderer<?> livingEntityRenderer) {
		this.field_4851 = livingEntityRenderer;
	}

	@Override
	public void render(LivingEntity livingEntity, float f, float g, float h, float i, float j, float k, float l) {
		ItemStack itemStack = livingEntity.getEquippedStack(EquipmentSlot.CHEST);
		if (itemStack.getItem() == Items.field_8833) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SrcBlendFactor.ONE, GlStateManager.DstBlendFactor.ZERO);
			if (livingEntity instanceof AbstractClientPlayerEntity) {
				AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)livingEntity;
				if (abstractClientPlayerEntity.method_3126() && abstractClientPlayerEntity.method_3122() != null) {
					this.field_4851.bindTexture(abstractClientPlayerEntity.method_3122());
				} else if (abstractClientPlayerEntity.method_3125()
					&& abstractClientPlayerEntity.method_3119() != null
					&& abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.CAPE)) {
					this.field_4851.bindTexture(abstractClientPlayerEntity.method_3119());
				} else {
					this.field_4851.bindTexture(SKIN);
				}
			} else {
				this.field_4851.bindTexture(SKIN);
			}

			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, 0.0F, 0.125F);
			this.field_4852.setRotationAngles(f, g, i, j, k, l, livingEntity);
			this.field_4852.render(livingEntity, f, g, i, j, k, l);
			if (itemStack.hasEnchantments()) {
				ArmorEntityRenderer.renderEnchantedGlint(this.field_4851, livingEntity, this.field_4852, f, g, h, i, j, k, l);
			}

			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean shouldMergeTextures() {
		return false;
	}
}
