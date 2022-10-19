package net.minecraft.resource.metadata;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;

public class PackResourceMetadataReader implements ResourceMetadataSerializer<PackResourceMetadata> {
	public PackResourceMetadata fromJson(JsonObject jsonObject) {
		Text text = Text.Serializer.fromJson(jsonObject.get("description"));
		if (text == null) {
			throw new JsonParseException("Invalid/missing description!");
		} else {
			int i = JsonHelper.getInt(jsonObject, "pack_format");
			return new PackResourceMetadata(text, i);
		}
	}

	public JsonObject toJson(PackResourceMetadata packResourceMetadata) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("description", Text.Serializer.toJsonTree(packResourceMetadata.getDescription()));
		jsonObject.addProperty("pack_format", packResourceMetadata.getPackFormat());
		return jsonObject;
	}

	@Override
	public String getKey() {
		return "pack";
	}
}
