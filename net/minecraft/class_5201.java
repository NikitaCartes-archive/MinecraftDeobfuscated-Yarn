/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.OptionalInt;
import net.minecraft.class_5202;
import net.minecraft.util.registry.Registry;

public abstract class class_5201 {
    protected final class_5202<?> field_24145;
    private final OptionalInt field_24146;

    public class_5201(class_5202<?> arg, OptionalInt optionalInt) {
        this.field_24145 = arg;
        this.field_24146 = optionalInt;
    }

    public abstract int method_27378(int var1, int var2);

    public OptionalInt method_27377() {
        return this.field_24146;
    }

    public <T> T method_27380(DynamicOps<T> dynamicOps) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("type"), dynamicOps.createString(Registry.FEATURE_SIZE_TYPE.getId(this.field_24145).toString()));
        this.field_24146.ifPresent(i -> builder.put(dynamicOps.createString("min_clipped_height"), dynamicOps.createInt(i)));
        return new Dynamic<T>(dynamicOps, dynamicOps.createMap(builder.build())).getValue();
    }
}

