package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5597;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class GuardianEntityModel extends class_5597<GuardianEntity> {
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
		this.body = modelPart.method_32086("head");

		for (int i = 0; i < this.field_3380.length; i++) {
			this.field_3380[i] = this.body.method_32086(method_32003(i));
		}

		this.eye = this.body.method_32086("eye");
		this.field_3378 = new ModelPart[3];
		this.field_3378[0] = this.body.method_32086("tail0");
		this.field_3378[1] = this.field_3378[0].method_32086("tail1");
		this.field_3378[2] = this.field_3378[1].method_32086("tail2");
	}

	private static String method_32003(int i) {
		return "spike" + i;
	}

	public static class_5607 method_32002() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		class_5610 lv3 = lv2.method_32117(
			"head",
			class_5606.method_32108()
				.method_32101(0, 0)
				.method_32097(-6.0F, 10.0F, -8.0F, 12.0F, 12.0F, 16.0F)
				.method_32101(0, 28)
				.method_32097(-8.0F, 10.0F, -6.0F, 2.0F, 12.0F, 12.0F)
				.method_32101(0, 28)
				.method_32100(6.0F, 10.0F, -6.0F, 2.0F, 12.0F, 12.0F, true)
				.method_32101(16, 40)
				.method_32097(-6.0F, 8.0F, -6.0F, 12.0F, 2.0F, 12.0F)
				.method_32101(16, 40)
				.method_32097(-6.0F, 22.0F, -6.0F, 12.0F, 2.0F, 12.0F),
			class_5603.field_27701
		);
		class_5606 lv4 = class_5606.method_32108().method_32101(0, 0).method_32097(-1.0F, -4.5F, -1.0F, 2.0F, 9.0F, 2.0F);

		for (int i = 0; i < 12; i++) {
			float f = method_32005(i, 0.0F, 0.0F);
			float g = method_32006(i, 0.0F, 0.0F);
			float h = method_32007(i, 0.0F, 0.0F);
			float j = (float) Math.PI * field_17131[i];
			float k = (float) Math.PI * field_17132[i];
			float l = (float) Math.PI * field_17133[i];
			lv3.method_32117(method_32003(i), lv4, class_5603.method_32091(f, g, h, j, k, l));
		}

		lv3.method_32117(
			"eye", class_5606.method_32108().method_32101(8, 0).method_32097(-1.0F, 15.0F, 0.0F, 2.0F, 2.0F, 1.0F), class_5603.method_32090(0.0F, 0.0F, -8.25F)
		);
		class_5610 lv5 = lv3.method_32117(
			"tail0", class_5606.method_32108().method_32101(40, 0).method_32097(-2.0F, 14.0F, 7.0F, 4.0F, 4.0F, 8.0F), class_5603.field_27701
		);
		class_5610 lv6 = lv5.method_32117(
			"tail1", class_5606.method_32108().method_32101(0, 54).method_32097(0.0F, 14.0F, 0.0F, 3.0F, 3.0F, 7.0F), class_5603.method_32090(-1.5F, 0.5F, 14.0F)
		);
		lv6.method_32117(
			"tail2",
			class_5606.method_32108()
				.method_32101(41, 32)
				.method_32097(0.0F, 14.0F, 0.0F, 2.0F, 2.0F, 6.0F)
				.method_32101(25, 19)
				.method_32097(1.0F, 10.5F, 3.0F, 1.0F, 9.0F, 9.0F),
			class_5603.method_32090(0.5F, 0.5F, 6.0F)
		);
		return class_5607.method_32110(lv, 64, 64);
	}

	@Override
	public ModelPart method_32008() {
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
