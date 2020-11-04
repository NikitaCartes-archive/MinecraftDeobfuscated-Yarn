package net.minecraft.item;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BundleItem extends Item {
	private static final int ITEM_BAR_COLOR = MathHelper.packRgb(0.4F, 0.4F, 1.0F);

	public BundleItem(Item.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	public static float getAmountFilled(ItemStack itemStack) {
		return (float)getBundleOccupancy(itemStack) / 64.0F;
	}

	@Override
	public boolean onStackClicked(ItemStack itemStack, ItemStack itemStack2, ClickType clickType, PlayerInventory playerInventory) {
		if (clickType == ClickType.RIGHT) {
			addToBundle(itemStack, itemStack2);
			return true;
		} else {
			return super.onStackClicked(itemStack, itemStack2, clickType, playerInventory);
		}
	}

	@Override
	public boolean onClicked(ItemStack itemStack, ItemStack itemStack2, ClickType clickType, PlayerInventory playerInventory) {
		if (clickType == ClickType.RIGHT) {
			if (itemStack2.isEmpty()) {
				emptyBundle(itemStack, playerInventory);
			} else {
				addToBundle(itemStack, itemStack2);
			}

			return true;
		} else {
			return super.onClicked(itemStack, itemStack2, clickType, playerInventory);
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
	public boolean isItemBarVisible(ItemStack itemStack) {
		int i = getBundleOccupancy(itemStack);
		return i != 0 && i != 64;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getItemBarStep(ItemStack itemStack) {
		return 13 * getBundleOccupancy(itemStack) / 64 + 1;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getItemBarColor(ItemStack itemStack) {
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
				Optional<Tag> optional = listTag.stream()
					.filter(tag -> tag instanceof CompoundTag && ItemStack.method_31577(ItemStack.fromTag((CompoundTag)tag), stack))
					.findFirst();
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

	private static int getItemOccupancy(ItemStack itemStack) {
		return itemStack.isOf(Items.BUNDLE) ? 4 + getBundleOccupancy(itemStack) : 64 / itemStack.getMaxCount();
	}

	private static int getBundleOccupancy(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getOrCreateTag();
		if (!compoundTag.contains("Items")) {
			return 0;
		} else {
			ListTag listTag = compoundTag.getList("Items", 10);
			return listTag.stream().map(tag -> ItemStack.fromTag((CompoundTag)tag)).mapToInt(itemStackx -> getItemOccupancy(itemStackx) * itemStackx.getCount()).sum();
		}
	}

	private static void emptyBundle(ItemStack itemStack, PlayerInventory playerInventory) {
		CompoundTag compoundTag = itemStack.getOrCreateTag();
		if (compoundTag.contains("Items")) {
			ListTag listTag = compoundTag.getList("Items", 10);

			for (int i = 0; i < listTag.size(); i++) {
				CompoundTag compoundTag2 = listTag.getCompound(i);
				ItemStack itemStack2 = ItemStack.fromTag(compoundTag2);
				if (playerInventory.player instanceof ServerPlayerEntity || playerInventory.player.isCreative()) {
					playerInventory.offerOrDrop(itemStack2);
				}
			}

			itemStack.removeSubTag("Items");
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		CompoundTag compoundTag = stack.getOrCreateTag();
		if (compoundTag.contains("Items", 9)) {
			ListTag listTag = compoundTag.getList("Items", 10);
			int i = 0;
			int j = 0;

			for (Tag tag : listTag) {
				ItemStack itemStack = ItemStack.fromTag((CompoundTag)tag);
				if (!itemStack.isEmpty()) {
					j++;
					if (i <= 8) {
						i++;
						MutableText mutableText = itemStack.getName().shallowCopy();
						mutableText.append(" x").append(String.valueOf(itemStack.getCount()));
						tooltip.add(mutableText);
					}
				}
			}

			if (j - i > 0) {
				tooltip.add(new TranslatableText("container.shulkerBox.more", j - i).formatted(Formatting.ITALIC));
			}
		}
	}
}
