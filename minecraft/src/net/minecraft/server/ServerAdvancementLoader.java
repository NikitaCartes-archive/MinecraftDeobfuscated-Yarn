package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementPositioner;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.text.Style;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerAdvancementLoader implements SynchronousResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder()
		.registerTypeHierarchyAdapter(Advancement.Task.class, (JsonDeserializer<Advancement.Task>)(jsonElement, type, jsonDeserializationContext) -> {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "advancement");
			return Advancement.Task.fromJson(jsonObject, jsonDeserializationContext);
		})
		.registerTypeAdapter(AdvancementRewards.class, new AdvancementRewards.Deserializer())
		.registerTypeHierarchyAdapter(TextComponent.class, new TextComponent.Serializer())
		.registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
		.registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory())
		.create();
	private static final AdvancementManager MANAGER = new AdvancementManager();
	public static final int PATH_PREFIX_LENGTH = "advancements/".length();
	public static final int FILE_EXTENSION_LENGTH = ".json".length();
	private boolean errored;

	private Map<Identifier, Advancement.Task> scanAdvancements(ResourceManager resourceManager) {
		Map<Identifier, Advancement.Task> map = Maps.<Identifier, Advancement.Task>newHashMap();

		for (Identifier identifier : resourceManager.findResources("advancements", stringx -> stringx.endsWith(".json"))) {
			String string = identifier.getPath();
			Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring(PATH_PREFIX_LENGTH, string.length() - FILE_EXTENSION_LENGTH));

			try {
				Resource resource = resourceManager.getResource(identifier);
				Throwable var8 = null;

				try {
					Advancement.Task task = JsonHelper.deserialize(GSON, IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8), Advancement.Task.class);
					if (task == null) {
						LOGGER.error("Couldn't load custom advancement {} from {} as it's empty or null", identifier2, identifier);
					} else {
						map.put(identifier2, task);
					}
				} catch (Throwable var19) {
					var8 = var19;
					throw var19;
				} finally {
					if (resource != null) {
						if (var8 != null) {
							try {
								resource.close();
							} catch (Throwable var18) {
								var8.addSuppressed(var18);
							}
						} else {
							resource.close();
						}
					}
				}
			} catch (IllegalArgumentException | InvalidIdentifierException | JsonParseException var21) {
				LOGGER.error("Parsing error loading custom advancement {}: {}", identifier2, var21.getMessage());
				this.errored = true;
			} catch (IOException var22) {
				LOGGER.error("Couldn't read custom advancement {} from {}", identifier2, identifier, var22);
				this.errored = true;
			}
		}

		return map;
	}

	@Nullable
	public Advancement get(Identifier identifier) {
		return MANAGER.get(identifier);
	}

	public Collection<Advancement> getAdvancements() {
		return MANAGER.getAdvancements();
	}

	@Override
	public void apply(ResourceManager resourceManager) {
		this.errored = false;
		MANAGER.clear();
		Map<Identifier, Advancement.Task> map = this.scanAdvancements(resourceManager);
		MANAGER.load(map);

		for (Advancement advancement : MANAGER.getRoots()) {
			if (advancement.getDisplay() != null) {
				AdvancementPositioner.method_852(advancement);
			}
		}
	}
}
