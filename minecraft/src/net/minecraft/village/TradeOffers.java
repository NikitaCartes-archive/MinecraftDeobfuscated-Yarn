package net.minecraft.village;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.InfoEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.DyeItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class TradeOffers {
	public static final Map<VillagerProfession, Int2ObjectMap<TradeOffers.Factory[]>> PROFESSION_TO_LEVELED_TRADE = SystemUtil.consume(
		Maps.<VillagerProfession, Int2ObjectMap<TradeOffers.Factory[]>>newHashMap(),
		hashMap -> {
			hashMap.put(
				VillagerProfession.field_17056,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8861, 20, 8, 2),
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8567, 26, 8, 2),
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8179, 22, 8, 2),
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8186, 15, 8, 2),
							new TradeOffers.SellItemFactory(Items.field_8229, 1, 6, 8, 1)
						},
						2,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.field_10261, 6, 6, 10),
							new TradeOffers.SellItemFactory(Items.field_8741, 1, 4, 5),
							new TradeOffers.SellItemFactory(Items.field_8279, 1, 4, 8, 5)
						},
						3,
						new TradeOffers.Factory[]{
							new TradeOffers.SellItemFactory(Items.field_8423, 3, 18, 10), new TradeOffers.BuyForOneEmeraldFactory(Blocks.field_10545, 4, 6, 20)
						},
						4,
						new TradeOffers.Factory[]{
							new TradeOffers.SellItemFactory(Blocks.field_10183, 1, 1, 6, 15),
							new TradeOffers.SellSuspiciousStewFactory(StatusEffects.field_5904, 160, 15),
							new TradeOffers.SellSuspiciousStewFactory(StatusEffects.field_5913, 160, 15),
							new TradeOffers.SellSuspiciousStewFactory(StatusEffects.field_5911, 140, 15),
							new TradeOffers.SellSuspiciousStewFactory(StatusEffects.field_5919, 120, 15),
							new TradeOffers.SellSuspiciousStewFactory(StatusEffects.field_5899, 280, 15),
							new TradeOffers.SellSuspiciousStewFactory(StatusEffects.field_5922, 7, 15)
						},
						5,
						new TradeOffers.Factory[]{new TradeOffers.SellItemFactory(Items.field_8071, 3, 3, 30), new TradeOffers.SellItemFactory(Items.field_8597, 4, 3, 30)}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17057,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8276, 20, 8, 2),
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8713, 10, 8, 2),
							new TradeOffers.ProcessItemFactory(Items.field_8429, 6, Items.field_8373, 6, 8, 1),
							new TradeOffers.SellItemFactory(Items.field_8666, 3, 1, 8, 1)
						},
						2,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8429, 15, 8, 10),
							new TradeOffers.ProcessItemFactory(Items.field_8209, 6, Items.field_8509, 6, 8, 5),
							new TradeOffers.SellItemFactory(Items.CAMPFIRE, 2, 1, 5)
						},
						3,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8209, 13, 8, 20), new TradeOffers.SellEnchantedToolFactory(Items.field_8378, 3, 2, 10, 0.2F)
						},
						4,
						new TradeOffers.Factory[]{new TradeOffers.BuyForOneEmeraldFactory(Items.field_8846, 6, 6, 30)},
						5,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8323, 4, 6, 30),
							new TradeOffers.TypeAwareBuyForOneEmeraldFactory(
								1,
								6,
								30,
								ImmutableMap.<VillagerType, Item>builder()
									.put(VillagerType.PLAINS, Items.field_8533)
									.put(VillagerType.TAIGA, Items.field_8486)
									.put(VillagerType.SNOW, Items.field_8486)
									.put(VillagerType.DESERT, Items.field_8730)
									.put(VillagerType.JUNGLE, Items.field_8730)
									.put(VillagerType.SAVANNA, Items.field_8094)
									.put(VillagerType.SWAMP, Items.field_8138)
									.build()
							)
						}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17063,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.field_10446, 18, 8, 2),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.field_10113, 18, 8, 2),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.field_10146, 18, 8, 2),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.field_10423, 18, 8, 2),
							new TradeOffers.SellItemFactory(Items.field_8868, 2, 1, 1)
						},
						2,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8446, 12, 8, 10),
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8298, 12, 8, 10),
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8226, 12, 8, 10),
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8273, 12, 8, 10),
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8131, 12, 8, 10),
							new TradeOffers.SellItemFactory(Blocks.field_10446, 1, 1, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10095, 1, 1, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10215, 1, 1, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10294, 1, 1, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10490, 1, 1, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10028, 1, 1, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10459, 1, 1, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10423, 1, 1, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10222, 1, 1, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10619, 1, 1, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10259, 1, 1, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10514, 1, 1, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10113, 1, 1, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10170, 1, 1, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10314, 1, 1, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10146, 1, 1, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10466, 1, 4, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_9977, 1, 4, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10482, 1, 4, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10290, 1, 4, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10512, 1, 4, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10040, 1, 4, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10393, 1, 4, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10591, 1, 4, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10209, 1, 4, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10433, 1, 4, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10510, 1, 4, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10043, 1, 4, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10473, 1, 4, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10338, 1, 4, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10536, 1, 4, 8, 5),
							new TradeOffers.SellItemFactory(Blocks.field_10106, 1, 4, 8, 5)
						},
						3,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8192, 12, 8, 20),
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8851, 12, 8, 20),
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8492, 12, 8, 20),
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8264, 12, 8, 20),
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8330, 12, 8, 20),
							new TradeOffers.SellItemFactory(Blocks.field_10120, 3, 1, 6, 10),
							new TradeOffers.SellItemFactory(Blocks.field_10356, 3, 1, 6, 10),
							new TradeOffers.SellItemFactory(Blocks.field_10069, 3, 1, 6, 10),
							new TradeOffers.SellItemFactory(Blocks.field_10461, 3, 1, 6, 10),
							new TradeOffers.SellItemFactory(Blocks.field_10527, 3, 1, 6, 10),
							new TradeOffers.SellItemFactory(Blocks.field_10288, 3, 1, 6, 10),
							new TradeOffers.SellItemFactory(Blocks.field_10109, 3, 1, 6, 10),
							new TradeOffers.SellItemFactory(Blocks.field_10141, 3, 1, 6, 10),
							new TradeOffers.SellItemFactory(Blocks.field_10561, 3, 1, 6, 10),
							new TradeOffers.SellItemFactory(Blocks.field_10621, 3, 1, 6, 10),
							new TradeOffers.SellItemFactory(Blocks.field_10326, 3, 1, 6, 10),
							new TradeOffers.SellItemFactory(Blocks.field_10180, 3, 1, 6, 10),
							new TradeOffers.SellItemFactory(Blocks.field_10230, 3, 1, 6, 10),
							new TradeOffers.SellItemFactory(Blocks.field_10410, 3, 1, 6, 10),
							new TradeOffers.SellItemFactory(Blocks.field_10610, 3, 1, 6, 10),
							new TradeOffers.SellItemFactory(Blocks.field_10019, 3, 1, 6, 10)
						},
						4,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8099, 12, 8, 30),
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8296, 12, 8, 30),
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8345, 12, 8, 30),
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8408, 12, 8, 30),
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8669, 12, 8, 30),
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8632, 12, 8, 30),
							new TradeOffers.SellItemFactory(Items.field_8539, 3, 1, 6, 15),
							new TradeOffers.SellItemFactory(Items.field_8128, 3, 1, 6, 15),
							new TradeOffers.SellItemFactory(Items.field_8379, 3, 1, 6, 15),
							new TradeOffers.SellItemFactory(Items.field_8586, 3, 1, 6, 15),
							new TradeOffers.SellItemFactory(Items.field_8329, 3, 1, 6, 15),
							new TradeOffers.SellItemFactory(Items.field_8295, 3, 1, 6, 15),
							new TradeOffers.SellItemFactory(Items.field_8778, 3, 1, 6, 15),
							new TradeOffers.SellItemFactory(Items.field_8617, 3, 1, 6, 15),
							new TradeOffers.SellItemFactory(Items.field_8572, 3, 1, 6, 15),
							new TradeOffers.SellItemFactory(Items.field_8405, 3, 1, 6, 15),
							new TradeOffers.SellItemFactory(Items.field_8671, 3, 1, 6, 15),
							new TradeOffers.SellItemFactory(Items.field_8629, 3, 1, 6, 15),
							new TradeOffers.SellItemFactory(Items.field_8124, 3, 1, 6, 15),
							new TradeOffers.SellItemFactory(Items.field_8049, 3, 1, 6, 15),
							new TradeOffers.SellItemFactory(Items.field_8824, 3, 1, 6, 15),
							new TradeOffers.SellItemFactory(Items.field_8855, 3, 1, 6, 15)
						},
						5,
						new TradeOffers.Factory[]{new TradeOffers.SellItemFactory(Items.field_8892, 2, 3, 30)}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17058,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8600, 32, 8, 2),
							new TradeOffers.SellItemFactory(Items.field_8107, 1, 16, 1),
							new TradeOffers.ProcessItemFactory(Blocks.field_10255, 10, Items.field_8145, 10, 6, 1)
						},
						2,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8145, 26, 6, 10), new TradeOffers.SellItemFactory(Items.field_8102, 2, 1, 5)
						},
						3,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8276, 14, 8, 20), new TradeOffers.SellItemFactory(Items.field_8399, 3, 1, 10)
						},
						4,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8153, 24, 8, 30), new TradeOffers.SellEnchantedToolFactory(Items.field_8102, 2, 2, 15)
						},
						5,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.TRIPWIRE_HOOK, 8, 6, 30),
							new TradeOffers.SellEnchantedToolFactory(Items.field_8399, 3, 2, 15),
							new TradeOffers.SellPotionHoldingItemFactory(Items.field_8107, 5, Items.field_8087, 5, 2, 6, 30)
						}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17060,
				copyToFastUtilMap(
					ImmutableMap.<Integer, TradeOffers.Factory[]>builder()
						.put(
							1,
							new TradeOffers.Factory[]{
								new TradeOffers.BuyForOneEmeraldFactory(Items.field_8407, 24, 8, 2),
								new TradeOffers.EnchantBookFactory(1),
								new TradeOffers.SellItemFactory(Blocks.field_10504, 6, 3, 6, 1)
							}
						)
						.put(
							2,
							new TradeOffers.Factory[]{
								new TradeOffers.BuyForOneEmeraldFactory(Items.field_8529, 4, 6, 10),
								new TradeOffers.EnchantBookFactory(5),
								new TradeOffers.SellItemFactory(Items.LANTERN, 1, 1, 5)
							}
						)
						.put(
							3,
							new TradeOffers.Factory[]{
								new TradeOffers.BuyForOneEmeraldFactory(Items.field_8794, 5, 6, 20),
								new TradeOffers.EnchantBookFactory(10),
								new TradeOffers.SellItemFactory(Items.GLASS, 1, 4, 10)
							}
						)
						.put(
							4,
							new TradeOffers.Factory[]{
								new TradeOffers.BuyForOneEmeraldFactory(Items.field_8674, 2, 6, 30),
								new TradeOffers.EnchantBookFactory(15),
								new TradeOffers.SellItemFactory(Items.field_8557, 5, 1, 15),
								new TradeOffers.SellItemFactory(Items.field_8251, 4, 1, 15)
							}
						)
						.put(5, new TradeOffers.Factory[]{new TradeOffers.SellItemFactory(Items.field_8448, 20, 1, 30)})
						.build()
				)
			);
			hashMap.put(
				VillagerProfession.field_17054,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8407, 24, 8, 2), new TradeOffers.SellItemFactory(Items.field_8895, 7, 1, 1)
						},
						2,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.GLASS_PANE, 11, 8, 10), new TradeOffers.SellMapFactory(13, "Monument", MapIcon.Type.field_98, 6, 5)
						},
						3,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8251, 1, 6, 20), new TradeOffers.SellMapFactory(14, "Mansion", MapIcon.Type.field_88, 6, 10)
						},
						4,
						new TradeOffers.Factory[]{
							new TradeOffers.SellItemFactory(Items.field_8143, 7, 1, 15),
							new TradeOffers.SellItemFactory(Items.field_8539, 3, 1, 15),
							new TradeOffers.SellItemFactory(Items.field_8128, 3, 1, 15),
							new TradeOffers.SellItemFactory(Items.field_8379, 3, 1, 15),
							new TradeOffers.SellItemFactory(Items.field_8586, 3, 1, 15),
							new TradeOffers.SellItemFactory(Items.field_8329, 3, 1, 15),
							new TradeOffers.SellItemFactory(Items.field_8295, 3, 1, 15),
							new TradeOffers.SellItemFactory(Items.field_8778, 3, 1, 15),
							new TradeOffers.SellItemFactory(Items.field_8617, 3, 1, 15),
							new TradeOffers.SellItemFactory(Items.field_8572, 3, 1, 15),
							new TradeOffers.SellItemFactory(Items.field_8405, 3, 1, 15),
							new TradeOffers.SellItemFactory(Items.field_8671, 3, 1, 15),
							new TradeOffers.SellItemFactory(Items.field_8629, 3, 1, 15),
							new TradeOffers.SellItemFactory(Items.field_8124, 3, 1, 15),
							new TradeOffers.SellItemFactory(Items.field_8049, 3, 1, 15),
							new TradeOffers.SellItemFactory(Items.field_8824, 3, 1, 15),
							new TradeOffers.SellItemFactory(Items.field_8855, 3, 1, 15)
						},
						5,
						new TradeOffers.Factory[]{new TradeOffers.SellItemFactory(Items.field_18674, 8, 1, 30)}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17055,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8511, 32, 8, 2), new TradeOffers.SellItemFactory(Items.field_8725, 1, 2, 1)
						},
						2,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8695, 3, 6, 10), new TradeOffers.SellItemFactory(Items.field_8759, 1, 1, 5)
						},
						3,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8073, 2, 6, 20), new TradeOffers.SellItemFactory(Blocks.field_10171, 4, 1, 6, 10)
						},
						4,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8161, 4, 6, 30),
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8469, 9, 6, 30),
							new TradeOffers.SellItemFactory(Items.field_8634, 5, 1, 15)
						},
						5,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8790, 22, 6, 30), new TradeOffers.SellItemFactory(Items.field_8287, 3, 1, 30)
						}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17052,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8713, 15, 8, 2),
							new TradeOffers.SellItemFactory(new ItemStack(Items.field_8396), 7, 1, 6, 1, 0.2F),
							new TradeOffers.SellItemFactory(new ItemStack(Items.field_8660), 4, 1, 6, 1, 0.2F),
							new TradeOffers.SellItemFactory(new ItemStack(Items.field_8743), 5, 1, 6, 1, 0.2F),
							new TradeOffers.SellItemFactory(new ItemStack(Items.field_8523), 9, 1, 6, 1, 0.2F)
						},
						2,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8620, 4, 6, 10),
							new TradeOffers.SellItemFactory(new ItemStack(Items.BELL), 36, 1, 6, 5, 0.2F),
							new TradeOffers.SellItemFactory(new ItemStack(Items.field_8313), 1, 1, 6, 5, 0.2F),
							new TradeOffers.SellItemFactory(new ItemStack(Items.field_8218), 3, 1, 6, 5, 0.2F)
						},
						3,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8187, 1, 6, 20),
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8477, 1, 6, 20),
							new TradeOffers.SellItemFactory(new ItemStack(Items.field_8283), 1, 1, 6, 10, 0.2F),
							new TradeOffers.SellItemFactory(new ItemStack(Items.field_8873), 4, 1, 6, 10, 0.2F),
							new TradeOffers.SellItemFactory(new ItemStack(Items.field_8255), 5, 1, 6, 10, 0.2F)
						},
						4,
						new TradeOffers.Factory[]{
							new TradeOffers.SellEnchantedToolFactory(Items.field_8348, 14, 2, 15, 0.2F), new TradeOffers.SellEnchantedToolFactory(Items.field_8285, 8, 2, 15, 0.2F)
						},
						5,
						new TradeOffers.Factory[]{
							new TradeOffers.SellEnchantedToolFactory(Items.field_8805, 8, 2, 30, 0.2F), new TradeOffers.SellEnchantedToolFactory(Items.field_8058, 16, 2, 30, 0.2F)
						}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17065,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8713, 15, 8, 2),
							new TradeOffers.SellItemFactory(new ItemStack(Items.field_8475), 3, 1, 6, 1, 0.2F),
							new TradeOffers.SellEnchantedToolFactory(Items.field_8371, 2, 2, 1)
						},
						2,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8620, 4, 6, 10), new TradeOffers.SellItemFactory(new ItemStack(Items.BELL), 36, 1, 6, 5, 0.2F)
						},
						3,
						new TradeOffers.Factory[]{new TradeOffers.BuyForOneEmeraldFactory(Items.field_8145, 24, 6, 20)},
						4,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8477, 1, 6, 30), new TradeOffers.SellEnchantedToolFactory(Items.field_8556, 12, 2, 15, 0.2F)
						},
						5,
						new TradeOffers.Factory[]{new TradeOffers.SellEnchantedToolFactory(Items.field_8802, 8, 2, 30, 0.2F)}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17064,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8713, 15, 8, 2),
							new TradeOffers.SellItemFactory(new ItemStack(Items.field_8062), 1, 1, 6, 1, 0.2F),
							new TradeOffers.SellItemFactory(new ItemStack(Items.field_8776), 1, 1, 6, 1, 0.2F),
							new TradeOffers.SellItemFactory(new ItemStack(Items.field_8387), 1, 1, 6, 1, 0.2F),
							new TradeOffers.SellItemFactory(new ItemStack(Items.field_8431), 1, 1, 6, 1, 0.2F)
						},
						2,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8620, 4, 6, 10), new TradeOffers.SellItemFactory(new ItemStack(Items.BELL), 36, 1, 6, 5, 0.2F)
						},
						3,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8145, 30, 6, 20),
							new TradeOffers.SellEnchantedToolFactory(Items.field_8475, 1, 2, 10, 0.2F),
							new TradeOffers.SellEnchantedToolFactory(Items.field_8699, 2, 2, 10, 0.2F),
							new TradeOffers.SellEnchantedToolFactory(Items.field_8403, 3, 2, 10, 0.2F),
							new TradeOffers.SellItemFactory(new ItemStack(Items.field_8527), 4, 1, 2, 10, 0.2F)
						},
						4,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8477, 1, 6, 30),
							new TradeOffers.SellEnchantedToolFactory(Items.field_8556, 12, 2, 15, 0.2F),
							new TradeOffers.SellEnchantedToolFactory(Items.field_8250, 5, 2, 15, 0.2F)
						},
						5,
						new TradeOffers.Factory[]{new TradeOffers.SellEnchantedToolFactory(Items.field_8377, 13, 2, 30, 0.2F)}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17053,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8726, 14, 8, 2),
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8389, 7, 8, 2),
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8504, 4, 8, 2),
							new TradeOffers.SellItemFactory(Items.field_8308, 1, 1, 1)
						},
						2,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8713, 15, 8, 2),
							new TradeOffers.SellItemFactory(Items.field_8261, 1, 5, 8, 5),
							new TradeOffers.SellItemFactory(Items.field_8544, 1, 8, 8, 5)
						},
						3,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8748, 7, 8, 20), new TradeOffers.BuyForOneEmeraldFactory(Items.field_8046, 10, 8, 20)
						},
						4,
						new TradeOffers.Factory[]{new TradeOffers.BuyForOneEmeraldFactory(Items.DRIED_KELP_BLOCK, 10, 6, 30)},
						5,
						new TradeOffers.Factory[]{new TradeOffers.BuyForOneEmeraldFactory(Items.field_16998, 10, 6, 30)}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17059,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8745, 6, 8, 2),
							new TradeOffers.SellDyedArmorFactory(Items.field_8570, 3),
							new TradeOffers.SellDyedArmorFactory(Items.field_8577, 7)
						},
						2,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8145, 26, 6, 10),
							new TradeOffers.SellDyedArmorFactory(Items.field_8267, 5, 6, 5),
							new TradeOffers.SellDyedArmorFactory(Items.field_8370, 4, 6, 5)
						},
						3,
						new TradeOffers.Factory[]{new TradeOffers.BuyForOneEmeraldFactory(Items.field_8245, 9, 6, 20), new TradeOffers.SellDyedArmorFactory(Items.field_8577, 7)},
						4,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8161, 4, 6, 30), new TradeOffers.SellDyedArmorFactory(Items.field_18138, 6, 6, 15)
						},
						5,
						new TradeOffers.Factory[]{
							new TradeOffers.SellItemFactory(new ItemStack(Items.field_8175), 6, 1, 6, 30, 0.2F), new TradeOffers.SellDyedArmorFactory(Items.field_8267, 5, 6, 30)
						}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17061,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8696, 10, 8, 2), new TradeOffers.SellItemFactory(Items.field_8621, 1, 10, 8, 1)
						},
						2,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.field_10340, 20, 8, 10), new TradeOffers.SellItemFactory(Blocks.field_10552, 1, 4, 8, 5)
						},
						3,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.field_10474, 16, 8, 20),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.field_10115, 16, 8, 20),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.field_10508, 16, 8, 20),
							new TradeOffers.SellItemFactory(Blocks.field_10093, 1, 4, 8, 10),
							new TradeOffers.SellItemFactory(Blocks.field_10346, 1, 4, 8, 10),
							new TradeOffers.SellItemFactory(Blocks.field_10289, 1, 4, 8, 10)
						},
						4,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Items.field_8155, 12, 6, 30),
							new TradeOffers.SellItemFactory(Blocks.field_10184, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10611, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10409, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10325, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10349, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10590, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10626, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10328, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10444, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10015, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10014, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10526, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10235, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10570, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10143, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10123, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10280, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10595, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10550, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10345, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10220, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10052, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10501, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10383, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10567, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10538, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10046, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10475, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10078, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10426, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10096, 1, 1, 6, 15),
							new TradeOffers.SellItemFactory(Blocks.field_10004, 1, 1, 6, 15)
						},
						5,
						new TradeOffers.Factory[]{
							new TradeOffers.SellItemFactory(Blocks.field_10437, 1, 1, 6, 30), new TradeOffers.SellItemFactory(Blocks.field_10153, 1, 1, 6, 30)
						}
					)
				)
			);
		}
	);
	public static final Int2ObjectMap<TradeOffers.Factory[]> WANDERING_TRADER_TRADES = copyToFastUtilMap(
		ImmutableMap.of(
			1,
			new TradeOffers.Factory[]{
				new TradeOffers.SellItemFactory(Items.SEA_PICKLE, 2, 1, 5, 1),
				new TradeOffers.SellItemFactory(Items.field_8777, 4, 1, 5, 1),
				new TradeOffers.SellItemFactory(Items.GLOWSTONE, 2, 1, 5, 1),
				new TradeOffers.SellItemFactory(Items.field_8864, 5, 1, 5, 1),
				new TradeOffers.SellItemFactory(Items.FERN, 1, 1, 12, 1),
				new TradeOffers.SellItemFactory(Items.SUGAR_CANE, 1, 1, 8, 1),
				new TradeOffers.SellItemFactory(Items.PUMPKIN, 1, 1, 4, 1),
				new TradeOffers.SellItemFactory(Items.KELP, 3, 1, 12, 1),
				new TradeOffers.SellItemFactory(Items.CACTUS, 3, 1, 8, 1),
				new TradeOffers.SellItemFactory(Items.DANDELION, 1, 1, 12, 1),
				new TradeOffers.SellItemFactory(Items.POPPY, 1, 1, 12, 1),
				new TradeOffers.SellItemFactory(Items.BLUE_ORCHID, 1, 1, 8, 1),
				new TradeOffers.SellItemFactory(Items.ALLIUM, 1, 1, 12, 1),
				new TradeOffers.SellItemFactory(Items.AZURE_BLUET, 1, 1, 12, 1),
				new TradeOffers.SellItemFactory(Items.RED_TULIP, 1, 1, 12, 1),
				new TradeOffers.SellItemFactory(Items.ORANGE_TULIP, 1, 1, 12, 1),
				new TradeOffers.SellItemFactory(Items.WHITE_TULIP, 1, 1, 12, 1),
				new TradeOffers.SellItemFactory(Items.PINK_TULIP, 1, 1, 12, 1),
				new TradeOffers.SellItemFactory(Items.OXEYE_DAISY, 1, 1, 12, 1),
				new TradeOffers.SellItemFactory(Items.CORNFLOWER, 1, 1, 12, 1),
				new TradeOffers.SellItemFactory(Items.LILY_OF_THE_VALLEY, 1, 1, 7, 1),
				new TradeOffers.SellItemFactory(Items.field_8317, 1, 1, 12, 1),
				new TradeOffers.SellItemFactory(Items.field_8309, 1, 1, 12, 1),
				new TradeOffers.SellItemFactory(Items.field_8706, 1, 1, 12, 1),
				new TradeOffers.SellItemFactory(Items.field_8188, 1, 1, 12, 1),
				new TradeOffers.SellItemFactory(Items.ACACIA_SAPLING, 5, 1, 8, 1),
				new TradeOffers.SellItemFactory(Items.BIRCH_SAPLING, 5, 1, 8, 1),
				new TradeOffers.SellItemFactory(Items.DARK_OAK_SAPLING, 5, 1, 8, 1),
				new TradeOffers.SellItemFactory(Items.JUNGLE_SAPLING, 5, 1, 8, 1),
				new TradeOffers.SellItemFactory(Items.OAK_SAPLING, 5, 1, 8, 1),
				new TradeOffers.SellItemFactory(Items.SPRUCE_SAPLING, 5, 1, 8, 1),
				new TradeOffers.SellItemFactory(Items.field_8264, 1, 3, 12, 1),
				new TradeOffers.SellItemFactory(Items.field_8446, 1, 3, 12, 1),
				new TradeOffers.SellItemFactory(Items.field_8345, 1, 3, 12, 1),
				new TradeOffers.SellItemFactory(Items.field_8330, 1, 3, 12, 1),
				new TradeOffers.SellItemFactory(Items.field_8226, 1, 3, 12, 1),
				new TradeOffers.SellItemFactory(Items.field_8408, 1, 3, 12, 1),
				new TradeOffers.SellItemFactory(Items.field_8851, 1, 3, 12, 1),
				new TradeOffers.SellItemFactory(Items.field_8669, 1, 3, 12, 1),
				new TradeOffers.SellItemFactory(Items.field_8192, 1, 3, 12, 1),
				new TradeOffers.SellItemFactory(Items.field_8298, 1, 3, 12, 1),
				new TradeOffers.SellItemFactory(Items.field_8296, 1, 3, 12, 1),
				new TradeOffers.SellItemFactory(Items.field_8273, 1, 3, 12, 1),
				new TradeOffers.SellItemFactory(Items.field_8131, 1, 3, 12, 1),
				new TradeOffers.SellItemFactory(Items.field_8492, 1, 3, 12, 1),
				new TradeOffers.SellItemFactory(Items.field_8099, 1, 3, 12, 1),
				new TradeOffers.SellItemFactory(Items.field_8632, 1, 3, 12, 1),
				new TradeOffers.SellItemFactory(Items.BRAIN_CORAL_BLOCK, 3, 1, 8, 1),
				new TradeOffers.SellItemFactory(Items.BUBBLE_CORAL_BLOCK, 3, 1, 8, 1),
				new TradeOffers.SellItemFactory(Items.FIRE_CORAL_BLOCK, 3, 1, 8, 1),
				new TradeOffers.SellItemFactory(Items.HORN_CORAL_BLOCK, 3, 1, 8, 1),
				new TradeOffers.SellItemFactory(Items.TUBE_CORAL_BLOCK, 3, 1, 8, 1),
				new TradeOffers.SellItemFactory(Items.VINE, 1, 1, 12, 1),
				new TradeOffers.SellItemFactory(Items.BROWN_MUSHROOM, 1, 1, 12, 1),
				new TradeOffers.SellItemFactory(Items.RED_MUSHROOM, 1, 1, 12, 1),
				new TradeOffers.SellItemFactory(Items.LILY_PAD, 1, 2, 5, 1),
				new TradeOffers.SellItemFactory(Items.SAND, 1, 8, 8, 1),
				new TradeOffers.SellItemFactory(Items.RED_SAND, 1, 4, 6, 1)
			},
			2,
			new TradeOffers.Factory[]{
				new TradeOffers.SellItemFactory(Items.field_8478, 5, 1, 4, 1),
				new TradeOffers.SellItemFactory(Items.field_8108, 5, 1, 4, 1),
				new TradeOffers.SellItemFactory(Items.PACKED_ICE, 3, 1, 6, 1),
				new TradeOffers.SellItemFactory(Items.BLUE_ICE, 6, 1, 6, 1),
				new TradeOffers.SellItemFactory(Items.field_8054, 1, 1, 8, 1),
				new TradeOffers.SellItemFactory(Items.PODZOL, 3, 3, 6, 1)
			}
		)
	);

	private static Int2ObjectMap<TradeOffers.Factory[]> copyToFastUtilMap(ImmutableMap<Integer, TradeOffers.Factory[]> immutableMap) {
		return new Int2ObjectOpenHashMap<>(immutableMap);
	}

	static class BuyForOneEmeraldFactory implements TradeOffers.Factory {
		private final Item buy;
		private final int price;
		private final int maxUses;
		private final int experience;
		private final float multiplier;

		public BuyForOneEmeraldFactory(ItemConvertible itemConvertible, int i, int j, int k) {
			this.buy = itemConvertible.asItem();
			this.price = i;
			this.maxUses = j;
			this.experience = k;
			this.multiplier = 0.05F;
		}

		@Override
		public TradeOffer method_7246(Entity entity, Random random) {
			ItemStack itemStack = new ItemStack(this.buy, this.price);
			return new TradeOffer(itemStack, new ItemStack(Items.field_8687), this.maxUses, this.experience, this.multiplier);
		}
	}

	static class EnchantBookFactory implements TradeOffers.Factory {
		private final int experience;

		public EnchantBookFactory(int i) {
			this.experience = i;
		}

		@Override
		public TradeOffer method_7246(Entity entity, Random random) {
			Enchantment enchantment = Registry.ENCHANTMENT.getRandom(random);
			int i = MathHelper.nextInt(random, enchantment.getMinimumLevel(), enchantment.getMaximumLevel());
			ItemStack itemStack = EnchantedBookItem.method_7808(new InfoEnchantment(enchantment, i));
			int j = 2 + random.nextInt(5 + i * 10) + 3 * i;
			if (enchantment.isTreasure()) {
				j *= 2;
			}

			if (j > 64) {
				j = 64;
			}

			return new TradeOffer(new ItemStack(Items.field_8687, j), new ItemStack(Items.field_8529), itemStack, 6, this.experience, 0.2F);
		}
	}

	public interface Factory {
		@Nullable
		TradeOffer method_7246(Entity entity, Random random);
	}

	static class ProcessItemFactory implements TradeOffers.Factory {
		private final ItemStack secondBuy;
		private final int secondCount;
		private final int price;
		private final ItemStack sell;
		private final int sellCount;
		private final int maxUses;
		private final int experience;
		private final float multiplier;

		public ProcessItemFactory(ItemConvertible itemConvertible, int i, Item item, int j, int k, int l) {
			this(itemConvertible, i, 1, item, j, k, l);
		}

		public ProcessItemFactory(ItemConvertible itemConvertible, int i, int j, Item item, int k, int l, int m) {
			this.secondBuy = new ItemStack(itemConvertible);
			this.secondCount = i;
			this.price = j;
			this.sell = new ItemStack(item);
			this.sellCount = k;
			this.maxUses = l;
			this.experience = m;
			this.multiplier = 0.05F;
		}

		@Nullable
		@Override
		public TradeOffer method_7246(Entity entity, Random random) {
			return new TradeOffer(
				new ItemStack(Items.field_8687, this.price),
				new ItemStack(this.secondBuy.getItem(), this.secondCount),
				new ItemStack(this.sell.getItem(), this.sellCount),
				this.maxUses,
				this.experience,
				this.multiplier
			);
		}
	}

	static class SellDyedArmorFactory implements TradeOffers.Factory {
		private final Item sell;
		private final int price;
		private final int maxUses;
		private final int experience;

		public SellDyedArmorFactory(Item item, int i) {
			this(item, i, 6, 1);
		}

		public SellDyedArmorFactory(Item item, int i, int j, int k) {
			this.sell = item;
			this.price = i;
			this.maxUses = j;
			this.experience = k;
		}

		@Override
		public TradeOffer method_7246(Entity entity, Random random) {
			ItemStack itemStack = new ItemStack(Items.field_8687, this.price);
			ItemStack itemStack2 = new ItemStack(this.sell);
			if (this.sell instanceof DyeableArmorItem) {
				List<DyeItem> list = Lists.<DyeItem>newArrayList();
				list.add(getDye(random));
				if (random.nextFloat() > 0.7F) {
					list.add(getDye(random));
				}

				if (random.nextFloat() > 0.8F) {
					list.add(getDye(random));
				}

				itemStack2 = DyeableItem.blendAndSetColor(itemStack2, list);
			}

			return new TradeOffer(itemStack, itemStack2, this.maxUses, this.experience, 0.2F);
		}

		private static DyeItem getDye(Random random) {
			return DyeItem.byColor(DyeColor.byId(random.nextInt(16)));
		}
	}

	static class SellEnchantedToolFactory implements TradeOffers.Factory {
		private final ItemStack tool;
		private final int basePrice;
		private final int maxUses;
		private final int experience;
		private final float multiplier;

		public SellEnchantedToolFactory(Item item, int i, int j, int k) {
			this(item, i, j, k, 0.05F);
		}

		public SellEnchantedToolFactory(Item item, int i, int j, int k, float f) {
			this.tool = new ItemStack(item);
			this.basePrice = i;
			this.maxUses = j;
			this.experience = k;
			this.multiplier = f;
		}

		@Override
		public TradeOffer method_7246(Entity entity, Random random) {
			int i = 5 + random.nextInt(15);
			ItemStack itemStack = EnchantmentHelper.enchant(random, new ItemStack(this.tool.getItem()), i, false);
			int j = Math.min(this.basePrice + i, 64);
			ItemStack itemStack2 = new ItemStack(Items.field_8687, j);
			return new TradeOffer(itemStack2, itemStack, this.maxUses, this.experience, this.multiplier);
		}
	}

	static class SellItemFactory implements TradeOffers.Factory {
		private final ItemStack sell;
		private final int price;
		private final int count;
		private final int maxUses;
		private final int experience;
		private final float multiplier;

		public SellItemFactory(Block block, int i, int j, int k, int l) {
			this(new ItemStack(block), i, j, k, l);
		}

		public SellItemFactory(Item item, int i, int j, int k) {
			this(new ItemStack(item), i, j, 6, k);
		}

		public SellItemFactory(Item item, int i, int j, int k, int l) {
			this(new ItemStack(item), i, j, k, l);
		}

		public SellItemFactory(ItemStack itemStack, int i, int j, int k, int l) {
			this(itemStack, i, j, k, l, 0.05F);
		}

		public SellItemFactory(ItemStack itemStack, int i, int j, int k, int l, float f) {
			this.sell = itemStack;
			this.price = i;
			this.count = j;
			this.maxUses = k;
			this.experience = l;
			this.multiplier = f;
		}

		@Override
		public TradeOffer method_7246(Entity entity, Random random) {
			return new TradeOffer(
				new ItemStack(Items.field_8687, this.price), new ItemStack(this.sell.getItem(), this.count), this.maxUses, this.experience, this.multiplier
			);
		}
	}

	static class SellMapFactory implements TradeOffers.Factory {
		private final int price;
		private final String structure;
		private final MapIcon.Type field_7473;
		private final int maxUses;
		private final int experience;

		public SellMapFactory(int i, String string, MapIcon.Type type, int j, int k) {
			this.price = i;
			this.structure = string;
			this.field_7473 = type;
			this.maxUses = j;
			this.experience = k;
		}

		@Nullable
		@Override
		public TradeOffer method_7246(Entity entity, Random random) {
			World world = entity.field_6002;
			BlockPos blockPos = world.locateStructure(this.structure, new BlockPos(entity), 100, true);
			if (blockPos != null) {
				ItemStack itemStack = FilledMapItem.method_8005(world, blockPos.getX(), blockPos.getZ(), (byte)2, true, true);
				FilledMapItem.method_8002(world, itemStack);
				MapState.addDecorationsTag(itemStack, blockPos, "+", this.field_7473);
				itemStack.setCustomName(new TranslatableText("filled_map." + this.structure.toLowerCase(Locale.ROOT)));
				return new TradeOffer(new ItemStack(Items.field_8687, this.price), new ItemStack(Items.field_8251), itemStack, this.maxUses, this.experience, 0.2F);
			} else {
				return null;
			}
		}
	}

	static class SellPotionHoldingItemFactory implements TradeOffers.Factory {
		private final ItemStack sell;
		private final int sellCount;
		private final int price;
		private final int maxUses;
		private final int experience;
		private final Item secondBuy;
		private final int secondCount;
		private final float priceMultiplier;

		public SellPotionHoldingItemFactory(Item item, int i, Item item2, int j, int k, int l, int m) {
			this.sell = new ItemStack(item2);
			this.price = k;
			this.maxUses = l;
			this.experience = m;
			this.secondBuy = item;
			this.secondCount = i;
			this.sellCount = j;
			this.priceMultiplier = 0.05F;
		}

		@Override
		public TradeOffer method_7246(Entity entity, Random random) {
			ItemStack itemStack = new ItemStack(Items.field_8687, this.price);
			List<Potion> list = (List<Potion>)Registry.POTION
				.stream()
				.filter(potionx -> !potionx.getEffects().isEmpty() && BrewingRecipeRegistry.isBrewable(potionx))
				.collect(Collectors.toList());
			Potion potion = (Potion)list.get(random.nextInt(list.size()));
			ItemStack itemStack2 = PotionUtil.setPotion(new ItemStack(this.sell.getItem(), this.sellCount), potion);
			return new TradeOffer(itemStack, new ItemStack(this.secondBuy, this.secondCount), itemStack2, this.maxUses, this.experience, this.priceMultiplier);
		}
	}

	static class SellSuspiciousStewFactory implements TradeOffers.Factory {
		final StatusEffect effect;
		final int duration;
		final int experience;
		private final float multiplier;

		public SellSuspiciousStewFactory(StatusEffect statusEffect, int i, int j) {
			this.effect = statusEffect;
			this.duration = i;
			this.experience = j;
			this.multiplier = 0.05F;
		}

		@Nullable
		@Override
		public TradeOffer method_7246(Entity entity, Random random) {
			ItemStack itemStack = new ItemStack(Items.field_8766, 1);
			SuspiciousStewItem.addEffectToStew(itemStack, this.effect, this.duration);
			return new TradeOffer(new ItemStack(Items.field_8687, 1), itemStack, 6, this.experience, this.multiplier);
		}
	}

	static class TypeAwareBuyForOneEmeraldFactory implements TradeOffers.Factory {
		private final Map<VillagerType, Item> map;
		private final int count;
		private final int maxUses;
		private final int experience;

		public TypeAwareBuyForOneEmeraldFactory(int i, int j, int k, Map<VillagerType, Item> map) {
			Registry.VILLAGER_TYPE.stream().filter(villagerType -> !map.containsKey(villagerType)).findAny().ifPresent(villagerType -> {
				throw new IllegalStateException("Missing trade for villager type: " + Registry.VILLAGER_TYPE.getId(villagerType));
			});
			this.map = map;
			this.count = i;
			this.maxUses = j;
			this.experience = k;
		}

		@Nullable
		@Override
		public TradeOffer method_7246(Entity entity, Random random) {
			if (entity instanceof VillagerDataContainer) {
				ItemStack itemStack = new ItemStack((ItemConvertible)this.map.get(((VillagerDataContainer)entity).getVillagerData().getType()), this.count);
				return new TradeOffer(itemStack, new ItemStack(Items.field_8687), this.maxUses, this.experience, 0.05F);
			} else {
				return null;
			}
		}
	}
}
