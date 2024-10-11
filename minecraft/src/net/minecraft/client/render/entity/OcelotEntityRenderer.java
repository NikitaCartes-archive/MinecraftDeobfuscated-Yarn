package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.OcelotEntityModel;
import net.minecraft.client.render.entity.state.FelineEntityRenderState;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class OcelotEntityRenderer extends AgeableMobEntityRenderer<OcelotEntity, FelineEntityRenderState, OcelotEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/cat/ocelot.png");

	public OcelotEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new OcelotEntityModel(context.getPart(EntityModelLayers.OCELOT)), new OcelotEntityModel(context.getPart(EntityModelLayers.OCELOT_BABY)), 0.4F);
	}

	public Identifier getTexture(FelineEntityRenderState felineEntityRenderState) {
		return TEXTURE;
	}

	public FelineEntityRenderState createRenderState() {
		return new FelineEntityRenderState();
	}

	public void updateRenderState(OcelotEntity ocelotEntity, FelineEntityRenderState felineEntityRenderState, float f) {
		super.updateRenderState(ocelotEntity, felineEntityRenderState, f);
		felineEntityRenderState.inSneakingPose = ocelotEntity.isInSneakingPose();
		felineEntityRenderState.sprinting = ocelotEntity.isSprinting();
	}
}
