/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.GlobalTagAccessor;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;

public class FluidTags {
    private static final GlobalTagAccessor<Fluid> ACCESSOR = new GlobalTagAccessor();
    public static final Tag.Identified<Fluid> WATER = FluidTags.register("water");
    public static final Tag.Identified<Fluid> LAVA = FluidTags.register("lava");

    private static Tag.Identified<Fluid> register(String id) {
        return ACCESSOR.get(id);
    }

    public static void setContainer(TagContainer<Fluid> container) {
        ACCESSOR.setContainer(container);
    }

    @Environment(value=EnvType.CLIENT)
    public static void markReady() {
        ACCESSOR.markReady();
    }

    public static TagContainer<Fluid> getContainer() {
        return ACCESSOR.getContainer();
    }

    public static List<GlobalTagAccessor.CachedTag<Fluid>> method_29897() {
        return ACCESSOR.method_29902();
    }

    public static Set<Identifier> method_29216(TagContainer<Fluid> tagContainer) {
        return ACCESSOR.method_29224(tagContainer);
    }
}

