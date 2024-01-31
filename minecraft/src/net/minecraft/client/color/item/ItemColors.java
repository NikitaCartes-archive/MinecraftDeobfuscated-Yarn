package net.minecraft.client.color.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.potion.PotionUtil;
import net.minecraft.registry.Registries;
import net.minecraft.util.collection.IdList;

@Environment(EnvType.CLIENT)
public class ItemColors {
	private static final int NO_COLOR = -1;
	private final IdList<ItemColorProvider> providers = new IdList<>(32);

	public static ItemColors create(BlockColors blockColors) {
		ItemColors itemColors = new ItemColors();
		itemColors.register(
			(stack, tintIndex) -> tintIndex > 0 ? -1 : DyeableItem.getColor(stack),
			Items.LEATHER_HELMET,
			Items.LEATHER_CHESTPLATE,
			Items.LEATHER_LEGGINGS,
			Items.LEATHER_BOOTS,
			Items.LEATHER_HORSE_ARMOR
		);
		itemColors.register((stack, tintIndex) -> GrassColors.getColor(0.5, 1.0), Blocks.TALL_GRASS, Blocks.LARGE_FERN);
		itemColors.register((stack, tintIndex) -> {
			if (tintIndex != 1) {
				return -1;
			} else {
				NbtCompound nbtCompound = stack.getSubNbt("Explosion");
				int[] is = nbtCompound != null && nbtCompound.contains("Colors", NbtElement.INT_ARRAY_TYPE) ? nbtCompound.getIntArray("Colors") : null;
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
		}, Items.FIREWORK_STAR);
		itemColors.register((stack, tintIndex) -> tintIndex > 0 ? -1 : PotionUtil.getColor(stack), Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);

		for (SpawnEggItem spawnEggItem : SpawnEggItem.getAll()) {
			itemColors.register((stack, tintIndex) -> spawnEggItem.getColor(tintIndex), spawnEggItem);
		}

		itemColors.register(
			(stack, tintIndex) -> {
				BlockState blockState = ((BlockItem)stack.getItem()).getBlock().getDefaultState();
				return blockColors.getColor(blockState, null, null, tintIndex);
			},
			Blocks.GRASS_BLOCK,
			Blocks.SHORT_GRASS,
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
		itemColors.register((stack, tintIndex) -> FoliageColors.getMangroveColor(), Blocks.MANGROVE_LEAVES);
		itemColors.register((stack, tintIndex) -> tintIndex == 0 ? PotionUtil.getColor(stack) : -1, Items.TIPPED_ARROW);
		itemColors.register((stack, tintIndex) -> tintIndex == 0 ? -1 : FilledMapItem.getMapColor(stack), Items.FILLED_MAP);
		return itemColors;
	}

	public int getColor(ItemStack item, int tintIndex) {
		ItemColorProvider itemColorProvider = this.providers.get(Registries.ITEM.getRawId(item.getItem()));
		return itemColorProvider == null ? -1 : itemColorProvider.getColor(item, tintIndex);
	}

	public void register(ItemColorProvider provider, ItemConvertible... items) {
		for (ItemConvertible itemConvertible : items) {
			this.providers.set(provider, Item.getRawId(itemConvertible.asItem()));
		}
	}
}
