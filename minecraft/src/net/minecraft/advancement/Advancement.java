package net.minecraft.advancement;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public record Advancement(
	Optional<Identifier> parent,
	Optional<AdvancementDisplay> display,
	AdvancementRewards rewards,
	Map<String, AdvancementCriterion<?>> criteria,
	AdvancementRequirements requirements,
	boolean sendsTelemetryEvent,
	Optional<Text> name
) {
	public Advancement(
		Optional<Identifier> parent,
		Optional<AdvancementDisplay> display,
		AdvancementRewards rewards,
		Map<String, AdvancementCriterion<?>> criteria,
		AdvancementRequirements requirements,
		boolean sendsTelemetryEvent
	) {
		this(parent, display, rewards, Map.copyOf(criteria), requirements, sendsTelemetryEvent, display.map(Advancement::createNameFromDisplay));
	}

	private static Text createNameFromDisplay(AdvancementDisplay display) {
		Text text = display.getTitle();
		Formatting formatting = display.getFrame().getTitleFormat();
		Text text2 = Texts.setStyleIfAbsent(text.copy(), Style.EMPTY.withColor(formatting)).append("\n").append(display.getDescription());
		Text text3 = text.copy().styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, text2)));
		return Texts.bracketed(text3).formatted(formatting);
	}

	public static Text getNameFromIdentity(AdvancementEntry identifiedAdvancement) {
		return (Text)identifiedAdvancement.value().name().orElseGet(() -> Text.literal(identifiedAdvancement.id().toString()));
	}

	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		this.parent.ifPresent(parent -> jsonObject.addProperty("parent", parent.toString()));
		this.display.ifPresent(display -> jsonObject.add("display", display.toJson()));
		jsonObject.add("rewards", this.rewards.toJson());
		JsonObject jsonObject2 = new JsonObject();

		for (Entry<String, AdvancementCriterion<?>> entry : this.criteria.entrySet()) {
			jsonObject2.add((String)entry.getKey(), ((AdvancementCriterion)entry.getValue()).toJson());
		}

		jsonObject.add("criteria", jsonObject2);
		jsonObject.add("requirements", this.requirements.toJson());
		jsonObject.addProperty("sends_telemetry_event", this.sendsTelemetryEvent);
		return jsonObject;
	}

	public static Advancement fromJson(JsonObject json, AdvancementEntityPredicateDeserializer predicateDeserializer) {
		Optional<Identifier> optional = json.has("parent") ? Optional.of(new Identifier(JsonHelper.getString(json, "parent"))) : Optional.empty();
		Optional<AdvancementDisplay> optional2 = json.has("display")
			? Optional.of(AdvancementDisplay.fromJson(JsonHelper.getObject(json, "display")))
			: Optional.empty();
		AdvancementRewards advancementRewards = json.has("rewards") ? AdvancementRewards.fromJson(JsonHelper.getObject(json, "rewards")) : AdvancementRewards.NONE;
		Map<String, AdvancementCriterion<?>> map = AdvancementCriterion.criteriaFromJson(JsonHelper.getObject(json, "criteria"), predicateDeserializer);
		if (map.isEmpty()) {
			throw new JsonSyntaxException("Advancement criteria cannot be empty");
		} else {
			AdvancementRequirements advancementRequirements = AdvancementRequirements.fromJson(JsonHelper.getArray(json, "requirements", new JsonArray()), map.keySet());
			if (advancementRequirements.isEmpty()) {
				advancementRequirements = AdvancementRequirements.allOf(map.keySet());
			}

			boolean bl = JsonHelper.getBoolean(json, "sends_telemetry_event", false);
			return new Advancement(optional, optional2, advancementRewards, map, advancementRequirements, bl);
		}
	}

	public void write(PacketByteBuf buf) {
		buf.writeOptional(this.parent, PacketByteBuf::writeIdentifier);
		buf.writeOptional(this.display, (bufx, display) -> display.toPacket(bufx));
		this.requirements.writeRequirements(buf);
		buf.writeBoolean(this.sendsTelemetryEvent);
	}

	public static Advancement read(PacketByteBuf buf) {
		return new Advancement(
			buf.readOptional(PacketByteBuf::readIdentifier),
			buf.readOptional(AdvancementDisplay::fromPacket),
			AdvancementRewards.NONE,
			Map.of(),
			new AdvancementRequirements(buf),
			buf.readBoolean()
		);
	}

	public boolean isRoot() {
		return this.parent.isEmpty();
	}

	public static class Builder {
		private Optional<Identifier> parentObj = Optional.empty();
		private Optional<AdvancementDisplay> display = Optional.empty();
		private AdvancementRewards rewards = AdvancementRewards.NONE;
		private final ImmutableMap.Builder<String, AdvancementCriterion<?>> criteria = ImmutableMap.builder();
		private Optional<AdvancementRequirements> requirements = Optional.empty();
		private AdvancementRequirements.CriterionMerger merger = AdvancementRequirements.CriterionMerger.AND;
		private boolean sendsTelemetryEvent;

		public static Advancement.Builder create() {
			return new Advancement.Builder().sendsTelemetryEvent();
		}

		public static Advancement.Builder createUntelemetered() {
			return new Advancement.Builder();
		}

		public Advancement.Builder parent(AdvancementEntry parent) {
			this.parentObj = Optional.of(parent.id());
			return this;
		}

		@Deprecated(
			forRemoval = true
		)
		public Advancement.Builder parent(Identifier parentId) {
			this.parentObj = Optional.of(parentId);
			return this;
		}

		public Advancement.Builder display(
			ItemStack icon,
			Text title,
			Text description,
			@Nullable Identifier background,
			AdvancementFrame frame,
			boolean showToast,
			boolean announceToChat,
			boolean hidden
		) {
			return this.display(new AdvancementDisplay(icon, title, description, background, frame, showToast, announceToChat, hidden));
		}

		public Advancement.Builder display(
			ItemConvertible icon,
			Text title,
			Text description,
			@Nullable Identifier background,
			AdvancementFrame frame,
			boolean showToast,
			boolean announceToChat,
			boolean hidden
		) {
			return this.display(new AdvancementDisplay(new ItemStack(icon.asItem()), title, description, background, frame, showToast, announceToChat, hidden));
		}

		public Advancement.Builder display(AdvancementDisplay display) {
			this.display = Optional.of(display);
			return this;
		}

		public Advancement.Builder rewards(AdvancementRewards.Builder builder) {
			return this.rewards(builder.build());
		}

		public Advancement.Builder rewards(AdvancementRewards rewards) {
			this.rewards = rewards;
			return this;
		}

		public Advancement.Builder criterion(String name, AdvancementCriterion<?> criterion) {
			this.criteria.put(name, criterion);
			return this;
		}

		public Advancement.Builder criteriaMerger(AdvancementRequirements.CriterionMerger merger) {
			this.merger = merger;
			return this;
		}

		public Advancement.Builder requirements(AdvancementRequirements requirements) {
			this.requirements = Optional.of(requirements);
			return this;
		}

		public Advancement.Builder sendsTelemetryEvent() {
			this.sendsTelemetryEvent = true;
			return this;
		}

		public AdvancementEntry build(Identifier id) {
			Map<String, AdvancementCriterion<?>> map = this.criteria.buildOrThrow();
			AdvancementRequirements advancementRequirements = (AdvancementRequirements)this.requirements.orElseGet(() -> this.merger.create(map.keySet()));
			return new AdvancementEntry(id, new Advancement(this.parentObj, this.display, this.rewards, map, advancementRequirements, this.sendsTelemetryEvent));
		}

		public AdvancementEntry build(Consumer<AdvancementEntry> exporter, String id) {
			AdvancementEntry advancementEntry = this.build(new Identifier(id));
			exporter.accept(advancementEntry);
			return advancementEntry;
		}
	}
}
