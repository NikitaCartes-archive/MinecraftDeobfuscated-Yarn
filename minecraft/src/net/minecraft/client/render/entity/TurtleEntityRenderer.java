package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.TurtleEntityModel;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TurtleEntityRenderer extends MobEntityRenderer<TurtleEntity, TurtleEntityModel<TurtleEntity>> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/turtle/big_sea_turtle.png");

	public TurtleEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new TurtleEntityModel<>(context.getPart(EntityModelLayers.TURTLE)), 0.7F);
	}

	protected float getShadowRadius(TurtleEntity turtleEntity) {
		float f = super.getShadowRadius(turtleEntity);
		return turtleEntity.isBaby() ? f * 0.83F : f;
	}

	public Identifier getTexture(TurtleEntity turtleEntity) {
		return TEXTURE;
	}
}
