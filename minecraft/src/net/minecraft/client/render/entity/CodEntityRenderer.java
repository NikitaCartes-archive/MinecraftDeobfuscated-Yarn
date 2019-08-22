package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.CodEntityModel;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CodEntityRenderer extends MobEntityRenderer<CodEntity, CodEntityModel<CodEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/fish/cod.png");

	public CodEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new CodEntityModel<>(), 0.3F);
	}

	@Nullable
	protected Identifier method_3897(CodEntity codEntity) {
		return SKIN;
	}

	protected void method_3896(CodEntity codEntity, float f, float g, float h) {
		super.setupTransforms(codEntity, f, g, h);
		float i = 4.3F * MathHelper.sin(0.6F * f);
		RenderSystem.rotatef(i, 0.0F, 1.0F, 0.0F);
		if (!codEntity.isInsideWater()) {
			RenderSystem.translatef(0.1F, 0.1F, -0.1F);
			RenderSystem.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
		}
	}
}
