package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

public class SetPotionLootFunction extends ConditionalLootFunction {
	public static final Codec<SetPotionLootFunction> CODEC = RecordCodecBuilder.create(
		instance -> addConditionsField(instance)
				.and(Registries.POTION.getEntryCodec().fieldOf("id").forGetter(function -> function.potion))
				.apply(instance, SetPotionLootFunction::new)
	);
	private final RegistryEntry<Potion> potion;

	private SetPotionLootFunction(List<LootCondition> conditions, RegistryEntry<Potion> potion) {
		super(conditions);
		this.potion = potion;
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.SET_POTION;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		PotionUtil.setPotion(stack, this.potion);
		return stack;
	}

	public static ConditionalLootFunction.Builder<?> builder(RegistryEntry<Potion> potion) {
		return builder(conditions -> new SetPotionLootFunction(conditions, potion));
	}
}
