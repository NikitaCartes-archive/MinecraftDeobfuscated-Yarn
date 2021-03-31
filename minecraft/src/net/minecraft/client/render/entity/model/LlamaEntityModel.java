package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class LlamaEntityModel<T extends AbstractDonkeyEntity> extends EntityModel<T> {
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart rightChest;
	private final ModelPart leftChest;

	public LlamaEntityModel(ModelPart root) {
		this.head = root.getChild(EntityModelPartNames.HEAD);
		this.body = root.getChild(EntityModelPartNames.BODY);
		this.rightChest = root.getChild(EntityModelPartNames.RIGHT_CHEST);
		this.leftChest = root.getChild(EntityModelPartNames.LEFT_CHEST);
		this.rightHindLeg = root.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
		this.leftHindLeg = root.getChild(EntityModelPartNames.LEFT_HIND_LEG);
		this.rightFrontLeg = root.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
		this.leftFrontLeg = root.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
	}

	public static TexturedModelData getTexturedModelData(Dilation dilation) {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			EntityModelPartNames.HEAD,
			ModelPartBuilder.create()
				.uv(0, 0)
				.cuboid(-2.0F, -14.0F, -10.0F, 4.0F, 4.0F, 9.0F, dilation)
				.uv(0, 14)
				.cuboid(EntityModelPartNames.NECK, -4.0F, -16.0F, -6.0F, 8.0F, 18.0F, 6.0F, dilation)
				.uv(17, 0)
				.cuboid("ear", -4.0F, -19.0F, -4.0F, 3.0F, 3.0F, 2.0F, dilation)
				.uv(17, 0)
				.cuboid("ear", 1.0F, -19.0F, -4.0F, 3.0F, 3.0F, 2.0F, dilation),
			ModelTransform.pivot(0.0F, 7.0F, -6.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create().uv(29, 0).cuboid(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F, dilation),
			ModelTransform.of(0.0F, 5.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_CHEST,
			ModelPartBuilder.create().uv(45, 28).cuboid(-3.0F, 0.0F, 0.0F, 8.0F, 8.0F, 3.0F, dilation),
			ModelTransform.of(-8.5F, 3.0F, 3.0F, 0.0F, (float) (Math.PI / 2), 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_CHEST,
			ModelPartBuilder.create().uv(45, 41).cuboid(-3.0F, 0.0F, 0.0F, 8.0F, 8.0F, 3.0F, dilation),
			ModelTransform.of(5.5F, 3.0F, 3.0F, 0.0F, (float) (Math.PI / 2), 0.0F)
		);
		int i = 4;
		int j = 14;
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(29, 29).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, dilation);
		modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(-3.5F, 10.0F, 6.0F));
		modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(3.5F, 10.0F, 6.0F));
		modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(-3.5F, 10.0F, -5.0F));
		modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(3.5F, 10.0F, -5.0F));
		return TexturedModelData.of(modelData, 128, 64);
	}

	public void setAngles(T abstractDonkeyEntity, float f, float g, float h, float i, float j) {
		this.head.pitch = j * (float) (Math.PI / 180.0);
		this.head.yaw = i * (float) (Math.PI / 180.0);
		this.rightHindLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		this.leftHindLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.rightFrontLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.leftFrontLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		boolean bl = !abstractDonkeyEntity.isBaby() && abstractDonkeyEntity.hasChest();
		this.rightChest.visible = bl;
		this.leftChest.visible = bl;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		if (this.child) {
			float f = 2.0F;
			matrices.push();
			float g = 0.7F;
			matrices.scale(0.71428573F, 0.64935064F, 0.7936508F);
			matrices.translate(0.0, 1.3125, 0.22F);
			this.head.render(matrices, vertices, light, overlay, red, green, blue, alpha);
			matrices.pop();
			matrices.push();
			float h = 1.1F;
			matrices.scale(0.625F, 0.45454544F, 0.45454544F);
			matrices.translate(0.0, 2.0625, 0.0);
			this.body.render(matrices, vertices, light, overlay, red, green, blue, alpha);
			matrices.pop();
			matrices.push();
			matrices.scale(0.45454544F, 0.41322312F, 0.45454544F);
			matrices.translate(0.0, 2.0625, 0.0);
			ImmutableList.of(this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg, this.rightChest, this.leftChest)
				.forEach(modelPart -> modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha));
			matrices.pop();
		} else {
			ImmutableList.of(this.head, this.body, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg, this.rightChest, this.leftChest)
				.forEach(modelPart -> modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha));
		}
	}
}
