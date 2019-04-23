/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FishBucketItem
extends BucketItem {
    private final EntityType<?> fishType;

    public FishBucketItem(EntityType<?> entityType, Fluid fluid, Item.Settings settings) {
        super(fluid, settings);
        this.fishType = entityType;
    }

    @Override
    public void onEmptied(World world, ItemStack itemStack, BlockPos blockPos) {
        if (!world.isClient) {
            this.spawnFish(world, itemStack, blockPos);
        }
    }

    @Override
    protected void playEmptyingSound(@Nullable PlayerEntity playerEntity, IWorld iWorld, BlockPos blockPos) {
        iWorld.playSound(playerEntity, blockPos, SoundEvents.ITEM_BUCKET_EMPTY_FISH, SoundCategory.NEUTRAL, 1.0f, 1.0f);
    }

    private void spawnFish(World world, ItemStack itemStack, BlockPos blockPos) {
        Entity entity = this.fishType.spawnFromItemStack(world, itemStack, null, blockPos, SpawnType.BUCKET, true, false);
        if (entity != null) {
            ((FishEntity)entity).setFromBucket(true);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void buildTooltip(ItemStack itemStack, @Nullable World world, List<Component> list, TooltipContext tooltipContext) {
        CompoundTag compoundTag;
        if (this.fishType == EntityType.TROPICAL_FISH && (compoundTag = itemStack.getTag()) != null && compoundTag.containsKey("BucketVariantTag", 3)) {
            int i = compoundTag.getInt("BucketVariantTag");
            ChatFormat[] chatFormats = new ChatFormat[]{ChatFormat.ITALIC, ChatFormat.GRAY};
            String string = "color.minecraft." + TropicalFishEntity.getBaseDyeColor(i);
            String string2 = "color.minecraft." + TropicalFishEntity.getPatternDyeColor(i);
            for (int j = 0; j < TropicalFishEntity.COMMON_VARIANTS.length; ++j) {
                if (i != TropicalFishEntity.COMMON_VARIANTS[j]) continue;
                list.add(new TranslatableComponent(TropicalFishEntity.getToolTipForVariant(j), new Object[0]).applyFormat(chatFormats));
                return;
            }
            list.add(new TranslatableComponent(TropicalFishEntity.getTranslationKey(i), new Object[0]).applyFormat(chatFormats));
            TranslatableComponent component = new TranslatableComponent(string, new Object[0]);
            if (!string.equals(string2)) {
                component.append(", ").append(new TranslatableComponent(string2, new Object[0]));
            }
            component.applyFormat(chatFormats);
            list.add(component);
        }
    }
}

