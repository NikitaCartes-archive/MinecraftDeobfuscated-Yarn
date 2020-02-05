/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.MaterialColor;
import net.minecraft.block.piston.PistonBehavior;

public final class Material {
    public static final Material AIR = Builder.method_15808(new Builder(MaterialColor.AIR).allowsMovement()).notSolid().replaceable().build();
    public static final Material STRUCTURE_VOID = Builder.method_15808(new Builder(MaterialColor.AIR).allowsMovement()).notSolid().replaceable().build();
    public static final Material PORTAL = Builder.method_15808(new Builder(MaterialColor.AIR).allowsMovement()).notSolid().blocksPistons().build();
    public static final Material CARPET = Builder.method_15808(new Builder(MaterialColor.WEB).allowsMovement()).notSolid().burnable().build();
    public static final Material PLANT = Builder.method_15808(new Builder(MaterialColor.FOLIAGE).allowsMovement()).notSolid().destroyedByPiston().build();
    public static final Material UNDERWATER_PLANT = Builder.method_15808(new Builder(MaterialColor.WATER).allowsMovement()).notSolid().destroyedByPiston().build();
    public static final Material REPLACEABLE_PLANT = Builder.method_15808(new Builder(MaterialColor.FOLIAGE).allowsMovement()).notSolid().destroyedByPiston().replaceable().burnable().build();
    public static final Material SEAGRASS = Builder.method_15808(new Builder(MaterialColor.WATER).allowsMovement()).notSolid().destroyedByPiston().replaceable().build();
    public static final Material WATER = Builder.method_15808(new Builder(MaterialColor.WATER).allowsMovement()).notSolid().destroyedByPiston().replaceable().liquid().build();
    public static final Material BUBBLE_COLUMN = Builder.method_15808(new Builder(MaterialColor.WATER).allowsMovement()).notSolid().destroyedByPiston().replaceable().liquid().build();
    public static final Material LAVA = Builder.method_15808(new Builder(MaterialColor.LAVA).allowsMovement()).notSolid().destroyedByPiston().replaceable().liquid().build();
    public static final Material SNOW = Builder.method_15808(new Builder(MaterialColor.WHITE).allowsMovement()).notSolid().destroyedByPiston().replaceable().requiresTool().build();
    public static final Material FIRE = Builder.method_15808(new Builder(MaterialColor.AIR).allowsMovement()).notSolid().destroyedByPiston().replaceable().build();
    public static final Material PART = Builder.method_15808(new Builder(MaterialColor.AIR).allowsMovement()).notSolid().destroyedByPiston().build();
    public static final Material COBWEB = Builder.method_15808(new Builder(MaterialColor.WEB).allowsMovement()).destroyedByPiston().requiresTool().build();
    public static final Material REDSTONE_LAMP = new Builder(MaterialColor.AIR).build();
    public static final Material CLAY = new Builder(MaterialColor.CLAY).build();
    public static final Material EARTH = new Builder(MaterialColor.DIRT).build();
    public static final Material ORGANIC = new Builder(MaterialColor.GRASS).build();
    public static final Material PACKED_ICE = new Builder(MaterialColor.ICE).build();
    public static final Material SAND = new Builder(MaterialColor.SAND).build();
    public static final Material SPONGE = new Builder(MaterialColor.YELLOW).build();
    public static final Material SHULKER_BOX = new Builder(MaterialColor.PURPLE).build();
    public static final Material WOOD = new Builder(MaterialColor.WOOD).burnable().build();
    public static final Material NETHER_WOOD = new Builder(MaterialColor.WOOD).build();
    public static final Material BAMBOO_SAPLING = new Builder(MaterialColor.WOOD).burnable().destroyedByPiston().allowsMovement().build();
    public static final Material BAMBOO = new Builder(MaterialColor.WOOD).burnable().destroyedByPiston().build();
    public static final Material WOOL = new Builder(MaterialColor.WEB).burnable().build();
    public static final Material TNT = Builder.method_15808(new Builder(MaterialColor.LAVA).burnable()).build();
    public static final Material LEAVES = Builder.method_15808(new Builder(MaterialColor.FOLIAGE).burnable()).destroyedByPiston().build();
    public static final Material GLASS = Builder.method_15808(new Builder(MaterialColor.AIR)).build();
    public static final Material ICE = Builder.method_15808(new Builder(MaterialColor.ICE)).build();
    public static final Material CACTUS = Builder.method_15808(new Builder(MaterialColor.FOLIAGE)).destroyedByPiston().build();
    public static final Material STONE = new Builder(MaterialColor.STONE).requiresTool().build();
    public static final Material METAL = new Builder(MaterialColor.IRON).requiresTool().build();
    public static final Material SNOW_BLOCK = new Builder(MaterialColor.WHITE).requiresTool().build();
    public static final Material ANVIL = new Builder(MaterialColor.IRON).requiresTool().blocksPistons().build();
    public static final Material BARRIER = new Builder(MaterialColor.AIR).requiresTool().blocksPistons().build();
    public static final Material PISTON = new Builder(MaterialColor.STONE).blocksPistons().build();
    public static final Material UNUSED_PLANT = new Builder(MaterialColor.FOLIAGE).destroyedByPiston().build();
    public static final Material PUMPKIN = new Builder(MaterialColor.FOLIAGE).destroyedByPiston().build();
    public static final Material EGG = new Builder(MaterialColor.FOLIAGE).destroyedByPiston().build();
    public static final Material CAKE = new Builder(MaterialColor.AIR).destroyedByPiston().build();
    private final MaterialColor color;
    private final PistonBehavior pistonBehavior;
    private final boolean blocksMovement;
    private final boolean burnable;
    private final boolean breakByHand;
    private final boolean liquid;
    private final boolean blocksLight;
    private final boolean replaceable;
    private final boolean solid;

    public Material(MaterialColor color, boolean liquid, boolean solid, boolean blocksMovement, boolean blocksLight, boolean breakByHand, boolean burnable, boolean replaceable, PistonBehavior pistonBehavior) {
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

        protected Builder requiresTool() {
            this.breakByHand = false;
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
            return new Material(this.color, this.liquid, this.solid, this.blocksMovement, this.blocksLight, this.breakByHand, this.burnable, this.replaceable, this.pistonBehavior);
        }

        static /* synthetic */ Builder method_15808(Builder builder) {
            return builder.lightPassesThrough();
        }
    }
}

