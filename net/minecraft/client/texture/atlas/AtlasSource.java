/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture.atlas;

import java.util.function.Predicate;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.client.texture.atlas.AtlasSourceType;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public interface AtlasSource {
    public static final ResourceFinder RESOURCE_FINDER = new ResourceFinder("textures", ".png");

    public void load(ResourceManager var1, SpriteRegions var2);

    public AtlasSourceType getType();

    @Environment(value=EnvType.CLIENT)
    public static interface SpriteRegion
    extends Supplier<SpriteContents> {
        default public void close() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface SpriteRegions {
        default public void add(Identifier id, Resource resource) {
            this.add(id, () -> SpriteLoader.load(id, resource));
        }

        public void add(Identifier var1, SpriteRegion var2);

        public void removeIf(Predicate<Identifier> var1);
    }
}

