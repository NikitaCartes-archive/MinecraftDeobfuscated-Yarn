package net.minecraft.tag;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagManager {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final TagManager EMPTY = new TagManager(ImmutableMap.of());
	private final Map<RegistryKey<? extends Registry<?>>, TagGroup<?>> tagGroups;

	private TagManager(Map<RegistryKey<? extends Registry<?>>, TagGroup<?>> tagGroups) {
		this.tagGroups = tagGroups;
	}

	@Nullable
	private <T> TagGroup<T> getTagGroup(RegistryKey<? extends Registry<T>> registryKey) {
		return (TagGroup<T>)this.tagGroups.get(registryKey);
	}

	public <T> TagGroup<T> getOrCreateTagGroup(RegistryKey<? extends Registry<T>> registryKey) {
		return (TagGroup<T>)this.tagGroups.getOrDefault(registryKey, TagGroup.createEmpty());
	}

	public <T, E extends Exception> Tag<T> getTag(RegistryKey<? extends Registry<T>> registryKey, Identifier id, Function<Identifier, E> function) throws E {
		TagGroup<T> tagGroup = this.getTagGroup(registryKey);
		if (tagGroup == null) {
			throw (Exception)function.apply(id);
		} else {
			Tag<T> tag = tagGroup.getTag(id);
			if (tag == null) {
				throw (Exception)function.apply(id);
			} else {
				return tag;
			}
		}
	}

	public <T, E extends Exception> Identifier getTagId(RegistryKey<? extends Registry<T>> registryKey, Tag<T> tag, Supplier<E> supplier) throws E {
		TagGroup<T> tagGroup = this.getTagGroup(registryKey);
		if (tagGroup == null) {
			throw (Exception)supplier.get();
		} else {
			Identifier identifier = tagGroup.getUncheckedTagId(tag);
			if (identifier == null) {
				throw (Exception)supplier.get();
			} else {
				return identifier;
			}
		}
	}

	public void method_33161(TagManager.class_5750 arg) {
		this.tagGroups.forEach((registryKey, tagGroup) -> method_33162(arg, registryKey, tagGroup));
	}

	private static <T> void method_33162(TagManager.class_5750 arg, RegistryKey<? extends Registry<?>> registryKey, TagGroup<?> tagGroup) {
		arg.method_33173(registryKey, tagGroup);
	}

	public void apply() {
		RequiredTagListRegistry.updateTagManager(this);
		Blocks.refreshShapeCache();
	}

	public Map<RegistryKey<? extends Registry<?>>, TagGroup.class_5748> toPacket(DynamicRegistryManager dynamicRegistryManager) {
		final Map<RegistryKey<? extends Registry<?>>, TagGroup.class_5748> map = Maps.<RegistryKey<? extends Registry<?>>, TagGroup.class_5748>newHashMap();
		this.method_33161(new TagManager.class_5750() {
			@Override
			public <T> void method_33173(RegistryKey<? extends Registry<T>> registryKey, TagGroup<T> tagGroup) {
				Optional<? extends Registry<T>> optional = dynamicRegistryManager.getOptional(registryKey);
				if (optional.isPresent()) {
					map.put(registryKey, tagGroup.toPacket((Registry<T>)optional.get()));
				} else {
					TagManager.LOGGER.error("Unknown registry {}", registryKey);
				}
			}
		});
		return map;
	}

	@Environment(EnvType.CLIENT)
	public static TagManager fromPacket(DynamicRegistryManager dynamicRegistryManager, Map<RegistryKey<? extends Registry<?>>, TagGroup.class_5748> map) {
		TagManager.class_5749 lv = new TagManager.class_5749();
		map.forEach((registryKey, arg2) -> method_33163(dynamicRegistryManager, lv, registryKey, arg2));
		return lv.method_33171();
	}

	@Environment(EnvType.CLIENT)
	private static <T> void method_33163(
		DynamicRegistryManager dynamicRegistryManager, TagManager.class_5749 arg, RegistryKey<? extends Registry<? extends T>> registryKey, TagGroup.class_5748 arg2
	) {
		Optional<? extends Registry<? extends T>> optional = dynamicRegistryManager.getOptional(registryKey);
		if (optional.isPresent()) {
			arg.method_33172(registryKey, TagGroup.method_33155(arg2, (Registry<? extends T>)optional.get()));
		} else {
			LOGGER.error("Unknown registry {}", registryKey);
		}
	}

	public static class class_5749 {
		private final Builder<RegistryKey<? extends Registry<?>>, TagGroup<?>> field_28310 = ImmutableMap.builder();

		public <T> TagManager.class_5749 method_33172(RegistryKey<? extends Registry<? extends T>> registryKey, TagGroup<T> tagGroup) {
			this.field_28310.put(registryKey, tagGroup);
			return this;
		}

		public TagManager method_33171() {
			return new TagManager(this.field_28310.build());
		}
	}

	@FunctionalInterface
	interface class_5750 {
		<T> void method_33173(RegistryKey<? extends Registry<T>> registryKey, TagGroup<T> tagGroup);
	}
}
