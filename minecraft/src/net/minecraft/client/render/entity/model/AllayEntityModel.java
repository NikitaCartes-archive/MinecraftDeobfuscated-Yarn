package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class AllayEntityModel extends SinglePartEntityModel<AllayEntity> implements ModelWithArms {
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart rightArm;
	private final ModelPart leftArm;
	private final ModelPart rightWing;
	private final ModelPart leftWing;
	private static final float field_38999 = (float) (Math.PI / 4);
	private static final float field_39000 = -1.134464F;
	private static final float field_39001 = (float) (-Math.PI / 3);

	public AllayEntityModel(ModelPart root) {
		super(RenderLayer::getEntityTranslucent);
		this.root = root.getChild(EntityModelPartNames.ROOT);
		this.head = this.root.getChild(EntityModelPartNames.HEAD);
		this.body = this.root.getChild(EntityModelPartNames.BODY);
		this.rightArm = this.body.getChild(EntityModelPartNames.RIGHT_ARM);
		this.leftArm = this.body.getChild(EntityModelPartNames.LEFT_ARM);
		this.rightWing = this.body.getChild(EntityModelPartNames.RIGHT_WING);
		this.leftWing = this.body.getChild(EntityModelPartNames.LEFT_WING);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.ROOT, ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 23.5F, 0.0F));
		modelPartData2.addChild(
			EntityModelPartNames.HEAD,
			ModelPartBuilder.create().uv(0, 0).cuboid(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F, new Dilation(0.0F)),
			ModelTransform.pivot(0.0F, -3.99F, 0.0F)
		);
		ModelPartData modelPartData3 = modelPartData2.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create()
				.uv(0, 10)
				.cuboid(-1.5F, 0.0F, -1.0F, 3.0F, 4.0F, 2.0F, new Dilation(0.0F))
				.uv(0, 16)
				.cuboid(-1.5F, 0.0F, -1.0F, 3.0F, 5.0F, 2.0F, new Dilation(-0.2F)),
			ModelTransform.pivot(0.0F, -4.0F, 0.0F)
		);
		modelPartData3.addChild(
			EntityModelPartNames.RIGHT_ARM,
			ModelPartBuilder.create().uv(23, 0).cuboid(-0.75F, -0.5F, -1.0F, 1.0F, 4.0F, 2.0F, new Dilation(-0.01F)),
			ModelTransform.pivot(-1.75F, 0.5F, 0.0F)
		);
		modelPartData3.addChild(
			EntityModelPartNames.LEFT_ARM,
			ModelPartBuilder.create().uv(23, 6).cuboid(-0.25F, -0.5F, -1.0F, 1.0F, 4.0F, 2.0F, new Dilation(-0.01F)),
			ModelTransform.pivot(1.75F, 0.5F, 0.0F)
		);
		modelPartData3.addChild(
			EntityModelPartNames.RIGHT_WING,
			ModelPartBuilder.create().uv(16, 14).cuboid(0.0F, 1.0F, 0.0F, 0.0F, 5.0F, 8.0F, new Dilation(0.0F)),
			ModelTransform.pivot(-0.5F, 0.0F, 0.6F)
		);
		modelPartData3.addChild(
			EntityModelPartNames.LEFT_WING,
			ModelPartBuilder.create().uv(16, 14).cuboid(0.0F, 1.0F, 0.0F, 0.0F, 5.0F, 8.0F, new Dilation(0.0F)),
			ModelTransform.pivot(0.5F, 0.0F, 0.6F)
		);
		return TexturedModelData.of(modelData, 32, 32);
	}

	public void setAngles(AllayEntity allayEntity, float f, float g, float h, float i, float j) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		float k = h * 20.0F * (float) (Math.PI / 180.0) + f;
		float l = MathHelper.cos(k) * (float) Math.PI * 0.15F + g;
		float m = h - (float)allayEntity.age;
		float n = h * 9.0F * (float) (Math.PI / 180.0);
		float o = Math.min(g / 0.3F, 1.0F);
		float p = 1.0F - o;
		float q = allayEntity.method_43397(m);
		if (allayEntity.isDancing()) {
			float r = h * 8.0F * (float) (Math.PI / 180.0) + g;
			float s = MathHelper.cos(r) * 16.0F * (float) (Math.PI / 180.0);
			float t = allayEntity.method_44368(m);
			float u = MathHelper.cos(r) * 14.0F * (float) (Math.PI / 180.0);
			float v = MathHelper.cos(r) * 30.0F * (float) (Math.PI / 180.0);
			this.root.yaw = allayEntity.isSpinning() ? (float) (Math.PI * 4) * t : this.root.yaw;
			this.root.roll = s * (1.0F - t);
			this.head.yaw = v * (1.0F - t);
			this.head.roll = u * (1.0F - t);
		} else {
			this.head.pitch = j * (float) (Math.PI / 180.0);
			this.head.yaw = i * (float) (Math.PI / 180.0);
		}

		this.rightWing.pitch = 0.43633232F * (1.0F - o);
		this.rightWing.yaw = (float) (-Math.PI / 4) + l;
		this.leftWing.pitch = 0.43633232F * (1.0F - o);
		this.leftWing.yaw = (float) (Math.PI / 4) - l;
		this.body.pitch = o * (float) (Math.PI / 4);
		float r = q * MathHelper.lerp(o, (float) (-Math.PI / 3), -1.134464F);
		this.root.pivotY = this.root.pivotY + (float)Math.cos((double)n) * 0.25F * p;
		this.rightArm.pitch = r;
		this.leftArm.pitch = r;
		float s = p * (1.0F - q);
		float t = 0.43633232F - MathHelper.cos(n + (float) (Math.PI * 3.0 / 2.0)) * (float) Math.PI * 0.075F * s;
		this.leftArm.roll = -t;
		this.rightArm.roll = t;
		this.rightArm.yaw = 0.27925268F * q;
		this.leftArm.yaw = -0.27925268F * q;
	}

	@Override
	public void setArmAngle(Arm arm, MatrixStack matrices) {
		float f = 1.0F;
		float g = 3.0F;
		this.root.rotate(matrices);
		this.body.rotate(matrices);
		matrices.translate(0.0F, 0.0625F, 0.1875F);
		matrices.multiply(RotationAxis.POSITIVE_X.rotation(this.rightArm.pitch));
		matrices.scale(0.7F, 0.7F, 0.7F);
		matrices.translate(0.0625F, 0.0F, 0.0F);
	}
}
