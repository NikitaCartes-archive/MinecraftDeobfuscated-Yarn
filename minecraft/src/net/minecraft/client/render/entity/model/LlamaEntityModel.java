package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class LlamaEntityModel<T extends AbstractDonkeyEntity> extends EntityModel<T> {
	private final ModelPart field_20935;
	private final ModelPart field_20936;
	private final ModelPart field_20937;
	private final ModelPart field_20938;
	private final ModelPart field_20939;
	private final ModelPart field_20940;
	private final ModelPart field_3430;
	private final ModelPart field_3429;

	public LlamaEntityModel(float f) {
		super(RenderLayer::getEntitySolid);
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.field_20935 = new ModelPart(this, 0, 0);
		this.field_20935.addCuboid(-2.0F, -14.0F, -10.0F, 4.0F, 4.0F, 9.0F, f);
		this.field_20935.setPivot(0.0F, 7.0F, -6.0F);
		this.field_20935.setTextureOffset(0, 14).addCuboid(-4.0F, -16.0F, -6.0F, 8.0F, 18.0F, 6.0F, f);
		this.field_20935.setTextureOffset(17, 0).addCuboid(-4.0F, -19.0F, -4.0F, 3.0F, 3.0F, 2.0F, f);
		this.field_20935.setTextureOffset(17, 0).addCuboid(1.0F, -19.0F, -4.0F, 3.0F, 3.0F, 2.0F, f);
		this.field_20936 = new ModelPart(this, 29, 0);
		this.field_20936.addCuboid(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F, f);
		this.field_20936.setPivot(0.0F, 5.0F, 2.0F);
		this.field_3430 = new ModelPart(this, 45, 28);
		this.field_3430.addCuboid(-3.0F, 0.0F, 0.0F, 8.0F, 8.0F, 3.0F, f);
		this.field_3430.setPivot(-8.5F, 3.0F, 3.0F);
		this.field_3430.yaw = (float) (Math.PI / 2);
		this.field_3429 = new ModelPart(this, 45, 41);
		this.field_3429.addCuboid(-3.0F, 0.0F, 0.0F, 8.0F, 8.0F, 3.0F, f);
		this.field_3429.setPivot(5.5F, 3.0F, 3.0F);
		this.field_3429.yaw = (float) (Math.PI / 2);
		int i = 4;
		int j = 14;
		this.field_20937 = new ModelPart(this, 29, 29);
		this.field_20937.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, f);
		this.field_20937.setPivot(-2.5F, 10.0F, 6.0F);
		this.field_20938 = new ModelPart(this, 29, 29);
		this.field_20938.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, f);
		this.field_20938.setPivot(2.5F, 10.0F, 6.0F);
		this.field_20939 = new ModelPart(this, 29, 29);
		this.field_20939.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, f);
		this.field_20939.setPivot(-2.5F, 10.0F, -4.0F);
		this.field_20940 = new ModelPart(this, 29, 29);
		this.field_20940.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, f);
		this.field_20940.setPivot(2.5F, 10.0F, -4.0F);
		this.field_20937.pivotX--;
		this.field_20938.pivotX++;
		this.field_20937.pivotZ += 0.0F;
		this.field_20938.pivotZ += 0.0F;
		this.field_20939.pivotX--;
		this.field_20940.pivotX++;
		this.field_20939.pivotZ--;
		this.field_20940.pivotZ--;
	}

	public void method_22962(T abstractDonkeyEntity, float f, float g, float h, float i, float j, float k) {
		this.field_20935.pitch = j * (float) (Math.PI / 180.0);
		this.field_20935.yaw = i * (float) (Math.PI / 180.0);
		this.field_20936.pitch = (float) (Math.PI / 2);
		this.field_20937.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		this.field_20938.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.field_20939.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.field_20940.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		boolean bl = !abstractDonkeyEntity.isBaby() && abstractDonkeyEntity.hasChest();
		this.field_3430.visible = bl;
		this.field_3429.visible = bl;
	}

	@Override
	public void renderItem(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h) {
		if (this.isChild) {
			float k = 2.0F;
			matrixStack.push();
			float l = 0.7F;
			matrixStack.scale(0.71428573F, 0.64935064F, 0.7936508F);
			matrixStack.translate(0.0, 1.3125, 0.22F);
			this.field_20935.render(matrixStack, vertexConsumer, 0.0625F, i, j, null, f, g, h);
			matrixStack.pop();
			matrixStack.push();
			float m = 1.1F;
			matrixStack.scale(0.625F, 0.45454544F, 0.45454544F);
			matrixStack.translate(0.0, 2.0625, 0.0);
			this.field_20936.render(matrixStack, vertexConsumer, 0.0625F, i, j, null, f, g, h);
			matrixStack.pop();
			matrixStack.push();
			matrixStack.scale(0.45454544F, 0.41322312F, 0.45454544F);
			matrixStack.translate(0.0, 2.0625, 0.0);
			ImmutableList.of(this.field_20937, this.field_20938, this.field_20939, this.field_20940, this.field_3430, this.field_3429)
				.forEach(modelPart -> modelPart.render(matrixStack, vertexConsumer, 0.0625F, i, j, null, f, g, h));
			matrixStack.pop();
		} else {
			ImmutableList.of(
					this.field_20935, this.field_20936, this.field_20937, this.field_20938, this.field_20939, this.field_20940, this.field_3430, this.field_3429
				)
				.forEach(modelPart -> modelPart.render(matrixStack, vertexConsumer, 0.0625F, i, j, null, f, g, h));
		}
	}
}
