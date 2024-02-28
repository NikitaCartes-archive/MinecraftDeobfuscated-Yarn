package net.minecraft.text;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.component.ComponentChanges;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Uuids;
import net.minecraft.util.dynamic.Codecs;

public class HoverEvent {
	public static final Codec<HoverEvent> CODEC = Codec.either(HoverEvent.EventData.CODEC.codec(), HoverEvent.EventData.LEGACY_CODEC.codec())
		.xmap(either -> new HoverEvent(either.map(data -> data, data -> data)), event -> Either.left(event.data));
	private final HoverEvent.EventData<?> data;

	public <T> HoverEvent(HoverEvent.Action<T> action, T contents) {
		this(new HoverEvent.EventData<>(action, contents));
	}

	private HoverEvent(HoverEvent.EventData<?> data) {
		this.data = data;
	}

	public HoverEvent.Action<?> getAction() {
		return this.data.action;
	}

	@Nullable
	public <T> T getValue(HoverEvent.Action<T> action) {
		return this.data.action == action ? action.cast(this.data.value) : null;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			return o != null && this.getClass() == o.getClass() ? ((HoverEvent)o).data.equals(this.data) : false;
		}
	}

	public String toString() {
		return this.data.toString();
	}

	public int hashCode() {
		return this.data.hashCode();
	}

	public static class Action<T> implements StringIdentifiable {
		public static final HoverEvent.Action<Text> SHOW_TEXT = new HoverEvent.Action<>("show_text", true, TextCodecs.CODEC, (text, ops) -> DataResult.success(text));
		public static final HoverEvent.Action<HoverEvent.ItemStackContent> SHOW_ITEM = new HoverEvent.Action<>(
			"show_item", true, HoverEvent.ItemStackContent.CODEC, HoverEvent.ItemStackContent::legacySerializer
		);
		public static final HoverEvent.Action<HoverEvent.EntityContent> SHOW_ENTITY = new HoverEvent.Action<>(
			"show_entity", true, HoverEvent.EntityContent.CODEC, HoverEvent.EntityContent::legacySerializer
		);
		public static final Codec<HoverEvent.Action<?>> UNVALIDATED_CODEC = StringIdentifiable.createBasicCodec(
			() -> new HoverEvent.Action[]{SHOW_TEXT, SHOW_ITEM, SHOW_ENTITY}
		);
		public static final Codec<HoverEvent.Action<?>> CODEC = Codecs.validate(UNVALIDATED_CODEC, HoverEvent.Action::validate);
		private final String name;
		private final boolean parsable;
		final Codec<HoverEvent.EventData<T>> codec;
		final Codec<HoverEvent.EventData<T>> legacyCodec;

		public Action(String name, boolean parsable, Codec<T> contentCodec, HoverEvent.LegacySerializer<T> legacySerializer) {
			this.name = name;
			this.parsable = parsable;
			this.codec = contentCodec.<HoverEvent.EventData<T>>xmap(value -> new HoverEvent.EventData<>(this, (T)value), action -> action.value)
				.fieldOf("contents")
				.codec();
			this.legacyCodec = new Codec<HoverEvent.EventData<T>>() {
				@Override
				public <D> DataResult<Pair<HoverEvent.EventData<T>, D>> decode(DynamicOps<D> ops, D input) {
					return TextCodecs.CODEC.decode(ops, input).flatMap(pair -> {
						DataResult<T> dataResult;
						if (ops instanceof RegistryOps<D> registryOps) {
							dataResult = legacySerializer.parse((Text)pair.getFirst(), registryOps);
						} else {
							dataResult = legacySerializer.parse((Text)pair.getFirst(), null);
						}

						return dataResult.map(value -> Pair.of(new HoverEvent.EventData<>(Action.this, value), pair.getSecond()));
					});
				}

				public <D> DataResult<D> encode(HoverEvent.EventData<T> eventData, DynamicOps<D> dynamicOps, D object) {
					return DataResult.error(() -> "Can't encode in legacy format");
				}
			};
		}

		public boolean isParsable() {
			return this.parsable;
		}

		@Override
		public String asString() {
			return this.name;
		}

		T cast(Object o) {
			return (T)o;
		}

		public String toString() {
			return "<action " + this.name + ">";
		}

		private static DataResult<HoverEvent.Action<?>> validate(@Nullable HoverEvent.Action<?> action) {
			if (action == null) {
				return DataResult.error(() -> "Unknown action");
			} else {
				return !action.isParsable() ? DataResult.error(() -> "Action not allowed: " + action) : DataResult.success(action, Lifecycle.stable());
			}
		}
	}

	public static class EntityContent {
		public static final Codec<HoverEvent.EntityContent> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Registries.ENTITY_TYPE.getCodec().fieldOf("type").forGetter(content -> content.entityType),
						Uuids.STRICT_CODEC.fieldOf("id").forGetter(content -> content.uuid),
						Codecs.createStrictOptionalFieldCodec(TextCodecs.CODEC, "name").forGetter(content -> content.name)
					)
					.apply(instance, HoverEvent.EntityContent::new)
		);
		public final EntityType<?> entityType;
		public final UUID uuid;
		public final Optional<Text> name;
		@Nullable
		private List<Text> tooltip;

		public EntityContent(EntityType<?> entityType, UUID uuid, @Nullable Text name) {
			this(entityType, uuid, Optional.ofNullable(name));
		}

		public EntityContent(EntityType<?> entityType, UUID uuid, Optional<Text> name) {
			this.entityType = entityType;
			this.uuid = uuid;
			this.name = name;
		}

		public static DataResult<HoverEvent.EntityContent> legacySerializer(Text text, @Nullable RegistryOps<?> ops) {
			try {
				NbtCompound nbtCompound = StringNbtReader.parse(text.getString());
				DynamicOps<JsonElement> dynamicOps = (DynamicOps<JsonElement>)(ops != null ? ops.withDelegate(JsonOps.INSTANCE) : JsonOps.INSTANCE);
				DataResult<Text> dataResult = TextCodecs.CODEC.parse(dynamicOps, JsonParser.parseString(nbtCompound.getString("name")));
				EntityType<?> entityType = Registries.ENTITY_TYPE.get(new Identifier(nbtCompound.getString("type")));
				UUID uUID = UUID.fromString(nbtCompound.getString("id"));
				return dataResult.map(textx -> new HoverEvent.EntityContent(entityType, uUID, textx));
			} catch (Exception var7) {
				return DataResult.error(() -> "Failed to parse tooltip: " + var7.getMessage());
			}
		}

		public List<Text> asTooltip() {
			if (this.tooltip == null) {
				this.tooltip = new ArrayList();
				this.name.ifPresent(this.tooltip::add);
				this.tooltip.add(Text.translatable("gui.entity_tooltip.type", this.entityType.getName()));
				this.tooltip.add(Text.literal(this.uuid.toString()));
			}

			return this.tooltip;
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else if (o != null && this.getClass() == o.getClass()) {
				HoverEvent.EntityContent entityContent = (HoverEvent.EntityContent)o;
				return this.entityType.equals(entityContent.entityType) && this.uuid.equals(entityContent.uuid) && this.name.equals(entityContent.name);
			} else {
				return false;
			}
		}

		public int hashCode() {
			int i = this.entityType.hashCode();
			i = 31 * i + this.uuid.hashCode();
			return 31 * i + this.name.hashCode();
		}
	}

	static record EventData<T>(HoverEvent.Action<T> action, T value) {
		public static final MapCodec<HoverEvent.EventData<?>> CODEC = HoverEvent.Action.CODEC
			.dispatchMap("action", HoverEvent.EventData::action, action -> action.codec);
		public static final MapCodec<HoverEvent.EventData<?>> LEGACY_CODEC = HoverEvent.Action.CODEC
			.dispatchMap("action", HoverEvent.EventData::action, action -> action.legacyCodec);
	}

	public static class ItemStackContent {
		public static final Codec<HoverEvent.ItemStackContent> ITEM_STACK_CODEC = ItemStack.CODEC
			.xmap(HoverEvent.ItemStackContent::new, HoverEvent.ItemStackContent::asStack);
		private static final Codec<HoverEvent.ItemStackContent> ENTRY_BASED_CODEC = ItemStack.REGISTRY_ENTRY_CODEC
			.xmap(HoverEvent.ItemStackContent::new, HoverEvent.ItemStackContent::asStack);
		public static final Codec<HoverEvent.ItemStackContent> CODEC = Codecs.alternatively(ITEM_STACK_CODEC, ENTRY_BASED_CODEC);
		private final RegistryEntry<Item> item;
		private final int count;
		private final ComponentChanges changes;
		@Nullable
		private ItemStack stack;

		ItemStackContent(RegistryEntry<Item> item, int count, ComponentChanges changes) {
			this.item = item;
			this.count = count;
			this.changes = changes;
		}

		public ItemStackContent(ItemStack stack) {
			this(stack.getRegistryEntry(), stack.getCount(), stack.getComponentChanges());
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else if (o != null && this.getClass() == o.getClass()) {
				HoverEvent.ItemStackContent itemStackContent = (HoverEvent.ItemStackContent)o;
				return this.count == itemStackContent.count && this.item.equals(itemStackContent.item) && this.changes.equals(itemStackContent.changes);
			} else {
				return false;
			}
		}

		public int hashCode() {
			int i = this.item.hashCode();
			i = 31 * i + this.count;
			return 31 * i + this.changes.hashCode();
		}

		public ItemStack asStack() {
			if (this.stack == null) {
				this.stack = new ItemStack(this.item, this.count, this.changes);
			}

			return this.stack;
		}

		private static DataResult<HoverEvent.ItemStackContent> legacySerializer(Text text, @Nullable RegistryOps<?> ops) {
			try {
				NbtCompound nbtCompound = StringNbtReader.parse(text.getString());
				DynamicOps<NbtElement> dynamicOps = (DynamicOps<NbtElement>)(ops != null ? ops.withDelegate(NbtOps.INSTANCE) : NbtOps.INSTANCE);
				return ItemStack.CODEC.parse(dynamicOps, nbtCompound).map(HoverEvent.ItemStackContent::new);
			} catch (CommandSyntaxException var4) {
				return DataResult.error(() -> "Failed to parse item tag: " + var4.getMessage());
			}
		}
	}

	public interface LegacySerializer<T> {
		DataResult<T> parse(Text text, @Nullable RegistryOps<?> os);
	}
}
