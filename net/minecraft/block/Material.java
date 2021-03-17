/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;

public final class Material {
    public static final Material AIR = Builder.method_15808(new Builder(MapColor.CLEAR).allowsMovement()).notSolid().replaceable().build();
    /**
     * Material for structure void block.
     */
    public static final Material STRUCTURE_VOID = Builder.method_15808(new Builder(MapColor.CLEAR).allowsMovement()).notSolid().replaceable().build();
    /**
     * Material for the various portal blocks.
     */
    public static final Material PORTAL = Builder.method_15808(new Builder(MapColor.CLEAR).allowsMovement()).notSolid().blocksPistons().build();
    public static final Material CARPET = Builder.method_15808(new Builder(MapColor.WHITE_GRAY).allowsMovement()).notSolid().burnable().build();
    /**
     * Material for plants such as flowers and crops
     */
    public static final Material PLANT = Builder.method_15808(new Builder(MapColor.DARK_GREEN).allowsMovement()).notSolid().destroyedByPiston().build();
    /**
     * Material for underwater plants without the replaceable property.
     */
    public static final Material UNDERWATER_PLANT = Builder.method_15808(new Builder(MapColor.WATER_BLUE).allowsMovement()).notSolid().destroyedByPiston().build();
    public static final Material REPLACEABLE_PLANT = Builder.method_15808(new Builder(MapColor.DARK_GREEN).allowsMovement()).notSolid().destroyedByPiston().replaceable().burnable().build();
    /**
     * Material for crimson and warped roots, as well as Nether sprouts.
     */
    public static final Material NETHER_SHOOTS = Builder.method_15808(new Builder(MapColor.DARK_GREEN).allowsMovement()).notSolid().destroyedByPiston().replaceable().build();
    public static final Material REPLACEABLE_UNDERWATER_PLANT = Builder.method_15808(new Builder(MapColor.WATER_BLUE).allowsMovement()).notSolid().destroyedByPiston().replaceable().build();
    public static final Material WATER = Builder.method_15808(new Builder(MapColor.WATER_BLUE).allowsMovement()).notSolid().destroyedByPiston().replaceable().liquid().build();
    public static final Material BUBBLE_COLUMN = Builder.method_15808(new Builder(MapColor.WATER_BLUE).allowsMovement()).notSolid().destroyedByPiston().replaceable().liquid().build();
    public static final Material LAVA = Builder.method_15808(new Builder(MapColor.BRIGHT_RED).allowsMovement()).notSolid().destroyedByPiston().replaceable().liquid().build();
    /**
     * Material for non-full blocks of snow. Has the replaceable property.
     */
    public static final Material SNOW_LAYER = Builder.method_15808(new Builder(MapColor.WHITE).allowsMovement()).notSolid().destroyedByPiston().replaceable().build();
    public static final Material FIRE = Builder.method_15808(new Builder(MapColor.CLEAR).allowsMovement()).notSolid().destroyedByPiston().replaceable().build();
    /**
     * Material for decoration blocks such as redstone components, torches, flower pots, rails, buttons, and skulls.
     */
    public static final Material DECORATION = Builder.method_15808(new Builder(MapColor.CLEAR).allowsMovement()).notSolid().destroyedByPiston().build();
    public static final Material COBWEB = Builder.method_15808(new Builder(MapColor.WHITE_GRAY).allowsMovement()).destroyedByPiston().build();
    public static final Material SCULK = new Builder(MapColor.BLACK).build();
    public static final Material REDSTONE_LAMP = new Builder(MapColor.CLEAR).build();
    /**
     * Material for blocks that come from mobs such as honey, slime, or infested blocks. Includes clay but not bone blocks.
     */
    public static final Material ORGANIC_PRODUCT = new Builder(MapColor.LIGHT_BLUE_GRAY).build();
    /**
     * Material for the top layer of soil. Path, dirt, podzol, soul soil, farmland and similar.
     */
    public static final Material SOIL = new Builder(MapColor.DIRT_BROWN).build();
    /**
     * Organic blocks that are solid, including hay, target, and grass blocks.
     */
    public static final Material SOLID_ORGANIC = new Builder(MapColor.PALE_GREEN).build();
    /**
     * Material for ice blocks that do not melt. See {@link #ICE} for meltable ice.
     */
    public static final Material DENSE_ICE = new Builder(MapColor.PALE_PURPLE).build();
    /**
     * A material or structure formed from a loosely compacted mass of fragments or particles.
     */
    public static final Material AGGREGATE = new Builder(MapColor.PALE_YELLOW).build();
    public static final Material SPONGE = new Builder(MapColor.YELLOW).build();
    public static final Material SHULKER_BOX = new Builder(MapColor.PURPLE).build();
    /**
     * Material for wood logs, and things crafted from them.
     */
    public static final Material WOOD = new Builder(MapColor.OAK_TAN).burnable().build();
    /**
     * Material for blocks crafted from Nether stems and hyphae.
     */
    public static final Material NETHER_WOOD = new Builder(MapColor.OAK_TAN).build();
    public static final Material BAMBOO_SAPLING = new Builder(MapColor.OAK_TAN).burnable().destroyedByPiston().allowsMovement().build();
    public static final Material BAMBOO = new Builder(MapColor.OAK_TAN).burnable().destroyedByPiston().build();
    /**
     * Material for wool and bed blocks.
     */
    public static final Material WOOL = new Builder(MapColor.WHITE_GRAY).burnable().build();
    public static final Material TNT = Builder.method_15808(new Builder(MapColor.BRIGHT_RED).burnable()).build();
    public static final Material LEAVES = Builder.method_15808(new Builder(MapColor.DARK_GREEN).burnable()).destroyedByPiston().build();
    /**
     * Material for glass and glass-like blocks (includes sea lanterns and conduits).
     */
    public static final Material GLASS = Builder.method_15808(new Builder(MapColor.CLEAR)).build();
    /**
     * Material for ice that can melt. See {@link #DENSE_ICE} for unmeltable ice.
     */
    public static final Material ICE = Builder.method_15808(new Builder(MapColor.PALE_PURPLE)).build();
    public static final Material CACTUS = Builder.method_15808(new Builder(MapColor.DARK_GREEN)).destroyedByPiston().build();
    /**
     * Material for blocks that are stone or made from it, and generally prefer to be broken by a pickaxe.
     */
    public static final Material STONE = new Builder(MapColor.STONE_GRAY).build();
    /**
     * Material for blocks metallic in nature, such as cauldrons, bells, iron doors, and iron trapdoors. It also includes non-obvious blocks such as brewing stands and compressed ore blocks, including diamond, redstone, and lapis blocks.
     */
    public static final Material METAL = new Builder(MapColor.IRON_GRAY).build();
    /**
     * Material for full sized snow blocks.
     */
    public static final Material SNOW_BLOCK = new Builder(MapColor.WHITE).build();
    /**
     * Material for blocks that can repair tools, including grindstone and anvils.
     */
    public static final Material REPAIR_STATION = new Builder(MapColor.IRON_GRAY).blocksPistons().build();
    public static final Material BARRIER = new Builder(MapColor.CLEAR).blocksPistons().build();
    public static final Material PISTON = new Builder(MapColor.STONE_GRAY).blocksPistons().build();
    /**
     * Not in use, but has foliage color.
     */
    public static final Material UNUSED_PLANT = new Builder(MapColor.DARK_GREEN).destroyedByPiston().build();
    /**
     * Material for gourds. Includes the carved pumpkin and jack o' lantern.
     */
    public static final Material GOURD = new Builder(MapColor.DARK_GREEN).destroyedByPiston().build();
    /**
     * Material for egg blocks, such as dragon and turtle eggs.
     */
    public static final Material EGG = new Builder(MapColor.DARK_GREEN).destroyedByPiston().build();
    public static final Material CAKE = new Builder(MapColor.CLEAR).destroyedByPiston().build();
    public static final Material AMETHYST = Builder.method_15808(new Builder(MapColor.PURPLE)).build();
    public static final Material POWDER_SNOW = new Builder(MapColor.WHITE).notSolid().allowsMovement().build();
    private final MapColor color;
    private final PistonBehavior pistonBehavior;
    private final boolean blocksMovement;
    private final boolean burnable;
    private final boolean liquid;
    private final boolean blocksLight;
    private final boolean replaceable;
    private final boolean solid;

