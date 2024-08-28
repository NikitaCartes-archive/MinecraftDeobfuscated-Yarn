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
import net.minecraft.client.render.entity.state.TurtleEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TurtleEntityModel extends QuadrupedEntityModel<TurtleEntityRenderState> {
	/**
	 * The key of the model part of the belly side of the turtle's shell, whose value is {@value}.
	 */
	private static final String EGG_BELLY = "egg_belly";
	public static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(true, 120.0F, 0.0F, 9.0F, 6.0F, 120.0F, Set.of("head"));
	/**
	 * The belly side of the turtle's shell.
	 */
	private final ModelPart plastron;

	public TurtleEntityModel(ModelPart modelPart) {
		super(modelPart);
		this.plastron = modelPart.getChild("egg_belly");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(3, 0).cuboid(-3.0F, -1.0F, -3.0F, 6.0F, 5.0F, 6.0F), ModelTransform.pivot(0.0F, 19.0F, -10.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create()
				.uv(7, 37)
				.cuboid("shell", -9.5F, 3.0F, -10.0F, 19.0F, 20.0F, 6.0F)
				.uv(31, 1)
				.cuboid("belly", -5.5F, 3.0F, -13.0F, 11.0F, 18.0F, 3.0F),
			ModelTransform.of(0.0F, 11.0F, -10.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		modelPartData.addChild(
			"egg_belly",
			ModelPartBuilder.create().uv(70, 33).cuboid(-4.5F, 3.0F, -14.0F, 9.0F, 18.0F, 1.0F),
			ModelTransform.of(0.0F, 11.0F, -10.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		int i = 1;
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_HIND_LEG,
			ModelPartBuilder.create().uv(1, 23).cuboid(-2.0F, 0.0F, 0.0F, 4.0F, 1.0F, 10.0F),
			ModelTransform.pivot(-3.5F, 22.0F, 11.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_HIND_LEG,
			ModelPartBuilder.create().uv(1, 12).cuboid(-2.0F, 0.0F, 0.0F, 4.0F, 1.0F, 10.0F),
			ModelTransform.pivot(3.5F, 22.0F, 11.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_FRONT_LEG,
			ModelPartBuilder.create().uv(27, 30).cuboid(-13.0F, 0.0F, -2.0F, 13.0F, 1.0F, 5.0F),
			ModelTransform.pivot(-5.0F, 21.0F, -4.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_FRONT_LEG,
			ModelPartBuilder.create().uv(27, 24).cuboid(0.0F, 0.0F, -2.0F, 13.0F, 1.0F, 5.0F),
			ModelTransform.pivot(5.0F, 21.0F, -4.0F)
		);
		return TexturedModelData.of(modelData, 128, 64);
	}

	public void setAngles(TurtleEntityRenderState turtleEntityRenderState) {
		super.setAngles(turtleEntityRenderState);
		float f = turtleEntityRenderState.limbFrequency;
		float g = turtleEntityRenderState.limbAmplitudeMultiplier;
		if (turtleEntityRenderState.onLand) {
			float h = turtleEntityRenderState.diggingSand ? 4.0F : 1.0F;
			float i = turtleEntityRenderState.diggingSand ? 2.0F : 1.0F;
			float j = f * 5.0F;
			float k = MathHelper.cos(h * j);
			float l = MathHelper.cos(j);
			this.rightFrontLeg.yaw = -k * 8.0F * g * i;
			this.leftFrontLeg.yaw = k * 8.0F * g * i;
			this.rightHindLeg.yaw = -l * 3.0F * g;
			this.leftHindLeg.yaw = l * 3.0F * g;
		} else {
			float h = 0.5F * g;
			float i = MathHelper.cos(f * 0.6662F * 0.6F) * h;
			this.rightHindLeg.pitch = i;
			this.leftHindLeg.pitch = -i;
			this.rightFrontLeg.roll = -i;
			this.leftFrontLeg.roll = i;
		}

		this.plastron.visible = turtleEntityRenderState.hasEgg;
		if (this.plastron.visible) {
			this.root.pivotY--;
		}
	}
}
