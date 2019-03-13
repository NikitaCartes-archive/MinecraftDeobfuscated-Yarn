package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;

@Environment(EnvType.CLIENT)
public class DragonHeadEntityModel extends SkullEntityModel {
	private final Cuboid field_3638;
	private final Cuboid field_3639;

	public DragonHeadEntityModel(float f) {
		this.textureWidth = 256;
		this.textureHeight = 256;
		float g = -16.0F;
		this.field_3638 = new Cuboid(this, "head");
		this.field_3638.addBox("upperlip", -6.0F, -1.0F, -24.0F, 12, 5, 16, f, 176, 44);
		this.field_3638.addBox("upperhead", -8.0F, -8.0F, -10.0F, 16, 16, 16, f, 112, 30);
		this.field_3638.mirror = true;
		this.field_3638.addBox("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, f, 0, 0);
		this.field_3638.addBox("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, f, 112, 0);
		this.field_3638.mirror = false;
		this.field_3638.addBox("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, f, 0, 0);
		this.field_3638.addBox("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, f, 112, 0);
		this.field_3639 = new Cuboid(this, "jaw");
		this.field_3639.setRotationPoint(0.0F, 4.0F, -8.0F);
		this.field_3639.addBox("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16, f, 176, 65);
		this.field_3638.addChild(this.field_3639);
	}

	@Override
	public void setRotationAngles(float f, float g, float h, float i, float j, float k) {
		this.field_3639.pitch = (float)(Math.sin((double)(f * (float) Math.PI * 0.2F)) + 1.0) * 0.2F;
		this.field_3638.yaw = i * (float) (Math.PI / 180.0);
		this.field_3638.pitch = j * (float) (Math.PI / 180.0);
		GlStateManager.translatef(0.0F, -0.374375F, 0.0F);
		GlStateManager.scalef(0.75F, 0.75F, 0.75F);
		this.field_3638.render(k);
	}
}
