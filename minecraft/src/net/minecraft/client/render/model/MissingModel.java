package net.minecraft.client.render.model;

import com.mojang.datafixers.util.Either;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class MissingModel {
	public static final String KEY = "missing";
	public static final Identifier ID = Models.forBuiltin("missing");
	public static final ModelIdentifier MODEL_ID = new ModelIdentifier(ID, "missing");

	public static UnbakedModel create() {
		ModelElementTexture modelElementTexture = new ModelElementTexture(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0);
		Map<Direction, ModelElementFace> map = new EnumMap(Direction.class);

		for (Direction direction : Direction.values()) {
			map.put(direction, new ModelElementFace(direction, 0, MissingSprite.getMissingSpriteId().getPath(), modelElementTexture));
		}

		ModelElement modelElement = new ModelElement(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(16.0F, 16.0F, 16.0F), map);
		JsonUnbakedModel jsonUnbakedModel = new JsonUnbakedModel(
			null,
			List.of(modelElement),
			Map.of("particle", Either.left(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, MissingSprite.getMissingSpriteId()))),
			null,
			null,
			ModelTransformation.NONE,
			List.of()
		);
		jsonUnbakedModel.id = "missingno";
		return jsonUnbakedModel;
	}
}
