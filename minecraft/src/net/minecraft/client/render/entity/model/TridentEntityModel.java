package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TridentEntityModel extends Model {
	public static final Identifier TEXTURE = new Identifier("textures/entity/trident.png");
	private final ModelPart field_3593;

	public TridentEntityModel() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		this.field_3593 = new ModelPart(this, 0, 0);
		this.field_3593.addCuboid(-0.5F, -4.0F, -0.5F, 1, 31, 1, 0.0F);
		ModelPart modelPart = new ModelPart(this, 4, 0);
		modelPart.addCuboid(-1.5F, 0.0F, -0.5F, 3, 2, 1);
		this.field_3593.addChild(modelPart);
		ModelPart modelPart2 = new ModelPart(this, 4, 3);
		modelPart2.addCuboid(-2.5F, -3.0F, -0.5F, 1, 4, 1);
		this.field_3593.addChild(modelPart2);
		ModelPart modelPart3 = new ModelPart(this, 4, 3);
		modelPart3.mirror = true;
		modelPart3.addCuboid(1.5F, -3.0F, -0.5F, 1, 4, 1);
		this.field_3593.addChild(modelPart3);
	}

	public void renderItem() {
		this.field_3593.render(0.0625F);
	}
}
