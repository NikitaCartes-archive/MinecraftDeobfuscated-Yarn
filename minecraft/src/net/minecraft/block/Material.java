package net.minecraft.block;

import net.minecraft.block.piston.PistonBehavior;

public final class Material {
	public static final Material AIR = new Material.Builder(MaterialColor.AIR).suffocates().setFlag5().setFlag7().setFlag6().build();
	public static final Material STRUCTURE_VOID = new Material.Builder(MaterialColor.AIR).suffocates().setFlag5().setFlag7().setFlag6().build();
	public static final Material PORTAL = new Material.Builder(MaterialColor.AIR).suffocates().setFlag5().setFlag7().blocksPistons().build();
	public static final Material CARPET = new Material.Builder(MaterialColor.WEB).suffocates().setFlag5().setFlag7().burnable().build();
	public static final Material PLANT = new Material.Builder(MaterialColor.FOLIAGE).suffocates().setFlag5().setFlag7().destroyedByPiston().build();
	public static final Material UNDERWATER_PLANT = new Material.Builder(MaterialColor.WATER).suffocates().setFlag5().setFlag7().destroyedByPiston().build();
	public static final Material REPLACEABLE_PLANT = new Material.Builder(MaterialColor.FOLIAGE)
		.suffocates()
		.setFlag5()
		.setFlag7()
		.destroyedByPiston()
		.setFlag6()
		.burnable()
		.build();
	public static final Material SEAGRASS = new Material.Builder(MaterialColor.WATER).suffocates().setFlag5().setFlag7().destroyedByPiston().setFlag6().build();
	public static final Material WATER = new Material.Builder(MaterialColor.WATER)
		.suffocates()
		.setFlag5()
		.setFlag7()
		.destroyedByPiston()
		.setFlag6()
		.setFlag4()
		.build();
	public static final Material BUBBLE_COLUMN = new Material.Builder(MaterialColor.WATER)
		.suffocates()
		.setFlag5()
		.setFlag7()
		.destroyedByPiston()
		.setFlag6()
		.setFlag4()
		.build();
	public static final Material LAVA = new Material.Builder(MaterialColor.LAVA)
		.suffocates()
		.setFlag5()
		.setFlag7()
		.destroyedByPiston()
		.setFlag6()
		.setFlag4()
		.build();
	public static final Material SNOW = new Material.Builder(MaterialColor.WHITE)
		.suffocates()
		.setFlag5()
		.setFlag7()
		.destroyedByPiston()
		.setFlag6()
		.setFlag3()
		.build();
	public static final Material FIRE = new Material.Builder(MaterialColor.AIR).suffocates().setFlag5().setFlag7().destroyedByPiston().setFlag6().build();
	public static final Material PART = new Material.Builder(MaterialColor.AIR).suffocates().setFlag5().setFlag7().destroyedByPiston().build();
	public static final Material COBWEB = new Material.Builder(MaterialColor.WEB).suffocates().setFlag5().destroyedByPiston().setFlag3().build();
	public static final Material REDSTONE_LAMP = new Material.Builder(MaterialColor.AIR).build();
	public static final Material CLAY = new Material.Builder(MaterialColor.CLAY).build();
	public static final Material EARTH = new Material.Builder(MaterialColor.DIRT).build();
	public static final Material ORGANIC = new Material.Builder(MaterialColor.GRASS).build();
	public static final Material PACKED_ICE = new Material.Builder(MaterialColor.ICE).build();
	public static final Material SAND = new Material.Builder(MaterialColor.SAND).build();
	public static final Material SPONGE = new Material.Builder(MaterialColor.YELLOW).build();
	public static final Material SHULKER_BOX = new Material.Builder(MaterialColor.PURPLE).build();
	public static final Material WOOD = new Material.Builder(MaterialColor.WOOD).burnable().build();
	public static final Material BAMBOO_SAPLING = new Material.Builder(MaterialColor.WOOD).burnable().destroyedByPiston().suffocates().build();
	public static final Material BAMBOO = new Material.Builder(MaterialColor.WOOD).burnable().destroyedByPiston().build();
	public static final Material WOOL = new Material.Builder(MaterialColor.WEB).burnable().build();
	public static final Material TNT = new Material.Builder(MaterialColor.LAVA).burnable().setFlag5().build();
	public static final Material LEAVES = new Material.Builder(MaterialColor.FOLIAGE).burnable().setFlag5().destroyedByPiston().build();
	public static final Material GLASS = new Material.Builder(MaterialColor.AIR).setFlag5().build();
	public static final Material ICE = new Material.Builder(MaterialColor.ICE).setFlag5().build();
	public static final Material CACTUS = new Material.Builder(MaterialColor.FOLIAGE).setFlag5().destroyedByPiston().build();
	public static final Material STONE = new Material.Builder(MaterialColor.STONE).setFlag3().build();
	public static final Material METAL = new Material.Builder(MaterialColor.IRON).setFlag3().build();
	public static final Material SNOW_BLOCK = new Material.Builder(MaterialColor.WHITE).setFlag3().build();
	public static final Material ANVIL = new Material.Builder(MaterialColor.IRON).setFlag3().blocksPistons().build();
	public static final Material BARRIER = new Material.Builder(MaterialColor.AIR).setFlag3().blocksPistons().build();
	public static final Material PISTON = new Material.Builder(MaterialColor.STONE).blocksPistons().build();
	public static final Material field_15921 = new Material.Builder(MaterialColor.FOLIAGE).destroyedByPiston().build();
	public static final Material PUMPKIN = new Material.Builder(MaterialColor.FOLIAGE).destroyedByPiston().build();
	public static final Material DRAGON_EGG = new Material.Builder(MaterialColor.FOLIAGE).destroyedByPiston().build();
	public static final Material CAKE = new Material.Builder(MaterialColor.AIR).destroyedByPiston().build();
	private final MaterialColor color;
	private final PistonBehavior pistonBehavior;
	private final boolean suffocates;
	private final boolean burnable;
	private final boolean flag3;
	private final boolean flag4;
	private final boolean flag5;
	private final boolean flag6;
	private final boolean flag7;

