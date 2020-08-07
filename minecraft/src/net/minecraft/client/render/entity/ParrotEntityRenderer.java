package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ParrotEntityRenderer extends MobEntityRenderer<ParrotEntity, ParrotEntityModel> {
	public static final Identifier[] TEXTURES = new Identifier[]{
		new Identifier("textures/entity/parrot/parrot_red_blue.png"),
		new Identifier("textures/entity/parrot/parrot_blue.png"),
		new Identifier("textures/entity/parrot/parrot_green.png"),
		new Identifier("textures/entity/parrot/parrot_yellow_blue.png"),
		new Identifier("textures/entity/parrot/parrot_grey.png")
	};

	public ParrotEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new ParrotEntityModel(), 0.3F);
	}

	public Identifier method_4080(ParrotEntity parrotEntity) {
		return TEXTURES[parrotEntity.getVariant()];
	}

	public float method_4081(ParrotEntity parrotEntity, float f) {
		float g = MathHelper.lerp(f, parrotEntity.prevFlapProgress, parrotEntity.flapProgress);
		float h = MathHelper.lerp(f, parrotEntity.prevMaxWingDeviation, parrotEntity.maxWingDeviation);
		return (MathHelper.sin(g) + 1.0F) * h;
	}
}
