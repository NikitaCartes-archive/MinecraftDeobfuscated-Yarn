/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
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
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerInventory playerInventory) {
        if (clickType != ClickType.RIGHT) {
            return false;
        }
        ItemStack itemStack = slot.getStack();
        if (itemStack.isEmpty()) {
            BundleItem.method_32759(stack).ifPresent(itemStack2 -> BundleItem.addToBundle(stack, slot.method_32756((ItemStack)itemStack2)));
        } else if (itemStack.getItem().hasStoredInventory()) {
            int i = (64 - BundleItem.getBundleOccupancy(stack)) / BundleItem.getItemOccupancy(itemStack);
            BundleItem.addToBundle(stack, slot.method_32753(itemStack.getCount(), i, playerInventory.player));
        }
        return true;
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerInventory playerInventory) {
        if (clickType != ClickType.RIGHT || !slot.method_32754(playerInventory.player)) {
            return false;
        }
        if (otherStack.isEmpty()) {
            BundleItem.method_32759(stack).ifPresent(playerInventory::setCursorStack);
        } else {
            otherStack.decrement(BundleItem.addToBundle(stack, otherStack));
        }
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (BundleItem.dropAllBundledItems(itemStack, user)) {
            return TypedActionResult.success(itemStack, world.isClient());
        }
        return TypedActionResult.fail(itemStack);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean isItemBarVisible(ItemStack stack) {
        return BundleItem.getBundleOccupancy(stack) > 0;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getItemBarStep(ItemStack stack) {
        return Math.min(1 + 12 * BundleItem.getBundleOccupancy(stack) / 64, 13);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getItemBarColor(ItemStack stack) {
        return ITEM_BAR_COLOR;
    }

    private static int addToBundle(ItemStack bundle, ItemStack stack) {
        if (stack.isEmpty() || !stack.getItem().hasStoredInventory()) {
            return 0;
        }
        CompoundTag compoundTag = bundle.getOrCreateTag();
        if (!compoundTag.contains("Items")) {
            compoundTag.put("Items", new ListTag());
        }
        int i = BundleItem.getBundleOccupancy(bundle);
        int j = BundleItem.getItemOccupancy(stack);
        int k = Math.min(stack.getCount(), (64 - i) / j);
        if (k == 0) {
            return 0;
        }
        ListTag listTag = compoundTag.getList("Items", 10);
        Optional<CompoundTag> optional = BundleItem.method_32344(stack, listTag);
        if (optional.isPresent()) {
            CompoundTag compoundTag2 = optional.get();
            ItemStack itemStack = ItemStack.fromTag(compoundTag2);
            itemStack.increment(k);
            itemStack.toTag(compoundTag2);
            listTag.remove(compoundTag2);
            listTag.add(0, compoundTag2);
        } else {
            ItemStack itemStack2 = stack.copy();
            itemStack2.setCount(k);
            CompoundTag compoundTag3 = new CompoundTag();
            itemStack2.toTag(compoundTag3);
            listTag.add(0, compoundTag3);
        }
        return k;
    }

    private static Optional<CompoundTag> method_32344(ItemStack itemStack, ListTag listTag) {
        if (itemStack.isOf(Items.BUNDLE)) {
            return Optional.empty();
        }
        return listTag.stream().filter(CompoundTag.class::isInstance).map(CompoundTag.class::cast).filter(compoundTag -> ItemStack.canCombine(ItemStack.fromTag(compoundTag), itemStack)).findFirst();
    }

    private static int getItemOccupancy(ItemStack stack) {
        if (stack.isOf(Items.BUNDLE)) {
            return 4 + BundleItem.getBundleOccupancy(stack);
        }
        return 64 / stack.getMaxCount();
    }

    private static int getBundleOccupancy(ItemStack stack) {
        return BundleItem.getBundledStacks(stack).mapToInt(itemStack -> BundleItem.getItemOccupancy(itemStack) * itemStack.getCount()).sum();
    }

    private static Optional<ItemStack> method_32759(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getOrCreateTag();
        if (!compoundTag.contains("Items")) {
            return Optional.empty();
        }
        ListTag listTag = compoundTag.getList("Items", 10);
        if (listTag.isEmpty()) {
            return Optional.empty();
        }
        boolean i = false;
        CompoundTag compoundTag2 = listTag.getCompound(0);
        ItemStack itemStack2 = ItemStack.fromTag(compoundTag2);
        listTag.remove(0);
        return Optional.of(itemStack2);
    }

    private static boolean dropAllBundledItems(ItemStack stack, PlayerEntity player) {
        CompoundTag compoundTag = stack.getOrCreateTag();
        if (!compoundTag.contains("Items")) {
            return false;
        }
        if (player instanceof ServerPlayerEntity) {
            ListTag listTag = compoundTag.getList("Items", 10);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag compoundTag2 = listTag.getCompound(i);
                ItemStack itemStack = ItemStack.fromTag(compoundTag2);
                player.dropItem(itemStack, true);
            }
        }
        stack.removeSubTag("Items");
        return true;
    }

    private static Stream<ItemStack> getBundledStacks(ItemStack stack) {
        CompoundTag compoundTag = stack.getTag();
        if (compoundTag == null) {
            return Stream.empty();
        }
        ListTag listTag = compoundTag.getList("Items", 10);
        return listTag.stream().map(CompoundTag.class::cast).map(ItemStack::fromTag);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();
        BundleItem.getBundledStacks(stack).forEach(defaultedList::add);
        return Optional.of(new BundleTooltipData(defaultedList, BundleItem.getBundleOccupancy(stack)));
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText("item.minecraft.bundle.fullness", BundleItem.getBundleOccupancy(stack), 64).formatted(Formatting.GRAY));
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        ItemUsage.spawnItemContents(entity, BundleItem.getBundledStacks(entity.getStack()));
    }
}

