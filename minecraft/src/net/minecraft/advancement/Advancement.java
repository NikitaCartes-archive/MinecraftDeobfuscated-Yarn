package net.minecraft.advancement;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public record Advancement(
	Optional<Identifier> parent,
	Optional<AdvancementDisplay> display,
	AdvancementRewards rewards,
	Map<String, AdvancementCriterion<?>> criteria,
	AdvancementRequirements requirements,
	boolean sendsTelemetryEvent,
	Optional<Text> name
) {
	private static final Codec<Map<String, AdvancementCriterion<?>>> CRITERIA_CODEC = Codecs.validate(
		Codec.unboundedMap(Codec.STRING, AdvancementCriterion.CODEC),
		criteria -> criteria.isEmpty() ? DataResult.error(() -> "Advancement criteria cannot be empty") : DataResult.success(criteria)
	);
	public static final Codec<Advancement> CODEC = Codecs.validate(
		RecordCodecBuilder.create(
			instance -> instance.group(
						Codecs.createStrictOptionalFieldCodec(Identifier.CODEC, "parent").forGetter(Advancement::parent),
						Codecs.createStrictOptionalFieldCodec(AdvancementDisplay.CODEC, "display").forGetter(Advancement::display),
						Codecs.createStrictOptionalFieldCodec(AdvancementRewards.CODEC, "rewards", AdvancementRewards.NONE).forGetter(Advancement::rewards),
						CRITERIA_CODEC.fieldOf("criteria").forGetter(Advancement::criteria),
						Codecs.createStrictOptionalFieldCodec(AdvancementRequirements.CODEC, "requirements").forGetter(advancement -> Optional.of(advancement.requirements())),
						Codecs.createStrictOptionalFieldCodec(Codec.BOOL, "sends_telemetry_event", false).forGetter(Advancement::sendsTelemetryEvent)
					)
					.apply(instance, (parent, display, rewards, criteria, requirements, sendsTelemetryEvent) -> {
						AdvancementRequirements advancementRequirements = (AdvancementRequirements)requirements.orElseGet(() -> AdvancementRequirements.allOf(criteria.keySet()));
						return new Advancement(parent, display, rewards, criteria, advancementRequirements, sendsTelemetryEvent);
					})
		),
		Advancement::validate
	);
	public static final PacketCodec<RegistryByteBuf, Advancement> PACKET_CODEC = PacketCodec.of(Advancement::write, Advancement::read);

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

	private static DataResult<Advancement> validate(Advancement advancement) {
		return advancement.requirements().validate(advancement.criteria().keySet()).map(validated -> advancement);
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

	private void write(RegistryByteBuf buf) {
		buf.writeOptional(this.parent, PacketByteBuf::writeIdentifier);
		AdvancementDisplay.PACKET_CODEC.collect(PacketCodecs::optional).encode(buf, this.display);
		this.requirements.writeRequirements(buf);
		buf.writeBoolean(this.sendsTelemetryEvent);
	}

	private static Advancement read(RegistryByteBuf buf) {
		return new Advancement(
			buf.readOptional(PacketByteBuf::readIdentifier),
			(Optional<AdvancementDisplay>)AdvancementDisplay.PACKET_CODEC.collect(PacketCodecs::optional).decode(buf),
			AdvancementRewards.NONE,
			Map.of(),
			new AdvancementRequirements(buf),
			buf.readBoolean()
		);
	}

	public boolean isRoot() {
		return this.parent.isEmpty();
	}

	public void validate(ErrorReporter errorReporter, RegistryEntryLookup.RegistryLookup lookup) {
		this.criteria.forEach((name, criterion) -> {
			LootContextPredicateValidator lootContextPredicateValidator = new LootContextPredicateValidator(errorReporter.makeChild(name), lookup);
			criterion.conditions().validate(lootContextPredicateValidator);
		});
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
			return this.display(new AdvancementDisplay(icon, title, description, Optional.ofNullable(background), frame, showToast, announceToChat, hidden));
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
			return this.display(
				new AdvancementDisplay(new ItemStack(icon.asItem()), title, description, Optional.ofNullable(background), frame, showToast, announceToChat, hidden)
			);
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
