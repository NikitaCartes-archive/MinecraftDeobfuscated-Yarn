package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class HoglinEntityModel extends CompositeEntityModel<HoglinEntity> {
	private final ModelPart field_22227;
	private final ModelPart field_22228;
	private final ModelPart field_22229;
	private final ModelPart field_22230;
	private final ModelPart field_22231;
	private final ModelPart field_22232;
	private final ModelPart field_22233;
	private final ModelPart field_22234;

	public HoglinEntityModel() {
		this.textureWidth = 128;
		this.textureHeight = 128;
		this.field_22230 = new ModelPart(this);
		this.field_22230.setPivot(0.0F, 7.0F, 0.0F);
		this.field_22230.setTextureOffset(1, 1).addCuboid(-8.0F, -7.0F, -13.0F, 16.0F, 14.0F, 26.0F);
		ModelPart modelPart = new ModelPart(this);
		modelPart.setPivot(0.0F, -14.0F, -7.0F);
		modelPart.setTextureOffset(5, 67).addCuboid(0.0F, 0.0F, -9.0F, 0.0F, 10.0F, 19.0F, 0.001F);
		this.field_22230.addChild(modelPart);
		this.field_22227 = new ModelPart(this);
		this.field_22227.setPivot(0.0F, 2.0F, -12.0F);
		this.field_22227.setTextureOffset(1, 42).addCuboid(-7.0F, -3.0F, -19.0F, 14.0F, 6.0F, 19.0F);
		this.field_22228 = new ModelPart(this);
		this.field_22228.setPivot(-6.0F, -2.0F, -3.0F);
		this.field_22228.setTextureOffset(4, 16).addCuboid(-6.0F, -1.0F, -2.0F, 6.0F, 1.0F, 4.0F);
		this.field_22228.roll = (float) (-Math.PI * 2.0 / 9.0);
		this.field_22227.addChild(this.field_22228);
		this.field_22229 = new ModelPart(this);
		this.field_22229.setPivot(6.0F, -2.0F, -3.0F);
		this.field_22229.setTextureOffset(4, 21).addCuboid(0.0F, -1.0F, -2.0F, 6.0F, 1.0F, 4.0F);
		this.field_22229.roll = (float) (Math.PI * 2.0 / 9.0);
		this.field_22227.addChild(this.field_22229);
		ModelPart modelPart2 = new ModelPart(this);
		modelPart2.setPivot(-7.0F, 2.0F, -12.0F);
		modelPart2.setTextureOffset(6, 45).addCuboid(-1.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F);
		this.field_22227.addChild(modelPart2);
		ModelPart modelPart3 = new ModelPart(this);
		modelPart3.setPivot(7.0F, 2.0F, -12.0F);
		modelPart3.setTextureOffset(6, 45).addCuboid(-1.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F);
		this.field_22227.addChild(modelPart3);
		this.field_22227.pitch = 0.87266463F;
		int i = 14;
		int j = 11;
		this.field_22231 = new ModelPart(this);
		this.field_22231.setPivot(-4.0F, 10.0F, -8.5F);
		this.field_22231.setTextureOffset(46, 75).addCuboid(-3.0F, 0.0F, -3.0F, 6.0F, 14.0F, 6.0F);
		this.field_22232 = new ModelPart(this);
		this.field_22232.setPivot(4.0F, 10.0F, -8.5F);
		this.field_22232.setTextureOffset(71, 75).addCuboid(-3.0F, 0.0F, -3.0F, 6.0F, 14.0F, 6.0F);
		this.field_22233 = new ModelPart(this);
		this.field_22233.setPivot(-5.0F, 13.0F, 10.0F);
		this.field_22233.setTextureOffset(51, 43).addCuboid(-2.5F, 0.0F, -2.5F, 5.0F, 11.0F, 5.0F);
		this.field_22234 = new ModelPart(this);
		this.field_22234.setPivot(5.0F, 13.0F, 10.0F);
		this.field_22234.setTextureOffset(72, 43).addCuboid(-2.5F, 0.0F, -2.5F, 5.0F, 11.0F, 5.0F);
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.field_22230, this.field_22227, this.field_22231, this.field_22232, this.field_22233, this.field_22234);
	}

	public void setAngles(HoglinEntity hoglinEntity, float f, float g, float h, float i, float j) {
		this.field_22228.roll = (float) (-Math.PI * 2.0 / 9.0) - g * 2.5F * MathHelper.sin(f * 3.0F);
		this.field_22229.roll = (float) (Math.PI * 2.0 / 9.0) + g * 2.5F * MathHelper.sin(f * 3.0F);
		this.field_22227.yaw = i * (float) (Math.PI / 180.0);
		this.field_22231.pitch = MathHelper.cos(f * 1.5F) * 4.4F * g;
		this.field_22232.pitch = MathHelper.cos(f * 1.5F + (float) Math.PI) * 4.4F * g;
		this.field_22233.pitch = MathHelper.cos(f * 1.5F + (float) Math.PI) * 4.4F * g;
		this.field_22234.pitch = MathHelper.cos(f * 1.5F) * 4.4F * g;
	}
}
