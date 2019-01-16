package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DragonEntityModel extends EntityModel<EnderDragonEntity> {
	private final Cuboid head;
	private final Cuboid neck;
	private final Cuboid jaw;
	private final Cuboid body;
	private final Cuboid rearLeg;
	private final Cuboid frontLeg;
	private final Cuboid rearLegTip;
	private final Cuboid frontLegTip;
	private final Cuboid rearFoot;
	private final Cuboid frontFoot;
	private final Cuboid wing;
	private final Cuboid wingTip;
	private float delta;

	public DragonEntityModel(float f) {
		this.textureWidth = 256;
		this.textureHeight = 256;
		float g = -16.0F;
		this.head = new Cuboid(this, "head");
		this.head.addBox("upperlip", -6.0F, -1.0F, -24.0F, 12, 5, 16, f, 176, 44);
		this.head.addBox("upperhead", -8.0F, -8.0F, -10.0F, 16, 16, 16, f, 112, 30);
		this.head.mirror = true;
		this.head.addBox("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, f, 0, 0);
		this.head.addBox("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, f, 112, 0);
		this.head.mirror = false;
		this.head.addBox("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, f, 0, 0);
		this.head.addBox("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, f, 112, 0);
		this.jaw = new Cuboid(this, "jaw");
		this.jaw.setRotationPoint(0.0F, 4.0F, -8.0F);
		this.jaw.addBox("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16, f, 176, 65);
		this.head.addChild(this.jaw);
		this.neck = new Cuboid(this, "neck");
		this.neck.addBox("box", -5.0F, -5.0F, -5.0F, 10, 10, 10, f, 192, 104);
		this.neck.addBox("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6, f, 48, 0);
		this.body = new Cuboid(this, "body");
		this.body.setRotationPoint(0.0F, 4.0F, 8.0F);
		this.body.addBox("body", -12.0F, 0.0F, -16.0F, 24, 24, 64, f, 0, 0);
		this.body.addBox("scale", -1.0F, -6.0F, -10.0F, 2, 6, 12, f, 220, 53);
		this.body.addBox("scale", -1.0F, -6.0F, 10.0F, 2, 6, 12, f, 220, 53);
		this.body.addBox("scale", -1.0F, -6.0F, 30.0F, 2, 6, 12, f, 220, 53);
		this.wing = new Cuboid(this, "wing");
		this.wing.setRotationPoint(-12.0F, 5.0F, 2.0F);
		this.wing.addBox("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8, f, 112, 88);
		this.wing.addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, f, -56, 88);
		this.wingTip = new Cuboid(this, "wingtip");
		this.wingTip.setRotationPoint(-56.0F, 0.0F, 0.0F);
		this.wingTip.addBox("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4, f, 112, 136);
		this.wingTip.addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, f, -56, 144);
		this.wing.addChild(this.wingTip);
		this.frontLeg = new Cuboid(this, "frontleg");
		this.frontLeg.setRotationPoint(-12.0F, 20.0F, 2.0F);
		this.frontLeg.addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, f, 112, 104);
		this.frontLegTip = new Cuboid(this, "frontlegtip");
		this.frontLegTip.setRotationPoint(0.0F, 20.0F, -1.0F);
		this.frontLegTip.addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, f, 226, 138);
		this.frontLeg.addChild(this.frontLegTip);
		this.frontFoot = new Cuboid(this, "frontfoot");
		this.frontFoot.setRotationPoint(0.0F, 23.0F, 0.0F);
		this.frontFoot.addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, f, 144, 104);
		this.frontLegTip.addChild(this.frontFoot);
		this.rearLeg = new Cuboid(this, "rearleg");
		this.rearLeg.setRotationPoint(-16.0F, 16.0F, 42.0F);
		this.rearLeg.addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, f, 0, 0);
		this.rearLegTip = new Cuboid(this, "rearlegtip");
		this.rearLegTip.setRotationPoint(0.0F, 32.0F, -4.0F);
		this.rearLegTip.addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, f, 196, 0);
		this.rearLeg.addChild(this.rearLegTip);
		this.rearFoot = new Cuboid(this, "rearfoot");
		this.rearFoot.setRotationPoint(0.0F, 31.0F, 4.0F);
		this.rearFoot.addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, f, 112, 0);
		this.rearLegTip.addChild(this.rearFoot);
	}

	public void method_17136(EnderDragonEntity enderDragonEntity, float f, float g, float h) {
		this.delta = h;
	}

	public void method_17137(EnderDragonEntity enderDragonEntity, float f, float g, float h, float i, float j, float k) {
		GlStateManager.pushMatrix();
		float l = MathHelper.lerp(this.delta, enderDragonEntity.field_7019, enderDragonEntity.field_7030);
		this.jaw.pitch = (float)(Math.sin((double)(l * (float) (Math.PI * 2))) + 1.0) * 0.2F;
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
			this.neck.yaw = this.updateRotations(es[0] - ds[0]) * (float) (Math.PI / 180.0) * 1.5F;
			this.neck.pitch = v + enderDragonEntity.method_6823(u, ds, es) * (float) (Math.PI / 180.0) * 1.5F * 5.0F;
			this.neck.roll = -this.updateRotations(es[0] - (double)s) * (float) (Math.PI / 180.0) * 1.5F;
			this.neck.rotationPointY = o;
			this.neck.rotationPointZ = p;
			this.neck.rotationPointX = n;
			o = (float)((double)o + Math.sin((double)this.neck.pitch) * 10.0);
			p = (float)((double)p - Math.cos((double)this.neck.yaw) * Math.cos((double)this.neck.pitch) * 10.0);
			n = (float)((double)n - Math.sin((double)this.neck.yaw) * Math.cos((double)this.neck.pitch) * 10.0);
			this.neck.render(k);
		}

		this.head.rotationPointY = o;
		this.head.rotationPointZ = p;
		this.head.rotationPointX = n;
		double[] fs = enderDragonEntity.method_6817(0, this.delta);
		this.head.yaw = this.updateRotations(fs[0] - ds[0]) * (float) (Math.PI / 180.0);
		this.head.pitch = this.updateRotations((double)enderDragonEntity.method_6823(6, ds, fs)) * (float) (Math.PI / 180.0) * 1.5F * 5.0F;
		this.head.roll = -this.updateRotations(fs[0] - (double)s) * (float) (Math.PI / 180.0);
		this.head.render(k);
		GlStateManager.pushMatrix();
		GlStateManager.translatef(0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(-r * 1.5F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translatef(0.0F, -1.0F, 0.0F);
		this.body.roll = 0.0F;
		this.body.render(k);

		for (int w = 0; w < 2; w++) {
			GlStateManager.enableCull();
			float v = l * (float) (Math.PI * 2);
			this.wing.pitch = 0.125F - (float)Math.cos((double)v) * 0.2F;
			this.wing.yaw = 0.25F;
			this.wing.roll = (float)(Math.sin((double)v) + 0.125) * 0.8F;
			this.wingTip.roll = -((float)(Math.sin((double)(v + 2.0F)) + 0.5)) * 0.75F;
			this.rearLeg.pitch = 1.0F + m * 0.1F;
			this.rearLegTip.pitch = 0.5F + m * 0.1F;
			this.rearFoot.pitch = 0.75F + m * 0.1F;
			this.frontLeg.pitch = 1.3F + m * 0.1F;
			this.frontLegTip.pitch = -0.5F - m * 0.1F;
			this.frontFoot.pitch = 0.75F + m * 0.1F;
			this.wing.render(k);
			this.frontLeg.render(k);
			this.rearLeg.render(k);
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
			this.neck.yaw = (this.updateRotations(fs[0] - ds[0]) * 1.5F + 180.0F) * (float) (Math.PI / 180.0);
			this.neck.pitch = x + (float)(fs[1] - ds[1]) * (float) (Math.PI / 180.0) * 1.5F * 5.0F;
			this.neck.roll = this.updateRotations(fs[0] - (double)s) * (float) (Math.PI / 180.0) * 1.5F;
			this.neck.rotationPointY = o;
			this.neck.rotationPointZ = p;
			this.neck.rotationPointX = n;
			o = (float)((double)o + Math.sin((double)this.neck.pitch) * 10.0);
			p = (float)((double)p - Math.cos((double)this.neck.yaw) * Math.cos((double)this.neck.pitch) * 10.0);
			n = (float)((double)n - Math.sin((double)this.neck.yaw) * Math.cos((double)this.neck.pitch) * 10.0);
			this.neck.render(k);
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
