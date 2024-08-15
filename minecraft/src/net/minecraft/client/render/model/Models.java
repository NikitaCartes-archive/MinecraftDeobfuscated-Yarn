package net.minecraft.client.render.model;

import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class Models {
	public static final Identifier GENERATED = forBuiltin("generated");
	public static final Identifier ENTITY = forBuiltin("entity");
	public static final UnbakedModel GENERATION_MARKER = createMarkerModel("generation marker", JsonUnbakedModel.GuiLight.ITEM);
	public static final UnbakedModel BLOCK_ENTITY_MARKER = createMarkerModel("block entity marker", JsonUnbakedModel.GuiLight.BLOCK);

	public static Identifier forBuiltin(String path) {
		return Identifier.ofVanilla("builtin/" + path);
	}

	private static UnbakedModel createMarkerModel(String id, JsonUnbakedModel.GuiLight guiLight) {
		JsonUnbakedModel jsonUnbakedModel = new JsonUnbakedModel(null, List.of(), Map.of(), null, guiLight, ModelTransformation.NONE, List.of());
		jsonUnbakedModel.id = id;
		return jsonUnbakedModel;
	}
}
