package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.IronGolemEntity;

@Environment(EnvType.CLIENT)
public class IronGolemEntityModel<T extends IronGolemEntity> extends EntityModel<T> {
	private final ModelPart field_3415;
	private final ModelPart field_3413;
	public final ModelPart field_3414;
	private final ModelPart field_3412;
	private final ModelPart field_3411;
	private final ModelPart field_3416;

	public IronGolemEntityModel() {
		this(0.0F);
	}

	public IronGolemEntityModel(float f) {
		this(f, -7.0F);
	}

	public IronGolemEntityModel(float f, float g) {
		int i = 128;
		int j = 128;
		this.field_3415 = new ModelPart(this).setTextureSize(128, 128);
		this.field_3415.setPivot(0.0F, 0.0F + g, -2.0F);
		this.field_3415.setTextureOffset(0, 0).addCuboid(-4.0F, -12.0F, -5.5F, 8, 10, 8, f);
		this.field_3415.setTextureOffset(24, 0).addCuboid(-1.0F, -5.0F, -7.5F, 2, 4, 2, f);
		this.field_3413 = new ModelPart(this).setTextureSize(128, 128);
		this.field_3413.setPivot(0.0F, 0.0F + g, 0.0F);
		this.field_3413.setTextureOffset(0, 40).addCuboid(-9.0F, -2.0F, -6.0F, 18, 12, 11, f);
		this.field_3413.setTextureOffset(0, 70).addCuboid(-4.5F, 10.0F, -3.0F, 9, 5, 6, f + 0.5F);
		this.field_3414 = new ModelPart(this).setTextureSize(128, 128);
		this.field_3414.setPivot(0.0F, -7.0F, 0.0F);
		this.field_3414.setTextureOffset(60, 21).addCuboid(-13.0F, -2.5F, -3.0F, 4, 30, 6, f);
		this.field_3412 = new ModelPart(this).setTextureSize(128, 128);
		this.field_3412.setPivot(0.0F, -7.0F, 0.0F);
		this.field_3412.setTextureOffset(60, 58).addCuboid(9.0F, -2.5F, -3.0F, 4, 30, 6, f);
		this.field_3411 = new ModelPart(this, 0, 22).setTextureSize(128, 128);
		this.field_3411.setPivot(-4.0F, 18.0F + g, 0.0F);
		this.field_3411.setTextureOffset(37, 0).addCuboid(-3.5F, -3.0F, -3.0F, 6, 16, 5, f);
		this.field_3416 = new ModelPart(this, 0, 22).setTextureSize(128, 128);
		this.field_3416.mirror = true;
		this.field_3416.setTextureOffset(60, 0).setPivot(5.0F, 18.0F + g, 0.0F);
		this.field_3416.addCuboid(-3.5F, -3.0F, -3.0F, 6, 16, 5, f);
	}

	public void render(T ironGolemEntity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(ironGolemEntity, f, g, h, i, j, k);
		this.field_3415.render(k);
		this.field_3413.render(k);
		this.field_3411.render(k);
		this.field_3416.render(k);
		this.field_3414.render(k);
		this.field_3412.render(k);
	}

	public void setAngles(T ironGolemEntity, float f, float g, float h, float i, float j, float k) {
		this.field_3415.yaw = i * (float) (Math.PI / 180.0);
		this.field_3415.pitch = j * (float) (Math.PI / 180.0);
		this.field_3411.pitch = -1.5F * this.method_2810(f, 13.0F) * g;
		this.field_3416.pitch = 1.5F * this.method_2810(f, 13.0F) * g;
		this.field_3411.yaw = 0.0F;
		this.field_3416.yaw = 0.0F;
	}

	public void animateModel(T ironGolemEntity, float f, float g, float h) {
		int i = ironGolemEntity.method_6501();
		if (i > 0) {
			this.field_3414.pitch = -2.0F + 1.5F * this.method_2810((float)i - h, 10.0F);
			this.field_3412.pitch = -2.0F + 1.5F * this.method_2810((float)i - h, 10.0F);
		} else {
			int j = ironGolemEntity.method_6502();
			if (j > 0) {
				this.field_3414.pitch = -0.8F + 0.025F * this.method_2810((float)j, 70.0F);
				this.field_3412.pitch = 0.0F;
			} else {
				this.field_3414.pitch = (-0.2F + 1.5F * this.method_2810(f, 13.0F)) * g;
				this.field_3412.pitch = (-0.2F - 1.5F * this.method_2810(f, 13.0F)) * g;
			}
		}
	}

	private float method_2810(float f, float g) {
		return (Math.abs(f % g - g * 0.5F) - g * 0.25F) / (g * 0.25F);
	}

	public ModelPart method_2809() {
		return this.field_3414;
	}
}
