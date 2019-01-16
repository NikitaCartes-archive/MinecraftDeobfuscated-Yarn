package net.minecraft.client.render.model.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class ModelElementFace {
	public final Direction cullFace;
	public final int tintIndex;
	public final String textureId;
	public final ModelElementTexture textureData;

	public ModelElementFace(@Nullable Direction direction, int i, String string, ModelElementTexture modelElementTexture) {
		this.cullFace = direction;
		this.tintIndex = i;
		this.textureId = string;
		this.textureData = modelElementTexture;
	}

	@Environment(EnvType.CLIENT)
	public static class Deserializer implements JsonDeserializer<ModelElementFace> {
		protected Deserializer() {
		}

		public ModelElementFace method_3397(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Direction direction = this.deserializeCullFace(jsonObject);
			int i = this.deserializeTintIndex(jsonObject);
			String string = this.deserializeTexture(jsonObject);
			ModelElementTexture modelElementTexture = jsonDeserializationContext.deserialize(jsonObject, ModelElementTexture.class);
			return new ModelElementFace(direction, i, string, modelElementTexture);
		}

		protected int deserializeTintIndex(JsonObject jsonObject) {
			return JsonHelper.getInt(jsonObject, "tintindex", -1);
		}

		private String deserializeTexture(JsonObject jsonObject) {
			return JsonHelper.getString(jsonObject, "texture");
		}

		@Nullable
		private Direction deserializeCullFace(JsonObject jsonObject) {
			String string = JsonHelper.getString(jsonObject, "cullface", "");
			return Direction.byName(string);
		}
	}
}
