/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.class_4651;
import net.minecraft.class_4652;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.FeatureConfig;

public class class_4634
implements FeatureConfig {
    public final class_4651 field_21229;

    public class_4634(class_4651 arg) {
        this.field_21229 = arg;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("state_provider"), this.field_21229.serialize(dynamicOps));
        return new Dynamic<T>(dynamicOps, dynamicOps.createMap(builder.build()));
    }

    public static <T> class_4634 method_23406(Dynamic<T> dynamic) {
        class_4652<T> lv = Registry.BLOCK_STATE_PROVIDER_TYPE.get(new Identifier(dynamic.get("state_provider").get("type").asString().orElseThrow(RuntimeException::new)));
        return new class_4634((class_4651)lv.method_23456(dynamic.get("state_provider").orElseEmptyMap()));
    }
}

