package net.minecraft.advancement;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.JsonHelper;

public class AdvancementProgress implements Comparable<AdvancementProgress> {
	final Map<String, CriterionProgress> criteriaProgresses;
	private String[][] requirements = new String[0][];

	private AdvancementProgress(Map<String, CriterionProgress> criteriaProgresses) {
		this.criteriaProgresses = criteriaProgresses;
	}

	public AdvancementProgress() {
		this.criteriaProgresses = Maps.<String, CriterionProgress>newHashMap();
	}

	public void init(Map<String, AdvancementCriterion> criteria, String[][] requirements) {
		Set<String> set = criteria.keySet();
		this.criteriaProgresses.entrySet().removeIf(progress -> !set.contains(progress.getKey()));

		for (String string : set) {
			if (!this.criteriaProgresses.containsKey(string)) {
				this.criteriaProgresses.put(string, new CriterionProgress());
			}
		}

		this.requirements = requirements;
	}

	public boolean isDone() {
		if (this.requirements.length == 0) {
			return false;
		} else {
			for (String[] strings : this.requirements) {
				boolean bl = false;

				for (String string : strings) {
					CriterionProgress criterionProgress = this.getCriterionProgress(string);
					if (criterionProgress != null && criterionProgress.isObtained()) {
						bl = true;
						break;
					}
				}

				if (!bl) {
					return false;
				}
			}

			return true;
		}
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
		return "AdvancementProgress{criteria=" + this.criteriaProgresses + ", requirements=" + Arrays.deepToString(this.requirements) + "}";
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

	public float getProgressBarPercentage() {
		if (this.criteriaProgresses.isEmpty()) {
			return 0.0F;
		} else {
			float f = (float)this.requirements.length;
			float g = (float)this.countObtainedRequirements();
			return g / f;
		}
	}

	@Nullable
	public String getProgressBarFraction() {
		if (this.criteriaProgresses.isEmpty()) {
			return null;
		} else {
			int i = this.requirements.length;
			if (i <= 1) {
				return null;
			} else {
				int j = this.countObtainedRequirements();
				return j + "/" + i;
			}
		}
	}

	private int countObtainedRequirements() {
		int i = 0;

		for (String[] strings : this.requirements) {
			boolean bl = false;

			for (String string : strings) {
				CriterionProgress criterionProgress = this.getCriterionProgress(string);
				if (criterionProgress != null && criterionProgress.isObtained()) {
					bl = true;
					break;
				}
			}

			if (bl) {
				i++;
			}
		}

		return i;
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
	public Date getEarliestProgressObtainDate() {
		Date date = null;

		for (CriterionProgress criterionProgress : this.criteriaProgresses.values()) {
			if (criterionProgress.isObtained() && (date == null || criterionProgress.getObtainedDate().before(date))) {
				date = criterionProgress.getObtainedDate();
			}
		}

		return date;
	}

	public int compareTo(AdvancementProgress advancementProgress) {
		Date date = this.getEarliestProgressObtainDate();
		Date date2 = advancementProgress.getEarliestProgressObtainDate();
		if (date == null && date2 != null) {
			return 1;
		} else if (date != null && date2 == null) {
			return -1;
		} else {
			return date == null && date2 == null ? 0 : date.compareTo(date2);
		}
	}

	public static class Serializer implements JsonDeserializer<AdvancementProgress>, JsonSerializer<AdvancementProgress> {
		public JsonElement serialize(AdvancementProgress advancementProgress, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			JsonObject jsonObject2 = new JsonObject();

			for (Entry<String, CriterionProgress> entry : advancementProgress.criteriaProgresses.entrySet()) {
				CriterionProgress criterionProgress = (CriterionProgress)entry.getValue();
				if (criterionProgress.isObtained()) {
					jsonObject2.add((String)entry.getKey(), criterionProgress.toJson());
				}
			}

			if (!jsonObject2.entrySet().isEmpty()) {
				jsonObject.add("criteria", jsonObject2);
			}

			jsonObject.addProperty("done", advancementProgress.isDone());
			return jsonObject;
		}

		public AdvancementProgress deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "advancement");
			JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "criteria", new JsonObject());
			AdvancementProgress advancementProgress = new AdvancementProgress();

			for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
				String string = (String)entry.getKey();
				advancementProgress.criteriaProgresses.put(string, CriterionProgress.obtainedAt(JsonHelper.asString((JsonElement)entry.getValue(), string)));
			}

			return advancementProgress;
		}
	}
}
