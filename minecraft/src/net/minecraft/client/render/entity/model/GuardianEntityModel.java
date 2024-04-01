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
	private static final float[] SPIKE_PIVOTS_X;
	private static final float[] SPIKE_PIVOTS_Y;
	private static final float field_51139 = 16.0F;
	private static final float[] SPIKE_PIVOTS_Z;
	private static final float field_51140 = 9.4F;
	private static final float field_51141;
	private static final float field_51142;
	private static final float[] field_51143;
	private static final float[] field_51144;
	private static final float[] field_51145;
	private static final float[] field_51146;
	private static final float[] field_51147;
	private static final float[] field_51148;
	private static final float[] field_51149;
	private static final float field_51150 = 19.0F;
	private static final float[] field_51151;
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

	public static TexturedModelData getTexturedModelData(boolean bl) {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		int i = bl ? 3 : 0;
		ModelPartData modelPartData2 = modelPartData.addChild(
			EntityModelPartNames.HEAD,
			ModelPartBuilder.create()
				.uv(0, 0)
				.cuboid(-6.0F, (float)(10 + i * 2), -8.0F, 12.0F, (float)(12 - i * 2), 16.0F)
				.uv(0, 28)
				.cuboid(-8.0F, (float)(10 + i * 2), -6.0F, 2.0F, (float)(12 - i * 2), 12.0F)
				.uv(0, 28)
				.cuboid(6.0F, (float)(10 + i * 2), -6.0F, 2.0F, (float)(12 - i * 2), 12.0F, true)
				.uv(16, 40)
				.cuboid(-6.0F, (float)(8 + i * 2), -6.0F, 12.0F, 2.0F, 12.0F)
				.uv(16, 40)
				.cuboid(-6.0F, 22.0F, -6.0F, 12.0F, 2.0F, 12.0F),
			ModelTransform.NONE
		);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -4.5F, -1.0F, 2.0F, 9.0F, 2.0F);

		for (int j = 0; j < 12; j++) {
			if (bl) {
				float f = field_51148[j] + field_51147[j];
				float g = 19.0F + field_51149[j];
				float h = field_51151[j];
				float k = field_51143[j];
				float l = field_51144[j];
				float m = field_51145[j];
				modelPartData2.addChild(getSpikeName(j), modelPartBuilder, ModelTransform.of(f, g, h, k, l, m));
			} else {
				float f = SPIKE_PIVOTS_X[j];
				float g = 16.0F + SPIKE_PIVOTS_Y[j];
				float h = SPIKE_PIVOTS_Z[j];
				float k = SPIKE_PITCHES[j];
				float l = SPIKE_YAWS[j];
				float m = SPIKE_ROLLS[j];
				modelPartData2.addChild(getSpikeName(j), modelPartBuilder, ModelTransform.of(f, g, h, k, l, m));
			}
		}

		modelPartData2.addChild(
			"eye", ModelPartBuilder.create().uv(8, 0).cuboid(-1.0F, (float)(15 + i), 0.0F, 2.0F, 2.0F, 1.0F), ModelTransform.pivot(0.0F, 0.0F, -8.25F)
		);
		ModelPartData modelPartData3 = modelPartData2.addChild(
			"tail0", ModelPartBuilder.create().uv(40, 0).cuboid(-2.0F, (float)(14 + i), 7.0F, 4.0F, 4.0F, 8.0F), ModelTransform.NONE
		);
		ModelPartData modelPartData4 = modelPartData3.addChild(
			"tail1", ModelPartBuilder.create().uv(0, 54).cuboid(0.0F, (float)(14 + i), 0.0F, 3.0F, 3.0F, 7.0F), ModelTransform.pivot(-1.5F, 0.5F, 14.0F)
		);
		modelPartData4.addChild(
			"tail2",
			ModelPartBuilder.create().uv(41, 32).cuboid(0.0F, (float)(14 + i), 0.0F, 2.0F, 2.0F, 6.0F).uv(25, 19).cuboid(1.0F, 10.5F + (float)i, 3.0F, 1.0F, 9.0F, 9.0F),
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
		if (guardianEntity.isPotato()) {
			this.method_59345(h, l, guardianEntity.hasPassengers(), guardianEntity.hasVehicle());
		} else {
			this.updateSpikeExtensions(h, l);
		}

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
			this.spikes[i].pivotX = SPIKE_PIVOTS_X[i] * getAngle(i, animationProgress, extension);
			this.spikes[i].pivotY = 16.0F + SPIKE_PIVOTS_Y[i] * getAngle(i, animationProgress, extension);
			this.spikes[i].pivotZ = SPIKE_PIVOTS_Z[i] * getAngle(i, animationProgress, extension);
			this.spikes[i].roll = SPIKE_ROLLS[i];
		}

		for (int i = 0; i < 4; i++) {
			this.spikes[i].visible = true;
		}
	}

	private void method_59345(float f, float g, boolean bl, boolean bl2) {
		int i = bl ? -1 : 1;
		float[] fs = bl ? field_51146 : field_51145;

		for (int j = 0; j < 12; j++) {
			this.spikes[j].pivotX = field_51148[j] * getAngle(j, f, g) + field_51147[j];
			this.spikes[j].pivotY = 19.0F + (float)i * field_51149[j] * getAngle(j, f, g);
			this.spikes[j].pivotZ = field_51151[j] * getAngle(j, f, g);
			this.spikes[j].roll = fs[j];
		}

		if (bl && bl2) {
			for (int j = 0; j < 4; j++) {
				this.spikes[j].visible = false;
			}
		} else {
			for (int j = 0; j < 4; j++) {
				this.spikes[j].visible = true;
			}
		}
	}

	private static float getAngle(int index, float animationProgress, float magnitude) {
		return 1.0F + MathHelper.cos(animationProgress * 1.5F + (float)index) * 0.01F - magnitude;
	}

	static {
		for (int i = 0; i < 12; i++) {
			SPIKE_PITCHES[i] = (float) Math.PI * SPIKE_PITCHES[i];
			SPIKE_YAWS[i] = (float) Math.PI * SPIKE_YAWS[i];
			SPIKE_ROLLS[i] = (float) Math.PI * SPIKE_ROLLS[i];
		}

		SPIKE_PIVOTS_X = new float[]{0.0F, 0.0F, 8.0F, -8.0F, -8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F, 8.0F, -8.0F};
		SPIKE_PIVOTS_Y = new float[]{-8.0F, -8.0F, -8.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 8.0F, 8.0F, 8.0F};
		SPIKE_PIVOTS_Z = new float[]{8.0F, -8.0F, 0.0F, 0.0F, -8.0F, -8.0F, 8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F};
		field_51141 = (float)Math.atan2(2.0, 1.0);
		field_51142 = (float)Math.atan2(1.0, 2.0);
		field_51143 = new float[]{
			field_51141,
			field_51142,
			-field_51142,
			-field_51141,
			field_51141,
			field_51142,
			-field_51142,
			-field_51141,
			field_51141,
			field_51142,
			-field_51142,
			-field_51141
		};
		field_51144 = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
		field_51145 = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, -0.5F, -0.5F, -0.5F, -0.5F};
		field_51146 = new float[]{1.0F, 1.0F, 1.0F, 1.0F, 0.5F, 0.5F, 0.5F, 0.5F, -0.5F, -0.5F, -0.5F, -0.5F};
		field_51147 = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 3.0F, 3.0F, 3.0F, 3.0F, -3.0F, -3.0F, -3.0F, -3.0F};
		field_51148 = new float[]{
			0.0F,
			0.0F,
			0.0F,
			0.0F,
			MathHelper.cos(field_51141),
			MathHelper.cos(field_51142),
			MathHelper.cos(field_51142),
			MathHelper.cos(field_51141),
			-MathHelper.cos(field_51141),
			-MathHelper.cos(field_51142),
			-MathHelper.cos(field_51142),
			-MathHelper.cos(field_51141)
		};
		field_51149 = new float[]{
			-MathHelper.cos(field_51141),
			-MathHelper.cos(field_51142),
			-MathHelper.cos(field_51142),
			-MathHelper.cos(field_51141),
			0.0F,
			0.0F,
			0.0F,
			0.0F,
			0.0F,
			0.0F,
			0.0F,
			0.0F
		};
		field_51151 = new float[]{
			-MathHelper.sin(field_51141),
			-MathHelper.sin(field_51142),
			MathHelper.sin(field_51142),
			MathHelper.sin(field_51141),
			-MathHelper.sin(field_51141),
			-MathHelper.sin(field_51142),
			MathHelper.sin(field_51142),
			MathHelper.sin(field_51141),
			-MathHelper.sin(field_51141),
			-MathHelper.sin(field_51142),
			MathHelper.sin(field_51142),
			MathHelper.sin(field_51141)
		};

		for (int i = 0; i < 12; i++) {
			field_51145[i] = field_51145[i] * (float) Math.PI;
			field_51146[i] = field_51146[i] * (float) Math.PI;
			field_51148[i] = field_51148[i] * 9.4F;
			field_51149[i] = field_51149[i] * 9.4F;
			field_51151[i] = field_51151[i] * 9.4F;
		}
	}
}
