package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.CodEntityModel;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CodEntityRenderer extends EntityMobRenderer<CodEntity> {
	private static final Identifier field_4652 = new Identifier("textures/entity/fish/cod.png");

	public CodEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new CodEntityModel(), 0.2F);
	}

	@Nullable
	protected Identifier getTexture(CodEntity codEntity) {
		return field_4652;
	}

	protected void method_3896(CodEntity codEntity, float f, float g, float h) {
		super.method_4058(codEntity, f, g, h);
		float i = 4.3F * MathHelper.sin(0.6F * f);
		GlStateManager.rotatef(i, 0.0F, 1.0F, 0.0F);
		if (!codEntity.isInsideWater()) {
			GlStateManager.translatef(0.1F, 0.1F, -0.1F);
			GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
		}
	}
}
