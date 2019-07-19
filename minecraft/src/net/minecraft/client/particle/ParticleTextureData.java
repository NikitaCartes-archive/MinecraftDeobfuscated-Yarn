package net.minecraft.client.particle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

@Environment(EnvType.CLIENT)
public class ParticleTextureData {
	@Nullable
	private final List<Identifier> textureList;

	private ParticleTextureData(@Nullable List<Identifier> textureList) {
		this.textureList = textureList;
	}

	@Nullable
	public List<Identifier> getTextureList() {
		return this.textureList;
	}

	public static ParticleTextureData load(JsonObject jsonObject) {
		JsonArray jsonArray = JsonHelper.getArray(jsonObject, "textures", null);
		List<Identifier> list;
		if (jsonArray != null) {
			list = (List<Identifier>)Streams.stream(jsonArray)
				.map(jsonElement -> JsonHelper.asString(jsonElement, "texture"))
				.map(Identifier::new)
				.collect(ImmutableList.toImmutableList());
		} else {
			list = null;
		}

		return new ParticleTextureData(list);
	}
}
