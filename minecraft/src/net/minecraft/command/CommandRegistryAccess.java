package net.minecraft.command;

import java.util.Optional;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;

/**
 * A class that creates {@link CommandRegistryWrapper} with ability to set a policy on
 * how to handle unrecognized tags.
 * 
 * @apiNote You usually do not need to create your own instance; the command registration
 * callbacks (such as {@link net.minecraft.server.command.CommandManager} constructor)
 * provides an instance with proper configurations.
 */
public final class CommandRegistryAccess {
	private final DynamicRegistryManager dynamicRegistryManager;
	private final FeatureSet enabledFeatures;
	CommandRegistryAccess.EntryListCreationPolicy entryListCreationPolicy = CommandRegistryAccess.EntryListCreationPolicy.FAIL;

	public CommandRegistryAccess(DynamicRegistryManager dynamicRegistryManager, FeatureSet enabledFeatures) {
		this.dynamicRegistryManager = dynamicRegistryManager;
		this.enabledFeatures = enabledFeatures;
	}

	/**
	 * Sets the policy on how to handle unrecognized tags.
	 * 
	 * <p>See {@link CommandRegistryAccess.EntryListCreationPolicy} for the description of
	 * each policy.
	 */
	public void setEntryListCreationPolicy(CommandRegistryAccess.EntryListCreationPolicy entryListCreationPolicy) {
		this.entryListCreationPolicy = entryListCreationPolicy;
	}

	/**
	 * Creates a registry wrapper that follows the entry list creation policy.
	 * 
	 * @param registryRef the registry key of the registry to wrap
	 */
	public <T> CommandRegistryWrapper<T> createWrapper(RegistryKey<? extends Registry<T>> registryRef) {
		CommandRegistryWrapper.Impl<T> impl = new CommandRegistryWrapper.Impl<T>(this.dynamicRegistryManager.get(registryRef)) {
			@Override
			public Optional<RegistryEntryList.Named<T>> getEntryList(TagKey<T> tag) {
				return switch (CommandRegistryAccess.this.entryListCreationPolicy) {
					case FAIL -> this.registry.getEntryList(tag);
					case CREATE_NEW -> Optional.of(this.registry.getOrCreateEntryList(tag));
					case RETURN_EMPTY -> {
						Optional<RegistryEntryList.Named<T>> optional = this.registry.getEntryList(tag);
						yield Optional.of((RegistryEntryList.Named)optional.orElseGet(() -> RegistryEntryList.of(this.registry, tag)));
					}
				};
			}
		};
		return impl.withFeatureFilter(this.enabledFeatures);
	}

	/**
	 * A policy on how to handle a {@link net.minecraft.tag.TagKey} that does not resolve
	 * to an existing tag (unrecognized tag) in {@link CommandRegistryWrapper#getEntryList}.
	 */
	public static enum EntryListCreationPolicy {
		/**
		 * Creates a new {@link net.minecraft.util.registry.RegistryEntryList}, stores it and returns it.
		 */
		CREATE_NEW,
		/**
		 * Returns a new, empty {@link net.minecraft.util.registry.RegistryEntryList} every time.
		 */
		RETURN_EMPTY,
		/**
		 * Returns {@link java.util.Optional#empty()}.
		 */
		FAIL;
	}
}
