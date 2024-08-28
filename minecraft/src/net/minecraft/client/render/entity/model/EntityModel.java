package net.minecraft.client.render.entity.model;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.util.Identifier;

/**
 * Represents the model of an {@linkplain Entity}.
 * 
 * <p>An instance of an entity model will not define the model data,
 * but will animate it.
 * Model data may be defined in a static method, most of the children classes will
 * have a {@code getTexturedModelData} method.
 * The model data is usually passed in the constructor of the entity model.
 * 
 * <p>Some children classes contain a model parts table, those tables contains
 * the model parts for the base model. In a modded context the tables may be
 * inaccurate. Model parts with an associated field are required.
 */
@Environment(EnvType.CLIENT)
public abstract class EntityModel<T extends EntityRenderState> extends Model {
	public static final float field_52908 = -1.501F;

	protected EntityModel(ModelPart root) {
		this(root, RenderLayer::getEntityCutoutNoCull);
	}

	protected EntityModel(ModelPart modelPart, Function<Identifier, RenderLayer> function) {
		super(modelPart, function);
	}

	public void setAngles(T state) {
		this.resetTransforms();
	}
}
