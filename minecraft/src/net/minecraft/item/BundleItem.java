package net.minecraft.item;

import java.util.List;
import java.util.Optional;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
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

	public static float getAmountFilled(ItemStack stack) {
		BundleContentsComponent bundleContentsComponent = stack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
		return (float)bundleContentsComponent.getOccupancy() / 64.0F;
	}

	@Override
	public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
		if (clickType != ClickType.RIGHT) {
			return false;
		} else {
			BundleContentsComponent bundleContentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
			if (bundleContentsComponent == null) {
				return false;
			} else {
				ItemStack itemStack = slot.getStack();
				BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleContentsComponent);
				if (itemStack.isEmpty()) {
					this.playRemoveOneSound(player);
					ItemStack itemStack2 = builder.removeFirst();
					if (itemStack2 != null) {
						ItemStack itemStack3 = slot.insertStack(itemStack2);
						builder.add(itemStack3);
					}
				} else if (itemStack.getItem().canBeNested()) {
					int i = builder.add(slot, player);
					if (i > 0) {
						this.playInsertSound(player);
					}
				}

				stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
				return true;
			}
		}
	}

	@Override
	public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
		if (clickType == ClickType.RIGHT && slot.canTakePartial(player)) {
			BundleContentsComponent bundleContentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
			if (bundleContentsComponent == null) {
				return false;
			} else {
				BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleContentsComponent);
				if (otherStack.isEmpty()) {
					ItemStack itemStack = builder.removeFirst();
					if (itemStack != null) {
						this.playRemoveOneSound(player);
						cursorStackReference.set(itemStack);
					}
				} else {
					int i = builder.add(otherStack);
					if (i > 0) {
						this.playInsertSound(player);
					}
				}

				stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		if (dropAllBundledItems(itemStack, user)) {
			this.playDropContentsSound(user);
			user.incrementStat(Stats.USED.getOrCreateStat(this));
			return TypedActionResult.success(itemStack, world.isClient());
		} else {
			return TypedActionResult.fail(itemStack);
		}
	}

	@Override
	public boolean isItemBarVisible(ItemStack stack) {
		BundleContentsComponent bundleContentsComponent = stack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
		return bundleContentsComponent.getOccupancy() > 0;
	}

	@Override
	public int getItemBarStep(ItemStack stack) {
		BundleContentsComponent bundleContentsComponent = stack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
		return Math.min(1 + 12 * bundleContentsComponent.getOccupancy() / 64, 13);
	}

	@Override
	public int getItemBarColor(ItemStack stack) {
		return ITEM_BAR_COLOR;
	}

	private static boolean dropAllBundledItems(ItemStack stack, PlayerEntity player) {
		BundleContentsComponent bundleContentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
		if (bundleContentsComponent != null && !bundleContentsComponent.isEmpty()) {
			stack.set(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
			if (player instanceof ServerPlayerEntity) {
				bundleContentsComponent.stream().forEach(stackx -> player.dropItem(stackx, true));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public Optional<TooltipData> getTooltipData(ItemStack stack) {
		return Optional.ofNullable(stack.get(DataComponentTypes.BUNDLE_CONTENTS)).map(BundleTooltipData::new);
	}

	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		BundleContentsComponent bundleContentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
		if (bundleContentsComponent != null) {
			tooltip.add(Text.translatable("item.minecraft.bundle.fullness", bundleContentsComponent.getOccupancy(), 64).formatted(Formatting.GRAY));
		}
	}

	@Override
	public void onItemEntityDestroyed(ItemEntity entity) {
		BundleContentsComponent bundleContentsComponent = entity.getStack().get(DataComponentTypes.BUNDLE_CONTENTS);
		if (bundleContentsComponent != null) {
			entity.getStack().set(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
			ItemUsage.spawnItemContents(entity, bundleContentsComponent.stream());
		}
	}

	private void playRemoveOneSound(Entity entity) {
		entity.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
	}

	private void playInsertSound(Entity entity) {
		entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
	}

	private void playDropContentsSound(Entity entity) {
		entity.playSound(SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
	}
}
