/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command;

import java.util.Optional;
import net.minecraft.command.CommandRegistryWrapper;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
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
    EntryListCreationPolicy entryListCreationPolicy = EntryListCreationPolicy.FAIL;

    public CommandRegistryAccess(DynamicRegistryManager dynamicRegistryManager) {
        this.dynamicRegistryManager = dynamicRegistryManager;
    }

    /**
     * Sets the policy on how to handle unrecognized tags.
     * 
     * <p>See {@link CommandRegistryAccess.EntryListCreationPolicy} for the description of
     * each policy.
     */
    public void setEntryListCreationPolicy(EntryListCreationPolicy entryListCreationPolicy) {
        this.entryListCreationPolicy = entryListCreationPolicy;
    }

    /**
     * Creates a registry wrapper that follows the entry list creation policy.
     * 
     * @param registryRef the registry key of the registry to wrap
     */
    public <T> CommandRegistryWrapper<T> createWrapper(RegistryKey<? extends Registry<T>> registryRef) {
        return new CommandRegistryWrapper.Impl<T>(this.dynamicRegistryManager.get(registryRef)){

            @Override
            public Optional<? extends RegistryEntryList<T>> getEntryList(TagKey<T> tag) {
                return switch (CommandRegistryAccess.this.entryListCreationPolicy) {
                    default -> throw new IncompatibleClassChangeError();
                    case EntryListCreationPolicy.FAIL -> this.registry.getEntryList(tag);
                    case EntryListCreationPolicy.CREATE_NEW -> Optional.of(this.registry.getOrCreateEntryList(tag));
                    case EntryListCreationPolicy.RETURN_EMPTY -> {
                        Optional optional = this.registry.getEntryList(tag);
                        yield Optional.of(optional.isPresent() ? (RegistryEntryList.Direct)((Object)optional.get()) : RegistryEntryList.of(new RegistryEntry[0]));
                    }
                };
            }
        };
    }

    public static enum EntryListCreationPolicy {
        CREATE_NEW,
        RETURN_EMPTY,
        FAIL;

    }
}

