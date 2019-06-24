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
import net.minecraft.util.IdList;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class ItemColors {
	private final IdList<ItemColorProvider> providers = new IdList<>(32);

	public static ItemColors create(BlockColors blockColors) {
		ItemColors itemColors = new ItemColors();
		itemColors.register(
			(itemStack, i) -> i > 0 ? -1 : ((DyeableItem)itemStack.getItem()).getColor(itemStack),
			Items.LEATHER_HELMET,
			Items.LEATHER_CHESTPLATE,
			Items.LEATHER_LEGGINGS,
			Items.LEATHER_BOOTS,
			Items.LEATHER_HORSE_ARMOR
		);
		itemColors.register((itemStack, i) -> GrassColors.getColor(0.5, 1.0), Blocks.TALL_GRASS, Blocks.LARGE_FERN);
		itemColors.register((itemStack, i) -> {
			if (i != 1) {
				return -1;
			} else {
				CompoundTag compoundTag = itemStack.getSubTag("Explosion");
				int[] is = compoundTag != null && compoundTag.containsKey("Colors", 11) ? compoundTag.getIntArray("Colors") : null;
				if (is == null) {
					return 9079434;
				} else if (is.length == 1) {
					return is[0];
				} else {
					int j = 0;
					int k = 0;
					int l = 0;

					for (int m : is) {
						j += (m & 0xFF0000) >> 16;
						k += (m & 0xFF00) >> 8;
						l += (m & 0xFF) >> 0;
					}

					j /= is.length;
					k /= is.length;
					l /= is.length;
					return j << 16 | k << 8 | l;
				}
			}
		}, Items.FIREWORK_STAR);
		itemColors.register((itemStack, i) -> i > 0 ? -1 : PotionUtil.getColor(itemStack), Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);

		for (SpawnEggItem spawnEggItem : SpawnEggItem.getAll()) {
			itemColors.register((itemStack, i) -> spawnEggItem.getColor(i), spawnEggItem);
		}

		itemColors.register(
			(itemStack, i) -> {
				BlockState blockState = ((BlockItem)itemStack.getItem()).getBlock().getDefaultState();
				return blockColors.getColorMultiplier(blockState, null, null, i);
			},
			Blocks.GRASS_BLOCK,
			Blocks.GRASS,
			Blocks.FERN,
			Blocks.VINE,
			Blocks.OAK_LEAVES,
			Blocks.SPRUCE_LEAVES,
			Blocks.BIRCH_LEAVES,
			Blocks.JUNGLE_LEAVES,
			Blocks.ACACIA_LEAVES,
			Blocks.DARK_OAK_LEAVES,
			Blocks.LILY_PAD
		);
		itemColors.register((itemStack, i) -> i == 0 ? PotionUtil.getColor(itemStack) : -1, Items.TIPPED_ARROW);
		itemColors.register((itemStack, i) -> i == 0 ? -1 : FilledMapItem.getMapColor(itemStack), Items.FILLED_MAP);
		return itemColors;
	}

	public int getColorMultiplier(ItemStack itemStack, int i) {
		ItemColorProvider itemColorProvider = this.providers.get(Registry.ITEM.getRawId(itemStack.getItem()));
		return itemColorProvider == null ? -1 : itemColorProvider.getColor(itemStack, i);
	}

	public void register(ItemColorProvider itemColorProvider, ItemConvertible... itemConvertibles) {
		for (ItemConvertible itemConvertible : itemConvertibles) {
			this.providers.set(itemColorProvider, Item.getRawId(itemConvertible.asItem()));
		}
	}
}
