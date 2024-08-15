package net.minecraft.client.render.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.RaftEntityModel;
import net.minecraft.client.render.entity.state.BoatEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class BoatEntityRenderer extends EntityRenderer<BoatEntity, BoatEntityRenderState> {
	private final Map<BoatEntity.Type, Pair<Identifier, EntityModel<BoatEntityRenderState>>> texturesAndModels;
	private final Model model;

	public BoatEntityRenderer(EntityRendererFactory.Context ctx, boolean chest) {
		super(ctx);
		this.shadowRadius = 0.8F;
		this.texturesAndModels = (Map<BoatEntity.Type, Pair<Identifier, EntityModel<BoatEntityRenderState>>>)Stream.of(BoatEntity.Type.values())
			.collect(ImmutableMap.toImmutableMap(type -> type, type -> Pair.of(getTexture(type, chest), this.createModel(ctx, type, chest))));
		this.model = new Model.SinglePartModel(ctx.getPart(EntityModelLayers.BOAT), identifier -> RenderLayer.getWaterMask());
	}

	private EntityModel<BoatEntityRenderState> createModel(EntityRendererFactory.Context ctx, BoatEntity.Type type, boolean chest) {
		EntityModelLayer entityModelLayer = chest ? EntityModelLayers.createChestBoat(type) : EntityModelLayers.createBoat(type);
		ModelPart modelPart = ctx.getPart(entityModelLayer);

		return (EntityModel<BoatEntityRenderState>)(switch (type) {
			case BAMBOO -> new RaftEntityModel(modelPart);
			default -> new BoatEntityModel(modelPart);
		});
	}

	private static Identifier getTexture(BoatEntity.Type type, boolean chest) {
		return chest
			? Identifier.ofVanilla("textures/entity/chest_boat/" + type.getName() + ".png")
			: Identifier.ofVanilla("textures/entity/boat/" + type.getName() + ".png");
	}

	public void render(BoatEntityRenderState boatEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		matrixStack.translate(0.0F, 0.375F, 0.0F);
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - boatEntityRenderState.yaw));
		float f = boatEntityRenderState.damageWobbleTicks;
		if (f > 0.0F) {
			matrixStack.multiply(
				RotationAxis.POSITIVE_X
					.rotationDegrees(MathHelper.sin(f) * f * boatEntityRenderState.damageWobbleStrength / 10.0F * (float)boatEntityRenderState.damageWobbleSide)
			);
		}

		if (!MathHelper.approximatelyEquals(boatEntityRenderState.bubbleWobble, 0.0F)) {
			matrixStack.multiply(new Quaternionf().setAngleAxis(boatEntityRenderState.bubbleWobble * (float) (Math.PI / 180.0), 1.0F, 0.0F, 1.0F));
		}

		Pair<Identifier, EntityModel<BoatEntityRenderState>> pair = (Pair<Identifier, EntityModel<BoatEntityRenderState>>)this.texturesAndModels
			.get(boatEntityRenderState.variant);
		Identifier identifier = pair.getFirst();
		EntityModel<BoatEntityRenderState> entityModel = pair.getSecond();
		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
		entityModel.setAngles(boatEntityRenderState);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(entityModel.getLayer(identifier));
		entityModel.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
		if (!boatEntityRenderState.submergedInWater && boatEntityRenderState.variant != BoatEntity.Type.BAMBOO) {
			this.model.render(matrixStack, vertexConsumerProvider.getBuffer(this.model.getLayer(identifier)), i, OverlayTexture.DEFAULT_UV);
		}

		matrixStack.pop();
		super.render(boatEntityRenderState, matrixStack, vertexConsumerProvider, i);
	}

	public Identifier getTexture(BoatEntityRenderState boatEntityRenderState) {
		return (Identifier)((Pair)this.texturesAndModels.get(boatEntityRenderState.variant)).getFirst();
	}

	public BoatEntityRenderState getRenderState() {
		return new BoatEntityRenderState();
	}

	public void updateRenderState(BoatEntity boatEntity, BoatEntityRenderState boatEntityRenderState, float f) {
		super.updateRenderState(boatEntity, boatEntityRenderState, f);
		boatEntityRenderState.yaw = boatEntity.getLerpedYaw(f);
		boatEntityRenderState.damageWobbleTicks = (float)boatEntity.getDamageWobbleTicks() - f;
		boatEntityRenderState.damageWobbleSide = boatEntity.getDamageWobbleSide();
		boatEntityRenderState.damageWobbleStrength = Math.max(boatEntity.getDamageWobbleStrength() - f, 0.0F);
		boatEntityRenderState.bubbleWobble = boatEntity.interpolateBubbleWobble(f);
		boatEntityRenderState.submergedInWater = boatEntity.isSubmergedInWater();
		boatEntityRenderState.variant = boatEntity.getVariant();
		boatEntityRenderState.leftPaddleAngle = boatEntity.interpolatePaddlePhase(0, f);
		boatEntityRenderState.rightPaddleAngle = boatEntity.interpolatePaddlePhase(1, f);
	}
}
