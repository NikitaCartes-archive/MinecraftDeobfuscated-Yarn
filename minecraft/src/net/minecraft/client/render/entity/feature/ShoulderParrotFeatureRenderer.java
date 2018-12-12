package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;

@Environment(EnvType.CLIENT)
public class ShoulderParrotFeatureRenderer<T extends PlayerEntity> extends FeatureRenderer<T, PlayerEntityModel<T>> {
	private final ParrotEntityModel field_17154 = new ParrotEntityModel();

	public ShoulderParrotFeatureRenderer(FeatureRendererContext<T, PlayerEntityModel<T>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void render(T playerEntity, float f, float g, float h, float i, float j, float k, float l) {
		GlStateManager.enableRescaleNormal();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.renderShoulderParrot(playerEntity, f, g, h, j, k, l, true);
		this.renderShoulderParrot(playerEntity, f, g, h, j, k, l, false);
		GlStateManager.disableRescaleNormal();
	}

	private void renderShoulderParrot(T playerEntity, float f, float g, float h, float i, float j, float k, boolean bl) {
		CompoundTag compoundTag = bl ? playerEntity.getShoulderEntityLeft() : playerEntity.getShoulderEntityRight();
		if (EntityType.get(compoundTag.getString("id")) == EntityType.PARROT) {
			GlStateManager.pushMatrix();
			GlStateManager.translatef(bl ? 0.4F : -0.4F, playerEntity.isSneaking() ? -1.3F : -1.5F, 0.0F);
			this.bindTexture(ParrotEntityRenderer.TEXTURES[compoundTag.getInt("Variant")]);
			this.field_17154.method_17106(f, g, i, j, k, playerEntity.age);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
