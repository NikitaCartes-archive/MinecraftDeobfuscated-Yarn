package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.SinglePartEntityModelWithChildTransform;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class class_9082 extends SinglePartEntityModelWithChildTransform<class_9069> {
	private static final float field_47858 = 16.02F;
	private static final float field_47859 = 32.5F;
	private static final float field_47860 = 25.0F;
	private static final float field_47861 = 22.5F;
	private static final float field_47862 = 16.5F;
	private static final float field_47863 = 2.5F;
	private static final String field_47864 = "head_cube";
	private static final String field_47865 = "right_ear_cube";
	private static final String field_47866 = "left_ear_cube";
	private final ModelPart field_47867;
	private final ModelPart field_47868;
	private final ModelPart field_47869;
	private final ModelPart field_47870;
	private final ModelPart field_47871;
	private final ModelPart field_47872;
	private final ModelPart field_47873;

	public class_9082(ModelPart modelPart) {
		super(0.6F, 16.02F);
		this.field_47867 = modelPart;
		this.field_47868 = modelPart.getChild(EntityModelPartNames.BODY);
		this.field_47869 = modelPart.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
		this.field_47870 = modelPart.getChild(EntityModelPartNames.LEFT_HIND_LEG);
		this.field_47872 = this.field_47868.getChild(EntityModelPartNames.HEAD);
		this.field_47873 = this.field_47868.getChild(EntityModelPartNames.TAIL);
		this.field_47871 = modelPart.getChild(EntityModelPartNames.CUBE);
	}

	public static TexturedModelData method_55818() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create()
				.uv(0, 20)
				.cuboid(-4.0F, -7.0F, -10.0F, 8.0F, 8.0F, 12.0F, new Dilation(0.3F))
				.uv(0, 40)
				.cuboid(-4.0F, -7.0F, -10.0F, 8.0F, 8.0F, 12.0F, new Dilation(0.0F)),
			ModelTransform.pivot(0.0F, 21.0F, 4.0F)
		);
		modelPartData2.addChild(
			EntityModelPartNames.TAIL,
			ModelPartBuilder.create().uv(44, 53).cuboid(-0.5F, -0.0865F, 0.0933F, 1.0F, 6.0F, 1.0F, new Dilation(0.0F)),
			ModelTransform.of(0.0F, -3.0F, 1.0F, 0.5061F, 0.0F, 0.0F)
		);
		ModelPartData modelPartData3 = modelPartData2.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -2.0F, -11.0F));
		modelPartData3.addChild(
			"head_cube",
			ModelPartBuilder.create().uv(43, 15).cuboid(-1.5F, -1.0F, -1.0F, 3.0F, 5.0F, 2.0F, new Dilation(0.0F)),
			ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F)
		);
		ModelPartData modelPartData4 = modelPartData3.addChild(EntityModelPartNames.RIGHT_EAR, ModelPartBuilder.create(), ModelTransform.pivot(-1.0F, -1.0F, 0.0F));
		modelPartData4.addChild(
			"right_ear_cube",
			ModelPartBuilder.create().uv(43, 10).cuboid(-2.0F, -3.0F, 0.0F, 2.0F, 5.0F, 0.0F, new Dilation(0.0F)),
			ModelTransform.of(-0.5F, 0.0F, -0.6F, 0.1886F, -0.3864F, -0.0718F)
		);
		ModelPartData modelPartData5 = modelPartData3.addChild(EntityModelPartNames.LEFT_EAR, ModelPartBuilder.create(), ModelTransform.pivot(1.0F, -2.0F, 0.0F));
		modelPartData5.addChild(
			"left_ear_cube",
			ModelPartBuilder.create().uv(47, 10).cuboid(0.0F, -3.0F, 0.0F, 2.0F, 5.0F, 0.0F, new Dilation(0.0F)),
			ModelTransform.of(0.5F, 1.0F, -0.6F, 0.1886F, 0.3864F, 0.0718F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_HIND_LEG,
			ModelPartBuilder.create().uv(51, 31).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)),
			ModelTransform.pivot(-2.0F, 21.0F, 4.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_HIND_LEG,
			ModelPartBuilder.create().uv(42, 31).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)),
			ModelTransform.pivot(2.0F, 21.0F, 4.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_FRONT_LEG,
			ModelPartBuilder.create().uv(51, 43).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)),
			ModelTransform.pivot(-2.0F, 21.0F, -4.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_FRONT_LEG,
			ModelPartBuilder.create().uv(42, 43).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)),
			ModelTransform.pivot(2.0F, 21.0F, -4.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.CUBE,
			ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -10.0F, -6.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F)),
			ModelTransform.pivot(0.0F, 24.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public ModelPart getPart() {
		return this.field_47867;
	}

	public void setAngles(class_9069 arg, float f, float g, float h, float i, float j) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		if (arg.method_55711()) {
			this.field_47868.hidden = true;
			this.field_47870.visible = false;
			this.field_47869.visible = false;
			this.field_47873.visible = false;
			this.field_47871.visible = true;
		} else {
			this.field_47868.hidden = false;
			this.field_47870.visible = true;
			this.field_47869.visible = true;
			this.field_47873.visible = true;
			this.field_47871.visible = false;
			this.field_47872.pitch = MathHelper.clamp(j, -22.5F, 25.0F) * (float) (Math.PI / 180.0);
			this.field_47872.yaw = MathHelper.clamp(i, -32.5F, 32.5F) * (float) (Math.PI / 180.0);
		}

		this.animateMovement(class_9079.field_47844, f, g, 16.5F, 2.5F);
		this.updateAnimation(arg.field_47780, class_9079.field_47845, h, 1.0F);
		this.updateAnimation(arg.field_47781, class_9079.field_47843, h, 1.0F);
	}
}
