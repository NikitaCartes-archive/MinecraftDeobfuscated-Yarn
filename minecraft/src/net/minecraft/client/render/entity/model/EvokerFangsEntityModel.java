package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.state.EvokerFangsEntityRenderState;
import net.minecraft.util.math.MathHelper;

/**
 * Represents the model of an evoker-fangs-like entity.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value #BASE}</td><td>{@linkplain #root Root part}</td><td>{@link #base}</td>
 * </tr>
 * <tr>
 *   <td>{@value #UPPER_JAW}</td><td>{@linkplain #root Root part}</td><td>{@link #upperJaw}</td>
 * </tr>
 * <tr>
 *   <td>{@value #LOWER_JAW}</td><td>{@linkplain #root Root part}</td><td>{@link #lowerJaw}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(EnvType.CLIENT)
public class EvokerFangsEntityModel extends EntityModel<EvokerFangsEntityRenderState> {
	/**
	 * The key of the base model part, whose value is {@value}.
	 */
	private static final String BASE = "base";
	/**
	 * The key of the upper jaw model part, whose value is {@value}.
	 */
	private static final String UPPER_JAW = "upper_jaw";
	/**
	 * The key of the lower jaw model part, whose value is {@value}.
	 */
	private static final String LOWER_JAW = "lower_jaw";
	private final ModelPart base;
	private final ModelPart upperJaw;
	private final ModelPart lowerJaw;

	public EvokerFangsEntityModel(ModelPart modelPart) {
		super(modelPart);
		this.base = modelPart.getChild("base");
		this.upperJaw = this.base.getChild("upper_jaw");
		this.lowerJaw = this.base.getChild("lower_jaw");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(
			"base", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, 0.0F, 0.0F, 10.0F, 12.0F, 10.0F), ModelTransform.pivot(-5.0F, 24.0F, -5.0F)
		);
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(40, 0).cuboid(0.0F, 0.0F, 0.0F, 4.0F, 14.0F, 8.0F);
		modelPartData2.addChild("upper_jaw", modelPartBuilder, ModelTransform.of(6.5F, 0.0F, 1.0F, 0.0F, 0.0F, 2.042035F));
		modelPartData2.addChild("lower_jaw", modelPartBuilder, ModelTransform.of(3.5F, 0.0F, 9.0F, 0.0F, (float) Math.PI, 4.2411504F));
		return TexturedModelData.of(modelData, 64, 32);
	}

	public void setAngles(EvokerFangsEntityRenderState evokerFangsEntityRenderState) {
		super.setAngles(evokerFangsEntityRenderState);
		float f = evokerFangsEntityRenderState.animationProgress;
		float g = Math.min(f * 2.0F, 1.0F);
		g = 1.0F - g * g * g;
		this.upperJaw.roll = (float) Math.PI - g * 0.35F * (float) Math.PI;
		this.lowerJaw.roll = (float) Math.PI + g * 0.35F * (float) Math.PI;
		this.base.pivotY = this.base.pivotY - (f + MathHelper.sin(f * 2.7F)) * 7.2F;
		float h = 1.0F;
		if (f > 0.9F) {
			h *= (1.0F - f) / 0.1F;
		}

		this.root.pivotY = 24.0F - 20.0F * h;
		this.root.xScale = h;
		this.root.yScale = h;
		this.root.zScale = h;
	}
}
