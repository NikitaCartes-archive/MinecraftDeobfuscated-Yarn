package net.minecraft.text;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HoverEvent {
	private static final Logger LOGGER = LogManager.getLogger();
	private final HoverEvent.Action<?> action;
	private final Object contents;

	public <T> HoverEvent(HoverEvent.Action<T> action, T contents) {
		this.action = action;
		this.contents = contents;
	}

	public HoverEvent.Action<?> getAction() {
		return this.action;
	}

	@Nullable
	public <T> T getValue(HoverEvent.Action<T> action) {
		return this.action == action ? action.cast(this.contents) : null;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj != null && this.getClass() == obj.getClass()) {
			HoverEvent hoverEvent = (HoverEvent)obj;
			return this.action == hoverEvent.action && Objects.equals(this.contents, hoverEvent.contents);
		} else {
			return false;
		}
	}

	public String toString() {
		return "HoverEvent{action=" + this.action + ", value='" + this.contents + '\'' + '}';
	}

	public int hashCode() {
		int i = this.action.hashCode();
		return 31 * i + (this.contents != null ? this.contents.hashCode() : 0);
	}

	@Nullable
	public static HoverEvent fromJson(JsonObject json) {
		String string = JsonHelper.getString(json, "action", null);
		if (string == null) {
			return null;
		} else {
			HoverEvent.Action<?> action = HoverEvent.Action.byName(string);
			if (action == null) {
				return null;
			} else {
				JsonElement jsonElement = json.get("contents");
				if (jsonElement != null) {
					return action.buildHoverEvent(jsonElement);
				} else {
					Text text = Text.Serializer.fromJson(json.get("value"));
					return text != null ? action.buildHoverEvent(text) : null;
				}
			}
		}
	}

	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("action", this.action.getName());
		jsonObject.add("contents", this.action.contentsToJson(this.contents));
		return jsonObject;
	}

	public static class Action<T> {
		public static final HoverEvent.Action<Text> SHOW_TEXT = new HoverEvent.Action<>(
			"show_text", true, Text.Serializer::fromJson, Text.Serializer::toJsonTree, Function.identity()
		);
		public static final HoverEvent.Action<HoverEvent.ItemStackContent> SHOW_ITEM = new HoverEvent.Action<>(
			"show_item",
			true,
			jsonElement -> HoverEvent.ItemStackContent.parse(jsonElement),
			object -> ((HoverEvent.ItemStackContent)object).toJson(),
			text -> HoverEvent.ItemStackContent.parse(text)
		);
		public static final HoverEvent.Action<HoverEvent.EntityContent> SHOW_ENTITY = new HoverEvent.Action<>(
			"show_entity", true, HoverEvent.EntityContent::parse, HoverEvent.EntityContent::toJson, HoverEvent.EntityContent::parse
		);
		private static final Map<String, HoverEvent.Action<?>> BY_NAME = (Map<String, HoverEvent.Action<?>>)Stream.of(SHOW_TEXT, SHOW_ITEM, SHOW_ENTITY)
			.collect(ImmutableMap.toImmutableMap(HoverEvent.Action::getName, action -> action));
		private final String name;
		private final boolean parsable;
		private final Function<JsonElement, T> deserializer;
		private final Function<T, JsonElement> serializer;
		private final Function<Text, T> legacyDeserializer;

		public Action(String name, boolean parsable, Function<JsonElement, T> deserializer, Function<T, JsonElement> serializer, Function<Text, T> legacyDeserializer) {
			this.name = name;
			this.parsable = parsable;
			this.deserializer = deserializer;
			this.serializer = serializer;
			this.legacyDeserializer = legacyDeserializer;
		}

		public boolean isParsable() {
			return this.parsable;
		}

		public String getName() {
			return this.name;
		}

		@Nullable
		public static HoverEvent.Action<?> byName(String name) {
			return (HoverEvent.Action<?>)BY_NAME.get(name);
		}

		private T cast(Object o) {
			return (T)o;
		}

		@Nullable
		public HoverEvent buildHoverEvent(JsonElement contents) {
			T object = (T)this.deserializer.apply(contents);
			return object == null ? null : new HoverEvent(this, object);
		}

		@Nullable
		public HoverEvent buildHoverEvent(Text value) {
			T object = (T)this.legacyDeserializer.apply(value);
			return object == null ? null : new HoverEvent(this, object);
		}

		public JsonElement contentsToJson(Object contents) {
			return (JsonElement)this.serializer.apply(this.cast(contents));
		}

		public String toString() {
			return "<action " + this.name + ">";
		}
	}

	public static class EntityContent {
		public final EntityType<?> entityType;
		public final UUID uuid;
		@Nullable
		public final Text name;
		@Nullable
		@Environment(EnvType.CLIENT)
		private List<Text> tooltip;

		public EntityContent(EntityType<?> entityType, UUID uuid, @Nullable Text name) {
			this.entityType = entityType;
			this.uuid = uuid;
			this.name = name;
		}

		@Nullable
		public static HoverEvent.EntityContent parse(JsonElement json) {
			if (!json.isJsonObject()) {
				return null;
			} else {
				JsonObject jsonObject = json.getAsJsonObject();
				EntityType<?> entityType = Registry.ENTITY_TYPE.get(new Identifier(JsonHelper.getString(jsonObject, "type")));
				UUID uUID = UUID.fromString(JsonHelper.getString(jsonObject, "id"));
				Text text = Text.Serializer.fromJson(jsonObject.get("name"));
				return new HoverEvent.EntityContent(entityType, uUID, text);
			}
		}

		@Nullable
		public static HoverEvent.EntityContent parse(Text text) {
			try {
				NbtCompound nbtCompound = StringNbtReader.parse(text.getString());
				Text text2 = Text.Serializer.fromJson(nbtCompound.getString("name"));
				EntityType<?> entityType = Registry.ENTITY_TYPE.get(new Identifier(nbtCompound.getString("type")));
				UUID uUID = UUID.fromString(nbtCompound.getString("id"));
				return new HoverEvent.EntityContent(entityType, uUID, text2);
			} catch (CommandSyntaxException | JsonSyntaxException var5) {
				return null;
			}
		}

		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("type", Registry.ENTITY_TYPE.getId(this.entityType).toString());
			jsonObject.addProperty("id", this.uuid.toString());
			if (this.name != null) {
				jsonObject.add("name", Text.Serializer.toJsonTree(this.name));
			}

			return jsonObject;
		}

		@Environment(EnvType.CLIENT)
		public List<Text> asTooltip() {
			if (this.tooltip == null) {
				this.tooltip = Lists.<Text>newArrayList();
				if (this.name != null) {
					this.tooltip.add(this.name);
				}

				this.tooltip.add(new TranslatableText("gui.entity_tooltip.type", this.entityType.getName()));
				this.tooltip.add(new LiteralText(this.uuid.toString()));
			}

			return this.tooltip;
		}

		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (object != null && this.getClass() == object.getClass()) {
				HoverEvent.EntityContent entityContent = (HoverEvent.EntityContent)object;
				return this.entityType.equals(entityContent.entityType) && this.uuid.equals(entityContent.uuid) && Objects.equals(this.name, entityContent.name);
			} else {
				return false;
			}
		}

		public int hashCode() {
			int i = this.entityType.hashCode();
			i = 31 * i + this.uuid.hashCode();
			return 31 * i + (this.name != null ? this.name.hashCode() : 0);
		}
	}

	public static class ItemStackContent {
		private final Item item;
		private final int count;
		@Nullable
		private final NbtCompound tag;
		@Nullable
		@Environment(EnvType.CLIENT)
		private ItemStack stack;

		ItemStackContent(Item item, int count, @Nullable NbtCompound tag) {
			this.item = item;
			this.count = count;
			this.tag = tag;
		}

		public ItemStackContent(ItemStack stack) {
			this(stack.getItem(), stack.getCount(), stack.getTag() != null ? stack.getTag().copy() : null);
		}

		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (object != null && this.getClass() == object.getClass()) {
				HoverEvent.ItemStackContent itemStackContent = (HoverEvent.ItemStackContent)object;
				return this.count == itemStackContent.count && this.item.equals(itemStackContent.item) && Objects.equals(this.tag, itemStackContent.tag);
			} else {
				return false;
			}
		}

		public int hashCode() {
			int i = this.item.hashCode();
			i = 31 * i + this.count;
			return 31 * i + (this.tag != null ? this.tag.hashCode() : 0);
		}

		@Environment(EnvType.CLIENT)
		public ItemStack asStack() {
			if (this.stack == null) {
				this.stack = new ItemStack(this.item, this.count);
				if (this.tag != null) {
					this.stack.setTag(this.tag);
				}
			}

			return this.stack;
		}

		private static HoverEvent.ItemStackContent parse(JsonElement json) {
			if (json.isJsonPrimitive()) {
				return new HoverEvent.ItemStackContent(Registry.ITEM.get(new Identifier(json.getAsString())), 1, null);
			} else {
				JsonObject jsonObject = JsonHelper.asObject(json, "item");
				Item item = Registry.ITEM.get(new Identifier(JsonHelper.getString(jsonObject, "id")));
				int i = JsonHelper.getInt(jsonObject, "count", 1);
				if (jsonObject.has("tag")) {
					String string = JsonHelper.getString(jsonObject, "tag");

					try {
						NbtCompound nbtCompound = StringNbtReader.parse(string);
						return new HoverEvent.ItemStackContent(item, i, nbtCompound);
					} catch (CommandSyntaxException var6) {
						HoverEvent.LOGGER.warn("Failed to parse tag: {}", string, var6);
					}
				}

				return new HoverEvent.ItemStackContent(item, i, null);
			}
		}

		@Nullable
		private static HoverEvent.ItemStackContent parse(Text text) {
			try {
				NbtCompound nbtCompound = StringNbtReader.parse(text.getString());
				return new HoverEvent.ItemStackContent(ItemStack.fromNbt(nbtCompound));
			} catch (CommandSyntaxException var2) {
				HoverEvent.LOGGER.warn("Failed to parse item tag: {}", text, var2);
				return null;
			}
		}

		private JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("id", Registry.ITEM.getId(this.item).toString());
			if (this.count != 1) {
				jsonObject.addProperty("count", this.count);
			}

			if (this.tag != null) {
				jsonObject.addProperty("tag", this.tag.toString());
			}

			return jsonObject;
		}
	}
}
