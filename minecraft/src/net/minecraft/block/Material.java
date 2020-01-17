package net.minecraft.block;

import net.minecraft.block.piston.PistonBehavior;

public final class Material {
	public static final Material AIR = new Material.Builder(MaterialColor.AIR).allowsMovement().lightPassesThrough().notSolid().replaceable().build();
	/**
	 * Material for structure void block.
	 */
	public static final Material STRUCTURE_VOID = new Material.Builder(MaterialColor.AIR).allowsMovement().lightPassesThrough().notSolid().replaceable().build();
	/**
	 * Material for the various portal blocks.
	 */
	public static final Material PORTAL = new Material.Builder(MaterialColor.AIR).allowsMovement().lightPassesThrough().notSolid().blocksPistons().build();
	public static final Material CARPET = new Material.Builder(MaterialColor.WEB).allowsMovement().lightPassesThrough().notSolid().burnable().build();
	/**
	 * Material for plants such as flowers and crops
	 */
	public static final Material PLANT = new Material.Builder(MaterialColor.FOLIAGE).allowsMovement().lightPassesThrough().notSolid().destroyedByPiston().build();
	/**
	 * Material for underwater plants without the replaceable property.
	 */
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
	public static final Material REPLACEABLE_UNDERWATER_PLANT = new Material.Builder(MaterialColor.WATER)
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
	/**
	 * Material for non-full blocks of snow. Has the replaceable property.
	 */
	public static final Material SNOW_LAYER = new Material.Builder(MaterialColor.WHITE)
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
	/**
	 * Material for blocks that require a supporting block such as redstone components, torches, flower pots, and skulls.
	 */
	public static final Material SUPPORTED = new Material.Builder(MaterialColor.AIR).allowsMovement().lightPassesThrough().notSolid().destroyedByPiston().build();
	public static final Material COBWEB = new Material.Builder(MaterialColor.WEB).allowsMovement().lightPassesThrough().destroyedByPiston().requiresTool().build();
	public static final Material REDSTONE_LAMP = new Material.Builder(MaterialColor.AIR).build();
	/**
	 * Material for blocks that come from mobs such as honey, slime, or infested blocks. Includes clay but not bone blocks.
	 */
	public static final Material ORGANIC_PRODUCT = new Material.Builder(MaterialColor.CLAY).build();
	/**
	 * Material for the top layer of soil. Path, dirt, podzol, soul soil, farmland and similar.
	 */
	public static final Material SOIL = new Material.Builder(MaterialColor.DIRT).build();
	/**
	 * Organic blocks that are solid, including hay, and grass blocks.
	 */
	public static final Material SOLID_ORGANIC = new Material.Builder(MaterialColor.GRASS).build();
	/**
	 * Material for ice blocks that do not melt. See {@link
	 */
	public static final Material DENSE_ICE = new Material.Builder(MaterialColor.ICE).build();
	/**
	 * A material or structure formed from a loosely compacted mass of fragments or particles.
	 */
	public static final Material AGGREGATE = new Material.Builder(MaterialColor.SAND).build();
	public static final Material SPONGE = new Material.Builder(MaterialColor.YELLOW).build();
	public static final Material SHULKER_BOX = new Material.Builder(MaterialColor.PURPLE).build();
	/**
	 * Material for wood logs, and things crafted from them.
	 */
	public static final Material WOOD = new Material.Builder(MaterialColor.WOOD).burnable().build();
	public static final Material BAMBOO_SAPLING = new Material.Builder(MaterialColor.WOOD).burnable().destroyedByPiston().allowsMovement().build();
	public static final Material BAMBOO = new Material.Builder(MaterialColor.WOOD).burnable().destroyedByPiston().build();
	/**
	 * Material for wool and bed blocks.
	 */
	public static final Material WOOL = new Material.Builder(MaterialColor.WEB).burnable().build();
	public static final Material TNT = new Material.Builder(MaterialColor.LAVA).burnable().lightPassesThrough().build();
	public static final Material LEAVES = new Material.Builder(MaterialColor.FOLIAGE).burnable().lightPassesThrough().destroyedByPiston().build();
	/**
	 * Material for glass and glass-like blocks (includes sea lanterns and conduits).
	 */
	public static final Material GLASS = new Material.Builder(MaterialColor.AIR).lightPassesThrough().build();
	/**
	 * Material for ice that can melt. See {@link
	 */
	public static final Material ICE = new Material.Builder(MaterialColor.ICE).lightPassesThrough().build();
	public static final Material CACTUS = new Material.Builder(MaterialColor.FOLIAGE).lightPassesThrough().destroyedByPiston().build();
	/**
	 * Material for blocks that are stone or made from it, and generally prefer to be broken by a pickaxe.
	 */
	public static final Material STONE = new Material.Builder(MaterialColor.STONE).requiresTool().build();
	public static final Material METAL = new Material.Builder(MaterialColor.IRON).requiresTool().build();
	/**
	 * Material for full sized snow blocks.
	 */
	public static final Material SNOW_BLOCK = new Material.Builder(MaterialColor.WHITE).requiresTool().build();
	/**
	 * Material for blocks that can repair tools, including grindstone and anvils.
	 */
	public static final Material REPAIR_STATION = new Material.Builder(MaterialColor.IRON).requiresTool().blocksPistons().build();
	public static final Material BARRIER = new Material.Builder(MaterialColor.AIR).requiresTool().blocksPistons().build();
	public static final Material PISTON = new Material.Builder(MaterialColor.STONE).blocksPistons().build();
	/**
	 * Not in use, but has foliage color.
	 */
	public static final Material UNUSED_PLANT = new Material.Builder(MaterialColor.FOLIAGE).destroyedByPiston().build();
	/**
	 * Material for gourds. Includes the carved pumpkin and jack o' lantern.
	 */
	public static final Material GOURD = new Material.Builder(MaterialColor.FOLIAGE).destroyedByPiston().build();
	/**
	 * Material for egg blocks, such as dragon and turtle eggs.
	 */
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
		MaterialColor color,
		boolean liquid,
		boolean solid,
		boolean blocksMovement,
		boolean blocksLight,
		boolean breakByHand,
		boolean burnable,
		boolean replaceable,
		PistonBehavior pistonBehavior
	) {
		this.color = color;
		this.liquid = liquid;
		this.solid = solid;
		this.blocksMovement = blocksMovement;
		this.blocksLight = blocksLight;
		this.breakByHand = breakByHand;
		this.burnable = burnable;
		this.replaceable = replaceable;
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
		private PistonBehavior pistonBehavior = PistonBehavior.NORMAL;
		private boolean blocksMovement = true;
		private boolean burnable;
		private boolean breakByHand = true;
		private boolean liquid;
		private boolean replaceable;
		private boolean solid = true;
		private final MaterialColor color;
		private boolean blocksLight = true;

		public Builder(MaterialColor color) {
			this.color = color;
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
			this.pistonBehavior = PistonBehavior.DESTROY;
			return this;
		}

		protected Material.Builder blocksPistons() {
			this.pistonBehavior = PistonBehavior.BLOCK;
			return this;
		}

		public Material build() {
			return new Material(
				this.color, this.liquid, this.solid, this.blocksMovement, this.blocksLight, this.breakByHand, this.burnable, this.replaceable, this.pistonBehavior
			);
		}
	}
}
