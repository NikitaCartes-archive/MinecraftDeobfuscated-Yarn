package net.minecraft.command;

import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;

/**
 * A class that creates {@link RegistryWrapper} with ability to set a policy on
 * how to handle unrecognized tags.
 * 
 * @apiNote You usually do not need to create your own instance; the command registration
 * callbacks (such as {@link net.minecraft.server.command.CommandManager} constructor)
 * provides an instance with proper configurations.
 */
public interface CommandRegistryAccess extends RegistryWrapper.WrapperLookup {
	static CommandRegistryAccess of(RegistryWrapper.WrapperLookup wrapperLookup, FeatureSet enabledFeatures) {
		return new CommandRegistryAccess() {
			@Override
			public Stream<RegistryKey<? extends Registry<?>>> streamAllRegistryKeys() {
				return wrapperLookup.streamAllRegistryKeys();
			}

			@Override
			public <T> Optional<RegistryWrapper.Impl<T>> getOptionalWrapper(RegistryKey<? extends Registry<? extends T>> registryRef) {
				return wrapperLookup.getOptionalWrapper(registryRef).map(wrapper -> wrapper.withFeatureFilter(enabledFeatures));
			}
		};
	}
}
