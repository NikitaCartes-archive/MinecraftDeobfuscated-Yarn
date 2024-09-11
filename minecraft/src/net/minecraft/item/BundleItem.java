package net.minecraft.item;

import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.tooltip.BundleTooltipData;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
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
	private static final int field_54109 = 10;
	private static final int field_54110 = 2;
	private static final int field_54111 = 60;
	private final Identifier openFrontTexture;
	private final Identifier openBackTexture;

	public BundleItem(Identifier openFrontTexture, Identifier openBackTexture, Item.Settings settings) {
		super(settings);
		this.openFrontTexture = openFrontTexture;
		this.openBackTexture = openBackTexture;
	}

	public static float getAmountFilled(ItemStack stack) {
		BundleContentsComponent bundleContentsComponent = stack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
		return bundleContentsComponent.getOccupancy().floatValue();
	}

	public Identifier getOpenFrontTexture() {
		return this.openFrontTexture;
	}

	public Identifier getOpenBackTexture() {
		return this.openBackTexture;
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
					playInsertSound(player);
				} else {
					playInsertFailSound(player);
				}

				stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
				return true;
			} else if (clickType == ClickType.RIGHT && itemStack.isEmpty()) {
				ItemStack itemStack2 = builder.removeSelected();
				if (itemStack2 != null) {
					ItemStack itemStack3 = slot.insertStack(itemStack2);
					if (itemStack3.getCount() > 0) {
						builder.add(itemStack3);
					} else {
						playRemoveOneSound(player);
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
						playInsertSound(player);
					} else {
						playInsertFailSound(player);
					}

					stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
					return true;
				} else if (clickType == ClickType.RIGHT && otherStack.isEmpty()) {
					if (slot.canTakePartial(player)) {
						ItemStack itemStack = builder.removeSelected();
						if (itemStack != null) {
							playRemoveOneSound(player);
							cursorStackReference.set(itemStack);
						}
					}

					stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
					return true;
				} else {
					setSelectedStackIndex(stack, -1);
					return false;
				}
			}
		}
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		if (world.isClient) {
			return ActionResult.CONSUME;
		} else {
			user.setCurrentHand(hand);
			return ActionResult.SUCCESS_SERVER;
		}
	}

	private void dropContentsOnUse(PlayerEntity player, ItemStack stack) {
		if (this.dropFirstBundledStack(stack, player)) {
			playDropContentsSound(player);
			player.incrementStat(Stats.USED.getOrCreateStat(this));
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

	private boolean dropFirstBundledStack(ItemStack stack, PlayerEntity player) {
		BundleContentsComponent bundleContentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
		if (bundleContentsComponent != null && !bundleContentsComponent.isEmpty()) {
			Optional<ItemStack> optional = popFirstBundledStack(stack, player, bundleContentsComponent);
			if (optional.isPresent()) {
				player.dropItem((ItemStack)optional.get(), true);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private static Optional<ItemStack> popFirstBundledStack(ItemStack stack, PlayerEntity player, BundleContentsComponent contents) {
		BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(contents);
		ItemStack itemStack = builder.removeSelected();
		if (itemStack != null) {
			playRemoveOneSound(player);
			stack.set(DataComponentTypes.BUNDLE_CONTENTS, builder.build());
			return Optional.of(itemStack);
		} else {
			return Optional.empty();
		}
	}

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		if (!world.isClient && user instanceof PlayerEntity playerEntity) {
			int i = this.getMaxUseTime(stack, user);
			boolean bl = remainingUseTicks == i;
			if (bl || remainingUseTicks < i - 10 && remainingUseTicks % 2 == 0) {
				this.dropContentsOnUse(playerEntity, stack);
			}
		}
	}

	@Override
	public int getMaxUseTime(ItemStack stack, LivingEntity user) {
		return 60;
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

	private static void playRemoveOneSound(Entity entity) {
		entity.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
	}

	private static void playInsertSound(Entity entity) {
		entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
	}

	private static void playInsertFailSound(Entity entity) {
		entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT_FAIL, 1.0F, 1.0F);
	}

	private static void playDropContentsSound(Entity entity) {
		entity.playSound(SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
	}
}
