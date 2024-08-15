package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LeashKnotEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LeashKnotEntityRenderer extends EntityRenderer<LeashKnotEntity, EntityRenderState> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/lead_knot.png");
	private final LeashKnotEntityModel field_53192;

	public LeashKnotEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.field_53192 = new LeashKnotEntityModel(context.getPart(EntityModelLayers.LEASH_KNOT));
	}

	@Override
	public void render(EntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		matrices.push();
		matrices.scale(-1.0F, -1.0F, 1.0F);
		this.field_53192.setAngles(state);
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.field_53192.getLayer(TEXTURE));
		this.field_53192.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
		matrices.pop();
		super.render(state, matrices, vertexConsumers, light);
	}

	@Override
	public Identifier getTexture(EntityRenderState state) {
		return TEXTURE;
	}

	@Override
	public EntityRenderState getRenderState() {
		return new EntityRenderState();
	}
}
