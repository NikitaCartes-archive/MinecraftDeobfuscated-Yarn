package net.minecraft.advancement;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.Codecs;

public class AdvancementProgress implements Comparable<AdvancementProgress> {
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z", Locale.ROOT);
	private static final Codec<Instant> TIME_CODEC = Codecs.formattedTime(TIME_FORMATTER).xmap(Instant::from, instant -> instant.atZone(ZoneId.systemDefault()));
	private static final Codec<Map<String, CriterionProgress>> MAP_CODEC = Codec.unboundedMap(Codec.STRING, TIME_CODEC)
		.xmap(
			map -> (Map)map.entrySet().stream().collect(Collectors.toMap(Entry::getKey, entry -> new CriterionProgress((Instant)entry.getValue()))),
			map -> (Map)map.entrySet()
					.stream()
					.filter(entry -> ((CriterionProgress)entry.getValue()).isObtained())
					.collect(Collectors.toMap(Entry::getKey, entry -> (Instant)Objects.requireNonNull(((CriterionProgress)entry.getValue()).getObtainedTime())))
		);
	public static final Codec<AdvancementProgress> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.createStrictOptionalFieldCodec(MAP_CODEC, "criteria", Map.of()).forGetter(advancementProgress -> advancementProgress.criteriaProgresses),
					Codec.BOOL.fieldOf("done").orElse(true).forGetter(AdvancementProgress::isDone)
				)
				.apply(instance, (criteriaProgresses, done) -> new AdvancementProgress(new HashMap(criteriaProgresses)))
	);
	private final Map<String, CriterionProgress> criteriaProgresses;
	private AdvancementRequirements requirements = AdvancementRequirements.EMPTY;

	private AdvancementProgress(Map<String, CriterionProgress> criteriaProgresses) {
		this.criteriaProgresses = criteriaProgresses;
	}

	public AdvancementProgress() {
		this.criteriaProgresses = Maps.<String, CriterionProgress>newHashMap();
	}

	public void init(AdvancementRequirements requirements) {
		Set<String> set = requirements.getNames();
		this.criteriaProgresses.entrySet().removeIf(progress -> !set.contains(progress.getKey()));

		for (String string : set) {
			this.criteriaProgresses.putIfAbsent(string, new CriterionProgress());
		}

		this.requirements = requirements;
	}

	public boolean isDone() {
		return this.requirements.matches(this::isCriterionObtained);
	}

	public boolean isAnyObtained() {
		for (CriterionProgress criterionProgress : this.criteriaProgresses.values()) {
			if (criterionProgress.isObtained()) {
				return true;
			}
		}

		return false;
	}

	public boolean obtain(String name) {
		CriterionProgress criterionProgress = (CriterionProgress)this.criteriaProgresses.get(name);
		if (criterionProgress != null && !criterionProgress.isObtained()) {
			criterionProgress.obtain();
			return true;
		} else {
			return false;
		}
	}

	public boolean reset(String name) {
		CriterionProgress criterionProgress = (CriterionProgress)this.criteriaProgresses.get(name);
		if (criterionProgress != null && criterionProgress.isObtained()) {
			criterionProgress.reset();
			return true;
		} else {
			return false;
		}
	}

	public String toString() {
		return "AdvancementProgress{criteria=" + this.criteriaProgresses + ", requirements=" + this.requirements + "}";
	}

	public void toPacket(PacketByteBuf buf) {
		buf.writeMap(this.criteriaProgresses, PacketByteBuf::writeString, (bufx, progresses) -> progresses.toPacket(bufx));
	}

	public static AdvancementProgress fromPacket(PacketByteBuf buf) {
		Map<String, CriterionProgress> map = buf.readMap(PacketByteBuf::readString, CriterionProgress::fromPacket);
		return new AdvancementProgress(map);
	}

	@Nullable
	public CriterionProgress getCriterionProgress(String name) {
		return (CriterionProgress)this.criteriaProgresses.get(name);
	}

	private boolean isCriterionObtained(String name) {
		CriterionProgress criterionProgress = this.getCriterionProgress(name);
		return criterionProgress != null && criterionProgress.isObtained();
	}

	public float getProgressBarPercentage() {
		if (this.criteriaProgresses.isEmpty()) {
			return 0.0F;
		} else {
			float f = (float)this.requirements.getLength();
			float g = (float)this.countObtainedRequirements();
			return g / f;
		}
	}

	@Nullable
	public Text getProgressBarFraction() {
		if (this.criteriaProgresses.isEmpty()) {
			return null;
		} else {
			int i = this.requirements.getLength();
			if (i <= 1) {
				return null;
			} else {
				int j = this.countObtainedRequirements();
				return Text.translatable("advancements.progress", j, i);
			}
		}
	}

	private int countObtainedRequirements() {
		return this.requirements.countMatches(this::isCriterionObtained);
	}

	public Iterable<String> getUnobtainedCriteria() {
		List<String> list = Lists.<String>newArrayList();

		for (Entry<String, CriterionProgress> entry : this.criteriaProgresses.entrySet()) {
			if (!((CriterionProgress)entry.getValue()).isObtained()) {
				list.add((String)entry.getKey());
			}
		}

		return list;
	}

	public Iterable<String> getObtainedCriteria() {
		List<String> list = Lists.<String>newArrayList();

		for (Entry<String, CriterionProgress> entry : this.criteriaProgresses.entrySet()) {
			if (((CriterionProgress)entry.getValue()).isObtained()) {
				list.add((String)entry.getKey());
			}
		}

		return list;
	}

	@Nullable
	public Instant getEarliestProgressObtainDate() {
		return (Instant)this.criteriaProgresses
			.values()
			.stream()
			.map(CriterionProgress::getObtainedTime)
			.filter(Objects::nonNull)
			.min(Comparator.naturalOrder())
			.orElse(null);
	}

	public int compareTo(AdvancementProgress advancementProgress) {
		Instant instant = this.getEarliestProgressObtainDate();
		Instant instant2 = advancementProgress.getEarliestProgressObtainDate();
		if (instant == null && instant2 != null) {
			return 1;
		} else if (instant != null && instant2 == null) {
			return -1;
		} else {
			return instant == null && instant2 == null ? 0 : instant.compareTo(instant2);
		}
	}
}
