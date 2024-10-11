package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CowEntityRenderer extends AgeableMobEntityRenderer<CowEntity, LivingEntityRenderState, CowEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/cow/cow.png");

	public CowEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new CowEntityModel(context.getPart(EntityModelLayers.COW)), new CowEntityModel(context.getPart(EntityModelLayers.COW_BABY)), 0.7F);
	}

	@Override
	public Identifier getTexture(LivingEntityRenderState state) {
		return TEXTURE;
	}

	public LivingEntityRenderState createRenderState() {
		return new LivingEntityRenderState();
	}

	public void updateRenderState(CowEntity cowEntity, LivingEntityRenderState livingEntityRenderState, float f) {
		super.updateRenderState(cowEntity, livingEntityRenderState, f);
	}
}
