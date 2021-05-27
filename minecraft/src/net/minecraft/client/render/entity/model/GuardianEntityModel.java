package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class GuardianEntityModel extends SinglePartEntityModel<GuardianEntity> {
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
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart eye;
	private final ModelPart[] spikes;
	private final ModelPart[] tail;

	public GuardianEntityModel(ModelPart root) {
		this.root = root;
		this.spikes = new ModelPart[12];
		this.head = root.getChild(EntityModelPartNames.HEAD);

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

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	public void setAngles(GuardianEntity guardianEntity, float f, float g, float h, float i, float j) {
		float k = h - (float)guardianEntity.age;
		this.head.yaw = i * (float) (Math.PI / 180.0);
		this.head.pitch = j * (float) (Math.PI / 180.0);
		float l = (1.0F - guardianEntity.getSpikesExtension(k)) * 0.55F;
		this.updateSpikeExtensions(h, l);
		Entity entity = MinecraftClient.getInstance().getCameraEntity();
		if (guardianEntity.hasBeamTarget()) {
			entity = guardianEntity.getBeamTarget();
		}

		if (entity != null) {
			Vec3d vec3d = entity.getCameraPosVec(0.0F);
			Vec3d vec3d2 = guardianEntity.getCameraPosVec(0.0F);
			double d = vec3d.y - vec3d2.y;
			if (d > 0.0) {
				this.eye.pivotY = 0.0F;
			} else {
				this.eye.pivotY = 1.0F;
			}

			Vec3d vec3d3 = guardianEntity.getRotationVec(0.0F);
			vec3d3 = new Vec3d(vec3d3.x, 0.0, vec3d3.z);
			Vec3d vec3d4 = new Vec3d(vec3d2.x - vec3d.x, 0.0, vec3d2.z - vec3d.z).normalize().rotateY((float) (Math.PI / 2));
			double e = vec3d3.dotProduct(vec3d4);
			this.eye.pivotX = MathHelper.sqrt((float)Math.abs(e)) * 2.0F * (float)Math.signum(e);
		}

		this.eye.visible = true;
		float m = guardianEntity.getTailAngle(k);
		this.tail[0].yaw = MathHelper.sin(m) * (float) Math.PI * 0.05F;
		this.tail[1].yaw = MathHelper.sin(m) * (float) Math.PI * 0.1F;
		this.tail[2].yaw = MathHelper.sin(m) * (float) Math.PI * 0.15F;
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
