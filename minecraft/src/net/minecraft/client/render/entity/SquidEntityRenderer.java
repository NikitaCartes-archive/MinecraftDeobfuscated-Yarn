package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SquidEntityRenderer extends MobEntityRenderer<SquidEntity, SquidEntityModel<SquidEntity>> {
	private static final Identifier field_4791 = new Identifier("textures/entity/squid.png");

	public SquidEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new SquidEntityModel<>(), 0.7F);
	}

	protected Identifier method_4127(SquidEntity squidEntity) {
		return field_4791;
	}

	protected void method_4126(SquidEntity squidEntity, float f, float g, float h) {
		float i = MathHelper.lerp(h, squidEntity.field_6905, squidEntity.field_6907);
		float j = MathHelper.lerp(h, squidEntity.field_6906, squidEntity.field_6903);
		GlStateManager.translatef(0.0F, 0.5F, 0.0F);
		GlStateManager.rotatef(180.0F - g, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(i, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(j, 0.0F, 1.0F, 0.0F);
		GlStateManager.translatef(0.0F, -1.2F, 0.0F);
	}

	protected float method_4125(SquidEntity squidEntity, float f) {
		return MathHelper.lerp(f, squidEntity.field_6900, squidEntity.field_6904);
	}
}
