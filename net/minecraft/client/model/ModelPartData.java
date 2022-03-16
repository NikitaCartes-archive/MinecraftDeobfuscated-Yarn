/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelCuboidData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelTransform;

@Environment(value=EnvType.CLIENT)
public class ModelPartData {
    private final List<ModelCuboidData> cuboidData;
    private final ModelTransform rotationData;
    private final Map<String, ModelPartData> children = Maps.newHashMap();

    ModelPartData(List<ModelCuboidData> cuboidData, ModelTransform rotationData) {
        this.cuboidData = cuboidData;
        this.rotationData = rotationData;
    }

    public ModelPartData addChild(String name, ModelPartBuilder builder, ModelTransform rotationData) {
        ModelPartData modelPartData = new ModelPartData(builder.build(), rotationData);
        ModelPartData modelPartData2 = this.children.put(name, modelPartData);
        if (modelPartData2 != null) {
            modelPartData.children.putAll(modelPartData2.children);
        }
        return modelPartData;
    }

    public ModelPart createPart(int textureWidth, int textureHeight) {
        Object2ObjectArrayMap object2ObjectArrayMap = this.children.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> ((ModelPartData)entry.getValue()).createPart(textureWidth, textureHeight), (modelPart, modelPart2) -> modelPart, Object2ObjectArrayMap::new));
        List list = this.cuboidData.stream().map(modelCuboidData -> modelCuboidData.createCuboid(textureWidth, textureHeight)).collect(ImmutableList.toImmutableList());
        ModelPart modelPart3 = new ModelPart(list, object2ObjectArrayMap);
        modelPart3.setDefaultTransform(this.rotationData);
        modelPart3.setTransform(this.rotationData);
        return modelPart3;
    }

    public ModelPartData getChild(String name) {
        return this.children.get(name);
    }
}

