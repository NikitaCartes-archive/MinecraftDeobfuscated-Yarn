package net.minecraft.advancement;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import org.apache.commons.lang3.ArrayUtils;

public class Advancement {
	private final Advancement parent;
	private final AdvancementDisplay display;
	private final AdvancementRewards rewards;
	private final Identifier id;
	private final Map<String, AdvancementCriterion> criteria;
	private final String[][] requirements;
	private final Set<Advancement> children = Sets.<Advancement>newLinkedHashSet();
	private final Text text;

	public Advancement(
		Identifier identifier,
		@Nullable Advancement advancement,
		@Nullable AdvancementDisplay advancementDisplay,
		AdvancementRewards advancementRewards,
		Map<String, AdvancementCriterion> map,
		String[][] strings
	) {
		this.id = identifier;
		this.display = advancementDisplay;
		this.criteria = ImmutableMap.copyOf(map);
		this.parent = advancement;
		this.rewards = advancementRewards;
		this.requirements = strings;
		if (advancement != null) {
			advancement.addChild(this);
		}

		if (advancementDisplay == null) {
			this.text = new LiteralText(identifier.toString());
		} else {
			Text text = advancementDisplay.getTitle();
			Formatting formatting = advancementDisplay.getFrame().getTitleFormat();
			Text text2 = text.deepCopy().formatted(formatting).append("\n").append(advancementDisplay.getDescription());
			Text text3 = text.deepCopy().styled(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, text2)));
			this.text = new LiteralText("[").append(text3).append("]").formatted(formatting);
		}
	}

	public Advancement.Task createTask() {
		return new Advancement.Task(this.parent == null ? null : this.parent.getId(), this.display, this.rewards, this.criteria, this.requirements);
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
			+ '}';
	}

	public Iterable<Advancement> getChildren() {
		return this.children;
	}

	public Map<String, AdvancementCriterion> getCriteria() {
		return this.criteria;
	}

	@Environment(EnvType.CLIENT)
	public int getRequirementCount() {
		return this.requirements.length;
	}

	public void addChild(Advancement advancement) {
		this.children.add(advancement);
	}

	public Identifier getId() {
		return this.id;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof Advancement)) {
			return false;
		} else {
			Advancement advancement = (Advancement)object;
			return this.id.equals(advancement.id);
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

	public static class Task {
		private Identifier parentId;
		private Advancement parentObj;
		private AdvancementDisplay display;
		private AdvancementRewards rewards = AdvancementRewards.NONE;
		private Map<String, AdvancementCriterion> criteria = Maps.<String, AdvancementCriterion>newLinkedHashMap();
		private String[][] requirements;
		private CriteriaMerger merger = CriteriaMerger.AND;

		private Task(
			@Nullable Identifier identifier,
			@Nullable AdvancementDisplay advancementDisplay,
			AdvancementRewards advancementRewards,
			Map<String, AdvancementCriterion> map,
			String[][] strings
		) {
			this.parentId = identifier;
			this.display = advancementDisplay;
			this.rewards = advancementRewards;
			this.criteria = map;
			this.requirements = strings;
		}

		private Task() {
		}

		public static Advancement.Task create() {
			return new Advancement.Task();
		}

		public Advancement.Task parent(Advancement advancement) {
			this.parentObj = advancement;
			return this;
		}

		public Advancement.Task parent(Identifier identifier) {
			this.parentId = identifier;
			return this;
		}

		public Advancement.Task display(
			ItemStack itemStack, Text text, Text text2, @Nullable Identifier identifier, AdvancementFrame advancementFrame, boolean bl, boolean bl2, boolean bl3
		) {
			return this.display(new AdvancementDisplay(itemStack, text, text2, identifier, advancementFrame, bl, bl2, bl3));
		}

		public Advancement.Task display(
			ItemConvertible itemConvertible,
			Text text,
			Text text2,
			@Nullable Identifier identifier,
			AdvancementFrame advancementFrame,
			boolean bl,
			boolean bl2,
			boolean bl3
		) {
			return this.display(new AdvancementDisplay(new ItemStack(itemConvertible.asItem()), text, text2, identifier, advancementFrame, bl, bl2, bl3));
		}

		public Advancement.Task display(AdvancementDisplay advancementDisplay) {
			this.display = advancementDisplay;
			return this;
		}

		public Advancement.Task rewards(AdvancementRewards.Builder builder) {
			return this.rewards(builder.build());
		}

		public Advancement.Task rewards(AdvancementRewards advancementRewards) {
			this.rewards = advancementRewards;
			return this;
		}

		public Advancement.Task criterion(String string, CriterionConditions criterionConditions) {
			return this.criterion(string, new AdvancementCriterion(criterionConditions));
		}

		public Advancement.Task criterion(String string, AdvancementCriterion advancementCriterion) {
			if (this.criteria.containsKey(string)) {
				throw new IllegalArgumentException("Duplicate criterion " + string);
			} else {
				this.criteria.put(string, advancementCriterion);
				return this;
			}
		}

		public Advancement.Task criteriaMerger(CriteriaMerger criteriaMerger) {
			this.merger = criteriaMerger;
			return this;
		}

		public boolean findParent(Function<Identifier, Advancement> function) {
			if (this.parentId == null) {
				return true;
			} else {
				if (this.parentObj == null) {
					this.parentObj = (Advancement)function.apply(this.parentId);
				}

				return this.parentObj != null;
			}
		}

		public Advancement build(Identifier identifier) {
			if (!this.findParent(identifierx -> null)) {
				throw new IllegalStateException("Tried to build incomplete advancement!");
			} else {
				if (this.requirements == null) {
					this.requirements = this.merger.createRequirements(this.criteria.keySet());
				}

				return new Advancement(identifier, this.parentObj, this.display, this.rewards, this.criteria, this.requirements);
			}
		}

		public Advancement build(Consumer<Advancement> consumer, String string) {
			Advancement advancement = this.build(new Identifier(string));
			consumer.accept(advancement);
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

		public void toPacket(PacketByteBuf packetByteBuf) {
			if (this.parentId == null) {
				packetByteBuf.writeBoolean(false);
			} else {
				packetByteBuf.writeBoolean(true);
				packetByteBuf.writeIdentifier(this.parentId);
			}

			if (this.display == null) {
				packetByteBuf.writeBoolean(false);
			} else {
				packetByteBuf.writeBoolean(true);
				this.display.toPacket(packetByteBuf);
			}

			AdvancementCriterion.serialize(this.criteria, packetByteBuf);
			packetByteBuf.writeVarInt(this.requirements.length);

			for (String[] strings : this.requirements) {
				packetByteBuf.writeVarInt(strings.length);

				for (String string : strings) {
					packetByteBuf.writeString(string);
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
				+ '}';
		}

		public static Advancement.Task fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			Identifier identifier = jsonObject.has("parent") ? new Identifier(JsonHelper.getString(jsonObject, "parent")) : null;
			AdvancementDisplay advancementDisplay = jsonObject.has("display")
				? AdvancementDisplay.fromJson(JsonHelper.getObject(jsonObject, "display"), jsonDeserializationContext)
				: null;
			AdvancementRewards advancementRewards = JsonHelper.deserialize(
				jsonObject, "rewards", AdvancementRewards.NONE, jsonDeserializationContext, AdvancementRewards.class
			);
			Map<String, AdvancementCriterion> map = AdvancementCriterion.fromJson(JsonHelper.getObject(jsonObject, "criteria"), jsonDeserializationContext);
			if (map.isEmpty()) {
				throw new JsonSyntaxException("Advancement criteria cannot be empty");
			} else {
				JsonArray jsonArray = JsonHelper.getArray(jsonObject, "requirements", new JsonArray());
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

				return new Advancement.Task(identifier, advancementDisplay, advancementRewards, map, strings);
			}
		}

		public static Advancement.Task fromPacket(PacketByteBuf packetByteBuf) {
			Identifier identifier = packetByteBuf.readBoolean() ? packetByteBuf.readIdentifier() : null;
			AdvancementDisplay advancementDisplay = packetByteBuf.readBoolean() ? AdvancementDisplay.fromPacket(packetByteBuf) : null;
			Map<String, AdvancementCriterion> map = AdvancementCriterion.fromPacket(packetByteBuf);
			String[][] strings = new String[packetByteBuf.readVarInt()][];

			for (int i = 0; i < strings.length; i++) {
				strings[i] = new String[packetByteBuf.readVarInt()];

				for (int j = 0; j < strings[i].length; j++) {
					strings[i][j] = packetByteBuf.readString(32767);
				}
			}

			return new Advancement.Task(identifier, advancementDisplay, AdvancementRewards.NONE, map, strings);
		}

		public Map<String, AdvancementCriterion> getCriteria() {
			return this.criteria;
		}
	}
}
