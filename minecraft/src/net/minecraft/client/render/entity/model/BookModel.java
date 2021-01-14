package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BookModel extends Model {
	private final ModelPart leftCover = new ModelPart(64, 32, 0, 0).addCuboid(-6.0F, -5.0F, -0.005F, 6.0F, 10.0F, 0.005F);
	private final ModelPart rightCover = new ModelPart(64, 32, 16, 0).addCuboid(0.0F, -5.0F, -0.005F, 6.0F, 10.0F, 0.005F);
	private final ModelPart leftPages;
	private final ModelPart rightPages;
	private final ModelPart leftPage;
	private final ModelPart rightPage;
	private final ModelPart spine = new ModelPart(64, 32, 12, 0).addCuboid(-1.0F, -5.0F, 0.0F, 2.0F, 10.0F, 0.005F);
	private final List<ModelPart> parts;

	public BookModel() {
		super(RenderLayer::getEntitySolid);
		this.leftPages = new ModelPart(64, 32, 0, 10).addCuboid(0.0F, -4.0F, -0.99F, 5.0F, 8.0F, 1.0F);
		this.rightPages = new ModelPart(64, 32, 12, 10).addCuboid(0.0F, -4.0F, -0.01F, 5.0F, 8.0F, 1.0F);
		this.leftPage = new ModelPart(64, 32, 24, 10).addCuboid(0.0F, -4.0F, 0.0F, 5.0F, 8.0F, 0.005F);
		this.rightPage = new ModelPart(64, 32, 24, 10).addCuboid(0.0F, -4.0F, 0.0F, 5.0F, 8.0F, 0.005F);
		this.parts = ImmutableList.of(this.leftCover, this.rightCover, this.spine, this.leftPages, this.rightPages, this.leftPage, this.rightPage);
		this.leftCover.setPivot(0.0F, 0.0F, -1.0F);
		this.rightCover.setPivot(0.0F, 0.0F, 1.0F);
		this.spine.yaw = (float) (Math.PI / 2);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		this.renderBook(matrices, vertices, light, overlay, red, green, blue, alpha);
	}

	public void renderBook(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		this.parts.forEach(modelPart -> modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha));
	}

	public void setPageAngles(float pageTurnAmount, float leftFlipAmount, float rightFlipAmount, float pageTurnSpeed) {
		float f = (MathHelper.sin(pageTurnAmount * 0.02F) * 0.1F + 1.25F) * pageTurnSpeed;
		this.leftCover.yaw = (float) Math.PI + f;
		this.rightCover.yaw = -f;
		this.leftPages.yaw = f;
		this.rightPages.yaw = -f;
		this.leftPage.yaw = f - f * 2.0F * leftFlipAmount;
		this.rightPage.yaw = f - f * 2.0F * rightFlipAmount;
		this.leftPages.pivotX = MathHelper.sin(f);
		this.rightPages.pivotX = MathHelper.sin(f);
		this.leftPage.pivotX = MathHelper.sin(f);
		this.rightPage.pivotX = MathHelper.sin(f);
	}
}
