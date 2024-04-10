package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;

public class AndLootFunction implements LootFunction {
	public static final MapCodec<AndLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(LootFunctionTypes.BASE_CODEC.listOf().fieldOf("functions").forGetter(function -> function.terms))
				.apply(instance, AndLootFunction::new)
	);
	public static final Codec<AndLootFunction> INLINE_CODEC = LootFunctionTypes.BASE_CODEC.listOf().xmap(AndLootFunction::new, function -> function.terms);
	private final List<LootFunction> terms;
	private final BiFunction<ItemStack, LootContext, ItemStack> applier;

	private AndLootFunction(List<LootFunction> terms) {
		this.terms = terms;
		this.applier = LootFunctionTypes.join(terms);
	}

	public static AndLootFunction create(List<LootFunction> terms) {
		return new AndLootFunction(List.copyOf(terms));
	}

	public ItemStack apply(ItemStack itemStack, LootContext lootContext) {
		return (ItemStack)this.applier.apply(itemStack, lootContext);
	}

	@Override
	public void validate(LootTableReporter reporter) {
		LootFunction.super.validate(reporter);

		for (int i = 0; i < this.terms.size(); i++) {
			((LootFunction)this.terms.get(i)).validate(reporter.makeChild(".function[" + i + "]"));
		}
	}

	@Override
	public LootFunctionType<AndLootFunction> getType() {
		return LootFunctionTypes.SEQUENCE;
	}
}
