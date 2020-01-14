/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Nameable;
import org.jetbrains.annotations.Nullable;

public class BannerBlockEntity
extends BlockEntity
implements Nameable {
    @Nullable
    private Text customName;
    @Nullable
    private DyeColor baseColor = DyeColor.WHITE;
    @Nullable
    private ListTag patternListTag;
    private boolean patternListTagRead;
    @Nullable
    private List<Pair<BannerPattern, DyeColor>> patterns;

    public BannerBlockEntity() {
        super(BlockEntityType.BANNER);
    }

    public BannerBlockEntity(DyeColor baseColor) {
        this();
        this.baseColor = baseColor;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static ListTag method_24281(ItemStack itemStack) {
        ListTag listTag = null;
        CompoundTag compoundTag = itemStack.getSubTag("BlockEntityTag");
        if (compoundTag != null && compoundTag.contains("Patterns", 9)) {
            listTag = compoundTag.getList("Patterns", 10).copy();
        }
        return listTag;
    }

    @Environment(value=EnvType.CLIENT)
    public void readFrom(ItemStack stack, DyeColor baseColor) {
        this.patternListTag = BannerBlockEntity.method_24281(stack);
        this.baseColor = baseColor;
        this.patterns = null;
        this.patternListTagRead = true;
        this.customName = stack.hasCustomName() ? stack.getName() : null;
    }

    @Override
    public Text getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return new TranslatableText("block.minecraft.banner", new Object[0]);
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
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (this.patternListTag != null) {
            tag.put("Patterns", this.patternListTag);
        }
        if (this.customName != null) {
            tag.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        if (tag.contains("CustomName", 8)) {
            this.customName = Text.Serializer.fromJson(tag.getString("CustomName"));
        }
        this.baseColor = this.hasWorld() ? ((AbstractBannerBlock)this.getCachedState().getBlock()).getColor() : null;
        this.patternListTag = tag.getList("Patterns", 10);
        this.patterns = null;
        this.patternListTagRead = true;
    }

    @Override
    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 6, this.toInitialChunkDataTag());
    }

    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }

    public static int getPatternCount(ItemStack stack) {
        CompoundTag compoundTag = stack.getSubTag("BlockEntityTag");
        if (compoundTag != null && compoundTag.contains("Patterns")) {
            return compoundTag.getList("Patterns", 10).size();
        }
        return 0;
    }

    @Environment(value=EnvType.CLIENT)
    public List<Pair<BannerPattern, DyeColor>> getPatterns() {
        if (this.patterns == null && this.patternListTagRead) {
            this.patterns = BannerBlockEntity.method_24280(this.getColorForState(this::getCachedState), this.patternListTag);
        }
        return this.patterns;
    }

    @Environment(value=EnvType.CLIENT)
    public static List<Pair<BannerPattern, DyeColor>> method_24280(DyeColor dyeColor, @Nullable ListTag listTag) {
        ArrayList<Pair<BannerPattern, DyeColor>> list = Lists.newArrayList();
        list.add(Pair.of(BannerPattern.BASE, dyeColor));
        if (listTag != null) {
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag compoundTag = listTag.getCompound(i);
                BannerPattern bannerPattern = BannerPattern.byId(compoundTag.getString("Pattern"));
                if (bannerPattern == null) continue;
                int j = compoundTag.getInt("Color");
                list.add(Pair.of(bannerPattern, DyeColor.byId(j)));
            }
        }
        return list;
    }

    public static void loadFromItemStack(ItemStack stack) {
        CompoundTag compoundTag = stack.getSubTag("BlockEntityTag");
        if (compoundTag == null || !compoundTag.contains("Patterns", 9)) {
            return;
        }
        ListTag listTag = compoundTag.getList("Patterns", 10);
        if (listTag.isEmpty()) {
            return;
        }
        listTag.remove(listTag.size() - 1);
        if (listTag.isEmpty()) {
            stack.removeSubTag("BlockEntityTag");
        }
    }

    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockState blockState) {
        ItemStack itemStack = new ItemStack(BannerBlock.getForColor(this.getColorForState(() -> blockState)));
        if (this.patternListTag != null && !this.patternListTag.isEmpty()) {
            itemStack.getOrCreateSubTag("BlockEntityTag").put("Patterns", this.patternListTag.copy());
        }
        if (this.customName != null) {
            itemStack.setCustomName(this.customName);
        }
        return itemStack;
    }

    public DyeColor getColorForState(Supplier<BlockState> supplier) {
        if (this.baseColor == null) {
            this.baseColor = ((AbstractBannerBlock)supplier.get().getBlock()).getColor();
        }
        return this.baseColor;
    }
}

