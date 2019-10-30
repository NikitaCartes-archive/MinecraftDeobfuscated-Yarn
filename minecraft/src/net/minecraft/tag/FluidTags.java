package net.minecraft.tag;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;

public class FluidTags {
	private static TagContainer<Fluid> container = new TagContainer<>(identifier -> Optional.empty(), "", false, "");
	private static int latestVersion;
	public static final Tag<Fluid> WATER = register("water");
	public static final Tag<Fluid> LAVA = register("lava");

	public static void setContainer(TagContainer<Fluid> container) {
		FluidTags.container = container;
		latestVersion++;
	}

	public static TagContainer<Fluid> getContainer() {
		return container;
	}

	private static Tag<Fluid> register(String id) {
		return new FluidTags.CachingTag(new Identifier(id));
	}

	public static class CachingTag extends Tag<Fluid> {
		private int version = -1;
		private Tag<Fluid> delegate;

		public CachingTag(Identifier identifier) {
			super(identifier);
		}

		public boolean method_15101(Fluid fluid) {
			if (this.version != FluidTags.latestVersion) {
				this.delegate = FluidTags.container.getOrCreate(this.getId());
				this.version = FluidTags.latestVersion;
			}

			return this.delegate.contains(fluid);
		}

		@Override
		public Collection<Fluid> values() {
			if (this.version != FluidTags.latestVersion) {
				this.delegate = FluidTags.container.getOrCreate(this.getId());
				this.version = FluidTags.latestVersion;
			}

			return this.delegate.values();
		}

		@Override
		public Collection<Tag.Entry<Fluid>> entries() {
			if (this.version != FluidTags.latestVersion) {
				this.delegate = FluidTags.container.getOrCreate(this.getId());
				this.version = FluidTags.latestVersion;
			}

			return this.delegate.entries();
		}
	}
}
