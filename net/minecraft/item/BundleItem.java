/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.Optional;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BundleItem
extends Item {
    private static final int ITEM_BAR_COLOR = MathHelper.packRgb(0.4f, 0.4f, 1.0f);

    public BundleItem(Item.Settings settings) {
        super(settings);
    }

    @Environment(value=EnvType.CLIENT)
    public static float getAmountFilled(ItemStack stack) {
        return (float)BundleItem.getBundleOccupancy(stack) / 64.0f;
    }

    @Override
    public boolean onStackClicked(ItemStack stack, ItemStack otherStack, ClickType clickType, PlayerInventory playerInventory) {
        if (clickType == ClickType.RIGHT) {
            BundleItem.addToBundle(stack, otherStack);
            return true;
        }
        return super.onStackClicked(stack, otherStack, clickType, playerInventory);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, ClickType clickType, PlayerInventory playerInventory) {
        if (clickType == ClickType.RIGHT) {
            if (otherStack.isEmpty()) {
                BundleItem.emptyBundle(stack, playerInventory);
            } else {
                BundleItem.addToBundle(stack, otherStack);
            }
            return true;
        }
        return super.onClicked(stack, otherStack, clickType, playerInventory);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        BundleItem.emptyBundle(itemStack, user.getInventory());
        return TypedActionResult.success(itemStack);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean isItemBarVisible(ItemStack stack) {
        int i = BundleItem.getBundleOccupancy(stack);
        return i > 0 && i < 64;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getItemBarStep(ItemStack stack) {
        return 13 * BundleItem.getBundleOccupancy(stack) / 64 + 1;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getItemBarColor(ItemStack stack) {
        return ITEM_BAR_COLOR;
    }

    private static void addToBundle(ItemStack bundle, ItemStack stack) {
        if (!stack.getItem().hasStoredInventory()) {
            return;
        }
        CompoundTag compoundTag = bundle.getOrCreateTag();
        if (!compoundTag.contains("Items")) {
            compoundTag.put("Items", new ListTag());
        }
        int i = BundleItem.getBundleOccupancy(bundle);
        int j = BundleItem.getItemOccupancy(stack);
        int k = Math.min(stack.getCount(), (64 - i) / j);
        if (k == 0) {
            return;
        }
        ListTag listTag = compoundTag.getList("Items", 10);
        Optional<CompoundTag> optional = BundleItem.method_32344(stack, listTag);
        if (optional.isPresent()) {
            CompoundTag compoundTag2 = optional.get();
            ItemStack itemStack = ItemStack.fromTag(compoundTag2);
            itemStack.increment(k);
            itemStack.toTag(compoundTag2);
        } else {
            ItemStack itemStack2 = stack.copy();
            itemStack2.setCount(k);
            CompoundTag compoundTag3 = new CompoundTag();
            itemStack2.toTag(compoundTag3);
            listTag.add(compoundTag3);
        }
        stack.decrement(k);
    }

    private static Optional<CompoundTag> method_32344(ItemStack itemStack, ListTag listTag) {
        if (itemStack.isOf(Items.BUNDLE)) {
            return Optional.empty();
        }
        return listTag.stream().filter(CompoundTag.class::isInstance).map(CompoundTag.class::cast).filter(compoundTag -> ItemStack.method_31577(ItemStack.fromTag(compoundTag), itemStack)).findFirst();
    }

    private static int getItemOccupancy(ItemStack stack) {
        if (stack.isOf(Items.BUNDLE)) {
            return 4 + BundleItem.getBundleOccupancy(stack);
        }
        return 64 / stack.getMaxCount();
    }

    private static int getBundleOccupancy(ItemStack stack) {
        return BundleItem.method_32345(stack).mapToInt(itemStack -> BundleItem.getItemOccupancy(itemStack) * itemStack.getCount()).sum();
    }

    private static void emptyBundle(ItemStack stack2, PlayerInventory playerInventory) {
        BundleItem.method_32345(stack2).forEach(stack -> {
            if (playerInventory.player instanceof ServerPlayerEntity || playerInventory.player.isCreative()) {
                playerInventory.offerOrDrop((ItemStack)stack);
            }
        });
        stack2.removeSubTag("Items");
    }

    private static Stream<ItemStack> method_32345(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getTag();
        if (compoundTag == null) {
            return Stream.empty();
        }
        ListTag listTag = compoundTag.getList("Items", 10);
        return listTag.stream().map(tag -> ItemStack.fromTag((CompoundTag)tag));
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();
        BundleItem.method_32345(stack).forEach(defaultedList::add);
        return Optional.of(new BundleTooltipData(defaultedList, BundleItem.getBundleOccupancy(stack) < 64));
    }
}

