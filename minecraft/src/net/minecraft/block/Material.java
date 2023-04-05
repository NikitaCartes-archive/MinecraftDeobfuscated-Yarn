package net.minecraft.block;

public final class Material {
	/**
	 * Material for plants such as flowers and crops
	 */
	public static final Material PLANT = new Material.Builder(MapColor.DARK_GREEN).allowsMovement().lightPassesThrough().notSolid().build();
	/**
	 * A material or structure formed from a loosely compacted mass of fragments or particles.
	 */
	public static final Material AGGREGATE = new Material.Builder(MapColor.PALE_YELLOW).build();
	/**
	 * Material for wood logs, and things crafted from them.
	 */
	public static final Material WOOD = new Material.Builder(MapColor.OAK_TAN).build();
	/**
	 * Material for blocks that are stone or made from it, and generally prefer to be broken by a pickaxe.
	 */
	public static final Material STONE = new Material.Builder(MapColor.STONE_GRAY).build();
	/**
	 * Material for glass and glass-like blocks (includes sea lanterns and conduits).
	 */
	public static final Material GLASS = new Material.Builder(MapColor.CLEAR).lightPassesThrough().build();
	public static final Material ALLOWS_MOVEMENT_LIGHT_PASSES_THROUGH_NOT_SOLID_REPLACEABLE = new Material.Builder(MapColor.CLEAR)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.replaceable()
		.build();
	public static final Material ALLOWS_MOVEMENT_LIGHT_PASSES_THROUGH_NOT_SOLID = new Material.Builder(MapColor.CLEAR)
		.allowsMovement()
		.lightPassesThrough()
		.notSolid()
		.build();
	public static final Material ALLOWS_MOVEMENT = new Material.Builder(MapColor.CLEAR).allowsMovement().build();
	public static final Material LIGHT_PASSES_THROUGH = new Material.Builder(MapColor.CLEAR).lightPassesThrough().build();
	public static final Material COBWEB = new Material.Builder(MapColor.CLEAR).allowsMovement().lightPassesThrough().build();
	public static final Material NOT_SOLID_ALLOWS_MOVEMENT = new Material.Builder(MapColor.CLEAR).notSolid().allowsMovement().build();
	public static final Material GENERIC = new Material.Builder(MapColor.CLEAR).build();
	private final MapColor color;
	private final boolean blocksMovement;
	private final boolean blocksLight;
	private final boolean replaceable;
	private final boolean solid;

	public Material(MapColor color, boolean solid, boolean blocksMovement, boolean blocksLight, boolean replaceable) {
		this.color = color;
		this.solid = solid;
		this.blocksMovement = blocksMovement;
		this.blocksLight = blocksLight;
		this.replaceable = replaceable;
	}

	public boolean isSolid() {
		return this.solid;
	}

	public boolean blocksMovement() {
		return this.blocksMovement;
	}

	public boolean isReplaceable() {
		return this.replaceable;
	}

	public boolean blocksLight() {
		return this.blocksLight;
	}

	public MapColor getColor() {
		return this.color;
	}

	public static class Builder {
		private boolean blocksMovement = true;
		private boolean replaceable;
		private boolean solid = true;
		private final MapColor color;
		private boolean blocksLight = true;

		public Builder(MapColor color) {
			this.color = color;
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

		public Material.Builder replaceable() {
			this.replaceable = true;
			return this;
		}

		public Material build() {
			return new Material(this.color, this.solid, this.blocksMovement, this.blocksLight, this.replaceable);
		}
	}
}
