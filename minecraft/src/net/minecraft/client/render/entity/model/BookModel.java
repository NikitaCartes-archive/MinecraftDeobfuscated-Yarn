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
	private final ModelPart leftBlock;
	private final ModelPart rightBlock;
	private final ModelPart leftPage;
	private final ModelPart rightPage;
	private final ModelPart spine = new ModelPart(64, 32, 12, 0).addCuboid(-1.0F, -5.0F, 0.0F, 2.0F, 10.0F, 0.005F);
	private final List<ModelPart> parts;

	public BookModel() {
		super(RenderLayer::getEntitySolid);
		this.leftBlock = new ModelPart(64, 32, 0, 10).addCuboid(0.0F, -4.0F, -0.99F, 5.0F, 8.0F, 1.0F);
		this.rightBlock = new ModelPart(64, 32, 12, 10).addCuboid(0.0F, -4.0F, -0.01F, 5.0F, 8.0F, 1.0F);
		this.leftPage = new ModelPart(64, 32, 24, 10).addCuboid(0.0F, -4.0F, 0.0F, 5.0F, 8.0F, 0.005F);
		this.rightPage = new ModelPart(64, 32, 24, 10).addCuboid(0.0F, -4.0F, 0.0F, 5.0F, 8.0F, 0.005F);
		this.parts = ImmutableList.of(this.leftCover, this.rightCover, this.spine, this.leftBlock, this.rightBlock, this.leftPage, this.rightPage);
		this.leftCover.setPivot(0.0F, 0.0F, -1.0F);
		this.rightCover.setPivot(0.0F, 0.0F, 1.0F);
		this.spine.yaw = (float) (Math.PI / 2);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		this.method_24184(matrices, vertices, light, overlay, red, green, blue, alpha);
	}

	public void method_24184(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		this.parts.forEach(modelPart -> modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha));
	}

	public void setPageAngles(float f, float g, float h, float i) {
		float j = (MathHelper.sin(f * 0.02F) * 0.1F + 1.25F) * i;
		this.leftCover.yaw = (float) Math.PI + j;
		this.rightCover.yaw = -j;
		this.leftBlock.yaw = j;
		this.rightBlock.yaw = -j;
		this.leftPage.yaw = j - j * 2.0F * g;
		this.rightPage.yaw = j - j * 2.0F * h;
		this.leftBlock.pivotX = MathHelper.sin(j);
		this.rightBlock.pivotX = MathHelper.sin(j);
		this.leftPage.pivotX = MathHelper.sin(j);
		this.rightPage.pivotX = MathHelper.sin(j);
	}
}
