/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.SharedConstants;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractFurnaceBlockEntity
extends LockableContainerBlockEntity
implements SidedInventory,
RecipeUnlocker,
RecipeInputProvider {
    private static final int[] TOP_SLOTS = new int[]{0};
    private static final int[] BOTTOM_SLOTS = new int[]{2, 1};
    private static final int[] SIDE_SLOTS = new int[]{1};
    protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    private int burnTime;
    private int fuelTime;
    private int cookTime;
    private int cookTimeTotal;
    protected final PropertyDelegate propertyDelegate = new PropertyDelegate(){

        @Override
        public int get(int index) {
            switch (index) {
                case 0: {
                    return AbstractFurnaceBlockEntity.this.burnTime;
                }
                case 1: {
                    return AbstractFurnaceBlockEntity.this.fuelTime;
                }
                case 2: {
                    return AbstractFurnaceBlockEntity.this.cookTime;
                }
                case 3: {
                    return AbstractFurnaceBlockEntity.this.cookTimeTotal;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    AbstractFurnaceBlockEntity.this.burnTime = value;
                    break;
                }
                case 1: {
                    AbstractFurnaceBlockEntity.this.fuelTime = value;
                    break;
                }
                case 2: {
                    AbstractFurnaceBlockEntity.this.cookTime = value;
                    break;
                }
                case 3: {
                    AbstractFurnaceBlockEntity.this.cookTimeTotal = value;
                    break;
                }
            }
        }

        @Override
        public int size() {
            return 4;
        }
    };
    private final Object2IntOpenHashMap<Identifier> recipesUsed = new Object2IntOpenHashMap();
    private final RecipeType<? extends AbstractCookingRecipe> recipeType;

    protected AbstractFurnaceBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState, RecipeType<? extends AbstractCookingRecipe> recipeType) {
        super(blockEntityType, blockPos, blockState);
        this.recipeType = recipeType;
    }

    public static Map<Item, Integer> createFuelTimeMap() {
        LinkedHashMap<Item, Integer> map = Maps.newLinkedHashMap();
        AbstractFurnaceBlockEntity.addFuel(map, Items.LAVA_BUCKET, 20000);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.COAL_BLOCK, 16000);
        AbstractFurnaceBlockEntity.addFuel(map, Items.BLAZE_ROD, 2400);
        AbstractFurnaceBlockEntity.addFuel(map, Items.COAL, 1600);
        AbstractFurnaceBlockEntity.addFuel(map, Items.CHARCOAL, 1600);
        AbstractFurnaceBlockEntity.addFuel(map, ItemTags.LOGS, 300);
        AbstractFurnaceBlockEntity.addFuel(map, ItemTags.PLANKS, 300);
        AbstractFurnaceBlockEntity.addFuel(map, ItemTags.WOODEN_STAIRS, 300);
        AbstractFurnaceBlockEntity.addFuel(map, ItemTags.WOODEN_SLABS, 150);
        AbstractFurnaceBlockEntity.addFuel(map, ItemTags.WOODEN_TRAPDOORS, 300);
        AbstractFurnaceBlockEntity.addFuel(map, ItemTags.WOODEN_PRESSURE_PLATES, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.OAK_FENCE, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.BIRCH_FENCE, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.SPRUCE_FENCE, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.JUNGLE_FENCE, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.DARK_OAK_FENCE, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.ACACIA_FENCE, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.OAK_FENCE_GATE, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.BIRCH_FENCE_GATE, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.SPRUCE_FENCE_GATE, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.JUNGLE_FENCE_GATE, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.DARK_OAK_FENCE_GATE, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.ACACIA_FENCE_GATE, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.NOTE_BLOCK, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.BOOKSHELF, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.LECTERN, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.JUKEBOX, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.CHEST, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.TRAPPED_CHEST, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.CRAFTING_TABLE, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.DAYLIGHT_DETECTOR, 300);
        AbstractFurnaceBlockEntity.addFuel(map, ItemTags.BANNERS, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Items.BOW, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Items.FISHING_ROD, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.LADDER, 300);
        AbstractFurnaceBlockEntity.addFuel(map, ItemTags.SIGNS, 200);
        AbstractFurnaceBlockEntity.addFuel(map, Items.WOODEN_SHOVEL, 200);
        AbstractFurnaceBlockEntity.addFuel(map, Items.WOODEN_SWORD, 200);
        AbstractFurnaceBlockEntity.addFuel(map, Items.WOODEN_HOE, 200);
        AbstractFurnaceBlockEntity.addFuel(map, Items.WOODEN_AXE, 200);
        AbstractFurnaceBlockEntity.addFuel(map, Items.WOODEN_PICKAXE, 200);
        AbstractFurnaceBlockEntity.addFuel(map, ItemTags.WOODEN_DOORS, 200);
        AbstractFurnaceBlockEntity.addFuel(map, ItemTags.BOATS, 1200);
        AbstractFurnaceBlockEntity.addFuel(map, ItemTags.WOOL, 100);
        AbstractFurnaceBlockEntity.addFuel(map, ItemTags.WOODEN_BUTTONS, 100);
        AbstractFurnaceBlockEntity.addFuel(map, Items.STICK, 100);
        AbstractFurnaceBlockEntity.addFuel(map, ItemTags.SAPLINGS, 100);
        AbstractFurnaceBlockEntity.addFuel(map, Items.BOWL, 100);
        AbstractFurnaceBlockEntity.addFuel(map, ItemTags.CARPETS, 67);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.DRIED_KELP_BLOCK, 4001);
        AbstractFurnaceBlockEntity.addFuel(map, Items.CROSSBOW, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.BAMBOO, 50);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.DEAD_BUSH, 100);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.SCAFFOLDING, 400);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.LOOM, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.BARREL, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.CARTOGRAPHY_TABLE, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.FLETCHING_TABLE, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.SMITHING_TABLE, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.COMPOSTER, 300);
        return map;
    }

    private static boolean isNonFlammableWood(Item item) {
        return ItemTags.NON_FLAMMABLE_WOOD.contains(item);
    }

    private static void addFuel(Map<Item, Integer> fuelTimes, Tag<Item> tag, int fuelTime) {
        for (Item item : tag.values()) {
            if (AbstractFurnaceBlockEntity.isNonFlammableWood(item)) continue;
            fuelTimes.put(item, fuelTime);
        }
    }

    private static void addFuel(Map<Item, Integer> map, ItemConvertible item, int fuelTime) {
        Item item2 = item.asItem();
        if (AbstractFurnaceBlockEntity.isNonFlammableWood(item2)) {
            if (SharedConstants.isDevelopment) {
                throw Util.throwOrPause(new IllegalStateException("A developer tried to explicitly make fire resistant item " + item2.getName(null).getString() + " a furnace fuel. That will not work!"));
            }
            return;
        }
        map.put(item2, fuelTime);
    }

    private boolean isBurning() {
        return this.burnTime > 0;
    }

    @Override
    public void fromTag(CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.fromTag(compoundTag, this.inventory);
        this.burnTime = compoundTag.getShort("BurnTime");
        this.cookTime = compoundTag.getShort("CookTime");
        this.cookTimeTotal = compoundTag.getShort("CookTimeTotal");
        this.fuelTime = this.getFuelTime(this.inventory.get(1));
        CompoundTag compoundTag2 = compoundTag.getCompound("RecipesUsed");
        for (String string : compoundTag2.getKeys()) {
            this.recipesUsed.put(new Identifier(string), compoundTag2.getInt(string));
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putShort("BurnTime", (short)this.burnTime);
        tag.putShort("CookTime", (short)this.cookTime);
        tag.putShort("CookTimeTotal", (short)this.cookTimeTotal);
        Inventories.toTag(tag, this.inventory);
        CompoundTag compoundTag = new CompoundTag();
        this.recipesUsed.forEach((identifier, integer) -> compoundTag.putInt(identifier.toString(), (int)integer));
        tag.put("RecipesUsed", compoundTag);
        return tag;
    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, AbstractFurnaceBlockEntity abstractFurnaceBlockEntity) {
        boolean bl = abstractFurnaceBlockEntity.isBurning();
        boolean bl2 = false;
        if (abstractFurnaceBlockEntity.isBurning()) {
            --abstractFurnaceBlockEntity.burnTime;
        }
        ItemStack itemStack = abstractFurnaceBlockEntity.inventory.get(1);
        if (abstractFurnaceBlockEntity.isBurning() || !itemStack.isEmpty() && !abstractFurnaceBlockEntity.inventory.get(0).isEmpty()) {
            Recipe recipe = world.getRecipeManager().getFirstMatch(abstractFurnaceBlockEntity.recipeType, abstractFurnaceBlockEntity, world).orElse(null);
            int i = abstractFurnaceBlockEntity.getMaxCountPerStack();
            if (!abstractFurnaceBlockEntity.isBurning() && AbstractFurnaceBlockEntity.canAcceptRecipeOutput(recipe, abstractFurnaceBlockEntity.inventory, i)) {
                abstractFurnaceBlockEntity.fuelTime = abstractFurnaceBlockEntity.burnTime = abstractFurnaceBlockEntity.getFuelTime(itemStack);
                if (abstractFurnaceBlockEntity.isBurning()) {
                    bl2 = true;
                    if (!itemStack.isEmpty()) {
                        Item item = itemStack.getItem();
                        itemStack.decrement(1);
                        if (itemStack.isEmpty()) {
                            Item item2 = item.getRecipeRemainder();
                            abstractFurnaceBlockEntity.inventory.set(1, item2 == null ? ItemStack.EMPTY : new ItemStack(item2));
                        }
                    }
                }
            }
            if (abstractFurnaceBlockEntity.isBurning() && AbstractFurnaceBlockEntity.canAcceptRecipeOutput(recipe, abstractFurnaceBlockEntity.inventory, i)) {
                ++abstractFurnaceBlockEntity.cookTime;
                if (abstractFurnaceBlockEntity.cookTime == abstractFurnaceBlockEntity.cookTimeTotal) {
                    abstractFurnaceBlockEntity.cookTime = 0;
                    abstractFurnaceBlockEntity.cookTimeTotal = AbstractFurnaceBlockEntity.getCookTime(world, abstractFurnaceBlockEntity.recipeType, abstractFurnaceBlockEntity);
                    if (AbstractFurnaceBlockEntity.craftRecipe(recipe, abstractFurnaceBlockEntity.inventory, i)) {
                        abstractFurnaceBlockEntity.setLastRecipe(recipe);
                    }
                    bl2 = true;
                }
            } else {
                abstractFurnaceBlockEntity.cookTime = 0;
            }
        } else if (!abstractFurnaceBlockEntity.isBurning() && abstractFurnaceBlockEntity.cookTime > 0) {
            abstractFurnaceBlockEntity.cookTime = MathHelper.clamp(abstractFurnaceBlockEntity.cookTime - 2, 0, abstractFurnaceBlockEntity.cookTimeTotal);
        }
        if (bl != abstractFurnaceBlockEntity.isBurning()) {
            bl2 = true;
            blockState = (BlockState)blockState.with(AbstractFurnaceBlock.LIT, abstractFurnaceBlockEntity.isBurning());
            world.setBlockState(blockPos, blockState, 3);
        }
        if (bl2) {
            AbstractFurnaceBlockEntity.markDirty(world, blockPos, blockState);
        }
    }

    private static boolean canAcceptRecipeOutput(@Nullable Recipe<?> recipe, DefaultedList<ItemStack> defaultedList, int i) {
        if (defaultedList.get(0).isEmpty() || recipe == null) {
            return false;
        }
        ItemStack itemStack = recipe.getOutput();
        if (itemStack.isEmpty()) {
            return false;
        }
        ItemStack itemStack2 = defaultedList.get(2);
        if (itemStack2.isEmpty()) {
            return true;
        }
        if (!itemStack2.isItemEqualIgnoreDamage(itemStack)) {
            return false;
        }
        if (itemStack2.getCount() < i && itemStack2.getCount() < itemStack2.getMaxCount()) {
            return true;
        }
        return itemStack2.getCount() < itemStack.getMaxCount();
    }

    private static boolean craftRecipe(@Nullable Recipe<?> recipe, DefaultedList<ItemStack> defaultedList, int i) {
        if (recipe == null || !AbstractFurnaceBlockEntity.canAcceptRecipeOutput(recipe, defaultedList, i)) {
            return false;
        }
        ItemStack itemStack = defaultedList.get(0);
        ItemStack itemStack2 = recipe.getOutput();
        ItemStack itemStack3 = defaultedList.get(2);
        if (itemStack3.isEmpty()) {
            defaultedList.set(2, itemStack2.copy());
        } else if (itemStack3.isOf(itemStack2.getItem())) {
            itemStack3.increment(1);
        }
        if (itemStack.isOf(Blocks.WET_SPONGE.asItem()) && !defaultedList.get(1).isEmpty() && defaultedList.get(1).isOf(Items.BUCKET)) {
            defaultedList.set(1, new ItemStack(Items.WATER_BUCKET));
        }
        itemStack.decrement(1);
        return true;
    }

    protected int getFuelTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        }
        Item item = fuel.getItem();
        return AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(item, 0);
    }

    private static int getCookTime(World world, RecipeType<? extends AbstractCookingRecipe> recipeType, Inventory inventory) {
        return world.getRecipeManager().getFirstMatch(recipeType, inventory, world).map(AbstractCookingRecipe::getCookTime).orElse(200);
    }

    public static boolean canUseAsFuel(ItemStack stack) {
        return AbstractFurnaceBlockEntity.createFuelTimeMap().containsKey(stack.getItem());
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) {
            return BOTTOM_SLOTS;
        }
        if (side == Direction.UP) {
            return TOP_SLOTS;
        }
        return SIDE_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.isValid(slot, stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (dir == Direction.DOWN && slot == 1) {
            return stack.isOf(Items.WATER_BUCKET) || stack.isOf(Items.BUCKET);
        }
        return true;
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.inventory) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        ItemStack itemStack = this.inventory.get(slot);
        boolean bl = !stack.isEmpty() && stack.isItemEqualIgnoreDamage(itemStack) && ItemStack.areTagsEqual(stack, itemStack);
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        if (slot == 0 && !bl) {
            this.cookTimeTotal = AbstractFurnaceBlockEntity.getCookTime(this.world, this.recipeType, this);
            this.cookTime = 0;
            this.markDirty();
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        }
        return player.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot == 2) {
            return false;
        }
        if (slot == 1) {
            ItemStack itemStack = this.inventory.get(1);
            return AbstractFurnaceBlockEntity.canUseAsFuel(stack) || stack.isOf(Items.BUCKET) && !itemStack.isOf(Items.BUCKET);
        }
        return true;
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    public void setLastRecipe(@Nullable Recipe<?> recipe) {
        if (recipe != null) {
            Identifier identifier = recipe.getId();
            this.recipesUsed.addTo(identifier, 1);
        }
    }

    @Override
    @Nullable
    public Recipe<?> getLastRecipe() {
        return null;
    }

    @Override
    public void unlockLastRecipe(PlayerEntity player) {
    }

    public void dropExperience(ServerPlayerEntity serverPlayerEntity) {
        List<Recipe<?>> list = this.method_27354(serverPlayerEntity.getServerWorld(), serverPlayerEntity.getPos());
        serverPlayerEntity.unlockRecipes(list);
        this.recipesUsed.clear();
    }

    public List<Recipe<?>> method_27354(ServerWorld serverWorld, Vec3d vec3d) {
        ArrayList<Recipe<?>> list = Lists.newArrayList();
        for (Object2IntMap.Entry entry : this.recipesUsed.object2IntEntrySet()) {
            serverWorld.getRecipeManager().get((Identifier)entry.getKey()).ifPresent(recipe -> {
                list.add((Recipe<?>)recipe);
                AbstractFurnaceBlockEntity.dropExperience(serverWorld, vec3d, entry.getIntValue(), ((AbstractCookingRecipe)recipe).getExperience());
            });
        }
        return list;
    }

    private static void dropExperience(ServerWorld serverWorld, Vec3d vec3d, int i, float f) {
        int j = MathHelper.floor((float)i * f);
        float g = MathHelper.fractionalPart((float)i * f);
        if (g != 0.0f && Math.random() < (double)g) {
            ++j;
        }
        ExperienceOrbEntity.method_31493(serverWorld, vec3d, j);
    }

    @Override
    public void provideRecipeInputs(RecipeFinder finder) {
        for (ItemStack itemStack : this.inventory) {
            finder.addItem(itemStack);
        }
    }
}

