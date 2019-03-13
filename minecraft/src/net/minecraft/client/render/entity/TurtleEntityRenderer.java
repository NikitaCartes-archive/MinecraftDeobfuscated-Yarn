package net.minecraft.client.render.entity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TurtleEntityRenderer extends MobEntityRenderer<TurtleEntity, EntityModelTurtle<TurtleEntity>> {
	private static final Identifier field_4798 = new Identifier("textures/entity/turtle/big_sea_turtle.png");

	public TurtleEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new EntityModelTurtle<>(0.0F), 0.7F);
	}

	public void method_4138(TurtleEntity turtleEntity, double d, double e, double f, float g, float h) {
		if (turtleEntity.isChild()) {
			this.field_4673 *= 0.5F;
		}

		super.method_4072(turtleEntity, d, e, f, g, h);
	}

	@Nullable
	protected Identifier method_4139(TurtleEntity turtleEntity) {
		return field_4798;
	}
}
