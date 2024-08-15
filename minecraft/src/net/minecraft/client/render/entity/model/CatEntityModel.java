package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.state.CatEntityRenderState;

/**
 * Represents the model of a {@linkplain CatEntity}.
 * 
 * <p>Inherits the {@link OcelotEntityModel}.
 */
@Environment(EnvType.CLIENT)
public class CatEntityModel extends FelineEntityModel<CatEntityRenderState> {
	public static final ModelTransformer CAT_TRANSFORMER = ModelTransformer.scaling(0.8F);

	public CatEntityModel(ModelPart modelPart) {
		super(modelPart);
	}
}
