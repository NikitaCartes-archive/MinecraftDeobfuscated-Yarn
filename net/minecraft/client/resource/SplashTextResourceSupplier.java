/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.resource;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SplashTextResourceSupplier
extends SinglePreparationResourceReloader<List<String>> {
    private static final Identifier RESOURCE_ID = new Identifier("texts/splashes.txt");
    private static final Random RANDOM = Random.create();
    private final List<String> splashTexts = Lists.newArrayList();
    private final Session session;

    public SplashTextResourceSupplier(Session session) {
        this.session = session;
    }

    @Override
    protected List<String> prepare(ResourceManager resourceManager, Profiler profiler) {
        List<String> list;
        block8: {
            BufferedReader bufferedReader = MinecraftClient.getInstance().getResourceManager().openAsReader(RESOURCE_ID);
            try {
                list = bufferedReader.lines().map(String::trim).filter(splashText -> splashText.hashCode() != 125780783).collect(Collectors.toList());
                if (bufferedReader == null) break block8;
            } catch (Throwable throwable) {
                try {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                } catch (IOException iOException) {
                    return Collections.emptyList();
                }
            }
            bufferedReader.close();
        }
        return list;
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

