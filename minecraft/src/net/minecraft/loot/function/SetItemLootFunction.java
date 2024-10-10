package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.entry.RegistryEntry;

public class SetItemLootFunction extends ConditionalLootFunction {
	public static final MapCodec<SetItemLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.and(Item.ENTRY_CODEC.fieldOf("item").forGetter(lootFunction -> lootFunction.item))
				.apply(instance, SetItemLootFunction::new)
	);
	private final RegistryEntry<Item> item;

	private SetItemLootFunction(List<LootCondition> conditions, RegistryEntry<Item> item) {
		super(conditions);
		this.item = item;
	}

	@Override
	public LootFunctionType<SetItemLootFunction> getType() {
		return LootFunctionTypes.SET_ITEM;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		return stack.withItem(this.item.value());
	}
}
