package net.minecraft.block;

import net.minecraft.block.piston.PistonBehavior;

public final class Material {
	public static final Material AIR = new Material.Builder(MaterialColor.AIR).allowsMovement().lightPassesThrough().notSolid().replaceable().build();
	public static final Material STRUCTURE_VOID = new Material.Builder(MaterialColor.AIR).allowsMovement().lightPassesThrough().notSolid().replaceable().build();
	public static final Material PORTAL = new Material.Builder(MaterialColor.AIR).allowsMovement().lightPassesThrough().notSolid().blocksPistons().build();
	public static final Material CARPET = new Material.Builder(MaterialColor.WEB).allowsMovement().lightPassesThrough().notSolid().burnable().build();
	public static final Material PLANT = new Material.Builder(MaterialColor.FOLIAGE).allowsMovement().lightPassesThrough().notSolid().destroyedByPiston().build();
	public static final Material UNDERWATER_PLANT = new Material.Builder(MaterialColor.WATER)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.build();
	public static final Material REPLACEABLE_PLANT = new Material.Builder(MaterialColor.FOLIAGE)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.replaceable()
		.burnable()
		.build();
	public static final Material SEAGRASS = new Material.Builder(MaterialColor.WATER)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.replaceable()
		.build();
	public static final Material WATER = new Material.Builder(MaterialColor.WATER)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.replaceable()
		.liquid()
		.build();
	public static final Material BUBBLE_COLUMN = new Material.Builder(MaterialColor.WATER)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.replaceable()
		.liquid()
		.build();
	public static final Material LAVA = new Material.Builder(MaterialColor.LAVA)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.replaceable()
		.liquid()
		.build();
	public static final Material SNOW = new Material.Builder(MaterialColor.WHITE)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.replaceable()
		.requiresTool()
		.build();
	public static final Material FIRE = new Material.Builder(MaterialColor.AIR)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.replaceable()
		.build();
	public static final Material PART = new Material.Builder(MaterialColor.AIR).allowsMovement().lightPassesThrough().notSolid().destroyedByPiston().build();
	public static final Material COBWEB = new Material.Builder(MaterialColor.WEB).allowsMovement().lightPassesThrough().destroyedByPiston().requiresTool().build();
	public static final Material REDSTONE_LAMP = new Material.Builder(MaterialColor.AIR).build();
	public static final Material CLAY = new Material.Builder(MaterialColor.CLAY).build();
	public static final Material EARTH = new Material.Builder(MaterialColor.DIRT).build();
	public static final Material ORGANIC = new Material.Builder(MaterialColor.GRASS).build();
	public static final Material PACKED_ICE = new Material.Builder(MaterialColor.ICE).build();
	public static final Material SAND = new Material.Builder(MaterialColor.SAND).build();
	public static final Material SPONGE = new Material.Builder(MaterialColor.YELLOW).build();
	public static final Material SHULKER_BOX = new Material.Builder(MaterialColor.PURPLE).build();
	public static final Material WOOD = new Material.Builder(MaterialColor.WOOD).burnable().build();
	public static final Material BAMBOO_SAPLING = new Material.Builder(MaterialColor.WOOD).burnable().destroyedByPiston().allowsMovement().build();
	public static final Material BAMBOO = new Material.Builder(MaterialColor.WOOD).burnable().destroyedByPiston().build();
	public static final Material WOOL = new Material.Builder(MaterialColor.WEB).burnable().build();
	public static final Material TNT = new Material.Builder(MaterialColor.LAVA).burnable().lightPassesThrough().build();
	public static final Material LEAVES = new Material.Builder(MaterialColor.FOLIAGE).burnable().lightPassesThrough().destroyedByPiston().build();
	public static final Material GLASS = new Material.Builder(MaterialColor.AIR).lightPassesThrough().build();
	public static final Material ICE = new Material.Builder(MaterialColor.ICE).lightPassesThrough().build();
	public static final Material CACTUS = new Material.Builder(MaterialColor.FOLIAGE).lightPassesThrough().destroyedByPiston().build();
	public static final Material STONE = new Material.Builder(MaterialColor.STONE).requiresTool().build();
	public static final Material METAL = new Material.Builder(MaterialColor.IRON).requiresTool().build();
	public static final Material SNOW_BLOCK = new Material.Builder(MaterialColor.WHITE).requiresTool().build();
	public static final Material ANVIL = new Material.Builder(MaterialColor.IRON).requiresTool().blocksPistons().build();
	public static final Material BARRIER = new Material.Builder(MaterialColor.AIR).requiresTool().blocksPistons().build();
	public static final Material PISTON = new Material.Builder(MaterialColor.STONE).blocksPistons().build();
	public static final Material UNUSED_PLANT = new Material.Builder(MaterialColor.FOLIAGE).destroyedByPiston().build();
	public static final Material PUMPKIN = new Material.Builder(MaterialColor.FOLIAGE).destroyedByPiston().build();
	public static final Material EGG = new Material.Builder(MaterialColor.FOLIAGE).destroyedByPiston().build();
	public static final Material CAKE = new Material.Builder(MaterialColor.AIR).destroyedByPiston().build();
	private final MaterialColor color;
	private final PistonBehavior pistonBehavior;
	private final boolean blocksMovement;
	private final boolean burnable;
	private final boolean breakByHand;
	private final boolean liquid;
	private final boolean blocksLight;
	private final boolean replaceable;
	private final boolean solid;

