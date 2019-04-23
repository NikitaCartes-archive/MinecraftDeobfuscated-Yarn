package net.minecraft.tag;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;

public class FluidTags {
	private static TagContainer<Fluid> container = new TagContainer<>(identifier -> Optional.empty(), "", false, "");
	private static int latestVersion;
	public static final Tag<Fluid> field_15517 = register("water");
	public static final Tag<Fluid> field_15518 = register("lava");

	public static void setContainer(TagContainer<Fluid> tagContainer) {
		container = tagContainer;
		latestVersion++;
	}

	private static Tag<Fluid> register(String string) {
		return new FluidTags.CachingTag(new Identifier(string));
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
