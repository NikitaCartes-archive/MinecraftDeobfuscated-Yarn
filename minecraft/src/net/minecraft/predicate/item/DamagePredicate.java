package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;

public record DamagePredicate(NumberRange.IntRange durability, NumberRange.IntRange damage) implements ComponentSubPredicate<Integer> {
	public static final Codec<DamagePredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					NumberRange.IntRange.CODEC.optionalFieldOf("durability", NumberRange.IntRange.ANY).forGetter(DamagePredicate::durability),
					NumberRange.IntRange.CODEC.optionalFieldOf("damage", NumberRange.IntRange.ANY).forGetter(DamagePredicate::damage)
				)
				.apply(instance, DamagePredicate::new)
	);

	@Override
	public ComponentType<Integer> getComponentType() {
		return DataComponentTypes.DAMAGE;
	}

	public boolean test(ItemStack itemStack, Integer integer) {
		return !this.durability.test(itemStack.getMaxDamage() - integer) ? false : this.damage.test(integer);
	}

	public static DamagePredicate durability(NumberRange.IntRange durability) {
		return new DamagePredicate(durability, NumberRange.IntRange.ANY);
	}
}
