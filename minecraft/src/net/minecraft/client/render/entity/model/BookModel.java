package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.texture.Sprite;
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
	private final List<ModelPart> field_20786;

	public BookModel() {
		this.leftBlock = new ModelPart(64, 32, 0, 10).addCuboid(0.0F, -4.0F, -0.99F, 5.0F, 8.0F, 1.0F);
		this.rightBlock = new ModelPart(64, 32, 12, 10).addCuboid(0.0F, -4.0F, -0.01F, 5.0F, 8.0F, 1.0F);
		this.leftPage = new ModelPart(64, 32, 24, 10).addCuboid(0.0F, -4.0F, 0.0F, 5.0F, 8.0F, 0.005F);
		this.rightPage = new ModelPart(64, 32, 24, 10).addCuboid(0.0F, -4.0F, 0.0F, 5.0F, 8.0F, 0.005F);
		this.field_20786 = ImmutableList.of(this.leftCover, this.rightCover, this.spine, this.leftBlock, this.rightBlock, this.leftPage, this.rightPage);
		this.leftCover.setRotationPoint(0.0F, 0.0F, -1.0F);
		this.rightCover.setRotationPoint(0.0F, 0.0F, 1.0F);
		this.spine.yaw = (float) (Math.PI / 2);
	}

	public void method_22693(float f) {
		this.field_20786.forEach(modelPart -> modelPart.render(f));
	}

	public void render(BufferBuilder bufferBuilder, float f, int i, int j, Sprite sprite) {
		this.field_20786.forEach(modelPart -> modelPart.method_22698(bufferBuilder, f, i, j, sprite));
	}

	public void setPageAngles(float f, float g, float h, float i) {
		float j = (MathHelper.sin(f * 0.02F) * 0.1F + 1.25F) * i;
		this.leftCover.yaw = (float) Math.PI + j;
		this.rightCover.yaw = -j;
		this.leftBlock.yaw = j;
		this.rightBlock.yaw = -j;
		this.leftPage.yaw = j - j * 2.0F * g;
		this.rightPage.yaw = j - j * 2.0F * h;
		this.leftBlock.rotationPointX = MathHelper.sin(j);
		this.rightBlock.rotationPointX = MathHelper.sin(j);
		this.leftPage.rotationPointX = MathHelper.sin(j);
		this.rightPage.rotationPointX = MathHelper.sin(j);
	}
}
