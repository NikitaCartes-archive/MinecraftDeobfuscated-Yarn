package net.minecraft.client.render.entity.feature;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class IronGolemCrackFeatureRenderer extends FeatureRenderer<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> {
	private static final Map<IronGolemEntity.class_4621, Identifier> field_21443 = ImmutableMap.of(
		IronGolemEntity.class_4621.LOW,
		new Identifier("textures/entity/iron_golem/iron_golem_crackiness_low.png"),
		IronGolemEntity.class_4621.MEDIUM,
		new Identifier("textures/entity/iron_golem/iron_golem_crackiness_medium.png"),
		IronGolemEntity.class_4621.HIGH,
		new Identifier("textures/entity/iron_golem/iron_golem_crackiness_high.png")
	);

	public IronGolemCrackFeatureRenderer(FeatureRendererContext<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_23623(
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		IronGolemEntity ironGolemEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l,
		float m
	) {
		IronGolemEntity.class_4621 lv = ironGolemEntity.method_23347();
		if (lv != IronGolemEntity.class_4621.NONE) {
			Identifier identifier = (Identifier)field_21443.get(lv);
			method_23199(this.getModel(), identifier, matrixStack, layeredVertexConsumerStorage, i, ironGolemEntity, 1.0F, 1.0F, 1.0F);
		}
	}
}
