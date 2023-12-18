package net.minecraft.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

public interface SuspiciousStewIngredient {
	List<SuspiciousStewIngredient.StewEffect> getStewEffects();

	static List<SuspiciousStewIngredient> getAll() {
		return (List<SuspiciousStewIngredient>)Registries.ITEM.stream().map(SuspiciousStewIngredient::of).filter(Objects::nonNull).collect(Collectors.toList());
	}

	@Nullable
	static SuspiciousStewIngredient of(ItemConvertible item) {
		if (item.asItem() instanceof BlockItem blockItem) {
			Block var6 = blockItem.getBlock();
			if (var6 instanceof SuspiciousStewIngredient) {
				return (SuspiciousStewIngredient)var6;
			}
		}

		Item suspiciousStewIngredient = item.asItem();
		return suspiciousStewIngredient instanceof SuspiciousStewIngredient ? (SuspiciousStewIngredient)suspiciousStewIngredient : null;
	}

	public static record StewEffect(RegistryEntry<StatusEffect> effect, int duration) {
		public static final Codec<SuspiciousStewIngredient.StewEffect> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Registries.STATUS_EFFECT.createEntryCodec().fieldOf("id").forGetter(SuspiciousStewIngredient.StewEffect::effect),
						Codec.INT.optionalFieldOf("duration", Integer.valueOf(160)).forGetter(SuspiciousStewIngredient.StewEffect::duration)
					)
					.apply(instance, SuspiciousStewIngredient.StewEffect::new)
		);
		public static final Codec<List<SuspiciousStewIngredient.StewEffect>> LIST_CODEC = CODEC.listOf();

		public StatusEffectInstance createStatusEffectInstance() {
			return new StatusEffectInstance(this.effect, this.duration);
		}
	}
}
