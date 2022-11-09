/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
    public <T> RegistryWrapper<T> createWrapper(RegistryKey<? extends Registry<T>> var1);

    public static CommandRegistryAccess of(final RegistryWrapper.WrapperLookup wrapperLookup, final FeatureSet enabledFeatures) {
        return new CommandRegistryAccess(){

            @Override
            public <T> RegistryWrapper<T> createWrapper(RegistryKey<? extends Registry<T>> registryRef) {
                return wrapperLookup.getWrapperOrThrow(registryRef).withFeatureFilter(enabledFeatures);
            }
        };
    }

    public static EntryListCreationPolicySettable of(final DynamicRegistryManager registryManager, final FeatureSet enabledFeatures) {
        return new EntryListCreationPolicySettable(){
            EntryListCreationPolicy entryListCreationPolicy = EntryListCreationPolicy.FAIL;

            @Override
            public void setEntryListCreationPolicy(EntryListCreationPolicy entryListCreationPolicy) {
                this.entryListCreationPolicy = entryListCreationPolicy;
            }

            @Override
            public <T> RegistryWrapper<T> createWrapper(RegistryKey<? extends Registry<T>> registryRef) {
                Registry registry = registryManager.get(registryRef);
                final RegistryWrapper.Impl impl = registry.getReadOnlyWrapper();
                final RegistryWrapper.Impl impl2 = registry.getTagCreatingWrapper();
                RegistryWrapper.Impl.Delegating impl3 = new RegistryWrapper.Impl.Delegating<T>(){

                    @Override
                    protected RegistryWrapper.Impl<T> getBase() {
                        return switch (entryListCreationPolicy) {
                            default -> throw new IncompatibleClassChangeError();
                            case EntryListCreationPolicy.FAIL -> impl;
                            case EntryListCreationPolicy.CREATE_NEW -> impl2;
                        };
                    }
                };
                return impl3.withFeatureFilter(enabledFeatures);
            }
        };
    }

    public static interface EntryListCreationPolicySettable
    extends CommandRegistryAccess {
        public void setEntryListCreationPolicy(EntryListCreationPolicy var1);
    }

    public static enum EntryListCreationPolicy {
        CREATE_NEW,
        FAIL;

    }
}

