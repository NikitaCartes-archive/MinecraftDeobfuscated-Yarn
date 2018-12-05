package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_993;
import net.minecraft.client.render.entity.model.PhantomEntityModel;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PhantomEntityRenderer extends EntityMobRenderer<PhantomEntity> {
	private static final Identifier field_4756 = new Identifier("textures/entity/phantom.png");

	public PhantomEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new PhantomEntityModel(), 0.75F);
		this.addLayer(new class_993(this));
	}

	protected Identifier getTexture(PhantomEntity phantomEntity) {
		return field_4756;
	}

	protected void method_4088(PhantomEntity phantomEntity, float f) {
		int i = phantomEntity.method_7084();
		float g = 1.0F + 0.15F * (float)i;
		GlStateManager.scalef(g, g, g);
		GlStateManager.translatef(0.0F, 1.3125F, 0.1875F);
	}

	protected void method_4089(PhantomEntity phantomEntity, float f, float g, float h) {
		super.method_4058(phantomEntity, f, g, h);
		GlStateManager.rotatef(phantomEntity.pitch, 1.0F, 0.0F, 0.0F);
	}
}
