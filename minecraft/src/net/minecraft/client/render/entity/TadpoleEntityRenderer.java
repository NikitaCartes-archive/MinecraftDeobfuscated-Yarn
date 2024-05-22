package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.TadpoleEntityModel;
import net.minecraft.entity.passive.TadpoleEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TadpoleEntityRenderer extends MobEntityRenderer<TadpoleEntity, TadpoleEntityModel<TadpoleEntity>> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/tadpole/tadpole.png");

	public TadpoleEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new TadpoleEntityModel<>(context.getPart(EntityModelLayers.TADPOLE)), 0.14F);
	}

	public Identifier getTexture(TadpoleEntity tadpoleEntity) {
		return TEXTURE;
	}
}
