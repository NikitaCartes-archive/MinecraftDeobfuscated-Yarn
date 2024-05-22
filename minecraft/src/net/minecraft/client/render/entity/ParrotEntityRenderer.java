package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ParrotEntityRenderer extends MobEntityRenderer<ParrotEntity, ParrotEntityModel> {
	private static final Identifier RED_BLUE_TEXTURE = Identifier.ofVanilla("textures/entity/parrot/parrot_red_blue.png");
	private static final Identifier BLUE_TEXTURE = Identifier.ofVanilla("textures/entity/parrot/parrot_blue.png");
	private static final Identifier GREEN_TEXTURE = Identifier.ofVanilla("textures/entity/parrot/parrot_green.png");
	private static final Identifier YELLOW_TEXTURE = Identifier.ofVanilla("textures/entity/parrot/parrot_yellow_blue.png");
	private static final Identifier GREY_TEXTURE = Identifier.ofVanilla("textures/entity/parrot/parrot_grey.png");

	public ParrotEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new ParrotEntityModel(context.getPart(EntityModelLayers.PARROT)), 0.3F);
	}

	public Identifier getTexture(ParrotEntity parrotEntity) {
		return getTexture(parrotEntity.getVariant());
	}

	public static Identifier getTexture(ParrotEntity.Variant variant) {
		return switch (variant) {
			case RED_BLUE -> RED_BLUE_TEXTURE;
			case BLUE -> BLUE_TEXTURE;
			case GREEN -> GREEN_TEXTURE;
			case YELLOW_BLUE -> YELLOW_TEXTURE;
			case GRAY -> GREY_TEXTURE;
		};
	}

	public float getAnimationProgress(ParrotEntity parrotEntity, float f) {
		float g = MathHelper.lerp(f, parrotEntity.prevFlapProgress, parrotEntity.flapProgress);
		float h = MathHelper.lerp(f, parrotEntity.prevMaxWingDeviation, parrotEntity.maxWingDeviation);
		return (MathHelper.sin(g) + 1.0F) * h;
	}
}
