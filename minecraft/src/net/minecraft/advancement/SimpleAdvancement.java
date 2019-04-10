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
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.event.HoverEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import org.apache.commons.lang3.ArrayUtils;

public class SimpleAdvancement {
	private final SimpleAdvancement parent;
	private final AdvancementDisplay display;
	private final AdvancementRewards rewards;
	private final Identifier id;
	private final Map<String, AdvancementCriterion> criteria;
	private final String[][] requirements;
	private final Set<SimpleAdvancement> children = Sets.<SimpleAdvancement>newLinkedHashSet();
	private final TextComponent textComponent;

	public SimpleAdvancement(
		Identifier identifier,
		@Nullable SimpleAdvancement simpleAdvancement,
		@Nullable AdvancementDisplay advancementDisplay,
		AdvancementRewards advancementRewards,
		Map<String, AdvancementCriterion> map,
		String[][] strings
	) {
		this.id = identifier;
		this.display = advancementDisplay;
		this.criteria = ImmutableMap.copyOf(map);
		this.parent = simpleAdvancement;
		this.rewards = advancementRewards;
		this.requirements = strings;
		if (simpleAdvancement != null) {
			simpleAdvancement.addChild(this);
		}

		if (advancementDisplay == null) {
			this.textComponent = new StringTextComponent(identifier.toString());
		} else {
			TextComponent textComponent = advancementDisplay.getTitle();
			TextFormat textFormat = advancementDisplay.getFrame().getTitleFormat();
			TextComponent textComponent2 = textComponent.copy().applyFormat(textFormat).append("\n").append(advancementDisplay.getDescription());
			TextComponent textComponent3 = textComponent.copy().modifyStyle(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, textComponent2)));
			this.textComponent = new StringTextComponent("[").append(textComponent3).append("]").applyFormat(textFormat);
		}
	}

	public SimpleAdvancement.Task createTask() {
		return new SimpleAdvancement.Task(this.parent == null ? null : this.parent.getId(), this.display, this.rewards, this.criteria, this.requirements);
	}

	@Nullable
	public SimpleAdvancement getParent() {
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

	public Iterable<SimpleAdvancement> getChildren() {
		return this.children;
	}

	public Map<String, AdvancementCriterion> getCriteria() {
		return this.criteria;
	}

	@Environment(EnvType.CLIENT)
	public int getRequirementCount() {
		return this.requirements.length;
	}

	public void addChild(SimpleAdvancement simpleAdvancement) {
		this.children.add(simpleAdvancement);
	}

	public Identifier getId() {
		return this.id;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof SimpleAdvancement)) {
			return false;
		} else {
			SimpleAdvancement simpleAdvancement = (SimpleAdvancement)object;
			return this.id.equals(simpleAdvancement.id);
		}
	}

	public int hashCode() {
		return this.id.hashCode();
	}

	public String[][] getRequirements() {
		return this.requirements;
	}

	public TextComponent getTextComponent() {
		return this.textComponent;
	}

	public static class Task {
		private Identifier parentId;
		private SimpleAdvancement parentObj;
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

		public static SimpleAdvancement.Task create() {
			return new SimpleAdvancement.Task();
		}

		public SimpleAdvancement.Task parent(SimpleAdvancement simpleAdvancement) {
			this.parentObj = simpleAdvancement;
			return this;
		}

		public SimpleAdvancement.Task parent(Identifier identifier) {
			this.parentId = identifier;
			return this;
		}

		public SimpleAdvancement.Task display(
			ItemProvider itemProvider,
			TextComponent textComponent,
			TextComponent textComponent2,
			@Nullable Identifier identifier,
			AdvancementFrame advancementFrame,
			boolean bl,
			boolean bl2,
			boolean bl3
		) {
			return this.display(new AdvancementDisplay(new ItemStack(itemProvider.getItem()), textComponent, textComponent2, identifier, advancementFrame, bl, bl2, bl3));
		}

		public SimpleAdvancement.Task display(AdvancementDisplay advancementDisplay) {
			this.display = advancementDisplay;
			return this;
		}

		public SimpleAdvancement.Task rewards(AdvancementRewards.Builder builder) {
			return this.rewards(builder.build());
		}

		public SimpleAdvancement.Task rewards(AdvancementRewards advancementRewards) {
			this.rewards = advancementRewards;
			return this;
		}

		public SimpleAdvancement.Task criterion(String string, CriterionConditions criterionConditions) {
			return this.criterion(string, new AdvancementCriterion(criterionConditions));
		}

		public SimpleAdvancement.Task criterion(String string, AdvancementCriterion advancementCriterion) {
			if (this.criteria.containsKey(string)) {
				throw new IllegalArgumentException("Duplicate criterion " + string);
			} else {
				this.criteria.put(string, advancementCriterion);
				return this;
			}
		}

		public SimpleAdvancement.Task criteriaMerger(CriteriaMerger criteriaMerger) {
			this.merger = criteriaMerger;
			return this;
		}

		public boolean findParent(Function<Identifier, SimpleAdvancement> function) {
			if (this.parentId == null) {
				return true;
			} else {
				if (this.parentObj == null) {
					this.parentObj = (SimpleAdvancement)function.apply(this.parentId);
				}

				return this.parentObj != null;
			}
		}

		public SimpleAdvancement build(Identifier identifier) {
			if (!this.findParent(identifierx -> null)) {
				throw new IllegalStateException("Tried to build incomplete advancement!");
			} else {
				if (this.requirements == null) {
					this.requirements = this.merger.createRequirements(this.criteria.keySet());
				}

				return new SimpleAdvancement(identifier, this.parentObj, this.display, this.rewards, this.criteria, this.requirements);
			}
		}

		public SimpleAdvancement build(Consumer<SimpleAdvancement> consumer, String string) {
			SimpleAdvancement simpleAdvancement = this.build(new Identifier(string));
			consumer.accept(simpleAdvancement);
			return simpleAdvancement;
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

		public static SimpleAdvancement.Task fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
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

				return new SimpleAdvancement.Task(identifier, advancementDisplay, advancementRewards, map, strings);
			}
		}

		public static SimpleAdvancement.Task fromPacket(PacketByteBuf packetByteBuf) {
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

			return new SimpleAdvancement.Task(identifier, advancementDisplay, AdvancementRewards.NONE, map, strings);
		}

		public Map<String, AdvancementCriterion> getCriteria() {
			return this.criteria;
		}
	}
}
