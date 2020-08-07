package net.minecraft.client.render.entity.feature;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.HorseMarking;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class HorseMarkingFeatureRenderer extends FeatureRenderer<HorseEntity, HorseEntityModel<HorseEntity>> {
	private static final Map<HorseMarking, Identifier> TEXTURES = Util.make(Maps.newEnumMap(HorseMarking.class), enumMap -> {
		enumMap.put(HorseMarking.field_23808, null);
		enumMap.put(HorseMarking.field_23809, new Identifier("textures/entity/horse/horse_markings_white.png"));
		enumMap.put(HorseMarking.field_23810, new Identifier("textures/entity/horse/horse_markings_whitefield.png"));
		enumMap.put(HorseMarking.field_23811, new Identifier("textures/entity/horse/horse_markings_whitedots.png"));
		enumMap.put(HorseMarking.field_23812, new Identifier("textures/entity/horse/horse_markings_blackdots.png"));
	});

	public HorseMarkingFeatureRenderer(FeatureRendererContext<HorseEntity, HorseEntityModel<HorseEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_27152(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, HorseEntity horseEntity, float f, float g, float h, float j, float k, float l
	) {
		Identifier identifier = (Identifier)TEXTURES.get(horseEntity.getMarking());
		if (identifier != null && !horseEntity.isInvisible()) {
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(identifier));
			this.getContextModel().render(matrixStack, vertexConsumer, i, LivingEntityRenderer.getOverlay(horseEntity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
}
