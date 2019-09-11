package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;

@Environment(EnvType.CLIENT)
public class BedEntityModel extends Model {
	private final ModelPart part1;
	private final ModelPart part2;
	private final ModelPart[] legs = new ModelPart[4];

	public BedEntityModel() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.part1 = new ModelPart(this, 0, 0);
		this.part1.addCuboid(0.0F, 0.0F, 0.0F, 16, 16, 6, 0.0F);
		this.part2 = new ModelPart(this, 0, 22);
		this.part2.addCuboid(0.0F, 0.0F, 0.0F, 16, 16, 6, 0.0F);
		this.legs[0] = new ModelPart(this, 50, 0);
		this.legs[1] = new ModelPart(this, 50, 6);
		this.legs[2] = new ModelPart(this, 50, 12);
		this.legs[3] = new ModelPart(this, 50, 18);
		this.legs[0].addCuboid(0.0F, 6.0F, -16.0F, 3, 3, 3);
		this.legs[1].addCuboid(0.0F, 6.0F, 0.0F, 3, 3, 3);
		this.legs[2].addCuboid(-16.0F, 6.0F, -16.0F, 3, 3, 3);
		this.legs[3].addCuboid(-16.0F, 6.0F, 0.0F, 3, 3, 3);
		this.legs[0].pitch = (float) (Math.PI / 2);
		this.legs[1].pitch = (float) (Math.PI / 2);
		this.legs[2].pitch = (float) (Math.PI / 2);
		this.legs[3].pitch = (float) (Math.PI / 2);
		this.legs[0].roll = 0.0F;
		this.legs[1].roll = (float) (Math.PI / 2);
		this.legs[2].roll = (float) (Math.PI * 3.0 / 2.0);
		this.legs[3].roll = (float) Math.PI;
	}

	public void render() {
		this.part1.render(0.0625F);
		this.part2.render(0.0625F);
		this.legs[0].render(0.0625F);
		this.legs[1].render(0.0625F);
		this.legs[2].render(0.0625F);
		this.legs[3].render(0.0625F);
	}

	public void setVisible(boolean bl) {
		this.part1.visible = bl;
		this.part2.visible = !bl;
		this.legs[0].visible = !bl;
		this.legs[1].visible = bl;
		this.legs[2].visible = !bl;
		this.legs[3].visible = bl;
	}
}
