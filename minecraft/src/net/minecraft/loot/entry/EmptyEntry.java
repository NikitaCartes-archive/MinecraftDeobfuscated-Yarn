package net.minecraft.loot.entry;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;

public class EmptyEntry extends LeafEntry {
	public static final MapCodec<EmptyEntry> CODEC = RecordCodecBuilder.mapCodec(instance -> addLeafFields(instance).apply(instance, EmptyEntry::new));

	private EmptyEntry(int weight, int quality, List<LootCondition> conditions, List<LootFunction> functions) {
		super(weight, quality, conditions, functions);
	}

	@Override
	public LootPoolEntryType getType() {
		return LootPoolEntryTypes.EMPTY;
	}

	@Override
	public void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context) {
	}

	public static LeafEntry.Builder<?> builder() {
		return builder(EmptyEntry::new);
	}
}
