package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DragonEntityModel extends EntityModel<EnderDragonEntity> {
	private final Cuboid field_3630;
	private final Cuboid field_3637;
	private final Cuboid field_3631;
	private final Cuboid field_3627;
	private final Cuboid field_3633;
	private final Cuboid field_3632;
	private final Cuboid field_3626;
	private final Cuboid field_3634;
	private final Cuboid field_3628;
	private final Cuboid field_3625;
	private final Cuboid field_3629;
	private final Cuboid field_3635;
	private float delta;

	public DragonEntityModel(float f) {
		this.textureWidth = 256;
		this.textureHeight = 256;
		float g = -16.0F;
		this.field_3630 = new Cuboid(this, "head");
		this.field_3630.addBox("upperlip", -6.0F, -1.0F, -24.0F, 12, 5, 16, f, 176, 44);
		this.field_3630.addBox("upperhead", -8.0F, -8.0F, -10.0F, 16, 16, 16, f, 112, 30);
		this.field_3630.mirror = true;
		this.field_3630.addBox("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, f, 0, 0);
		this.field_3630.addBox("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, f, 112, 0);
		this.field_3630.mirror = false;
		this.field_3630.addBox("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, f, 0, 0);
		this.field_3630.addBox("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, f, 112, 0);
		this.field_3631 = new Cuboid(this, "jaw");
		this.field_3631.setRotationPoint(0.0F, 4.0F, -8.0F);
		this.field_3631.addBox("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16, f, 176, 65);
		this.field_3630.addChild(this.field_3631);
		this.field_3637 = new Cuboid(this, "neck");
		this.field_3637.addBox("box", -5.0F, -5.0F, -5.0F, 10, 10, 10, f, 192, 104);
		this.field_3637.addBox("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6, f, 48, 0);
		this.field_3627 = new Cuboid(this, "body");
		this.field_3627.setRotationPoint(0.0F, 4.0F, 8.0F);
		this.field_3627.addBox("body", -12.0F, 0.0F, -16.0F, 24, 24, 64, f, 0, 0);
		this.field_3627.addBox("scale", -1.0F, -6.0F, -10.0F, 2, 6, 12, f, 220, 53);
		this.field_3627.addBox("scale", -1.0F, -6.0F, 10.0F, 2, 6, 12, f, 220, 53);
		this.field_3627.addBox("scale", -1.0F, -6.0F, 30.0F, 2, 6, 12, f, 220, 53);
		this.field_3629 = new Cuboid(this, "wing");
		this.field_3629.setRotationPoint(-12.0F, 5.0F, 2.0F);
		this.field_3629.addBox("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8, f, 112, 88);
		this.field_3629.addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, f, -56, 88);
		this.field_3635 = new Cuboid(this, "wingtip");
		this.field_3635.setRotationPoint(-56.0F, 0.0F, 0.0F);
		this.field_3635.addBox("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4, f, 112, 136);
		this.field_3635.addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, f, -56, 144);
		this.field_3629.addChild(this.field_3635);
		this.field_3632 = new Cuboid(this, "frontleg");
		this.field_3632.setRotationPoint(-12.0F, 20.0F, 2.0F);
		this.field_3632.addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, f, 112, 104);
		this.field_3634 = new Cuboid(this, "frontlegtip");
		this.field_3634.setRotationPoint(0.0F, 20.0F, -1.0F);
		this.field_3634.addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, f, 226, 138);
		this.field_3632.addChild(this.field_3634);
		this.field_3625 = new Cuboid(this, "frontfoot");
		this.field_3625.setRotationPoint(0.0F, 23.0F, 0.0F);
		this.field_3625.addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, f, 144, 104);
		this.field_3634.addChild(this.field_3625);
		this.field_3633 = new Cuboid(this, "rearleg");
		this.field_3633.setRotationPoint(-16.0F, 16.0F, 42.0F);
		this.field_3633.addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, f, 0, 0);
		this.field_3626 = new Cuboid(this, "rearlegtip");
		this.field_3626.setRotationPoint(0.0F, 32.0F, -4.0F);
		this.field_3626.addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, f, 196, 0);
		this.field_3633.addChild(this.field_3626);
		this.field_3628 = new Cuboid(this, "rearfoot");
		this.field_3628.setRotationPoint(0.0F, 31.0F, 4.0F);
		this.field_3628.addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, f, 112, 0);
		this.field_3626.addChild(this.field_3628);
	}

	public void method_17136(EnderDragonEntity enderDragonEntity, float f, float g, float h) {
		this.delta = h;
	}

	public void method_17137(EnderDragonEntity enderDragonEntity, float f, float g, float h, float i, float j, float k) {
		GlStateManager.pushMatrix();
		float l = MathHelper.lerp(this.delta, enderDragonEntity.field_7019, enderDragonEntity.field_7030);
		this.field_3631.pitch = (float)(Math.sin((double)(l * (float) (Math.PI * 2))) + 1.0) * 0.2F;
		float m = (float)(Math.sin((double)(l * (float) (Math.PI * 2) - 1.0F)) + 1.0);
		m = (m * m + m * 2.0F) * 0.05F;
		GlStateManager.translatef(0.0F, m - 2.0F, -3.0F);
		GlStateManager.rotatef(m * 2.0F, 1.0F, 0.0F, 0.0F);
		float n = 0.0F;
		float o = 20.0F;
		float p = -12.0F;
		float q = 1.5F;
		double[] ds = enderDragonEntity.method_6817(6, this.delta);
		float r = this.updateRotations(enderDragonEntity.method_6817(5, this.delta)[0] - enderDragonEntity.method_6817(10, this.delta)[0]);
		float s = this.updateRotations(enderDragonEntity.method_6817(5, this.delta)[0] + (double)(r / 2.0F));
		float t = l * (float) (Math.PI * 2);

		for (int u = 0; u < 5; u++) {
			double[] es = enderDragonEntity.method_6817(5 - u, this.delta);
			float v = (float)Math.cos((double)((float)u * 0.45F + t)) * 0.15F;
			this.field_3637.yaw = this.updateRotations(es[0] - ds[0]) * (float) (Math.PI / 180.0) * 1.5F;
			this.field_3637.pitch = v + enderDragonEntity.method_6823(u, ds, es) * (float) (Math.PI / 180.0) * 1.5F * 5.0F;
			this.field_3637.roll = -this.updateRotations(es[0] - (double)s) * (float) (Math.PI / 180.0) * 1.5F;
			this.field_3637.rotationPointY = o;
			this.field_3637.rotationPointZ = p;
			this.field_3637.rotationPointX = n;
			o = (float)((double)o + Math.sin((double)this.field_3637.pitch) * 10.0);
			p = (float)((double)p - Math.cos((double)this.field_3637.yaw) * Math.cos((double)this.field_3637.pitch) * 10.0);
			n = (float)((double)n - Math.sin((double)this.field_3637.yaw) * Math.cos((double)this.field_3637.pitch) * 10.0);
			this.field_3637.render(k);
		}

		this.field_3630.rotationPointY = o;
		this.field_3630.rotationPointZ = p;
		this.field_3630.rotationPointX = n;
		double[] fs = enderDragonEntity.method_6817(0, this.delta);
		this.field_3630.yaw = this.updateRotations(fs[0] - ds[0]) * (float) (Math.PI / 180.0);
		this.field_3630.pitch = this.updateRotations((double)enderDragonEntity.method_6823(6, ds, fs)) * (float) (Math.PI / 180.0) * 1.5F * 5.0F;
		this.field_3630.roll = -this.updateRotations(fs[0] - (double)s) * (float) (Math.PI / 180.0);
		this.field_3630.render(k);
		GlStateManager.pushMatrix();
		GlStateManager.translatef(0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(-r * 1.5F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translatef(0.0F, -1.0F, 0.0F);
		this.field_3627.roll = 0.0F;
		this.field_3627.render(k);

		for (int w = 0; w < 2; w++) {
			GlStateManager.enableCull();
			float v = l * (float) (Math.PI * 2);
			this.field_3629.pitch = 0.125F - (float)Math.cos((double)v) * 0.2F;
			this.field_3629.yaw = 0.25F;
			this.field_3629.roll = (float)(Math.sin((double)v) + 0.125) * 0.8F;
			this.field_3635.roll = -((float)(Math.sin((double)(v + 2.0F)) + 0.5)) * 0.75F;
			this.field_3633.pitch = 1.0F + m * 0.1F;
			this.field_3626.pitch = 0.5F + m * 0.1F;
			this.field_3628.pitch = 0.75F + m * 0.1F;
			this.field_3632.pitch = 1.3F + m * 0.1F;
			this.field_3634.pitch = -0.5F - m * 0.1F;
			this.field_3625.pitch = 0.75F + m * 0.1F;
			this.field_3629.render(k);
			this.field_3632.render(k);
			this.field_3633.render(k);
			GlStateManager.scalef(-1.0F, 1.0F, 1.0F);
			if (w == 0) {
				GlStateManager.cullFace(GlStateManager.FaceSides.field_5068);
			}
		}

		GlStateManager.popMatrix();
		GlStateManager.cullFace(GlStateManager.FaceSides.field_5070);
		GlStateManager.disableCull();
		float x = -((float)Math.sin((double)(l * (float) (Math.PI * 2)))) * 0.0F;
		t = l * (float) (Math.PI * 2);
		o = 10.0F;
		p = 60.0F;
		n = 0.0F;
		ds = enderDragonEntity.method_6817(11, this.delta);

		for (int y = 0; y < 12; y++) {
			fs = enderDragonEntity.method_6817(12 + y, this.delta);
			x = (float)((double)x + Math.sin((double)((float)y * 0.45F + t)) * 0.05F);
			this.field_3637.yaw = (this.updateRotations(fs[0] - ds[0]) * 1.5F + 180.0F) * (float) (Math.PI / 180.0);
			this.field_3637.pitch = x + (float)(fs[1] - ds[1]) * (float) (Math.PI / 180.0) * 1.5F * 5.0F;
			this.field_3637.roll = this.updateRotations(fs[0] - (double)s) * (float) (Math.PI / 180.0) * 1.5F;
			this.field_3637.rotationPointY = o;
			this.field_3637.rotationPointZ = p;
			this.field_3637.rotationPointX = n;
			o = (float)((double)o + Math.sin((double)this.field_3637.pitch) * 10.0);
			p = (float)((double)p - Math.cos((double)this.field_3637.yaw) * Math.cos((double)this.field_3637.pitch) * 10.0);
			n = (float)((double)n - Math.sin((double)this.field_3637.yaw) * Math.cos((double)this.field_3637.pitch) * 10.0);
			this.field_3637.render(k);
		}

		GlStateManager.popMatrix();
	}

	private float updateRotations(double d) {
		while (d >= 180.0) {
			d -= 360.0;
		}

		while (d < -180.0) {
			d += 360.0;
		}

		return (float)d;
	}
}
