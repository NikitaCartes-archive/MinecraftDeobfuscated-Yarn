package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;

@Environment(EnvType.CLIENT)
public class BannerBlockEntityModel extends Model {
	private final ModelPart banner;
	private final ModelPart verticalStick;
	private final ModelPart horizontalStick;

	public BannerBlockEntityModel() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.banner = new ModelPart(this, 0, 0);
		this.banner.addCuboid(-10.0F, 0.0F, -2.0F, 20, 40, 1, 0.0F);
		this.verticalStick = new ModelPart(this, 44, 0);
		this.verticalStick.addCuboid(-1.0F, -30.0F, -1.0F, 2, 42, 2, 0.0F);
		this.horizontalStick = new ModelPart(this, 0, 42);
		this.horizontalStick.addCuboid(-10.0F, -32.0F, -1.0F, 20, 2, 2, 0.0F);
	}

	public void render() {
		this.banner.rotationPointY = -32.0F;
		this.banner.render(0.0625F);
		this.verticalStick.render(0.0625F);
		this.horizontalStick.render(0.0625F);
	}

	public ModelPart getVerticalStick() {
		return this.verticalStick;
	}

	public ModelPart getBanner() {
		return this.banner;
	}
}
