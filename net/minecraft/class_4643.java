/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.class_4651;
import net.minecraft.class_4652;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.feature.FeatureConfig;

public class class_4643
implements FeatureConfig {
    public final class_4651 field_21288;
    public final class_4651 field_21289;
    public final List<TreeDecorator> field_21290;
    public final int field_21291;

    protected class_4643(class_4651 arg, class_4651 arg2, List<TreeDecorator> list, int i) {
        this.field_21288 = arg;
        this.field_21289 = arg2;
        this.field_21290 = list;
        this.field_21291 = i;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("trunk_provider"), this.field_21288.serialize(dynamicOps)).put(dynamicOps.createString("leaves_provider"), this.field_21289.serialize(dynamicOps)).put(dynamicOps.createString("decorators"), dynamicOps.createList(this.field_21290.stream().map(treeDecorator -> treeDecorator.serialize(dynamicOps)))).put(dynamicOps.createString("base_height"), dynamicOps.createInt(this.field_21291));
        return new Dynamic<T>(dynamicOps, dynamicOps.createMap(builder.build()));
    }

    public static <T> class_4643 method_23444(Dynamic<T> dynamic2) {
        class_4652<T> lv = Registry.BLOCK_STATE_PROVIDER_TYPE.get(new Identifier(dynamic2.get("trunk_provider").get("type").asString().orElseThrow(RuntimeException::new)));
        class_4652<T> lv2 = Registry.BLOCK_STATE_PROVIDER_TYPE.get(new Identifier(dynamic2.get("leaves_provider").get("type").asString().orElseThrow(RuntimeException::new)));
        return new class_4643((class_4651)lv.method_23456(dynamic2.get("trunk_provider").orElseEmptyMap()), (class_4651)lv2.method_23456(dynamic2.get("leaves_provider").orElseEmptyMap()), dynamic2.get("decorators").asList(dynamic -> Registry.TREE_DECORATOR_TYPE.get(new Identifier(dynamic.get("type").asString().orElseThrow(RuntimeException::new))).method_23472((Dynamic<?>)dynamic)), dynamic2.get("base_height").asInt(0));
    }

    public static class class_4644 {
        public final class_4651 field_21292;
        public final class_4651 field_21293;
        private List<TreeDecorator> field_21294 = Lists.newArrayList();
        private int field_21295 = 0;

        public class_4644(class_4651 arg, class_4651 arg2) {
            this.field_21292 = arg;
            this.field_21293 = arg2;
        }

        public class_4644 method_23446(int i) {
            this.field_21295 = i;
            return this;
        }

        public class_4643 method_23445() {
            return new class_4643(this.field_21292, this.field_21293, this.field_21294, this.field_21295);
        }
    }
}

