package net.minecraft.client.resource;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlDebugInfo;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class VideoWarningManager extends SinglePreparationResourceReloader<VideoWarningManager.WarningPatternLoader> {
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
		this.warnings.forEach((key, value) -> stringBuilder.append(key).append(": ").append(value));
		return stringBuilder.length() == 0 ? null : stringBuilder.toString();
	}

	protected VideoWarningManager.WarningPatternLoader prepare(ResourceManager resourceManager, Profiler profiler) {
		List<Pattern> list = Lists.<Pattern>newArrayList();
		List<Pattern> list2 = Lists.<Pattern>newArrayList();
		List<Pattern> list3 = Lists.<Pattern>newArrayList();
		profiler.startTick();
		JsonObject jsonObject = loadWarnlist(resourceManager, profiler);
		if (jsonObject != null) {
			profiler.push("compile_regex");
			compilePatterns(jsonObject.getAsJsonArray("renderer"), list);
			compilePatterns(jsonObject.getAsJsonArray("version"), list2);
			compilePatterns(jsonObject.getAsJsonArray("vendor"), list3);
			profiler.pop();
		}

		profiler.endTick();
		return new VideoWarningManager.WarningPatternLoader(list, list2, list3);
	}

	protected void apply(VideoWarningManager.WarningPatternLoader warningPatternLoader, ResourceManager resourceManager, Profiler profiler) {
		this.warnings = warningPatternLoader.buildWarnings();
	}

	private static void compilePatterns(JsonArray array, List<Pattern> patterns) {
		array.forEach(json -> patterns.add(Pattern.compile(json.getAsString(), 2)));
	}

	@Nullable
	private static JsonObject loadWarnlist(ResourceManager resourceManager, Profiler profiler) {
		profiler.push("parse_json");
		JsonObject jsonObject = null;

		try {
			Resource resource = resourceManager.getResource(GPU_WARNLIST_ID);

			try {
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));

				try {
					jsonObject = new JsonParser().parse(bufferedReader).getAsJsonObject();
				} catch (Throwable var9) {
					try {
						bufferedReader.close();
					} catch (Throwable var8) {
						var9.addSuppressed(var8);
					}

					throw var9;
				}

				bufferedReader.close();
			} catch (Throwable var10) {
				if (resource != null) {
					try {
						resource.close();
					} catch (Throwable var7) {
						var10.addSuppressed(var7);
					}
				}

				throw var10;
			}

			if (resource != null) {
				resource.close();
			}
		} catch (JsonSyntaxException | IOException var11) {
			LOGGER.warn("Failed to load GPU warnlist");
		}

		profiler.pop();
		return jsonObject;
	}

	@Environment(EnvType.CLIENT)
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
			List<String> list = Lists.<String>newArrayList();

			for (Pattern pattern : warningPattern) {
				Matcher matcher = pattern.matcher(info);

				while (matcher.find()) {
					list.add(matcher.group());
				}
			}

			return String.join(", ", list);
		}

		ImmutableMap<String, String> buildWarnings() {
			Builder<String, String> builder = new Builder<>();
			String string = buildWarning(this.rendererPatterns, GlDebugInfo.getRenderer());
			if (!string.isEmpty()) {
				builder.put("renderer", string);
			}

			String string2 = buildWarning(this.versionPatterns, GlDebugInfo.getVersion());
			if (!string2.isEmpty()) {
				builder.put("version", string2);
			}

			String string3 = buildWarning(this.vendorPatterns, GlDebugInfo.getVendor());
			if (!string3.isEmpty()) {
				builder.put("vendor", string3);
			}

			return builder.build();
		}
	}
}