    public Material(MapColor color, boolean liquid, boolean solid, boolean blocksMovement, boolean blocksLight, boolean breakByHand, boolean burnable, PistonBehavior pistonBehavior) {
        this.color = color;
        this.liquid = liquid;
        this.solid = solid;
        this.blocksMovement = blocksMovement;
        this.blocksLight = blocksLight;
        this.burnable = breakByHand;
        this.replaceable = burnable;
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

        public Builder liquid() {
            this.liquid = true;
            return this;
        }

        public Builder notSolid() {
            this.solid = false;
            return this;
        }

        public Builder allowsMovement() {
            this.blocksMovement = false;
            return this;
        }

        private Builder lightPassesThrough() {
            this.blocksLight = false;
            return this;
        }

        protected Builder burnable() {
            this.burnable = true;
            return this;
        }

        public Builder replaceable() {
            this.replaceable = true;
            return this;
        }

        protected Builder destroyedByPiston() {
            this.pistonBehavior = PistonBehavior.DESTROY;
            return this;
        }

        protected Builder blocksPistons() {
            this.pistonBehavior = PistonBehavior.BLOCK;
            return this;
        }

        public Material build() {
            return new Material(this.color, this.liquid, this.solid, this.blocksMovement, this.blocksLight, this.burnable, this.replaceable, this.pistonBehavior);
        }

        static /* synthetic */ Builder method_15808(Builder builder) {
            return builder.lightPassesThrough();
        }
    }
}

