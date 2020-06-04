package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class class_5368 extends class_5375 {
	private static final Identifier field_25446 = new Identifier("textures/misc/unknown_pack.png");

	public class_5368(Screen screen, class_5359 arg, BiConsumer<class_5359, ResourcePackManager<ResourcePackProfile>> biConsumer, File file) {
		super(
			screen,
			new TranslatableText("dataPack.title"),
			new Function<Runnable, class_5369<?>>() {
				private class_5359 field_25450 = arg;
				private final ResourcePackManager<ResourcePackProfile> field_25451 = new ResourcePackManager<>(
					ResourcePackProfile::new, new VanillaDataPackProvider(), new FileResourcePackProvider(file, class_5352.field_25347)
				);

				public class_5369<?> apply(Runnable runnable) {
					this.field_25451.scanPacks();
					List<String> list = this.field_25450.method_29547();
					List<ResourcePackProfile> list2 = class_5368.method_29630(this.field_25451, list.stream());
					List<ResourcePackProfile> list3 = class_5368.method_29630(
						this.field_25451, this.field_25451.method_29206().stream().filter(string -> !list.contains(string))
					);
					return new class_5369(
						runnable,
						(resourcePackProfile, textureManager) -> textureManager.bindTexture(class_5368.field_25446),
						Lists.reverse(list2),
						list3,
						(listx, list2x, bl) -> {
							List<String> list3x = (List<String>)Lists.reverse(listx).stream().map(ResourcePackProfile::getName).collect(ImmutableList.toImmutableList());
							List<String> list4 = (List<String>)list2x.stream().map(ResourcePackProfile::getName).collect(ImmutableList.toImmutableList());
							this.field_25450 = new class_5359(list3x, list4);
							if (!bl) {
								this.field_25451.setEnabledProfiles(list3x);
								biConsumer.accept(this.field_25450, this.field_25451);
							}
						}
					);
				}
			},
			minecraftClient -> file
		);
	}

	private static List<ResourcePackProfile> method_29630(ResourcePackManager<ResourcePackProfile> resourcePackManager, Stream<String> stream) {
		return (List<ResourcePackProfile>)stream.map(resourcePackManager::getProfile).filter(Objects::nonNull).collect(ImmutableList.toImmutableList());
	}
}
