package net.minecraft.resource;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Util;

/**
 * A resource pack manager manages a list of {@link ResourcePackProfile}s and
 * builds {@linkplain #createResourcePacks() a list of resource packs} when the
 * resource manager reloads.
 */
public class ResourcePackManager {
	private final Set<ResourcePackProvider> providers;
	private Map<String, ResourcePackProfile> profiles = ImmutableMap.of();
	private List<ResourcePackProfile> enabled = ImmutableList.of();

	public ResourcePackManager(ResourcePackProvider... providers) {
		this.providers = ImmutableSet.copyOf(providers);
	}

	public void scanPacks() {
		List<String> list = (List<String>)this.enabled.stream().map(ResourcePackProfile::getId).collect(ImmutableList.toImmutableList());
		this.profiles = this.providePackProfiles();
		this.enabled = this.buildEnabledProfiles(list);
	}

	private Map<String, ResourcePackProfile> providePackProfiles() {
		Map<String, ResourcePackProfile> map = Maps.newTreeMap();

		for (ResourcePackProvider resourcePackProvider : this.providers) {
			resourcePackProvider.register(profile -> map.put(profile.getId(), profile));
		}

		return ImmutableMap.copyOf(map);
	}

	public void setEnabledProfiles(Collection<String> enabled) {
		this.enabled = this.buildEnabledProfiles(enabled);
	}

	public boolean enable(String profile) {
		ResourcePackProfile resourcePackProfile = (ResourcePackProfile)this.profiles.get(profile);
		if (resourcePackProfile != null && !this.enabled.contains(resourcePackProfile)) {
			List<ResourcePackProfile> list = Lists.<ResourcePackProfile>newArrayList(this.enabled);
			list.add(resourcePackProfile);
			this.enabled = list;
			return true;
		} else {
			return false;
		}
	}

	public boolean disable(String profile) {
		ResourcePackProfile resourcePackProfile = (ResourcePackProfile)this.profiles.get(profile);
		if (resourcePackProfile != null && this.enabled.contains(resourcePackProfile)) {
			List<ResourcePackProfile> list = Lists.<ResourcePackProfile>newArrayList(this.enabled);
			list.remove(resourcePackProfile);
			this.enabled = list;
			return true;
		} else {
			return false;
		}
	}

	private List<ResourcePackProfile> buildEnabledProfiles(Collection<String> enabledNames) {
		List<ResourcePackProfile> list = (List<ResourcePackProfile>)this.streamProfilesById(enabledNames).collect(Util.toArrayList());

		for (ResourcePackProfile resourcePackProfile : this.profiles.values()) {
			if (resourcePackProfile.isRequired() && !list.contains(resourcePackProfile)) {
				resourcePackProfile.getInitialPosition().insert(list, resourcePackProfile, ResourcePackProfile::getPosition, false);
			}
		}

		return ImmutableList.copyOf(list);
	}

	private Stream<ResourcePackProfile> streamProfilesById(Collection<String> ids) {
		return ids.stream().map(this.profiles::get).filter(Objects::nonNull);
	}

	public Collection<String> getIds() {
		return this.profiles.keySet();
	}

	public Collection<ResourcePackProfile> getProfiles() {
		return this.profiles.values();
	}

	public Collection<String> getEnabledIds() {
		return (Collection<String>)this.enabled.stream().map(ResourcePackProfile::getId).collect(ImmutableSet.toImmutableSet());
	}

	public FeatureSet getRequestedFeatures() {
		return (FeatureSet)this.getEnabledProfiles().stream().map(ResourcePackProfile::getRequestedFeatures).reduce(FeatureSet::combine).orElse(FeatureSet.empty());
	}

	public Collection<ResourcePackProfile> getEnabledProfiles() {
		return this.enabled;
	}

	@Nullable
	public ResourcePackProfile getProfile(String id) {
		return (ResourcePackProfile)this.profiles.get(id);
	}

	public boolean hasProfile(String id) {
		return this.profiles.containsKey(id);
	}

	public List<ResourcePack> createResourcePacks() {
		return (List<ResourcePack>)this.enabled.stream().map(ResourcePackProfile::createResourcePack).collect(ImmutableList.toImmutableList());
	}
}
