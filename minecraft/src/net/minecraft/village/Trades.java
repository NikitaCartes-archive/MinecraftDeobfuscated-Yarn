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
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.DyeColor;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class Trades {
	public static final Map<VillagerProfession, Int2ObjectMap<Trades.Factory[]>> PROFESSION_TO_LEVELED_TRADE = SystemUtil.consume(
		Maps.<VillagerProfession, Int2ObjectMap<Trades.Factory[]>>newHashMap(),
		hashMap -> {
			hashMap.put(
				VillagerProfession.field_17056,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new Trades.Factory[]{
							new Trades.class_4161(Items.field_8861, 20, 4, 2),
							new Trades.class_4161(Items.field_8567, 26, 4, 2),
							new Trades.class_4161(Items.field_8179, 22, 4, 2),
							new Trades.class_4161(Items.field_8186, 15, 4, 2),
							new Trades.class_4165(Items.field_8229, 1, 6, 1)
						},
						2,
						new Trades.Factory[]{
							new Trades.class_4161(Blocks.field_10261, 6, 4, 10), new Trades.class_4165(Items.field_8741, 1, 4, 5), new Trades.class_4165(Items.field_8279, 1, 4, 5)
						},
						3,
						new Trades.Factory[]{new Trades.class_4165(Items.field_8423, 3, 18, 10), new Trades.class_4161(Blocks.field_10545, 4, 4, 20)},
						4,
						new Trades.Factory[]{
							new Trades.class_4165(Blocks.field_10183, 1, 1, 15),
							new Trades.class_4166(StatusEffects.field_5904, 8, 15),
							new Trades.class_4166(StatusEffects.field_5913, 8, 15),
							new Trades.class_4166(StatusEffects.field_5911, 7, 15),
							new Trades.class_4166(StatusEffects.field_5919, 6, 15),
							new Trades.class_4166(StatusEffects.field_5899, 14, 15),
							new Trades.class_4166(StatusEffects.field_5922, 8, 15)
						},
						5,
						new Trades.Factory[]{new Trades.class_4165(Items.field_8071, 3, 3, 30), new Trades.class_4165(Items.field_8597, 4, 3, 30)}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17057,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new Trades.Factory[]{
							new Trades.class_4161(Items.field_8276, 20, 4, 2),
							new Trades.class_4161(Items.field_8713, 10, 4, 2),
							new Trades.class_4164(Items.field_8429, 6, Items.field_8373, 6, 4, 1),
							new Trades.class_4165(Items.field_8666, 3, 1, 1)
						},
						2,
						new Trades.Factory[]{
							new Trades.class_4161(Items.field_8429, 15, 4, 10),
							new Trades.class_4164(Items.field_8209, 6, Items.field_8509, 6, 4, 5),
							new Trades.class_4165(Items.CAMPFIRE, 2, 1, 5)
						},
						3,
						new Trades.Factory[]{new Trades.class_4161(Items.field_8209, 13, 4, 20), new Trades.class_4163(Items.field_8378, 3, 1, 10, 0.2F)},
						4,
						new Trades.Factory[]{new Trades.class_4161(Items.field_8846, 6, 4, 30)},
						5,
						new Trades.Factory[]{
							new Trades.class_4161(Items.field_8323, 4, 4, 30),
							new Trades.class_4162(
								1,
								1,
								30,
								ImmutableMap.<VillagerType, Item>builder()
									.put(VillagerType.field_17073, Items.field_8533)
									.put(VillagerType.field_17077, Items.field_8486)
									.put(VillagerType.field_17075, Items.field_8486)
									.put(VillagerType.field_17071, Items.field_8730)
									.put(VillagerType.field_17072, Items.field_8730)
									.put(VillagerType.field_17074, Items.field_8094)
									.put(VillagerType.field_17076, Items.field_8138)
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
						new Trades.Factory[]{
							new Trades.class_4161(Blocks.field_10446, 18, 4, 2),
							new Trades.class_4161(Blocks.field_10113, 18, 4, 2),
							new Trades.class_4161(Blocks.field_10146, 18, 4, 2),
							new Trades.class_4161(Blocks.field_10423, 18, 4, 2),
							new Trades.class_4165(Items.field_8868, 2, 1, 1)
						},
						2,
						new Trades.Factory[]{
							new Trades.class_4161(Items.field_8446, 12, 4, 10),
							new Trades.class_4161(Items.field_8298, 12, 4, 10),
							new Trades.class_4161(Items.field_8226, 12, 4, 10),
							new Trades.class_4161(Items.field_8273, 12, 4, 10),
							new Trades.class_4161(Items.field_8131, 12, 4, 10),
							new Trades.class_4165(Blocks.field_10446, 1, 1, 5),
							new Trades.class_4165(Blocks.field_10095, 1, 1, 5),
							new Trades.class_4165(Blocks.field_10215, 1, 1, 5),
							new Trades.class_4165(Blocks.field_10294, 1, 1, 5),
							new Trades.class_4165(Blocks.field_10490, 1, 1, 5),
							new Trades.class_4165(Blocks.field_10028, 1, 1, 5),
							new Trades.class_4165(Blocks.field_10459, 1, 1, 5),
							new Trades.class_4165(Blocks.field_10423, 1, 1, 5),
							new Trades.class_4165(Blocks.field_10222, 1, 1, 5),
							new Trades.class_4165(Blocks.field_10619, 1, 1, 5),
							new Trades.class_4165(Blocks.field_10259, 1, 1, 5),
							new Trades.class_4165(Blocks.field_10514, 1, 1, 5),
							new Trades.class_4165(Blocks.field_10113, 1, 1, 5),
							new Trades.class_4165(Blocks.field_10170, 1, 1, 5),
							new Trades.class_4165(Blocks.field_10314, 1, 1, 5),
							new Trades.class_4165(Blocks.field_10146, 1, 1, 5),
							new Trades.class_4165(Blocks.field_10466, 1, 4, 5),
							new Trades.class_4165(Blocks.field_9977, 1, 4, 5),
							new Trades.class_4165(Blocks.field_10482, 1, 4, 5),
							new Trades.class_4165(Blocks.field_10290, 1, 4, 5),
							new Trades.class_4165(Blocks.field_10512, 1, 4, 5),
							new Trades.class_4165(Blocks.field_10040, 1, 4, 5),
							new Trades.class_4165(Blocks.field_10393, 1, 4, 5),
							new Trades.class_4165(Blocks.field_10591, 1, 4, 5),
							new Trades.class_4165(Blocks.field_10209, 1, 4, 5),
							new Trades.class_4165(Blocks.field_10433, 1, 4, 5),
							new Trades.class_4165(Blocks.field_10510, 1, 4, 5),
							new Trades.class_4165(Blocks.field_10043, 1, 4, 5),
							new Trades.class_4165(Blocks.field_10473, 1, 4, 5),
							new Trades.class_4165(Blocks.field_10338, 1, 4, 5),
							new Trades.class_4165(Blocks.field_10536, 1, 4, 5),
							new Trades.class_4165(Blocks.field_10106, 1, 4, 5)
						},
						3,
						new Trades.Factory[]{
							new Trades.class_4161(Items.field_8192, 12, 4, 20),
							new Trades.class_4161(Items.field_8851, 12, 4, 20),
							new Trades.class_4161(Items.field_8492, 12, 4, 20),
							new Trades.class_4161(Items.field_8264, 12, 4, 20),
							new Trades.class_4161(Items.field_8330, 12, 4, 20),
							new Trades.class_4165(Blocks.field_10120, 3, 1, 10),
							new Trades.class_4165(Blocks.field_10356, 3, 1, 10),
							new Trades.class_4165(Blocks.field_10069, 3, 1, 10),
							new Trades.class_4165(Blocks.field_10461, 3, 1, 10),
							new Trades.class_4165(Blocks.field_10527, 3, 1, 10),
							new Trades.class_4165(Blocks.field_10288, 3, 1, 10),
							new Trades.class_4165(Blocks.field_10109, 3, 1, 10),
							new Trades.class_4165(Blocks.field_10141, 3, 1, 10),
							new Trades.class_4165(Blocks.field_10561, 3, 1, 10),
							new Trades.class_4165(Blocks.field_10621, 3, 1, 10),
							new Trades.class_4165(Blocks.field_10326, 3, 1, 10),
							new Trades.class_4165(Blocks.field_10180, 3, 1, 10),
							new Trades.class_4165(Blocks.field_10230, 3, 1, 10),
							new Trades.class_4165(Blocks.field_10410, 3, 1, 10),
							new Trades.class_4165(Blocks.field_10610, 3, 1, 10),
							new Trades.class_4165(Blocks.field_10019, 3, 1, 10)
						},
						4,
						new Trades.Factory[]{
							new Trades.class_4161(Items.field_8099, 12, 4, 30),
							new Trades.class_4161(Items.field_8296, 12, 4, 30),
							new Trades.class_4161(Items.field_8345, 12, 4, 30),
							new Trades.class_4161(Items.field_8408, 12, 4, 30),
							new Trades.class_4161(Items.field_8669, 12, 4, 30),
							new Trades.class_4161(Items.field_8632, 12, 4, 30),
							new Trades.class_4165(Items.field_8539, 3, 1, 15),
							new Trades.class_4165(Items.field_8128, 3, 1, 15),
							new Trades.class_4165(Items.field_8379, 3, 1, 15),
							new Trades.class_4165(Items.field_8586, 3, 1, 15),
							new Trades.class_4165(Items.field_8329, 3, 1, 15),
							new Trades.class_4165(Items.field_8295, 3, 1, 15),
							new Trades.class_4165(Items.field_8778, 3, 1, 15),
							new Trades.class_4165(Items.field_8617, 3, 1, 15),
							new Trades.class_4165(Items.field_8572, 3, 1, 15),
							new Trades.class_4165(Items.field_8405, 3, 1, 15),
							new Trades.class_4165(Items.field_8671, 3, 1, 15),
							new Trades.class_4165(Items.field_8629, 3, 1, 15),
							new Trades.class_4165(Items.field_8124, 3, 1, 15),
							new Trades.class_4165(Items.field_8049, 3, 1, 15),
							new Trades.class_4165(Items.field_8824, 3, 1, 15),
							new Trades.class_4165(Items.field_8855, 3, 1, 15)
						},
						5,
						new Trades.Factory[]{new Trades.class_4165(Items.field_8892, 2, 3, 30)}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17058,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new Trades.Factory[]{
							new Trades.class_4161(Items.field_8600, 32, 4, 2),
							new Trades.class_4165(Items.field_8107, 1, 16, 1),
							new Trades.class_4164(Blocks.field_10255, 10, Items.field_8145, 10, 4, 1)
						},
						2,
						new Trades.Factory[]{new Trades.class_4161(Items.field_8145, 26, 4, 10), new Trades.class_4165(Items.field_8102, 2, 1, 5)},
						3,
						new Trades.Factory[]{new Trades.class_4161(Items.field_8276, 14, 4, 20), new Trades.class_4165(Items.field_8399, 3, 1, 10)},
						4,
						new Trades.Factory[]{new Trades.class_4161(Items.field_8153, 24, 4, 30), new Trades.class_4163(Items.field_8102, 2, 1, 15)},
						5,
						new Trades.Factory[]{
							new Trades.class_4161(Items.TRIPWIRE_HOOK, 8, 4, 30),
							new Trades.class_4163(Items.field_8399, 3, 1, 15),
							new Trades.class_4167(Items.field_8107, 5, Items.field_8087, 5, 2, 4, 30)
						}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17060,
				copyToFastUtilMap(
					ImmutableMap.<Integer, Trades.Factory[]>builder()
						.put(
							1,
							new Trades.Factory[]{
								new Trades.class_4161(Items.field_8407, 24, 4, 2), new Trades.EnchantBookFactory(1), new Trades.class_4165(Blocks.field_10504, 6, 3, 1)
							}
						)
						.put(
							2,
							new Trades.Factory[]{new Trades.class_4161(Items.field_8529, 4, 4, 10), new Trades.EnchantBookFactory(5), new Trades.class_4165(Items.LANTERN, 1, 1, 5)}
						)
						.put(
							3,
							new Trades.Factory[]{new Trades.class_4161(Items.field_8794, 5, 4, 20), new Trades.EnchantBookFactory(10), new Trades.class_4165(Items.GLASS, 1, 4, 10)}
						)
						.put(
							4,
							new Trades.Factory[]{
								new Trades.class_4161(Items.field_8674, 2, 4, 30),
								new Trades.EnchantBookFactory(15),
								new Trades.class_4165(Items.field_8557, 5, 1, 15),
								new Trades.class_4165(Items.field_8251, 4, 1, 15)
							}
						)
						.put(5, new Trades.Factory[]{new Trades.class_4165(Items.field_8448, 20, 1, 30)})
						.build()
				)
			);
			hashMap.put(
				VillagerProfession.field_17054,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new Trades.Factory[]{new Trades.class_4161(Items.field_8407, 24, 4, 2), new Trades.class_4165(Items.field_8895, 7, 1, 1)},
						2,
						new Trades.Factory[]{new Trades.class_4161(Items.GLASS_PANE, 10, 4, 10), new Trades.SellMapFactory(13, "Monument", MapIcon.Type.field_98, 4, 5)},
						3,
						new Trades.Factory[]{new Trades.class_4161(Items.field_8251, 1, 4, 20), new Trades.SellMapFactory(14, "Mansion", MapIcon.Type.field_88, 4, 10)},
						4,
						new Trades.Factory[]{
							new Trades.class_4165(Items.field_8143, 7, 1, 15),
							new Trades.class_4165(Items.field_8539, 3, 1, 15),
							new Trades.class_4165(Items.field_8128, 3, 1, 15),
							new Trades.class_4165(Items.field_8379, 3, 1, 15),
							new Trades.class_4165(Items.field_8586, 3, 1, 15),
							new Trades.class_4165(Items.field_8329, 3, 1, 15),
							new Trades.class_4165(Items.field_8295, 3, 1, 15),
							new Trades.class_4165(Items.field_8778, 3, 1, 15),
							new Trades.class_4165(Items.field_8617, 3, 1, 15),
							new Trades.class_4165(Items.field_8572, 3, 1, 15),
							new Trades.class_4165(Items.field_8405, 3, 1, 15),
							new Trades.class_4165(Items.field_8671, 3, 1, 15),
							new Trades.class_4165(Items.field_8629, 3, 1, 15),
							new Trades.class_4165(Items.field_8124, 3, 1, 15),
							new Trades.class_4165(Items.field_8049, 3, 1, 15),
							new Trades.class_4165(Items.field_8824, 3, 1, 15),
							new Trades.class_4165(Items.field_8855, 3, 1, 15)
						},
						5,
						new Trades.Factory[]{new Trades.class_4165(Items.field_18674, 8, 1, 30)}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17055,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new Trades.Factory[]{new Trades.class_4161(Items.field_8511, 32, 4, 2), new Trades.class_4165(Items.field_8725, 1, 2, 1)},
						2,
						new Trades.Factory[]{new Trades.class_4161(Items.field_8695, 3, 4, 10), new Trades.class_4165(Items.field_8759, 1, 1, 5)},
						3,
						new Trades.Factory[]{new Trades.class_4161(Items.field_8073, 2, 4, 20), new Trades.class_4165(Blocks.field_10171, 4, 1, 10)},
						4,
						new Trades.Factory[]{
							new Trades.class_4161(Items.field_8161, 4, 4, 30), new Trades.class_4161(Items.field_8469, 9, 4, 30), new Trades.class_4165(Items.field_8634, 5, 1, 15)
						},
						5,
						new Trades.Factory[]{new Trades.class_4161(Items.field_8790, 22, 4, 30), new Trades.class_4165(Items.field_8287, 3, 1, 30)}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17052,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new Trades.Factory[]{
							new Trades.class_4161(Items.field_8713, 15, 4, 2),
							new Trades.class_4165(new ItemStack(Items.field_8396), 7, 1, 4, 1, 0.2F),
							new Trades.class_4165(new ItemStack(Items.field_8660), 4, 1, 4, 1, 0.2F),
							new Trades.class_4165(new ItemStack(Items.field_8743), 5, 1, 4, 1, 0.2F),
							new Trades.class_4165(new ItemStack(Items.field_8523), 9, 1, 4, 1, 0.2F)
						},
						2,
						new Trades.Factory[]{
							new Trades.class_4161(Items.field_8620, 4, 4, 10),
							new Trades.class_4165(new ItemStack(Items.BELL), 36, 1, 4, 5, 0.2F),
							new Trades.class_4165(new ItemStack(Items.field_8313), 1, 1, 4, 5, 0.2F),
							new Trades.class_4165(new ItemStack(Items.field_8218), 3, 1, 4, 5, 0.2F)
						},
						3,
						new Trades.Factory[]{
							new Trades.class_4161(Items.field_8187, 1, 4, 20),
							new Trades.class_4161(Items.field_8477, 1, 4, 20),
							new Trades.class_4165(new ItemStack(Items.field_8283), 1, 1, 4, 10, 0.2F),
							new Trades.class_4165(new ItemStack(Items.field_8873), 4, 1, 4, 10, 0.2F),
							new Trades.class_4165(new ItemStack(Items.field_8255), 5, 1, 4, 10, 0.2F)
						},
						4,
						new Trades.Factory[]{new Trades.class_4163(Items.field_8348, 14, 1, 15, 0.2F), new Trades.class_4163(Items.field_8285, 8, 1, 15, 0.2F)},
						5,
						new Trades.Factory[]{new Trades.class_4163(Items.field_8805, 8, 1, 30, 0.2F), new Trades.class_4163(Items.field_8058, 16, 1, 30, 0.2F)}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17065,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new Trades.Factory[]{
							new Trades.class_4161(Items.field_8713, 15, 4, 2),
							new Trades.class_4165(new ItemStack(Items.field_8475), 3, 1, 4, 1, 0.2F),
							new Trades.class_4163(Items.field_8371, 2, 1, 1)
						},
						2,
						new Trades.Factory[]{new Trades.class_4161(Items.field_8620, 4, 4, 10), new Trades.class_4165(new ItemStack(Items.BELL), 36, 1, 4, 5, 0.2F)},
						3,
						new Trades.Factory[]{new Trades.class_4161(Items.field_8145, 24, 4, 20)},
						4,
						new Trades.Factory[]{new Trades.class_4161(Items.field_8477, 1, 4, 30), new Trades.class_4163(Items.field_8556, 12, 1, 15, 0.2F)},
						5,
						new Trades.Factory[]{new Trades.class_4163(Items.field_8802, 8, 1, 30, 0.2F)}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17064,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new Trades.Factory[]{
							new Trades.class_4161(Items.field_8713, 15, 4, 2),
							new Trades.class_4165(new ItemStack(Items.field_8062), 1, 1, 4, 1, 0.2F),
							new Trades.class_4165(new ItemStack(Items.field_8776), 1, 1, 4, 1, 0.2F),
							new Trades.class_4165(new ItemStack(Items.field_8387), 1, 1, 4, 1, 0.2F),
							new Trades.class_4165(new ItemStack(Items.field_8431), 1, 1, 4, 1, 0.2F)
						},
						2,
						new Trades.Factory[]{new Trades.class_4161(Items.field_8620, 4, 4, 10), new Trades.class_4165(new ItemStack(Items.BELL), 36, 1, 4, 5, 0.2F)},
						3,
						new Trades.Factory[]{
							new Trades.class_4161(Items.field_8145, 30, 4, 20),
							new Trades.class_4163(Items.field_8475, 1, 1, 10, 0.2F),
							new Trades.class_4163(Items.field_8699, 2, 1, 10, 0.2F),
							new Trades.class_4163(Items.field_8403, 3, 1, 10, 0.2F),
							new Trades.class_4163(Items.field_8527, 9, 1, 10, 0.2F)
						},
						4,
						new Trades.Factory[]{
							new Trades.class_4161(Items.field_8477, 1, 4, 30),
							new Trades.class_4163(Items.field_8556, 12, 1, 15, 0.2F),
							new Trades.class_4163(Items.field_8250, 5, 1, 15, 0.2F)
						},
						5,
						new Trades.Factory[]{new Trades.class_4163(Items.field_8377, 13, 1, 30, 0.2F)}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17053,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new Trades.Factory[]{
							new Trades.class_4161(Items.field_8726, 14, 4, 2),
							new Trades.class_4161(Items.field_8389, 7, 4, 2),
							new Trades.class_4161(Items.field_8504, 4, 4, 2),
							new Trades.class_4165(Items.field_8308, 1, 1, 1)
						},
						2,
						new Trades.Factory[]{
							new Trades.class_4161(Items.field_8713, 15, 4, 2), new Trades.class_4165(Items.field_8261, 1, 5, 5), new Trades.class_4165(Items.field_8544, 1, 8, 5)
						},
						3,
						new Trades.Factory[]{new Trades.class_4161(Items.field_8748, 7, 4, 20), new Trades.class_4161(Items.field_8046, 10, 4, 20)},
						4,
						new Trades.Factory[]{new Trades.class_4161(Items.DRIED_KELP_BLOCK, 10, 4, 30)},
						5,
						new Trades.Factory[]{new Trades.class_4161(Items.field_16998, 10, 4, 30)}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17059,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new Trades.Factory[]{
							new Trades.class_4161(Items.field_8745, 6, 4, 2), new Trades.class_4160(Items.field_8570, 3), new Trades.class_4160(Items.field_8577, 7)
						},
						2,
						new Trades.Factory[]{
							new Trades.class_4161(Items.field_8145, 26, 4, 10), new Trades.class_4160(Items.field_8267, 5, 4, 5), new Trades.class_4160(Items.field_8370, 4, 4, 5)
						},
						3,
						new Trades.Factory[]{new Trades.class_4161(Items.field_8245, 9, 4, 20), new Trades.class_4160(Items.field_8577, 7)},
						4,
						new Trades.Factory[]{new Trades.class_4161(Items.field_8161, 4, 4, 30), new Trades.class_4160(Items.field_18138, 6, 4, 15)},
						5,
						new Trades.Factory[]{new Trades.class_4165(new ItemStack(Items.field_8175), 6, 1, 4, 30, 0.2F), new Trades.class_4160(Items.field_8267, 5, 4, 30)}
					)
				)
			);
			hashMap.put(
				VillagerProfession.field_17061,
				copyToFastUtilMap(
					ImmutableMap.of(
						1,
						new Trades.Factory[]{new Trades.class_4161(Items.field_8696, 10, 4, 2), new Trades.class_4165(Items.field_8621, 1, 10, 1)},
						2,
						new Trades.Factory[]{new Trades.class_4161(Blocks.field_10340, 20, 4, 10), new Trades.class_4165(Blocks.field_10552, 1, 4, 5)},
						3,
						new Trades.Factory[]{
							new Trades.class_4161(Blocks.field_10474, 16, 4, 20),
							new Trades.class_4161(Blocks.field_10115, 16, 4, 20),
							new Trades.class_4161(Blocks.field_10508, 16, 4, 20),
							new Trades.class_4165(Blocks.field_10093, 1, 4, 10),
							new Trades.class_4165(Blocks.field_10346, 1, 4, 10),
							new Trades.class_4165(Blocks.field_10289, 1, 4, 10)
						},
						4,
						new Trades.Factory[]{
							new Trades.class_4161(Items.field_8155, 12, 4, 30),
							new Trades.class_4165(Blocks.field_10184, 1, 1, 15),
							new Trades.class_4165(Blocks.field_10611, 1, 1, 15),
							new Trades.class_4165(Blocks.field_10409, 1, 1, 15),
							new Trades.class_4165(Blocks.field_10325, 1, 1, 15),
							new Trades.class_4165(Blocks.field_10349, 1, 1, 15),
							new Trades.class_4165(Blocks.field_10590, 1, 1, 15),
							new Trades.class_4165(Blocks.field_10626, 1, 1, 15),
							new Trades.class_4165(Blocks.field_10328, 1, 1, 15),
							new Trades.class_4165(Blocks.field_10444, 1, 1, 15),
							new Trades.class_4165(Blocks.field_10015, 1, 1, 15),
							new Trades.class_4165(Blocks.field_10014, 1, 1, 15),
							new Trades.class_4165(Blocks.field_10526, 1, 1, 15),
							new Trades.class_4165(Blocks.field_10235, 1, 1, 15),
							new Trades.class_4165(Blocks.field_10570, 1, 1, 15),
							new Trades.class_4165(Blocks.field_10143, 1, 1, 15),
							new Trades.class_4165(Blocks.field_10123, 1, 1, 15)
						},
						5,
						new Trades.Factory[]{new Trades.class_4165(Blocks.field_10437, 1, 1, 30)}
					)
				)
			);
		}
	);
	public static final Int2ObjectMap<Trades.Factory[]> WANDERING_TRADER_TRADES = copyToFastUtilMap(
		ImmutableMap.of(
			1,
			new Trades.Factory[]{
				new Trades.class_4165(Items.SEA_PICKLE, 2, 1, 5, 1),
				new Trades.class_4165(Items.field_8777, 4, 1, 5, 1),
				new Trades.class_4165(Items.GLOWSTONE, 2, 1, 5, 1),
				new Trades.class_4165(Items.field_8864, 5, 1, 5, 1),
				new Trades.class_4165(Items.FERN, 1, 1, 12, 1),
				new Trades.class_4165(Items.SUGAR_CANE, 1, 1, 8, 1),
				new Trades.class_4165(Items.PUMPKIN, 1, 1, 4, 1),
				new Trades.class_4165(Items.KELP, 3, 1, 12, 1),
				new Trades.class_4165(Items.CACTUS, 3, 1, 8, 1),
				new Trades.class_4165(Items.DANDELION, 1, 1, 12, 1),
				new Trades.class_4165(Items.POPPY, 1, 1, 12, 1),
				new Trades.class_4165(Items.BLUE_ORCHID, 1, 1, 8, 1),
				new Trades.class_4165(Items.ALLIUM, 1, 1, 12, 1),
				new Trades.class_4165(Items.AZURE_BLUET, 1, 1, 12, 1),
				new Trades.class_4165(Items.RED_TULIP, 1, 1, 12, 1),
				new Trades.class_4165(Items.ORANGE_TULIP, 1, 1, 12, 1),
				new Trades.class_4165(Items.WHITE_TULIP, 1, 1, 12, 1),
				new Trades.class_4165(Items.PINK_TULIP, 1, 1, 12, 1),
				new Trades.class_4165(Items.OXEYE_DAISY, 1, 1, 12, 1),
				new Trades.class_4165(Items.CORNFLOWER, 1, 1, 12, 1),
				new Trades.class_4165(Items.LILY_OF_THE_VALLEY, 1, 1, 7, 1),
				new Trades.class_4165(Items.field_8317, 1, 1, 12, 1),
				new Trades.class_4165(Items.field_8309, 1, 1, 12, 1),
				new Trades.class_4165(Items.field_8706, 1, 1, 12, 1),
				new Trades.class_4165(Items.field_8188, 1, 1, 12, 1),
				new Trades.class_4165(Items.ACACIA_SAPLING, 5, 1, 8, 1),
				new Trades.class_4165(Items.BIRCH_SAPLING, 5, 1, 8, 1),
				new Trades.class_4165(Items.DARK_OAK_SAPLING, 5, 1, 8, 1),
				new Trades.class_4165(Items.JUNGLE_SAPLING, 5, 1, 8, 1),
				new Trades.class_4165(Items.OAK_SAPLING, 5, 1, 8, 1),
				new Trades.class_4165(Items.SPRUCE_SAPLING, 5, 1, 8, 1),
				new Trades.class_4165(Items.field_8264, 1, 3, 12, 1),
				new Trades.class_4165(Items.field_8446, 1, 3, 12, 1),
				new Trades.class_4165(Items.field_8345, 1, 3, 12, 1),
				new Trades.class_4165(Items.field_8330, 1, 3, 12, 1),
				new Trades.class_4165(Items.field_8226, 1, 3, 12, 1),
				new Trades.class_4165(Items.field_8408, 1, 3, 12, 1),
				new Trades.class_4165(Items.field_8851, 1, 3, 12, 1),
				new Trades.class_4165(Items.field_8669, 1, 3, 12, 1),
				new Trades.class_4165(Items.field_8192, 1, 3, 12, 1),
				new Trades.class_4165(Items.field_8298, 1, 3, 12, 1),
				new Trades.class_4165(Items.field_8296, 1, 3, 12, 1),
				new Trades.class_4165(Items.field_8273, 1, 3, 12, 1),
				new Trades.class_4165(Items.field_8131, 1, 3, 12, 1),
				new Trades.class_4165(Items.field_8492, 1, 3, 12, 1),
				new Trades.class_4165(Items.field_8099, 1, 3, 12, 1),
				new Trades.class_4165(Items.field_8632, 1, 3, 12, 1),
				new Trades.class_4165(Items.BRAIN_CORAL_BLOCK, 3, 1, 8, 1),
				new Trades.class_4165(Items.BUBBLE_CORAL_BLOCK, 3, 1, 8, 1),
				new Trades.class_4165(Items.FIRE_CORAL_BLOCK, 3, 1, 8, 1),
				new Trades.class_4165(Items.HORN_CORAL_BLOCK, 3, 1, 8, 1),
				new Trades.class_4165(Items.TUBE_CORAL_BLOCK, 3, 1, 8, 1),
				new Trades.class_4165(Items.VINE, 1, 1, 12, 1),
				new Trades.class_4165(Items.BROWN_MUSHROOM, 1, 1, 12, 1),
				new Trades.class_4165(Items.RED_MUSHROOM, 1, 1, 12, 1),
				new Trades.class_4165(Items.LILY_PAD, 1, 2, 5, 1),
				new Trades.class_4165(Items.SAND, 1, 8, 8, 1),
				new Trades.class_4165(Items.RED_SAND, 1, 4, 6, 1)
			},
			2,
			new Trades.Factory[]{
				new Trades.class_4165(Items.field_8478, 5, 1, 4, 1),
				new Trades.class_4165(Items.field_8108, 5, 1, 4, 1),
				new Trades.class_4165(Items.PACKED_ICE, 3, 1, 6, 1),
				new Trades.class_4165(Items.BLUE_ICE, 6, 1, 6, 1),
				new Trades.class_4165(Items.field_8054, 1, 1, 8, 1),
				new Trades.class_4165(Items.PODZOL, 3, 3, 6, 1)
			}
		)
	);

	private static Int2ObjectMap<Trades.Factory[]> copyToFastUtilMap(ImmutableMap<Integer, Trades.Factory[]> immutableMap) {
		return new Int2ObjectOpenHashMap<>(immutableMap);
	}

	static class EnchantBookFactory implements Trades.Factory {
		private final int field_18557;

		public EnchantBookFactory(int i) {
			this.field_18557 = i;
		}

		@Override
		public TraderRecipe method_7246(Entity entity, Random random) {
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

			return new TraderRecipe(new ItemStack(Items.field_8687, j), new ItemStack(Items.field_8529), itemStack, 4, this.field_18557, 0.2F);
		}
	}

	public interface Factory {
		@Nullable
		TraderRecipe method_7246(Entity entity, Random random);
	}

	static class SellMapFactory implements Trades.Factory {
		private final int field_18589;
		private final String structure;
		private final MapIcon.Type field_7473;
		private final int field_18590;
		private final int field_18591;

		public SellMapFactory(int i, String string, MapIcon.Type type, int j, int k) {
			this.field_18589 = i;
			this.structure = string;
			this.field_7473 = type;
			this.field_18590 = j;
			this.field_18591 = k;
		}

		@Nullable
		@Override
		public TraderRecipe method_7246(Entity entity, Random random) {
			World world = entity.field_6002;
			BlockPos blockPos = world.method_8487(this.structure, new BlockPos(entity), 100, true);
			if (blockPos != null) {
				ItemStack itemStack = FilledMapItem.method_8005(world, blockPos.getX(), blockPos.getZ(), (byte)2, true, true);
				FilledMapItem.method_8002(world, itemStack);
				MapState.method_110(itemStack, blockPos, "+", this.field_7473);
				itemStack.method_7977(new TranslatableTextComponent("filled_map." + this.structure.toLowerCase(Locale.ROOT)));
				return new TraderRecipe(
					new ItemStack(Items.field_8687, this.field_18589), new ItemStack(Items.field_8251), itemStack, this.field_18590, this.field_18591, 0.2F
				);
			} else {
				return null;
			}
		}
	}

	static class class_4160 implements Trades.Factory {
		private final Item field_18544;
		private final int field_18545;
		private final int field_18546;
		private final int field_18547;

		public class_4160(Item item, int i) {
			this(item, i, 4, 1);
		}

		public class_4160(Item item, int i, int j, int k) {
			this.field_18544 = item;
			this.field_18545 = i;
			this.field_18546 = j;
			this.field_18547 = k;
		}

		@Override
		public TraderRecipe method_7246(Entity entity, Random random) {
			ItemStack itemStack = new ItemStack(Items.field_8687, this.field_18545);
			ItemStack itemStack2 = new ItemStack(this.field_18544);
			if (this.field_18544 instanceof DyeableArmorItem) {
				List<DyeItem> list = Lists.<DyeItem>newArrayList();
				list.add(method_19200(random));
				if (random.nextFloat() > 0.7F) {
					list.add(method_19200(random));
				}

				if (random.nextFloat() > 0.8F) {
					list.add(method_19200(random));
				}

				itemStack2 = DyeableItem.method_19261(itemStack2, list);
			}

			return new TraderRecipe(itemStack, itemStack2, this.field_18546, this.field_18547, 0.2F);
		}

		private static DyeItem method_19200(Random random) {
			return DyeItem.fromColor(DyeColor.byId(random.nextInt(16)));
		}
	}

	static class class_4161 implements Trades.Factory {
		private final Item field_18548;
		private final int field_18549;
		private final int field_18550;
		private final int field_18551;
		private final float field_18552;

		public class_4161(ItemProvider itemProvider, int i, int j, int k) {
			this.field_18548 = itemProvider.getItem();
			this.field_18549 = i;
			this.field_18550 = j;
			this.field_18551 = k;
			this.field_18552 = 0.05F;
		}

		@Override
		public TraderRecipe method_7246(Entity entity, Random random) {
			ItemStack itemStack = new ItemStack(this.field_18548, this.field_18549);
			return new TraderRecipe(itemStack, new ItemStack(Items.field_8687), this.field_18550, this.field_18551, this.field_18552);
		}
	}

	static class class_4162 implements Trades.Factory {
		private final Map<VillagerType, Item> field_18553;
		private final int field_18554;
		private final int field_18555;
		private final int field_18556;

		public class_4162(int i, int j, int k, Map<VillagerType, Item> map) {
			Registry.VILLAGER_TYPE.stream().filter(villagerType -> !map.containsKey(villagerType)).findAny().ifPresent(villagerType -> {
				throw new IllegalStateException("Missing trade for villager type: " + Registry.VILLAGER_TYPE.method_10221(villagerType));
			});
			this.field_18553 = map;
			this.field_18554 = i;
			this.field_18555 = j;
			this.field_18556 = k;
		}

		@Nullable
		@Override
		public TraderRecipe method_7246(Entity entity, Random random) {
			if (entity instanceof VillagerDataContainer) {
				ItemStack itemStack = new ItemStack((ItemProvider)this.field_18553.get(((VillagerDataContainer)entity).getVillagerData().method_16919()), this.field_18554);
				return new TraderRecipe(itemStack, new ItemStack(Items.field_8687), this.field_18555, this.field_18556, 0.05F);
			} else {
				return null;
			}
		}
	}

	static class class_4163 implements Trades.Factory {
		private final ItemStack field_18558;
		private final int field_18559;
		private final int field_18560;
		private final int field_18561;
		private final float field_18562;

		public class_4163(Item item, int i, int j, int k) {
			this(item, i, j, k, 0.05F);
		}

		public class_4163(Item item, int i, int j, int k, float f) {
			this.field_18558 = new ItemStack(item);
			this.field_18559 = i;
			this.field_18560 = j;
			this.field_18561 = k;
			this.field_18562 = f;
		}

		@Override
		public TraderRecipe method_7246(Entity entity, Random random) {
			int i = 5 + random.nextInt(15);
			ItemStack itemStack = EnchantmentHelper.enchant(random, new ItemStack(this.field_18558.getItem()), i, false);
			int j = Math.min(this.field_18559 + i, 64);
			ItemStack itemStack2 = new ItemStack(Items.field_8687, j);
			return new TraderRecipe(itemStack2, itemStack, this.field_18560, this.field_18561, this.field_18562);
		}
	}

	static class class_4164 implements Trades.Factory {
		private final ItemStack field_18563;
		private final int field_18564;
		private final int field_18565;
		private final ItemStack field_18566;
		private final int field_18567;
		private final int field_18568;
		private final int field_18569;
		private final float field_18570;

		public class_4164(ItemProvider itemProvider, int i, Item item, int j, int k, int l) {
			this(itemProvider, i, 1, item, j, k, l);
		}

		public class_4164(ItemProvider itemProvider, int i, int j, Item item, int k, int l, int m) {
			this.field_18563 = new ItemStack(itemProvider);
			this.field_18564 = i;
			this.field_18565 = j;
			this.field_18566 = new ItemStack(item);
			this.field_18567 = k;
			this.field_18568 = l;
			this.field_18569 = m;
			this.field_18570 = 0.05F;
		}

		@Nullable
		@Override
		public TraderRecipe method_7246(Entity entity, Random random) {
			return new TraderRecipe(
				new ItemStack(Items.field_8687, this.field_18565),
				new ItemStack(this.field_18563.getItem(), this.field_18564),
				new ItemStack(this.field_18566.getItem(), this.field_18567),
				this.field_18568,
				this.field_18569,
				this.field_18570
			);
		}
	}

	static class class_4165 implements Trades.Factory {
		private final ItemStack field_18571;
		private final int field_18572;
		private final int field_18573;
		private final int field_18574;
		private final int field_18575;
		private final float field_18576;

		public class_4165(Block block, int i, int j, int k) {
			this(new ItemStack(block), i, j, 4, k);
		}

		public class_4165(Item item, int i, int j, int k) {
			this(new ItemStack(item), i, j, 4, k);
		}

		public class_4165(Item item, int i, int j, int k, int l) {
			this(new ItemStack(item), i, j, k, l);
		}

		public class_4165(ItemStack itemStack, int i, int j, int k, int l) {
			this(itemStack, i, j, k, l, 0.05F);
		}

		public class_4165(ItemStack itemStack, int i, int j, int k, int l, float f) {
			this.field_18571 = itemStack;
			this.field_18572 = i;
			this.field_18573 = j;
			this.field_18574 = k;
			this.field_18575 = l;
			this.field_18576 = f;
		}

		@Override
		public TraderRecipe method_7246(Entity entity, Random random) {
			return new TraderRecipe(
				new ItemStack(Items.field_8687, this.field_18572),
				new ItemStack(this.field_18571.getItem(), this.field_18573),
				this.field_18574,
				this.field_18575,
				this.field_18576
			);
		}
	}

	static class class_4166 implements Trades.Factory {
		final StatusEffect field_18577;
		final int field_18578;
		final int field_18579;
		private final float field_18580;

		public class_4166(StatusEffect statusEffect, int i, int j) {
			this.field_18577 = statusEffect;
			this.field_18578 = i;
			this.field_18579 = j;
			this.field_18580 = 0.05F;
		}

		@Nullable
		@Override
		public TraderRecipe method_7246(Entity entity, Random random) {
			ItemStack itemStack = new ItemStack(Items.field_8766, 1);
			SuspiciousStewItem.addEffectToStew(itemStack, this.field_18577, this.field_18578);
			return new TraderRecipe(new ItemStack(Items.field_8687, 1), itemStack, 4, this.field_18579, this.field_18580);
		}
	}

	static class class_4167 implements Trades.Factory {
		private final ItemStack field_18581;
		private final int field_18582;
		private final int field_18583;
		private final int field_18584;
		private final int field_18585;
		private final Item field_18586;
		private final int field_18587;
		private final float field_18588;

		public class_4167(Item item, int i, Item item2, int j, int k, int l, int m) {
			this.field_18581 = new ItemStack(item2);
			this.field_18583 = k;
			this.field_18584 = l;
			this.field_18585 = m;
			this.field_18586 = item;
			this.field_18587 = i;
			this.field_18582 = j;
			this.field_18588 = 0.05F;
		}

		@Override
		public TraderRecipe method_7246(Entity entity, Random random) {
			ItemStack itemStack = new ItemStack(Items.field_8687, this.field_18583);
			List<Potion> list = (List<Potion>)Registry.POTION.stream().filter(potionx -> !potionx.getEffects().isEmpty()).collect(Collectors.toList());
			Potion potion = (Potion)list.get(random.nextInt(list.size()));
			ItemStack itemStack2 = PotionUtil.setPotion(new ItemStack(this.field_18581.getItem(), this.field_18582), potion);
			return new TraderRecipe(itemStack, new ItemStack(this.field_18586, this.field_18587), itemStack2, this.field_18584, this.field_18585, this.field_18588);
		}
	}
}
