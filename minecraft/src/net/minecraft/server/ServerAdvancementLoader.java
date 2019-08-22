package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementPositioner;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerAdvancementLoader extends JsonDataLoader {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = new GsonBuilder()
		.registerTypeHierarchyAdapter(Advancement.Task.class, (JsonDeserializer<Advancement.Task>)(jsonElement, type, jsonDeserializationContext) -> {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "advancement");
			return Advancement.Task.fromJson(jsonObject, jsonDeserializationContext);
		})
		.registerTypeAdapter(AdvancementRewards.class, new AdvancementRewards.Deserializer())
		.registerTypeHierarchyAdapter(Text.class, new Text.Serializer())
		.registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
		.registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory())
		.create();
	private AdvancementManager manager = new AdvancementManager();

	public ServerAdvancementLoader() {
		super(GSON, "advancements");
	}

	protected void method_20724(Map<Identifier, JsonObject> map, ResourceManager resourceManager, Profiler profiler) {
		Map<Identifier, Advancement.Task> map2 = Maps.<Identifier, Advancement.Task>newHashMap();
		map.forEach((identifier, jsonObject) -> {
			try {
				Advancement.Task task = GSON.fromJson(jsonObject, Advancement.Task.class);
				map2.put(identifier, task);
			} catch (IllegalArgumentException | JsonParseException var4x) {
				LOGGER.error("Parsing error loading custom advancement {}: {}", identifier, var4x.getMessage());
			}
		});
		AdvancementManager advancementManager = new AdvancementManager();
		advancementManager.load(map2);

		for (Advancement advancement : advancementManager.getRoots()) {
			if (advancement.getDisplay() != null) {
				AdvancementPositioner.arrangeForTree(advancement);
			}
		}

		this.manager = advancementManager;
	}

	@Nullable
	public Advancement get(Identifier identifier) {
		return this.manager.get(identifier);
	}

	public Collection<Advancement> getAdvancements() {
		return this.manager.getAdvancements();
	}
}
