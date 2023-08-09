package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

public record TableBonusLootCondition(RegistryEntry<Enchantment> enchantment, List<Float> chances) implements LootCondition {
	public static final Codec<TableBonusLootCondition> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Registries.ENCHANTMENT.createEntryCodec().fieldOf("enchantment").forGetter(TableBonusLootCondition::enchantment),
					Codec.FLOAT.listOf().fieldOf("chances").forGetter(TableBonusLootCondition::chances)
				)
				.apply(instance, TableBonusLootCondition::new)
	);

	@Override
	public LootConditionType getType() {
		return LootConditionTypes.TABLE_BONUS;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.TOOL);
	}

	public boolean test(LootContext lootContext) {
		ItemStack itemStack = lootContext.get(LootContextParameters.TOOL);
		int i = itemStack != null ? EnchantmentHelper.getLevel(this.enchantment.value(), itemStack) : 0;
		float f = (Float)this.chances.get(Math.min(i, this.chances.size() - 1));
		return lootContext.getRandom().nextFloat() < f;
	}

	public static LootCondition.Builder builder(Enchantment enchantment, float... chances) {
		List<Float> list = new ArrayList(chances.length);

		for (float f : chances) {
			list.add(f);
		}

		return () -> new TableBonusLootCondition(enchantment.getRegistryEntry(), list);
	}
}
