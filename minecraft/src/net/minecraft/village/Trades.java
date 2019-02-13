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

public class Trades {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Map<VillagerProfession, Int2ObjectMap<Trades.Factory[]>> PROFESSION_TO_LEVELED_TRADE = SystemUtil.consume(
		Maps.<VillagerProfession, Int2ObjectMap<Trades.Factory[]>>newHashMap(),
		hashMap -> {
			hashMap.put(
				VillagerProfession.field_17056,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new Trades.Factory[]{
							new Trades.BuyItemFactory(Items.field_8861, new Trades.PriceRange(18, 22)),
							new Trades.BuyItemFactory(Items.field_8567, new Trades.PriceRange(15, 19)),
							new Trades.BuyItemFactory(Items.field_8179, new Trades.PriceRange(15, 19)),
							new Trades.BuyOrSellOnPriceFactory(Items.field_8229, new Trades.PriceRange(-4, -2))
						},
						2,
						new Trades.Factory[]{
							new Trades.BuyItemFactory(Blocks.field_10261, new Trades.PriceRange(8, 13)),
							new Trades.BuyOrSellOnPriceFactory(Items.field_8741, new Trades.PriceRange(-3, -2))
						},
						3,
						new Trades.Factory[]{
							new Trades.BuyItemFactory(Blocks.field_10545, new Trades.PriceRange(7, 12)),
							new Trades.BuyOrSellOnPriceFactory(Items.field_8279, new Trades.PriceRange(-7, -5))
						},
						4,
						new Trades.Factory[]{
							new Trades.BuyOrSellOnPriceFactory(Items.field_8423, new Trades.PriceRange(-10, -6)),
							new Trades.BuyOrSellOnPriceFactory(Blocks.field_10183, new Trades.PriceRange(1, 1))
						}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17057,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new Trades.Factory[]{
							new Trades.BuyItemFactory(Items.field_8276, new Trades.PriceRange(15, 20)),
							new Trades.BuyItemFactory(Items.field_8713, new Trades.PriceRange(16, 24)),
							new Trades.ProcessBoughtFactory(Items.field_8429, new Trades.PriceRange(6, 6), Items.field_8373, new Trades.PriceRange(6, 6)),
							new Trades.ProcessBoughtFactory(Items.field_8209, new Trades.PriceRange(6, 6), Items.field_8509, new Trades.PriceRange(6, 6))
						},
						2,
						new Trades.Factory[]{new Trades.SellEnchantedToolFactory(Items.field_8378, new Trades.PriceRange(7, 8))}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17063,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new Trades.Factory[]{
							new Trades.BuyItemFactory(Blocks.field_10446, new Trades.PriceRange(16, 22)),
							new Trades.BuyOrSellOnPriceFactory(Items.field_8868, new Trades.PriceRange(3, 4))
						},
						2,
						new Trades.Factory[]{
							new Trades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10446), new Trades.PriceRange(1, 2)),
							new Trades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10095), new Trades.PriceRange(1, 2)),
							new Trades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10215), new Trades.PriceRange(1, 2)),
							new Trades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10294), new Trades.PriceRange(1, 2)),
							new Trades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10490), new Trades.PriceRange(1, 2)),
							new Trades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10028), new Trades.PriceRange(1, 2)),
							new Trades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10459), new Trades.PriceRange(1, 2)),
							new Trades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10423), new Trades.PriceRange(1, 2)),
							new Trades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10222), new Trades.PriceRange(1, 2)),
							new Trades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10619), new Trades.PriceRange(1, 2)),
							new Trades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10259), new Trades.PriceRange(1, 2)),
							new Trades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10514), new Trades.PriceRange(1, 2)),
							new Trades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10113), new Trades.PriceRange(1, 2)),
							new Trades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10170), new Trades.PriceRange(1, 2)),
							new Trades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10314), new Trades.PriceRange(1, 2)),
							new Trades.BuyOrSellOnPriceFactory(new ItemStack(Blocks.field_10146), new Trades.PriceRange(1, 2))
						}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17058,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new Trades.Factory[]{
							new Trades.BuyItemFactory(Items.field_8276, new Trades.PriceRange(15, 20)),
							new Trades.BuyOrSellOnPriceFactory(Items.field_8107, new Trades.PriceRange(-12, -8))
						},
						2,
						new Trades.Factory[]{
							new Trades.BuyOrSellOnPriceFactory(Items.field_8102, new Trades.PriceRange(2, 3)),
							new Trades.ProcessBoughtFactory(Blocks.field_10255, new Trades.PriceRange(10, 10), Items.field_8145, new Trades.PriceRange(6, 10))
						}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17060,
				copyToFastUtilMap(
					ImmutableMap.<Integer, Trades.Factory[]>builder()
						.put(1, new Trades.Factory[]{new Trades.BuyItemFactory(Items.field_8407, new Trades.PriceRange(24, 36)), new Trades.EnchantBookFactory()})
						.put(
							2,
							new Trades.Factory[]{
								new Trades.BuyItemFactory(Items.field_8529, new Trades.PriceRange(8, 10)),
								new Trades.BuyOrSellOnPriceFactory(Items.field_8251, new Trades.PriceRange(10, 12)),
								new Trades.BuyOrSellOnPriceFactory(Blocks.field_10504, new Trades.PriceRange(3, 4))
							}
						)
						.put(
							3,
							new Trades.Factory[]{
								new Trades.BuyItemFactory(Items.field_8360, new Trades.PriceRange(2, 2)),
								new Trades.BuyOrSellOnPriceFactory(Items.field_8557, new Trades.PriceRange(10, 12)),
								new Trades.BuyOrSellOnPriceFactory(Blocks.field_10033, new Trades.PriceRange(-5, -3))
							}
						)
						.put(4, new Trades.Factory[]{new Trades.EnchantBookFactory()})
						.put(5, new Trades.Factory[]{new Trades.EnchantBookFactory()})
						.put(6, new Trades.Factory[]{new Trades.BuyOrSellOnPriceFactory(Items.field_8448, new Trades.PriceRange(20, 22))})
						.build()
				)
			);
			hashMap.put(
				VillagerProfession.field_17054,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new Trades.Factory[]{new Trades.BuyItemFactory(Items.field_8407, new Trades.PriceRange(24, 36))},
						2,
						new Trades.Factory[]{new Trades.BuyItemFactory(Items.field_8251, new Trades.PriceRange(1, 1))},
						3,
						new Trades.Factory[]{new Trades.BuyOrSellOnPriceFactory(Items.field_8895, new Trades.PriceRange(7, 11))},
						4,
						new Trades.Factory[]{
							new Trades.SellMapFactory(new Trades.PriceRange(12, 20), "Monument", MapIcon.Type.field_98),
							new Trades.SellMapFactory(new Trades.PriceRange(16, 28), "Mansion", MapIcon.Type.field_88)
						}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17055,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new Trades.Factory[]{
							new Trades.BuyItemFactory(Items.field_8511, new Trades.PriceRange(36, 40)), new Trades.BuyItemFactory(Items.field_8695, new Trades.PriceRange(8, 10))
						},
						2,
						new Trades.Factory[]{
							new Trades.BuyOrSellOnPriceFactory(Items.field_8725, new Trades.PriceRange(-4, -1)),
							new Trades.BuyOrSellOnPriceFactory(new ItemStack(Items.field_8759), new Trades.PriceRange(-2, -1))
						},
						3,
						new Trades.Factory[]{
							new Trades.BuyOrSellOnPriceFactory(Items.field_8634, new Trades.PriceRange(4, 7)),
							new Trades.BuyOrSellOnPriceFactory(Blocks.field_10171, new Trades.PriceRange(-3, -1))
						},
						4,
						new Trades.Factory[]{new Trades.BuyOrSellOnPriceFactory(Items.field_8287, new Trades.PriceRange(3, 11))}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17052,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new Trades.Factory[]{
							new Trades.BuyItemFactory(Items.field_8713, new Trades.PriceRange(16, 24)),
							new Trades.BuyOrSellOnPriceFactory(Items.field_8743, new Trades.PriceRange(4, 6))
						},
						2,
						new Trades.Factory[]{
							new Trades.BuyItemFactory(Items.field_8620, new Trades.PriceRange(7, 9)),
							new Trades.BuyOrSellOnPriceFactory(Items.field_8523, new Trades.PriceRange(10, 14))
						},
						3,
						new Trades.Factory[]{
							new Trades.BuyItemFactory(Items.field_8477, new Trades.PriceRange(3, 4)),
							new Trades.SellEnchantedToolFactory(Items.field_8058, new Trades.PriceRange(16, 19))
						},
						4,
						new Trades.Factory[]{
							new Trades.BuyOrSellOnPriceFactory(Items.field_8313, new Trades.PriceRange(5, 7)),
							new Trades.BuyOrSellOnPriceFactory(Items.field_8218, new Trades.PriceRange(9, 11)),
							new Trades.BuyOrSellOnPriceFactory(Items.field_8283, new Trades.PriceRange(5, 7)),
							new Trades.BuyOrSellOnPriceFactory(Items.field_8873, new Trades.PriceRange(11, 15))
						}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17065,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new Trades.Factory[]{
							new Trades.BuyItemFactory(Items.field_8713, new Trades.PriceRange(16, 24)),
							new Trades.BuyOrSellOnPriceFactory(Items.field_8475, new Trades.PriceRange(6, 8))
						},
						2,
						new Trades.Factory[]{
							new Trades.BuyItemFactory(Items.field_8620, new Trades.PriceRange(7, 9)),
							new Trades.SellEnchantedToolFactory(Items.field_8371, new Trades.PriceRange(9, 10))
						},
						3,
						new Trades.Factory[]{
							new Trades.BuyItemFactory(Items.field_8477, new Trades.PriceRange(3, 4)),
							new Trades.SellEnchantedToolFactory(Items.field_8802, new Trades.PriceRange(12, 15)),
							new Trades.SellEnchantedToolFactory(Items.field_8556, new Trades.PriceRange(9, 12))
						}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17064,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new Trades.Factory[]{
							new Trades.BuyItemFactory(Items.field_8713, new Trades.PriceRange(16, 24)),
							new Trades.SellEnchantedToolFactory(Items.field_8699, new Trades.PriceRange(5, 7))
						},
						2,
						new Trades.Factory[]{
							new Trades.BuyItemFactory(Items.field_8620, new Trades.PriceRange(7, 9)),
							new Trades.SellEnchantedToolFactory(Items.field_8403, new Trades.PriceRange(9, 11))
						},
						3,
						new Trades.Factory[]{
							new Trades.BuyItemFactory(Items.field_8477, new Trades.PriceRange(3, 4)),
							new Trades.SellEnchantedToolFactory(Items.field_8377, new Trades.PriceRange(12, 15))
						}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17053,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new Trades.Factory[]{
							new Trades.BuyItemFactory(Items.field_8389, new Trades.PriceRange(14, 18)), new Trades.BuyItemFactory(Items.field_8726, new Trades.PriceRange(14, 18))
						},
						2,
						new Trades.Factory[]{
							new Trades.BuyItemFactory(Items.field_8713, new Trades.PriceRange(16, 24)),
							new Trades.BuyOrSellOnPriceFactory(Items.field_8261, new Trades.PriceRange(-7, -5)),
							new Trades.BuyOrSellOnPriceFactory(Items.field_8544, new Trades.PriceRange(-8, -6))
						}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17059,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new Trades.Factory[]{
							new Trades.BuyItemFactory(Items.field_8745, new Trades.PriceRange(9, 12)),
							new Trades.BuyOrSellOnPriceFactory(Items.field_8570, new Trades.PriceRange(2, 4))
						},
						2,
						new Trades.Factory[]{new Trades.SellEnchantedToolFactory(Items.field_8577, new Trades.PriceRange(7, 12))},
						3,
						new Trades.Factory[]{new Trades.BuyOrSellOnPriceFactory(Items.field_8175, new Trades.PriceRange(8, 10))}
					)
				)
			);
		}
	);
	public static final Int2ObjectMap<Trades.Factory[]> WANDERING_TRADER_TRADES = copyToFastUtilMap(
		ImmutableMap.of(
			1,
			new Trades.Factory[]{
				new Trades.BuyOrSellOnPriceFactory(Items.field_17498, new Trades.PriceRange(2, 2)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8777, new Trades.PriceRange(4, 4)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8801, new Trades.PriceRange(2, 2)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8864, new Trades.PriceRange(5, 5)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8471, new Trades.PriceRange(1, 1)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_17531, new Trades.PriceRange(1, 1)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_17518, new Trades.PriceRange(1, 1)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_17532, new Trades.PriceRange(3, 3)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_17520, new Trades.PriceRange(3, 3)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8491, new Trades.PriceRange(1, 1)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8880, new Trades.PriceRange(1, 1)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_17499, new Trades.PriceRange(1, 1)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_17500, new Trades.PriceRange(1, 1)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_17501, new Trades.PriceRange(1, 1)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_17502, new Trades.PriceRange(1, 1)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_17509, new Trades.PriceRange(1, 1)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_17510, new Trades.PriceRange(1, 1)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_17511, new Trades.PriceRange(1, 1)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_17512, new Trades.PriceRange(1, 1)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_17513, new Trades.PriceRange(1, 1)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_17514, new Trades.PriceRange(1, 1)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8317, new Trades.PriceRange(1, 1)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8309, new Trades.PriceRange(1, 1)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8706, new Trades.PriceRange(1, 1)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8188, new Trades.PriceRange(1, 1)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_17539, new Trades.PriceRange(5, 5)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_17537, new Trades.PriceRange(5, 5)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_17540, new Trades.PriceRange(5, 5)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_17538, new Trades.PriceRange(5, 5)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_17535, new Trades.PriceRange(5, 5)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_17536, new Trades.PriceRange(5, 5)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8264, new Trades.PriceRange(-3, -3)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8446, new Trades.PriceRange(-3, -3)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8345, new Trades.PriceRange(-3, -3)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8330, new Trades.PriceRange(-3, -3)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8226, new Trades.PriceRange(-3, -3)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8408, new Trades.PriceRange(-3, -3)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8851, new Trades.PriceRange(-3, -3)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8669, new Trades.PriceRange(-3, -3)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8192, new Trades.PriceRange(-3, -3)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8298, new Trades.PriceRange(-3, -3)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8296, new Trades.PriceRange(-3, -3)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8273, new Trades.PriceRange(-3, -3)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8131, new Trades.PriceRange(-3, -3)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8492, new Trades.PriceRange(-3, -3)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8099, new Trades.PriceRange(-3, -3)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8632, new Trades.PriceRange(-3, -3)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8474, new Trades.PriceRange(3, 3)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8883, new Trades.PriceRange(3, 3)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8278, new Trades.PriceRange(3, 3)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8104, new Trades.PriceRange(3, 3)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8402, new Trades.PriceRange(3, 3)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_17523, new Trades.PriceRange(1, 1)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_17516, new Trades.PriceRange(1, 1)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_17517, new Trades.PriceRange(1, 1)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_17524, new Trades.PriceRange(-2, -2)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8858, new Trades.PriceRange(-8, -8)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8200, new Trades.PriceRange(-4, -4))
			},
			2,
			new Trades.Factory[]{
				new Trades.BuyOrSellOnPriceFactory(Items.field_8478, new Trades.PriceRange(5, 5)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8108, new Trades.PriceRange(5, 5)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8081, new Trades.PriceRange(3, 3)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8178, new Trades.PriceRange(6, 6)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8054, new Trades.PriceRange(1, 1)),
				new Trades.BuyOrSellOnPriceFactory(Items.field_8382, new Trades.PriceRange(3, 3))
			}
		)
	);

	private static Int2ObjectMap<Trades.Factory[]> copyToFastUtilMap(ImmutableMap<Integer, Trades.Factory[]> immutableMap) {
		return new Int2ObjectOpenHashMap<>(immutableMap);
	}

	static class BuyItemFactory implements Trades.Factory {
		public final Item bought;
		public final Trades.PriceRange range;

		public BuyItemFactory(ItemProvider itemProvider, Trades.PriceRange priceRange) {
			this.bought = itemProvider.getItem();
			this.range = priceRange;
		}

		@Override
		public TraderRecipe create(Trader trader, Random random) {
			ItemStack itemStack = new ItemStack(this.bought, this.range == null ? 1 : this.range.getPrice(random));
			return new TraderRecipe(itemStack, new ItemStack(Items.field_8687));
		}
	}

	static class BuyOrSellOnPriceFactory implements Trades.Factory {
		public final ItemStack item;
		public final Trades.PriceRange range;

		public BuyOrSellOnPriceFactory(Block block, Trades.PriceRange priceRange) {
			this(new ItemStack(block), priceRange);
		}

		public BuyOrSellOnPriceFactory(Item item, Trades.PriceRange priceRange) {
			this(new ItemStack(item), priceRange);
		}

		public BuyOrSellOnPriceFactory(ItemStack itemStack, Trades.PriceRange priceRange) {
			this.item = itemStack;
			this.range = priceRange;
		}

		@Override
		public TraderRecipe create(Trader trader, Random random) {
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

			return new TraderRecipe(itemStack, itemStack2);
		}
	}

	static class EnchantBookFactory implements Trades.Factory {
		private EnchantBookFactory() {
		}

		@Override
		public TraderRecipe create(Trader trader, Random random) {
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

			return new TraderRecipe(new ItemStack(Items.field_8529), new ItemStack(Items.field_8687, j), itemStack);
		}
	}

	public interface Factory {
		@Nullable
		TraderRecipe create(Trader trader, Random random);
	}

	static class PriceRange {
		private final int lower;
		private final int range;

		public PriceRange(int i, int j) {
			this.lower = i;
			this.range = 1 + Math.max(0, j - i);
			if (j < i) {
				Trades.LOGGER.warn("PriceRange({}, {}) invalid, {} smaller than {}", i, j, j, i);
			}
		}

		public int getPrice(Random random) {
			return this.lower + random.nextInt(this.range);
		}
	}

	static class ProcessBoughtFactory implements Trades.Factory {
		public final ItemStack bought;
		public final Trades.PriceRange boughtRange;
		public final ItemStack result;
		public final Trades.PriceRange resultRange;

		public ProcessBoughtFactory(ItemProvider itemProvider, Trades.PriceRange priceRange, Item item, Trades.PriceRange priceRange2) {
			this.bought = new ItemStack(itemProvider);
			this.boughtRange = priceRange;
			this.result = new ItemStack(item);
			this.resultRange = priceRange2;
		}

		@Nullable
		@Override
		public TraderRecipe create(Trader trader, Random random) {
			int i = this.boughtRange.getPrice(random);
			int j = this.resultRange.getPrice(random);
			return new TraderRecipe(new ItemStack(this.bought.getItem(), i), new ItemStack(Items.field_8687), new ItemStack(this.result.getItem(), j));
		}
	}

	static class SellEnchantedToolFactory implements Trades.Factory {
		public final ItemStack sold;
		public final Trades.PriceRange range;

		public SellEnchantedToolFactory(Item item, Trades.PriceRange priceRange) {
			this.sold = new ItemStack(item);
			this.range = priceRange;
		}

		@Override
		public TraderRecipe create(Trader trader, Random random) {
			int i = 1;
			if (this.range != null) {
				i = this.range.getPrice(random);
			}

			ItemStack itemStack = new ItemStack(Items.field_8687, i);
			ItemStack itemStack2 = EnchantmentHelper.enchant(random, new ItemStack(this.sold.getItem()), 5 + random.nextInt(15), false);
			return new TraderRecipe(itemStack, itemStack2);
		}
	}

	static class SellMapFactory implements Trades.Factory {
		public final Trades.PriceRange range;
		public final String structure;
		public final MapIcon.Type field_7473;

		public SellMapFactory(Trades.PriceRange priceRange, String string, MapIcon.Type type) {
			this.range = priceRange;
			this.structure = string;
			this.field_7473 = type;
		}

		@Nullable
		@Override
		public TraderRecipe create(Trader trader, Random random) {
			int i = this.range.getPrice(random);
			World world = trader.getTraderWorld();
			BlockPos blockPos = world.locateStructure(this.structure, trader.getTraderPos(), 100, true);
			if (blockPos != null) {
				ItemStack itemStack = FilledMapItem.method_8005(world, blockPos.getX(), blockPos.getZ(), (byte)2, true, true);
				FilledMapItem.method_8002(world, itemStack);
				MapState.method_110(itemStack, blockPos, "+", this.field_7473);
				itemStack.setDisplayName(new TranslatableTextComponent("filled_map." + this.structure.toLowerCase(Locale.ROOT)));
				return new TraderRecipe(new ItemStack(Items.field_8687, i), new ItemStack(Items.field_8251), itemStack);
			} else {
				return null;
			}
		}
	}
}
