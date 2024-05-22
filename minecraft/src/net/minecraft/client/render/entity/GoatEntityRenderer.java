package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.GoatEntityModel;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GoatEntityRenderer extends MobEntityRenderer<GoatEntity, GoatEntityModel<GoatEntity>> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/goat/goat.png");

	public GoatEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new GoatEntityModel<>(context.getPart(EntityModelLayers.GOAT)), 0.7F);
	}

	public Identifier getTexture(GoatEntity goatEntity) {
		return TEXTURE;
	}
}
