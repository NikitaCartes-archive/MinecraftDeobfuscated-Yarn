/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.poi.PointOfInterestType;

public class PointOfInterestTypeTags {
    public static final TagKey<PointOfInterestType> ACQUIRABLE_JOB_SITE = PointOfInterestTypeTags.of("acquirable_job_site");
    public static final TagKey<PointOfInterestType> VILLAGE = PointOfInterestTypeTags.of("village");
    public static final TagKey<PointOfInterestType> BEE_HOME = PointOfInterestTypeTags.of("bee_home");

    private PointOfInterestTypeTags() {
    }

    private static TagKey<PointOfInterestType> of(String id) {
        return TagKey.of(Registry.POINT_OF_INTEREST_TYPE_KEY, new Identifier(id));
    }
}

