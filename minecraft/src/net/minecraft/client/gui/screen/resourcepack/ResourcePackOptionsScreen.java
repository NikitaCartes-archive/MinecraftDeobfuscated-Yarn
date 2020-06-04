package net.minecraft.client.gui.screen.resourcepack;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5369;
import net.minecraft.class_5375;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.ClientResourcePackProfile;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class ResourcePackOptionsScreen extends class_5375 {
	public ResourcePackOptionsScreen(Screen parent, GameOptions gameOptions, ResourcePackManager<ClientResourcePackProfile> resourcePackManager, Runnable runnable) {
		super(parent, new TranslatableText("resourcePack.title"), runnable2 -> {
			resourcePackManager.scanPacks();
			List<ClientResourcePackProfile> list = Lists.<ClientResourcePackProfile>newArrayList(resourcePackManager.getEnabledProfiles());
			List<ClientResourcePackProfile> list2 = Lists.<ClientResourcePackProfile>newArrayList(resourcePackManager.getProfiles());
			list2.removeAll(list);
			return new class_5369(runnable2, ClientResourcePackProfile::drawIcon, Lists.reverse(list), list2, (listx, list2x, bl) -> {
				List<ClientResourcePackProfile> list3 = Lists.reverse(listx);
				List<String> list4 = (List<String>)list3.stream().map(ResourcePackProfile::getName).collect(ImmutableList.toImmutableList());
				resourcePackManager.setEnabledProfiles(list4);
				gameOptions.resourcePacks.clear();
				gameOptions.incompatibleResourcePacks.clear();

				for (ClientResourcePackProfile clientResourcePackProfile : list3) {
					if (!clientResourcePackProfile.isPinned()) {
						gameOptions.resourcePacks.add(clientResourcePackProfile.getName());
						if (!clientResourcePackProfile.getCompatibility().isCompatible()) {
							gameOptions.incompatibleResourcePacks.add(clientResourcePackProfile.getName());
						}
					}
				}

				gameOptions.write();
				if (!bl) {
					runnable.run();
				}
			});
		}, MinecraftClient::getResourcePackDir);
	}
}
