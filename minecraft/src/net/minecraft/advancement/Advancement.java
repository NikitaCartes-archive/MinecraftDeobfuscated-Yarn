package net.minecraft.advancement;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.CriterionConditions;
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
import org.apache.commons.lang3.ArrayUtils;

public class Advancement {
	@Nullable
	private final Advancement parent;
	@Nullable
	private final AdvancementDisplay display;
	private final AdvancementRewards rewards;
	private final Identifier id;
	private final Map<String, AdvancementCriterion> criteria;
	private final String[][] requirements;
	private final Set<Advancement> children = Sets.<Advancement>newLinkedHashSet();
	private final Text text;

	public Advancement(
		Identifier id,
		@Nullable Advancement parent,
		@Nullable AdvancementDisplay display,
		AdvancementRewards rewards,
		Map<String, AdvancementCriterion> criteria,
		String[][] requirements
	) {
		this.id = id;
		this.display = display;
		this.criteria = ImmutableMap.copyOf(criteria);
		this.parent = parent;
		this.rewards = rewards;
		this.requirements = requirements;
		if (parent != null) {
			parent.addChild(this);
		}

		if (display == null) {
			this.text = Text.literal(id.toString());
		} else {
			Text text = display.getTitle();
			Formatting formatting = display.getFrame().getTitleFormat();
			Text text2 = Texts.setStyleIfAbsent(text.copy(), Style.EMPTY.withColor(formatting)).append("\n").append(display.getDescription());
			Text text3 = text.copy().styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, text2)));
			this.text = Texts.bracketed(text3).formatted(formatting);
		}
	}

	public Advancement.Builder createTask() {
		return new Advancement.Builder(this.parent == null ? null : this.parent.getId(), this.display, this.rewards, this.criteria, this.requirements);
	}

	@Nullable
	public Advancement getParent() {
		return this.parent;
	}

	@Nullable
	public AdvancementDisplay getDisplay() {
		return this.display;
	}

	public AdvancementRewards getRewards() {
		return this.rewards;
	}

	public String toString() {
		return "SimpleAdvancement{id="
			+ this.getId()
			+ ", parent="
			+ (this.parent == null ? "null" : this.parent.getId())
			+ ", display="
			+ this.display
			+ ", rewards="
			+ this.rewards
			+ ", criteria="
			+ this.criteria
			+ ", requirements="
			+ Arrays.deepToString(this.requirements)
			+ "}";
	}

	public Iterable<Advancement> getChildren() {
		return this.children;
	}

	public Map<String, AdvancementCriterion> getCriteria() {
		return this.criteria;
	}

	public int getRequirementCount() {
		return this.requirements.length;
	}

	public void addChild(Advancement child) {
		this.children.add(child);
	}

	public Identifier getId() {
		return this.id;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			return !(o instanceof Advancement advancement) ? false : this.id.equals(advancement.id);
		}
	}

	public int hashCode() {
		return this.id.hashCode();
	}

	public String[][] getRequirements() {
		return this.requirements;
	}

	public Text toHoverableText() {
		return this.text;
	}

	public static class Builder {
		@Nullable
		private Identifier parentId;
		@Nullable
		private Advancement parentObj;
		@Nullable
		private AdvancementDisplay display;
		private AdvancementRewards rewards = AdvancementRewards.NONE;
		private Map<String, AdvancementCriterion> criteria = Maps.<String, AdvancementCriterion>newLinkedHashMap();
		@Nullable
		private String[][] requirements;
		private CriterionMerger merger = CriterionMerger.AND;

		Builder(
			@Nullable Identifier parentId,
			@Nullable AdvancementDisplay display,
			AdvancementRewards rewards,
			Map<String, AdvancementCriterion> criteria,
			String[][] requirements
		) {
			this.parentId = parentId;
			this.display = display;
			this.rewards = rewards;
			this.criteria = criteria;
			this.requirements = requirements;
		}

		private Builder() {
		}

		public static Advancement.Builder create() {
			return new Advancement.Builder();
		}

		public Advancement.Builder parent(Advancement parent) {
			this.parentObj = parent;
			return this;
		}

		public Advancement.Builder parent(Identifier parentId) {
			this.parentId = parentId;
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
			this.display = display;
			return this;
		}

		public Advancement.Builder rewards(AdvancementRewards.Builder builder) {
			return this.rewards(builder.build());
		}

		public Advancement.Builder rewards(AdvancementRewards rewards) {
			this.rewards = rewards;
			return this;
		}

		public Advancement.Builder criterion(String name, CriterionConditions conditions) {
			return this.criterion(name, new AdvancementCriterion(conditions));
		}

		public Advancement.Builder criterion(String name, AdvancementCriterion criterion) {
			if (this.criteria.containsKey(name)) {
				throw new IllegalArgumentException("Duplicate criterion " + name);
			} else {
				this.criteria.put(name, criterion);
				return this;
			}
		}

		public Advancement.Builder criteriaMerger(CriterionMerger merger) {
			this.merger = merger;
			return this;
		}

		public Advancement.Builder requirements(String[][] requirements) {
			this.requirements = requirements;
			return this;
		}

		public boolean findParent(Function<Identifier, Advancement> parentProvider) {
			if (this.parentId == null) {
				return true;
			} else {
				if (this.parentObj == null) {
					this.parentObj = (Advancement)parentProvider.apply(this.parentId);
				}

				return this.parentObj != null;
			}
		}

		public Advancement build(Identifier id) {
			if (!this.findParent(idx -> null)) {
				throw new IllegalStateException("Tried to build incomplete advancement!");
			} else {
				if (this.requirements == null) {
					this.requirements = this.merger.createRequirements(this.criteria.keySet());
				}

				return new Advancement(id, this.parentObj, this.display, this.rewards, this.criteria, this.requirements);
			}
		}

		public Advancement build(Consumer<Advancement> exporter, String id) {
			Advancement advancement = this.build(new Identifier(id));
			exporter.accept(advancement);
			return advancement;
		}

		public JsonObject toJson() {
			if (this.requirements == null) {
				this.requirements = this.merger.createRequirements(this.criteria.keySet());
			}

			JsonObject jsonObject = new JsonObject();
			if (this.parentObj != null) {
				jsonObject.addProperty("parent", this.parentObj.getId().toString());
			} else if (this.parentId != null) {
				jsonObject.addProperty("parent", this.parentId.toString());
			}

			if (this.display != null) {
				jsonObject.add("display", this.display.toJson());
			}

			jsonObject.add("rewards", this.rewards.toJson());
			JsonObject jsonObject2 = new JsonObject();

			for (Entry<String, AdvancementCriterion> entry : this.criteria.entrySet()) {
				jsonObject2.add((String)entry.getKey(), ((AdvancementCriterion)entry.getValue()).toJson());
			}

			jsonObject.add("criteria", jsonObject2);
			JsonArray jsonArray = new JsonArray();

			for (String[] strings : this.requirements) {
				JsonArray jsonArray2 = new JsonArray();

				for (String string : strings) {
					jsonArray2.add(string);
				}

				jsonArray.add(jsonArray2);
			}

			jsonObject.add("requirements", jsonArray);
			return jsonObject;
		}

		public void toPacket(PacketByteBuf buf) {
			if (this.requirements == null) {
				this.requirements = this.merger.createRequirements(this.criteria.keySet());
			}

			buf.writeNullable(this.parentId, PacketByteBuf::writeIdentifier);
			buf.writeNullable(this.display, (buf2, display) -> display.toPacket(buf2));
			AdvancementCriterion.criteriaToPacket(this.criteria, buf);
			buf.writeVarInt(this.requirements.length);

			for (String[] strings : this.requirements) {
				buf.writeVarInt(strings.length);

				for (String string : strings) {
					buf.writeString(string);
				}
			}
		}

		public String toString() {
			return "Task Advancement{parentId="
				+ this.parentId
				+ ", display="
				+ this.display
				+ ", rewards="
				+ this.rewards
				+ ", criteria="
				+ this.criteria
				+ ", requirements="
				+ Arrays.deepToString(this.requirements)
				+ "}";
		}

		public static Advancement.Builder fromJson(JsonObject obj, AdvancementEntityPredicateDeserializer predicateDeserializer) {
			Identifier identifier = obj.has("parent") ? new Identifier(JsonHelper.getString(obj, "parent")) : null;
			AdvancementDisplay advancementDisplay = obj.has("display") ? AdvancementDisplay.fromJson(JsonHelper.getObject(obj, "display")) : null;
			AdvancementRewards advancementRewards = obj.has("rewards") ? AdvancementRewards.fromJson(JsonHelper.getObject(obj, "rewards")) : AdvancementRewards.NONE;
			Map<String, AdvancementCriterion> map = AdvancementCriterion.criteriaFromJson(JsonHelper.getObject(obj, "criteria"), predicateDeserializer);
			if (map.isEmpty()) {
				throw new JsonSyntaxException("Advancement criteria cannot be empty");
			} else {
				JsonArray jsonArray = JsonHelper.getArray(obj, "requirements", new JsonArray());
				String[][] strings = new String[jsonArray.size()][];

				for (int i = 0; i < jsonArray.size(); i++) {
					JsonArray jsonArray2 = JsonHelper.asArray(jsonArray.get(i), "requirements[" + i + "]");
					strings[i] = new String[jsonArray2.size()];

					for (int j = 0; j < jsonArray2.size(); j++) {
						strings[i][j] = JsonHelper.asString(jsonArray2.get(j), "requirements[" + i + "][" + j + "]");
					}
				}

				if (strings.length == 0) {
					strings = new String[map.size()][];
					int i = 0;

					for (String string : map.keySet()) {
						strings[i++] = new String[]{string};
					}
				}

				for (String[] strings2 : strings) {
					if (strings2.length == 0 && map.isEmpty()) {
						throw new JsonSyntaxException("Requirement entry cannot be empty");
					}

					for (String string2 : strings2) {
						if (!map.containsKey(string2)) {
							throw new JsonSyntaxException("Unknown required criterion '" + string2 + "'");
						}
					}
				}

				for (String string3 : map.keySet()) {
					boolean bl = false;

					for (String[] strings3 : strings) {
						if (ArrayUtils.contains(strings3, string3)) {
							bl = true;
							break;
						}
					}

					if (!bl) {
						throw new JsonSyntaxException(
							"Criterion '" + string3 + "' isn't a requirement for completion. This isn't supported behaviour, all criteria must be required."
						);
					}
				}

				return new Advancement.Builder(identifier, advancementDisplay, advancementRewards, map, strings);
			}
		}

		public static Advancement.Builder fromPacket(PacketByteBuf buf) {
			Identifier identifier = buf.readNullable(PacketByteBuf::readIdentifier);
			AdvancementDisplay advancementDisplay = buf.readNullable(AdvancementDisplay::fromPacket);
			Map<String, AdvancementCriterion> map = AdvancementCriterion.criteriaFromPacket(buf);
			String[][] strings = new String[buf.readVarInt()][];

			for (int i = 0; i < strings.length; i++) {
				strings[i] = new String[buf.readVarInt()];

				for (int j = 0; j < strings[i].length; j++) {
					strings[i][j] = buf.readString();
				}
			}

			return new Advancement.Builder(identifier, advancementDisplay, AdvancementRewards.NONE, map, strings);
		}

		public Map<String, AdvancementCriterion> getCriteria() {
			return this.criteria;
		}
	}
}
