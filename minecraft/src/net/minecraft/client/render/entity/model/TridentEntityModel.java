package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TridentEntityModel extends Model {
	public static final Identifier TEXTURE = new Identifier("textures/entity/trident.png");
	private final Cuboid field_3593;

	public TridentEntityModel() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		this.field_3593 = new Cuboid(this, 0, 0);
		this.field_3593.addBox(-0.5F, -4.0F, -0.5F, 1, 31, 1, 0.0F);
		Cuboid cuboid = new Cuboid(this, 4, 0);
		cuboid.addBox(-1.5F, 0.0F, -0.5F, 3, 2, 1);
		this.field_3593.addChild(cuboid);
		Cuboid cuboid2 = new Cuboid(this, 4, 3);
		cuboid2.addBox(-2.5F, -3.0F, -0.5F, 1, 4, 1);
		this.field_3593.addChild(cuboid2);
		Cuboid cuboid3 = new Cuboid(this, 4, 3);
		cuboid3.mirror = true;
		cuboid3.addBox(1.5F, -3.0F, -0.5F, 1, 4, 1);
		this.field_3593.addChild(cuboid3);
	}

	public void renderItem() {
		this.field_3593.render(0.0625F);
	}
}
