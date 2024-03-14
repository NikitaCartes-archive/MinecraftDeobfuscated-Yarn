package net.minecraft.inventory;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;

public class SlotRanges {
	private static final List<SlotRange> SLOT_RANGES = Util.make(new ArrayList(), list -> {
		createAndAdd(list, "contents", 0);
		createAndAdd(list, "container.", 0, 54);
		createAndAdd(list, "hotbar.", 0, 9);
		createAndAdd(list, "inventory.", 9, 27);
		createAndAdd(list, "enderchest.", 200, 27);
		createAndAdd(list, "villager.", 300, 8);
		createAndAdd(list, "horse.", 500, 15);
		int i = EquipmentSlot.MAINHAND.getOffsetEntitySlotId(98);
		int j = EquipmentSlot.OFFHAND.getOffsetEntitySlotId(98);
		createAndAdd(list, "weapon", i);
		createAndAdd(list, "weapon.mainhand", i);
		createAndAdd(list, "weapon.offhand", j);
		createAndAdd(list, "weapon.*", i, j);
		i = EquipmentSlot.HEAD.getOffsetEntitySlotId(100);
		j = EquipmentSlot.CHEST.getOffsetEntitySlotId(100);
		int k = EquipmentSlot.LEGS.getOffsetEntitySlotId(100);
		int l = EquipmentSlot.FEET.getOffsetEntitySlotId(100);
		int m = EquipmentSlot.BODY.getOffsetEntitySlotId(105);
		createAndAdd(list, "armor.head", i);
		createAndAdd(list, "armor.chest", j);
		createAndAdd(list, "armor.legs", k);
		createAndAdd(list, "armor.feet", l);
		createAndAdd(list, "armor.body", m);
		createAndAdd(list, "armor.*", i, j, k, l, m);
		createAndAdd(list, "horse.saddle", 400);
		createAndAdd(list, "horse.chest", 499);
		createAndAdd(list, "player.cursor", 499);
		createAndAdd(list, "player.crafting.", 500, 4);
	});
	public static final Codec<SlotRange> CODEC = StringIdentifiable.createBasicCodec(() -> (SlotRange[])SLOT_RANGES.toArray(new SlotRange[0]));
	private static final Function<String, SlotRange> FROM_NAME = StringIdentifiable.createMapper((SlotRange[])SLOT_RANGES.toArray(new SlotRange[0]), name -> name);

	private static SlotRange create(String name, int slotId) {
		return SlotRange.create(name, IntLists.singleton(slotId));
	}

	private static SlotRange create(String name, IntList slotIds) {
		return SlotRange.create(name, IntLists.unmodifiable(slotIds));
	}

	private static SlotRange create(String name, int... slotIds) {
		return SlotRange.create(name, IntList.of(slotIds));
	}

	private static void createAndAdd(List<SlotRange> list, String name, int slotId) {
		list.add(create(name, slotId));
	}

	private static void createAndAdd(List<SlotRange> list, String baseName, int firstSlotId, int lastSlotId) {
		IntList intList = new IntArrayList(lastSlotId);

		for (int i = 0; i < lastSlotId; i++) {
			int j = firstSlotId + i;
			list.add(create(baseName + i, j));
			intList.add(j);
		}

		list.add(create(baseName + "*", intList));
	}

	private static void createAndAdd(List<SlotRange> list, String name, int... slots) {
		list.add(create(name, slots));
	}

	@Nullable
	public static SlotRange fromName(String name) {
		return (SlotRange)FROM_NAME.apply(name);
	}

	public static Stream<String> streamNames() {
		return SLOT_RANGES.stream().map(StringIdentifiable::asString);
	}

	public static Stream<String> streamSingleSlotNames() {
		return SLOT_RANGES.stream().filter(slotRange -> slotRange.getSlotCount() == 1).map(StringIdentifiable::asString);
	}
}
