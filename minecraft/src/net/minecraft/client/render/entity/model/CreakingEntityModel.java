package net.minecraft.client.render.entity.model;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.animation.CreakingAnimations;
import net.minecraft.client.render.entity.state.CreakingEntityRenderState;

@Environment(EnvType.CLIENT)
public class CreakingEntityModel extends EntityModel<CreakingEntityRenderState> {
	public static final List<ModelPart> INACTIVE_EMISSIVE_PARTS = List.of();
	private final ModelPart head;
	private final List<ModelPart> activeEmissiveParts;

	public CreakingEntityModel(ModelPart modelPart) {
		super(modelPart);
		ModelPart modelPart2 = modelPart.getChild(EntityModelPartNames.ROOT);
		ModelPart modelPart3 = modelPart2.getChild(EntityModelPartNames.UPPER_BODY);
		this.head = modelPart3.getChild(EntityModelPartNames.HEAD);
		this.activeEmissiveParts = List.of(this.head);
	}

	private static ModelData getModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.ROOT, ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		ModelPartData modelPartData3 = modelPartData2.addChild(EntityModelPartNames.UPPER_BODY, ModelPartBuilder.create(), ModelTransform.pivot(-1.0F, -19.0F, 0.0F));
		modelPartData3.addChild(
			EntityModelPartNames.HEAD,
			ModelPartBuilder.create()
				.uv(0, 0)
				.cuboid(-3.0F, -10.0F, -3.0F, 6.0F, 10.0F, 6.0F)
				.uv(28, 31)
				.cuboid(-3.0F, -13.0F, -3.0F, 6.0F, 3.0F, 6.0F)
				.uv(12, 40)
				.cuboid(3.0F, -13.0F, 0.0F, 9.0F, 14.0F, 0.0F)
				.uv(34, 12)
				.cuboid(-12.0F, -14.0F, 0.0F, 9.0F, 14.0F, 0.0F),
			ModelTransform.pivot(-3.0F, -11.0F, 0.0F)
		);
		modelPartData3.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create().uv(0, 16).cuboid(0.0F, -3.0F, -3.0F, 6.0F, 13.0F, 5.0F).uv(24, 0).cuboid(-6.0F, -4.0F, -3.0F, 6.0F, 7.0F, 5.0F),
			ModelTransform.pivot(0.0F, -7.0F, 1.0F)
		);
		modelPartData3.addChild(
			EntityModelPartNames.RIGHT_ARM,
			ModelPartBuilder.create().uv(22, 13).cuboid(-2.0F, -1.5F, -1.5F, 3.0F, 21.0F, 3.0F).uv(46, 0).cuboid(-2.0F, 19.5F, -1.5F, 3.0F, 4.0F, 3.0F),
			ModelTransform.pivot(-7.0F, -9.5F, 1.5F)
		);
		modelPartData3.addChild(
			EntityModelPartNames.LEFT_ARM,
			ModelPartBuilder.create()
				.uv(30, 40)
				.cuboid(0.0F, -1.0F, -1.5F, 3.0F, 16.0F, 3.0F)
				.uv(52, 12)
				.cuboid(0.0F, -5.0F, -1.5F, 3.0F, 4.0F, 3.0F)
				.uv(52, 19)
				.cuboid(0.0F, 15.0F, -1.5F, 3.0F, 4.0F, 3.0F),
			ModelTransform.pivot(6.0F, -9.0F, 0.5F)
		);
		modelPartData2.addChild(
			EntityModelPartNames.LEFT_LEG,
			ModelPartBuilder.create().uv(42, 40).cuboid(-1.5F, 0.0F, -1.5F, 3.0F, 16.0F, 3.0F).uv(45, 55).cuboid(-1.5F, 15.7F, -4.5F, 5.0F, 0.0F, 9.0F),
			ModelTransform.pivot(1.5F, -16.0F, 0.5F)
		);
		modelPartData2.addChild(
			EntityModelPartNames.RIGHT_LEG,
			ModelPartBuilder.create()
				.uv(0, 34)
				.cuboid(-3.0F, -1.5F, -1.5F, 3.0F, 19.0F, 3.0F)
				.uv(45, 46)
				.cuboid(-5.0F, 17.2F, -4.5F, 5.0F, 0.0F, 9.0F)
				.uv(12, 34)
				.cuboid(-3.0F, -4.5F, -1.5F, 3.0F, 3.0F, 3.0F),
			ModelTransform.pivot(-1.0F, -17.5F, 0.5F)
		);
		return modelData;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = getModelData();
		return TexturedModelData.of(modelData, 64, 64);
	}

	public void setAngles(CreakingEntityRenderState creakingEntityRenderState) {
		this.getRootPart().traverse().forEach(ModelPart::resetTransform);
		if (creakingEntityRenderState.unrooted) {
			this.animateWalking(CreakingAnimations.WALKING, creakingEntityRenderState.limbFrequency, creakingEntityRenderState.limbAmplitudeMultiplier, 5.5F, 3.0F);
		}

		this.animate(creakingEntityRenderState.attackAnimationState, CreakingAnimations.ATTACKING, creakingEntityRenderState.age);
		this.animate(creakingEntityRenderState.invulnerableAnimationState, CreakingAnimations.INVULNERABLE, creakingEntityRenderState.age);
	}

	public List<ModelPart> getEmissiveParts(CreakingEntityRenderState state) {
		return !state.active ? INACTIVE_EMISSIVE_PARTS : this.activeEmissiveParts;
	}
}
