package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class HorseEntityModel<T extends HorseBaseEntity> extends EntityModel<T> {
	protected final ModelPart field_3305;
	protected final ModelPart field_3307;
	private final ModelPart field_3306;
	private final ModelPart field_3303;
	private final ModelPart field_3302;
	private final ModelPart field_3308;
	private final ModelPart field_3300;
	private final ModelPart[] field_3304;
	private final ModelPart[] field_3301;

	public HorseEntityModel(float f) {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.field_3305 = new ModelPart(this, 0, 32);
		this.field_3305.addCuboid(-5.0F, -8.0F, -17.0F, 10, 10, 22, 0.05F);
		this.field_3305.setPivot(0.0F, 11.0F, 5.0F);
		this.field_3307 = new ModelPart(this, 0, 35);
		this.field_3307.addCuboid(-2.05F, -6.0F, -2.0F, 4, 12, 7);
		this.field_3307.pitch = (float) (Math.PI / 6);
		ModelPart modelPart = new ModelPart(this, 0, 13);
		modelPart.addCuboid(-3.0F, -11.0F, -2.0F, 6, 5, 7, f);
		ModelPart modelPart2 = new ModelPart(this, 56, 36);
		modelPart2.addCuboid(-1.0F, -11.0F, 5.01F, 2, 16, 2, f);
		ModelPart modelPart3 = new ModelPart(this, 0, 25);
		modelPart3.addCuboid(-2.0F, -11.0F, -7.0F, 4, 5, 5, f);
		this.field_3307.addChild(modelPart);
		this.field_3307.addChild(modelPart2);
		this.field_3307.addChild(modelPart3);
		this.method_2789(this.field_3307);
		this.field_3306 = new ModelPart(this, 48, 21);
		this.field_3306.mirror = true;
		this.field_3306.addCuboid(-3.0F, -1.01F, -1.0F, 4, 11, 4, f);
		this.field_3306.setPivot(4.0F, 14.0F, 7.0F);
		this.field_3303 = new ModelPart(this, 48, 21);
		this.field_3303.addCuboid(-1.0F, -1.01F, -1.0F, 4, 11, 4, f);
		this.field_3303.setPivot(-4.0F, 14.0F, 7.0F);
		this.field_3302 = new ModelPart(this, 48, 21);
		this.field_3302.mirror = true;
		this.field_3302.addCuboid(-3.0F, -1.01F, -1.9F, 4, 11, 4, f);
		this.field_3302.setPivot(4.0F, 6.0F, -12.0F);
		this.field_3308 = new ModelPart(this, 48, 21);
		this.field_3308.addCuboid(-1.0F, -1.01F, -1.9F, 4, 11, 4, f);
		this.field_3308.setPivot(-4.0F, 6.0F, -12.0F);
		this.field_3300 = new ModelPart(this, 42, 36);
		this.field_3300.addCuboid(-1.5F, 0.0F, 0.0F, 3, 14, 4, f);
		this.field_3300.setPivot(0.0F, -5.0F, 2.0F);
		this.field_3300.pitch = (float) (Math.PI / 6);
		this.field_3305.addChild(this.field_3300);
		ModelPart modelPart4 = new ModelPart(this, 26, 0);
		modelPart4.addCuboid(-5.0F, -8.0F, -9.0F, 10, 9, 9, 0.5F);
		this.field_3305.addChild(modelPart4);
		ModelPart modelPart5 = new ModelPart(this, 29, 5);
		modelPart5.addCuboid(2.0F, -9.0F, -6.0F, 1, 2, 2, f);
		this.field_3307.addChild(modelPart5);
		ModelPart modelPart6 = new ModelPart(this, 29, 5);
		modelPart6.addCuboid(-3.0F, -9.0F, -6.0F, 1, 2, 2, f);
		this.field_3307.addChild(modelPart6);
		ModelPart modelPart7 = new ModelPart(this, 32, 2);
		modelPart7.addCuboid(3.1F, -6.0F, -8.0F, 0, 3, 16, f);
		modelPart7.pitch = (float) (-Math.PI / 6);
		this.field_3307.addChild(modelPart7);
		ModelPart modelPart8 = new ModelPart(this, 32, 2);
		modelPart8.addCuboid(-3.1F, -6.0F, -8.0F, 0, 3, 16, f);
		modelPart8.pitch = (float) (-Math.PI / 6);
		this.field_3307.addChild(modelPart8);
		ModelPart modelPart9 = new ModelPart(this, 1, 1);
		modelPart9.addCuboid(-3.0F, -11.0F, -1.9F, 6, 5, 6, 0.2F);
		this.field_3307.addChild(modelPart9);
		ModelPart modelPart10 = new ModelPart(this, 19, 0);
		modelPart10.addCuboid(-2.0F, -11.0F, -4.0F, 4, 5, 2, 0.2F);
		this.field_3307.addChild(modelPart10);
		this.field_3304 = new ModelPart[]{modelPart4, modelPart5, modelPart6, modelPart9, modelPart10};
		this.field_3301 = new ModelPart[]{modelPart7, modelPart8};
	}

	protected void method_2789(ModelPart modelPart) {
		ModelPart modelPart2 = new ModelPart(this, 19, 16);
		modelPart2.addCuboid(0.55F, -13.0F, 4.0F, 2, 3, 1, -0.001F);
		ModelPart modelPart3 = new ModelPart(this, 19, 16);
		modelPart3.addCuboid(-2.55F, -13.0F, 4.0F, 2, 3, 1, -0.001F);
		modelPart.addChild(modelPart2);
		modelPart.addChild(modelPart3);
	}

	public void render(T horseBaseEntity, float f, float g, float h, float i, float j, float k) {
		boolean bl = horseBaseEntity.isBaby();
		float l = horseBaseEntity.getScaleFactor();
		boolean bl2 = horseBaseEntity.isSaddled();
		boolean bl3 = horseBaseEntity.hasPassengers();

		for (ModelPart modelPart : this.field_3304) {
			modelPart.visible = bl2;
		}

		for (ModelPart modelPart : this.field_3301) {
			modelPart.visible = bl3 && bl2;
		}

		if (bl) {
			GlStateManager.pushMatrix();
			GlStateManager.scalef(l, 0.5F + l * 0.5F, l);
			GlStateManager.translatef(0.0F, 0.95F * (1.0F - l), 0.0F);
		}

		this.field_3306.render(k);
		this.field_3303.render(k);
		this.field_3302.render(k);
		this.field_3308.render(k);
		if (bl) {
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(l, l, l);
			GlStateManager.translatef(0.0F, 2.3F * (1.0F - l), 0.0F);
		}

		this.field_3305.render(k);
		if (bl) {
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			float m = l + 0.1F * l;
			GlStateManager.scalef(m, m, m);
			GlStateManager.translatef(0.0F, 2.25F * (1.0F - m), 0.1F * (1.4F - m));
		}

		this.field_3307.render(k);
		if (bl) {
			GlStateManager.popMatrix();
		}
	}

	public void animateModel(T horseBaseEntity, float f, float g, float h) {
		super.animateModel(horseBaseEntity, f, g, h);
		float i = this.method_2790(horseBaseEntity.field_6220, horseBaseEntity.field_6283, h);
		float j = this.method_2790(horseBaseEntity.prevHeadYaw, horseBaseEntity.headYaw, h);
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
		float s = horseBaseEntity.isTouchingWater() ? 0.2F : 1.0F;
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
	}

	private float method_2790(float f, float g, float h) {
		float i = g - f;

		while (i < -180.0F) {
			i += 360.0F;
		}

		while (i >= 180.0F) {
			i -= 360.0F;
		}

		return f + h * i;
	}
}
