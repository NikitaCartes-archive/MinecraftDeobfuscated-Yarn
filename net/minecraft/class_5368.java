/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.class_5352;
import net.minecraft.class_5359;
import net.minecraft.class_5369;
import net.minecraft.class_5375;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class class_5368
extends class_5375 {
    private static final Identifier field_25446 = new Identifier("textures/misc/unknown_pack.png");

    public class_5368(Screen screen, final class_5359 arg, final BiConsumer<class_5359, ResourcePackManager<ResourcePackProfile>> biConsumer, final File file) {
        super(screen, new TranslatableText("dataPack.title"), new Function<Runnable, class_5369<?>>(){
            private class_5359 field_25450;
            private final ResourcePackManager<ResourcePackProfile> field_25451;
            {
                this.field_25450 = arg;
                this.field_25451 = new ResourcePackManager<ResourcePackProfile>(ResourcePackProfile::new, new VanillaDataPackProvider(), new FileResourcePackProvider(file, class_5352.field_25347));
            }

            @Override
            public class_5369<?> apply(Runnable runnable) {
                this.field_25451.scanPacks();
                List<String> list3 = this.field_25450.method_29547();
                List list22 = class_5368.method_29630(this.field_25451, list3.stream());
                List list32 = class_5368.method_29630(this.field_25451, this.field_25451.method_29206().stream().filter(string -> !list3.contains(string)));
                return new class_5369<ResourcePackProfile>(runnable, (resourcePackProfile, textureManager) -> textureManager.bindTexture(field_25446), Lists.reverse(list22), list32, (list, list2, bl) -> {
                    List list3 = Lists.reverse(list).stream().map(ResourcePackProfile::getName).collect(ImmutableList.toImmutableList());
                    List list4 = list2.stream().map(ResourcePackProfile::getName).collect(ImmutableList.toImmutableList());
                    this.field_25450 = new class_5359(list3, list4);
                    if (!bl) {
                        this.field_25451.setEnabledProfiles(list3);
                        biConsumer.accept(this.field_25450, this.field_25451);
                    }
                });
            }

            @Override
            public /* synthetic */ Object apply(Object object) {
                return this.apply((Runnable)object);
            }
        }, (MinecraftClient minecraftClient) -> file);
    }

    private static List<ResourcePackProfile> method_29630(ResourcePackManager<ResourcePackProfile> resourcePackManager, Stream<String> stream) {
        return stream.map(resourcePackManager::getProfile).filter(Objects::nonNull).collect(ImmutableList.toImmutableList());
    }
}

