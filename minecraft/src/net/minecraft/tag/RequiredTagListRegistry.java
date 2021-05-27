package net.minecraft.tag;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class RequiredTagListRegistry {
	private static final Set<RegistryKey<?>> REQUIRED_LIST_KEYS = Sets.<RegistryKey<?>>newHashSet();
	private static final List<RequiredTagList<?>> ALL = Lists.<RequiredTagList<?>>newArrayList();

	/**
	 * Creates and registers a required tag list.
	 * 
	 * <p>The registered tag must be in the set returned by {@link #getBuiltinTags()},
	 * or the registry will fail {@linkplain #validate() validation}.
	 * 
	 * @return the created and registered list
	 * @throws IllegalStateException if there is a list with a duplicate {@code registryKey}
	 * 
	 * @param registryKey the key representing the element type of the tags
	 * @param dataType the data type, or ID's path prefix, for the tag JSONs in the data pack
	 */
	public static <T> RequiredTagList<T> register(RegistryKey<? extends Registry<T>> registryKey, String dataType) {
		if (!REQUIRED_LIST_KEYS.add(registryKey)) {
			throw new IllegalStateException("Duplicate entry for static tag collection: " + registryKey);
		} else {
			RequiredTagList<T> requiredTagList = new RequiredTagList<>(registryKey, dataType);
			ALL.add(requiredTagList);
			return requiredTagList;
		}
	}

	public static void updateTagManager(TagManager tagManager) {
		ALL.forEach(list -> list.updateTagManager(tagManager));
	}

	public static void clearAllTags() {
		ALL.forEach(RequiredTagList::clearAllTags);
	}

	public static Multimap<RegistryKey<? extends Registry<?>>, Identifier> getMissingTags(TagManager tagManager) {
		Multimap<RegistryKey<? extends Registry<?>>, Identifier> multimap = HashMultimap.create();
		ALL.forEach(list -> multimap.putAll(list.getRegistryKey(), list.getMissingTags(tagManager)));
		return multimap;
	}

	public static void validateRegistrations() {
		validate();
	}

	private static Set<RequiredTagList<?>> getBuiltinTags() {
		return ImmutableSet.of(BlockTags.REQUIRED_TAGS, ItemTags.REQUIRED_TAGS, FluidTags.REQUIRED_TAGS, EntityTypeTags.REQUIRED_TAGS, GameEventTags.REQUIRED_TAGS);
	}

	/**
	 * Ensures that each key in {@link #REQUIRED_LIST_KEYS} have a corresponding list
	 * in the return value of {@link #getBuiltinTags()}.
	 * 
	 * @throws IllegalStateException when the validation fails
	 */
	private static void validate() {
		Set<RegistryKey<?>> set = (Set<RegistryKey<?>>)getBuiltinTags().stream().map(RequiredTagList::getRegistryKey).collect(Collectors.toSet());
		if (!Sets.difference(REQUIRED_LIST_KEYS, set).isEmpty()) {
			throw new IllegalStateException("Missing helper registrations");
		}
	}

	public static void forEach(Consumer<RequiredTagList<?>> consumer) {
		ALL.forEach(consumer);
	}

	public static TagManager createBuiltinTagManager() {
		TagManager.Builder builder = new TagManager.Builder();
		validate();
		ALL.forEach(list -> list.addToManager(builder));
		return builder.build();
	}
}
