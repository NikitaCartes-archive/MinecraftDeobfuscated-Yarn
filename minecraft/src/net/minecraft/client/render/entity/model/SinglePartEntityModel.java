package net.minecraft.client.render.entity.model;

import java.util.Optional;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class SinglePartEntityModel<E extends Entity> extends EntityModel<E> {
	public SinglePartEntityModel() {
		this(RenderLayer::getEntityCutoutNoCull);
	}

	public SinglePartEntityModel(Function<Identifier, RenderLayer> function) {
		super(function);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		this.getPart().render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}

	public abstract ModelPart getPart();

	public Optional<ModelPart> getChild(String name) {
		return this.getPart().traverse().filter(part -> part.hasChild(name)).findFirst().map(part -> part.getChild(name));
	}
}
