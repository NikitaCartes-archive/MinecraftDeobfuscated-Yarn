package net.minecraft.registry.entry;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import net.minecraft.registry.VersionedIdentifier;

public record RegistryEntryInfo(Optional<VersionedIdentifier> knownPackInfo, Lifecycle lifecycle) {
	public static final RegistryEntryInfo DEFAULT = new RegistryEntryInfo(Optional.empty(), Lifecycle.stable());
}
