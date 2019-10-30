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

	public ModelElementFace(@Nullable Direction cullFace, int tintIndex, String textureId, ModelElementTexture textureData) {
		this.cullFace = cullFace;
		this.tintIndex = tintIndex;
		this.textureId = textureId;
		this.textureData = textureData;
	}

	@Environment(EnvType.CLIENT)
	public static class Deserializer implements JsonDeserializer<ModelElementFace> {
		protected Deserializer() {
		}

		public ModelElementFace method_3397(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonObject = element.getAsJsonObject();
			Direction direction = this.deserializeCullFace(jsonObject);
			int i = this.deserializeTintIndex(jsonObject);
			String string = this.deserializeTexture(jsonObject);
			ModelElementTexture modelElementTexture = context.deserialize(jsonObject, ModelElementTexture.class);
			return new ModelElementFace(direction, i, string, modelElementTexture);
		}

		protected int deserializeTintIndex(JsonObject object) {
			return JsonHelper.getInt(object, "tintindex", -1);
		}

		private String deserializeTexture(JsonObject object) {
			return JsonHelper.getString(object, "texture");
		}

		@Nullable
		private Direction deserializeCullFace(JsonObject object) {
			String string = JsonHelper.getString(object, "cullface", "");
			return Direction.byName(string);
		}
	}
}
