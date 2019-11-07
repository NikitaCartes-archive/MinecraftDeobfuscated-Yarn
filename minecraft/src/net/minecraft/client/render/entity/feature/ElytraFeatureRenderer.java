package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ElytraFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	private static final Identifier SKIN = new Identifier("textures/entity/elytra.png");
	private final ElytraEntityModel<T> elytra = new ElytraEntityModel<>();

	public ElytraFeatureRenderer(FeatureRendererContext<T, M> context) {
		super(context);
	}

	public void method_17161(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l
	) {
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

			matrixStack.push();
			matrixStack.translate(0.0, 0.0, 0.125);
			this.getModel().copyStateTo(this.elytra);
			this.elytra.method_17079(livingEntity, f, g, j, k, l);
			VertexConsumer vertexConsumer = ItemRenderer.getArmorVertexConsumer(
				vertexConsumerProvider, this.elytra.getLayer(identifier), false, itemStack.hasEnchantmentGlint()
			);
			this.elytra.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F);
			matrixStack.pop();
		}
	}
}
