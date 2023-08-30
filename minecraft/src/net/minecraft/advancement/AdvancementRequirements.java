package net.minecraft.advancement;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.JsonHelper;

public record AdvancementRequirements(String[][] requirements) {
	public static final AdvancementRequirements EMPTY = new AdvancementRequirements(new String[0][]);

	public AdvancementRequirements(PacketByteBuf buf) {
		this(readRequirements(buf));
	}

	private static String[][] readRequirements(PacketByteBuf buf) {
		String[][] strings = new String[buf.readVarInt()][];

		for (int i = 0; i < strings.length; i++) {
			strings[i] = new String[buf.readVarInt()];

			for (int j = 0; j < strings[i].length; j++) {
				strings[i][j] = buf.readString();
			}
		}

		return strings;
	}

	public void writeRequirements(PacketByteBuf buf) {
		buf.writeVarInt(this.requirements.length);

		for (String[] strings : this.requirements) {
			buf.writeVarInt(strings.length);

			for (String string : strings) {
				buf.writeString(string);
			}
		}
	}

	public static AdvancementRequirements allOf(Collection<String> requirements) {
		return new AdvancementRequirements((String[][])requirements.stream().map(string -> new String[]{string}).toArray(String[][]::new));
	}

	public static AdvancementRequirements anyOf(Collection<String> requirements) {
		return new AdvancementRequirements(new String[][]{(String[])requirements.toArray(String[]::new)});
	}

	public int getLength() {
		return this.requirements.length;
	}

	public boolean matches(Predicate<String> predicate) {
		if (this.requirements.length == 0) {
			return false;
		} else {
			for (String[] strings : this.requirements) {
				if (!anyMatch(strings, predicate)) {
					return false;
				}
			}

			return true;
		}
	}

	public int countMatches(Predicate<String> predicate) {
		int i = 0;

		for (String[] strings : this.requirements) {
			if (anyMatch(strings, predicate)) {
				i++;
			}
		}

		return i;
	}

	private static boolean anyMatch(String[] requirements, Predicate<String> predicate) {
		for (String string : requirements) {
			if (predicate.test(string)) {
				return true;
			}
		}

		return false;
	}

	public static AdvancementRequirements fromJson(JsonArray json, Set<String> criteria) {
		String[][] strings = new String[json.size()][];
		Set<String> set = new ObjectOpenHashSet<>();

		for (int i = 0; i < json.size(); i++) {
			JsonArray jsonArray = JsonHelper.asArray(json.get(i), "requirements[" + i + "]");
			if (jsonArray.isEmpty() && criteria.isEmpty()) {
				throw new JsonSyntaxException("Requirement entry cannot be empty");
			}

			strings[i] = new String[jsonArray.size()];

			for (int j = 0; j < jsonArray.size(); j++) {
				String string = JsonHelper.asString(jsonArray.get(j), "requirements[" + i + "][" + j + "]");
				strings[i][j] = string;
				set.add(string);
			}
		}

		if (!criteria.equals(set)) {
			Set<String> set2 = Sets.<String>difference(criteria, set);
			Set<String> set3 = Sets.<String>difference(set, criteria);
			throw new JsonSyntaxException("Advancement completion requirements did not exactly match specified criteria. Missing: " + set2 + ". Unknown: " + set3);
		} else {
			return new AdvancementRequirements(strings);
		}
	}

	public JsonArray toJson() {
		JsonArray jsonArray = new JsonArray();

		for (String[] strings : this.requirements) {
			JsonArray jsonArray2 = new JsonArray();
			Arrays.stream(strings).forEach(jsonArray2::add);
			jsonArray.add(jsonArray2);
		}

		return jsonArray;
	}

	public boolean isEmpty() {
		return this.requirements.length == 0;
	}

	public String toString() {
		return Arrays.deepToString(this.requirements);
	}

	public Set<String> getNames() {
		Set<String> set = new ObjectOpenHashSet<>();

		for (String[] strings : this.requirements) {
			Collections.addAll(set, strings);
		}

		return set;
	}

	public interface CriterionMerger {
		AdvancementRequirements.CriterionMerger AND = AdvancementRequirements::allOf;
		AdvancementRequirements.CriterionMerger OR = AdvancementRequirements::anyOf;

		AdvancementRequirements create(Collection<String> requirements);
	}
}
