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
	private static final float[] field_17131 = new float[]{1.75F, 0.25F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 1.25F, 0.75F, 0.0F, 0.0F};
	private static final float[] field_17132 = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.25F, 1.75F, 1.25F, 0.75F, 0.0F, 0.0F, 0.0F, 0.0F};
	private static final float[] field_17133 = new float[]{0.0F, 0.0F, 0.25F, 1.75F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.75F, 1.25F};
	private static final float[] field_17134 = new float[]{0.0F, 0.0F, 8.0F, -8.0F, -8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F, 8.0F, -8.0F};
	private static final float[] field_17135 = new float[]{-8.0F, -8.0F, -8.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 8.0F, 8.0F, 8.0F};
	private static final float[] field_17136 = new float[]{8.0F, -8.0F, 0.0F, 0.0F, -8.0F, -8.0F, 8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F};
	private final ModelPart field_27420;
	private final ModelPart body;
	private final ModelPart eye;
	private final ModelPart[] field_3380;
	private final ModelPart[] field_3378;

	public GuardianEntityModel(ModelPart modelPart) {
		this.field_27420 = modelPart;
		this.field_3380 = new ModelPart[12];
		this.body = modelPart.getChild("head");

		for (int i = 0; i < this.field_3380.length; i++) {
			this.field_3380[i] = this.body.getChild(method_32003(i));
		}

		this.eye = this.body.getChild("eye");
		this.field_3378 = new ModelPart[3];
		this.field_3378[0] = this.body.getChild("tail0");
		this.field_3378[1] = this.field_3378[0].getChild("tail1");
		this.field_3378[2] = this.field_3378[1].getChild("tail2");
	}

	private static String method_32003(int i) {
		return "spike" + i;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(
			"head",
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
			float f = method_32005(i, 0.0F, 0.0F);
			float g = method_32006(i, 0.0F, 0.0F);
			float h = method_32007(i, 0.0F, 0.0F);
			float j = (float) Math.PI * field_17131[i];
			float k = (float) Math.PI * field_17132[i];
			float l = (float) Math.PI * field_17133[i];
			modelPartData2.addChild(method_32003(i), modelPartBuilder, ModelTransform.of(f, g, h, j, k, l));
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
		return this.field_27420;
	}

	public void setAngles(GuardianEntity guardianEntity, float f, float g, float h, float i, float j) {
		float k = h - (float)guardianEntity.age;
		this.body.yaw = i * (float) (Math.PI / 180.0);
		this.body.pitch = j * (float) (Math.PI / 180.0);
		float l = (1.0F - guardianEntity.getTailAngle(k)) * 0.55F;
		this.method_24185(h, l);
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
		float m = guardianEntity.getSpikesExtension(k);
		this.field_3378[0].yaw = MathHelper.sin(m) * (float) Math.PI * 0.05F;
		this.field_3378[1].yaw = MathHelper.sin(m) * (float) Math.PI * 0.1F;
		this.field_3378[2].yaw = MathHelper.sin(m) * (float) Math.PI * 0.15F;
	}

	private void method_24185(float f, float g) {
		for (int i = 0; i < 12; i++) {
			this.field_3380[i].pivotX = method_32005(i, f, g);
			this.field_3380[i].pivotY = method_32006(i, f, g);
			this.field_3380[i].pivotZ = method_32007(i, f, g);
		}
	}

	private static float method_32004(int i, float f, float g) {
		return 1.0F + MathHelper.cos(f * 1.5F + (float)i) * 0.01F - g;
	}

	private static float method_32005(int i, float f, float g) {
		return field_17134[i] * method_32004(i, f, g);
	}

	private static float method_32006(int i, float f, float g) {
		return 16.0F + field_17135[i] * method_32004(i, f, g);
	}

	private static float method_32007(int i, float f, float g) {
		return field_17136[i] * method_32004(i, f, g);
	}
}
