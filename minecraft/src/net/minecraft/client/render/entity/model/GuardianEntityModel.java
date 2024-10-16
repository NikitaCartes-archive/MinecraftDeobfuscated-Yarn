package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.state.GuardianEntityRenderState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class GuardianEntityModel extends EntityModel<GuardianEntityRenderState> {
	public static final ModelTransformer ELDER_TRANSFORMER = ModelTransformer.scaling(2.35F);
	private static final float[] SPIKE_PITCHES = new float[]{1.75F, 0.25F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 1.25F, 0.75F, 0.0F, 0.0F};
	private static final float[] SPIKE_YAWS = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.25F, 1.75F, 1.25F, 0.75F, 0.0F, 0.0F, 0.0F, 0.0F};
	private static final float[] SPIKE_ROLLS = new float[]{0.0F, 0.0F, 0.25F, 1.75F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.75F, 1.25F};
	private static final float[] SPIKE_PIVOTS_X = new float[]{0.0F, 0.0F, 8.0F, -8.0F, -8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F, 8.0F, -8.0F};
	private static final float[] SPIKE_PIVOTS_Y = new float[]{-8.0F, -8.0F, -8.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 8.0F, 8.0F, 8.0F};
	private static final float[] SPIKE_PIVOTS_Z = new float[]{8.0F, -8.0F, 0.0F, 0.0F, -8.0F, -8.0F, 8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F};
	/**
	 * The key of the eye model part, whose value is {@value}.
	 */
	private static final String EYE = "eye";
	/**
	 * The key of the tail0 model part, whose value is {@value}.
	 */
	private static final String TAIL0 = "tail0";
	/**
	 * The key of the tail1 model part, whose value is {@value}.
	 */
	private static final String TAIL1 = "tail1";
	/**
	 * The key of the tail2 model part, whose value is {@value}.
	 */
	private static final String TAIL2 = "tail2";
	private final ModelPart head;
	private final ModelPart eye;
	private final ModelPart[] spikes = new ModelPart[12];
	private final ModelPart[] tail;

	public GuardianEntityModel(ModelPart modelPart) {
		super(modelPart);
		this.head = modelPart.getChild(EntityModelPartNames.HEAD);

		for (int i = 0; i < this.spikes.length; i++) {
			this.spikes[i] = this.head.getChild(getSpikeName(i));
		}

		this.eye = this.head.getChild("eye");
		this.tail = new ModelPart[3];
		this.tail[0] = this.head.getChild("tail0");
		this.tail[1] = this.tail[0].getChild("tail1");
		this.tail[2] = this.tail[1].getChild("tail2");
	}

	private static String getSpikeName(int index) {
		return "spike" + index;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(
			EntityModelPartNames.HEAD,
			ModelPartBuilder.create()
				.uv(0, 0)
				.cuboid(-6.0F, 10.0F, -8.0F, 12.0F, 12.0F, 16.0F)
				.uv(0, 28)
				.cuboid(-8.0F, 10.0F, -6.0F, 2.0F, 12.0F, 12.0F)
				.uv(0, 28)
				.cuboid(6.0F, 10.0F, -6.0F, 2.0F, 12.0F, 12.0F, true)
				.uv(16, 40)
				.cuboid(-6.0F, 8.0F, -6.0F, 12.0F, 2.0F, 12.0F)
				.uv(16, 40)
				.cuboid(-6.0F, 22.0F, -6.0F, 12.0F, 2.0F, 12.0F),
			ModelTransform.NONE
		);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -4.5F, -1.0F, 2.0F, 9.0F, 2.0F);

		for (int i = 0; i < 12; i++) {
			float f = getSpikePivotX(i, 0.0F, 0.0F);
			float g = getSpikePivotY(i, 0.0F, 0.0F);
			float h = getSpikePivotZ(i, 0.0F, 0.0F);
			float j = (float) Math.PI * SPIKE_PITCHES[i];
			float k = (float) Math.PI * SPIKE_YAWS[i];
			float l = (float) Math.PI * SPIKE_ROLLS[i];
			modelPartData2.addChild(getSpikeName(i), modelPartBuilder, ModelTransform.of(f, g, h, j, k, l));
		}

		modelPartData2.addChild("eye", ModelPartBuilder.create().uv(8, 0).cuboid(-1.0F, 15.0F, 0.0F, 2.0F, 2.0F, 1.0F), ModelTransform.pivot(0.0F, 0.0F, -8.25F));
		ModelPartData modelPartData3 = modelPartData2.addChild(
			"tail0", ModelPartBuilder.create().uv(40, 0).cuboid(-2.0F, 14.0F, 7.0F, 4.0F, 4.0F, 8.0F), ModelTransform.NONE
		);
		ModelPartData modelPartData4 = modelPartData3.addChild(
			"tail1", ModelPartBuilder.create().uv(0, 54).cuboid(0.0F, 14.0F, 0.0F, 3.0F, 3.0F, 7.0F), ModelTransform.pivot(-1.5F, 0.5F, 14.0F)
		);
		modelPartData4.addChild(
			"tail2",
			ModelPartBuilder.create().uv(41, 32).cuboid(0.0F, 14.0F, 0.0F, 2.0F, 2.0F, 6.0F).uv(25, 19).cuboid(1.0F, 10.5F, 3.0F, 1.0F, 9.0F, 9.0F),
			ModelTransform.pivot(0.5F, 0.5F, 6.0F)
		);
		return TexturedModelData.of(modelData, 64, 64);
	}

	public static TexturedModelData getElderTexturedModelData() {
		return getTexturedModelData().transform(ELDER_TRANSFORMER);
	}

	public void setAngles(GuardianEntityRenderState guardianEntityRenderState) {
		super.setAngles(guardianEntityRenderState);
		this.head.yaw = guardianEntityRenderState.yawDegrees * (float) (Math.PI / 180.0);
		this.head.pitch = guardianEntityRenderState.pitch * (float) (Math.PI / 180.0);
		float f = (1.0F - guardianEntityRenderState.spikesExtension) * 0.55F;
		this.updateSpikeExtensions(guardianEntityRenderState.age, f);
		if (guardianEntityRenderState.lookAtPos != null && guardianEntityRenderState.rotationVec != null) {
			double d = guardianEntityRenderState.lookAtPos.y - guardianEntityRenderState.cameraPosVec.y;
			if (d > 0.0) {
				this.eye.pivotY = 0.0F;
			} else {
				this.eye.pivotY = 1.0F;
			}

			Vec3d vec3d = guardianEntityRenderState.rotationVec;
			vec3d = new Vec3d(vec3d.x, 0.0, vec3d.z);
			Vec3d vec3d2 = new Vec3d(
					guardianEntityRenderState.cameraPosVec.x - guardianEntityRenderState.lookAtPos.x,
					0.0,
					guardianEntityRenderState.cameraPosVec.z - guardianEntityRenderState.lookAtPos.z
				)
				.normalize()
				.rotateY((float) (Math.PI / 2));
			double e = vec3d.dotProduct(vec3d2);
			this.eye.pivotX = MathHelper.sqrt((float)Math.abs(e)) * 2.0F * (float)Math.signum(e);
		}

		this.eye.visible = true;
		float g = guardianEntityRenderState.tailAngle;
		this.tail[0].yaw = MathHelper.sin(g) * (float) Math.PI * 0.05F;
		this.tail[1].yaw = MathHelper.sin(g) * (float) Math.PI * 0.1F;
		this.tail[2].yaw = MathHelper.sin(g) * (float) Math.PI * 0.15F;
	}

	private void updateSpikeExtensions(float animationProgress, float extension) {
		for (int i = 0; i < 12; i++) {
			this.spikes[i].pivotX = getSpikePivotX(i, animationProgress, extension);
			this.spikes[i].pivotY = getSpikePivotY(i, animationProgress, extension);
			this.spikes[i].pivotZ = getSpikePivotZ(i, animationProgress, extension);
		}
	}

	private static float getAngle(int index, float animationProgress, float magnitude) {
		return 1.0F + MathHelper.cos(animationProgress * 1.5F + (float)index) * 0.01F - magnitude;
	}

	private static float getSpikePivotX(int index, float animationProgress, float extension) {
		return SPIKE_PIVOTS_X[index] * getAngle(index, animationProgress, extension);
	}

	private static float getSpikePivotY(int index, float animationProgress, float extension) {
		return 16.0F + SPIKE_PIVOTS_Y[index] * getAngle(index, animationProgress, extension);
	}

	private static float getSpikePivotZ(int index, float animationProgress, float extension) {
		return SPIKE_PIVOTS_Z[index] * getAngle(index, animationProgress, extension);
	}
}
