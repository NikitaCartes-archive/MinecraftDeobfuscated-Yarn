package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.DataComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.util.dynamic.Codecs;

public record DamagePredicate(NumberRange.IntRange durability, NumberRange.IntRange damage) implements ComponentSubPredicate<Integer> {
	public static final Codec<DamagePredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.createStrictOptionalFieldCodec(NumberRange.IntRange.CODEC, "durability", NumberRange.IntRange.ANY).forGetter(DamagePredicate::durability),
					Codecs.createStrictOptionalFieldCodec(NumberRange.IntRange.CODEC, "damage", NumberRange.IntRange.ANY).forGetter(DamagePredicate::damage)
				)
				.apply(instance, DamagePredicate::new)
	);

	@Override
	public DataComponentType<Integer> getComponentType() {
		return DataComponentTypes.DAMAGE;
	}

	public boolean test(ItemStack itemStack, Integer integer) {
		return !this.durability.test(itemStack.getMaxDamage() - integer) ? false : this.damage.test(integer);
	}

	public static DamagePredicate durability(NumberRange.IntRange durability) {
		return new DamagePredicate(durability, NumberRange.IntRange.ANY);
	}
}
