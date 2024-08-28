package net.minecraft.item;

import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.tooltip.BundleTooltipData;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.math.Fraction;

public class BundleItem extends Item {
	public static final int TOOLTIP_STACKS_COLUMNS = 4;
	public static final int TOOLTIP_STACKS_ROWS = 3;
	public static final int MAX_TOOLTIP_STACKS_SHOWN = 12;
	public static final int MAX_TOOLTIP_STACKS_SHOWN_WHEN_TOO_MANY_TYPES = 11;
	private static final int FULL_ITEM_BAR_COLOR = ColorHelper.fromFloats(1.0F, 1.0F, 0.33F, 0.33F);
	private static final int ITEM_BAR_COLOR = ColorHelper.fromFloats(1.0F, 0.44F, 0.53F, 1.0F);
	private final String openFrontModelName;
	private final String openBackModelName;

	public BundleItem(String openFrontModelName, String openBackModelName, Item.Settings settings) {
		super(settings);
		this.openFrontModelName = openFrontModelName;
		this.openBackModelName = openBackModelName;
	}

	public static float getAmountFilled(ItemStack stack) {
		BundleContentsComponent bundleContentsComponent = stack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
		return bundleContentsComponent.getOccupancy().floatValue();
	}

	public String getOpenFrontModelName() {
		return this.openFrontModelName;
	}

	public String getOpenBackModelName() {
		return this.openBackModelName;
	}

	@Override
	public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
		BundleContentsComponent bundleContentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
		if (bundleContentsComponent == null) {
			return false;
		} else {
			ItemStack itemStack = slot.getStack();
			BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleContentsComponent);
			if (clickType == ClickType.LEFT && !itemStack.isEmpty()) {
				if (builder.add(slot, player) > 0) {
					this.playInsertSound(player);
				} else {
					playInsertFailSound(player);
				}

				stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
				return true;
			} else if (clickType == ClickType.RIGHT && itemStack.isEmpty()) {
				ItemStack itemStack2 = builder.removeFirst();
				if (itemStack2 != null) {
					ItemStack itemStack3 = slot.insertStack(itemStack2);
					if (itemStack3.getCount() > 0) {
						builder.add(itemStack3);
					} else {
						this.playRemoveOneSound(player);
					}
				}

				stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
		if (clickType == ClickType.LEFT && otherStack.isEmpty()) {
			setSelectedStackIndex(stack, -1);
			return false;
		} else {
			BundleContentsComponent bundleContentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
			if (bundleContentsComponent == null) {
				return false;
			} else {
				BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleContentsComponent);
				if (clickType == ClickType.LEFT && !otherStack.isEmpty()) {
					if (slot.canTakePartial(player) && builder.add(otherStack) > 0) {
						this.playInsertSound(player);
					} else {
						playInsertFailSound(player);
					}

					stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
					return true;
				} else if (clickType == ClickType.RIGHT && otherStack.isEmpty()) {
					if (slot.canTakePartial(player)) {
						ItemStack itemStack = builder.removeFirst();
						if (itemStack != null) {
							this.playRemoveOneSound(player);
							cursorStackReference.set(itemStack);
						}
					}

					stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
					return true;
				} else {
					return false;
				}
			}
		}
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		if (dropAllBundledItems(itemStack, user)) {
			this.playDropContentsSound(user);
			user.incrementStat(Stats.USED.getOrCreateStat(this));
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.FAIL;
		}
	}

	@Override
	public boolean isItemBarVisible(ItemStack stack) {
		BundleContentsComponent bundleContentsComponent = stack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
		return bundleContentsComponent.getOccupancy().compareTo(Fraction.ZERO) > 0;
	}

	@Override
	public int getItemBarStep(ItemStack stack) {
		BundleContentsComponent bundleContentsComponent = stack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
		return Math.min(1 + MathHelper.multiplyFraction(bundleContentsComponent.getOccupancy(), 12), 13);
	}

	@Override
	public int getItemBarColor(ItemStack stack) {
		BundleContentsComponent bundleContentsComponent = stack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
		return bundleContentsComponent.getOccupancy().compareTo(Fraction.ONE) >= 0 ? FULL_ITEM_BAR_COLOR : ITEM_BAR_COLOR;
	}

	public static void setSelectedStackIndex(ItemStack stack, int selectedStackIndex) {
		BundleContentsComponent bundleContentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
		if (bundleContentsComponent != null) {
			BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleContentsComponent);
			builder.setSelectedStackIndex(selectedStackIndex);
			stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
		}
	}

	public static boolean hasSelectedStack(ItemStack stack) {
		BundleContentsComponent bundleContentsComponent = stack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
		return bundleContentsComponent.getSelectedStackIndex() != -1;
	}

	public static int getSelectedStackIndex(ItemStack stack) {
		BundleContentsComponent bundleContentsComponent = stack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
		return bundleContentsComponent.getSelectedStackIndex();
	}

	public static ItemStack getSelectedStack(ItemStack stack) {
		BundleContentsComponent bundleContentsComponent = stack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
		return bundleContentsComponent.get(bundleContentsComponent.getSelectedStackIndex());
	}

	public static int getNumberOfStacksShown(ItemStack stack) {
		BundleContentsComponent bundleContentsComponent = stack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
		return bundleContentsComponent.getNumberOfStacksShown();
	}

	private static boolean dropAllBundledItems(ItemStack stack, PlayerEntity player) {
		BundleContentsComponent bundleContentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
		if (bundleContentsComponent != null && !bundleContentsComponent.isEmpty()) {
			stack.set(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
			if (player instanceof ServerPlayerEntity) {
				bundleContentsComponent.iterateCopy().forEach(stackx -> player.dropItem(stackx, true));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public Optional<TooltipData> getTooltipData(ItemStack stack) {
		return !stack.contains(DataComponentTypes.HIDE_TOOLTIP) && !stack.contains(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP)
			? Optional.ofNullable(stack.get(DataComponentTypes.BUNDLE_CONTENTS)).map(BundleTooltipData::new)
			: Optional.empty();
	}

	@Override
	public void onItemEntityDestroyed(ItemEntity entity) {
		BundleContentsComponent bundleContentsComponent = entity.getStack().get(DataComponentTypes.BUNDLE_CONTENTS);
		if (bundleContentsComponent != null) {
			entity.getStack().set(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
			ItemUsage.spawnItemContents(entity, bundleContentsComponent.iterateCopy());
		}
	}

	private void playRemoveOneSound(Entity entity) {
		entity.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
	}

	private void playInsertSound(Entity entity) {
		entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
	}

	private static void playInsertFailSound(Entity entity) {
		entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT_FAIL, 1.0F, 1.0F);
	}

	private void playDropContentsSound(Entity entity) {
		entity.playSound(SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
	}
}
