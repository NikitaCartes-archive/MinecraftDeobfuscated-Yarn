/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.model.json;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.MultipartUnbakedModel;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.client.render.model.json.WeightedUnbakedModel;
import net.minecraft.state.StateFactory;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ModelVariantMap {
    private final Map<String, WeightedUnbakedModel> variantMap = Maps.newLinkedHashMap();
    private MultipartUnbakedModel multipartModel;

    public static ModelVariantMap deserialize(DeserializationContext deserializationContext, Reader reader) {
        return JsonHelper.deserialize(deserializationContext.gson, reader, ModelVariantMap.class);
    }

    public ModelVariantMap(Map<String, WeightedUnbakedModel> map, MultipartUnbakedModel multipartUnbakedModel) {
        this.multipartModel = multipartUnbakedModel;
        this.variantMap.putAll(map);
    }

    public ModelVariantMap(List<ModelVariantMap> list) {
        ModelVariantMap modelVariantMap = null;
        for (ModelVariantMap modelVariantMap2 : list) {
            if (modelVariantMap2.hasMultipartModel()) {
                this.variantMap.clear();
                modelVariantMap = modelVariantMap2;
            }
            this.variantMap.putAll(modelVariantMap2.variantMap);
        }
        if (modelVariantMap != null) {
            this.multipartModel = modelVariantMap.multipartModel;
        }
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof ModelVariantMap) {
            ModelVariantMap modelVariantMap = (ModelVariantMap)object;
            if (this.variantMap.equals(modelVariantMap.variantMap)) {
                return this.hasMultipartModel() ? this.multipartModel.equals(modelVariantMap.multipartModel) : !modelVariantMap.hasMultipartModel();
            }
        }
        return false;
    }

    public int hashCode() {
        return 31 * this.variantMap.hashCode() + (this.hasMultipartModel() ? this.multipartModel.hashCode() : 0);
    }

    public Map<String, WeightedUnbakedModel> getVariantMap() {
        return this.variantMap;
    }

    public boolean hasMultipartModel() {
        return this.multipartModel != null;
    }

    public MultipartUnbakedModel getMultipartModel() {
        return this.multipartModel;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<ModelVariantMap> {
        public ModelVariantMap method_3428(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Map<String, WeightedUnbakedModel> map = this.deserializeVariants(jsonDeserializationContext, jsonObject);
            MultipartUnbakedModel multipartUnbakedModel = this.deserializeMultipart(jsonDeserializationContext, jsonObject);
            if (map.isEmpty() && (multipartUnbakedModel == null || multipartUnbakedModel.getModels().isEmpty())) {
                throw new JsonParseException("Neither 'variants' nor 'multipart' found");
            }
            return new ModelVariantMap(map, multipartUnbakedModel);
        }

        protected Map<String, WeightedUnbakedModel> deserializeVariants(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
            HashMap<String, WeightedUnbakedModel> map = Maps.newHashMap();
            if (jsonObject.has("variants")) {
                JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "variants");
                for (Map.Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
                    map.put(entry.getKey(), (WeightedUnbakedModel)jsonDeserializationContext.deserialize(entry.getValue(), (Type)((Object)WeightedUnbakedModel.class)));
                }
            }
            return map;
        }

        @Nullable
        protected MultipartUnbakedModel deserializeMultipart(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
            if (!jsonObject.has("multipart")) {
                return null;
            }
            JsonArray jsonArray = JsonHelper.getArray(jsonObject, "multipart");
            return (MultipartUnbakedModel)jsonDeserializationContext.deserialize(jsonArray, (Type)((Object)MultipartUnbakedModel.class));
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.method_3428(jsonElement, type, jsonDeserializationContext);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class DeserializationContext {
        protected final Gson gson = new GsonBuilder().registerTypeAdapter((Type)((Object)ModelVariantMap.class), new Deserializer()).registerTypeAdapter((Type)((Object)ModelVariant.class), new ModelVariant.Deserializer()).registerTypeAdapter((Type)((Object)WeightedUnbakedModel.class), new WeightedUnbakedModel.Deserializer()).registerTypeAdapter((Type)((Object)MultipartUnbakedModel.class), new MultipartUnbakedModel.Deserializer(this)).registerTypeAdapter((Type)((Object)MultipartModelComponent.class), new MultipartModelComponent.Deserializer()).create();
        private StateFactory<Block, BlockState> stateFactory;

        public StateFactory<Block, BlockState> getStateFactory() {
            return this.stateFactory;
        }

        public void setStateFactory(StateFactory<Block, BlockState> stateFactory) {
            this.stateFactory = stateFactory;
        }
    }
}

