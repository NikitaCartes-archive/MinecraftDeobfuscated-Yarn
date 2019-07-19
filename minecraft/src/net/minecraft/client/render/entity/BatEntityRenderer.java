package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.BatEntityModel;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BatEntityRenderer extends MobEntityRenderer<BatEntity, BatEntityModel> {
	private static final Identifier SKIN = new Identifier("textures/entity/bat.png");

	public BatEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new BatEntityModel(), 0.25F);
	}

	protected Identifier getTexture(BatEntity batEntity) {
		return SKIN;
	}

	protected void scale(BatEntity batEntity, float f) {
		GlStateManager.scalef(0.35F, 0.35F, 0.35F);
	}

	protected void setupTransforms(BatEntity batEntity, float f, float g, float h) {
		if (batEntity.isRoosting()) {
			GlStateManager.translatef(0.0F, -0.1F, 0.0F);
		} else {
			GlStateManager.translatef(0.0F, MathHelper.cos(f * 0.3F) * 0.1F, 0.0F);
		}

		super.setupTransforms(batEntity, f, g, h);
	}
}
