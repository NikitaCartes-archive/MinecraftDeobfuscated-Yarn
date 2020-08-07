package net.minecraft.client.color.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class ItemColors {
	private final IdList<ItemColorProvider> providers = new IdList<>(32);

	public static ItemColors create(BlockColors blockColors) {
		ItemColors itemColors = new ItemColors();
		itemColors.register(
			(stack, tintIndex) -> tintIndex > 0 ? -1 : ((DyeableItem)stack.getItem()).getColor(stack),
			Items.field_8267,
			Items.field_8577,
			Items.field_8570,
			Items.field_8370,
			Items.field_18138
		);
		itemColors.register((stack, tintIndex) -> GrassColors.getColor(0.5, 1.0), Blocks.field_10214, Blocks.field_10313);
		itemColors.register((stack, tintIndex) -> {
			if (tintIndex != 1) {
				return -1;
			} else {
				CompoundTag compoundTag = stack.getSubTag("Explosion");
				int[] is = compoundTag != null && compoundTag.contains("Colors", 11) ? compoundTag.getIntArray("Colors") : null;
				if (is != null && is.length != 0) {
					if (is.length == 1) {
						return is[0];
					} else {
						int i = 0;
						int j = 0;
						int k = 0;

						for (int l : is) {
							i += (l & 0xFF0000) >> 16;
							j += (l & 0xFF00) >> 8;
							k += (l & 0xFF) >> 0;
						}

						i /= is.length;
						j /= is.length;
						k /= is.length;
						return i << 16 | j << 8 | k;
					}
				} else {
					return 9079434;
				}
			}
		}, Items.field_8450);
		itemColors.register((stack, tintIndex) -> tintIndex > 0 ? -1 : PotionUtil.getColor(stack), Items.field_8574, Items.field_8436, Items.field_8150);

		for (SpawnEggItem spawnEggItem : SpawnEggItem.getAll()) {
			itemColors.register((stack, tintIndex) -> spawnEggItem.getColor(tintIndex), spawnEggItem);
		}

		itemColors.register(
			(stack, tintIndex) -> {
				BlockState blockState = ((BlockItem)stack.getItem()).getBlock().getDefaultState();
				return blockColors.getColor(blockState, null, null, tintIndex);
			},
			Blocks.field_10219,
			Blocks.field_10479,
			Blocks.field_10112,
			Blocks.field_10597,
			Blocks.field_10503,
			Blocks.field_9988,
			Blocks.field_10539,
			Blocks.field_10335,
			Blocks.field_10098,
			Blocks.field_10035,
			Blocks.field_10588
		);
		itemColors.register((stack, tintIndex) -> tintIndex == 0 ? PotionUtil.getColor(stack) : -1, Items.field_8087);
		itemColors.register((stack, tintIndex) -> tintIndex == 0 ? -1 : FilledMapItem.getMapColor(stack), Items.field_8204);
		return itemColors;
	}

	public int getColorMultiplier(ItemStack item, int tintIndex) {
		ItemColorProvider itemColorProvider = this.providers.get(Registry.ITEM.getRawId(item.getItem()));
		return itemColorProvider == null ? -1 : itemColorProvider.getColor(item, tintIndex);
	}

	public void register(ItemColorProvider mapper, ItemConvertible... items) {
		for (ItemConvertible itemConvertible : items) {
			this.providers.set(mapper, Item.getRawId(itemConvertible.asItem()));
		}
	}
}
