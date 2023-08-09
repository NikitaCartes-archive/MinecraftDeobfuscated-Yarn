package net.minecraft.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;

public class AndFunction implements LootFunction {
	public static final Codec<AndFunction> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(LootFunctionTypes.CODEC.listOf().fieldOf("functions").forGetter(andFunction -> andFunction.terms))
				.apply(instance, AndFunction::new)
	);
	public static final Codec<AndFunction> field_45835 = LootFunctionTypes.CODEC.listOf().xmap(AndFunction::new, andFunction -> andFunction.terms);
	private final List<LootFunction> terms;
	private final BiFunction<ItemStack, LootContext, ItemStack> applier;

	private AndFunction(List<LootFunction> terms) {
		this.terms = terms;
		this.applier = LootFunctionTypes.join(terms);
	}

	public static AndFunction create(List<LootFunction> terms) {
		return new AndFunction(List.copyOf(terms));
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
	public LootFunctionType getType() {
		return LootFunctionTypes.SEQUENCE;
	}
}
