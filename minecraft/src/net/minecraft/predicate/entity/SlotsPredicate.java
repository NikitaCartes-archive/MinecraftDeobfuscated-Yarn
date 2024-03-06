package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.SlotRange;
import net.minecraft.inventory.SlotRanges;
import net.minecraft.inventory.StackReference;
import net.minecraft.predicate.item.ItemPredicate;

public record SlotsPredicate(Map<SlotRange, ItemPredicate> slots) {
	public static final Codec<SlotsPredicate> CODEC = Codec.unboundedMap(SlotRanges.CODEC, ItemPredicate.CODEC).xmap(SlotsPredicate::new, SlotsPredicate::slots);

	public boolean matches(Entity entity) {
		for (Entry<SlotRange, ItemPredicate> entry : this.slots.entrySet()) {
			if (!matches(entity, (ItemPredicate)entry.getValue(), ((SlotRange)entry.getKey()).getSlotIds())) {
				return false;
			}
		}

		return true;
	}

	private static boolean matches(Entity entity, ItemPredicate itemPredicate, IntList slotIds) {
		for (int i = 0; i < slotIds.size(); i++) {
			int j = slotIds.getInt(i);
			StackReference stackReference = entity.getStackReference(j);
			if (itemPredicate.test(stackReference.get())) {
				return true;
			}
		}

		return false;
	}
}
