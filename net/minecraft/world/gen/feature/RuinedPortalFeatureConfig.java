/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.RuinedPortalFeature;

public class RuinedPortalFeatureConfig
implements FeatureConfig {
    public final RuinedPortalFeature.Type portalType;

    public RuinedPortalFeatureConfig() {
        this(RuinedPortalFeature.Type.STANDARD);
    }

    public RuinedPortalFeatureConfig(RuinedPortalFeature.Type type) {
        this.portalType = type;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic<T>(ops, ops.createMap(ImmutableMap.of(ops.createString("portal_type"), ops.createString(this.portalType.getName()))));
    }

    public static <T> RuinedPortalFeatureConfig deserialize(Dynamic<T> dynamic) {
        RuinedPortalFeature.Type type = RuinedPortalFeature.Type.byName(dynamic.get("portal_type").asString(""));
        return new RuinedPortalFeatureConfig(type);
    }
}