	public Material(
		MaterialColor materialColor, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, boolean bl6, boolean bl7, PistonBehavior pistonBehavior
	) {
		this.color = materialColor;
		this.liquid = bl;
		this.solid = bl2;
		this.blocksMovement = bl3;
		this.blocksLight = bl4;
		this.breakByHand = bl5;
		this.burnable = bl6;
		this.replaceable = bl7;
		this.pistonBehavior = pistonBehavior;
	}

	public boolean isLiquid() {
		return this.liquid;
	}

	public boolean isSolid() {
		return this.solid;
	}

	public boolean blocksMovement() {
		return this.blocksMovement;
	}

	public boolean isBurnable() {
		return this.burnable;
	}

	public boolean isReplaceable() {
		return this.replaceable;
	}

	public boolean blocksLight() {
		return this.blocksLight;
	}

	public boolean canBreakByHand() {
		return this.breakByHand;
	}

	public PistonBehavior getPistonBehavior() {
		return this.pistonBehavior;
	}

	public MaterialColor getColor() {
		return this.color;
	}

	public static class Builder {
		private PistonBehavior pistonBehavior = PistonBehavior.field_15974;
		private boolean blocksMovement = true;
		private boolean burnable;
		private boolean breakByHand = true;
		private boolean liquid;
		private boolean replaceable;
		private boolean solid = true;
		private final MaterialColor color;
		private boolean blocksLight = true;

		public Builder(MaterialColor materialColor) {
			this.color = materialColor;
		}

		public Material.Builder liquid() {
			this.liquid = true;
			return this;
		}

		public Material.Builder notSolid() {
			this.solid = false;
			return this;
		}

		public Material.Builder allowsMovement() {
			this.blocksMovement = false;
			return this;
		}

		private Material.Builder lightPassesThrough() {
			this.blocksLight = false;
			return this;
		}

		protected Material.Builder requiresTool() {
			this.breakByHand = false;
			return this;
		}

		protected Material.Builder burnable() {
			this.burnable = true;
			return this;
		}

		public Material.Builder replaceable() {
			this.replaceable = true;
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
			return new Material(
				this.color, this.liquid, this.solid, this.blocksMovement, this.blocksLight, this.breakByHand, this.burnable, this.replaceable, this.pistonBehavior
			);
		}
	}
}
