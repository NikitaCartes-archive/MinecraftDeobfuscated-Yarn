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
	public final Direction field_4225;
	public final int tintIndex;
	public final String texture;
	public final ModelElementTexture field_4227;

	public ModelElementFace(@Nullable Direction direction, int i, String string, ModelElementTexture modelElementTexture) {
		this.field_4225 = direction;
		this.tintIndex = i;
		this.texture = string;
		this.field_4227 = modelElementTexture;
	}

	@Environment(EnvType.CLIENT)
	static class class_784 implements JsonDeserializer<ModelElementFace> {
		public ModelElementFace method_3397(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Direction direction = this.method_3398(jsonObject);
			int i = this.method_3400(jsonObject);
			String string = this.method_3399(jsonObject);
			ModelElementTexture modelElementTexture = jsonDeserializationContext.deserialize(jsonObject, ModelElementTexture.class);
			return new ModelElementFace(direction, i, string, modelElementTexture);
		}

		protected int method_3400(JsonObject jsonObject) {
			return JsonHelper.getInt(jsonObject, "tintindex", -1);
		}

		private String method_3399(JsonObject jsonObject) {
			return JsonHelper.getString(jsonObject, "texture");
		}

		@Nullable
		private Direction method_3398(JsonObject jsonObject) {
			String string = JsonHelper.getString(jsonObject, "cullface", "");
			return Direction.byName(string);
		}
	}
}
