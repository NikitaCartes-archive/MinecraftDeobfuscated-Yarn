package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BookModel extends Model {
	private final ModelPart leftCover = new ModelPart(this).setTextureOffset(0, 0).addCuboid(-6.0F, -5.0F, 0.0F, 6, 10, 0);
	private final ModelPart rightCover = new ModelPart(this).setTextureOffset(16, 0).addCuboid(0.0F, -5.0F, 0.0F, 6, 10, 0);
	private final ModelPart leftBlock;
	private final ModelPart rightBlock;
	private final ModelPart leftPage;
	private final ModelPart rightPage;
	private final ModelPart spine = new ModelPart(this).setTextureOffset(12, 0).addCuboid(-1.0F, -5.0F, 0.0F, 2, 10, 0);

	public BookModel() {
		this.leftBlock = new ModelPart(this).setTextureOffset(0, 10).addCuboid(0.0F, -4.0F, -0.99F, 5, 8, 1);
		this.rightBlock = new ModelPart(this).setTextureOffset(12, 10).addCuboid(0.0F, -4.0F, -0.01F, 5, 8, 1);
		this.leftPage = new ModelPart(this).setTextureOffset(24, 10).addCuboid(0.0F, -4.0F, 0.0F, 5, 8, 0);
		this.rightPage = new ModelPart(this).setTextureOffset(24, 10).addCuboid(0.0F, -4.0F, 0.0F, 5, 8, 0);
		this.leftCover.setPivot(0.0F, 0.0F, -1.0F);
		this.rightCover.setPivot(0.0F, 0.0F, 1.0F);
		this.spine.yaw = (float) (Math.PI / 2);
	}

	public void render(float ticks, float leftPageAngle, float rightPageAngle, float pageTurningSpeed, float f, float g) {
		this.setPageAngles(ticks, leftPageAngle, rightPageAngle, pageTurningSpeed, f, g);
		this.leftCover.render(g);
		this.rightCover.render(g);
		this.spine.render(g);
		this.leftBlock.render(g);
		this.rightBlock.render(g);
		this.leftPage.render(g);
		this.rightPage.render(g);
	}

	private void setPageAngles(float ticks, float leftPageAngle, float rightPageAngle, float pageTurningSpeed, float f, float g) {
		float h = (MathHelper.sin(ticks * 0.02F) * 0.1F + 1.25F) * pageTurningSpeed;
		this.leftCover.yaw = (float) Math.PI + h;
		this.rightCover.yaw = -h;
		this.leftBlock.yaw = h;
		this.rightBlock.yaw = -h;
		this.leftPage.yaw = h - h * 2.0F * leftPageAngle;
		this.rightPage.yaw = h - h * 2.0F * rightPageAngle;
		this.leftBlock.pivotX = MathHelper.sin(h);
		this.rightBlock.pivotX = MathHelper.sin(h);
		this.leftPage.pivotX = MathHelper.sin(h);
		this.rightPage.pivotX = MathHelper.sin(h);
	}
}
