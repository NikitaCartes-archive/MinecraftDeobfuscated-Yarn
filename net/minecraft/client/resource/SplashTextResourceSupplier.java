/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.resource;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SplashTextResourceSupplier
extends SinglePreparationResourceReloader<List<String>> {
    private static final Identifier RESOURCE_ID = new Identifier("texts/splashes.txt");
    private static final Random RANDOM = new Random();
    private final List<String> splashTexts = Lists.newArrayList();
    private final Session session;

    public SplashTextResourceSupplier(Session session) {
        this.session = session;
    }

    /*
     * Enabled aggressive exception aggregation
     */
    @Override
    protected List<String> prepare(ResourceManager resourceManager, Profiler profiler) {
        try (Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(RESOURCE_ID);){
            List<String> list;
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));){
                list = bufferedReader.lines().map(String::trim).filter(string -> string.hashCode() != 125780783).collect(Collectors.toList());
            }
            return list;
        } catch (IOException iOException) {
            return Collections.emptyList();
        }
    }

    @Override
    protected void apply(List<String> list, ResourceManager resourceManager, Profiler profiler) {
        this.splashTexts.clear();
        this.splashTexts.addAll(list);
    }

    @Nullable
    public String get() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
            return "Merry X-mas!";
        }
        if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
            return "Happy new year!";
        }
        if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
            return "OOoooOOOoooo! Spooky!";
        }
        if (this.splashTexts.isEmpty()) {
            return null;
        }
        if (this.session != null && RANDOM.nextInt(this.splashTexts.size()) == 42) {
            return this.session.getUsername().toUpperCase(Locale.ROOT) + " IS YOU";
        }
        return this.splashTexts.get(RANDOM.nextInt(this.splashTexts.size()));
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
        return this.prepare(manager, profiler);
    }
}

