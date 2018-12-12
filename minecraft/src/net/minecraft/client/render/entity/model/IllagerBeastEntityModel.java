package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.mob.IllagerBeastEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class IllagerBeastEntityModel extends EntityModel<IllagerBeastEntity> {
	private final Cuboid field_3386;
	private final Cuboid field_3388;
	private final Cuboid field_3387;
	private final Cuboid field_3385;
	private final Cuboid field_3383;
	private final Cuboid field_3389;
	private final Cuboid field_3382;
	private final Cuboid field_3384;

	public IllagerBeastEntityModel() {
		this.textureWidth = 128;
		this.textureHeight = 128;
		int i = 16;
		float f = 0.0F;
		this.field_3384 = new Cuboid(this);
		this.field_3384.setRotationPoint(0.0F, -7.0F, -1.5F);
		this.field_3384.setTextureOffset(68, 73).addBox(-5.0F, -1.0F, -18.0F, 10, 10, 18, 0.0F);
		this.field_3386 = new Cuboid(this);
		this.field_3386.setRotationPoint(0.0F, 16.0F, -17.0F);
		this.field_3386.setTextureOffset(0, 0).addBox(-8.0F, -20.0F, -14.0F, 16, 20, 16, 0.0F);
		this.field_3386.setTextureOffset(0, 0).addBox(-2.0F, -6.0F, -18.0F, 4, 8, 4, 0.0F);
		Cuboid cuboid = new Cuboid(this);
		cuboid.setRotationPoint(-10.0F, -14.0F, -8.0F);
		cuboid.setTextureOffset(74, 55).addBox(0.0F, -14.0F, -2.0F, 2, 14, 4, 0.0F);
		cuboid.pitch = 1.0995574F;
		this.field_3386.addChild(cuboid);
		Cuboid cuboid2 = new Cuboid(this);
		cuboid2.mirror = true;
		cuboid2.setRotationPoint(8.0F, -14.0F, -8.0F);
		cuboid2.setTextureOffset(74, 55).addBox(0.0F, -14.0F, -2.0F, 2, 14, 4, 0.0F);
		cuboid2.pitch = 1.0995574F;
		this.field_3386.addChild(cuboid2);
		this.field_3388 = new Cuboid(this);
		this.field_3388.setRotationPoint(0.0F, -2.0F, 2.0F);
		this.field_3388.setTextureOffset(0, 36).addBox(-8.0F, 0.0F, -16.0F, 16, 3, 16, 0.0F);
		this.field_3386.addChild(this.field_3388);
		this.field_3384.addChild(this.field_3386);
		this.field_3387 = new Cuboid(this);
		this.field_3387.setTextureOffset(0, 55).addBox(-7.0F, -10.0F, -7.0F, 14, 16, 20, 0.0F);
		this.field_3387.setTextureOffset(0, 91).addBox(-6.0F, 6.0F, -7.0F, 12, 13, 18, 0.0F);
		this.field_3387.setRotationPoint(0.0F, 1.0F, 2.0F);
		this.field_3385 = new Cuboid(this, 96, 0);
		this.field_3385.addBox(-4.0F, 0.0F, -4.0F, 8, 37, 8, 0.0F);
		this.field_3385.setRotationPoint(-8.0F, -13.0F, 18.0F);
		this.field_3383 = new Cuboid(this, 96, 0);
		this.field_3383.mirror = true;
		this.field_3383.addBox(-4.0F, 0.0F, -4.0F, 8, 37, 8, 0.0F);
		this.field_3383.setRotationPoint(8.0F, -13.0F, 18.0F);
		this.field_3389 = new Cuboid(this, 64, 0);
		this.field_3389.addBox(-4.0F, 0.0F, -4.0F, 8, 37, 8, 0.0F);
		this.field_3389.setRotationPoint(-8.0F, -13.0F, -5.0F);
		this.field_3382 = new Cuboid(this, 64, 0);
		this.field_3382.mirror = true;
		this.field_3382.addBox(-4.0F, 0.0F, -4.0F, 8, 37, 8, 0.0F);
		this.field_3382.setRotationPoint(8.0F, -13.0F, -5.0F);
	}

	public void method_17090(IllagerBeastEntity illagerBeastEntity, float f, float g, float h, float i, float j, float k) {
		this.method_17091(illagerBeastEntity, f, g, h, i, j, k);
		this.field_3384.render(k);
		this.field_3387.render(k);
		this.field_3385.render(k);
		this.field_3383.render(k);
		this.field_3389.render(k);
		this.field_3382.render(k);
	}

	public void method_17091(IllagerBeastEntity illagerBeastEntity, float f, float g, float h, float i, float j, float k) {
		this.field_3386.pitch = j * (float) (Math.PI / 180.0);
		this.field_3386.yaw = i * (float) (Math.PI / 180.0);
		this.field_3387.pitch = (float) (Math.PI / 2);
		float l = 0.4F * g;
		this.field_3385.pitch = MathHelper.cos(f * 0.6662F) * l;
		this.field_3383.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * l;
		this.field_3389.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * l;
		this.field_3382.pitch = MathHelper.cos(f * 0.6662F) * l;
	}

	public void method_17089(IllagerBeastEntity illagerBeastEntity, float f, float g, float h) {
		super.animateModel(illagerBeastEntity, f, g, h);
		int i = illagerBeastEntity.method_7074();
		int j = illagerBeastEntity.method_7072();
		int k = 20;
		int l = illagerBeastEntity.method_7070();
		int m = 10;
		if (l > 0) {
			float n = this.method_2801((float)l - h, 10.0F);
			float o = (1.0F + n) * 0.5F;
			float p = o * o * o * 12.0F;
			float q = p * MathHelper.sin(this.field_3384.pitch);
			this.field_3384.rotationPointZ = -6.5F + p;
			this.field_3384.rotationPointY = -7.0F - q;
			float r = MathHelper.sin(((float)l - h) / 10.0F * (float) Math.PI * 0.25F);
			this.field_3388.pitch = (float) (Math.PI / 2) * r;
			if (l > 5) {
				this.field_3388.pitch = MathHelper.sin(((float)(-4 + l) - h) / 4.0F) * (float) Math.PI * 0.4F;
			} else {
				this.field_3388.pitch = (float) (Math.PI / 20) * MathHelper.sin((float) Math.PI * ((float)l - h) / 10.0F);
			}
		} else {
			float n = -1.0F;
			float o = -1.0F * MathHelper.sin(this.field_3384.pitch);
			this.field_3384.rotationPointX = 0.0F;
			this.field_3384.rotationPointY = -7.0F - o;
			this.field_3384.rotationPointZ = 5.5F;
			boolean bl = i > 0;
			this.field_3384.pitch = bl ? 0.21991149F : 0.0F;
			this.field_3388.pitch = (float) Math.PI * (bl ? 0.05F : 0.01F);
			if (bl) {
				double d = (double)i / 40.0;
				this.field_3384.rotationPointX = (float)Math.sin(d * 10.0) * 3.0F;
			} else if (j > 0) {
				float q = MathHelper.sin(((float)(20 - j) - h) / 20.0F * (float) Math.PI * 0.25F);
				this.field_3388.pitch = (float) (Math.PI / 2) * q;
			}
		}
	}

	private float method_2801(float f, float g) {
		return (Math.abs(f % g - g * 0.5F) - g * 0.25F) / (g * 0.25F);
	}
}
