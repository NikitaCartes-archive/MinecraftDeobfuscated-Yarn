package net.minecraft.loot.provider.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.loot.context.LootContext;

public record ConstantLootNumberProvider(float value) implements LootNumberProvider {
	public static final Codec<ConstantLootNumberProvider> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Codec.FLOAT.fieldOf("value").forGetter(ConstantLootNumberProvider::value)).apply(instance, ConstantLootNumberProvider::new)
	);
	public static final Codec<ConstantLootNumberProvider> INLINE_CODEC = Codec.FLOAT.xmap(ConstantLootNumberProvider::new, ConstantLootNumberProvider::value);

	@Override
	public LootNumberProviderType getType() {
		return LootNumberProviderTypes.CONSTANT;
	}

	@Override
	public float nextFloat(LootContext context) {
		return this.value;
	}

	public static ConstantLootNumberProvider create(float value) {
		return new ConstantLootNumberProvider(value);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			return Float.compare(((ConstantLootNumberProvider)o).value, this.value) == 0;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.value != 0.0F ? Float.floatToIntBits(this.value) : 0;
	}
}
