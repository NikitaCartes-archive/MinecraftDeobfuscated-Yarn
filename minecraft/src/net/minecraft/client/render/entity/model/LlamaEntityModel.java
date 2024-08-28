package net.minecraft.client.render.entity.model;

import java.util.Map.Entry;
import java.util.function.UnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.state.LlamaEntityRenderState;
import net.minecraft.util.math.MathHelper;

/**
 * Represents the model of a llama-like entity.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#HEAD}</td><td>Root part</td><td>{@link #head}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#BODY}</td><td>Root part</td><td>{@link #body}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_CHEST}</td><td>Root part</td><td>{@link #rightChest}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_CHEST}</td><td>Root part</td><td>{@link #leftChest}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_HIND_LEG}</td><td>Root part</td><td>{@link #rightHindLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_HIND_LEG}</td><td>Root part</td><td>{@link #leftHindLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_FRONT_LEG}</td><td>Root part</td><td>{@link #rightFrontLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_FRONT_LEG}</td><td>Root part</td><td>{@link #leftFrontLeg}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(EnvType.CLIENT)
public class LlamaEntityModel extends EntityModel<LlamaEntityRenderState> {
	public static final ModelTransformer BABY_TRANSFORMER = LlamaEntityModel::transformBaby;
	private final ModelPart head;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart rightChest;
	private final ModelPart leftChest;

	public LlamaEntityModel(ModelPart modelPart) {
		super(modelPart);
		this.head = modelPart.getChild(EntityModelPartNames.HEAD);
		this.rightChest = modelPart.getChild(EntityModelPartNames.RIGHT_CHEST);
		this.leftChest = modelPart.getChild(EntityModelPartNames.LEFT_CHEST);
		this.rightHindLeg = modelPart.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
		this.leftHindLeg = modelPart.getChild(EntityModelPartNames.LEFT_HIND_LEG);
		this.rightFrontLeg = modelPart.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
		this.leftFrontLeg = modelPart.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
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

	private static ModelData transformBaby(ModelData modelData) {
		float f = 2.0F;
		float g = 0.7F;
		float h = 1.1F;
		UnaryOperator<ModelTransform> unaryOperator = modelTransform -> modelTransform.addPivot(0.0F, 21.0F, 3.52F).scaled(0.71428573F, 0.64935064F, 0.7936508F);
		UnaryOperator<ModelTransform> unaryOperator2 = modelTransform -> modelTransform.addPivot(0.0F, 33.0F, 0.0F).scaled(0.625F, 0.45454544F, 0.45454544F);
		UnaryOperator<ModelTransform> unaryOperator3 = modelTransform -> modelTransform.addPivot(0.0F, 33.0F, 0.0F).scaled(0.45454544F, 0.41322312F, 0.45454544F);
		ModelData modelData2 = new ModelData();

		for (Entry<String, ModelPartData> entry : modelData.getRoot().getChildren()) {
			String string = (String)entry.getKey();
			ModelPartData modelPartData = (ModelPartData)entry.getValue();

			UnaryOperator<ModelTransform> unaryOperator4 = switch (string) {
				case "head" -> unaryOperator;
				case "body" -> unaryOperator2;
				default -> unaryOperator3;
			};
			modelData2.getRoot().addChild(string, modelPartData.applyTransformer(unaryOperator4));
		}

		return modelData2;
	}

	public void setAngles(LlamaEntityRenderState llamaEntityRenderState) {
		super.setAngles(llamaEntityRenderState);
		this.head.pitch = llamaEntityRenderState.pitch * (float) (Math.PI / 180.0);
		this.head.yaw = llamaEntityRenderState.yawDegrees * (float) (Math.PI / 180.0);
		float f = llamaEntityRenderState.limbAmplitudeMultiplier;
		float g = llamaEntityRenderState.limbFrequency;
		this.rightHindLeg.pitch = MathHelper.cos(g * 0.6662F) * 1.4F * f;
		this.leftHindLeg.pitch = MathHelper.cos(g * 0.6662F + (float) Math.PI) * 1.4F * f;
		this.rightFrontLeg.pitch = MathHelper.cos(g * 0.6662F + (float) Math.PI) * 1.4F * f;
		this.leftFrontLeg.pitch = MathHelper.cos(g * 0.6662F) * 1.4F * f;
		this.rightChest.visible = llamaEntityRenderState.hasChest;
		this.leftChest.visible = llamaEntityRenderState.hasChest;
	}
}
