package net.minecraft.block;

import net.minecraft.block.piston.PistonBehavior;

public final class Material {
	public static final Material AIR = new Material.Builder(MapColor.CLEAR).allowsMovement().lightPassesThrough().notSolid().replaceable().build();
	/**
	 * Material for structure void block.
	 */
	public static final Material STRUCTURE_VOID = new Material.Builder(MapColor.CLEAR).allowsMovement().lightPassesThrough().notSolid().replaceable().build();
	/**
	 * Material for the various portal blocks.
	 */
	public static final Material PORTAL = new Material.Builder(MapColor.CLEAR).allowsMovement().lightPassesThrough().notSolid().blocksPistons().build();
	public static final Material CARPET = new Material.Builder(MapColor.WHITE_GRAY).allowsMovement().lightPassesThrough().notSolid().burnable().build();
	/**
	 * Material for plants such as flowers and crops
	 */
	public static final Material PLANT = new Material.Builder(MapColor.DARK_GREEN).allowsMovement().lightPassesThrough().notSolid().destroyedByPiston().build();
	/**
	 * Material for underwater plants without the replaceable property.
	 */
	public static final Material UNDERWATER_PLANT = new Material.Builder(MapColor.WATER_BLUE)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.build();
	public static final Material REPLACEABLE_PLANT = new Material.Builder(MapColor.DARK_GREEN)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.replaceable()
		.burnable()
		.build();
	/**
	 * Material for crimson and warped roots, as well as Nether sprouts.
	 */
	public static final Material NETHER_SHOOTS = new Material.Builder(MapColor.DARK_GREEN)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.replaceable()
		.build();
	public static final Material REPLACEABLE_UNDERWATER_PLANT = new Material.Builder(MapColor.WATER_BLUE)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.replaceable()
		.build();
	public static final Material WATER = new Material.Builder(MapColor.WATER_BLUE)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.replaceable()
		.liquid()
		.build();
	public static final Material BUBBLE_COLUMN = new Material.Builder(MapColor.WATER_BLUE)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.replaceable()
		.liquid()
		.build();
	public static final Material LAVA = new Material.Builder(MapColor.BRIGHT_RED)
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
	public static final Material SNOW_LAYER = new Material.Builder(MapColor.WHITE)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.replaceable()
		.build();
	public static final Material FIRE = new Material.Builder(MapColor.CLEAR)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.replaceable()
		.build();
	/**
	 * Material for decoration blocks such as redstone components, torches, flower pots, rails, buttons, and skulls.
	 */
	public static final Material DECORATION = new Material.Builder(MapColor.CLEAR).allowsMovement().lightPassesThrough().notSolid().destroyedByPiston().build();
	public static final Material COBWEB = new Material.Builder(MapColor.WHITE_GRAY).allowsMovement().lightPassesThrough().destroyedByPiston().build();
	public static final Material SCULK = new Material.Builder(MapColor.BLACK).build();
	public static final Material REDSTONE_LAMP = new Material.Builder(MapColor.CLEAR).build();
	/**
	 * Material for blocks that come from mobs such as honey, slime, or infested blocks. Includes clay but not bone blocks.
	 */
	public static final Material ORGANIC_PRODUCT = new Material.Builder(MapColor.LIGHT_BLUE_GRAY).build();
	/**
	 * Material for the top layer of soil. Path, dirt, podzol, soul soil, farmland and similar.
	 */
	public static final Material SOIL = new Material.Builder(MapColor.DIRT_BROWN).build();
	/**
	 * Organic blocks that are solid, including hay, target, and grass blocks.
	 */
	public static final Material SOLID_ORGANIC = new Material.Builder(MapColor.PALE_GREEN).build();
	/**
	 * Material for ice blocks that do not melt. See {@link #ICE} for meltable ice.
	 */
	public static final Material DENSE_ICE = new Material.Builder(MapColor.PALE_PURPLE).build();
	/**
	 * A material or structure formed from a loosely compacted mass of fragments or particles.
	 */
	public static final Material AGGREGATE = new Material.Builder(MapColor.PALE_YELLOW).build();
	public static final Material SPONGE = new Material.Builder(MapColor.YELLOW).build();
	public static final Material SHULKER_BOX = new Material.Builder(MapColor.PURPLE).build();
	/**
	 * Material for wood logs, and things crafted from them.
	 */
	public static final Material WOOD = new Material.Builder(MapColor.OAK_TAN).burnable().build();
	/**
	 * Material for blocks crafted from Nether stems and hyphae.
	 */
	public static final Material NETHER_WOOD = new Material.Builder(MapColor.OAK_TAN).build();
	public static final Material BAMBOO_SAPLING = new Material.Builder(MapColor.OAK_TAN).burnable().destroyedByPiston().allowsMovement().build();
	public static final Material BAMBOO = new Material.Builder(MapColor.OAK_TAN).burnable().destroyedByPiston().build();
	/**
	 * Material for wool and bed blocks.
	 */
	public static final Material WOOL = new Material.Builder(MapColor.WHITE_GRAY).burnable().build();
	public static final Material TNT = new Material.Builder(MapColor.BRIGHT_RED).burnable().lightPassesThrough().build();
	public static final Material LEAVES = new Material.Builder(MapColor.DARK_GREEN).burnable().lightPassesThrough().destroyedByPiston().build();
	/**
	 * Material for glass and glass-like blocks (includes sea lanterns and conduits).
	 */
	public static final Material GLASS = new Material.Builder(MapColor.CLEAR).lightPassesThrough().build();
	/**
	 * Material for ice that can melt. See {@link #DENSE_ICE} for unmeltable ice.
	 */
	public static final Material ICE = new Material.Builder(MapColor.PALE_PURPLE).lightPassesThrough().build();
	public static final Material CACTUS = new Material.Builder(MapColor.DARK_GREEN).lightPassesThrough().destroyedByPiston().build();
	/**
	 * Material for blocks that are stone or made from it, and generally prefer to be broken by a pickaxe.
	 */
	public static final Material STONE = new Material.Builder(MapColor.STONE_GRAY).build();
	/**
	 * Material for blocks metallic in nature, such as cauldrons, bells, iron doors, and iron trapdoors. It also includes non-obvious blocks such as brewing stands and compressed ore blocks, including diamond, redstone, and lapis blocks.
	 */
	public static final Material METAL = new Material.Builder(MapColor.IRON_GRAY).build();
	/**
	 * Material for full sized snow blocks.
	 */
	public static final Material SNOW_BLOCK = new Material.Builder(MapColor.WHITE).build();
	/**
	 * Material for blocks that can repair tools, including grindstone and anvils.
	 */
	public static final Material REPAIR_STATION = new Material.Builder(MapColor.IRON_GRAY).blocksPistons().build();
	public static final Material BARRIER = new Material.Builder(MapColor.CLEAR).blocksPistons().build();
	public static final Material PISTON = new Material.Builder(MapColor.STONE_GRAY).blocksPistons().build();
	/**
	 * Material for full sized moss blocks.
	 */
	public static final Material MOSS_BLOCK = new Material.Builder(MapColor.DARK_GREEN).destroyedByPiston().build();
	/**
	 * Material for gourds. Includes the carved pumpkin and jack o' lantern.
	 */
	public static final Material GOURD = new Material.Builder(MapColor.DARK_GREEN).destroyedByPiston().build();
	/**
	 * Material for egg blocks, such as dragon and turtle eggs.
	 */
	public static final Material EGG = new Material.Builder(MapColor.DARK_GREEN).destroyedByPiston().build();
	public static final Material CAKE = new Material.Builder(MapColor.CLEAR).destroyedByPiston().build();
	public static final Material AMETHYST = new Material.Builder(MapColor.PURPLE).build();
	public static final Material POWDER_SNOW = new Material.Builder(MapColor.WHITE).notSolid().allowsMovement().build();
	public static final Material FROGSPAWN = new Material.Builder(MapColor.WATER_BLUE)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.destroyedByPiston()
		.build();
	private final MapColor color;
	private final PistonBehavior pistonBehavior;
	private final boolean blocksMovement;
	private final boolean burnable;
	private final boolean liquid;
	private final boolean blocksLight;
	private final boolean replaceable;
	private final boolean solid;

	public Material(
		MapColor color,
		boolean liquid,
		boolean solid,
		boolean blocksMovement,
		boolean blocksLight,
		boolean burnable,
		boolean replaceable,
		PistonBehavior pistonBehavior
	) {
		this.color = color;
		this.liquid = liquid;
		this.solid = solid;
		this.blocksMovement = blocksMovement;
		this.blocksLight = blocksLight;
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

	public PistonBehavior getPistonBehavior() {
		return this.pistonBehavior;
	}

	public MapColor getColor() {
		return this.color;
	}

	public static class Builder {
		private PistonBehavior pistonBehavior = PistonBehavior.NORMAL;
		private boolean blocksMovement = true;
		private boolean burnable;
		private boolean liquid;
		private boolean replaceable;
		private boolean solid = true;
		private final MapColor color;
		private boolean blocksLight = true;

		public Builder(MapColor color) {
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

		Material.Builder lightPassesThrough() {
			this.blocksLight = false;
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
			return new Material(this.color, this.liquid, this.solid, this.blocksMovement, this.blocksLight, this.burnable, this.replaceable, this.pistonBehavior);
		}
	}
}
