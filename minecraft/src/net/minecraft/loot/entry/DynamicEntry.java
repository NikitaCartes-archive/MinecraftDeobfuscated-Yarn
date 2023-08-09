package net.minecraft.loot.entry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.Identifier;

public class DynamicEntry extends LeafEntry {
	public static final Codec<DynamicEntry> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Identifier.CODEC.fieldOf("name").forGetter(dynamicEntry -> dynamicEntry.name))
				.<int, int, List<LootCondition>, List<LootFunction>>and(method_53290(instance))
				.apply(instance, DynamicEntry::new)
	);
	private final Identifier name;

	private DynamicEntry(Identifier name, int weight, int quality, List<LootCondition> conditions, List<LootFunction> functions) {
		super(weight, quality, conditions, functions);
		this.name = name;
	}

	@Override
	public LootPoolEntryType getType() {
		return LootPoolEntryTypes.DYNAMIC;
	}

	@Override
	public void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context) {
		context.drop(this.name, lootConsumer);
	}

	public static LeafEntry.Builder<?> builder(Identifier name) {
		return builder((weight, quality, conditions, functions) -> new DynamicEntry(name, weight, quality, conditions, functions));
	}
}
