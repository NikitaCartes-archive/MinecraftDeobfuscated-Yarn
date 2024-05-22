package net.minecraft.client.particle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

@Environment(EnvType.CLIENT)
public class ParticleTextureData {
	private final List<Identifier> textureList;

	private ParticleTextureData(List<Identifier> textureList) {
		this.textureList = textureList;
	}

	public List<Identifier> getTextureList() {
		return this.textureList;
	}

	public static ParticleTextureData load(JsonObject json) {
		JsonArray jsonArray = JsonHelper.getArray(json, "textures", null);
		if (jsonArray == null) {
			return new ParticleTextureData(List.of());
		} else {
			List<Identifier> list = (List<Identifier>)Streams.stream(jsonArray)
				.map(texture -> JsonHelper.asString(texture, "texture"))
				.map(Identifier::of)
				.collect(ImmutableList.toImmutableList());
			return new ParticleTextureData(list);
		}
	}
}
