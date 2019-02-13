package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.block.AirBlockItem;
import net.minecraft.item.block.BannerItem;
import net.minecraft.item.block.BedItem;
import net.minecraft.item.block.BlockItem;
import net.minecraft.item.block.CommandBlockItem;
import net.minecraft.item.block.LilyPadItem;
import net.minecraft.item.block.ScaffoldingItem;
import net.minecraft.item.block.SignItem;
import net.minecraft.item.block.SkullItem;
import net.minecraft.item.block.TallBlockItem;
import net.minecraft.item.block.WallStandingBlockItem;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class Items {
	public static final Item AIR = register(Blocks.field_10124, new AirBlockItem(Blocks.field_10124, new Item.Settings()));
	public static final Item field_8352 = registerBlock(Blocks.field_10340, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8532 = registerBlock(Blocks.field_10474, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8642 = registerBlock(Blocks.field_10289, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8413 = registerBlock(Blocks.field_10508, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8221 = registerBlock(Blocks.field_10346, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8082 = registerBlock(Blocks.field_10115, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8123 = registerBlock(Blocks.field_10093, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8270 = registerBlock(Blocks.field_10219, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8831 = registerBlock(Blocks.field_10566, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8460 = registerBlock(Blocks.field_10253, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8382 = registerBlock(Blocks.field_10520, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8724 = registerBlock(Blocks.field_10445, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8118 = registerBlock(Blocks.field_10161, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8113 = registerBlock(Blocks.field_9975, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8191 = registerBlock(Blocks.field_10148, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8842 = registerBlock(Blocks.field_10334, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8651 = registerBlock(Blocks.field_10218, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8404 = registerBlock(Blocks.field_10075, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_17535 = registerBlock(Blocks.field_10394, ItemGroup.DECORATIONS);
	public static final Item field_17536 = registerBlock(Blocks.field_10217, ItemGroup.DECORATIONS);
	public static final Item field_17537 = registerBlock(Blocks.field_10575, ItemGroup.DECORATIONS);
	public static final Item field_17538 = registerBlock(Blocks.field_10276, ItemGroup.DECORATIONS);
	public static final Item field_17539 = registerBlock(Blocks.field_10385, ItemGroup.DECORATIONS);
	public static final Item field_17540 = registerBlock(Blocks.field_10160, ItemGroup.DECORATIONS);
	public static final Item field_8542 = registerBlock(Blocks.field_9987, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8858 = registerBlock(Blocks.field_10102, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8200 = registerBlock(Blocks.field_10534, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8110 = registerBlock(Blocks.field_10255, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8775 = registerBlock(Blocks.field_10571, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8599 = registerBlock(Blocks.field_10212, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8476 = registerBlock(Blocks.field_10418, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8583 = registerBlock(Blocks.field_10431, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8684 = registerBlock(Blocks.field_10037, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8170 = registerBlock(Blocks.field_10511, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8125 = registerBlock(Blocks.field_10306, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8820 = registerBlock(Blocks.field_10533, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8652 = registerBlock(Blocks.field_10010, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8415 = registerBlock(Blocks.field_10519, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8624 = registerBlock(Blocks.field_10436, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8767 = registerBlock(Blocks.field_10366, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8334 = registerBlock(Blocks.field_10254, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8072 = registerBlock(Blocks.field_10622, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8808 = registerBlock(Blocks.field_10244, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8248 = registerBlock(Blocks.field_10250, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8362 = registerBlock(Blocks.field_10558, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8472 = registerBlock(Blocks.field_10204, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8785 = registerBlock(Blocks.field_10084, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8284 = registerBlock(Blocks.field_10103, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8219 = registerBlock(Blocks.field_10374, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8888 = registerBlock(Blocks.field_10126, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8210 = registerBlock(Blocks.field_10155, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8201 = registerBlock(Blocks.field_10307, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8439 = registerBlock(Blocks.field_10303, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8587 = registerBlock(Blocks.field_9999, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8458 = registerBlock(Blocks.field_10178, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_17503 = registerBlock(Blocks.field_10503, ItemGroup.DECORATIONS);
	public static final Item field_17504 = registerBlock(Blocks.field_9988, ItemGroup.DECORATIONS);
	public static final Item field_17505 = registerBlock(Blocks.field_10539, ItemGroup.DECORATIONS);
	public static final Item field_17506 = registerBlock(Blocks.field_10335, ItemGroup.DECORATIONS);
	public static final Item field_17507 = registerBlock(Blocks.field_10098, ItemGroup.DECORATIONS);
	public static final Item field_17508 = registerBlock(Blocks.field_10035, ItemGroup.DECORATIONS);
	public static final Item field_8535 = registerBlock(Blocks.field_10258, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8554 = registerBlock(Blocks.field_10562, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8280 = registerBlock(Blocks.field_10033, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8809 = registerBlock(Blocks.field_10090, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8055 = registerBlock(Blocks.field_10441, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8357 = registerBlock(Blocks.field_10200, ItemGroup.REDSTONE);
	public static final Item field_8709 = registerBlock(Blocks.field_9979, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8552 = registerBlock(Blocks.field_10292, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8489 = registerBlock(Blocks.field_10361, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8643 = registerBlock(Blocks.field_10179, ItemGroup.REDSTONE);
	public static final Item field_8848 = registerBlock(Blocks.field_10425, ItemGroup.TRANSPORTATION);
	public static final Item field_8211 = registerBlock(Blocks.field_10025, ItemGroup.TRANSPORTATION);
	public static final Item field_8105 = registerBlock(Blocks.field_10615, ItemGroup.REDSTONE);
	public static final Item field_8786 = registerBlock(Blocks.field_10343, ItemGroup.DECORATIONS);
	public static final Item field_8602 = registerBlock(Blocks.field_10479, ItemGroup.DECORATIONS);
	public static final Item field_8471 = registerBlock(Blocks.field_10112, ItemGroup.DECORATIONS);
	public static final Item field_8689 = registerBlock(Blocks.field_10428, ItemGroup.DECORATIONS);
	public static final Item field_8158 = registerBlock(Blocks.field_10376, ItemGroup.DECORATIONS);
	public static final Item field_17498 = registerBlock(Blocks.field_10476, ItemGroup.DECORATIONS);
	public static final Item field_8249 = registerBlock(Blocks.field_10560, ItemGroup.REDSTONE);
	public static final Item field_8859 = registerBlock(Blocks.field_10446, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8513 = registerBlock(Blocks.field_10095, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8568 = registerBlock(Blocks.field_10215, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8763 = registerBlock(Blocks.field_10294, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8180 = registerBlock(Blocks.field_10490, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8432 = registerBlock(Blocks.field_10028, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8168 = registerBlock(Blocks.field_10459, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8697 = registerBlock(Blocks.field_10423, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8571 = registerBlock(Blocks.field_10222, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8473 = registerBlock(Blocks.field_10619, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8826 = registerBlock(Blocks.field_10259, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8234 = registerBlock(Blocks.field_10514, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8424 = registerBlock(Blocks.field_10113, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8101 = registerBlock(Blocks.field_10170, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8744 = registerBlock(Blocks.field_10314, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8326 = registerBlock(Blocks.field_10146, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8491 = registerBlock(Blocks.field_10182, ItemGroup.DECORATIONS);
	public static final Item field_8880 = registerBlock(Blocks.field_10449, ItemGroup.DECORATIONS);
	public static final Item field_17499 = registerBlock(Blocks.field_10086, ItemGroup.DECORATIONS);
	public static final Item field_17500 = registerBlock(Blocks.field_10226, ItemGroup.DECORATIONS);
	public static final Item field_17501 = registerBlock(Blocks.field_10573, ItemGroup.DECORATIONS);
	public static final Item field_17502 = registerBlock(Blocks.field_10270, ItemGroup.DECORATIONS);
	public static final Item field_17509 = registerBlock(Blocks.field_10048, ItemGroup.DECORATIONS);
	public static final Item field_17510 = registerBlock(Blocks.field_10156, ItemGroup.DECORATIONS);
	public static final Item field_17511 = registerBlock(Blocks.field_10315, ItemGroup.DECORATIONS);
	public static final Item field_17512 = registerBlock(Blocks.field_10554, ItemGroup.DECORATIONS);
	public static final Item field_17513 = registerBlock(Blocks.field_9995, ItemGroup.DECORATIONS);
	public static final Item field_17514 = registerBlock(Blocks.field_10548, ItemGroup.DECORATIONS);
	public static final Item field_17515 = registerBlock(Blocks.field_10606, ItemGroup.DECORATIONS);
	public static final Item field_17516 = registerBlock(Blocks.field_10251, ItemGroup.DECORATIONS);
	public static final Item field_17517 = registerBlock(Blocks.field_10559, ItemGroup.DECORATIONS);
	public static final Item field_8494 = registerBlock(Blocks.field_10205, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8773 = registerBlock(Blocks.field_10085, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8320 = registerBlock(Blocks.field_10119, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8189 = registerBlock(Blocks.field_10071, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8843 = registerBlock(Blocks.field_10257, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8224 = registerBlock(Blocks.field_10617, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8400 = registerBlock(Blocks.field_10031, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8540 = registerBlock(Blocks.field_10500, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8595 = registerBlock(Blocks.field_10454, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8291 = registerBlock(Blocks.field_10136, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8147 = registerBlock(Blocks.field_10007, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8060 = registerBlock(Blocks.field_10298, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8194 = registerBlock(Blocks.field_10351, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8342 = registerBlock(Blocks.field_10191, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8524 = registerBlock(Blocks.field_10131, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8505 = registerBlock(Blocks.field_10390, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8412 = registerBlock(Blocks.field_10237, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8109 = registerBlock(Blocks.field_10624, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8202 = registerBlock(Blocks.field_10175, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8440 = registerBlock(Blocks.field_10389, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8588 = registerBlock(Blocks.field_10236, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8459 = registerBlock(Blocks.field_10623, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8590 = registerBlock(Blocks.field_9978, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8339 = registerBlock(Blocks.field_10483, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8813 = registerBlock(Blocks.field_10467, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8216 = registerBlock(Blocks.field_10360, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8421 = registerBlock(Blocks.field_10104, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8626 = registerBlock(Blocks.field_10375, ItemGroup.REDSTONE);
	public static final Item field_8536 = registerBlock(Blocks.field_10504, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8555 = registerBlock(Blocks.field_9989, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8281 = registerBlock(Blocks.field_10540, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8810 = register(
		new WallStandingBlockItem(Blocks.field_10336, Blocks.field_10099, new Item.Settings().itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8056 = registerBlock(Blocks.field_10455, ItemGroup.DECORATIONS);
	public static final Item field_8358 = registerBlock(Blocks.field_10021, ItemGroup.DECORATIONS);
	public static final Item field_8710 = registerBlock(Blocks.field_10528, ItemGroup.DECORATIONS);
	public static final Item field_8553 = registerBlock(Blocks.field_10286, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8490 = registerBlock(Blocks.field_10505, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8644 = registerBlock(Blocks.field_9992, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8849 = method_7989(Blocks.field_10260);
	public static final Item field_8212 = registerBlock(Blocks.field_10563, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8106 = registerBlock(Blocks.field_10034, ItemGroup.DECORATIONS);
	public static final Item field_8787 = registerBlock(Blocks.field_10442, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8603 = registerBlock(Blocks.field_10201, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8465 = registerBlock(Blocks.field_9980, ItemGroup.DECORATIONS);
	public static final Item field_8365 = registerBlock(Blocks.field_10362, ItemGroup.DECORATIONS);
	public static final Item field_8732 = registerBlock(Blocks.field_10181, ItemGroup.DECORATIONS);
	public static final Item field_8121 = registerBlock(Blocks.field_9983, ItemGroup.DECORATIONS);
	public static final Item field_8129 = registerBlock(Blocks.field_10167, ItemGroup.TRANSPORTATION);
	public static final Item field_8310 = registerBlock(Blocks.field_10596, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8865 = registerBlock(Blocks.field_10363, ItemGroup.REDSTONE);
	public static final Item field_8667 = registerBlock(Blocks.field_10158, ItemGroup.REDSTONE);
	public static final Item field_8391 = registerBlock(Blocks.field_10484, ItemGroup.REDSTONE);
	public static final Item field_8707 = registerBlock(Blocks.field_10332, ItemGroup.REDSTONE);
	public static final Item field_8779 = registerBlock(Blocks.field_10592, ItemGroup.REDSTONE);
	public static final Item field_8047 = registerBlock(Blocks.field_10026, ItemGroup.REDSTONE);
	public static final Item field_8173 = registerBlock(Blocks.field_10397, ItemGroup.REDSTONE);
	public static final Item field_8886 = registerBlock(Blocks.field_10470, ItemGroup.REDSTONE);
	public static final Item field_8604 = registerBlock(Blocks.field_10080, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8530 = register(
		new WallStandingBlockItem(Blocks.field_10523, Blocks.field_10301, new Item.Settings().itemGroup(ItemGroup.REDSTONE))
	);
	public static final Item field_8781 = registerBlock(Blocks.field_10494, ItemGroup.REDSTONE);
	public static final Item field_8749 = registerBlock(Blocks.field_10477, ItemGroup.DECORATIONS);
	public static final Item field_8426 = registerBlock(Blocks.field_10295, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8246 = registerBlock(Blocks.field_10491, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_17520 = registerBlock(Blocks.field_10029, ItemGroup.DECORATIONS);
	public static final Item field_8591 = registerBlock(Blocks.field_10460, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8565 = registerBlock(Blocks.field_10223, ItemGroup.DECORATIONS);
	public static final Item field_8792 = registerBlock(Blocks.field_10620, ItemGroup.DECORATIONS);
	public static final Item field_8701 = registerBlock(Blocks.field_10020, ItemGroup.DECORATIONS);
	public static final Item field_8457 = registerBlock(Blocks.field_10299, ItemGroup.DECORATIONS);
	public static final Item field_8823 = registerBlock(Blocks.field_10319, ItemGroup.DECORATIONS);
	public static final Item field_8646 = registerBlock(Blocks.field_10144, ItemGroup.DECORATIONS);
	public static final Item field_8454 = registerBlock(Blocks.field_10132, ItemGroup.DECORATIONS);
	public static final Item field_17518 = registerBlock(Blocks.field_10261, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_17519 = registerBlock(Blocks.field_10147, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8328 = registerBlock(Blocks.field_10515, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8067 = registerBlock(Blocks.field_10114, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8801 = registerBlock(Blocks.field_10171, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8693 = registerBlock(Blocks.field_10009, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8376 = registerBlock(Blocks.field_10137, ItemGroup.REDSTONE);
	public static final Item field_8495 = registerBlock(Blocks.field_10323, ItemGroup.REDSTONE);
	public static final Item field_8774 = registerBlock(Blocks.field_10486, ItemGroup.REDSTONE);
	public static final Item field_8321 = registerBlock(Blocks.field_10017, ItemGroup.REDSTONE);
	public static final Item field_8190 = registerBlock(Blocks.field_10608, ItemGroup.REDSTONE);
	public static final Item field_8844 = registerBlock(Blocks.field_10246, ItemGroup.REDSTONE);
	public static final Item field_8225 = registerBlock(Blocks.field_10277, ItemGroup.DECORATIONS);
	public static final Item field_8401 = registerBlock(Blocks.field_10492, ItemGroup.DECORATIONS);
	public static final Item field_8541 = registerBlock(Blocks.field_10387, ItemGroup.DECORATIONS);
	public static final Item field_8596 = registerBlock(Blocks.field_10480, ItemGroup.DECORATIONS);
	public static final Item field_8292 = registerBlock(Blocks.field_10100, ItemGroup.DECORATIONS);
	public static final Item field_8148 = registerBlock(Blocks.field_10176, ItemGroup.DECORATIONS);
	public static final Item field_8061 = registerBlock(Blocks.field_10056, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8195 = registerBlock(Blocks.field_10065, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8343 = registerBlock(Blocks.field_10416, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8525 = registerBlock(Blocks.field_10552, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8506 = registerBlock(Blocks.field_10580, ItemGroup.DECORATIONS);
	public static final Item field_8682 = registerBlock(Blocks.field_10240, ItemGroup.DECORATIONS);
	public static final Item field_17521 = registerBlock(Blocks.field_10556, ItemGroup.DECORATIONS);
	public static final Item field_8076 = registerBlock(Blocks.field_10576, ItemGroup.DECORATIONS);
	public static final Item field_8141 = registerBlock(Blocks.field_10285, ItemGroup.DECORATIONS);
	public static final Item field_17522 = registerBlock(Blocks.field_10545, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_17523 = registerBlock(Blocks.field_10597, ItemGroup.DECORATIONS);
	public static final Item field_8874 = registerBlock(Blocks.field_10188, ItemGroup.REDSTONE);
	public static final Item field_8653 = registerBlock(Blocks.field_10291, ItemGroup.REDSTONE);
	public static final Item field_8289 = registerBlock(Blocks.field_10513, ItemGroup.REDSTONE);
	public static final Item field_8097 = registerBlock(Blocks.field_10041, ItemGroup.REDSTONE);
	public static final Item field_8114 = registerBlock(Blocks.field_10457, ItemGroup.REDSTONE);
	public static final Item field_8293 = registerBlock(Blocks.field_10196, ItemGroup.REDSTONE);
	public static final Item field_8663 = registerBlock(Blocks.field_10089, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8481 = registerBlock(Blocks.field_10392, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8610 = registerBlock(Blocks.field_10402, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_17524 = register(new LilyPadItem(Blocks.field_10588, new Item.Settings().itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8796 = registerBlock(Blocks.field_10266, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8080 = registerBlock(Blocks.field_10364, ItemGroup.DECORATIONS);
	public static final Item field_8444 = registerBlock(Blocks.field_10159, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8657 = registerBlock(Blocks.field_10485, ItemGroup.DECORATIONS);
	public static final Item field_8827 = registerBlock(Blocks.field_10398, ItemGroup.DECORATIONS);
	public static final Item field_8518 = registerBlock(Blocks.field_10471, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8314 = registerBlock(Blocks.field_10462, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8840 = register(new BlockItem(Blocks.field_10081, new Item.Settings().rarity(Rarity.field_8904)));
	public static final Item field_8230 = registerBlock(Blocks.field_10524, ItemGroup.REDSTONE);
	public static final Item field_8443 = registerBlock(Blocks.field_10142, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8837 = registerBlock(Blocks.field_10013, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8466 = registerBlock(Blocks.field_10443, ItemGroup.DECORATIONS);
	public static final Item field_8366 = registerBlock(Blocks.field_10348, ItemGroup.REDSTONE);
	public static final Item field_8733 = registerBlock(Blocks.field_10234, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8122 = registerBlock(Blocks.field_10569, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8130 = registerBlock(Blocks.field_10408, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8311 = registerBlock(Blocks.field_10122, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8866 = register(new CommandBlockItem(Blocks.field_10525, new Item.Settings().rarity(Rarity.field_8904)));
	public static final Item field_8668 = register(new BlockItem(Blocks.field_10327, new Item.Settings().itemGroup(ItemGroup.MISC).rarity(Rarity.field_8903)));
	public static final Item field_8392 = registerBlock(Blocks.field_10625, ItemGroup.DECORATIONS);
	public static final Item field_8708 = registerBlock(Blocks.field_9990, ItemGroup.DECORATIONS);
	public static final Item field_8780 = registerBlock(Blocks.field_10057, ItemGroup.REDSTONE);
	public static final Item field_8048 = registerBlock(Blocks.field_10066, ItemGroup.REDSTONE);
	public static final Item field_8174 = registerBlock(Blocks.field_10417, ItemGroup.REDSTONE);
	public static final Item field_8887 = registerBlock(Blocks.field_10553, ItemGroup.REDSTONE);
	public static final Item field_8605 = registerBlock(Blocks.field_10278, ItemGroup.REDSTONE);
	public static final Item field_8531 = registerBlock(Blocks.field_10493, ItemGroup.REDSTONE);
	public static final Item field_8782 = registerBlock(Blocks.field_10535, ItemGroup.DECORATIONS);
	public static final Item field_8750 = registerBlock(Blocks.field_10105, ItemGroup.DECORATIONS);
	public static final Item field_8427 = registerBlock(Blocks.field_10414, ItemGroup.DECORATIONS);
	public static final Item field_8247 = registerBlock(Blocks.field_10380, ItemGroup.REDSTONE);
	public static final Item field_8721 = registerBlock(Blocks.field_10224, ItemGroup.REDSTONE);
	public static final Item field_8592 = registerBlock(Blocks.field_10582, ItemGroup.REDSTONE);
	public static final Item field_8566 = registerBlock(Blocks.field_10429, ItemGroup.REDSTONE);
	public static final Item field_8793 = registerBlock(Blocks.field_10002, ItemGroup.REDSTONE);
	public static final Item field_8702 = registerBlock(Blocks.field_10213, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8239 = registerBlock(Blocks.field_10312, ItemGroup.REDSTONE);
	public static final Item field_8084 = registerBlock(Blocks.field_10044, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8738 = registerBlock(Blocks.field_10153, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8746 = registerBlock(Blocks.field_10437, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8499 = registerBlock(Blocks.field_10451, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8655 = registerBlock(Blocks.field_10546, ItemGroup.TRANSPORTATION);
	public static final Item field_8878 = registerBlock(Blocks.field_10228, ItemGroup.REDSTONE);
	public static final Item field_8156 = registerBlock(Blocks.field_10611, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8043 = registerBlock(Blocks.field_10184, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8783 = registerBlock(Blocks.field_10015, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8717 = registerBlock(Blocks.field_10325, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8385 = registerBlock(Blocks.field_10143, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8672 = registerBlock(Blocks.field_10014, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8853 = registerBlock(Blocks.field_10444, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8304 = registerBlock(Blocks.field_10349, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8133 = registerBlock(Blocks.field_10590, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8821 = registerBlock(Blocks.field_10235, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8715 = registerBlock(Blocks.field_10570, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8455 = registerBlock(Blocks.field_10409, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8467 = registerBlock(Blocks.field_10123, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8798 = registerBlock(Blocks.field_10526, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8353 = registerBlock(Blocks.field_10328, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8181 = registerBlock(Blocks.field_10626, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8077 = method_7989(Blocks.field_10499);
	public static final Item field_8241 = registerBlock(Blocks.field_10453, ItemGroup.REDSTONE);
	public static final Item field_17528 = registerBlock(Blocks.field_10359, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8850 = registerBlock(Blocks.field_10466, ItemGroup.DECORATIONS);
	public static final Item field_8683 = registerBlock(Blocks.field_9977, ItemGroup.DECORATIONS);
	public static final Item field_8384 = registerBlock(Blocks.field_10482, ItemGroup.DECORATIONS);
	public static final Item field_8078 = registerBlock(Blocks.field_10290, ItemGroup.DECORATIONS);
	public static final Item field_8142 = registerBlock(Blocks.field_10512, ItemGroup.DECORATIONS);
	public static final Item field_8253 = registerBlock(Blocks.field_10040, ItemGroup.DECORATIONS);
	public static final Item field_8580 = registerBlock(Blocks.field_10393, ItemGroup.DECORATIONS);
	public static final Item field_8875 = registerBlock(Blocks.field_10591, ItemGroup.DECORATIONS);
	public static final Item field_8654 = registerBlock(Blocks.field_10209, ItemGroup.DECORATIONS);
	public static final Item field_8290 = registerBlock(Blocks.field_10433, ItemGroup.DECORATIONS);
	public static final Item field_8098 = registerBlock(Blocks.field_10510, ItemGroup.DECORATIONS);
	public static final Item field_8115 = registerBlock(Blocks.field_10043, ItemGroup.DECORATIONS);
	public static final Item field_8294 = registerBlock(Blocks.field_10473, ItemGroup.DECORATIONS);
	public static final Item field_8664 = registerBlock(Blocks.field_10338, ItemGroup.DECORATIONS);
	public static final Item field_8482 = registerBlock(Blocks.field_10536, ItemGroup.DECORATIONS);
	public static final Item field_8611 = registerBlock(Blocks.field_10106, ItemGroup.DECORATIONS);
	public static final Item field_8260 = registerBlock(Blocks.field_10415, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8797 = registerBlock(Blocks.field_10381, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8081 = registerBlock(Blocks.field_10225, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8445 = registerBlock(Blocks.field_10256, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8658 = registerBlock(Blocks.field_10616, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8828 = registerBlock(Blocks.field_10030, ItemGroup.DECORATIONS);
	public static final Item field_8519 = registerBlock(Blocks.field_10194, ItemGroup.DECORATIONS);
	public static final Item field_17525 = register(new TallBlockItem(Blocks.field_10583, new Item.Settings().itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_17526 = register(new TallBlockItem(Blocks.field_10378, new Item.Settings().itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_17527 = register(new TallBlockItem(Blocks.field_10430, new Item.Settings().itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_17529 = register(new TallBlockItem(Blocks.field_10003, new Item.Settings().itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8256 = register(new TallBlockItem(Blocks.field_10214, new Item.Settings().itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8561 = register(new TallBlockItem(Blocks.field_10313, new Item.Settings().itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8483 = registerBlock(Blocks.field_10087, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8393 = registerBlock(Blocks.field_10227, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8243 = registerBlock(Blocks.field_10574, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8869 = registerBlock(Blocks.field_10271, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8095 = registerBlock(Blocks.field_10049, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8340 = registerBlock(Blocks.field_10157, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8770 = registerBlock(Blocks.field_10317, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8507 = registerBlock(Blocks.field_10555, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8363 = registerBlock(Blocks.field_9996, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8685 = registerBlock(Blocks.field_10248, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8838 = registerBlock(Blocks.field_10399, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8126 = registerBlock(Blocks.field_10060, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8332 = registerBlock(Blocks.field_10073, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8734 = registerBlock(Blocks.field_10357, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8636 = registerBlock(Blocks.field_10272, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8410 = registerBlock(Blocks.field_9997, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8736 = registerBlock(Blocks.field_9991, ItemGroup.DECORATIONS);
	public static final Item field_8761 = registerBlock(Blocks.field_10496, ItemGroup.DECORATIONS);
	public static final Item field_8119 = registerBlock(Blocks.field_10469, ItemGroup.DECORATIONS);
	public static final Item field_8196 = registerBlock(Blocks.field_10193, ItemGroup.DECORATIONS);
	public static final Item field_8703 = registerBlock(Blocks.field_10578, ItemGroup.DECORATIONS);
	public static final Item field_8581 = registerBlock(Blocks.field_10305, ItemGroup.DECORATIONS);
	public static final Item field_8500 = registerBlock(Blocks.field_10565, ItemGroup.DECORATIONS);
	public static final Item field_8871 = registerBlock(Blocks.field_10077, ItemGroup.DECORATIONS);
	public static final Item field_8240 = registerBlock(Blocks.field_10129, ItemGroup.DECORATIONS);
	public static final Item field_8085 = registerBlock(Blocks.field_10355, ItemGroup.DECORATIONS);
	public static final Item field_8739 = registerBlock(Blocks.field_10152, ItemGroup.DECORATIONS);
	public static final Item field_8747 = registerBlock(Blocks.field_9982, ItemGroup.DECORATIONS);
	public static final Item field_8501 = registerBlock(Blocks.field_10163, ItemGroup.DECORATIONS);
	public static final Item field_8656 = registerBlock(Blocks.field_10419, ItemGroup.DECORATIONS);
	public static final Item field_8879 = registerBlock(Blocks.field_10118, ItemGroup.DECORATIONS);
	public static final Item field_8157 = registerBlock(Blocks.field_10070, ItemGroup.DECORATIONS);
	public static final Item field_8044 = registerBlock(Blocks.field_10135, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8784 = registerBlock(Blocks.field_10006, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8718 = registerBlock(Blocks.field_10297, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8386 = registerBlock(Blocks.field_10350, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8673 = registerBlock(Blocks.field_10190, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8854 = registerBlock(Blocks.field_10130, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8305 = registerBlock(Blocks.field_10174, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8134 = registerBlock(Blocks.field_10344, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8822 = registerBlock(Blocks.field_10117, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8716 = registerBlock(Blocks.field_10518, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8456 = registerBlock(Blocks.field_10420, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8468 = register(new CommandBlockItem(Blocks.field_10263, new Item.Settings().rarity(Rarity.field_8904)));
	public static final Item field_8799 = register(new CommandBlockItem(Blocks.field_10395, new Item.Settings().rarity(Rarity.field_8904)));
	public static final Item field_8354 = registerBlock(Blocks.field_10092, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8182 = registerBlock(Blocks.field_10541, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8079 = registerBlock(Blocks.field_9986, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8242 = registerBlock(Blocks.field_10166, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8615 = method_7989(Blocks.field_10369);
	public static final Item field_8537 = registerBlock(Blocks.field_10282, ItemGroup.REDSTONE);
	public static final Item field_8545 = register(new BlockItem(Blocks.field_10603, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8722 = register(new BlockItem(Blocks.field_10199, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8380 = register(new BlockItem(Blocks.field_10407, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8050 = register(new BlockItem(Blocks.field_10063, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8829 = register(new BlockItem(Blocks.field_10203, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8271 = register(new BlockItem(Blocks.field_10600, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8548 = register(new BlockItem(Blocks.field_10275, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8520 = register(new BlockItem(Blocks.field_10051, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8627 = register(new BlockItem(Blocks.field_10140, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8451 = register(new BlockItem(Blocks.field_10320, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8213 = register(new BlockItem(Blocks.field_10532, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8816 = register(new BlockItem(Blocks.field_10268, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8350 = register(new BlockItem(Blocks.field_10605, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8584 = register(new BlockItem(Blocks.field_10373, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8461 = register(new BlockItem(Blocks.field_10055, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8676 = register(new BlockItem(Blocks.field_10068, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8268 = register(new BlockItem(Blocks.field_10371, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8177 = registerBlock(Blocks.field_10595, ItemGroup.DECORATIONS);
	public static final Item field_8139 = registerBlock(Blocks.field_10280, ItemGroup.DECORATIONS);
	public static final Item field_8318 = registerBlock(Blocks.field_10538, ItemGroup.DECORATIONS);
	public static final Item field_8640 = registerBlock(Blocks.field_10345, ItemGroup.DECORATIONS);
	public static final Item field_8889 = registerBlock(Blocks.field_10096, ItemGroup.DECORATIONS);
	public static final Item field_8649 = registerBlock(Blocks.field_10046, ItemGroup.DECORATIONS);
	public static final Item field_8277 = registerBlock(Blocks.field_10567, ItemGroup.DECORATIONS);
	public static final Item field_8885 = registerBlock(Blocks.field_10220, ItemGroup.DECORATIONS);
	public static final Item field_8172 = registerBlock(Blocks.field_10052, ItemGroup.DECORATIONS);
	public static final Item field_8257 = registerBlock(Blocks.field_10078, ItemGroup.DECORATIONS);
	public static final Item field_8562 = registerBlock(Blocks.field_10426, ItemGroup.DECORATIONS);
	public static final Item field_8484 = registerBlock(Blocks.field_10550, ItemGroup.DECORATIONS);
	public static final Item field_8394 = registerBlock(Blocks.field_10004, ItemGroup.DECORATIONS);
	public static final Item field_8244 = registerBlock(Blocks.field_10475, ItemGroup.DECORATIONS);
	public static final Item field_8870 = registerBlock(Blocks.field_10383, ItemGroup.DECORATIONS);
	public static final Item field_8096 = registerBlock(Blocks.field_10501, ItemGroup.DECORATIONS);
	public static final Item field_8341 = registerBlock(Blocks.field_10107, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8771 = registerBlock(Blocks.field_10210, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8508 = registerBlock(Blocks.field_10585, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8364 = registerBlock(Blocks.field_10242, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8686 = registerBlock(Blocks.field_10542, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8839 = registerBlock(Blocks.field_10421, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8127 = registerBlock(Blocks.field_10434, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8333 = registerBlock(Blocks.field_10038, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8735 = registerBlock(Blocks.field_10172, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8637 = registerBlock(Blocks.field_10308, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8411 = registerBlock(Blocks.field_10206, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8737 = registerBlock(Blocks.field_10011, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8762 = registerBlock(Blocks.field_10439, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8120 = registerBlock(Blocks.field_10367, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8197 = registerBlock(Blocks.field_10058, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8704 = registerBlock(Blocks.field_10458, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8582 = registerBlock(Blocks.field_10197, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8487 = registerBlock(Blocks.field_10022, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8336 = registerBlock(Blocks.field_10300, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8764 = registerBlock(Blocks.field_10321, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8205 = registerBlock(Blocks.field_10145, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8418 = registerBlock(Blocks.field_10133, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8222 = registerBlock(Blocks.field_10522, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8818 = registerBlock(Blocks.field_10353, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8558 = registerBlock(Blocks.field_10628, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8593 = registerBlock(Blocks.field_10233, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8690 = registerBlock(Blocks.field_10404, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8164 = registerBlock(Blocks.field_10456, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8437 = registerBlock(Blocks.field_10023, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8198 = registerBlock(Blocks.field_10529, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8757 = registerBlock(Blocks.field_10287, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8516 = registerBlock(Blocks.field_10506, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8618 = registerBlock(Blocks.field_10195, ItemGroup.MISC);
	public static final Item field_8856 = registerBlock(Blocks.field_10614, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8237 = registerBlock(Blocks.field_10264, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8089 = registerBlock(Blocks.field_10396, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8160 = registerBlock(Blocks.field_10111, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8698 = registerBlock(Blocks.field_10488, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8402 = registerBlock(Blocks.field_10309, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8474 = registerBlock(Blocks.field_10629, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8883 = registerBlock(Blocks.field_10000, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8278 = registerBlock(Blocks.field_10516, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8104 = registerBlock(Blocks.field_10464, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8847 = registerBlock(Blocks.field_10125, ItemGroup.DECORATIONS);
	public static final Item field_8616 = registerBlock(Blocks.field_10339, ItemGroup.DECORATIONS);
	public static final Item field_8538 = registerBlock(Blocks.field_10134, ItemGroup.DECORATIONS);
	public static final Item field_8546 = registerBlock(Blocks.field_10618, ItemGroup.DECORATIONS);
	public static final Item field_8723 = registerBlock(Blocks.field_10169, ItemGroup.DECORATIONS);
	public static final Item field_8381 = registerBlock(Blocks.field_10572, ItemGroup.DECORATIONS);
	public static final Item field_8051 = registerBlock(Blocks.field_10296, ItemGroup.DECORATIONS);
	public static final Item field_8830 = registerBlock(Blocks.field_10579, ItemGroup.DECORATIONS);
	public static final Item field_8272 = registerBlock(Blocks.field_10032, ItemGroup.DECORATIONS);
	public static final Item field_8549 = registerBlock(Blocks.field_10082, ItemGroup.DECORATIONS);
	public static final Item field_8521 = register(
		new WallStandingBlockItem(Blocks.field_10053, Blocks.field_10584, new Item.Settings().itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8628 = register(
		new WallStandingBlockItem(Blocks.field_10079, Blocks.field_10186, new Item.Settings().itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8452 = register(
		new WallStandingBlockItem(Blocks.field_10427, Blocks.field_10447, new Item.Settings().itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8214 = register(
		new WallStandingBlockItem(Blocks.field_10551, Blocks.field_10498, new Item.Settings().itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8817 = register(
		new WallStandingBlockItem(Blocks.field_10005, Blocks.field_9976, new Item.Settings().itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8351 = register(
		new WallStandingBlockItem(Blocks.field_10448, Blocks.field_10347, new Item.Settings().itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8585 = register(
		new WallStandingBlockItem(Blocks.field_10097, Blocks.field_10116, new Item.Settings().itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8462 = register(
		new WallStandingBlockItem(Blocks.field_10047, Blocks.field_10094, new Item.Settings().itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8677 = register(
		new WallStandingBlockItem(Blocks.field_10568, Blocks.field_10557, new Item.Settings().itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8269 = register(
		new WallStandingBlockItem(Blocks.field_10221, Blocks.field_10239, new Item.Settings().itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8178 = registerBlock(Blocks.field_10384, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8140 = register(new BlockItem(Blocks.field_10502, new Item.Settings().itemGroup(ItemGroup.MISC).rarity(Rarity.field_8903)));
	public static final Item field_8319 = registerBlock(Blocks.field_10435, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8641 = registerBlock(Blocks.field_10039, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8890 = registerBlock(Blocks.field_10173, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8650 = registerBlock(Blocks.field_10310, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8275 = registerBlock(Blocks.field_10207, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8152 = registerBlock(Blocks.field_10012, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8053 = registerBlock(Blocks.field_10440, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8166 = registerBlock(Blocks.field_10549, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8430 = registerBlock(Blocks.field_10245, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8608 = registerBlock(Blocks.field_10607, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8526 = registerBlock(Blocks.field_10386, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8302 = registerBlock(Blocks.field_10497, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8316 = registerBlock(Blocks.field_9994, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8860 = registerBlock(Blocks.field_10216, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8228 = registerBlock(Blocks.field_10329, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8266 = registerBlock(Blocks.field_10283, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8576 = registerBlock(Blocks.field_10024, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8569 = registerBlock(Blocks.field_10412, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8369 = registerBlock(Blocks.field_10405, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8282 = registerBlock(Blocks.field_10064, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8872 = registerBlock(Blocks.field_10262, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8217 = registerBlock(Blocks.field_10601, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8312 = registerBlock(Blocks.field_10189, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8742 = registerBlock(Blocks.field_10016, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8522 = registerBlock(Blocks.field_10478, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8395 = registerBlock(Blocks.field_10322, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8659 = registerBlock(Blocks.field_10507, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8804 = registerBlock(Blocks.field_10269, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8057 = registerBlock(Blocks.field_10530, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8359 = registerBlock(Blocks.field_10413, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8811 = registerBlock(Blocks.field_10059, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8488 = registerBlock(Blocks.field_10072, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8337 = registerBlock(Blocks.field_10252, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8765 = registerBlock(Blocks.field_10127, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8206 = registerBlock(Blocks.field_10489, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8419 = registerBlock(Blocks.field_10311, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8223 = registerBlock(Blocks.field_10630, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8819 = registerBlock(Blocks.field_10001, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8559 = registerBlock(Blocks.field_10517, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_16482 = register(new ScaffoldingItem(Blocks.field_16492, new Item.Settings().itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8594 = register(new TallBlockItem(Blocks.field_9973, new Item.Settings().itemGroup(ItemGroup.REDSTONE)));
	public static final Item field_8691 = register(new TallBlockItem(Blocks.field_10149, new Item.Settings().itemGroup(ItemGroup.REDSTONE)));
	public static final Item field_8165 = register(new TallBlockItem(Blocks.field_10521, new Item.Settings().itemGroup(ItemGroup.REDSTONE)));
	public static final Item field_8438 = register(new TallBlockItem(Blocks.field_10352, new Item.Settings().itemGroup(ItemGroup.REDSTONE)));
	public static final Item field_8199 = register(new TallBlockItem(Blocks.field_10627, new Item.Settings().itemGroup(ItemGroup.REDSTONE)));
	public static final Item field_8758 = register(new TallBlockItem(Blocks.field_10232, new Item.Settings().itemGroup(ItemGroup.REDSTONE)));
	public static final Item field_8517 = register(new TallBlockItem(Blocks.field_10403, new Item.Settings().itemGroup(ItemGroup.REDSTONE)));
	public static final Item field_8619 = registerBlock(Blocks.field_10450, ItemGroup.REDSTONE);
	public static final Item field_8857 = registerBlock(Blocks.field_10377, ItemGroup.REDSTONE);
	public static final Item field_8238 = register(new CommandBlockItem(Blocks.field_10465, new Item.Settings().rarity(Rarity.field_8904)));
	public static final Item field_16538 = register(new CommandBlockItem(Blocks.field_16540, new Item.Settings().rarity(Rarity.field_8904)));
	public static final Item field_17530 = registerBlock(Blocks.field_17563, ItemGroup.MISC);
	public static final Item field_8090 = register(
		"turtle_helmet", new ArmorItem(ArmorMaterials.field_7890, EquipmentSlot.HEAD, new Item.Settings().itemGroup(ItemGroup.COMBAT))
	);
	public static final Item field_8161 = register("scute", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8699 = register(
		"iron_shovel", new ShovelItem(ToolMaterials.field_8923, 1.5F, -3.0F, new Item.Settings().itemGroup(ItemGroup.TOOLS))
	);
	public static final Item field_8403 = register(
		"iron_pickaxe", new PickaxeItem(ToolMaterials.field_8923, 1, -2.8F, new Item.Settings().itemGroup(ItemGroup.TOOLS))
	);
	public static final Item field_8475 = register("iron_axe", new AxeItem(ToolMaterials.field_8923, 6.0F, -3.1F, new Item.Settings().itemGroup(ItemGroup.TOOLS)));
	public static final Item field_8884 = register("flint_and_steel", new FlintAndSteelItem(new Item.Settings().durability(64).itemGroup(ItemGroup.TOOLS)));
	public static final Item field_8279 = register("apple", new FoodItem(4, 0.3F, false, new Item.Settings().itemGroup(ItemGroup.FOOD)));
	public static final Item field_8102 = register("bow", new BowItem(new Item.Settings().durability(384).itemGroup(ItemGroup.COMBAT)));
	public static final Item field_8107 = register("arrow", new ArrowItem(new Item.Settings().itemGroup(ItemGroup.COMBAT)));
	public static final Item field_8713 = register("coal", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8665 = register("charcoal", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8477 = register("diamond", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8620 = register("iron_ingot", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8695 = register("gold_ingot", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8371 = register(
		"iron_sword", new SwordItem(ToolMaterials.field_8923, 3, -2.4F, new Item.Settings().itemGroup(ItemGroup.COMBAT))
	);
	public static final Item field_8091 = register(
		"wooden_sword", new SwordItem(ToolMaterials.field_8922, 3, -2.4F, new Item.Settings().itemGroup(ItemGroup.COMBAT))
	);
	public static final Item field_8876 = register(
		"wooden_shovel", new ShovelItem(ToolMaterials.field_8922, 1.5F, -3.0F, new Item.Settings().itemGroup(ItemGroup.TOOLS))
	);
	public static final Item field_8647 = register(
		"wooden_pickaxe", new PickaxeItem(ToolMaterials.field_8922, 1, -2.8F, new Item.Settings().itemGroup(ItemGroup.TOOLS))
	);
	public static final Item field_8406 = register(
		"wooden_axe", new AxeItem(ToolMaterials.field_8922, 6.0F, -3.2F, new Item.Settings().itemGroup(ItemGroup.TOOLS))
	);
	public static final Item field_8528 = register(
		"stone_sword", new SwordItem(ToolMaterials.field_8927, 3, -2.4F, new Item.Settings().itemGroup(ItemGroup.COMBAT))
	);
	public static final Item field_8776 = register(
		"stone_shovel", new ShovelItem(ToolMaterials.field_8927, 1.5F, -3.0F, new Item.Settings().itemGroup(ItemGroup.TOOLS))
	);
	public static final Item field_8387 = register(
		"stone_pickaxe", new PickaxeItem(ToolMaterials.field_8927, 1, -2.8F, new Item.Settings().itemGroup(ItemGroup.TOOLS))
	);
	public static final Item field_8062 = register("stone_axe", new AxeItem(ToolMaterials.field_8927, 7.0F, -3.2F, new Item.Settings().itemGroup(ItemGroup.TOOLS)));
	public static final Item field_8802 = register(
		"diamond_sword", new SwordItem(ToolMaterials.field_8930, 3, -2.4F, new Item.Settings().itemGroup(ItemGroup.COMBAT))
	);
	public static final Item field_8250 = register(
		"diamond_shovel", new ShovelItem(ToolMaterials.field_8930, 1.5F, -3.0F, new Item.Settings().itemGroup(ItemGroup.TOOLS))
	);
	public static final Item field_8377 = register(
		"diamond_pickaxe", new PickaxeItem(ToolMaterials.field_8930, 1, -2.8F, new Item.Settings().itemGroup(ItemGroup.TOOLS))
	);
	public static final Item field_8556 = register(
		"diamond_axe", new AxeItem(ToolMaterials.field_8930, 5.0F, -3.0F, new Item.Settings().itemGroup(ItemGroup.TOOLS))
	);
	public static final Item field_8600 = register("stick", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8428 = register("bowl", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8208 = register("mushroom_stew", new MushroomStewItem(6, new Item.Settings().stackSize(1).itemGroup(ItemGroup.FOOD)));
	public static final Item field_8845 = register(
		"golden_sword", new SwordItem(ToolMaterials.field_8929, 3, -2.4F, new Item.Settings().itemGroup(ItemGroup.COMBAT))
	);
	public static final Item field_8322 = register(
		"golden_shovel", new ShovelItem(ToolMaterials.field_8929, 1.5F, -3.0F, new Item.Settings().itemGroup(ItemGroup.TOOLS))
	);
	public static final Item field_8335 = register(
		"golden_pickaxe", new PickaxeItem(ToolMaterials.field_8929, 1, -2.8F, new Item.Settings().itemGroup(ItemGroup.TOOLS))
	);
	public static final Item field_8825 = register(
		"golden_axe", new AxeItem(ToolMaterials.field_8929, 6.0F, -3.0F, new Item.Settings().itemGroup(ItemGroup.TOOLS))
	);
	public static final Item field_8276 = register("string", new StringItem(Blocks.field_10589, new Item.Settings().itemGroup(ItemGroup.MISC)));
	public static final Item field_8153 = register("feather", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8054 = register("gunpowder", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8167 = register("wooden_hoe", new HoeItem(ToolMaterials.field_8922, -3.0F, new Item.Settings().itemGroup(ItemGroup.TOOLS)));
	public static final Item field_8431 = register("stone_hoe", new HoeItem(ToolMaterials.field_8927, -2.0F, new Item.Settings().itemGroup(ItemGroup.TOOLS)));
	public static final Item field_8609 = register("iron_hoe", new HoeItem(ToolMaterials.field_8923, -1.0F, new Item.Settings().itemGroup(ItemGroup.TOOLS)));
	public static final Item field_8527 = register("diamond_hoe", new HoeItem(ToolMaterials.field_8930, 0.0F, new Item.Settings().itemGroup(ItemGroup.TOOLS)));
	public static final Item field_8303 = register("golden_hoe", new HoeItem(ToolMaterials.field_8929, -3.0F, new Item.Settings().itemGroup(ItemGroup.TOOLS)));
	public static final Item field_8317 = register("wheat_seeds", new SeedsItem(Blocks.field_10293, new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8861 = register("wheat", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8229 = register("bread", new FoodItem(5, 0.6F, false, new Item.Settings().itemGroup(ItemGroup.FOOD)));
	public static final Item field_8267 = register(
		"leather_helmet", new DyeableArmorItem(ArmorMaterials.field_7897, EquipmentSlot.HEAD, new Item.Settings().itemGroup(ItemGroup.COMBAT))
	);
	public static final Item field_8577 = register(
		"leather_chestplate", new DyeableArmorItem(ArmorMaterials.field_7897, EquipmentSlot.CHEST, new Item.Settings().itemGroup(ItemGroup.COMBAT))
	);
	public static final Item field_8570 = register(
		"leather_leggings", new DyeableArmorItem(ArmorMaterials.field_7897, EquipmentSlot.LEGS, new Item.Settings().itemGroup(ItemGroup.COMBAT))
	);
	public static final Item field_8370 = register(
		"leather_boots", new DyeableArmorItem(ArmorMaterials.field_7897, EquipmentSlot.FEET, new Item.Settings().itemGroup(ItemGroup.COMBAT))
	);
	public static final Item field_8283 = register(
		"chainmail_helmet", new ArmorItem(ArmorMaterials.field_7887, EquipmentSlot.HEAD, new Item.Settings().itemGroup(ItemGroup.COMBAT))
	);
	public static final Item field_8873 = register(
		"chainmail_chestplate", new ArmorItem(ArmorMaterials.field_7887, EquipmentSlot.CHEST, new Item.Settings().itemGroup(ItemGroup.COMBAT))
	);
	public static final Item field_8218 = register(
		"chainmail_leggings", new ArmorItem(ArmorMaterials.field_7887, EquipmentSlot.LEGS, new Item.Settings().itemGroup(ItemGroup.COMBAT))
	);
	public static final Item field_8313 = register(
		"chainmail_boots", new ArmorItem(ArmorMaterials.field_7887, EquipmentSlot.FEET, new Item.Settings().itemGroup(ItemGroup.COMBAT))
	);
	public static final Item field_8743 = register(
		"iron_helmet", new ArmorItem(ArmorMaterials.field_7892, EquipmentSlot.HEAD, new Item.Settings().itemGroup(ItemGroup.COMBAT))
	);
	public static final Item field_8523 = register(
		"iron_chestplate", new ArmorItem(ArmorMaterials.field_7892, EquipmentSlot.CHEST, new Item.Settings().itemGroup(ItemGroup.COMBAT))
	);
	public static final Item field_8396 = register(
		"iron_leggings", new ArmorItem(ArmorMaterials.field_7892, EquipmentSlot.LEGS, new Item.Settings().itemGroup(ItemGroup.COMBAT))
	);
	public static final Item field_8660 = register(
		"iron_boots", new ArmorItem(ArmorMaterials.field_7892, EquipmentSlot.FEET, new Item.Settings().itemGroup(ItemGroup.COMBAT))
	);
	public static final Item field_8805 = register(
		"diamond_helmet", new ArmorItem(ArmorMaterials.field_7889, EquipmentSlot.HEAD, new Item.Settings().itemGroup(ItemGroup.COMBAT))
	);
	public static final Item field_8058 = register(
		"diamond_chestplate", new ArmorItem(ArmorMaterials.field_7889, EquipmentSlot.CHEST, new Item.Settings().itemGroup(ItemGroup.COMBAT))
	);
	public static final Item field_8348 = register(
		"diamond_leggings", new ArmorItem(ArmorMaterials.field_7889, EquipmentSlot.LEGS, new Item.Settings().itemGroup(ItemGroup.COMBAT))
	);
	public static final Item field_8285 = register(
		"diamond_boots", new ArmorItem(ArmorMaterials.field_7889, EquipmentSlot.FEET, new Item.Settings().itemGroup(ItemGroup.COMBAT))
	);
	public static final Item field_8862 = register(
		"golden_helmet", new ArmorItem(ArmorMaterials.field_7895, EquipmentSlot.HEAD, new Item.Settings().itemGroup(ItemGroup.COMBAT))
	);
	public static final Item field_8678 = register(
		"golden_chestplate", new ArmorItem(ArmorMaterials.field_7895, EquipmentSlot.CHEST, new Item.Settings().itemGroup(ItemGroup.COMBAT))
	);
	public static final Item field_8416 = register(
		"golden_leggings", new ArmorItem(ArmorMaterials.field_7895, EquipmentSlot.LEGS, new Item.Settings().itemGroup(ItemGroup.COMBAT))
	);
	public static final Item field_8753 = register(
		"golden_boots", new ArmorItem(ArmorMaterials.field_7895, EquipmentSlot.FEET, new Item.Settings().itemGroup(ItemGroup.COMBAT))
	);
	public static final Item field_8145 = register("flint", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8389 = register("porkchop", new FoodItem(3, 0.3F, true, new Item.Settings().itemGroup(ItemGroup.FOOD)));
	public static final Item field_8261 = register("cooked_porkchop", new FoodItem(8, 0.8F, true, new Item.Settings().itemGroup(ItemGroup.FOOD)));
	public static final Item field_8892 = register("painting", new DecorationItem(EntityType.PAINTING, new Item.Settings().itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8463 = register(
		"golden_apple", new GoldenAppleItem(4, 1.2F, false, new Item.Settings().itemGroup(ItemGroup.FOOD).rarity(Rarity.field_8903)).setAlwaysConsumable()
	);
	public static final Item field_8367 = register(
		"enchanted_golden_apple",
		new EnchantedGoldenAppleItem(4, 1.2F, false, new Item.Settings().itemGroup(ItemGroup.FOOD).rarity(Rarity.field_8904)).setAlwaysConsumable()
	);
	public static final Item field_8788 = register(
		"oak_sign", new SignItem(new Item.Settings().stackSize(16).itemGroup(ItemGroup.DECORATIONS), Blocks.field_10121, Blocks.field_10187)
	);
	public static final Item field_8111 = register(
		"spruce_sign", new SignItem(new Item.Settings().stackSize(16).itemGroup(ItemGroup.DECORATIONS), Blocks.field_10411, Blocks.field_10088)
	);
	public static final Item field_8422 = register(
		"birch_sign", new SignItem(new Item.Settings().stackSize(16).itemGroup(ItemGroup.DECORATIONS), Blocks.field_10231, Blocks.field_10391)
	);
	public static final Item field_8203 = register(
		"acacia_sign", new SignItem(new Item.Settings().stackSize(16).itemGroup(ItemGroup.DECORATIONS), Blocks.field_10284, Blocks.field_10401)
	);
	public static final Item field_8867 = register(
		"jungle_sign", new SignItem(new Item.Settings().stackSize(16).itemGroup(ItemGroup.DECORATIONS), Blocks.field_10544, Blocks.field_10587)
	);
	public static final Item field_8496 = register(
		"dark_oak_sign", new SignItem(new Item.Settings().stackSize(16).itemGroup(ItemGroup.DECORATIONS), Blocks.field_10330, Blocks.field_10265)
	);
	public static final Item field_8550 = register("bucket", new BucketItem(Fluids.EMPTY, new Item.Settings().stackSize(16).itemGroup(ItemGroup.MISC)));
	public static final Item field_8705 = register(
		"water_bucket", new BucketItem(Fluids.WATER, new Item.Settings().recipeRemainder(field_8550).stackSize(1).itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8187 = register(
		"lava_bucket", new BucketItem(Fluids.LAVA, new Item.Settings().recipeRemainder(field_8550).stackSize(1).itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8045 = register(
		"minecart", new MinecartItem(AbstractMinecartEntity.Type.field_7674, new Item.Settings().stackSize(1).itemGroup(ItemGroup.TRANSPORTATION))
	);
	public static final Item field_8175 = register("saddle", new SaddleItem(new Item.Settings().stackSize(1).itemGroup(ItemGroup.TRANSPORTATION)));
	public static final Item field_8725 = register("redstone", new StringItem(Blocks.field_10091, new Item.Settings().itemGroup(ItemGroup.REDSTONE)));
	public static final Item field_8543 = register("snowball", new SnowballItem(new Item.Settings().stackSize(16).itemGroup(ItemGroup.MISC)));
	public static final Item field_8533 = register(
		"oak_boat", new BoatItem(BoatEntity.Type.OAK, new Item.Settings().stackSize(1).itemGroup(ItemGroup.TRANSPORTATION))
	);
	public static final Item field_8745 = register("leather", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8103 = register(
		"milk_bucket", new BucketMilkItem(new Item.Settings().recipeRemainder(field_8550).stackSize(1).itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8108 = register(
		"pufferfish_bucket", new FishBucketItem(EntityType.PUFFERFISH, Fluids.WATER, new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8714 = register(
		"salmon_bucket", new FishBucketItem(EntityType.SALMON, Fluids.WATER, new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8666 = register(
		"cod_bucket", new FishBucketItem(EntityType.COD, Fluids.WATER, new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8478 = register(
		"tropical_fish_bucket", new FishBucketItem(EntityType.TROPICAL_FISH, Fluids.WATER, new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8621 = register("brick", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8696 = register("clay_ball", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_17531 = registerBlock(Blocks.field_10424, ItemGroup.MISC);
	public static final Item field_17532 = registerBlock(Blocks.field_9993, ItemGroup.MISC);
	public static final Item field_17533 = registerBlock(Blocks.field_10342, ItemGroup.BUILDING_BLOCKS);
	public static final Item field_8648 = registerBlock(Blocks.field_10211, ItemGroup.MATERIALS);
	public static final Item field_8407 = register("paper", new Item(new Item.Settings().itemGroup(ItemGroup.MISC)));
	public static final Item field_8529 = register("book", new BookItem(new Item.Settings().itemGroup(ItemGroup.MISC)));
	public static final Item field_8777 = register("slime_ball", new Item(new Item.Settings().itemGroup(ItemGroup.MISC)));
	public static final Item field_8388 = register(
		"chest_minecart", new MinecartItem(AbstractMinecartEntity.Type.field_7678, new Item.Settings().stackSize(1).itemGroup(ItemGroup.TRANSPORTATION))
	);
	public static final Item field_8063 = register(
		"furnace_minecart", new MinecartItem(AbstractMinecartEntity.Type.field_7679, new Item.Settings().stackSize(1).itemGroup(ItemGroup.TRANSPORTATION))
	);
	public static final Item field_8803 = register("egg", new EggItem(new Item.Settings().stackSize(16).itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8251 = register("compass", new CompassItem(new Item.Settings().itemGroup(ItemGroup.TOOLS)));
	public static final Item field_8378 = register("fishing_rod", new FishingRodItem(new Item.Settings().durability(64).itemGroup(ItemGroup.TOOLS)));
	public static final Item field_8557 = register("clock", new ClockItem(new Item.Settings().itemGroup(ItemGroup.TOOLS)));
	public static final Item field_8601 = register("glowstone_dust", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8429 = register("cod", new FishItem(FishItem.Type.COD, false, new Item.Settings().itemGroup(ItemGroup.FOOD)));
	public static final Item field_8209 = register("salmon", new FishItem(FishItem.Type.SALMON, false, new Item.Settings().itemGroup(ItemGroup.FOOD)));
	public static final Item field_8846 = register(
		"tropical_fish", new FishItem(FishItem.Type.TROPICAL_FISH, false, new Item.Settings().itemGroup(ItemGroup.FOOD))
	);
	public static final Item field_8323 = register("pufferfish", new FishItem(FishItem.Type.PUFFERFISH, false, new Item.Settings().itemGroup(ItemGroup.FOOD)));
	public static final Item field_8373 = register("cooked_cod", new FishItem(FishItem.Type.COD, true, new Item.Settings().itemGroup(ItemGroup.FOOD)));
	public static final Item field_8509 = register("cooked_salmon", new FishItem(FishItem.Type.SALMON, true, new Item.Settings().itemGroup(ItemGroup.FOOD)));
	public static final Item field_8794 = register("ink_sac", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8264 = register("red_dye", new DyeItem(DyeColor.field_7964, new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8408 = register("green_dye", new DyeItem(DyeColor.field_7942, new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8116 = register("cocoa_beans", new BlockItem(Blocks.field_10302, new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8759 = register("lapis_lazuli", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8296 = register("purple_dye", new DyeItem(DyeColor.field_7945, new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8632 = register("cyan_dye", new DyeItem(DyeColor.field_7955, new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8851 = register("light_gray_dye", new DyeItem(DyeColor.field_7967, new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8298 = register("gray_dye", new DyeItem(DyeColor.field_7944, new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8330 = register("pink_dye", new DyeItem(DyeColor.field_7954, new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8131 = register("lime_dye", new DyeItem(DyeColor.field_7961, new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8192 = register("yellow_dye", new DyeItem(DyeColor.field_7947, new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8273 = register("light_blue_dye", new DyeItem(DyeColor.field_7951, new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8669 = register("magenta_dye", new DyeItem(DyeColor.field_7958, new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8492 = register("orange_dye", new DyeItem(DyeColor.field_7946, new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8324 = register("bone_meal", new BoneMealItem(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8345 = register("blue_dye", new DyeItem(DyeColor.field_7966, new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8099 = register("brown_dye", new DyeItem(DyeColor.field_7957, new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8226 = register("black_dye", new DyeItem(DyeColor.BLACK, new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8446 = register("white_dye", new DyeItem(DyeColor.field_7952, new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8606 = register("bone", new Item(new Item.Settings().itemGroup(ItemGroup.MISC)));
	public static final Item field_8479 = register("sugar", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_17534 = register(new BlockItem(Blocks.field_10183, new Item.Settings().stackSize(1).itemGroup(ItemGroup.FOOD)));
	public static final Item field_8258 = register(new BedItem(Blocks.field_10120, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8059 = register(new BedItem(Blocks.field_10410, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8349 = register(new BedItem(Blocks.field_10230, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8286 = register(new BedItem(Blocks.field_10621, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8863 = register(new BedItem(Blocks.field_10356, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8679 = register(new BedItem(Blocks.field_10180, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8417 = register(new BedItem(Blocks.field_10610, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8754 = register(new BedItem(Blocks.field_10141, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8146 = register(new BedItem(Blocks.field_10326, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8390 = register(new BedItem(Blocks.field_10109, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8262 = register(new BedItem(Blocks.field_10019, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8893 = register(new BedItem(Blocks.field_10527, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8464 = register(new BedItem(Blocks.field_10288, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8368 = register(new BedItem(Blocks.field_10561, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8789 = register(new BedItem(Blocks.field_10069, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8112 = register(new BedItem(Blocks.field_10461, new Item.Settings().stackSize(1).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8423 = register("cookie", new FoodItem(2, 0.1F, false, new Item.Settings().itemGroup(ItemGroup.FOOD)));
	public static final Item field_8204 = register("filled_map", new FilledMapItem(new Item.Settings()));
	public static final Item field_8868 = register("shears", new ShearsItem(new Item.Settings().durability(238).itemGroup(ItemGroup.TOOLS)));
	public static final Item field_8497 = register("melon_slice", new FoodItem(2, 0.3F, false, new Item.Settings().itemGroup(ItemGroup.FOOD)));
	public static final Item field_8551 = register("dried_kelp", new FoodItem(1, 0.3F, false, new Item.Settings().itemGroup(ItemGroup.FOOD)).setConsumeQuickly());
	public static final Item field_8706 = register("pumpkin_seeds", new SeedsItem(Blocks.field_9984, new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8188 = register("melon_seeds", new SeedsItem(Blocks.field_10168, new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8046 = register("beef", new FoodItem(3, 0.3F, true, new Item.Settings().itemGroup(ItemGroup.FOOD)));
	public static final Item field_8176 = register("cooked_beef", new FoodItem(8, 0.8F, true, new Item.Settings().itemGroup(ItemGroup.FOOD)));
	public static final Item field_8726 = register(
		"chicken",
		new FoodItem(2, 0.3F, true, new Item.Settings().itemGroup(ItemGroup.FOOD)).setStatusEffect(new StatusEffectInstance(StatusEffects.field_5903, 600, 0), 0.3F)
	);
	public static final Item field_8544 = register("cooked_chicken", new FoodItem(6, 0.6F, true, new Item.Settings().itemGroup(ItemGroup.FOOD)));
	public static final Item field_8511 = register(
		"rotten_flesh",
		new FoodItem(4, 0.1F, true, new Item.Settings().itemGroup(ItemGroup.FOOD)).setStatusEffect(new StatusEffectInstance(StatusEffects.field_5903, 600, 0), 0.8F)
	);
	public static final Item field_8634 = register("ender_pearl", new EnderPearlItem(new Item.Settings().stackSize(16).itemGroup(ItemGroup.MISC)));
	public static final Item field_8894 = register("blaze_rod", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8070 = register("ghast_tear", new Item(new Item.Settings().itemGroup(ItemGroup.BREWING)));
	public static final Item field_8397 = register("gold_nugget", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8790 = register(
		"nether_wart", new SeedsItem(Blocks.field_9974, SoundEvents.field_17613, new Item.Settings().itemGroup(ItemGroup.MATERIALS))
	);
	public static final Item field_8574 = register("potion", new PotionItem(new Item.Settings().stackSize(1).itemGroup(ItemGroup.BREWING)));
	public static final Item field_8469 = register("glass_bottle", new GlassBottleItem(new Item.Settings().itemGroup(ItemGroup.BREWING)));
	public static final Item field_8680 = register(
		"spider_eye",
		new FoodItem(2, 0.8F, false, new Item.Settings().itemGroup(ItemGroup.FOOD)).setStatusEffect(new StatusEffectInstance(StatusEffects.field_5899, 100, 0), 1.0F)
	);
	public static final Item field_8711 = register("fermented_spider_eye", new Item(new Item.Settings().itemGroup(ItemGroup.BREWING)));
	public static final Item field_8183 = register("blaze_powder", new Item(new Item.Settings().itemGroup(ItemGroup.BREWING)));
	public static final Item field_8135 = register("magma_cream", new Item(new Item.Settings().itemGroup(ItemGroup.BREWING)));
	public static final Item field_8740 = registerBlock(Blocks.field_10333, ItemGroup.BREWING);
	public static final Item field_8638 = registerBlock(Blocks.field_10593, ItemGroup.BREWING);
	public static final Item field_8449 = register("ender_eye", new EnderEyeItem(new Item.Settings().itemGroup(ItemGroup.MISC)));
	public static final Item field_8597 = register("glistering_melon_slice", new Item(new Item.Settings().itemGroup(ItemGroup.BREWING)));
	public static final Item field_8727 = register(
		"bat_spawn_egg", new SpawnEggItem(EntityType.BAT, 4996656, 986895, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8154 = register(
		"blaze_spawn_egg", new SpawnEggItem(EntityType.BLAZE, 16167425, 16775294, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_16314 = register(
		"cat_spawn_egg", new SpawnEggItem(EntityType.CAT, 15714446, 9794134, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8068 = register(
		"cave_spider_spawn_egg", new SpawnEggItem(EntityType.CAVE_SPIDER, 803406, 11013646, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8835 = register(
		"chicken_spawn_egg", new SpawnEggItem(EntityType.CHICKEN, 10592673, 16711680, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8661 = register(
		"cod_spawn_egg", new SpawnEggItem(EntityType.COD, 12691306, 15058059, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8433 = register(
		"cow_spawn_egg", new SpawnEggItem(EntityType.COW, 4470310, 10592673, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8503 = register(
		"creeper_spawn_egg", new SpawnEggItem(EntityType.CREEPER, 894731, 0, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8751 = register(
		"dolphin_spawn_egg", new SpawnEggItem(EntityType.DOLPHIN, 2243405, 16382457, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8306 = register(
		"donkey_spawn_egg", new SpawnEggItem(EntityType.DONKEY, 5457209, 8811878, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8083 = register(
		"drowned_spawn_egg", new SpawnEggItem(EntityType.DROWNED, 9433559, 7969893, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8769 = register(
		"elder_guardian_spawn_egg", new SpawnEggItem(EntityType.ELDER_GUARDIAN, 13552826, 7632531, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8374 = register(
		"enderman_spawn_egg", new SpawnEggItem(EntityType.ENDERMAN, 1447446, 0, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8510 = register(
		"endermite_spawn_egg", new SpawnEggItem(EntityType.ENDERMITE, 1447446, 7237230, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8795 = register(
		"evoker_spawn_egg", new SpawnEggItem(EntityType.EVOKER, 9804699, 1973274, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_18005 = register(
		"fox_spawn_egg", new SpawnEggItem(EntityType.field_17943, 14005919, 13396256, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8265 = register(
		"ghast_spawn_egg", new SpawnEggItem(EntityType.GHAST, 16382457, 12369084, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8409 = register(
		"guardian_spawn_egg", new SpawnEggItem(EntityType.GUARDIAN, 5931634, 15826224, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8117 = register(
		"horse_spawn_egg", new SpawnEggItem(EntityType.HORSE, 12623485, 15656192, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8760 = register(
		"husk_spawn_egg", new SpawnEggItem(EntityType.HUSK, 7958625, 15125652, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8633 = register(
		"llama_spawn_egg", new SpawnEggItem(EntityType.LLAMA, 12623485, 10051392, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8852 = register(
		"magma_cube_spawn_egg", new SpawnEggItem(EntityType.MAGMA_CUBE, 3407872, 16579584, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8299 = register(
		"mooshroom_spawn_egg", new SpawnEggItem(EntityType.MOOSHROOM, 10489616, 12040119, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8331 = register(
		"mule_spawn_egg", new SpawnEggItem(EntityType.MULE, 1769984, 5321501, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8132 = register(
		"ocelot_spawn_egg", new SpawnEggItem(EntityType.OCELOT, 15720061, 5653556, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8193 = register(
		"panda_spawn_egg", new SpawnEggItem(EntityType.PANDA, 15198183, 1776418, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8274 = register(
		"parrot_spawn_egg", new SpawnEggItem(EntityType.PARROT, 894731, 16711680, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8670 = register(
		"phantom_spawn_egg", new SpawnEggItem(EntityType.PHANTOM, 4411786, 8978176, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8493 = register(
		"pig_spawn_egg", new SpawnEggItem(EntityType.PIG, 15771042, 14377823, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8325 = register(
		"pillager_spawn_egg", new SpawnEggItem(EntityType.PILLAGER, 5451574, 9804699, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8346 = register(
		"polar_bear_spawn_egg", new SpawnEggItem(EntityType.POLAR_BEAR, 15921906, 9803152, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8100 = register(
		"pufferfish_spawn_egg", new SpawnEggItem(EntityType.PUFFERFISH, 16167425, 3654642, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8227 = register(
		"rabbit_spawn_egg", new SpawnEggItem(EntityType.RABBIT, 10051392, 7555121, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8297 = register(
		"ravager_spawn_egg", new SpawnEggItem(EntityType.ILLAGER_BEAST, 7697520, 5984329, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8447 = register(
		"salmon_spawn_egg", new SpawnEggItem(EntityType.SALMON, 10489616, 951412, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8607 = register(
		"sheep_spawn_egg", new SpawnEggItem(EntityType.SHEEP, 15198183, 16758197, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8480 = register(
		"shulker_spawn_egg", new SpawnEggItem(EntityType.SHULKER, 9725844, 5060690, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8564 = register(
		"silverfish_spawn_egg", new SpawnEggItem(EntityType.SILVERFISH, 7237230, 3158064, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8300 = register(
		"skeleton_spawn_egg", new SpawnEggItem(EntityType.SKELETON, 12698049, 4802889, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8232 = register(
		"skeleton_horse_spawn_egg", new SpawnEggItem(EntityType.SKELETON_HORSE, 6842447, 15066584, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8881 = register(
		"slime_spawn_egg", new SpawnEggItem(EntityType.SLIME, 5349438, 8306542, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8185 = register(
		"spider_spawn_egg", new SpawnEggItem(EntityType.SPIDER, 3419431, 11013646, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8307 = register(
		"squid_spawn_egg", new SpawnEggItem(EntityType.SQUID, 2243405, 7375001, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8514 = register(
		"stray_spawn_egg", new SpawnEggItem(EntityType.STRAY, 6387319, 14543594, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_17731 = register(
		"trader_llama_spawn_egg", new SpawnEggItem(EntityType.field_17714, 15377456, 4547222, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8612 = register(
		"tropical_fish_spawn_egg", new SpawnEggItem(EntityType.TROPICAL_FISH, 15690005, 16775663, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8435 = register(
		"turtle_spawn_egg", new SpawnEggItem(EntityType.TURTLE, 15198183, 44975, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8235 = register(
		"vex_spawn_egg", new SpawnEggItem(EntityType.VEX, 8032420, 15265265, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8086 = register(
		"villager_spawn_egg", new SpawnEggItem(EntityType.VILLAGER, 5651507, 12422002, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8149 = register(
		"vindicator_spawn_egg", new SpawnEggItem(EntityType.VINDICATOR, 9804699, 2580065, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_17732 = register(
		"wandering_trader_spawn_egg", new SpawnEggItem(EntityType.field_17713, 4547222, 15377456, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8254 = register(
		"witch_spawn_egg", new SpawnEggItem(EntityType.WITCH, 3407872, 5349438, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8832 = register(
		"wither_skeleton_spawn_egg", new SpawnEggItem(EntityType.WITHER_SKELETON, 1315860, 4672845, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8485 = register(
		"wolf_spawn_egg", new SpawnEggItem(EntityType.WOLF, 14144467, 13545366, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8441 = register(
		"zombie_spawn_egg", new SpawnEggItem(EntityType.ZOMBIE, 44975, 7969893, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8728 = register(
		"zombie_horse_spawn_egg", new SpawnEggItem(EntityType.ZOMBIE_HORSE, 3232308, 9945732, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8093 = register(
		"zombie_pigman_spawn_egg", new SpawnEggItem(EntityType.ZOMBIE_PIGMAN, 15373203, 5009705, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8136 = register(
		"zombie_villager_spawn_egg", new SpawnEggItem(EntityType.ZOMBIE_VILLAGER, 5651507, 7969893, new Item.Settings().itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8287 = register(
		"experience_bottle", new ExperienceBottleItem(new Item.Settings().itemGroup(ItemGroup.MISC).rarity(Rarity.field_8907))
	);
	public static final Item field_8814 = register("fire_charge", new FireChargeItem(new Item.Settings().itemGroup(ItemGroup.MISC)));
	public static final Item field_8674 = register("writable_book", new WritableBookItem(new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC)));
	public static final Item field_8360 = register("written_book", new WrittenBookItem(new Item.Settings().stackSize(16)));
	public static final Item field_8687 = register("emerald", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8143 = register("item_frame", new ItemFrameItem(new Item.Settings().itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8074 = registerBlock(Blocks.field_10495, ItemGroup.DECORATIONS);
	public static final Item field_8179 = register(
		"carrot", new FoodCropItem(3, 0.6F, Blocks.field_10609, SoundEvents.field_17611, new Item.Settings().itemGroup(ItemGroup.FOOD))
	);
	public static final Item field_8567 = register(
		"potato", new FoodCropItem(1, 0.3F, Blocks.field_10247, SoundEvents.field_17611, new Item.Settings().itemGroup(ItemGroup.FOOD))
	);
	public static final Item field_8512 = register("baked_potato", new FoodItem(5, 0.6F, false, new Item.Settings().itemGroup(ItemGroup.FOOD)));
	public static final Item field_8635 = register(
		"poisonous_potato",
		new FoodItem(2, 0.3F, false, new Item.Settings().itemGroup(ItemGroup.FOOD)).setStatusEffect(new StatusEffectInstance(StatusEffects.field_5899, 100, 0), 0.6F)
	);
	public static final Item field_8895 = register("map", new EmptyMapItem(new Item.Settings().itemGroup(ItemGroup.MISC)));
	public static final Item field_8071 = register("golden_carrot", new FoodItem(6, 1.2F, false, new Item.Settings().itemGroup(ItemGroup.BREWING)));
	public static final Item field_8398 = register(
		new WallStandingBlockItem(Blocks.field_10481, Blocks.field_10388, new Item.Settings().itemGroup(ItemGroup.DECORATIONS).rarity(Rarity.field_8907))
	);
	public static final Item field_8791 = register(
		new WallStandingBlockItem(Blocks.field_10177, Blocks.field_10101, new Item.Settings().itemGroup(ItemGroup.DECORATIONS).rarity(Rarity.field_8907))
	);
	public static final Item field_8575 = register(
		new SkullItem(Blocks.field_10432, Blocks.field_10208, new Item.Settings().itemGroup(ItemGroup.DECORATIONS).rarity(Rarity.field_8907))
	);
	public static final Item field_8470 = register(
		new WallStandingBlockItem(Blocks.field_10241, Blocks.field_10581, new Item.Settings().itemGroup(ItemGroup.DECORATIONS).rarity(Rarity.field_8907))
	);
	public static final Item field_8681 = register(
		new WallStandingBlockItem(Blocks.field_10042, Blocks.field_10509, new Item.Settings().itemGroup(ItemGroup.DECORATIONS).rarity(Rarity.field_8907))
	);
	public static final Item field_8712 = register(
		new WallStandingBlockItem(Blocks.field_10337, Blocks.field_10472, new Item.Settings().itemGroup(ItemGroup.DECORATIONS).rarity(Rarity.field_8907))
	);
	public static final Item field_8184 = register(
		"carrot_on_a_stick", new CarrotOnAStickItem(new Item.Settings().durability(25).itemGroup(ItemGroup.TRANSPORTATION))
	);
	public static final Item field_8137 = register("nether_star", new NetherStarItem(new Item.Settings().itemGroup(ItemGroup.MATERIALS).rarity(Rarity.field_8907)));
	public static final Item field_8741 = register("pumpkin_pie", new FoodItem(8, 0.3F, false, new Item.Settings().itemGroup(ItemGroup.FOOD)));
	public static final Item field_8639 = register("firework_rocket", new FireworksItem(new Item.Settings().itemGroup(ItemGroup.MISC)));
	public static final Item field_8450 = register("firework_star", new FireworkChargeItem(new Item.Settings().itemGroup(ItemGroup.MISC)));
	public static final Item field_8598 = register("enchanted_book", new EnchantedBookItem(new Item.Settings().stackSize(1).rarity(Rarity.field_8907)));
	public static final Item field_8729 = register("nether_brick", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8155 = register("quartz", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8069 = register(
		"tnt_minecart", new MinecartItem(AbstractMinecartEntity.Type.field_7675, new Item.Settings().stackSize(1).itemGroup(ItemGroup.TRANSPORTATION))
	);
	public static final Item field_8836 = register(
		"hopper_minecart", new MinecartItem(AbstractMinecartEntity.Type.field_7677, new Item.Settings().stackSize(1).itemGroup(ItemGroup.TRANSPORTATION))
	);
	public static final Item field_8662 = register("prismarine_shard", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8434 = register("prismarine_crystals", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8504 = register("rabbit", new FoodItem(3, 0.3F, true, new Item.Settings().itemGroup(ItemGroup.FOOD)));
	public static final Item field_8752 = register("cooked_rabbit", new FoodItem(5, 0.6F, true, new Item.Settings().itemGroup(ItemGroup.FOOD)));
	public static final Item field_8308 = register("rabbit_stew", new MushroomStewItem(10, new Item.Settings().stackSize(1).itemGroup(ItemGroup.FOOD)));
	public static final Item field_8073 = register("rabbit_foot", new Item(new Item.Settings().itemGroup(ItemGroup.BREWING)));
	public static final Item field_8245 = register("rabbit_hide", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8694 = register("armor_stand", new ArmorStandItem(new Item.Settings().stackSize(16).itemGroup(ItemGroup.DECORATIONS)));
	public static final Item field_8578 = register("iron_horse_armor", new Item(new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC)));
	public static final Item field_8560 = register("golden_horse_armor", new Item(new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC)));
	public static final Item field_8807 = register("diamond_horse_armor", new Item(new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC)));
	public static final Item field_8719 = register("lead", new LeashItem(new Item.Settings().itemGroup(ItemGroup.TOOLS)));
	public static final Item field_8448 = register("name_tag", new NameTagItem(new Item.Settings().itemGroup(ItemGroup.TOOLS)));
	public static final Item field_8220 = register(
		"command_block_minecart", new MinecartItem(AbstractMinecartEntity.Type.field_7681, new Item.Settings().stackSize(1))
	);
	public static final Item field_8748 = register("mutton", new FoodItem(2, 0.3F, true, new Item.Settings().itemGroup(ItemGroup.FOOD)));
	public static final Item field_8347 = register("cooked_mutton", new FoodItem(6, 0.8F, true, new Item.Settings().itemGroup(ItemGroup.FOOD)));
	public static final Item field_8539 = register(
		"white_banner", new BannerItem(Blocks.field_10154, Blocks.field_10202, new Item.Settings().stackSize(16).itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8824 = register(
		"orange_banner", new BannerItem(Blocks.field_10045, Blocks.field_10599, new Item.Settings().stackSize(16).itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8671 = register(
		"magenta_banner", new BannerItem(Blocks.field_10438, Blocks.field_10274, new Item.Settings().stackSize(16).itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8379 = register(
		"light_blue_banner", new BannerItem(Blocks.field_10452, Blocks.field_10050, new Item.Settings().stackSize(16).itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8049 = register(
		"yellow_banner", new BannerItem(Blocks.field_10547, Blocks.field_10139, new Item.Settings().stackSize(16).itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8778 = register(
		"lime_banner", new BannerItem(Blocks.field_10229, Blocks.field_10318, new Item.Settings().stackSize(16).itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8329 = register(
		"pink_banner", new BannerItem(Blocks.field_10612, Blocks.field_10531, new Item.Settings().stackSize(16).itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8617 = register(
		"gray_banner", new BannerItem(Blocks.field_10185, Blocks.field_10267, new Item.Settings().stackSize(16).itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8855 = register(
		"light_gray_banner", new BannerItem(Blocks.field_9985, Blocks.field_10604, new Item.Settings().stackSize(16).itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8629 = register(
		"cyan_banner", new BannerItem(Blocks.field_10165, Blocks.field_10372, new Item.Settings().stackSize(16).itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8405 = register(
		"purple_banner", new BannerItem(Blocks.field_10368, Blocks.field_10054, new Item.Settings().stackSize(16).itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8128 = register(
		"blue_banner", new BannerItem(Blocks.field_10281, Blocks.field_10067, new Item.Settings().stackSize(16).itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8124 = register(
		"brown_banner", new BannerItem(Blocks.field_10602, Blocks.field_10370, new Item.Settings().stackSize(16).itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8295 = register(
		"green_banner", new BannerItem(Blocks.field_10198, Blocks.field_10594, new Item.Settings().stackSize(16).itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8586 = register(
		"red_banner", new BannerItem(Blocks.field_10406, Blocks.field_10279, new Item.Settings().stackSize(16).itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8572 = register(
		"black_banner", new BannerItem(Blocks.field_10062, Blocks.field_10537, new Item.Settings().stackSize(16).itemGroup(ItemGroup.DECORATIONS))
	);
	public static final Item field_8301 = register(
		"end_crystal", new EndCrystalItem(new Item.Settings().itemGroup(ItemGroup.DECORATIONS).rarity(Rarity.field_8903))
	);
	public static final Item field_8233 = register(
		"chorus_fruit", new ChorusFruitItem(4, 0.3F, new Item.Settings().itemGroup(ItemGroup.MATERIALS)).setAlwaysConsumable()
	);
	public static final Item field_8882 = register("popped_chorus_fruit", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8186 = register("beetroot", new FoodItem(1, 0.6F, false, new Item.Settings().itemGroup(ItemGroup.FOOD)));
	public static final Item field_8309 = register("beetroot_seeds", new SeedsItem(Blocks.field_10341, new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8515 = register("beetroot_soup", new MushroomStewItem(6, new Item.Settings().stackSize(1).itemGroup(ItemGroup.FOOD)));
	public static final Item field_8613 = register(
		"dragon_breath", new Item(new Item.Settings().recipeRemainder(field_8469).itemGroup(ItemGroup.BREWING).rarity(Rarity.field_8907))
	);
	public static final Item field_8436 = register("splash_potion", new SplashPotionItem(new Item.Settings().stackSize(1).itemGroup(ItemGroup.BREWING)));
	public static final Item field_8236 = register("spectral_arrow", new SpectralArrowItem(new Item.Settings().itemGroup(ItemGroup.COMBAT)));
	public static final Item field_8087 = register("tipped_arrow", new TippedArrowItem(new Item.Settings().itemGroup(ItemGroup.COMBAT)));
	public static final Item field_8150 = register("lingering_potion", new LingeringPotionItem(new Item.Settings().stackSize(1).itemGroup(ItemGroup.BREWING)));
	public static final Item field_8255 = register("shield", new ShieldItem(new Item.Settings().durability(336).itemGroup(ItemGroup.COMBAT)));
	public static final Item field_8833 = register(
		"elytra", new ElytraItem(new Item.Settings().durability(432).itemGroup(ItemGroup.TRANSPORTATION).rarity(Rarity.field_8907))
	);
	public static final Item field_8486 = register(
		"spruce_boat", new BoatItem(BoatEntity.Type.SPRUCE, new Item.Settings().stackSize(1).itemGroup(ItemGroup.TRANSPORTATION))
	);
	public static final Item field_8442 = register(
		"birch_boat", new BoatItem(BoatEntity.Type.BIRCH, new Item.Settings().stackSize(1).itemGroup(ItemGroup.TRANSPORTATION))
	);
	public static final Item field_8730 = register(
		"jungle_boat", new BoatItem(BoatEntity.Type.JUNGLE, new Item.Settings().stackSize(1).itemGroup(ItemGroup.TRANSPORTATION))
	);
	public static final Item field_8094 = register(
		"acacia_boat", new BoatItem(BoatEntity.Type.ACACIA, new Item.Settings().stackSize(1).itemGroup(ItemGroup.TRANSPORTATION))
	);
	public static final Item field_8138 = register(
		"dark_oak_boat", new BoatItem(BoatEntity.Type.DARK_OAK, new Item.Settings().stackSize(1).itemGroup(ItemGroup.TRANSPORTATION))
	);
	public static final Item field_8288 = register(
		"totem_of_undying", new Item(new Item.Settings().stackSize(1).itemGroup(ItemGroup.COMBAT).rarity(Rarity.field_8907))
	);
	public static final Item field_8815 = register("shulker_shell", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8675 = register("iron_nugget", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8361 = register("knowledge_book", new KnowledgeBookItem(new Item.Settings().stackSize(1)));
	public static final Item field_8688 = register("debug_stick", new DebugStickItem(new Item.Settings().stackSize(1)));
	public static final Item field_8144 = register(
		"music_disc_13", new RecordItem(1, SoundEvents.field_14592, new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC).rarity(Rarity.field_8903))
	);
	public static final Item field_8075 = register(
		"music_disc_cat", new RecordItem(2, SoundEvents.field_14744, new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC).rarity(Rarity.field_8903))
	);
	public static final Item field_8425 = register(
		"music_disc_blocks", new RecordItem(3, SoundEvents.field_14829, new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC).rarity(Rarity.field_8903))
	);
	public static final Item field_8623 = register(
		"music_disc_chirp", new RecordItem(4, SoundEvents.field_15039, new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC).rarity(Rarity.field_8903))
	);
	public static final Item field_8502 = register(
		"music_disc_far", new RecordItem(5, SoundEvents.field_14944, new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC).rarity(Rarity.field_8903))
	);
	public static final Item field_8534 = register(
		"music_disc_mall", new RecordItem(6, SoundEvents.field_15059, new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC).rarity(Rarity.field_8903))
	);
	public static final Item field_8344 = register(
		"music_disc_mellohi", new RecordItem(7, SoundEvents.field_15169, new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC).rarity(Rarity.field_8903))
	);
	public static final Item field_8834 = register(
		"music_disc_stal", new RecordItem(8, SoundEvents.field_14578, new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC).rarity(Rarity.field_8903))
	);
	public static final Item field_8065 = register(
		"music_disc_strad", new RecordItem(9, SoundEvents.field_14656, new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC).rarity(Rarity.field_8903))
	);
	public static final Item field_8355 = register(
		"music_disc_ward", new RecordItem(10, SoundEvents.field_14838, new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC).rarity(Rarity.field_8903))
	);
	public static final Item field_8731 = register(
		"music_disc_11", new RecordItem(11, SoundEvents.field_14654, new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC).rarity(Rarity.field_8903))
	);
	public static final Item field_8806 = register(
		"music_disc_wait", new RecordItem(12, SoundEvents.field_14759, new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC).rarity(Rarity.field_8903))
	);
	public static final Item field_8547 = register("trident", new TridentItem(new Item.Settings().durability(250).itemGroup(ItemGroup.COMBAT)));
	public static final Item field_8614 = register("phantom_membrane", new Item(new Item.Settings().itemGroup(ItemGroup.BREWING)));
	public static final Item field_8864 = register("nautilus_shell", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS)));
	public static final Item field_8207 = register("heart_of_the_sea", new Item(new Item.Settings().itemGroup(ItemGroup.MATERIALS).rarity(Rarity.field_8907)));
	public static final Item field_8399 = register("crossbow", new CrossbowItem(new Item.Settings().stackSize(1).itemGroup(ItemGroup.COMBAT).durability(326)));
	public static final Item field_8766 = register("suspicious_stew", new SuspiciousStewItem(6, new Item.Settings().stackSize(1)));
	public static final Item field_8772 = registerBlock(Blocks.field_10083, ItemGroup.DECORATIONS);
	public static final Item field_8498 = register(
		"flower_banner_pattern", new BannerPatternItem(BannerPattern.FLOWER, new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC))
	);
	public static final Item field_8573 = register(
		"creeper_banner_pattern", new BannerPatternItem(BannerPattern.CREEPER, new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC).rarity(Rarity.field_8907))
	);
	public static final Item field_8891 = register(
		"skull_banner_pattern", new BannerPatternItem(BannerPattern.SKULL, new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC).rarity(Rarity.field_8907))
	);
	public static final Item field_8159 = register(
		"mojang_banner_pattern", new BannerPatternItem(BannerPattern.MOJANG, new Item.Settings().stackSize(1).itemGroup(ItemGroup.MISC).rarity(Rarity.field_8904))
	);
	public static final Item field_16307 = registerBlock(Blocks.field_16328, ItemGroup.DECORATIONS);
	public static final Item field_16309 = registerBlock(Blocks.field_16334, ItemGroup.DECORATIONS);
	public static final Item field_16306 = registerBlock(Blocks.field_16333, ItemGroup.DECORATIONS);
	public static final Item field_16313 = registerBlock(Blocks.field_16336, ItemGroup.DECORATIONS);
	public static final Item field_16310 = registerBlock(Blocks.field_16331, ItemGroup.DECORATIONS);
	public static final Item field_16311 = registerBlock(Blocks.field_16337, ItemGroup.DECORATIONS);
	public static final Item field_16312 = registerBlock(Blocks.field_16330, ItemGroup.REDSTONE);
	public static final Item field_16308 = registerBlock(Blocks.field_16329, ItemGroup.DECORATIONS);
	public static final Item field_16305 = registerBlock(Blocks.field_16335, ItemGroup.DECORATIONS);
	public static final Item field_16315 = registerBlock(Blocks.field_16332, ItemGroup.DECORATIONS);
	public static final Item field_16539 = registerBlock(Blocks.field_16541, ItemGroup.DECORATIONS);
	public static final Item field_16998 = register(
		"sweet_berries", new FoodCropItem(2, 0.1F, Blocks.field_16999, SoundEvents.field_17616, new Item.Settings().itemGroup(ItemGroup.FOOD))
	);
	public static final Item field_17346 = registerBlock(Blocks.field_17350, ItemGroup.DECORATIONS);

	private static Item method_7989(Block block) {
		return register(new BlockItem(block, new Item.Settings()));
	}

	private static Item registerBlock(Block block, ItemGroup itemGroup) {
		return register(new BlockItem(block, new Item.Settings().itemGroup(itemGroup)));
	}

	private static Item register(BlockItem blockItem) {
		return register(blockItem.getBlock(), blockItem);
	}

	protected static Item register(Block block, Item item) {
		return register(Registry.BLOCK.getId(block), item);
	}

	private static Item register(String string, Item item) {
		return register(new Identifier(string), item);
	}

	private static Item register(Identifier identifier, Item item) {
		if (item instanceof BlockItem) {
			((BlockItem)item).registerBlockItemMap(Item.BLOCK_ITEM_MAP, item);
		}

		return Registry.register(Registry.ITEM, identifier, item);
	}
}
