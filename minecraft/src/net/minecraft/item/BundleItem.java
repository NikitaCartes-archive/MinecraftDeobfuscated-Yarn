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
import net.minecraft.inventory.CommandItemSlot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
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
	public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
		if (clickType != ClickType.RIGHT) {
			return false;
		} else {
			ItemStack itemStack = slot.getStack();
			if (itemStack.isEmpty()) {
				removeFirstStack(stack).ifPresent(itemStack2 -> addToBundle(stack, slot.insertStack(itemStack2)));
			} else if (itemStack.getItem().canBeNested()) {
				int i = (64 - getBundleOccupancy(stack)) / getItemOccupancy(itemStack);
				addToBundle(stack, slot.takeStackRange(itemStack.getCount(), i, player));
			}

			return true;
		}
	}

	@Override
	public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, CommandItemSlot cursorSlot) {
		if (clickType == ClickType.RIGHT && slot.canTakePartial(player)) {
			if (otherStack.isEmpty()) {
				removeFirstStack(stack).ifPresent(cursorSlot::set);
			} else {
				otherStack.decrement(addToBundle(stack, otherStack));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		if (dropAllBundledItems(itemStack, user)) {
			user.incrementStat(Stats.USED.getOrCreateStat(this));
			return TypedActionResult.success(itemStack, world.isClient());
		} else {
			return TypedActionResult.fail(itemStack);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isItemBarVisible(ItemStack stack) {
		return getBundleOccupancy(stack) > 0;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getItemBarStep(ItemStack stack) {
		return Math.min(1 + 12 * getBundleOccupancy(stack) / 64, 13);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getItemBarColor(ItemStack stack) {
		return ITEM_BAR_COLOR;
	}

	private static int addToBundle(ItemStack bundle, ItemStack stack) {
		if (!stack.isEmpty() && stack.getItem().canBeNested()) {
			CompoundTag compoundTag = bundle.getOrCreateTag();
			if (!compoundTag.contains("Items")) {
				compoundTag.put("Items", new ListTag());
			}

			int i = getBundleOccupancy(bundle);
			int j = getItemOccupancy(stack);
			int k = Math.min(stack.getCount(), (64 - i) / j);
			if (k == 0) {
				return 0;
			} else {
				ListTag listTag = compoundTag.getList("Items", 10);
				Optional<CompoundTag> optional = method_32344(stack, listTag);
				if (optional.isPresent()) {
					CompoundTag compoundTag2 = (CompoundTag)optional.get();
					ItemStack itemStack = ItemStack.fromNbt(compoundTag2);
					itemStack.increment(k);
					itemStack.writeNbt(compoundTag2);
					listTag.remove(compoundTag2);
					listTag.add(0, compoundTag2);
				} else {
					ItemStack itemStack2 = stack.copy();
					itemStack2.setCount(k);
					CompoundTag compoundTag3 = new CompoundTag();
					itemStack2.writeNbt(compoundTag3);
					listTag.add(0, compoundTag3);
				}

				return k;
			}
		} else {
			return 0;
		}
	}

	private static Optional<CompoundTag> method_32344(ItemStack itemStack, ListTag listTag) {
		return itemStack.isOf(Items.BUNDLE)
			? Optional.empty()
			: listTag.stream()
				.filter(CompoundTag.class::isInstance)
				.map(CompoundTag.class::cast)
				.filter(compoundTag -> ItemStack.canCombine(ItemStack.fromNbt(compoundTag), itemStack))
				.findFirst();
	}

	private static int getItemOccupancy(ItemStack stack) {
		return stack.isOf(Items.BUNDLE) ? 4 + getBundleOccupancy(stack) : 64 / stack.getMaxCount();
	}

	private static int getBundleOccupancy(ItemStack stack) {
		return getBundledStacks(stack).mapToInt(itemStack -> getItemOccupancy(itemStack) * itemStack.getCount()).sum();
	}

	private static Optional<ItemStack> removeFirstStack(ItemStack stack) {
		CompoundTag compoundTag = stack.getOrCreateTag();
		if (!compoundTag.contains("Items")) {
			return Optional.empty();
		} else {
			ListTag listTag = compoundTag.getList("Items", 10);
			if (listTag.isEmpty()) {
				return Optional.empty();
			} else {
				int i = 0;
				CompoundTag compoundTag2 = listTag.getCompound(0);
				ItemStack itemStack = ItemStack.fromNbt(compoundTag2);
				listTag.remove(0);
				if (listTag.isEmpty()) {
					stack.removeSubTag("Items");
				}

				return Optional.of(itemStack);
			}
		}
	}

	private static boolean dropAllBundledItems(ItemStack stack, PlayerEntity player) {
		CompoundTag compoundTag = stack.getOrCreateTag();
		if (!compoundTag.contains("Items")) {
			return false;
		} else {
			if (player instanceof ServerPlayerEntity) {
				ListTag listTag = compoundTag.getList("Items", 10);

				for (int i = 0; i < listTag.size(); i++) {
					CompoundTag compoundTag2 = listTag.getCompound(i);
					ItemStack itemStack = ItemStack.fromNbt(compoundTag2);
					player.dropItem(itemStack, true);
				}
			}

			stack.removeSubTag("Items");
			return true;
		}
	}

	private static Stream<ItemStack> getBundledStacks(ItemStack stack) {
		CompoundTag compoundTag = stack.getTag();
		if (compoundTag == null) {
			return Stream.empty();
		} else {
			ListTag listTag = compoundTag.getList("Items", 10);
			return listTag.stream().map(CompoundTag.class::cast).map(ItemStack::fromNbt);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Optional<TooltipData> getTooltipData(ItemStack stack) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.of();
		getBundledStacks(stack).forEach(defaultedList::add);
		return Optional.of(new BundleTooltipData(defaultedList, getBundleOccupancy(stack)));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(new TranslatableText("item.minecraft.bundle.fullness", getBundleOccupancy(stack), 64).formatted(Formatting.GRAY));
	}

	@Override
	public void onItemEntityDestroyed(ItemEntity entity) {
		ItemUsage.spawnItemContents(entity, getBundledStacks(entity.getStack()));
	}
}
