package net.minecraft.client.texture.atlas;

import java.util.function.Predicate;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface AtlasSource {
	void load(ResourceManager resourceManager, AtlasSource.SpriteRegions regions);

	AtlasSourceType getType();

	@Environment(EnvType.CLIENT)
	public interface SpriteRegion extends Supplier<SpriteContents> {
		default void close() {
		}
	}

	@Environment(EnvType.CLIENT)
	public interface SpriteRegions {
		default void add(Identifier id, Resource resource) {
			this.add(id, () -> SpriteLoader.load(id, resource));
		}

		void add(Identifier arg, AtlasSource.SpriteRegion region);

		void method_47671(Predicate<Identifier> predicate);
	}
}
