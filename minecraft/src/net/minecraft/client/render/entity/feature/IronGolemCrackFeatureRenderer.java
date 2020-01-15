package net.minecraft.client.render.entity.feature;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class IronGolemCrackFeatureRenderer extends FeatureRenderer<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> {
	private static final Map<IronGolemEntity.Crack, Identifier> DAMAGE_TO_TEXTURE = ImmutableMap.of(
		IronGolemEntity.Crack.LOW,
		new Identifier("textures/entity/iron_golem/iron_golem_crackiness_low.png"),
		IronGolemEntity.Crack.MEDIUM,
		new Identifier("textures/entity/iron_golem/iron_golem_crackiness_medium.png"),
		IronGolemEntity.Crack.HIGH,
		new Identifier("textures/entity/iron_golem/iron_golem_crackiness_high.png")
	);

	public IronGolemCrackFeatureRenderer(FeatureRendererContext<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void render(
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int i,
		IronGolemEntity ironGolemEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l
	) {
		if (!ironGolemEntity.isInvisible()) {
			IronGolemEntity.Crack crack = ironGolemEntity.getCrack();
			if (crack != IronGolemEntity.Crack.NONE) {
				Identifier identifier = (Identifier)DAMAGE_TO_TEXTURE.get(crack);
				renderModel(this.getContextModel(), identifier, matrixStack, vertexConsumerProvider, i, ironGolemEntity, 1.0F, 1.0F, 1.0F);
			}
		}
	}
}
