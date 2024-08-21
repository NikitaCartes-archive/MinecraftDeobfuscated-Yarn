package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.state.EndCrystalEntityRenderState;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class EndCrystalEntityModel extends EntityModel<EndCrystalEntityRenderState> {
	private static final String OUTER_GLASS = "outer_glass";
	private static final String INNER_GLASS = "inner_glass";
	private static final String BASE = "base";
	private static final float field_52906 = (float)Math.sin(Math.PI / 4);
	private final ModelPart root;
	public final ModelPart base;
	public final ModelPart outerGlass;
	public final ModelPart innerGlass;
	public final ModelPart cube;

	public EndCrystalEntityModel(ModelPart root) {
		this.root = root;
		this.base = root.getChild("base");
		this.outerGlass = root.getChild("outer_glass");
		this.innerGlass = this.outerGlass.getChild("inner_glass");
		this.cube = this.innerGlass.getChild(EntityModelPartNames.CUBE);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		float f = 0.875F;
		ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
		ModelPartData modelPartData2 = modelPartData.addChild("outer_glass", modelPartBuilder, ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		ModelPartData modelPartData3 = modelPartData2.addChild("inner_glass", modelPartBuilder, ModelTransform.NONE.withScale(0.875F));
		modelPartData3.addChild(
			EntityModelPartNames.CUBE, ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), ModelTransform.NONE.withScale(0.765625F)
		);
		modelPartData.addChild("base", ModelPartBuilder.create().uv(0, 16).cuboid(-6.0F, 0.0F, -6.0F, 12.0F, 4.0F, 12.0F), ModelTransform.NONE);
		return TexturedModelData.of(modelData, 64, 32);
	}

	public void setAngles(EndCrystalEntityRenderState endCrystalEntityRenderState) {
		this.root.traverse().forEach(ModelPart::resetTransform);
		this.base.visible = endCrystalEntityRenderState.baseVisible;
		float f = endCrystalEntityRenderState.age * 3.0F;
		float g = EndCrystalEntityRenderer.getYOffset(endCrystalEntityRenderState.age) * 16.0F;
		this.outerGlass.pivotY += g / 2.0F;
		this.outerGlass.method_62132(RotationAxis.POSITIVE_Y.rotationDegrees(f).rotateAxis((float) (Math.PI / 3), field_52906, 0.0F, field_52906));
		this.innerGlass.method_62132(new Quaternionf().setAngleAxis((float) (Math.PI / 3), field_52906, 0.0F, field_52906).rotateY(f * (float) (Math.PI / 180.0)));
		this.cube.method_62132(new Quaternionf().setAngleAxis((float) (Math.PI / 3), field_52906, 0.0F, field_52906).rotateY(f * (float) (Math.PI / 180.0)));
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}
}
