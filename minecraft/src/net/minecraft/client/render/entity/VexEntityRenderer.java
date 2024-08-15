package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.VexEntityModel;
import net.minecraft.client.render.entity.state.VexEntityRenderState;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class VexEntityRenderer extends MobEntityRenderer<VexEntity, VexEntityRenderState, VexEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/illager/vex.png");
	private static final Identifier CHARGING_TEXTURE = Identifier.ofVanilla("textures/entity/illager/vex_charging.png");

	public VexEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new VexEntityModel(context.getPart(EntityModelLayers.VEX)), 0.3F);
		this.addFeature(new HeldItemFeatureRenderer<>(this, context.getItemRenderer()));
	}

	protected int getBlockLight(VexEntity vexEntity, BlockPos blockPos) {
		return 15;
	}

	public Identifier getTexture(VexEntityRenderState vexEntityRenderState) {
		return vexEntityRenderState.charging ? CHARGING_TEXTURE : TEXTURE;
	}

	public VexEntityRenderState getRenderState() {
		return new VexEntityRenderState();
	}

	public void updateRenderState(VexEntity vexEntity, VexEntityRenderState vexEntityRenderState, float f) {
		super.updateRenderState(vexEntity, vexEntityRenderState, f);
		vexEntityRenderState.charging = vexEntity.isCharging();
	}
}
