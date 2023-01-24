/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.client;

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
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.util.Identifier;

public class Model {
    private final Optional<Identifier> parent;
    private final Set<TextureKey> requiredTextures;
    private final Optional<String> variant;

    public Model(Optional<Identifier> parent, Optional<String> variant, TextureKey ... requiredTextureKeys) {
        this.parent = parent;
        this.variant = variant;
        this.requiredTextures = ImmutableSet.copyOf(requiredTextureKeys);
    }

    public Identifier upload(Block block, TextureMap textures, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector) {
        return this.upload(ModelIds.getBlockSubModelId(block, this.variant.orElse("")), textures, modelCollector);
    }

    public Identifier upload(Block block, String suffix, TextureMap textures, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector) {
        return this.upload(ModelIds.getBlockSubModelId(block, suffix + this.variant.orElse("")), textures, modelCollector);
    }

    public Identifier uploadWithoutVariant(Block block, String suffix, TextureMap textures, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector) {
        return this.upload(ModelIds.getBlockSubModelId(block, suffix), textures, modelCollector);
    }

    public Identifier upload(Identifier id, TextureMap textures, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector) {
        return this.upload(id, textures, modelCollector, this::createJson);
    }

    public Identifier upload(Identifier id, TextureMap textures, BiConsumer<Identifier, Supplier<JsonElement>> modelCollector, JsonFactory jsonFactory) {
        Map<TextureKey, Identifier> map = this.createTextureMap(textures);
        modelCollector.accept(id, () -> jsonFactory.create(id, map));
        return id;
    }

    public JsonObject createJson(Identifier id, Map<TextureKey, Identifier> textures) {
        JsonObject jsonObject = new JsonObject();
        this.parent.ifPresent(identifier -> jsonObject.addProperty("parent", identifier.toString()));
        if (!textures.isEmpty()) {
            JsonObject jsonObject2 = new JsonObject();
            textures.forEach((textureKey, texture) -> jsonObject2.addProperty(textureKey.getName(), texture.toString()));
            jsonObject.add("textures", jsonObject2);
        }
        return jsonObject;
    }

    private Map<TextureKey, Identifier> createTextureMap(TextureMap textures) {
        return Streams.concat(this.requiredTextures.stream(), textures.getInherited()).collect(ImmutableMap.toImmutableMap(Function.identity(), textures::getTexture));
    }

    public static interface JsonFactory {
        public JsonObject create(Identifier var1, Map<TextureKey, Identifier> var2);
    }
}

