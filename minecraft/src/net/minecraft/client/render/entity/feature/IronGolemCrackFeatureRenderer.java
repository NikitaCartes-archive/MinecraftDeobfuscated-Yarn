package net.minecraft.client.render.entity.feature;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.render.entity.state.IronGolemEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.Cracks;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class IronGolemCrackFeatureRenderer extends FeatureRenderer<IronGolemEntityRenderState, IronGolemEntityModel> {
	private static final Map<Cracks.CrackLevel, Identifier> CRACK_TEXTURES = ImmutableMap.of(
		Cracks.CrackLevel.LOW,
		Identifier.ofVanilla("textures/entity/iron_golem/iron_golem_crackiness_low.png"),
		Cracks.CrackLevel.MEDIUM,
		Identifier.ofVanilla("textures/entity/iron_golem/iron_golem_crackiness_medium.png"),
		Cracks.CrackLevel.HIGH,
		Identifier.ofVanilla("textures/entity/iron_golem/iron_golem_crackiness_high.png")
	);

	public IronGolemCrackFeatureRenderer(FeatureRendererContext<IronGolemEntityRenderState, IronGolemEntityModel> featureRendererContext) {
		super(featureRendererContext);
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, IronGolemEntityRenderState ironGolemEntityRenderState, float f, float g
	) {
		if (!ironGolemEntityRenderState.invisible) {
			Cracks.CrackLevel crackLevel = ironGolemEntityRenderState.crackLevel;
			if (crackLevel != Cracks.CrackLevel.NONE) {
				Identifier identifier = (Identifier)CRACK_TEXTURES.get(crackLevel);
				renderModel(this.getContextModel(), identifier, matrixStack, vertexConsumerProvider, i, ironGolemEntityRenderState, -1);
			}
		}
	}
}
