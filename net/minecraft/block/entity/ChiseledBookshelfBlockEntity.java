/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Objects;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Clearable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public class ChiseledBookshelfBlockEntity
extends BlockEntity
implements Clearable {
    public static final int MAX_BOOKS = 6;
    private final Deque<ItemStack> books = new ArrayDeque<ItemStack>(6);

    public ChiseledBookshelfBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.CHISELED_BOOKSHELF, pos, state);
    }

    public ItemStack getLastBook() {
        return Objects.requireNonNullElse(this.books.poll(), ItemStack.EMPTY);
    }

    public void addBook(ItemStack stack) {
        if (stack.isIn(ItemTags.BOOKSHELF_BOOKS)) {
            this.books.addFirst(stack);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(6, ItemStack.EMPTY);
        Inventories.readNbt(nbt, defaultedList);
        this.books.clear();
        for (ItemStack itemStack : defaultedList) {
            if (!itemStack.isIn(ItemTags.BOOKSHELF_BOOKS)) continue;
            this.books.add(itemStack);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, ChiseledBookshelfBlockEntity.getBooksAsList(this.books), true);
    }

    @NotNull
    private static DefaultedList<ItemStack> getBooksAsList(Collection<ItemStack> books) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(books.size());
        defaultedList.addAll(books);
        return defaultedList;
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        Inventories.writeNbt(nbtCompound, ChiseledBookshelfBlockEntity.getBooksAsList(this.books), true);
        return nbtCompound;
    }

    @Override
    public void clear() {
        this.books.clear();
    }

    public int getBookCount() {
        return this.books.size();
    }

    public boolean isFull() {
        return this.getBookCount() == 6;
    }

    public boolean isEmpty() {
        return this.books.isEmpty();
    }

    public /* synthetic */ Packet toUpdatePacket() {
        return this.toUpdatePacket();
    }
}

