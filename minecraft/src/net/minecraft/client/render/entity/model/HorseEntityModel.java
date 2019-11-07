package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class HorseEntityModel<T extends HorseBaseEntity> extends AnimalModel<T> {
	protected final ModelPart field_3305;
	protected final ModelPart field_3307;
	private final ModelPart field_3306;
	private final ModelPart field_3303;
	private final ModelPart field_3302;
	private final ModelPart field_3308;
	private final ModelPart field_20930;
	private final ModelPart field_20931;
	private final ModelPart field_20932;
	private final ModelPart field_20933;
	private final ModelPart field_3300;
	private final ModelPart[] field_3304;
	private final ModelPart[] field_3301;

	public HorseEntityModel(float f) {
		super(true, 16.2F, 1.36F, 2.7272F, 2.0F, 20.0F);
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.field_3305 = new ModelPart(this, 0, 32);
		this.field_3305.addCuboid(-5.0F, -8.0F, -17.0F, 10.0F, 10.0F, 22.0F, 0.05F);
		this.field_3305.setPivot(0.0F, 11.0F, 5.0F);
		this.field_3307 = new ModelPart(this, 0, 35);
		this.field_3307.addCuboid(-2.05F, -6.0F, -2.0F, 4.0F, 12.0F, 7.0F);
		this.field_3307.pitch = (float) (Math.PI / 6);
		ModelPart modelPart = new ModelPart(this, 0, 13);
		modelPart.addCuboid(-3.0F, -11.0F, -2.0F, 6.0F, 5.0F, 7.0F, f);
		ModelPart modelPart2 = new ModelPart(this, 56, 36);
		modelPart2.addCuboid(-1.0F, -11.0F, 5.01F, 2.0F, 16.0F, 2.0F, f);
		ModelPart modelPart3 = new ModelPart(this, 0, 25);
		modelPart3.addCuboid(-2.0F, -11.0F, -7.0F, 4.0F, 5.0F, 5.0F, f);
		this.field_3307.addChild(modelPart);
		this.field_3307.addChild(modelPart2);
		this.field_3307.addChild(modelPart3);
		this.method_2789(this.field_3307);
		this.field_3306 = new ModelPart(this, 48, 21);
		this.field_3306.mirror = true;
		this.field_3306.addCuboid(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, f);
		this.field_3306.setPivot(4.0F, 14.0F, 7.0F);
		this.field_3303 = new ModelPart(this, 48, 21);
		this.field_3303.addCuboid(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, f);
		this.field_3303.setPivot(-4.0F, 14.0F, 7.0F);
		this.field_3302 = new ModelPart(this, 48, 21);
		this.field_3302.mirror = true;
		this.field_3302.addCuboid(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, f);
		this.field_3302.setPivot(4.0F, 6.0F, -12.0F);
		this.field_3308 = new ModelPart(this, 48, 21);
		this.field_3308.addCuboid(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, f);
		this.field_3308.setPivot(-4.0F, 6.0F, -12.0F);
		float g = 5.5F;
		this.field_20930 = new ModelPart(this, 48, 21);
		this.field_20930.mirror = true;
		this.field_20930.addCuboid(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, f, f + 5.5F, f);
		this.field_20930.setPivot(4.0F, 14.0F, 7.0F);
		this.field_20931 = new ModelPart(this, 48, 21);
		this.field_20931.addCuboid(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, f, f + 5.5F, f);
		this.field_20931.setPivot(-4.0F, 14.0F, 7.0F);
		this.field_20932 = new ModelPart(this, 48, 21);
		this.field_20932.mirror = true;
		this.field_20932.addCuboid(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, f, f + 5.5F, f);
		this.field_20932.setPivot(4.0F, 6.0F, -12.0F);
		this.field_20933 = new ModelPart(this, 48, 21);
		this.field_20933.addCuboid(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, f, f + 5.5F, f);
		this.field_20933.setPivot(-4.0F, 6.0F, -12.0F);
		this.field_3300 = new ModelPart(this, 42, 36);
		this.field_3300.addCuboid(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 4.0F, f);
		this.field_3300.setPivot(0.0F, -5.0F, 2.0F);
		this.field_3300.pitch = (float) (Math.PI / 6);
		this.field_3305.addChild(this.field_3300);
		ModelPart modelPart4 = new ModelPart(this, 26, 0);
		modelPart4.addCuboid(-5.0F, -8.0F, -9.0F, 10.0F, 9.0F, 9.0F, 0.5F);
		this.field_3305.addChild(modelPart4);
		ModelPart modelPart5 = new ModelPart(this, 29, 5);
		modelPart5.addCuboid(2.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F, f);
		this.field_3307.addChild(modelPart5);
		ModelPart modelPart6 = new ModelPart(this, 29, 5);
		modelPart6.addCuboid(-3.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F, f);
		this.field_3307.addChild(modelPart6);
		ModelPart modelPart7 = new ModelPart(this, 32, 2);
		modelPart7.addCuboid(3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F, f);
		modelPart7.pitch = (float) (-Math.PI / 6);
		this.field_3307.addChild(modelPart7);
		ModelPart modelPart8 = new ModelPart(this, 32, 2);
		modelPart8.addCuboid(-3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F, f);
		modelPart8.pitch = (float) (-Math.PI / 6);
		this.field_3307.addChild(modelPart8);
		ModelPart modelPart9 = new ModelPart(this, 1, 1);
		modelPart9.addCuboid(-3.0F, -11.0F, -1.9F, 6.0F, 5.0F, 6.0F, 0.2F);
		this.field_3307.addChild(modelPart9);
		ModelPart modelPart10 = new ModelPart(this, 19, 0);
		modelPart10.addCuboid(-2.0F, -11.0F, -4.0F, 4.0F, 5.0F, 2.0F, 0.2F);
		this.field_3307.addChild(modelPart10);
		this.field_3304 = new ModelPart[]{modelPart4, modelPart5, modelPart6, modelPart9, modelPart10};
		this.field_3301 = new ModelPart[]{modelPart7, modelPart8};
	}

	protected void method_2789(ModelPart modelPart) {
		ModelPart modelPart2 = new ModelPart(this, 19, 16);
		modelPart2.addCuboid(0.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, -0.001F);
		ModelPart modelPart3 = new ModelPart(this, 19, 16);
		modelPart3.addCuboid(-2.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, -0.001F);
		modelPart.addChild(modelPart2);
		modelPart.addChild(modelPart3);
	}

	public void method_17085(T horseBaseEntity, float f, float g, float h, float i, float j) {
		boolean bl = horseBaseEntity.isSaddled();
		boolean bl2 = horseBaseEntity.hasPassengers();

		for (ModelPart modelPart : this.field_3304) {
			modelPart.visible = bl;
		}

		for (ModelPart modelPart : this.field_3301) {
			modelPart.visible = bl2 && bl;
		}

		this.field_3305.pivotY = 11.0F;
	}

	@Override
	public Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of(this.field_3307);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(
			this.field_3305, this.field_3306, this.field_3303, this.field_3302, this.field_3308, this.field_20930, this.field_20931, this.field_20932, this.field_20933
		);
	}

	public void method_17084(T horseBaseEntity, float f, float g, float h) {
		super.animateModel(horseBaseEntity, f, g, h);
		float i = MathHelper.method_22859(horseBaseEntity.prevBodyYaw, horseBaseEntity.bodyYaw, h);
		float j = MathHelper.method_22859(horseBaseEntity.prevHeadYaw, horseBaseEntity.headYaw, h);
		float k = MathHelper.lerp(h, horseBaseEntity.prevPitch, horseBaseEntity.pitch);
		float l = j - i;
		float m = k * (float) (Math.PI / 180.0);
		if (l > 20.0F) {
			l = 20.0F;
		}

		if (l < -20.0F) {
			l = -20.0F;
		}

		if (g > 0.2F) {
			m += MathHelper.cos(f * 0.4F) * 0.15F * g;
		}

		float n = horseBaseEntity.getEatingGrassAnimationProgress(h);
		float o = horseBaseEntity.getAngryAnimationProgress(h);
		float p = 1.0F - o;
		float q = horseBaseEntity.getEatingAnimationProgress(h);
		boolean bl = horseBaseEntity.field_6957 != 0;
		float r = (float)horseBaseEntity.age + h;
		this.field_3307.pivotY = 4.0F;
		this.field_3307.pivotZ = -12.0F;
		this.field_3305.pitch = 0.0F;
		this.field_3307.pitch = (float) (Math.PI / 6) + m;
		this.field_3307.yaw = l * (float) (Math.PI / 180.0);
		float s = horseBaseEntity.isInsideWater() ? 0.2F : 1.0F;
		float t = MathHelper.cos(s * f * 0.6662F + (float) Math.PI);
		float u = t * 0.8F * g;
		float v = (1.0F - Math.max(o, n)) * ((float) (Math.PI / 6) + m + q * MathHelper.sin(r) * 0.05F);
		this.field_3307.pitch = o * ((float) (Math.PI / 12) + m) + n * (2.1816616F + MathHelper.sin(r) * 0.05F) + v;
		this.field_3307.yaw = o * l * (float) (Math.PI / 180.0) + (1.0F - Math.max(o, n)) * this.field_3307.yaw;
		this.field_3307.pivotY = o * -4.0F + n * 11.0F + (1.0F - Math.max(o, n)) * this.field_3307.pivotY;
		this.field_3307.pivotZ = o * -4.0F + n * -12.0F + (1.0F - Math.max(o, n)) * this.field_3307.pivotZ;
		this.field_3305.pitch = o * (float) (-Math.PI / 4) + p * this.field_3305.pitch;
		float w = (float) (Math.PI / 12) * o;
		float x = MathHelper.cos(r * 0.6F + (float) Math.PI);
		this.field_3302.pivotY = 2.0F * o + 14.0F * p;
		this.field_3302.pivotZ = -6.0F * o - 10.0F * p;
		this.field_3308.pivotY = this.field_3302.pivotY;
		this.field_3308.pivotZ = this.field_3302.pivotZ;
		float y = ((float) (-Math.PI / 3) + x) * o + u * p;
		float z = ((float) (-Math.PI / 3) - x) * o - u * p;
		this.field_3306.pitch = w - t * 0.5F * g * p;
		this.field_3303.pitch = w + t * 0.5F * g * p;
		this.field_3302.pitch = y;
		this.field_3308.pitch = z;
		this.field_3300.pitch = (float) (Math.PI / 6) + g * 0.75F;
		this.field_3300.pivotY = -5.0F + g;
		this.field_3300.pivotZ = 2.0F + g * 2.0F;
		if (bl) {
			this.field_3300.yaw = MathHelper.cos(r * 0.7F);
		} else {
			this.field_3300.yaw = 0.0F;
		}

		this.field_20930.pivotY = this.field_3306.pivotY;
		this.field_20930.pivotZ = this.field_3306.pivotZ;
		this.field_20930.pitch = this.field_3306.pitch;
		this.field_20931.pivotY = this.field_3303.pivotY;
		this.field_20931.pivotZ = this.field_3303.pivotZ;
		this.field_20931.pitch = this.field_3303.pitch;
		this.field_20932.pivotY = this.field_3302.pivotY;
		this.field_20932.pivotZ = this.field_3302.pivotZ;
		this.field_20932.pitch = this.field_3302.pitch;
		this.field_20933.pivotY = this.field_3308.pivotY;
		this.field_20933.pivotZ = this.field_3308.pivotZ;
		this.field_20933.pitch = this.field_3308.pitch;
		boolean bl2 = horseBaseEntity.isBaby();
		this.field_3306.visible = !bl2;
		this.field_3303.visible = !bl2;
		this.field_3302.visible = !bl2;
		this.field_3308.visible = !bl2;
		this.field_20930.visible = bl2;
		this.field_20931.visible = bl2;
		this.field_20932.visible = bl2;
		this.field_20933.visible = bl2;
		this.field_3305.pivotY = bl2 ? 10.8F : 0.0F;
	}
}
