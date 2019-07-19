package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.PhantomEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.PhantomEntityModel;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PhantomEntityRenderer extends MobEntityRenderer<PhantomEntity, PhantomEntityModel<PhantomEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/phantom.png");

	public PhantomEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new PhantomEntityModel<>(), 0.75F);
		this.addFeature(new PhantomEyesFeatureRenderer<>(this));
	}

	protected Identifier getTexture(PhantomEntity phantomEntity) {
		return SKIN;
	}

	protected void scale(PhantomEntity phantomEntity, float f) {
		int i = phantomEntity.getPhantomSize();
		float g = 1.0F + 0.15F * (float)i;
		GlStateManager.scalef(g, g, g);
		GlStateManager.translatef(0.0F, 1.3125F, 0.1875F);
	}

	protected void setupTransforms(PhantomEntity phantomEntity, float f, float g, float h) {
		super.setupTransforms(phantomEntity, f, g, h);
		GlStateManager.rotatef(phantomEntity.pitch, 1.0F, 0.0F, 0.0F);
	}
}
