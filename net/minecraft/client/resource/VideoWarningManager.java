/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.resource;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlDebugInfo;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class VideoWarningManager
extends SinglePreparationResourceReloader<WarningPatternLoader> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Identifier GPU_WARNLIST_ID = new Identifier("gpu_warnlist.json");
    private ImmutableMap<String, String> warnings = ImmutableMap.of();
    private boolean warningScheduled;
    private boolean warned;
    private boolean cancelledAfterWarning;

    public boolean hasWarning() {
        return !this.warnings.isEmpty();
    }

    public boolean canWarn() {
        return this.hasWarning() && !this.warned;
    }

    public void scheduleWarning() {
        this.warningScheduled = true;
    }

    public void acceptAfterWarnings() {
        this.warned = true;
    }

    public void cancelAfterWarnings() {
        this.warned = true;
        this.cancelledAfterWarning = true;
    }

    public boolean shouldWarn() {
        return this.warningScheduled && !this.warned;
    }

    public boolean hasCancelledAfterWarning() {
        return this.cancelledAfterWarning;
    }

    public void reset() {
        this.warningScheduled = false;
        this.warned = false;
        this.cancelledAfterWarning = false;
    }

    @Nullable
    public String getRendererWarning() {
        return this.warnings.get("renderer");
    }

    @Nullable
    public String getVersionWarning() {
        return this.warnings.get("version");
    }

    @Nullable
    public String getVendorWarning() {
        return this.warnings.get("vendor");
    }

    @Nullable
    public String getWarningsAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        this.warnings.forEach((key, value) -> stringBuilder.append((String)key).append(": ").append((String)value));
        return stringBuilder.length() == 0 ? null : stringBuilder.toString();
    }

    @Override
    protected WarningPatternLoader prepare(ResourceManager resourceManager, Profiler profiler) {
        ArrayList<Pattern> list = Lists.newArrayList();
        ArrayList<Pattern> list2 = Lists.newArrayList();
        ArrayList<Pattern> list3 = Lists.newArrayList();
        profiler.startTick();
        JsonObject jsonObject = VideoWarningManager.loadWarnlist(resourceManager, profiler);
        if (jsonObject != null) {
            profiler.push("compile_regex");
            VideoWarningManager.compilePatterns(jsonObject.getAsJsonArray("renderer"), list);
            VideoWarningManager.compilePatterns(jsonObject.getAsJsonArray("version"), list2);
            VideoWarningManager.compilePatterns(jsonObject.getAsJsonArray("vendor"), list3);
            profiler.pop();
        }
        profiler.endTick();
        return new WarningPatternLoader(list, list2, list3);
    }

    @Override
    protected void apply(WarningPatternLoader warningPatternLoader, ResourceManager resourceManager, Profiler profiler) {
        this.warnings = warningPatternLoader.buildWarnings();
    }

    private static void compilePatterns(JsonArray array, List<Pattern> patterns) {
        array.forEach(json -> patterns.add(Pattern.compile(json.getAsString(), 2)));
    }

    @Nullable
    private static JsonObject loadWarnlist(ResourceManager resourceManager, Profiler profiler) {
        profiler.push("parse_json");
        JsonObject jsonObject = null;
        try (BufferedReader reader = resourceManager.openAsReader(GPU_WARNLIST_ID);){
            jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (JsonSyntaxException | IOException exception) {
            LOGGER.warn("Failed to load GPU warnlist");
        }
        profiler.pop();
        return jsonObject;
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
        return this.prepare(manager, profiler);
    }

    @Environment(value=EnvType.CLIENT)
    protected static final class WarningPatternLoader {
        private final List<Pattern> rendererPatterns;
        private final List<Pattern> versionPatterns;
        private final List<Pattern> vendorPatterns;

        WarningPatternLoader(List<Pattern> rendererPatterns, List<Pattern> versionPatterns, List<Pattern> vendorPatterns) {
            this.rendererPatterns = rendererPatterns;
            this.versionPatterns = versionPatterns;
            this.vendorPatterns = vendorPatterns;
        }

        private static String buildWarning(List<Pattern> warningPattern, String info) {
            ArrayList<String> list = Lists.newArrayList();
            for (Pattern pattern : warningPattern) {
                Matcher matcher = pattern.matcher(info);
                while (matcher.find()) {
                    list.add(matcher.group());
                }
            }
            return String.join((CharSequence)", ", list);
        }

        ImmutableMap<String, String> buildWarnings() {
            String string3;
            String string2;
            ImmutableMap.Builder<String, String> builder = new ImmutableMap.Builder<String, String>();
            String string = WarningPatternLoader.buildWarning(this.rendererPatterns, GlDebugInfo.getRenderer());
            if (!string.isEmpty()) {
                builder.put("renderer", string);
            }
            if (!(string2 = WarningPatternLoader.buildWarning(this.versionPatterns, GlDebugInfo.getVersion())).isEmpty()) {
                builder.put("version", string2);
            }
            if (!(string3 = WarningPatternLoader.buildWarning(this.vendorPatterns, GlDebugInfo.getVendor())).isEmpty()) {
                builder.put("vendor", string3);
            }
            return builder.build();
        }
    }
}

