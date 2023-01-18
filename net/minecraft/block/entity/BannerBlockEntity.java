/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BannerPatterns;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class BannerBlockEntity
extends BlockEntity
implements Nameable {
    public static final int MAX_PATTERN_COUNT = 6;
    public static final String PATTERNS_KEY = "Patterns";
    public static final String PATTERN_KEY = "Pattern";
    public static final String COLOR_KEY = "Color";
    @Nullable
    private Text customName;
    private DyeColor baseColor;
    @Nullable
    private NbtList patternListNbt;
    @Nullable
    private List<Pair<RegistryEntry<BannerPattern>, DyeColor>> patterns;

    public BannerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.BANNER, pos, state);
        this.baseColor = ((AbstractBannerBlock)state.getBlock()).getColor();
    }

    public BannerBlockEntity(BlockPos pos, BlockState state, DyeColor baseColor) {
        this(pos, state);
        this.baseColor = baseColor;
    }

    @Nullable
    public static NbtList getPatternListNbt(ItemStack stack) {
        NbtList nbtList = null;
        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
        if (nbtCompound != null && nbtCompound.contains(PATTERNS_KEY, NbtElement.LIST_TYPE)) {
            nbtList = nbtCompound.getList(PATTERNS_KEY, NbtElement.COMPOUND_TYPE).copy();
        }
        return nbtList;
    }

    public void readFrom(ItemStack stack, DyeColor baseColor) {
        this.baseColor = baseColor;
        this.readFrom(stack);
    }

    public void readFrom(ItemStack stack) {
        this.patternListNbt = BannerBlockEntity.getPatternListNbt(stack);
        this.patterns = null;
        this.customName = stack.hasCustomName() ? stack.getName() : null;
    }

    @Override
    public Text getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return Text.translatable("block.minecraft.banner");
    }

    @Override
    @Nullable
    public Text getCustomName() {
        return this.customName;
    }

    public void setCustomName(Text customName) {
        this.customName = customName;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.patternListNbt != null) {
            nbt.put(PATTERNS_KEY, this.patternListNbt);
        }
        if (this.customName != null) {
            nbt.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("CustomName", NbtElement.STRING_TYPE)) {
            this.customName = Text.Serializer.fromJson(nbt.getString("CustomName"));
        }
        this.patternListNbt = nbt.getList(PATTERNS_KEY, NbtElement.COMPOUND_TYPE);
        this.patterns = null;
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    public static int getPatternCount(ItemStack stack) {
        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
        if (nbtCompound != null && nbtCompound.contains(PATTERNS_KEY)) {
            return nbtCompound.getList(PATTERNS_KEY, NbtElement.COMPOUND_TYPE).size();
        }
        return 0;
    }

    public List<Pair<RegistryEntry<BannerPattern>, DyeColor>> getPatterns() {
        if (this.patterns == null) {
            this.patterns = BannerBlockEntity.getPatternsFromNbt(this.baseColor, this.patternListNbt);
        }
        return this.patterns;
    }

    public static List<Pair<RegistryEntry<BannerPattern>, DyeColor>> getPatternsFromNbt(DyeColor baseColor, @Nullable NbtList patternListNbt) {
        ArrayList<Pair<RegistryEntry<BannerPattern>, DyeColor>> list = Lists.newArrayList();
        list.add(Pair.of(Registries.BANNER_PATTERN.entryOf(BannerPatterns.BASE), baseColor));
        if (patternListNbt != null) {
            for (int i = 0; i < patternListNbt.size(); ++i) {
                NbtCompound nbtCompound = patternListNbt.getCompound(i);
                RegistryEntry<BannerPattern> registryEntry = BannerPattern.byId(nbtCompound.getString(PATTERN_KEY));
                if (registryEntry == null) continue;
                int j = nbtCompound.getInt(COLOR_KEY);
                list.add(Pair.of(registryEntry, DyeColor.byId(j)));
            }
        }
        return list;
    }

    public static void loadFromItemStack(ItemStack stack) {
        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
        if (nbtCompound == null || !nbtCompound.contains(PATTERNS_KEY, NbtElement.LIST_TYPE)) {
            return;
        }
        NbtList nbtList = nbtCompound.getList(PATTERNS_KEY, NbtElement.COMPOUND_TYPE);
        if (nbtList.isEmpty()) {
            return;
        }
        nbtList.remove(nbtList.size() - 1);
        if (nbtList.isEmpty()) {
            nbtCompound.remove(PATTERNS_KEY);
        }
        BlockItem.setBlockEntityNbt(stack, BlockEntityType.BANNER, nbtCompound);
    }

    public ItemStack getPickStack() {
        ItemStack itemStack = new ItemStack(BannerBlock.getForColor(this.baseColor));
        if (this.patternListNbt != null && !this.patternListNbt.isEmpty()) {
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.put(PATTERNS_KEY, this.patternListNbt.copy());
            BlockItem.setBlockEntityNbt(itemStack, this.getType(), nbtCompound);
        }
        if (this.customName != null) {
            itemStack.setCustomName(this.customName);
        }
        return itemStack;
    }

    public DyeColor getColorForState() {
        return this.baseColor;
    }

    public /* synthetic */ Packet toUpdatePacket() {
        return this.toUpdatePacket();
    }
}

