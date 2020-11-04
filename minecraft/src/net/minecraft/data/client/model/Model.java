package net.minecraft.data.client.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

public class Model {
	private final Optional<Identifier> parent;
	private final Set<TextureKey> requiredTextures;
	private final Optional<String> variant;

	public Model(Optional<Identifier> parent, Optional<String> variant, TextureKey... requiredTextures) {
		this.parent = parent;
		this.variant = variant;
		this.requiredTextures = ImmutableSet.copyOf(requiredTextures);
	}

	public Identifier upload(Block block, Texture texture, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector) {
		return this.upload(ModelIds.getBlockSubModelId(block, (String)this.variant.orElse("")), texture, modelCollector);
	}

	public Identifier upload(Block block, String suffix, Texture texture, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector) {
		return this.upload(ModelIds.getBlockSubModelId(block, suffix + (String)this.variant.orElse("")), texture, modelCollector);
	}

	public Identifier uploadWithoutVariant(Block block, String suffix, Texture texture, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector) {
		return this.upload(ModelIds.getBlockSubModelId(block, suffix), texture, modelCollector);
	}

	public Identifier upload(Identifier id, Texture texture, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector) {
		Map<TextureKey, Identifier> map = this.createTextureMap(texture);
		modelCollector.accept(id, (Supplier)() -> {
			JsonObject jsonObject = new JsonObject();
			this.parent.ifPresent(identifier -> jsonObject.addProperty("parent", identifier.toString()));
			if (!map.isEmpty()) {
				JsonObject jsonObject2 = new JsonObject();
				map.forEach((textureKey, identifier) -> jsonObject2.addProperty(textureKey.getName(), identifier.toString()));
				jsonObject.add("textures", jsonObject2);
			}

			return jsonObject;
		});
		return id;
	}

	private Map<TextureKey, Identifier> createTextureMap(Texture texture) {
		return (Map<TextureKey, Identifier>)Streams.concat(this.requiredTextures.stream(), texture.getInherited())
			.collect(ImmutableMap.toImmutableMap(Function.identity(), texture::getTexture));
	}
}
