package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.ComponentSubPredicate;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;

public record class_9790(Optional<RegistryEntryList<class_9793>> song) implements ComponentSubPredicate<class_9792> {
	public static final Codec<class_9790> field_52020 = RecordCodecBuilder.create(
		instance -> instance.group(RegistryCodecs.entryList(RegistryKeys.JUKEBOX_SONG).optionalFieldOf("song").forGetter(class_9790::song))
				.apply(instance, class_9790::new)
	);

	@Override
	public ComponentType<class_9792> getComponentType() {
		return DataComponentTypes.JUKEBOX_PLAYABLE;
	}

	public boolean test(ItemStack itemStack, class_9792 arg) {
		if (!this.song.isPresent()) {
			return true;
		} else {
			boolean bl = false;

			for (RegistryEntry<class_9793> registryEntry : (RegistryEntryList)this.song.get()) {
				Optional<RegistryKey<class_9793>> optional = registryEntry.getKey();
				if (!optional.isEmpty() && optional.get() == arg.song().key()) {
					bl = true;
					break;
				}
			}

			return bl;
		}
	}

	public static class_9790 method_60732() {
		return new class_9790(Optional.empty());
	}
}
