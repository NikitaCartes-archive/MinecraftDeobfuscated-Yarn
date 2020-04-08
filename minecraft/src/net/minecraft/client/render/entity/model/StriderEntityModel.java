package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class StriderEntityModel<T extends StriderEntity> extends CompositeEntityModel<T> {
	private final ModelPart field_23353;
	private final ModelPart field_23354;
	private final ModelPart field_23355;
	private final ModelPart field_23356;
	private final ModelPart field_23357;
	private final ModelPart field_23358;
	private final ModelPart field_23359;
	private final ModelPart field_23360;
	private final ModelPart field_23361;

	public StriderEntityModel() {
		this.textureWidth = 64;
		this.textureHeight = 128;
		this.field_23353 = new ModelPart(this, 0, 32);
		this.field_23353.setPivot(-4.0F, 8.0F, 0.0F);
		this.field_23353.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 16.0F, 4.0F, 0.0F);
		this.field_23354 = new ModelPart(this, 0, 32);
		this.field_23354.setPivot(4.0F, 8.0F, 0.0F);
		this.field_23354.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 16.0F, 4.0F, 0.0F);
		this.field_23355 = new ModelPart(this, 0, 0);
		this.field_23355.setPivot(0.0F, 1.0F, 0.0F);
		this.field_23355.addCuboid(-8.0F, -6.0F, -8.0F, 16.0F, 14.0F, 16.0F, 0.0F);
		this.field_23356 = new ModelPart(this, 16, 65);
		this.field_23356.setPivot(-8.0F, 4.0F, -8.0F);
		this.field_23356.addCuboid(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, 0.0F, true);
		this.method_26415(this.field_23356, 0.0F, 0.0F, -1.2217305F);
		this.field_23357 = new ModelPart(this, 16, 49);
		this.field_23357.setPivot(-8.0F, -1.0F, -8.0F);
		this.field_23357.addCuboid(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, 0.0F, true);
		this.method_26415(this.field_23357, 0.0F, 0.0F, -1.134464F);
		this.field_23358 = new ModelPart(this, 16, 33);
		this.field_23358.setPivot(-8.0F, -5.0F, -8.0F);
		this.field_23358.addCuboid(-12.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, 0.0F, true);
		this.method_26415(this.field_23358, 0.0F, 0.0F, -0.87266463F);
		this.field_23359 = new ModelPart(this, 16, 33);
		this.field_23359.setPivot(8.0F, -6.0F, -8.0F);
		this.field_23359.addCuboid(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, 0.0F);
		this.method_26415(this.field_23359, 0.0F, 0.0F, 0.87266463F);
		this.field_23360 = new ModelPart(this, 16, 49);
		this.field_23360.setPivot(8.0F, -2.0F, -8.0F);
		this.field_23360.addCuboid(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, 0.0F);
		this.method_26415(this.field_23360, 0.0F, 0.0F, 1.134464F);
		this.field_23361 = new ModelPart(this, 16, 65);
		this.field_23361.setPivot(8.0F, 3.0F, -8.0F);
		this.field_23361.addCuboid(0.0F, 0.0F, 0.0F, 12.0F, 0.0F, 16.0F, 0.0F);
		this.method_26415(this.field_23361, 0.0F, 0.0F, 1.2217305F);
		this.field_23355.addChild(this.field_23356);
		this.field_23355.addChild(this.field_23357);
		this.field_23355.addChild(this.field_23358);
		this.field_23355.addChild(this.field_23359);
		this.field_23355.addChild(this.field_23360);
		this.field_23355.addChild(this.field_23361);
	}

	public void setAngles(StriderEntity striderEntity, float f, float g, float h, float i, float j) {
		g = Math.min(0.25F, g);
		if (striderEntity.getPassengerList().size() <= 0) {
			this.field_23355.pitch = j * (float) (Math.PI / 180.0);
			this.field_23355.yaw = i * (float) (Math.PI / 180.0);
		} else {
			this.field_23355.pitch = 0.0F;
			this.field_23355.yaw = 0.0F;
		}

		float k = 1.5F;
		this.field_23355.roll = 0.1F * MathHelper.sin(f * 1.5F) * 4.0F * g;
		this.field_23355.pivotY = 2.0F;
		this.field_23355.pivotY = this.field_23355.pivotY - 2.0F * MathHelper.cos(f * 1.5F) * 2.0F * g;
		this.field_23354.pitch = MathHelper.sin(f * 1.5F * 0.5F) * 2.0F * g;
		this.field_23353.pitch = MathHelper.sin(f * 1.5F * 0.5F + (float) Math.PI) * 2.0F * g;
		this.field_23354.roll = (float) (Math.PI / 18) * MathHelper.cos(f * 1.5F * 0.5F) * g;
		this.field_23353.roll = (float) (Math.PI / 18) * MathHelper.cos(f * 1.5F * 0.5F + (float) Math.PI) * g;
		this.field_23354.pivotY = 8.0F + 2.0F * MathHelper.sin(f * 1.5F * 0.5F + (float) Math.PI) * 2.0F * g;
		this.field_23353.pivotY = 8.0F + 2.0F * MathHelper.sin(f * 1.5F * 0.5F) * 2.0F * g;
		this.field_23356.roll = -1.2217305F;
		this.field_23357.roll = -1.134464F;
		this.field_23358.roll = -0.87266463F;
		this.field_23359.roll = 0.87266463F;
		this.field_23360.roll = 1.134464F;
		this.field_23361.roll = 1.2217305F;
		float l = MathHelper.cos(f * 1.5F + (float) Math.PI) * g;
		this.field_23356.roll += l * 1.3F;
		this.field_23357.roll += l * 1.2F;
		this.field_23358.roll += l * 0.6F;
		this.field_23359.roll += l * 0.6F;
		this.field_23360.roll += l * 1.2F;
		this.field_23361.roll += l * 1.3F;
		float m = 1.0F;
		float n = 1.0F;
		this.field_23356.roll = this.field_23356.roll + 0.05F * MathHelper.sin(h * 1.0F * -0.4F);
		this.field_23357.roll = this.field_23357.roll + 0.1F * MathHelper.sin(h * 1.0F * 0.2F);
		this.field_23358.roll = this.field_23358.roll + 0.1F * MathHelper.sin(h * 1.0F * 0.4F);
		this.field_23359.roll = this.field_23359.roll + 0.1F * MathHelper.sin(h * 1.0F * 0.4F);
		this.field_23360.roll = this.field_23360.roll + 0.1F * MathHelper.sin(h * 1.0F * 0.2F);
		this.field_23361.roll = this.field_23361.roll + 0.05F * MathHelper.sin(h * 1.0F * -0.4F);
	}

	public void method_26415(ModelPart modelPart, float f, float g, float h) {
		modelPart.pitch = f;
		modelPart.yaw = g;
		modelPart.roll = h;
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.field_23355, this.field_23354, this.field_23353);
	}
}
