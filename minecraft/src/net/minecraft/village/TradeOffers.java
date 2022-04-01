package net.minecraft.village;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_7317;
import net.minecraft.class_7320;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Util;

public class TradeOffers {
	public static final class_7317.class_7318 field_38533 = class_7317.method_42845(Blocks.EMERALD_ORE);
	private static final int DEFAULT_MAX_USES = Integer.MAX_VALUE;
	private static final int COMMON_MAX_USES = Integer.MAX_VALUE;
	private static final int NOVICE_SELL_XP = 4;
	private static final int NOVICE_BUY_XP = 8;
	private static final int APPRENTICE_SELL_XP = 20;
	private static final int APPRENTICE_BUY_XP = 40;
	private static final int JOURNEYMAN_SELL_XP = 40;
	private static final int JOURNEYMAN_BUY_XP = 80;
	private static final int EXPERT_SELL_XP = 60;
	private static final int EXPERT_BUY_XP = 120;
	private static final int MASTER_TRADE_XP = 120;
	private static final float LOW_PRICE_MULTIPLIER = 0.05F;
	private static final float HIGH_PRICE_MULTIPLIER = 0.2F;
	public static final Map<VillagerProfession, Int2ObjectMap<TradeOffers.Factory[]>> PROFESSION_TO_LEVELED_TRADE = Util.make(
		Maps.<VillagerProfession, Int2ObjectMap<TradeOffers.Factory[]>>newHashMap(),
		map -> {
			map.put(
				VillagerProfession.FARMER,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.HAY_BLOCK, Integer.MAX_VALUE, 8), new TradeOffers.SellItemFactory(Blocks.HAY_BLOCK, 1, 4)
						},
						2,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.PUMPKIN, Integer.MAX_VALUE, 40), new TradeOffers.SellItemFactory(Blocks.PUMPKIN, 1, 20)
						},
						3,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.MELON, Integer.MAX_VALUE, 80),
							new TradeOffers.SellItemFactory(Blocks.MELON, 1, 40),
							new TradeOffers.BuyForOneEmeraldFactory(class_7317.method_42842(EntityType.SKELETON), 1, 80),
							new TradeOffers.BuyForOneEmeraldFactory(class_7317.method_42842(EntityType.SKELETON_HORSE), 1, 80)
						},
						4,
						new TradeOffers.Factory[]{
							new TradeOffers.SellItemFactory(Blocks.CAKE, 1, 60),
							new TradeOffers.BuyForOneEmeraldFactory(class_7317.method_42842(EntityType.WITHER_SKELETON), 1, 120)
						},
						5,
						new TradeOffers.Factory[]{new TradeOffers.SellItemFactory(Blocks.CACTUS, 1, 120)}
					)
				)
			);
			map.put(
				VillagerProfession.FISHERMAN,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.KELP_PLANT, Integer.MAX_VALUE, 8),
							new TradeOffers.BuyForOneEmeraldFactory(class_7317.method_42842(EntityType.COD), Integer.MAX_VALUE, 8),
							new TradeOffers.BuyForOneEmeraldFactory(class_7317.method_42842(EntityType.SALMON), Integer.MAX_VALUE, 8),
							new TradeOffers.SellItemFactory(class_7317.method_42842(EntityType.COD), Integer.MAX_VALUE, 4)
						},
						2,
						new TradeOffers.Factory[]{new TradeOffers.SellItemFactory(Blocks.CAMPFIRE, 1, 20)},
						3,
						new TradeOffers.Factory[]{new TradeOffers.SellItemFactory(Blocks.PRISMARINE, 1, 40)},
						4,
						new TradeOffers.Factory[]{
							new TradeOffers.SellItemFactory(Blocks.DARK_PRISMARINE, 1, 60), new TradeOffers.SellItemFactory(class_7317.method_42842(EntityType.SQUID), 1, 8)
						},
						5,
						new TradeOffers.Factory[]{
							new TradeOffers.SellItemFactory(Blocks.BRAIN_CORAL_BLOCK, 1, 120),
							new TradeOffers.SellItemFactory(Blocks.BUBBLE_CORAL_BLOCK, 1, 120),
							new TradeOffers.SellItemFactory(Blocks.TUBE_CORAL_BLOCK, 1, 120),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.FIRE_CORAL_BLOCK, Integer.MAX_VALUE, 120),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.HORN_CORAL_BLOCK, Integer.MAX_VALUE, 120),
							new TradeOffers.SellItemFactory(class_7317.method_42842(EntityType.TROPICAL_FISH), Integer.MAX_VALUE, 120),
							new TradeOffers.SellItemFactory(class_7317.method_42842(EntityType.GLOW_SQUID), 1, 8)
						}
					)
				)
			);
			map.put(
				VillagerProfession.SHEPHERD,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.WHITE_WOOL, Integer.MAX_VALUE, 8),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.YELLOW_WOOL, Integer.MAX_VALUE, 8),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.RED_WOOL, Integer.MAX_VALUE, 8),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.BLACK_WOOL, Integer.MAX_VALUE, 8),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.BLUE_WOOL, Integer.MAX_VALUE, 8),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.GREEN_WOOL, Integer.MAX_VALUE, 8),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.LIME_WOOL, Integer.MAX_VALUE, 8),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.PURPLE_WOOL, Integer.MAX_VALUE, 8),
							new TradeOffers.SellItemFactory(Blocks.WHITE_WOOL, 1, 4),
							new TradeOffers.SellItemFactory(Blocks.YELLOW_WOOL, 1, 4),
							new TradeOffers.SellItemFactory(Blocks.RED_WOOL, 1, 4),
							new TradeOffers.SellItemFactory(Blocks.BLACK_WOOL, 1, 4),
							new TradeOffers.SellItemFactory(Blocks.BLUE_WOOL, 1, 4),
							new TradeOffers.SellItemFactory(Blocks.GREEN_WOOL, 1, 4),
							new TradeOffers.SellItemFactory(Blocks.LIME_WOOL, 1, 4),
							new TradeOffers.SellItemFactory(Blocks.PURPLE_WOOL, 1, 4)
						},
						2,
						new TradeOffers.Factory[]{
							new TradeOffers.SellItemFactory(Blocks.WHITE_BED, 1, 20),
							new TradeOffers.SellItemFactory(Blocks.YELLOW_BED, 1, 20),
							new TradeOffers.SellItemFactory(Blocks.RED_BED, 1, 20),
							new TradeOffers.SellItemFactory(Blocks.BLACK_BED, 1, 20),
							new TradeOffers.SellItemFactory(Blocks.BLUE_BED, 1, 20),
							new TradeOffers.SellItemFactory(Blocks.GREEN_BED, 1, 20),
							new TradeOffers.SellItemFactory(Blocks.LIME_BED, 1, 20),
							new TradeOffers.SellItemFactory(Blocks.PURPLE_BED, 1, 20)
						}
					)
				)
			);
			map.put(
				VillagerProfession.FLETCHER,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.OAK_LOG, 4, 8),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.SPRUCE_LOG, 4, 8),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.ACACIA_LOG, 4, 8),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.DARK_OAK_LOG, 4, 8),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.BIRCH_LOG, 4, 8),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.JUNGLE_LOG, 4, 8),
							new TradeOffers.SellItemFactory(Items.STONE_AXE, 4, 4)
						},
						2,
						new TradeOffers.Factory[]{new TradeOffers.BuyForOneEmeraldFactory(Blocks.GRAVEL, Integer.MAX_VALUE, 40)},
						3,
						new TradeOffers.Factory[]{new TradeOffers.SellItemFactory(Blocks.GRAVEL, 1, 40)}
					)
				)
			);
			map.put(
				VillagerProfession.LIBRARIAN,
				copyToFastUtilMap(
					ImmutableMap.<Integer, TradeOffers.Factory[]>builder()
						.put(1, new TradeOffers.Factory[]{new TradeOffers.SellItemFactory(Blocks.BOOKSHELF, 1, 4)})
						.put(
							2,
							new TradeOffers.Factory[]{
								new TradeOffers.BuyForOneEmeraldFactory(Blocks.BOOKSHELF, Integer.MAX_VALUE, 40), new TradeOffers.SellItemFactory(Blocks.LANTERN, 1, 20)
							}
						)
						.put(3, new TradeOffers.Factory[]{new TradeOffers.SellItemFactory(Blocks.GLASS, 1, 40)})
						.build()
				)
			);
			map.put(
				VillagerProfession.CARTOGRAPHER,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.GLASS_PANE, Integer.MAX_VALUE, 8),
							new TradeOffers.SellItemFactory(class_7317.method_42842(EntityType.PARROT), 1, 4)
						},
						2,
						new TradeOffers.Factory[]{
							new TradeOffers.SellItemFactory(Blocks.WHITE_BANNER, 1, 20),
							new TradeOffers.SellItemFactory(Blocks.BLUE_BANNER, 1, 20),
							new TradeOffers.SellItemFactory(Blocks.RED_BANNER, 1, 20),
							new TradeOffers.SellItemFactory(Blocks.GREEN_BANNER, 1, 20),
							new TradeOffers.SellItemFactory(Blocks.LIME_BANNER, 1, 20),
							new TradeOffers.SellItemFactory(Blocks.BLACK_BANNER, 1, 20),
							new TradeOffers.SellItemFactory(Blocks.PURPLE_BANNER, 1, 20),
							new TradeOffers.SellItemFactory(Blocks.MAGENTA_BANNER, 1, 20),
							new TradeOffers.SellItemFactory(Blocks.YELLOW_BANNER, 1, 20),
							new TradeOffers.SellItemFactory(Blocks.ORANGE_BANNER, 1, 20)
						},
						3,
						new TradeOffers.Factory[]{new TradeOffers.SellItemFactory(Items.SPYGLASS, Integer.MAX_VALUE, 40)}
					)
				)
			);
			map.put(
				VillagerProfession.CLERIC,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new TradeOffers.Factory[]{
							new TradeOffers.SellItemFactory(Blocks.GLOWSTONE, 1, 8),
							new TradeOffers.SellItemFactory(Blocks.REDSTONE_BLOCK, 1, 8),
							new TradeOffers.SellItemFactory(class_7317.method_42842(EntityType.SILVERFISH), 1, 8)
						},
						2,
						new TradeOffers.Factory[]{
							new TradeOffers.SellItemFactory(Blocks.GOLD_BLOCK, 1, 40),
							new TradeOffers.SellItemFactory(Blocks.LAPIS_BLOCK, 1, 40),
							new TradeOffers.SellItemFactory(Blocks.EMERALD_BLOCK, 1, 40)
						},
						3,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(class_7317.method_42842(EntityType.ZOMBIE), 1, 80),
							new TradeOffers.BuyForOneEmeraldFactory(class_7317.method_42842(EntityType.ZOMBIE_VILLAGER), 1, 80)
						},
						4,
						new TradeOffers.Factory[]{new TradeOffers.SellItemFactory(class_7317.method_42842(EntityType.SNOW_GOLEM), 4, 60)},
						5,
						new TradeOffers.Factory[]{new TradeOffers.SellItemFactory(class_7317.method_42842(EntityType.IRON_GOLEM), 1, 120)}
					)
				)
			);
			map.put(
				VillagerProfession.ARMORER,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.COAL_ORE, Integer.MAX_VALUE, 8),
							new TradeOffers.SellItemFactory(Blocks.CARVED_PUMPKIN, Integer.MAX_VALUE, 4)
						},
						2,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.IRON_ORE, Integer.MAX_VALUE, 40),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.GOLD_ORE, Integer.MAX_VALUE, 40),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.DIAMOND_ORE, Integer.MAX_VALUE, 40)
						},
						3,
						new TradeOffers.Factory[]{
							new TradeOffers.SellItemFactory(Blocks.IRON_BLOCK, 1, 40),
							new TradeOffers.SellItemFactory(Blocks.GOLD_BLOCK, 1, 40),
							new TradeOffers.SellItemFactory(class_7317.method_42845(Blocks.BELL), 1, 40, 0.2F)
						},
						4,
						new TradeOffers.Factory[]{new TradeOffers.SellItemFactory(Blocks.DIAMOND_BLOCK, 1, 60)}
					)
				)
			);
			map.put(
				VillagerProfession.WEAPONSMITH,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.COAL_ORE, Integer.MAX_VALUE, 8),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.GRAVEL, Integer.MAX_VALUE, 8),
							new TradeOffers.SellItemFactory(Blocks.CACTUS, Integer.MAX_VALUE, 4)
						},
						2,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.IRON_ORE, Integer.MAX_VALUE, 40),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.GOLD_ORE, Integer.MAX_VALUE, 40),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.DIAMOND_ORE, Integer.MAX_VALUE, 40)
						},
						3,
						new TradeOffers.Factory[]{new TradeOffers.SellItemFactory(Blocks.IRON_BLOCK, 1, 40), new TradeOffers.SellItemFactory(Blocks.GOLD_BLOCK, 1, 40)},
						4,
						new TradeOffers.Factory[]{new TradeOffers.SellItemFactory(Blocks.DIAMOND_BLOCK, 1, 60)}
					)
				)
			);
			map.put(
				VillagerProfession.TOOLSMITH,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.COAL_ORE, Integer.MAX_VALUE, 8),
							new TradeOffers.SellItemFactory(Items.STONE_PICKAXE, Integer.MAX_VALUE, 8)
						},
						2,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.IRON_ORE, Integer.MAX_VALUE, 40),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.GOLD_ORE, Integer.MAX_VALUE, 40),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.DIAMOND_ORE, Integer.MAX_VALUE, 40)
						},
						3,
						new TradeOffers.Factory[]{
							new TradeOffers.SellItemFactory(Items.IRON_PICKAXE, Integer.MAX_VALUE, 80),
							new TradeOffers.SellItemFactory(Blocks.IRON_BLOCK, 1, 40),
							new TradeOffers.SellItemFactory(Blocks.GOLD_BLOCK, 1, 40)
						},
						4,
						new TradeOffers.Factory[]{new TradeOffers.SellItemFactory(Blocks.DIAMOND_BLOCK, 1, 60)},
						5,
						new TradeOffers.Factory[]{
							new TradeOffers.SellItemFactory(Blocks.DIAMOND_BLOCK, 1, 120),
							new TradeOffers.BuyForOneEmeraldFactory(class_7317.method_42842(EntityType.CREEPER), 1, 120)
						}
					)
				)
			);
			map.put(
				VillagerProfession.BUTCHER,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new TradeOffers.Factory[]{
							new TradeOffers.SellItemFactory(Blocks.CAMPFIRE, 1, 4), new TradeOffers.BuyForOneEmeraldFactory(class_7317.method_42842(EntityType.CHICKEN), 3, 8)
						},
						2,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.DRIED_KELP_BLOCK, Integer.MAX_VALUE, 40),
							new TradeOffers.BuyForOneEmeraldFactory(class_7317.method_42842(EntityType.PIG), 3, 8),
							new TradeOffers.BuyForOneEmeraldFactory(class_7317.method_42842(EntityType.COW), 3, 8),
							new TradeOffers.BuyForOneEmeraldFactory(class_7317.method_42842(EntityType.GOAT), 3, 8)
						}
					)
				)
			);
			map.put(
				VillagerProfession.LEATHERWORKER,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(class_7317.method_42842(EntityType.COW), 3, 8),
							new TradeOffers.SellItemFactory(class_7317.method_42842(EntityType.CAT), 3, 4)
						}
					)
				)
			);
			map.put(
				VillagerProfession.MASON,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.CLAY, Integer.MAX_VALUE, 8), new TradeOffers.SellItemFactory(Blocks.BRICKS, Integer.MAX_VALUE, 4)
						},
						2,
						new TradeOffers.Factory[]{new TradeOffers.BuyForOneEmeraldFactory(Blocks.STONE, Integer.MAX_VALUE, 40)},
						3,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.GRANITE, Integer.MAX_VALUE, 80),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.ANDESITE, Integer.MAX_VALUE, 80),
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.DIORITE, Integer.MAX_VALUE, 80),
							new TradeOffers.SellItemFactory(Blocks.DRIPSTONE_BLOCK, 1, 40)
						},
						4,
						new TradeOffers.Factory[]{
							new TradeOffers.BuyForOneEmeraldFactory(Blocks.QUARTZ_BLOCK, Integer.MAX_VALUE, 120),
							new TradeOffers.SellItemFactory(Blocks.ORANGE_TERRACOTTA, Integer.MAX_VALUE, 60),
							new TradeOffers.SellItemFactory(Blocks.WHITE_TERRACOTTA, Integer.MAX_VALUE, 60),
							new TradeOffers.SellItemFactory(Blocks.RED_TERRACOTTA, Integer.MAX_VALUE, 60),
							new TradeOffers.SellItemFactory(Blocks.PINK_TERRACOTTA, Integer.MAX_VALUE, 60),
							new TradeOffers.SellItemFactory(Blocks.GREEN_TERRACOTTA, Integer.MAX_VALUE, 60),
							new TradeOffers.SellItemFactory(Blocks.BLACK_GLAZED_TERRACOTTA, Integer.MAX_VALUE, 60),
							new TradeOffers.SellItemFactory(Blocks.MAGENTA_GLAZED_TERRACOTTA, Integer.MAX_VALUE, 60),
							new TradeOffers.SellItemFactory(Blocks.LIME_GLAZED_TERRACOTTA, Integer.MAX_VALUE, 60),
							new TradeOffers.SellItemFactory(Blocks.GREEN_GLAZED_TERRACOTTA, Integer.MAX_VALUE, 60),
							new TradeOffers.SellItemFactory(Blocks.YELLOW_GLAZED_TERRACOTTA, Integer.MAX_VALUE, 60)
						},
						5,
						new TradeOffers.Factory[]{
							new TradeOffers.SellItemFactory(Blocks.QUARTZ_PILLAR, 1, 120),
							new TradeOffers.SellItemFactory(Blocks.QUARTZ_BLOCK, 1, 120),
							new TradeOffers.SellItemFactory(Blocks.TNT, 3, 120),
							new TradeOffers.SellItemFactory(Items.DIAMOND_PICKAXE, Integer.MAX_VALUE, 120)
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
				new TradeOffers.SellItemFactory(Blocks.SEA_PICKLE, 5, 1),
				new TradeOffers.SellItemFactory(Blocks.GLOWSTONE, 5, 1),
				new TradeOffers.SellItemFactory(Blocks.FERN, 12, 1),
				new TradeOffers.SellItemFactory(Blocks.SUGAR_CANE, 8, 1),
				new TradeOffers.SellItemFactory(Blocks.PUMPKIN, 4, 1),
				new TradeOffers.SellItemFactory(Blocks.KELP, 12, 1),
				new TradeOffers.SellItemFactory(Blocks.CACTUS, 8, 1),
				new TradeOffers.SellItemFactory(Blocks.DANDELION, 12, 1),
				new TradeOffers.SellItemFactory(Blocks.POPPY, 12, 1),
				new TradeOffers.SellItemFactory(Blocks.BLUE_ORCHID, 8, 1),
				new TradeOffers.SellItemFactory(Blocks.ALLIUM, 12, 1),
				new TradeOffers.SellItemFactory(Blocks.AZURE_BLUET, 12, 1),
				new TradeOffers.SellItemFactory(Blocks.RED_TULIP, 12, 1),
				new TradeOffers.SellItemFactory(Blocks.ORANGE_TULIP, 12, 1),
				new TradeOffers.SellItemFactory(Blocks.WHITE_TULIP, 12, 1),
				new TradeOffers.SellItemFactory(Blocks.PINK_TULIP, 12, 1),
				new TradeOffers.SellItemFactory(Blocks.OXEYE_DAISY, 12, 1),
				new TradeOffers.SellItemFactory(Blocks.CORNFLOWER, 12, 1),
				new TradeOffers.SellItemFactory(Blocks.LILY_OF_THE_VALLEY, 7, 1),
				new TradeOffers.SellItemFactory(Blocks.ACACIA_SAPLING, 8, 1),
				new TradeOffers.SellItemFactory(Blocks.BIRCH_SAPLING, 8, 1),
				new TradeOffers.SellItemFactory(Blocks.DARK_OAK_SAPLING, 8, 1),
				new TradeOffers.SellItemFactory(Blocks.JUNGLE_SAPLING, 8, 1),
				new TradeOffers.SellItemFactory(Blocks.OAK_SAPLING, 8, 1),
				new TradeOffers.SellItemFactory(Blocks.SPRUCE_SAPLING, 8, 1),
				new TradeOffers.SellItemFactory(Blocks.BRAIN_CORAL_BLOCK, 8, 1),
				new TradeOffers.SellItemFactory(Blocks.BUBBLE_CORAL_BLOCK, 8, 1),
				new TradeOffers.SellItemFactory(Blocks.FIRE_CORAL_BLOCK, 8, 1),
				new TradeOffers.SellItemFactory(Blocks.HORN_CORAL_BLOCK, 8, 1),
				new TradeOffers.SellItemFactory(Blocks.TUBE_CORAL_BLOCK, 8, 1),
				new TradeOffers.SellItemFactory(Blocks.VINE, 12, 1),
				new TradeOffers.SellItemFactory(Blocks.BROWN_MUSHROOM, 12, 1),
				new TradeOffers.SellItemFactory(Blocks.RED_MUSHROOM, 12, 1),
				new TradeOffers.SellItemFactory(Blocks.LILY_PAD, 5, 1),
				new TradeOffers.SellItemFactory(Blocks.SMALL_DRIPLEAF, 5, 1),
				new TradeOffers.SellItemFactory(Blocks.SAND, 8, 1),
				new TradeOffers.SellItemFactory(Blocks.RED_SAND, 6, 1),
				new TradeOffers.SellItemFactory(Blocks.POINTED_DRIPSTONE, 5, 1),
				new TradeOffers.SellItemFactory(Blocks.ROOTED_DIRT, 5, 1),
				new TradeOffers.SellItemFactory(Blocks.MOSS_BLOCK, 5, 1)
			},
			2,
			new TradeOffers.Factory[]{
				new TradeOffers.SellItemFactory(Blocks.PACKED_ICE, 6, 1),
				new TradeOffers.SellItemFactory(Blocks.BLUE_ICE, 6, 1),
				new TradeOffers.SellItemFactory(Blocks.PODZOL, 6, 1)
			}
		)
	);

	private static Int2ObjectMap<TradeOffers.Factory[]> copyToFastUtilMap(ImmutableMap<Integer, TradeOffers.Factory[]> map) {
		return new Int2ObjectOpenHashMap<>(map);
	}

	static class BuyForOneEmeraldFactory implements TradeOffers.Factory {
		private final class_7317 field_38534;
		private final int maxUses;
		private final int experience;
		private final float multiplier;

		public BuyForOneEmeraldFactory(Block block, int i, int j) {
			this(class_7317.method_42845(block), i, j);
		}

		public BuyForOneEmeraldFactory(class_7317 arg, int price, int maxUses) {
			this.field_38534 = arg;
			this.maxUses = price;
			this.experience = maxUses;
			this.multiplier = 0.05F;
		}

		@Override
		public TradeOffer create(Entity entity, Random random) {
			return new TradeOffer(this.field_38534, TradeOffers.field_38533, this.maxUses, this.experience, this.multiplier);
		}
	}

	/**
	 * A factory to create trade offers.
	 */
	public interface Factory {
		/**
		 * Creates a trade offer.
		 * 
		 * @return a new trade offer, or {@code null} if none should be created
		 */
		@Nullable
		TradeOffer create(Entity entity, Random random);
	}

	static class SellItemFactory implements TradeOffers.Factory {
		private final class_7317 field_38535;
		private final int maxUses;
		private final int experience;
		private final float multiplier;

		public SellItemFactory(Block block, int i, int j) {
			this(class_7317.method_42845(block), i, j);
		}

		public SellItemFactory(Item item, int i, int j) {
			this(
				class_7317.method_42845(
					(Block)class_7320.method_42858(item.getDefaultStack()).map(AbstractBlock.AbstractBlockState::getBlock).orElse(TradeOffers.field_38533.block())
				),
				i,
				j
			);
		}

		public SellItemFactory(class_7317 arg, int price, int count) {
			this(arg, price, count, 0.05F);
		}

		public SellItemFactory(class_7317 arg, int price, int count, float f) {
			this.field_38535 = arg;
			this.maxUses = price;
			this.experience = count;
			this.multiplier = f;
		}

		@Override
		public TradeOffer create(Entity entity, Random random) {
			return new TradeOffer(TradeOffers.field_38533, this.field_38535, this.maxUses, this.experience, this.multiplier);
		}
	}
}