	public Material(
		MaterialColor materialColor, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, boolean bl6, boolean bl7, PistonBehavior pistonBehavior
	) {
		this.color = materialColor;
		this.flag4 = bl;
		this.flag7 = bl2;
		this.suffocates = bl3;
		this.flag5 = bl4;
		this.flag3 = bl5;
		this.burnable = bl6;
		this.flag6 = bl7;
		this.pistonBehavior = pistonBehavior;
	}

	public boolean method_15797() {
		return this.flag4;
	}

	public boolean method_15799() {
		return this.flag7;
	}

	public boolean suffocates() {
		return this.suffocates;
	}

	public boolean isBurnable() {
		return this.burnable;
	}

	public boolean method_15800() {
		return this.flag6;
	}

	public boolean method_15804() {
		return this.flag5;
	}

	public boolean method_15805() {
		return this.flag3;
	}

	public PistonBehavior getPistonBehavior() {
		return this.pistonBehavior;
	}

	public MaterialColor getColor() {
		return this.color;
	}

	public static class Builder {
		private PistonBehavior pistonBehavior = PistonBehavior.field_15974;
		private boolean suffocates = true;
		private boolean burnable;
		private boolean flag3 = true;
		private boolean flag4;
		private boolean flag6;
		private boolean flag7 = true;
		private final MaterialColor color;
		private boolean flag5 = true;

		public Builder(MaterialColor materialColor) {
			this.color = materialColor;
		}

		public Material.Builder setFlag4() {
			this.flag4 = true;
			return this;
		}

		public Material.Builder setFlag7() {
			this.flag7 = false;
			return this;
		}

		public Material.Builder suffocates() {
			this.suffocates = false;
			return this;
		}

		private Material.Builder setFlag5() {
			this.flag5 = false;
			return this;
		}

		protected Material.Builder setFlag3() {
			this.flag3 = false;
			return this;
		}

		protected Material.Builder burnable() {
			this.burnable = true;
			return this;
		}

		public Material.Builder setFlag6() {
			this.flag6 = true;
			return this;
		}

		protected Material.Builder destroyedByPiston() {
			this.pistonBehavior = PistonBehavior.field_15971;
			return this;
		}

		protected Material.Builder blocksPistons() {
			this.pistonBehavior = PistonBehavior.field_15972;
			return this;
		}

		public Material build() {
			return new Material(this.color, this.flag4, this.flag7, this.suffocates, this.flag5, this.flag3, this.burnable, this.flag6, this.pistonBehavior);
		}
	}
}
