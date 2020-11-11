package net.minecraft.item;

import java.util.Optional;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BundleItem extends Item {
	private static final int ITEM_BAR_COLOR = MathHelper.packRgb(0.4F, 0.4F, 1.0F);

	public BundleItem(Item.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	public static float getAmountFilled(ItemStack stack) {
		return (float)getBundleOccupancy(stack) / 64.0F;
	}

	@Override
	public boolean onStackClicked(ItemStack stack, ItemStack otherStack, ClickType clickType, PlayerInventory playerInventory) {
		if (clickType == ClickType.RIGHT) {
			addToBundle(stack, otherStack);
			return true;
		} else {
			return super.onStackClicked(stack, otherStack, clickType, playerInventory);
		}
	}

	@Override
	public boolean onClicked(ItemStack stack, ItemStack otherStack, ClickType clickType, PlayerInventory playerInventory) {
		if (clickType == ClickType.RIGHT) {
			if (otherStack.isEmpty()) {
				emptyBundle(stack, playerInventory);
			} else {
				addToBundle(stack, otherStack);
			}

			return true;
		} else {
			return super.onClicked(stack, otherStack, clickType, playerInventory);
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		emptyBundle(itemStack, user.getInventory());
		return TypedActionResult.success(itemStack);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isItemBarVisible(ItemStack stack) {
		int i = getBundleOccupancy(stack);
		return i > 0 && i < 64;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getItemBarStep(ItemStack stack) {
		return 13 * getBundleOccupancy(stack) / 64 + 1;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getItemBarColor(ItemStack stack) {
		return ITEM_BAR_COLOR;
	}

	private static void addToBundle(ItemStack bundle, ItemStack stack) {
		if (stack.getItem().hasStoredInventory()) {
			CompoundTag compoundTag = bundle.getOrCreateTag();
			if (!compoundTag.contains("Items")) {
				compoundTag.put("Items", new ListTag());
			}

			int i = getBundleOccupancy(bundle);
			int j = getItemOccupancy(stack);
			int k = Math.min(stack.getCount(), (64 - i) / j);
			if (k != 0) {
				ListTag listTag = compoundTag.getList("Items", 10);
				Optional<CompoundTag> optional = method_32344(stack, listTag);
				if (optional.isPresent()) {
					CompoundTag compoundTag2 = (CompoundTag)optional.get();
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
		}
	}

	private static Optional<CompoundTag> method_32344(ItemStack itemStack, ListTag listTag) {
		return itemStack.isOf(Items.BUNDLE)
			? Optional.empty()
			: listTag.stream()
				.filter(CompoundTag.class::isInstance)
				.map(CompoundTag.class::cast)
				.filter(compoundTag -> ItemStack.method_31577(ItemStack.fromTag(compoundTag), itemStack))
				.findFirst();
	}

	private static int getItemOccupancy(ItemStack stack) {
		return stack.isOf(Items.BUNDLE) ? 4 + getBundleOccupancy(stack) : 64 / stack.getMaxCount();
	}

	private static int getBundleOccupancy(ItemStack stack) {
		return method_32345(stack).mapToInt(itemStack -> getItemOccupancy(itemStack) * itemStack.getCount()).sum();
	}

	private static void emptyBundle(ItemStack stack, PlayerInventory playerInventory) {
		method_32345(stack).forEach(stackx -> {
			if (playerInventory.player instanceof ServerPlayerEntity || playerInventory.player.isCreative()) {
				playerInventory.offerOrDrop(stackx);
			}
		});
		stack.removeSubTag("Items");
	}

	private static Stream<ItemStack> method_32345(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getTag();
		if (compoundTag == null) {
			return Stream.empty();
		} else {
			ListTag listTag = compoundTag.getList("Items", 10);
			return listTag.stream().map(tag -> ItemStack.fromTag((CompoundTag)tag));
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Optional<TooltipData> getTooltipData(ItemStack stack) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.of();
		method_32345(stack).forEach(defaultedList::add);
		return Optional.of(new BundleTooltipData(defaultedList, getBundleOccupancy(stack) < 64));
	}
}
