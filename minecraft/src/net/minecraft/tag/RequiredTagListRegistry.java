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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class RequiredTagListRegistry {
	private static final Set<RegistryKey<?>> REGISTRY_KEYS = Sets.<RegistryKey<?>>newHashSet();
	private static final List<RequiredTagList<?>> REQUIRED_TAG_LISTS = Lists.<RequiredTagList<?>>newArrayList();

	public static <T> RequiredTagList<T> register(RegistryKey<? extends Registry<T>> registryKey, String dataType) {
		if (!REGISTRY_KEYS.add(registryKey)) {
			throw new IllegalStateException("Duplicate entry for static tag collection: " + registryKey);
		} else {
			RequiredTagList<T> requiredTagList = new RequiredTagList<>(registryKey, dataType);
			REQUIRED_TAG_LISTS.add(requiredTagList);
			return requiredTagList;
		}
	}

	public static void updateTagManager(TagManager tagManager) {
		REQUIRED_TAG_LISTS.forEach(list -> list.updateTagManager(tagManager));
	}

	@Environment(EnvType.CLIENT)
	public static void clearAllTags() {
		REQUIRED_TAG_LISTS.forEach(RequiredTagList::clearAllTags);
	}

	public static Multimap<RegistryKey<? extends Registry<?>>, Identifier> getMissingTags(TagManager tagManager) {
		Multimap<RegistryKey<? extends Registry<?>>, Identifier> multimap = HashMultimap.create();
		REQUIRED_TAG_LISTS.forEach(requiredTagList -> multimap.putAll(requiredTagList.getRegistryKey(), requiredTagList.getMissingTags(tagManager)));
		return multimap;
	}

	public static void validateRegistrations() {
		method_33154();
	}

	private static Set<RequiredTagList<?>> getRequiredTags() {
		return ImmutableSet.of(BlockTags.REQUIRED_TAGS, ItemTags.REQUIRED_TAGS, FluidTags.REQUIRED_TAGS, EntityTypeTags.REQUIRED_TAGS, GameEventTags.REQUIRED_TAGS);
	}

	private static void method_33154() {
		Set<RegistryKey<?>> set = (Set<RegistryKey<?>>)getRequiredTags().stream().map(RequiredTagList::getRegistryKey).collect(Collectors.toSet());
		if (!Sets.difference(REGISTRY_KEYS, set).isEmpty()) {
			throw new IllegalStateException("Missing helper registrations");
		}
	}

	public static void forEach(Consumer<RequiredTagList<?>> consumer) {
		REQUIRED_TAG_LISTS.forEach(consumer);
	}

	public static TagManager method_33152() {
		TagManager.Builder builder = new TagManager.Builder();
		method_33154();
		REQUIRED_TAG_LISTS.forEach(requiredTagList -> requiredTagList.method_33147(builder));
		return builder.build();
	}
}
