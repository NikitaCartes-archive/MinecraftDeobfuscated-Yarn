package net.minecraft.client.render.entity.feature;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_8293;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.TailEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TailFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	private static final int field_44423 = -1;
	private static final List<Identifier> TEXTURES = List.of(
		new Identifier("textures/entity/player/tails/red_fox.png"),
		new Identifier("textures/entity/player/tails/snow_fox.png"),
		new Identifier("textures/entity/player/tails/alex.png"),
		new Identifier("textures/entity/player/tails/ari.png"),
		new Identifier("textures/entity/player/tails/efe.png"),
		new Identifier("textures/entity/player/tails/kai.png"),
		new Identifier("textures/entity/player/tails/makena.png"),
		new Identifier("textures/entity/player/tails/noor.png"),
		new Identifier("textures/entity/player/tails/steve.png"),
		new Identifier("textures/entity/player/tails/sunny.png"),
		new Identifier("textures/entity/player/tails/zuri.png"),
		new Identifier("textures/entity/player/tails/brown_bear.png"),
		new Identifier("textures/entity/player/tails/striped.png"),
		new Identifier("textures/entity/player/tails/black_fox.png"),
		new Identifier("textures/entity/player/tails/earthern.png"),
		new Identifier("textures/entity/player/tails/fire_fox.png")
	);
	private final TailEntityModel<T> model;

	public TailFeatureRenderer(FeatureRendererContext<T, M> context, EntityModelLoader loader) {
		super(context);
		this.model = new TailEntityModel<>(loader.getModelPart(EntityModelLayers.TAIL));
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l
	) {
		if (class_8293.field_43649.method_50116()) {
			matrixStack.push();
			this.model.setAngles(livingEntity, f, g, j, k, l);
			int m = Math.floorMod(livingEntity.getUuid().getLeastSignificantBits(), TEXTURES.size());
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer((Identifier)TEXTURES.get(m)));
			this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
			matrixStack.pop();
		}
	}
}
