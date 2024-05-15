package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.potion.Potion;
import net.minecraft.registry.entry.RegistryEntry;

public class SetPotionLootFunction extends ConditionalLootFunction {
	public static final MapCodec<SetPotionLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance).and(Potion.CODEC.fieldOf("id").forGetter(function -> function.potion)).apply(instance, SetPotionLootFunction::new)
	);
	private final RegistryEntry<Potion> potion;

	private SetPotionLootFunction(List<LootCondition> conditions, RegistryEntry<Potion> potion) {
		super(conditions);
		this.potion = potion;
	}

	@Override
	public LootFunctionType<SetPotionLootFunction> getType() {
		return LootFunctionTypes.SET_POTION;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		stack.apply(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT, this.potion, PotionContentsComponent::with);
		return stack;
	}

	public static ConditionalLootFunction.Builder<?> builder(RegistryEntry<Potion> potion) {
		return builder(conditions -> new SetPotionLootFunction(conditions, potion));
	}
}
