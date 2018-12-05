package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class DragonHeadEntityModel extends Model {
	private final Cuboid head;
	private final Cuboid jaw;

	public DragonHeadEntityModel(float f) {
		this.textureWidth = 256;
		this.textureHeight = 256;
		this.setTextureOffset("body.body", 0, 0);
		this.setTextureOffset("wing.skin", -56, 88);
		this.setTextureOffset("wingtip.skin", -56, 144);
		this.setTextureOffset("rearleg.main", 0, 0);
		this.setTextureOffset("rearfoot.main", 112, 0);
		this.setTextureOffset("rearlegtip.main", 196, 0);
		this.setTextureOffset("head.upperhead", 112, 30);
		this.setTextureOffset("wing.bone", 112, 88);
		this.setTextureOffset("head.upperlip", 176, 44);
		this.setTextureOffset("jaw.jaw", 176, 65);
		this.setTextureOffset("frontleg.main", 112, 104);
		this.setTextureOffset("wingtip.bone", 112, 136);
		this.setTextureOffset("frontfoot.main", 144, 104);
		this.setTextureOffset("neck.box", 192, 104);
		this.setTextureOffset("frontlegtip.main", 226, 138);
		this.setTextureOffset("body.scale", 220, 53);
		this.setTextureOffset("head.scale", 0, 0);
		this.setTextureOffset("neck.scale", 48, 0);
		this.setTextureOffset("head.nostril", 112, 0);
		float g = -16.0F;
		this.head = new Cuboid(this, "head");
		this.head.addBox("upperlip", -6.0F, -1.0F, -24.0F, 12, 5, 16);
		this.head.addBox("upperhead", -8.0F, -8.0F, -10.0F, 16, 16, 16);
		this.head.mirror = true;
		this.head.addBox("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6);
		this.head.addBox("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4);
		this.head.mirror = false;
		this.head.addBox("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6);
		this.head.addBox("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4);
		this.jaw = new Cuboid(this, "jaw");
		this.jaw.setRotationPoint(0.0F, 4.0F, -8.0F);
		this.jaw.addBox("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16);
		this.head.addChild(this.jaw);
	}

	@Override
	public void render(Entity entity, float f, float g, float h, float i, float j, float k) {
		this.jaw.pitch = (float)(Math.sin((double)(f * (float) Math.PI * 0.2F)) + 1.0) * 0.2F;
		this.head.yaw = i * (float) (Math.PI / 180.0);
		this.head.pitch = j * (float) (Math.PI / 180.0);
		GlStateManager.translatef(0.0F, -0.374375F, 0.0F);
		GlStateManager.scalef(0.75F, 0.75F, 0.75F);
		this.head.render(k);
	}
}
