package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SnifferEntityModel;
import net.minecraft.client.render.entity.state.SnifferEntityRenderState;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

@Environment(EnvType.CLIENT)
public class SnifferEntityRenderer extends AgeableMobEntityRenderer<SnifferEntity, SnifferEntityRenderState, SnifferEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/sniffer/sniffer.png");

	public SnifferEntityRenderer(EntityRendererFactory.Context context) {
		super(
			context, new SnifferEntityModel(context.getPart(EntityModelLayers.SNIFFER)), new SnifferEntityModel(context.getPart(EntityModelLayers.SNIFFER_BABY)), 1.1F
		);
	}

	public Identifier getTexture(SnifferEntityRenderState snifferEntityRenderState) {
		return TEXTURE;
	}

	public SnifferEntityRenderState createRenderState() {
		return new SnifferEntityRenderState();
	}

	public void updateRenderState(SnifferEntity snifferEntity, SnifferEntityRenderState snifferEntityRenderState, float f) {
		super.updateRenderState(snifferEntity, snifferEntityRenderState, f);
		snifferEntityRenderState.searching = snifferEntity.isSearching();
		snifferEntityRenderState.diggingAnimationState.copyFrom(snifferEntity.diggingAnimationState);
		snifferEntityRenderState.sniffingAnimationState.copyFrom(snifferEntity.sniffingAnimationState);
		snifferEntityRenderState.risingAnimationState.copyFrom(snifferEntity.risingAnimationState);
		snifferEntityRenderState.feelingHappyAnimationState.copyFrom(snifferEntity.feelingHappyAnimationState);
		snifferEntityRenderState.scentingAnimationState.copyFrom(snifferEntity.scentingAnimationState);
	}

	protected Box getBoundingBox(SnifferEntity snifferEntity) {
		return super.getBoundingBox(snifferEntity).expand(0.6F);
	}
}
