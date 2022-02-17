package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WardenFeatureRenderer<T extends WardenEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	private final Identifier field_37010;
	private final WardenFeatureRenderer.class_7037<T> field_37011;

	public WardenFeatureRenderer(FeatureRendererContext<T, M> context, Identifier identifier, WardenFeatureRenderer.class_7037<T> arg) {
		super(context);
		this.field_37010 = identifier;
		this.field_37011 = arg;
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T wardenEntity, float f, float g, float h, float j, float k, float l
	) {
		if (!wardenEntity.isInvisible()) {
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.method_40948(this.field_37010));
			this.getContextModel()
				.render(matrixStack, vertexConsumer, i, LivingEntityRenderer.getOverlay(wardenEntity, 0.0F), 1.0F, 1.0F, 1.0F, this.field_37011.apply(wardenEntity, h, j));
		}
	}

	@Environment(EnvType.CLIENT)
	public interface class_7037<T extends WardenEntity> {
		float apply(T wardenEntity, float f, float g);
	}
}
