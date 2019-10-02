package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.LeashEntityModel;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class LeashKnotEntityRenderer extends EntityRenderer<LeadKnotEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/lead_knot.png");
	private final LeashEntityModel<LeadKnotEntity> model = new LeashEntityModel<>();

	public LeashKnotEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_4035(
		LeadKnotEntity leadKnotEntity,
		double d,
		double e,
		double f,
		float g,
		float h,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		matrixStack.push();
		float i = 0.0625F;
		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		int j = leadKnotEntity.getLightmapCoordinates();
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.method_23017(SKIN));
		OverlayTexture.clearDefaultOverlay(vertexConsumer);
		this.model.setAngles(leadKnotEntity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		this.model.method_22957(matrixStack, vertexConsumer, j);
		vertexConsumer.clearDefaultOverlay();
		matrixStack.pop();
		super.render(leadKnotEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
	}

	public Identifier method_4036(LeadKnotEntity leadKnotEntity) {
		return SKIN;
	}
}
