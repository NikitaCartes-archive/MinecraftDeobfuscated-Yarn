package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BookModel extends Model {
	private final Cuboid leftCover = new Cuboid(this).setTextureOffset(0, 0).addBox(-6.0F, -5.0F, 0.0F, 6, 10, 0);
	private final Cuboid rightCover = new Cuboid(this).setTextureOffset(16, 0).addBox(0.0F, -5.0F, 0.0F, 6, 10, 0);
	private final Cuboid leftBlock;
	private final Cuboid rightBlock;
	private final Cuboid leftPage;
	private final Cuboid rightPage;
	private final Cuboid spine = new Cuboid(this).setTextureOffset(12, 0).addBox(-1.0F, -5.0F, 0.0F, 2, 10, 0);

	public BookModel() {
		this.leftBlock = new Cuboid(this).setTextureOffset(0, 10).addBox(0.0F, -4.0F, -0.99F, 5, 8, 1);
		this.rightBlock = new Cuboid(this).setTextureOffset(12, 10).addBox(0.0F, -4.0F, -0.01F, 5, 8, 1);
		this.leftPage = new Cuboid(this).setTextureOffset(24, 10).addBox(0.0F, -4.0F, 0.0F, 5, 8, 0);
		this.rightPage = new Cuboid(this).setTextureOffset(24, 10).addBox(0.0F, -4.0F, 0.0F, 5, 8, 0);
		this.leftCover.setRotationPoint(0.0F, 0.0F, -1.0F);
		this.rightCover.setRotationPoint(0.0F, 0.0F, 1.0F);
		this.spine.yaw = (float) (Math.PI / 2);
	}

	public void render(float f, float g, float h, float i, float j, float k) {
		this.setPageAngles(f, g, h, i, j, k);
		this.leftCover.render(k);
		this.rightCover.render(k);
		this.spine.render(k);
		this.leftBlock.render(k);
		this.rightBlock.render(k);
		this.leftPage.render(k);
		this.rightPage.render(k);
	}

	private void setPageAngles(float f, float g, float h, float i, float j, float k) {
		float l = (MathHelper.sin(f * 0.02F) * 0.1F + 1.25F) * i;
		this.leftCover.yaw = (float) Math.PI + l;
		this.rightCover.yaw = -l;
		this.leftBlock.yaw = l;
		this.rightBlock.yaw = -l;
		this.leftPage.yaw = l - l * 2.0F * g;
		this.rightPage.yaw = l - l * 2.0F * h;
		this.leftBlock.rotationPointX = MathHelper.sin(l);
		this.rightBlock.rotationPointX = MathHelper.sin(l);
		this.leftPage.rotationPointX = MathHelper.sin(l);
		this.rightPage.rotationPointX = MathHelper.sin(l);
	}
}
