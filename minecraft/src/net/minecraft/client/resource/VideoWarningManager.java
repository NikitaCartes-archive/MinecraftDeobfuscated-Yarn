package net.minecraft.client.resource;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlDebugInfo;
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
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class VideoWarningManager extends SinglePreparationResourceReloadListener<VideoWarningManager.WarningPatternLoader> {
	private static final Logger LOGGER = LogManager.getLogger();
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
	public String method_30920() {
		StringBuilder stringBuilder = new StringBuilder();
		this.warnings.forEach((string, string2) -> stringBuilder.append(string).append(": ").append(string2));
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
		array.forEach(jsonElement -> patterns.add(Pattern.compile(jsonElement.getAsString(), 2)));
	}

	@Nullable
	private static JsonObject loadWarnlist(ResourceManager resourceManager, Profiler profiler) {
		profiler.push("parse_json");
		JsonObject jsonObject = null;

		try {
			Resource resource = resourceManager.getResource(GPU_WARNLIST_ID);
			Throwable var4 = null;

			try {
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
				Throwable var6 = null;

				try {
					jsonObject = new JsonParser().parse(bufferedReader).getAsJsonObject();
				} catch (Throwable var31) {
					var6 = var31;
					throw var31;
				} finally {
					if (bufferedReader != null) {
						if (var6 != null) {
							try {
								bufferedReader.close();
							} catch (Throwable var30) {
								var6.addSuppressed(var30);
							}
						} else {
							bufferedReader.close();
						}
					}
				}
			} catch (Throwable var33) {
				var4 = var33;
				throw var33;
			} finally {
				if (resource != null) {
					if (var4 != null) {
						try {
							resource.close();
						} catch (Throwable var29) {
							var4.addSuppressed(var29);
						}
					} else {
						resource.close();
					}
				}
			}
		} catch (JsonSyntaxException | IOException var35) {
			LOGGER.warn("Failed to load GPU warnlist");
		}

		profiler.pop();
		return jsonObject;
	}

	@Environment(EnvType.CLIENT)
	public static final class WarningPatternLoader {
		private final List<Pattern> rendererPatterns;
		private final List<Pattern> versionPatterns;
		private final List<Pattern> vendorPatterns;

		private WarningPatternLoader(List<Pattern> rendererPatterns, List<Pattern> versionPatterns, List<Pattern> vendorPatterns) {
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

		private ImmutableMap<String, String> buildWarnings() {
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
