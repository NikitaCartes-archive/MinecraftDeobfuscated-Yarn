package net.minecraft.client.render.entity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.TurtleEntityModel;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TurtleEntityRenderer extends MobEntityRenderer<TurtleEntity, TurtleEntityModel<TurtleEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/turtle/big_sea_turtle.png");

	public TurtleEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new TurtleEntityModel<>(0.0F), 0.7F);
	}

	public void render(TurtleEntity turtleEntity, double d, double e, double f, float g, float h) {
		if (turtleEntity.isBaby()) {
			this.field_4673 *= 0.5F;
		}

		super.render(turtleEntity, d, e, f, g, h);
	}

	@Nullable
	protected Identifier getTexture(TurtleEntity turtleEntity) {
		return SKIN;
	}
}
