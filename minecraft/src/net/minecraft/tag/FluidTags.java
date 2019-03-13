package net.minecraft.tag;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;

public class FluidTags {
	private static TagContainer<Fluid> field_15520 = new TagContainer<>(identifier -> Optional.empty(), "", false, "");
	private static int containerChanges;
	public static final Tag<Fluid> field_15517 = method_15095("water");
	public static final Tag<Fluid> field_15518 = method_15095("lava");

	public static void method_15096(TagContainer<Fluid> tagContainer) {
		field_15520 = tagContainer;
		containerChanges++;
	}

	private static Tag<Fluid> method_15095(String string) {
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
				this.field_15521 = FluidTags.field_15520.getOrCreate(this.getId());
				this.field_15522 = FluidTags.containerChanges;
			}

			return this.field_15521.contains(fluid);
		}

		@Override
		public Collection<Fluid> values() {
			if (this.field_15522 != FluidTags.containerChanges) {
				this.field_15521 = FluidTags.field_15520.getOrCreate(this.getId());
				this.field_15522 = FluidTags.containerChanges;
			}

			return this.field_15521.values();
		}

		@Override
		public Collection<Tag.Entry<Fluid>> entries() {
			if (this.field_15522 != FluidTags.containerChanges) {
				this.field_15521 = FluidTags.field_15520.getOrCreate(this.getId());
				this.field_15522 = FluidTags.containerChanges;
			}

			return this.field_15521.entries();
		}
	}
}
