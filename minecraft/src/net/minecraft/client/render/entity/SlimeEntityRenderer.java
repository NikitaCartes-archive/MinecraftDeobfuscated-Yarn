package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SlimeEntityRenderer extends EntityMobRenderer<SlimeEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/slime/slime.png");

	public SlimeEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new SlimeEntityModel(16), 0.25F);
		this.addLayer(new SlimeOverlayEntityRenderer(this));
	}

	public void method_4117(SlimeEntity slimeEntity, double d, double e, double f, float g, float h) {
		this.field_4673 = 0.25F * (float)slimeEntity.getSize();
		super.method_4072(slimeEntity, d, e, f, g, h);
	}

	protected void method_4118(SlimeEntity slimeEntity, float f) {
		float g = 0.999F;
		GlStateManager.scalef(0.999F, 0.999F, 0.999F);
		float h = (float)slimeEntity.getSize();
		float i = MathHelper.lerp(f, slimeEntity.field_7387, slimeEntity.field_7388) / (h * 0.5F + 1.0F);
		float j = 1.0F / (i + 1.0F);
		GlStateManager.scalef(j * h, 1.0F / j * h, j * h);
	}

	protected Identifier getTexture(SlimeEntity slimeEntity) {
		return SKIN;
	}
}
