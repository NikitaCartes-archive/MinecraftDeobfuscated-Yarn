package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.state.ShulkerBulletEntityRenderState;

/**
 * Represents the model of shulker-bullet-like entity.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value #MAIN}</td><td>{@linkplain #root Root part}</td><td>{@link #bullet}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(EnvType.CLIENT)
public class ShulkerBulletEntityModel extends EntityModel<ShulkerBulletEntityRenderState> {
	/**
	 * The key of the main model part, whose value is {@value}.
	 */
	private static final String MAIN = "main";
	private final ModelPart bullet;

	public ShulkerBulletEntityModel(ModelPart modelPart) {
		super(modelPart);
		this.bullet = modelPart.getChild("main");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			"main",
			ModelPartBuilder.create()
				.uv(0, 0)
				.cuboid(-4.0F, -4.0F, -1.0F, 8.0F, 8.0F, 2.0F)
				.uv(0, 10)
				.cuboid(-1.0F, -4.0F, -4.0F, 2.0F, 8.0F, 8.0F)
				.uv(20, 0)
				.cuboid(-4.0F, -1.0F, -4.0F, 8.0F, 2.0F, 8.0F),
			ModelTransform.NONE
		);
		return TexturedModelData.of(modelData, 64, 32);
	}

	public void setAngles(ShulkerBulletEntityRenderState shulkerBulletEntityRenderState) {
		super.setAngles(shulkerBulletEntityRenderState);
		this.bullet.yaw = shulkerBulletEntityRenderState.yaw * (float) (Math.PI / 180.0);
		this.bullet.pitch = shulkerBulletEntityRenderState.pitch * (float) (Math.PI / 180.0);
	}
}
