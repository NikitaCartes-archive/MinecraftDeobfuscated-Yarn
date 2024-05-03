package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;

public class SetComponentsLootFunction extends ConditionalLootFunction {
	public static final MapCodec<SetComponentsLootFunction> CODEC = RecordCodecBuilder.mapCodec(
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
	public LootFunctionType<SetComponentsLootFunction> getType() {
		return LootFunctionTypes.SET_COMPONENTS;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		stack.applyChanges(this.changes);
		return stack;
	}

	public static <T> ConditionalLootFunction.Builder<?> builder(ComponentType<T> componentType, T value) {
		return builder(conditions -> new SetComponentsLootFunction(conditions, ComponentChanges.builder().add(componentType, value).build()));
	}
}
