package net.minecraft.tag;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;

public class FluidTags {
	private static TagContainer<Fluid> container = new TagContainer<>(identifier -> Optional.empty(), "", false, "");
	private static int containerChanges;
	public static final Tag<Fluid> field_15517 = register("water");
	public static final Tag<Fluid> field_15518 = register("lava");

	public static void setContainer(TagContainer<Fluid> tagContainer) {
		container = tagContainer;
		containerChanges++;
	}

	private static Tag<Fluid> register(String string) {
		return new FluidTags.class_3487(new Identifier(string));
	}

	public static class class_3487 extends Tag<Fluid> {
		private int field_15522 = -1;
		private Tag<Fluid> field_15521;

		public class_3487(Identifier identifier) {
			super(identifier);
		}

		public boolean method_15101(Fluid fluid) {
			if (this.field_15522 != FluidTags.containerChanges) {
				this.field_15521 = FluidTags.container.getOrCreate(this.getId());
				this.field_15522 = FluidTags.containerChanges;
			}

			return this.field_15521.contains(fluid);
		}

		@Override
		public Collection<Fluid> values() {
			if (this.field_15522 != FluidTags.containerChanges) {
				this.field_15521 = FluidTags.container.getOrCreate(this.getId());
				this.field_15522 = FluidTags.containerChanges;
			}

			return this.field_15521.values();
		}

		@Override
		public Collection<Tag.Entry<Fluid>> entries() {
			if (this.field_15522 != FluidTags.containerChanges) {
				this.field_15521 = FluidTags.container.getOrCreate(this.getId());
				this.field_15522 = FluidTags.containerChanges;
			}

			return this.field_15521.entries();
		}
	}
}
