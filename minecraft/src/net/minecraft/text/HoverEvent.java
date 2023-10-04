package net.minecraft.text;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
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
		public static final HoverEvent.Action<Text> SHOW_TEXT = new HoverEvent.Action<>("show_text", true, TextCodecs.CODEC, DataResult::success);
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

		public Action(String name, boolean parsable, Codec<T> contentCodec, Function<Text, DataResult<T>> legacySerializer) {
			this.name = name;
			this.parsable = parsable;
			this.codec = contentCodec.<HoverEvent.EventData<T>>xmap(content -> new HoverEvent.EventData<>(this, (T)content), action -> action.value)
				.fieldOf("contents")
				.codec();
			this.legacyCodec = Codec.of(
				Encoder.error("Can't encode in legacy format"), TextCodecs.CODEC.flatMap(legacySerializer).map(text -> new HoverEvent.EventData<>(this, (T)text))
			);
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

		public static DataResult<HoverEvent.EntityContent> legacySerializer(Text text) {
			try {
				NbtCompound nbtCompound = StringNbtReader.parse(text.getString());
				Text text2 = Text.Serialization.fromJson(nbtCompound.getString("name"));
				EntityType<?> entityType = Registries.ENTITY_TYPE.get(new Identifier(nbtCompound.getString("type")));
				UUID uUID = UUID.fromString(nbtCompound.getString("id"));
				return DataResult.success(new HoverEvent.EntityContent(entityType, uUID, text2));
			} catch (Exception var5) {
				return DataResult.error(() -> "Failed to parse tooltip: " + var5.getMessage());
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
		public static final Codec<HoverEvent.ItemStackContent> ITEM_STACK_CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Registries.ITEM.getCodec().fieldOf("id").forGetter(content -> content.item),
						Codecs.createStrictOptionalFieldCodec(Codec.INT, "count", 1).forGetter(content -> content.count),
						Codecs.createStrictOptionalFieldCodec(StringNbtReader.STRINGIFIED_CODEC, "tag").forGetter(content -> content.nbt)
					)
					.apply(instance, HoverEvent.ItemStackContent::new)
		);
		public static final Codec<HoverEvent.ItemStackContent> CODEC = Codec.either(Registries.ITEM.getCodec(), ITEM_STACK_CODEC)
			.xmap(either -> either.map(item -> new HoverEvent.ItemStackContent(item, 1, Optional.empty()), content -> content), Either::right);
		private final Item item;
		private final int count;
		private final Optional<NbtCompound> nbt;
		@Nullable
		private ItemStack stack;

		ItemStackContent(Item item, int count, @Nullable NbtCompound nbt) {
			this(item, count, Optional.ofNullable(nbt));
		}

		ItemStackContent(Item item, int count, Optional<NbtCompound> nbt) {
			this.item = item;
			this.count = count;
			this.nbt = nbt;
		}

		public ItemStackContent(ItemStack stack) {
			this(stack.getItem(), stack.getCount(), stack.getNbt() != null ? Optional.of(stack.getNbt().copy()) : Optional.empty());
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else if (o != null && this.getClass() == o.getClass()) {
				HoverEvent.ItemStackContent itemStackContent = (HoverEvent.ItemStackContent)o;
				return this.count == itemStackContent.count && this.item.equals(itemStackContent.item) && this.nbt.equals(itemStackContent.nbt);
			} else {
				return false;
			}
		}

		public int hashCode() {
			int i = this.item.hashCode();
			i = 31 * i + this.count;
			return 31 * i + this.nbt.hashCode();
		}

		public ItemStack asStack() {
			if (this.stack == null) {
				this.stack = new ItemStack(this.item, this.count);
				this.nbt.ifPresent(this.stack::setNbt);
			}

			return this.stack;
		}

		private static DataResult<HoverEvent.ItemStackContent> legacySerializer(Text text) {
			try {
				NbtCompound nbtCompound = StringNbtReader.parse(text.getString());
				return DataResult.success(new HoverEvent.ItemStackContent(ItemStack.fromNbt(nbtCompound)));
			} catch (CommandSyntaxException var2) {
				return DataResult.error(() -> "Failed to parse item tag: " + var2.getMessage());
			}
		}
	}
}
