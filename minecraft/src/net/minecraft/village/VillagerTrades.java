package net.minecraft.village;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.InfoEnchantment;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VillagerTrades {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Map<VillagerProfession, Int2ObjectMap<VillagerTrades.Factory[]>> PROFESSION_TO_LEVELED_TRADE = SystemUtil.consume(
		Maps.<VillagerProfession, Int2ObjectMap<VillagerTrades.Factory[]>>newHashMap(),
		hashMap -> {
			hashMap.put(
				VillagerProfession.field_17056,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new VillagerTrades.Factory[]{
							new VillagerTrades.BuyItemFactory(Items.field_8861, new VillagerTrades.PriceRange(18, 22)),
							new VillagerTrades.BuyItemFactory(Items.field_8567, new VillagerTrades.PriceRange(15, 19)),
							new VillagerTrades.BuyItemFactory(Items.field_8179, new VillagerTrades.PriceRange(15, 19)),
							new VillagerTrades.BuyOrSellOnPriceFactory(Items.field_8229, new VillagerTrades.PriceRange(-4, -2))
						},
						2,
						new VillagerTrades.Factory[]{
							new VillagerTrades.BuyItemFactory(Blocks.field_10261, new VillagerTrades.PriceRange(8, 13)),
							new VillagerTrades.BuyOrSellOnPriceFactory(Items.field_8741, new VillagerTrades.PriceRange(-3, -2))
						},
						3,
						new VillagerTrades.Factory[]{
							new VillagerTrades.BuyItemFactory(Blocks.field_10545, new VillagerTrades.PriceRange(7, 12)),
							new VillagerTrades.BuyOrSellOnPriceFactory(Items.field_8279, new VillagerTrades.PriceRange(-7, -5))
						},
						4,
						new VillagerTrades.Factory[]{
							new VillagerTrades.BuyOrSellOnPriceFactory(Items.field_8423, new VillagerTrades.PriceRange(-10, -6)),
							new VillagerTrades.BuyOrSellOnPriceFactory(Blocks.field_10183, new VillagerTrades.PriceRange(1, 1))
						}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17057,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new VillagerTrades.Factory[]{
							new VillagerTrades.BuyItemFactory(Items.field_8276, new VillagerTrades.PriceRange(15, 20)),
							new VillagerTrades.BuyItemFactory(Items.field_8713, new VillagerTrades.PriceRange(16, 24)),
							new VillagerTrades.ProcessBoughtFactory(Items.field_8429, new VillagerTrades.PriceRange(6, 6), Items.field_8373, new VillagerTrades.PriceRange(6, 6)),
							new VillagerTrades.ProcessBoughtFactory(Items.field_8209, new VillagerTrades.PriceRange(6, 6), Items.field_8509, new VillagerTrades.PriceRange(6, 6))
						},
						2,
						new VillagerTrades.Factory[]{new VillagerTrades.SellEnchantedToolFactory(Items.field_8378, new VillagerTrades.PriceRange(7, 8))}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17063,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new VillagerTrades.Factory[]{
							new VillagerTrades.BuyItemFactory(Blocks.field_10446, new VillagerTrades.PriceRange(16, 22)),
							new VillagerTrades.BuyOrSellOnPriceFactory(Items.field_8868, new VillagerTrades.PriceRange(3, 4))
						},
						2,
						new VillagerTrades.Factory[]{
							new VillagerTrades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10446), new VillagerTrades.PriceRange(1, 2)),
							new VillagerTrades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10095), new VillagerTrades.PriceRange(1, 2)),
							new VillagerTrades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10215), new VillagerTrades.PriceRange(1, 2)),
							new VillagerTrades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10294), new VillagerTrades.PriceRange(1, 2)),
							new VillagerTrades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10490), new VillagerTrades.PriceRange(1, 2)),
							new VillagerTrades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10028), new VillagerTrades.PriceRange(1, 2)),
							new VillagerTrades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10459), new VillagerTrades.PriceRange(1, 2)),
							new VillagerTrades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10423), new VillagerTrades.PriceRange(1, 2)),
							new VillagerTrades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10222), new VillagerTrades.PriceRange(1, 2)),
							new VillagerTrades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10619), new VillagerTrades.PriceRange(1, 2)),
							new VillagerTrades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10259), new VillagerTrades.PriceRange(1, 2)),
							new VillagerTrades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10514), new VillagerTrades.PriceRange(1, 2)),
							new VillagerTrades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10113), new VillagerTrades.PriceRange(1, 2)),
							new VillagerTrades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10170), new VillagerTrades.PriceRange(1, 2)),
							new VillagerTrades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10314), new VillagerTrades.PriceRange(1, 2)),
							new VillagerTrades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10146), new VillagerTrades.PriceRange(1, 2))
						}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17058,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new VillagerTrades.Factory[]{
							new VillagerTrades.BuyItemFactory(Items.field_8276, new VillagerTrades.PriceRange(15, 20)),
							new VillagerTrades.BuyOrSellOnPriceFactory(Items.field_8107, new VillagerTrades.PriceRange(-12, -8))
						},
						2,
						new VillagerTrades.Factory[]{
							new VillagerTrades.BuyOrSellOnPriceFactory(Items.field_8102, new VillagerTrades.PriceRange(2, 3)),
							new VillagerTrades.ProcessBoughtFactory(
								Blocks.field_10255, new VillagerTrades.PriceRange(10, 10), Items.field_8145, new VillagerTrades.PriceRange(6, 10)
							)
						}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17060,
				copyToFastUtilMap(
					ImmutableMap.<Integer, VillagerTrades.Factory[]>builder()
						.put(
							1,
							new VillagerTrades.Factory[]{
								new VillagerTrades.BuyItemFactory(Items.field_8407, new VillagerTrades.PriceRange(24, 36)), new VillagerTrades.EnchantBookFactory()
							}
						)
						.put(
							2,
							new VillagerTrades.Factory[]{
								new VillagerTrades.BuyItemFactory(Items.field_8529, new VillagerTrades.PriceRange(8, 10)),
								new VillagerTrades.BuyOrSellOnPriceFactory(Items.field_8251, new VillagerTrades.PriceRange(10, 12)),
								new VillagerTrades.BuyOrSellOnPriceFactory(Blocks.field_10504, new VillagerTrades.PriceRange(3, 4))
							}
						)
						.put(
							3,
							new VillagerTrades.Factory[]{
								new VillagerTrades.BuyItemFactory(Items.field_8360, new VillagerTrades.PriceRange(2, 2)),
								new VillagerTrades.BuyOrSellOnPriceFactory(Items.field_8557, new VillagerTrades.PriceRange(10, 12)),
								new VillagerTrades.BuyOrSellOnPriceFactory(Blocks.field_10033, new VillagerTrades.PriceRange(-5, -3))
							}
						)
						.put(4, new VillagerTrades.Factory[]{new VillagerTrades.EnchantBookFactory()})
						.put(5, new VillagerTrades.Factory[]{new VillagerTrades.EnchantBookFactory()})
						.put(6, new VillagerTrades.Factory[]{new VillagerTrades.BuyOrSellOnPriceFactory(Items.field_8448, new VillagerTrades.PriceRange(20, 22))})
						.build()
				)
			);
			hashMap.put(
				VillagerProfession.field_17054,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new VillagerTrades.Factory[]{new VillagerTrades.BuyItemFactory(Items.field_8407, new VillagerTrades.PriceRange(24, 36))},
						2,
						new VillagerTrades.Factory[]{new VillagerTrades.BuyItemFactory(Items.field_8251, new VillagerTrades.PriceRange(1, 1))},
						3,
						new VillagerTrades.Factory[]{new VillagerTrades.BuyOrSellOnPriceFactory(Items.field_8895, new VillagerTrades.PriceRange(7, 11))},
						4,
						new VillagerTrades.Factory[]{
							new VillagerTrades.SellMapFactory(new VillagerTrades.PriceRange(12, 20), "Monument", MapIcon.Direction.field_98),
							new VillagerTrades.SellMapFactory(new VillagerTrades.PriceRange(16, 28), "Mansion", MapIcon.Direction.field_88)
						}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17055,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new VillagerTrades.Factory[]{
							new VillagerTrades.BuyItemFactory(Items.field_8511, new VillagerTrades.PriceRange(36, 40)),
							new VillagerTrades.BuyItemFactory(Items.field_8695, new VillagerTrades.PriceRange(8, 10))
						},
						2,
						new VillagerTrades.Factory[]{
							new VillagerTrades.BuyOrSellOnPriceFactory(Items.field_8725, new VillagerTrades.PriceRange(-4, -1)),
							new VillagerTrades.BuyOrSellOnPriceFactory(new ItemStack(Items.field_8759), new VillagerTrades.PriceRange(-2, -1))
						},
						3,
						new VillagerTrades.Factory[]{
							new VillagerTrades.BuyOrSellOnPriceFactory(Items.field_8634, new VillagerTrades.PriceRange(4, 7)),
							new VillagerTrades.BuyOrSellOnPriceFactory(Blocks.field_10171, new VillagerTrades.PriceRange(-3, -1))
						},
						4,
						new VillagerTrades.Factory[]{new VillagerTrades.BuyOrSellOnPriceFactory(Items.field_8287, new VillagerTrades.PriceRange(3, 11))}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17052,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new VillagerTrades.Factory[]{
							new VillagerTrades.BuyItemFactory(Items.field_8713, new VillagerTrades.PriceRange(16, 24)),
							new VillagerTrades.BuyOrSellOnPriceFactory(Items.field_8743, new VillagerTrades.PriceRange(4, 6))
						},
						2,
						new VillagerTrades.Factory[]{
							new VillagerTrades.BuyItemFactory(Items.field_8620, new VillagerTrades.PriceRange(7, 9)),
							new VillagerTrades.BuyOrSellOnPriceFactory(Items.field_8523, new VillagerTrades.PriceRange(10, 14))
						},
						3,
						new VillagerTrades.Factory[]{
							new VillagerTrades.BuyItemFactory(Items.field_8477, new VillagerTrades.PriceRange(3, 4)),
							new VillagerTrades.SellEnchantedToolFactory(Items.field_8058, new VillagerTrades.PriceRange(16, 19))
						},
						4,
						new VillagerTrades.Factory[]{
							new VillagerTrades.BuyOrSellOnPriceFactory(Items.field_8313, new VillagerTrades.PriceRange(5, 7)),
							new VillagerTrades.BuyOrSellOnPriceFactory(Items.field_8218, new VillagerTrades.PriceRange(9, 11)),
							new VillagerTrades.BuyOrSellOnPriceFactory(Items.field_8283, new VillagerTrades.PriceRange(5, 7)),
							new VillagerTrades.BuyOrSellOnPriceFactory(Items.field_8873, new VillagerTrades.PriceRange(11, 15))
						}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17065,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new VillagerTrades.Factory[]{
							new VillagerTrades.BuyItemFactory(Items.field_8713, new VillagerTrades.PriceRange(16, 24)),
							new VillagerTrades.BuyOrSellOnPriceFactory(Items.field_8475, new VillagerTrades.PriceRange(6, 8))
						},
						2,
						new VillagerTrades.Factory[]{
							new VillagerTrades.BuyItemFactory(Items.field_8620, new VillagerTrades.PriceRange(7, 9)),
							new VillagerTrades.SellEnchantedToolFactory(Items.field_8371, new VillagerTrades.PriceRange(9, 10))
						},
						3,
						new VillagerTrades.Factory[]{
							new VillagerTrades.BuyItemFactory(Items.field_8477, new VillagerTrades.PriceRange(3, 4)),
							new VillagerTrades.SellEnchantedToolFactory(Items.field_8802, new VillagerTrades.PriceRange(12, 15)),
							new VillagerTrades.SellEnchantedToolFactory(Items.field_8556, new VillagerTrades.PriceRange(9, 12))
						}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17064,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new VillagerTrades.Factory[]{
							new VillagerTrades.BuyItemFactory(Items.field_8713, new VillagerTrades.PriceRange(16, 24)),
							new VillagerTrades.SellEnchantedToolFactory(Items.field_8699, new VillagerTrades.PriceRange(5, 7))
						},
						2,
						new VillagerTrades.Factory[]{
							new VillagerTrades.BuyItemFactory(Items.field_8620, new VillagerTrades.PriceRange(7, 9)),
							new VillagerTrades.SellEnchantedToolFactory(Items.field_8403, new VillagerTrades.PriceRange(9, 11))
						},
						3,
						new VillagerTrades.Factory[]{
							new VillagerTrades.BuyItemFactory(Items.field_8477, new VillagerTrades.PriceRange(3, 4)),
							new VillagerTrades.SellEnchantedToolFactory(Items.field_8377, new VillagerTrades.PriceRange(12, 15))
						}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17053,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new VillagerTrades.Factory[]{
							new VillagerTrades.BuyItemFactory(Items.field_8389, new VillagerTrades.PriceRange(14, 18)),
							new VillagerTrades.BuyItemFactory(Items.field_8726, new VillagerTrades.PriceRange(14, 18))
						},
						2,
						new VillagerTrades.Factory[]{
							new VillagerTrades.BuyItemFactory(Items.field_8713, new VillagerTrades.PriceRange(16, 24)),
							new VillagerTrades.BuyOrSellOnPriceFactory(Items.field_8261, new VillagerTrades.PriceRange(-7, -5)),
							new VillagerTrades.BuyOrSellOnPriceFactory(Items.field_8544, new VillagerTrades.PriceRange(-8, -6))
						}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17059,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new VillagerTrades.Factory[]{
							new VillagerTrades.BuyItemFactory(Items.field_8745, new VillagerTrades.PriceRange(9, 12)),
							new VillagerTrades.BuyOrSellOnPriceFactory(Items.field_8570, new VillagerTrades.PriceRange(2, 4))
						},
						2,
						new VillagerTrades.Factory[]{new VillagerTrades.SellEnchantedToolFactory(Items.field_8577, new VillagerTrades.PriceRange(7, 12))},
						3,
						new VillagerTrades.Factory[]{new VillagerTrades.BuyOrSellOnPriceFactory(Items.field_8175, new VillagerTrades.PriceRange(8, 10))}
					)
				)
			);
		}
	);

	private static Int2ObjectMap<VillagerTrades.Factory[]> copyToFastUtilMap(ImmutableMap<Integer, VillagerTrades.Factory[]> immutableMap) {
		return new Int2ObjectOpenHashMap<>(immutableMap);
	}

	static class BuyItemFactory implements VillagerTrades.Factory {
		public Item bought;
		public VillagerTrades.PriceRange range;

		public BuyItemFactory(ItemProvider itemProvider, VillagerTrades.PriceRange priceRange) {
			this.bought = itemProvider.getItem();
			this.range = priceRange;
		}

		@Override
		public VillagerRecipe create(Villager villager, Random random) {
			ItemStack itemStack = new ItemStack(this.bought, this.range == null ? 1 : this.range.getPrice(random));
			return new VillagerRecipe(itemStack, new ItemStack(Items.field_8687));
		}
	}

	static class BuyOrSellOnPriceFactory implements VillagerTrades.Factory {
		public ItemStack item;
		public VillagerTrades.PriceRange range;

		public BuyOrSellOnPriceFactory(Block block, VillagerTrades.PriceRange priceRange) {
			this(new ItemStack(block), priceRange);
		}

		public BuyOrSellOnPriceFactory(Item item, VillagerTrades.PriceRange priceRange) {
			this(new ItemStack(item), priceRange);
		}

		public BuyOrSellOnPriceFactory(ItemStack itemStack, VillagerTrades.PriceRange priceRange) {
			this.item = itemStack;
			this.range = priceRange;
		}

		@Override
		public VillagerRecipe create(Villager villager, Random random) {
			int i = 1;
			if (this.range != null) {
				i = this.range.getPrice(random);
			}

			ItemStack itemStack;
			ItemStack itemStack2;
			if (i < 0) {
				itemStack = new ItemStack(Items.field_8687);
				itemStack2 = new ItemStack(this.item.getItem(), -i);
			} else {
				itemStack = new ItemStack(Items.field_8687, i);
				itemStack2 = new ItemStack(this.item.getItem());
			}

			return new VillagerRecipe(itemStack, itemStack2);
		}
	}

	static class EnchantBookFactory implements VillagerTrades.Factory {
		private EnchantBookFactory() {
		}

		@Override
		public VillagerRecipe create(Villager villager, Random random) {
			Enchantment enchantment = Registry.ENCHANTMENT.getRandom(random);
			int i = MathHelper.nextInt(random, enchantment.getMinimumLevel(), enchantment.getMaximumLevel());
			ItemStack itemStack = EnchantedBookItem.method_7808(new InfoEnchantment(enchantment, i));
			int j = 2 + random.nextInt(5 + i * 10) + 3 * i;
			if (enchantment.isLootOnly()) {
				j *= 2;
			}

			if (j > 64) {
				j = 64;
			}

			return new VillagerRecipe(new ItemStack(Items.field_8529), new ItemStack(Items.field_8687, j), itemStack);
		}
	}

	public interface Factory {
		@Nullable
		VillagerRecipe create(Villager villager, Random random);
	}

	static class PriceRange {
		final int lower;
		final int range;

		public PriceRange(int i, int j) {
			this.lower = i;
			this.range = 1 + Math.max(0, j - i);
			if (j < i) {
				VillagerTrades.LOGGER.warn("PriceRange({}, {}) invalid, {} smaller than {}", i, j, j, i);
			}
		}

		public int getPrice(Random random) {
			return this.lower + random.nextInt(this.range);
		}
	}

	static class ProcessBoughtFactory implements VillagerTrades.Factory {
		public ItemStack bought;
		public VillagerTrades.PriceRange boughtRange;
		public ItemStack result;
		public VillagerTrades.PriceRange resultRange;

		public ProcessBoughtFactory(ItemProvider itemProvider, VillagerTrades.PriceRange priceRange, Item item, VillagerTrades.PriceRange priceRange2) {
			this.bought = new ItemStack(itemProvider);
			this.boughtRange = priceRange;
			this.result = new ItemStack(item);
			this.resultRange = priceRange2;
		}

		@Nullable
		@Override
		public VillagerRecipe create(Villager villager, Random random) {
			int i = this.boughtRange.getPrice(random);
			int j = this.resultRange.getPrice(random);
			return new VillagerRecipe(new ItemStack(this.bought.getItem(), i), new ItemStack(Items.field_8687), new ItemStack(this.result.getItem(), j));
		}
	}

	static class SellEnchantedToolFactory implements VillagerTrades.Factory {
		public ItemStack sold;
		public VillagerTrades.PriceRange range;

		public SellEnchantedToolFactory(Item item, VillagerTrades.PriceRange priceRange) {
			this.sold = new ItemStack(item);
			this.range = priceRange;
		}

		@Override
		public VillagerRecipe create(Villager villager, Random random) {
			int i = 1;
			if (this.range != null) {
				i = this.range.getPrice(random);
			}

			ItemStack itemStack = new ItemStack(Items.field_8687, i);
			ItemStack itemStack2 = EnchantmentHelper.enchant(random, new ItemStack(this.sold.getItem()), 5 + random.nextInt(15), false);
			return new VillagerRecipe(itemStack, itemStack2);
		}
	}

	static class SellMapFactory implements VillagerTrades.Factory {
		public VillagerTrades.PriceRange range;
		public String structure;
		public MapIcon.Direction field_7473;

		public SellMapFactory(VillagerTrades.PriceRange priceRange, String string, MapIcon.Direction direction) {
			this.range = priceRange;
			this.structure = string;
			this.field_7473 = direction;
		}

		@Nullable
		@Override
		public VillagerRecipe create(Villager villager, Random random) {
			int i = this.range.getPrice(random);
			World world = villager.getVillagerWorld();
			BlockPos blockPos = world.locateStructure(this.structure, villager.getVillagerPos(), 100, true);
			if (blockPos != null) {
				ItemStack itemStack = FilledMapItem.method_8005(world, blockPos.getX(), blockPos.getZ(), (byte)2, true, true);
				FilledMapItem.method_8002(world, itemStack);
				MapState.method_110(itemStack, blockPos, "+", this.field_7473);
				itemStack.setDisplayName(new TranslatableTextComponent("filled_map." + this.structure.toLowerCase(Locale.ROOT)));
				return new VillagerRecipe(new ItemStack(Items.field_8687, i), new ItemStack(Items.field_8251), itemStack);
			} else {
				return null;
			}
		}
	}
}
