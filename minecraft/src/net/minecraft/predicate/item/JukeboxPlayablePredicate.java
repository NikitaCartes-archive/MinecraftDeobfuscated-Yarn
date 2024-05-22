package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.JukeboxPlayableComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;

public record JukeboxPlayablePredicate(Optional<RegistryEntryList<JukeboxSong>> song) implements ComponentSubPredicate<JukeboxPlayableComponent> {
	public static final Codec<JukeboxPlayablePredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(RegistryCodecs.entryList(RegistryKeys.JUKEBOX_SONG).optionalFieldOf("song").forGetter(JukeboxPlayablePredicate::song))
				.apply(instance, JukeboxPlayablePredicate::new)
	);

	@Override
	public ComponentType<JukeboxPlayableComponent> getComponentType() {
		return DataComponentTypes.JUKEBOX_PLAYABLE;
	}

	public boolean test(ItemStack itemStack, JukeboxPlayableComponent jukeboxPlayableComponent) {
		if (!this.song.isPresent()) {
			return true;
		} else {
			boolean bl = false;

			for (RegistryEntry<JukeboxSong> registryEntry : (RegistryEntryList)this.song.get()) {
				Optional<RegistryKey<JukeboxSong>> optional = registryEntry.getKey();
				if (!optional.isEmpty() && optional.get() == jukeboxPlayableComponent.song().key()) {
					bl = true;
					break;
				}
			}

			return bl;
		}
	}

	public static JukeboxPlayablePredicate empty() {
		return new JukeboxPlayablePredicate(Optional.empty());
	}
}
