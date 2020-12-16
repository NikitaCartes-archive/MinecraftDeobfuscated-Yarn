/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.RequiredTagList;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.Tag;
import net.minecraft.util.registry.Registry;

public final class FluidTags {
    protected static final RequiredTagList<Fluid> REQUIRED_TAGS = RequiredTagListRegistry.register(Registry.FLUID_KEY, "tags/fluids");
    private static final List<Tag<Fluid>> TAGS = Lists.newArrayList();
    public static final Tag.Identified<Fluid> WATER = FluidTags.register("water");
    public static final Tag.Identified<Fluid> LAVA = FluidTags.register("lava");

    private static Tag.Identified<Fluid> register(String id) {
        Tag.Identified<Fluid> identified = REQUIRED_TAGS.add(id);
        TAGS.add(identified);
        return identified;
    }

    @Deprecated
    public static List<Tag<Fluid>> getTags() {
        return TAGS;
    }
}

