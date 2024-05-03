package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;

public record TrimPredicate(Optional<RegistryEntryList<ArmorTrimMaterial>> material, Optional<RegistryEntryList<ArmorTrimPattern>> pattern)
	implements ComponentSubPredicate<ArmorTrim> {
	public static final Codec<TrimPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					RegistryCodecs.entryList(RegistryKeys.TRIM_MATERIAL).optionalFieldOf("material").forGetter(TrimPredicate::material),
					RegistryCodecs.entryList(RegistryKeys.TRIM_PATTERN).optionalFieldOf("pattern").forGetter(TrimPredicate::pattern)
				)
				.apply(instance, TrimPredicate::new)
	);

	@Override
	public ComponentType<ArmorTrim> getComponentType() {
		return DataComponentTypes.TRIM;
	}

	public boolean test(ItemStack itemStack, ArmorTrim armorTrim) {
		return this.material.isPresent() && !((RegistryEntryList)this.material.get()).contains(armorTrim.getMaterial())
			? false
			: !this.pattern.isPresent() || ((RegistryEntryList)this.pattern.get()).contains(armorTrim.getPattern());
	}
}
