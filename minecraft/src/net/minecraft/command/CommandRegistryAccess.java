package net.minecraft.command;

import net.minecraft.registry.DynamicRegistryManager;
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
public interface CommandRegistryAccess {
	/**
	 * Creates a registry wrapper that follows the entry list creation policy.
	 * 
	 * @param registryRef the registry key of the registry to wrap
	 */
	<T> RegistryWrapper<T> createWrapper(RegistryKey<? extends Registry<T>> registryRef);

	static CommandRegistryAccess of(RegistryWrapper.WrapperLookup wrapperLookup, FeatureSet enabledFeatures) {
		return new CommandRegistryAccess() {
			@Override
			public <T> RegistryWrapper<T> createWrapper(RegistryKey<? extends Registry<T>> registryRef) {
				return wrapperLookup.getWrapperOrThrow(registryRef).withFeatureFilter(enabledFeatures);
			}
		};
	}

	static CommandRegistryAccess.EntryListCreationPolicySettable of(DynamicRegistryManager registryManager, FeatureSet enabledFeatures) {
		return new CommandRegistryAccess.EntryListCreationPolicySettable() {
			CommandRegistryAccess.EntryListCreationPolicy entryListCreationPolicy = CommandRegistryAccess.EntryListCreationPolicy.FAIL;

			@Override
			public void setEntryListCreationPolicy(CommandRegistryAccess.EntryListCreationPolicy entryListCreationPolicy) {
				this.entryListCreationPolicy = entryListCreationPolicy;
			}

			@Override
			public <T> RegistryWrapper<T> createWrapper(RegistryKey<? extends Registry<T>> registryRef) {
				Registry<T> registry = registryManager.get(registryRef);
				final RegistryWrapper.Impl<T> impl = registry.getReadOnlyWrapper();
				final RegistryWrapper.Impl<T> impl2 = registry.getTagCreatingWrapper();
				RegistryWrapper.Impl<T> impl3 = new RegistryWrapper.Impl.Delegating<T>() {
					@Override
					protected RegistryWrapper.Impl<T> getBase() {
						return switch (entryListCreationPolicy) {
							case FAIL -> impl;
							case CREATE_NEW -> impl2;
						};
					}
				};
				return impl3.withFeatureFilter(enabledFeatures);
			}
		};
	}

	/**
	 * A policy on how to handle a {@link net.minecraft.registry.tag.TagKey} that does not resolve
	 * to an existing tag (unrecognized tag) in {@link
	 * net.minecraft.registry.RegistryWrapper#getOptional(net.minecraft.registry.tag.TagKey)}.
	 */
	public static enum EntryListCreationPolicy {
		/**
		 * Creates a new {@link net.minecraft.registry.entry.RegistryEntryList}, stores it and returns it.
		 */
		CREATE_NEW,
		/**
		 * Throws an exception.
		 */
		FAIL;
	}

	public interface EntryListCreationPolicySettable extends CommandRegistryAccess {
		void setEntryListCreationPolicy(CommandRegistryAccess.EntryListCreationPolicy entryListCreationPolicy);
	}
}
