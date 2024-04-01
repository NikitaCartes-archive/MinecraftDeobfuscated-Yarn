package net.minecraft.client.render.entity.model;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.animation.BatAnimations;
import net.minecraft.entity.passive.BatatoEntity;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class BatatoEntityModel extends SinglePartEntityModel<BatatoEntity> {
	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart rightWing;
	private final ModelPart leftWing;
	private final ModelPart rightWingTip;
	private final ModelPart leftWingTip;
	private final ModelPart feet;

	public BatatoEntityModel(ModelPart root) {
		super(RenderLayer::getEntityCutout);
		this.root = root;
		this.body = root.getChild(EntityModelPartNames.BODY);
		this.rightWing = this.body.getChild(EntityModelPartNames.RIGHT_WING);
		this.rightWingTip = this.rightWing.getChild(EntityModelPartNames.RIGHT_WING_TIP);
		this.leftWing = this.body.getChild(EntityModelPartNames.LEFT_WING);
		this.leftWingTip = this.leftWing.getChild(EntityModelPartNames.LEFT_WING_TIP);
		this.feet = this.body.getChild(EntityModelPartNames.FEET);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create()
				.uv(19, 20)
				.cuboid(-1.5F, -2.0F, 0.0F, 13.0F, 12.0F, 0.01F, Set.of(Direction.NORTH))
				.uv(6, 20)
				.mirrored()
				.cuboid(-1.5F, -2.0F, 0.0F, 13.0F, 12.0F, 0.01F, Set.of(Direction.SOUTH)),
			ModelTransform.pivot(0.0F, 17.0F, 0.0F)
		);
		ModelPartData modelPartData3 = modelPartData2.addChild(
			EntityModelPartNames.RIGHT_WING, ModelPartBuilder.create().uv(12, 0).cuboid(-2.0F, -2.0F, 0.0F, 2.0F, 7.0F, 0.0F), ModelTransform.pivot(-1.5F, 0.0F, 0.0F)
		);
		modelPartData3.addChild(
			EntityModelPartNames.RIGHT_WING_TIP,
			ModelPartBuilder.create().uv(16, 0).cuboid(-6.0F, -2.0F, 0.0F, 6.0F, 8.0F, 0.0F),
			ModelTransform.pivot(-2.0F, 0.0F, 0.0F)
		);
		ModelPartData modelPartData4 = modelPartData2.addChild(
			EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().uv(12, 7).cuboid(0.0F, -2.0F, 0.0F, 2.0F, 7.0F, 0.0F), ModelTransform.pivot(11.5F, 2.0F, 0.0F)
		);
		modelPartData4.addChild(
			EntityModelPartNames.LEFT_WING_TIP, ModelPartBuilder.create().uv(16, 8).cuboid(0.0F, -2.0F, 0.0F, 6.0F, 8.0F, 0.0F), ModelTransform.pivot(2.0F, 0.0F, 0.0F)
		);
		modelPartData2.addChild(
			EntityModelPartNames.FEET, ModelPartBuilder.create().uv(16, 16).cuboid(-1.5F, 0.0F, 0.0F, 3.0F, 2.0F, 0.0F), ModelTransform.pivot(3.0F, 10.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	public void setAngles(BatatoEntity batatoEntity, float f, float g, float h, float i, float j) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		this.updateAnimation(batatoEntity.flyingAnimationState, BatAnimations.FLYING, h, 1.0F);
		this.updateAnimation(batatoEntity.roostingAnimationState, BatAnimations.ROOSTING, h, 1.0F);
	}
}
