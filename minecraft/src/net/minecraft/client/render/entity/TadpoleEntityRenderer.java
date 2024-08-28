package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.TadpoleEntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.TadpoleEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TadpoleEntityRenderer extends MobEntityRenderer<TadpoleEntity, LivingEntityRenderState, TadpoleEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/tadpole/tadpole.png");

	public TadpoleEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new TadpoleEntityModel(context.getPart(EntityModelLayers.TADPOLE)), 0.14F);
	}

	@Override
	public Identifier getTexture(LivingEntityRenderState state) {
		return TEXTURE;
	}

	public LivingEntityRenderState getRenderState() {
		return new LivingEntityRenderState();
	}
}
