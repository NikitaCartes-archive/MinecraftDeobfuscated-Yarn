package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EndermiteEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EndermiteEntityRenderer extends MobEntityRenderer<EndermiteEntity, LivingEntityRenderState, EndermiteEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/endermite.png");

	public EndermiteEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new EndermiteEntityModel(context.getPart(EntityModelLayers.ENDERMITE)), 0.3F);
	}

	@Override
	protected float method_3919() {
		return 180.0F;
	}

	@Override
	public Identifier getTexture(LivingEntityRenderState state) {
		return TEXTURE;
	}

	public LivingEntityRenderState createRenderState() {
		return new LivingEntityRenderState();
	}
}
