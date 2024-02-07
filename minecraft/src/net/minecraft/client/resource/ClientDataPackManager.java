package net.minecraft.client.resource;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.VersionedIdentifier;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackInfo;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.VanillaDataPackProvider;

@Environment(EnvType.CLIENT)
public class ClientDataPackManager {
	private final ResourcePackManager packManager = VanillaDataPackProvider.createClientManager();
	private final Map<VersionedIdentifier, String> knownPacks;

	public ClientDataPackManager() {
		this.packManager.scanPacks();
		Builder<VersionedIdentifier, String> builder = ImmutableMap.builder();
		this.packManager.getProfiles().forEach(resourcePackProfile -> {
			ResourcePackInfo resourcePackInfo = resourcePackProfile.getInfo();
			resourcePackInfo.knownPackInfo().ifPresent(knownPackInfo -> builder.put(knownPackInfo, resourcePackInfo.id()));
		});
		this.knownPacks = builder.build();
	}

	public List<VersionedIdentifier> getCommonKnownPacks(List<VersionedIdentifier> serverKnownPacks) {
		List<VersionedIdentifier> list = new ArrayList(serverKnownPacks.size());
		List<String> list2 = new ArrayList(serverKnownPacks.size());

		for (VersionedIdentifier versionedIdentifier : serverKnownPacks) {
			String string = (String)this.knownPacks.get(versionedIdentifier);
			if (string != null) {
				list2.add(string);
				list.add(versionedIdentifier);
			}
		}

		this.packManager.setEnabledProfiles(list2);
		return list;
	}

	public LifecycledResourceManager createResourceManager() {
		List<ResourcePack> list = this.packManager.createResourcePacks();
		return new LifecycledResourceManagerImpl(ResourceType.SERVER_DATA, list);
	}
}
