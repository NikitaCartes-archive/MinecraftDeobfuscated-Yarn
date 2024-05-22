package net.minecraft.client.render.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.render.entity.model.ChestBoatEntityModel;
import net.minecraft.client.render.entity.model.ChestRaftEntityModel;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ModelWithWaterPatch;
import net.minecraft.client.render.entity.model.RaftEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class BoatEntityRenderer extends EntityRenderer<BoatEntity> {
	private final Map<BoatEntity.Type, Pair<Identifier, CompositeEntityModel<BoatEntity>>> texturesAndModels;

	public BoatEntityRenderer(EntityRendererFactory.Context ctx, boolean chest) {
		super(ctx);
		this.shadowRadius = 0.8F;
		this.texturesAndModels = (Map<BoatEntity.Type, Pair<Identifier, CompositeEntityModel<BoatEntity>>>)Stream.of(BoatEntity.Type.values())
			.collect(ImmutableMap.toImmutableMap(type -> type, type -> Pair.of(getTexture(type, chest), this.createModel(ctx, type, chest))));
	}

	private CompositeEntityModel<BoatEntity> createModel(EntityRendererFactory.Context ctx, BoatEntity.Type type, boolean chest) {
		EntityModelLayer entityModelLayer = chest ? EntityModelLayers.createChestBoat(type) : EntityModelLayers.createBoat(type);
		ModelPart modelPart = ctx.getPart(entityModelLayer);
		if (type == BoatEntity.Type.BAMBOO) {
			return (CompositeEntityModel<BoatEntity>)(chest ? new ChestRaftEntityModel(modelPart) : new RaftEntityModel(modelPart));
		} else {
			return (CompositeEntityModel<BoatEntity>)(chest ? new ChestBoatEntityModel(modelPart) : new BoatEntityModel(modelPart));
		}
	}

	private static Identifier getTexture(BoatEntity.Type type, boolean chest) {
		return chest
			? Identifier.ofVanilla("textures/entity/chest_boat/" + type.getName() + ".png")
			: Identifier.ofVanilla("textures/entity/boat/" + type.getName() + ".png");
	}

	public void render(BoatEntity boatEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		matrixStack.translate(0.0F, 0.375F, 0.0F);
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - f));
		float h = (float)boatEntity.getDamageWobbleTicks() - g;
		float j = boatEntity.getDamageWobbleStrength() - g;
		if (j < 0.0F) {
			j = 0.0F;
		}

		if (h > 0.0F) {
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.sin(h) * h * j / 10.0F * (float)boatEntity.getDamageWobbleSide()));
		}

		float k = boatEntity.interpolateBubbleWobble(g);
		if (!MathHelper.approximatelyEquals(k, 0.0F)) {
			matrixStack.multiply(new Quaternionf().setAngleAxis(boatEntity.interpolateBubbleWobble(g) * (float) (Math.PI / 180.0), 1.0F, 0.0F, 1.0F));
		}

		Pair<Identifier, CompositeEntityModel<BoatEntity>> pair = (Pair<Identifier, CompositeEntityModel<BoatEntity>>)this.texturesAndModels
			.get(boatEntity.getVariant());
		Identifier identifier = pair.getFirst();
		CompositeEntityModel<BoatEntity> compositeEntityModel = pair.getSecond();
		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
		compositeEntityModel.setAngles(boatEntity, g, 0.0F, -0.1F, 0.0F, 0.0F);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(compositeEntityModel.getLayer(identifier));
		compositeEntityModel.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
		if (!boatEntity.isSubmergedInWater()) {
			VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(RenderLayer.getWaterMask());
			if (compositeEntityModel instanceof ModelWithWaterPatch modelWithWaterPatch) {
				modelWithWaterPatch.getWaterPatch().render(matrixStack, vertexConsumer2, i, OverlayTexture.DEFAULT_UV);
			}
		}

		matrixStack.pop();
		super.render(boatEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	public Identifier getTexture(BoatEntity boatEntity) {
		return (Identifier)((Pair)this.texturesAndModels.get(boatEntity.getVariant())).getFirst();
	}
}
