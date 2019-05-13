/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
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
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractFurnaceBlockEntity
extends LockableContainerBlockEntity
implements SidedInventory,
RecipeUnlocker,
RecipeInputProvider,
Tickable {
    private static final int[] TOP_SLOTS = new int[]{0};
    private static final int[] BOTTOM_SLOTS = new int[]{2, 1};
    private static final int[] SIDE_SLOTS = new int[]{1};
    protected DefaultedList<ItemStack> inventory = DefaultedList.create(3, ItemStack.EMPTY);
    private int burnTime;
    private int fuelTime;
    private int cookTime;
    private int cookTimeTotal;
    protected final PropertyDelegate propertyDelegate = new PropertyDelegate(){

        @Override
        public int get(int i) {
            switch (i) {
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
        public void set(int i, int j) {
            switch (i) {
                case 0: {
                    AbstractFurnaceBlockEntity.this.burnTime = j;
                    break;
                }
                case 1: {
                    AbstractFurnaceBlockEntity.this.fuelTime = j;
                    break;
                }
                case 2: {
                    AbstractFurnaceBlockEntity.this.cookTime = j;
                    break;
                }
                case 3: {
                    AbstractFurnaceBlockEntity.this.cookTimeTotal = j;
                    break;
                }
            }
        }

        @Override
        public int size() {
            return 4;
        }
    };
    private final Map<Identifier, Integer> recipesUsed = Maps.newHashMap();
    protected final RecipeType<? extends AbstractCookingRecipe> recipeType;

    protected AbstractFurnaceBlockEntity(BlockEntityType<?> blockEntityType, RecipeType<? extends AbstractCookingRecipe> recipeType) {
        super(blockEntityType);
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
        AbstractFurnaceBlockEntity.addFuel(map, ItemTags.BOATS, 200);
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
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.SCAFFOLDING, 50);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.LOOM, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.BARREL, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.CARTOGRAPHY_TABLE, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.FLETCHING_TABLE, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.SMITHING_TABLE, 300);
        AbstractFurnaceBlockEntity.addFuel(map, Blocks.COMPOSTER, 300);
        return map;
    }

    private static void addFuel(Map<Item, Integer> map, Tag<Item> tag, int i) {
        for (Item item : tag.values()) {
            map.put(item, i);
        }
    }

    private static void addFuel(Map<Item, Integer> map, ItemConvertible itemConvertible, int i) {
        map.put(itemConvertible.asItem(), i);
    }

    private boolean isBurning() {
        return this.burnTime > 0;
    }

    @Override
    public void fromTag(CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.inventory = DefaultedList.create(this.getInvSize(), ItemStack.EMPTY);
        Inventories.fromTag(compoundTag, this.inventory);
        this.burnTime = compoundTag.getShort("BurnTime");
        this.cookTime = compoundTag.getShort("CookTime");
        this.cookTimeTotal = compoundTag.getShort("CookTimeTotal");
        this.fuelTime = this.getFuelTime(this.inventory.get(1));
        int i = compoundTag.getShort("RecipesUsedSize");
        for (int j = 0; j < i; ++j) {
            Identifier identifier = new Identifier(compoundTag.getString("RecipeLocation" + j));
            int k = compoundTag.getInt("RecipeAmount" + j);
            this.recipesUsed.put(identifier, k);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        super.toTag(compoundTag);
        compoundTag.putShort("BurnTime", (short)this.burnTime);
        compoundTag.putShort("CookTime", (short)this.cookTime);
        compoundTag.putShort("CookTimeTotal", (short)this.cookTimeTotal);
        Inventories.toTag(compoundTag, this.inventory);
        compoundTag.putShort("RecipesUsedSize", (short)this.recipesUsed.size());
        int i = 0;
        for (Map.Entry<Identifier, Integer> entry : this.recipesUsed.entrySet()) {
            compoundTag.putString("RecipeLocation" + i, entry.getKey().toString());
            compoundTag.putInt("RecipeAmount" + i, entry.getValue());
            ++i;
        }
        return compoundTag;
    }

    @Override
    public void tick() {
        boolean bl = this.isBurning();
        boolean bl2 = false;
        if (this.isBurning()) {
            --this.burnTime;
        }
        if (!this.world.isClient) {
            ItemStack itemStack = this.inventory.get(1);
            if (this.isBurning() || !itemStack.isEmpty() && !this.inventory.get(0).isEmpty()) {
                Recipe recipe = this.world.getRecipeManager().getFirstMatch(this.recipeType, this, this.world).orElse(null);
                if (!this.isBurning() && this.canAcceptRecipeOutput(recipe)) {
                    this.fuelTime = this.burnTime = this.getFuelTime(itemStack);
                    if (this.isBurning()) {
                        bl2 = true;
                        if (!itemStack.isEmpty()) {
                            Item item = itemStack.getItem();
                            itemStack.subtractAmount(1);
                            if (itemStack.isEmpty()) {
                                Item item2 = item.getRecipeRemainder();
                                this.inventory.set(1, item2 == null ? ItemStack.EMPTY : new ItemStack(item2));
                            }
                        }
                    }
                }
                if (this.isBurning() && this.canAcceptRecipeOutput(recipe)) {
                    ++this.cookTime;
                    if (this.cookTime == this.cookTimeTotal) {
                        this.cookTime = 0;
                        this.cookTimeTotal = this.getCookTime();
                        this.craftRecipe(recipe);
                        bl2 = true;
                    }
                } else {
                    this.cookTime = 0;
                }
            } else if (!this.isBurning() && this.cookTime > 0) {
                this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal);
            }
            if (bl != this.isBurning()) {
                bl2 = true;
                this.world.setBlockState(this.pos, (BlockState)this.world.getBlockState(this.pos).with(AbstractFurnaceBlock.LIT, this.isBurning()), 3);
            }
        }
        if (bl2) {
            this.markDirty();
        }
    }

    protected boolean canAcceptRecipeOutput(@Nullable Recipe<?> recipe) {
        if (this.inventory.get(0).isEmpty() || recipe == null) {
            return false;
        }
        ItemStack itemStack = recipe.getOutput();
        if (itemStack.isEmpty()) {
            return false;
        }
        ItemStack itemStack2 = this.inventory.get(2);
        if (itemStack2.isEmpty()) {
            return true;
        }
        if (!itemStack2.isEqualIgnoreTags(itemStack)) {
            return false;
        }
        if (itemStack2.getAmount() < this.getInvMaxStackAmount() && itemStack2.getAmount() < itemStack2.getMaxAmount()) {
            return true;
        }
        return itemStack2.getAmount() < itemStack.getMaxAmount();
    }

    private void craftRecipe(@Nullable Recipe<?> recipe) {
        if (recipe == null || !this.canAcceptRecipeOutput(recipe)) {
            return;
        }
        ItemStack itemStack = this.inventory.get(0);
        ItemStack itemStack2 = recipe.getOutput();
        ItemStack itemStack3 = this.inventory.get(2);
        if (itemStack3.isEmpty()) {
            this.inventory.set(2, itemStack2.copy());
        } else if (itemStack3.getItem() == itemStack2.getItem()) {
            itemStack3.addAmount(1);
        }
        if (!this.world.isClient) {
            this.setLastRecipe(recipe);
        }
        if (itemStack.getItem() == Blocks.WET_SPONGE.asItem() && !this.inventory.get(1).isEmpty() && this.inventory.get(1).getItem() == Items.BUCKET) {
            this.inventory.set(1, new ItemStack(Items.WATER_BUCKET));
        }
        itemStack.subtractAmount(1);
    }

    protected int getFuelTime(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return 0;
        }
        Item item = itemStack.getItem();
        return AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(item, 0);
    }

    protected int getCookTime() {
        return this.world.getRecipeManager().getFirstMatch(this.recipeType, this, this.world).map(AbstractCookingRecipe::getCookTime).orElse(200);
    }

    public static boolean canUseAsFuel(ItemStack itemStack) {
        return AbstractFurnaceBlockEntity.createFuelTimeMap().containsKey(itemStack.getItem());
    }

    @Override
    public int[] getInvAvailableSlots(Direction direction) {
        if (direction == Direction.DOWN) {
            return BOTTOM_SLOTS;
        }
        if (direction == Direction.UP) {
            return TOP_SLOTS;
        }
        return SIDE_SLOTS;
    }

    @Override
    public boolean canInsertInvStack(int i, ItemStack itemStack, @Nullable Direction direction) {
        return this.isValidInvStack(i, itemStack);
    }

    @Override
    public boolean canExtractInvStack(int i, ItemStack itemStack, Direction direction) {
        Item item;
        return direction != Direction.DOWN || i != 1 || (item = itemStack.getItem()) == Items.WATER_BUCKET || item == Items.BUCKET;
    }

    @Override
    public int getInvSize() {
        return this.inventory.size();
    }

    @Override
    public boolean isInvEmpty() {
        for (ItemStack itemStack : this.inventory) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getInvStack(int i) {
        return this.inventory.get(i);
    }

    @Override
    public ItemStack takeInvStack(int i, int j) {
        return Inventories.splitStack(this.inventory, i, j);
    }

    @Override
    public ItemStack removeInvStack(int i) {
        return Inventories.removeStack(this.inventory, i);
    }

    @Override
    public void setInvStack(int i, ItemStack itemStack) {
        ItemStack itemStack2 = this.inventory.get(i);
        boolean bl = !itemStack.isEmpty() && itemStack.isEqualIgnoreTags(itemStack2) && ItemStack.areTagsEqual(itemStack, itemStack2);
        this.inventory.set(i, itemStack);
        if (itemStack.getAmount() > this.getInvMaxStackAmount()) {
            itemStack.setAmount(this.getInvMaxStackAmount());
        }
        if (i == 0 && !bl) {
            this.cookTimeTotal = this.getCookTime();
            this.cookTime = 0;
            this.markDirty();
        }
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity playerEntity) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        }
        return playerEntity.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public boolean isValidInvStack(int i, ItemStack itemStack) {
        if (i == 2) {
            return false;
        }
        if (i == 1) {
            ItemStack itemStack2 = this.inventory.get(1);
            return AbstractFurnaceBlockEntity.canUseAsFuel(itemStack) || itemStack.getItem() == Items.BUCKET && itemStack2.getItem() != Items.BUCKET;
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
            this.recipesUsed.compute(recipe.getId(), (identifier, integer) -> 1 + (integer == null ? 0 : integer));
        }
    }

    @Override
    @Nullable
    public Recipe<?> getLastRecipe() {
        return null;
    }

    @Override
    public void unlockLastRecipe(PlayerEntity playerEntity) {
    }

    public void dropExperience(PlayerEntity playerEntity) {
        ArrayList<Recipe<?>> list = Lists.newArrayList();
        for (Map.Entry<Identifier, Integer> entry : this.recipesUsed.entrySet()) {
            playerEntity.world.getRecipeManager().get(entry.getKey()).ifPresent(recipe -> {
                list.add((Recipe<?>)recipe);
                AbstractFurnaceBlockEntity.dropExperience(playerEntity, (Integer)entry.getValue(), ((AbstractCookingRecipe)recipe).getExperience());
            });
        }
        playerEntity.unlockRecipes(list);
        this.recipesUsed.clear();
    }

    private static void dropExperience(PlayerEntity playerEntity, int i, float f) {
        int j;
        if (f == 0.0f) {
            i = 0;
        } else if (f < 1.0f) {
            j = MathHelper.floor((float)i * f);
            if (j < MathHelper.ceil((float)i * f) && Math.random() < (double)((float)i * f - (float)j)) {
                ++j;
            }
            i = j;
        }
        while (i > 0) {
            j = ExperienceOrbEntity.roundToOrbSize(i);
            i -= j;
            playerEntity.world.spawnEntity(new ExperienceOrbEntity(playerEntity.world, playerEntity.x, playerEntity.y + 0.5, playerEntity.z + 0.5, j));
        }
    }

    @Override
    public void provideRecipeInputs(RecipeFinder recipeFinder) {
        for (ItemStack itemStack : this.inventory) {
            recipeFinder.addItem(itemStack);
        }
    }
}

