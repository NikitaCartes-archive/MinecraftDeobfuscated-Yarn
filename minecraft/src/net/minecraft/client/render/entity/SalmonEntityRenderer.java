package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.SalmonEntityModel;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SalmonEntityRenderer extends MobEntityRenderer<SalmonEntity, SalmonEntityModel<SalmonEntity>> {
	private static final Identifier field_4767 = new Identifier("textures/entity/fish/salmon.png");

	public SalmonEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new SalmonEntityModel<>(), 0.2F);
	}

	@Nullable
	protected Identifier getTexture(SalmonEntity salmonEntity) {
		return field_4767;
	}

	protected void method_4100(SalmonEntity salmonEntity, float f, float g, float h) {
		super.setupTransforms(salmonEntity, f, g, h);
		float i = 1.0F;
		float j = 1.0F;
		if (!salmonEntity.isInsideWater()) {
			i = 1.3F;
			j = 1.7F;
		}

		float k = i * 4.3F * MathHelper.sin(j * 0.6F * f);
		GlStateManager.rotatef(k, 0.0F, 1.0F, 0.0F);
		GlStateManager.translatef(0.0F, 0.0F, -0.4F);
		if (!salmonEntity.isInsideWater()) {
			GlStateManager.translatef(0.2F, 0.1F, 0.0F);
			GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
		}
	}
}
