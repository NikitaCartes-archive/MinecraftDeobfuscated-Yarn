/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;

public class FluidTags {
    private static TagContainer<Fluid> container = new TagContainer(identifier -> Optional.empty(), "", false, "");
    private static int latestVersion;
    public static final Tag<Fluid> WATER;
    public static final Tag<Fluid> LAVA;

    public static void setContainer(TagContainer<Fluid> tagContainer) {
        container = tagContainer;
        ++latestVersion;
    }

    public static TagContainer<Fluid> getContainer() {
        return container;
    }

    private static Tag<Fluid> register(String string) {
        return new CachingTag(new Identifier(string));
    }

    static {
        WATER = FluidTags.register("water");
        LAVA = FluidTags.register("lava");
    }

    public static class CachingTag
    extends Tag<Fluid> {
        private int version = -1;
        private Tag<Fluid> delegate;

        public CachingTag(Identifier identifier) {
            super(identifier);
        }

        @Override
        public boolean contains(Fluid fluid) {
            if (this.version != latestVersion) {
                this.delegate = container.getOrCreate(this.getId());
                this.version = latestVersion;
            }
            return this.delegate.contains(fluid);
        }

        @Override
        public Collection<Fluid> values() {
            if (this.version != latestVersion) {
                this.delegate = container.getOrCreate(this.getId());
                this.version = latestVersion;
            }
            return this.delegate.values();
        }

        @Override
        public Collection<Tag.Entry<Fluid>> entries() {
            if (this.version != latestVersion) {
                this.delegate = container.getOrCreate(this.getId());
                this.version = latestVersion;
            }
            return this.delegate.entries();
        }
    }
}

