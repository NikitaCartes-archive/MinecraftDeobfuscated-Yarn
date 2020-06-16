/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlDebugInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class class_5407
extends SinglePreparationResourceReloadListener<class_5408> {
    private static final Logger field_25716 = LogManager.getLogger();
    private static final Identifier field_25689 = new Identifier("gpu_warnlist.json");
    private ImmutableMap<String, String> field_25690 = ImmutableMap.of();
    private boolean field_25717;
    private boolean field_25718;
    private boolean field_25719;

    public boolean method_30055() {
        return !this.field_25690.isEmpty();
    }

    public boolean method_30137() {
        return this.method_30055() && !this.field_25718;
    }

    public void method_30138() {
        this.field_25717 = true;
    }

    public void method_30139() {
        this.field_25718 = true;
    }

    public void method_30140() {
        this.field_25718 = true;
        this.field_25719 = true;
    }

    public boolean method_30141() {
        return this.field_25717 && !this.field_25718;
    }

    public boolean method_30142() {
        return this.field_25719;
    }

    public void method_30143() {
        this.field_25717 = false;
        this.field_25718 = false;
        this.field_25719 = false;
    }

    @Nullable
    public String method_30060() {
        return this.field_25690.get("renderer");
    }

    @Nullable
    public String method_30062() {
        return this.field_25690.get("version");
    }

    @Nullable
    public String method_30063() {
        return this.field_25690.get("vendor");
    }

    @Override
    protected class_5408 prepare(ResourceManager resourceManager, Profiler profiler) {
        ArrayList<Pattern> list = Lists.newArrayList();
        ArrayList<Pattern> list2 = Lists.newArrayList();
        ArrayList<Pattern> list3 = Lists.newArrayList();
        profiler.startTick();
        JsonObject jsonObject = class_5407.method_30061(resourceManager, profiler);
        if (jsonObject != null) {
            profiler.push("compile_regex");
            class_5407.method_30057(jsonObject.getAsJsonArray("renderer"), list);
            class_5407.method_30057(jsonObject.getAsJsonArray("version"), list2);
            class_5407.method_30057(jsonObject.getAsJsonArray("vendor"), list3);
            profiler.pop();
        }
        profiler.endTick();
        return new class_5408(list, list2, list3);
    }

    @Override
    protected void apply(class_5408 arg, ResourceManager resourceManager, Profiler profiler) {
        this.field_25690 = arg.method_30064();
    }

    private static void method_30057(JsonArray jsonArray, List<Pattern> list) {
        jsonArray.forEach(jsonElement -> list.add(Pattern.compile(jsonElement.getAsString(), 2)));
    }

    @Nullable
    private static JsonObject method_30061(ResourceManager resourceManager, Profiler profiler) {
        profiler.push("parse_json");
        JsonObject jsonObject = null;
        try (Resource resource = resourceManager.getResource(field_25689);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));){
            jsonObject = new JsonParser().parse(bufferedReader).getAsJsonObject();
        } catch (JsonSyntaxException | IOException exception) {
            field_25716.warn("Failed to load GPU warnlist");
        }
        profiler.pop();
        return jsonObject;
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
        return this.prepare(manager, profiler);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class class_5408 {
        private final List<Pattern> field_25691;
        private final List<Pattern> field_25692;
        private final List<Pattern> field_25693;

        private class_5408(List<Pattern> list, List<Pattern> list2, List<Pattern> list3) {
            this.field_25691 = list;
            this.field_25692 = list2;
            this.field_25693 = list3;
        }

        private static String method_30066(List<Pattern> list, String string) {
            ArrayList<String> list2 = Lists.newArrayList();
            for (Pattern pattern : list) {
                Matcher matcher = pattern.matcher(string);
                while (matcher.find()) {
                    list2.add(matcher.group());
                }
            }
            return String.join((CharSequence)", ", list2);
        }

        private ImmutableMap<String, String> method_30064() {
            String string3;
            String string2;
            ImmutableMap.Builder<String, String> builder = new ImmutableMap.Builder<String, String>();
            String string = class_5408.method_30066(this.field_25691, GlDebugInfo.getRenderer());
            if (!string.isEmpty()) {
                builder.put("renderer", string);
            }
            if (!(string2 = class_5408.method_30066(this.field_25692, GlDebugInfo.getVersion())).isEmpty()) {
                builder.put("version", string2);
            }
            if (!(string3 = class_5408.method_30066(this.field_25693, GlDebugInfo.getVendor())).isEmpty()) {
                builder.put("vendor", string3);
            }
            return builder.build();
        }
    }
}

