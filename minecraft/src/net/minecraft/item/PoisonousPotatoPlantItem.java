package net.minecraft.item;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Nullables;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PoisonousPotatoPlantItem extends ArmorItem {
	private static final Style RUMBLED_TEXT_STYLE = Style.EMPTY.withColor(Formatting.DARK_PURPLE).withItalic(true);
	private static final int CLICKS_BEFORE_REVEAL = 4;

	public PoisonousPotatoPlantItem(RegistryEntry<ArmorMaterial> registryEntry, ArmorItem.Type type, Item.Settings settings) {
		super(registryEntry, type, settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		int i = stack.getOrDefault(DataComponentTypes.CLICKS, Integer.valueOf(0));
		if (i >= 4) {
			tooltip.add(Texts.setStyleIfAbsent(Text.translatable("item.minecraft.poisonous_potato_plant.rumbled.line1"), RUMBLED_TEXT_STYLE));
			tooltip.add(
				Texts.setStyleIfAbsent(
					Text.translatable("item.minecraft.poisonous_potato_plant.rumbled.line2", Text.translatable("item.minecraft.poisonous_potato")), RUMBLED_TEXT_STYLE
				)
			);
		}
	}

	@Override
	public void onViewInChest(ItemStack stack, World world, BlockPos pos, Inventory inventory) {
		List<ItemStack> list = inventory.getMatchingStacks(stackx -> stackx.contains(DataComponentTypes.UNDERCOVER_ID));

		for (ItemStack itemStack : list) {
			if (itemStack.get(DataComponentTypes.UNDERCOVER_ID) == 0) {
				itemStack.set(DataComponentTypes.UNDERCOVER_ID, world.getRandom().nextInt());
			}
		}

		int i = stack.get(DataComponentTypes.UNDERCOVER_ID);
		list.removeIf(stackx -> stackx.get(DataComponentTypes.UNDERCOVER_ID) == i);
		if (!list.isEmpty()) {
			ItemStack itemStackx = (ItemStack)list.get(world.getRandom().nextInt(list.size()));
			int j = itemStackx.get(DataComponentTypes.UNDERCOVER_ID);
			Int2IntMap int2IntMap = itemStackx.getOrDefault(DataComponentTypes.CONTACTS_MESSAGES, new Int2IntOpenHashMap());
			Int2IntMap int2IntMap2 = stack.getOrDefault(DataComponentTypes.CONTACTS_MESSAGES, new Int2IntOpenHashMap());
			int k = int2IntMap.getOrDefault(i, -1);
			int l = int2IntMap2.getOrDefault(j, -1);
			if (k > l) {
				int2IntMap2.put(j, k);
				stack.set(DataComponentTypes.CONTACTS_MESSAGES, int2IntMap2);
			} else {
				int m = l + 1;
				Optional<MutableText> optional = getPaperSecretText(m, world.getClosestPlayer(pos, 4.0, false));
				if (!optional.isEmpty()) {
					int2IntMap2.put(j, m);
					stack.set(DataComponentTypes.CONTACTS_MESSAGES, int2IntMap2);
					List<ItemStack> list2 = inventory.getMatchingStacks(stackx -> stackx.isOf(Items.PAPER));
					list2.removeIf(stackx -> {
						if (!stackx.contains(DataComponentTypes.SECRET_MESSAGE)) {
							return true;
						} else {
							IntIntPair intIntPair = stackx.get(DataComponentTypes.SECRET_MESSAGE);
							return intIntPair.firstInt() != i && intIntPair.firstInt() != j;
						}
					});
					ItemStack itemStack2;
					if (list2.isEmpty()) {
						itemStack2 = new ItemStack(Items.PAPER);
						int n = Inventories.tryAddStack(inventory, itemStack2);
						if (n < 0) {
							return;
						}
					} else {
						itemStack2 = (ItemStack)list2.get(world.getRandom().nextInt(list2.size()));
					}

					itemStack2.set(DataComponentTypes.SECRET_MESSAGE, new IntIntImmutablePair(j, m));
					itemStack2.set(DataComponentTypes.CUSTOM_NAME, (MutableText)optional.get());
				}
			}
		}
	}

	private static Optional<MutableText> getPaperSecretText(int messageIndex, @Nullable PlayerEntity player) {
		MutableText mutableText = Text.translatable(
			"item.minecraft.paper.secret." + messageIndex, Nullables.mapOrElse(player, PlayerEntity::getDisplayName, Text.translatable("the.player"))
		);
		return mutableText.getString().startsWith("item.minecraft.paper.secret.") ? Optional.empty() : Optional.of(mutableText);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		int i = itemStack.getOrDefault(DataComponentTypes.CLICKS, Integer.valueOf(0));
		return i >= 4 ? super.use(world, user, hand) : TypedActionResult.pass(itemStack);
	}
}
