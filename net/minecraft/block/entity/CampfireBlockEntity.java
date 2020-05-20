/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import java.util.Optional;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Clearable;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CampfireBlockEntity
extends BlockEntity
implements Clearable,
Tickable {
    private final DefaultedList<ItemStack> itemsBeingCooked = DefaultedList.ofSize(4, ItemStack.EMPTY);
    private final int[] cookingTimes = new int[4];
    private final int[] cookingTotalTimes = new int[4];

    public CampfireBlockEntity() {
        super(BlockEntityType.CAMPFIRE);
    }

    @Override
    public void tick() {
        boolean bl = this.getCachedState().get(CampfireBlock.LIT);
        boolean bl2 = this.world.isClient;
        if (bl2) {
            if (bl) {
                this.spawnSmokeParticles();
            }
            return;
        }
        if (bl) {
            this.updateItemsBeingCooked();
        } else {
            for (int i = 0; i < this.itemsBeingCooked.size(); ++i) {
                if (this.cookingTimes[i] <= 0) continue;
                this.cookingTimes[i] = MathHelper.clamp(this.cookingTimes[i] - 2, 0, this.cookingTotalTimes[i]);
            }
        }
    }

    private void updateItemsBeingCooked() {
        for (int i = 0; i < this.itemsBeingCooked.size(); ++i) {
            ItemStack itemStack = this.itemsBeingCooked.get(i);
            if (itemStack.isEmpty()) continue;
            int n = i;
            this.cookingTimes[n] = this.cookingTimes[n] + 1;
            if (this.cookingTimes[i] < this.cookingTotalTimes[i]) continue;
            SimpleInventory inventory = new SimpleInventory(itemStack);
            ItemStack itemStack2 = this.world.getRecipeManager().getFirstMatch(RecipeType.CAMPFIRE_COOKING, inventory, this.world).map(campfireCookingRecipe -> campfireCookingRecipe.craft(inventory)).orElse(itemStack);
            BlockPos blockPos = this.getPos();
            ItemScatterer.spawn(this.world, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), itemStack2);
            this.itemsBeingCooked.set(i, ItemStack.EMPTY);
            this.updateListeners();
        }
    }

    private void spawnSmokeParticles() {
        int i;
        World world = this.getWorld();
        if (world == null) {
            return;
        }
        BlockPos blockPos = this.getPos();
        Random random = world.random;
        if (random.nextFloat() < 0.11f) {
            for (i = 0; i < random.nextInt(2) + 2; ++i) {
                CampfireBlock.spawnSmokeParticle(world, blockPos, this.getCachedState().get(CampfireBlock.SIGNAL_FIRE), false);
            }
        }
        i = this.getCachedState().get(CampfireBlock.FACING).getHorizontal();
        for (int j = 0; j < this.itemsBeingCooked.size(); ++j) {
            if (this.itemsBeingCooked.get(j).isEmpty() || !(random.nextFloat() < 0.2f)) continue;
            Direction direction = Direction.fromHorizontal(Math.floorMod(j + i, 4));
            float f = 0.3125f;
            double d = (double)blockPos.getX() + 0.5 - (double)((float)direction.getOffsetX() * 0.3125f) + (double)((float)direction.rotateYClockwise().getOffsetX() * 0.3125f);
            double e = (double)blockPos.getY() + 0.5;
            double g = (double)blockPos.getZ() + 0.5 - (double)((float)direction.getOffsetZ() * 0.3125f) + (double)((float)direction.rotateYClockwise().getOffsetZ() * 0.3125f);
            for (int k = 0; k < 4; ++k) {
                world.addParticle(ParticleTypes.SMOKE, d, e, g, 0.0, 5.0E-4, 0.0);
            }
        }
    }

    public DefaultedList<ItemStack> getItemsBeingCooked() {
        return this.itemsBeingCooked;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        int[] is;
        super.fromTag(state, tag);
        this.itemsBeingCooked.clear();
        Inventories.fromTag(tag, this.itemsBeingCooked);
        if (tag.contains("CookingTimes", 11)) {
            is = tag.getIntArray("CookingTimes");
            System.arraycopy(is, 0, this.cookingTimes, 0, Math.min(this.cookingTotalTimes.length, is.length));
        }
        if (tag.contains("CookingTotalTimes", 11)) {
            is = tag.getIntArray("CookingTotalTimes");
            System.arraycopy(is, 0, this.cookingTotalTimes, 0, Math.min(this.cookingTotalTimes.length, is.length));
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        this.saveInitialChunkData(tag);
        tag.putIntArray("CookingTimes", this.cookingTimes);
        tag.putIntArray("CookingTotalTimes", this.cookingTotalTimes);
        return tag;
    }

    private CompoundTag saveInitialChunkData(CompoundTag tag) {
        super.toTag(tag);
        Inventories.toTag(tag, this.itemsBeingCooked, true);
        return tag;
    }

    @Override
    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 13, this.toInitialChunkDataTag());
    }

    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.saveInitialChunkData(new CompoundTag());
    }

    public Optional<CampfireCookingRecipe> getRecipeFor(ItemStack item) {
        if (this.itemsBeingCooked.stream().noneMatch(ItemStack::isEmpty)) {
            return Optional.empty();
        }
        return this.world.getRecipeManager().getFirstMatch(RecipeType.CAMPFIRE_COOKING, new SimpleInventory(item), this.world);
    }

    public boolean addItem(ItemStack item, int integer) {
        for (int i = 0; i < this.itemsBeingCooked.size(); ++i) {
            ItemStack itemStack = this.itemsBeingCooked.get(i);
            if (!itemStack.isEmpty()) continue;
            this.cookingTotalTimes[i] = integer;
            this.cookingTimes[i] = 0;
            this.itemsBeingCooked.set(i, item.split(1));
            this.updateListeners();
            return true;
        }
        return false;
    }

    private void updateListeners() {
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
    }

    @Override
    public void clear() {
        this.itemsBeingCooked.clear();
    }

    public void spawnItemsBeingCooked() {
        if (this.world != null) {
            if (!this.world.isClient) {
                ItemScatterer.spawn(this.world, this.getPos(), this.getItemsBeingCooked());
            }
            this.updateListeners();
        }
    }
}

