package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;

public class SetComponentsLootFunction extends ConditionalLootFunction {
	public static final Codec<SetComponentsLootFunction> CODEC = RecordCodecBuilder.create(
		instance -> addConditionsField(instance)
				.and(ComponentChanges.CODEC.fieldOf("components").forGetter(function -> function.changes))
				.apply(instance, SetComponentsLootFunction::new)
	);
	private final ComponentChanges changes;

	private SetComponentsLootFunction(List<LootCondition> conditions, ComponentChanges changes) {
		super(conditions);
		this.changes = changes;
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.SET_COMPONENTS;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		stack.applyChanges(this.changes);
		return stack;
	}

	public static <T> ConditionalLootFunction.Builder<?> builder(DataComponentType<T> componentType, T value) {
		return builder(conditions -> new SetComponentsLootFunction(conditions, ComponentChanges.builder().add(componentType, value).build()));
	}
}
