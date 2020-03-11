package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ChickenEntityRenderer extends MobEntityRenderer<ChickenEntity, ChickenEntityModel<ChickenEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/chicken.png");

	public ChickenEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new ChickenEntityModel<>(), 0.3F);
	}

	public Identifier getTexture(ChickenEntity chickenEntity) {
		return TEXTURE;
	}

	protected float getAnimationProgress(ChickenEntity chickenEntity, float f) {
		float g = MathHelper.lerp(f, chickenEntity.field_6736, chickenEntity.field_6741);
		float h = MathHelper.lerp(f, chickenEntity.field_6738, chickenEntity.field_6743);
		return (MathHelper.sin(g) + 1.0F) * h;
	}
}
