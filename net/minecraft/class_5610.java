/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5604;
import net.minecraft.class_5606;
import net.minecraft.client.model.ModelPart;

@Environment(value=EnvType.CLIENT)
public class class_5610 {
    private final List<class_5604> field_27728;
    private final class_5603 field_27729;
    private final Map<String, class_5610> field_27730 = Maps.newHashMap();

    class_5610(List<class_5604> list, class_5603 arg) {
        this.field_27728 = list;
        this.field_27729 = arg;
    }

    public class_5610 method_32117(String string, class_5606 arg, class_5603 arg2) {
        class_5610 lv = new class_5610(arg.method_32107(), arg2);
        class_5610 lv2 = this.field_27730.put(string, lv);
        if (lv2 != null) {
            lv.field_27730.putAll(lv2.field_27730);
        }
        return lv;
    }

    public ModelPart method_32112(int i, int j) {
        Object2ObjectArrayMap object2ObjectArrayMap = this.field_27730.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> ((class_5610)entry.getValue()).method_32112(i, j), (modelPart, modelPart2) -> modelPart, Object2ObjectArrayMap::new));
        List list = this.field_27728.stream().map(arg -> arg.method_32093(i, j)).collect(ImmutableList.toImmutableList());
        ModelPart modelPart3 = new ModelPart(list, object2ObjectArrayMap);
        modelPart3.method_32085(this.field_27729);
        return modelPart3;
    }

    public class_5610 method_32116(String string) {
        return this.field_27730.get(string);
    }
}

