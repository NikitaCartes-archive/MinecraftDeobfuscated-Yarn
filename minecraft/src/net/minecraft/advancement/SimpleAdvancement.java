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
	private final AdvancementDisplay field_1146;
	private final AdvancementRewards field_1145;
	private final Identifier field_1144;
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
		this.field_1144 = identifier;
		this.field_1146 = advancementDisplay;
		this.criteria = ImmutableMap.copyOf(map);
		this.parent = simpleAdvancement;
		this.field_1145 = advancementRewards;
		this.requirements = strings;
		if (simpleAdvancement != null) {
			simpleAdvancement.addChild(this);
		}

		if (advancementDisplay == null) {
			this.textComponent = new StringTextComponent(identifier.toString());
		} else {
			TextComponent textComponent = advancementDisplay.getTitle();
			TextFormat textFormat = advancementDisplay.method_815().getTitleFormat();
			TextComponent textComponent2 = textComponent.copy().applyFormat(textFormat).append("\n").append(advancementDisplay.getDescription());
			TextComponent textComponent3 = textComponent.copy().modifyStyle(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, textComponent2)));
			this.textComponent = new StringTextComponent("[").append(textComponent3).append("]").applyFormat(textFormat);
		}
	}

	public SimpleAdvancement.Builder createTask() {
		return new SimpleAdvancement.Builder(
			this.parent == null ? null : this.parent.method_688(), this.field_1146, this.field_1145, this.criteria, this.requirements
		);
	}

	@Nullable
	public SimpleAdvancement getParent() {
		return this.parent;
	}

	@Nullable
	public AdvancementDisplay method_686() {
		return this.field_1146;
	}

	public AdvancementRewards method_691() {
		return this.field_1145;
	}

	public String toString() {
		return "SimpleAdvancement{id="
			+ this.method_688()
			+ ", parent="
			+ (this.parent == null ? "null" : this.parent.method_688())
			+ ", display="
			+ this.field_1146
			+ ", rewards="
			+ this.field_1145
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

	public Identifier method_688() {
		return this.field_1144;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof SimpleAdvancement)) {
			return false;
		} else {
			SimpleAdvancement simpleAdvancement = (SimpleAdvancement)object;
			return this.field_1144.equals(simpleAdvancement.field_1144);
		}
	}

	public int hashCode() {
		return this.field_1144.hashCode();
	}

	public String[][] getRequirements() {
		return this.requirements;
	}

	public TextComponent getTextComponent() {
		return this.textComponent;
	}

	public static class Builder {
		private Identifier field_1152;
		private SimpleAdvancement parentObj;
		private AdvancementDisplay field_1147;
		private AdvancementRewards field_1153 = AdvancementRewards.NONE;
		private Map<String, AdvancementCriterion> criteria = Maps.<String, AdvancementCriterion>newLinkedHashMap();
		private String[][] requirements;
		private CriteriaMerger merger = CriteriaMerger.AND;

		private Builder(
			@Nullable Identifier identifier,
			@Nullable AdvancementDisplay advancementDisplay,
			AdvancementRewards advancementRewards,
			Map<String, AdvancementCriterion> map,
			String[][] strings
		) {
			this.field_1152 = identifier;
			this.field_1147 = advancementDisplay;
			this.field_1153 = advancementRewards;
			this.criteria = map;
			this.requirements = strings;
		}

		private Builder() {
		}

		public static SimpleAdvancement.Builder create() {
			return new SimpleAdvancement.Builder();
		}

		public SimpleAdvancement.Builder parent(SimpleAdvancement simpleAdvancement) {
			this.parentObj = simpleAdvancement;
			return this;
		}

		public SimpleAdvancement.Builder method_708(Identifier identifier) {
			this.field_1152 = identifier;
			return this;
		}

		public SimpleAdvancement.Builder method_697(
			ItemProvider itemProvider,
			TextComponent textComponent,
			TextComponent textComponent2,
			@Nullable Identifier identifier,
			AdvancementFrame advancementFrame,
			boolean bl,
			boolean bl2,
			boolean bl3
		) {
			return this.method_693(
				new AdvancementDisplay(new ItemStack(itemProvider.getItem()), textComponent, textComponent2, identifier, advancementFrame, bl, bl2, bl3)
			);
		}

		public SimpleAdvancement.Builder method_693(AdvancementDisplay advancementDisplay) {
			this.field_1147 = advancementDisplay;
			return this;
		}

		public SimpleAdvancement.Builder method_703(AdvancementRewards.Builder builder) {
			return this.method_706(builder.build());
		}

		public SimpleAdvancement.Builder method_706(AdvancementRewards advancementRewards) {
			this.field_1153 = advancementRewards;
			return this;
		}

		public SimpleAdvancement.Builder method_709(String string, CriterionConditions criterionConditions) {
			return this.method_705(string, new AdvancementCriterion(criterionConditions));
		}

		public SimpleAdvancement.Builder method_705(String string, AdvancementCriterion advancementCriterion) {
			if (this.criteria.containsKey(string)) {
				throw new IllegalArgumentException("Duplicate criterion " + string);
			} else {
				this.criteria.put(string, advancementCriterion);
				return this;
			}
		}

		public SimpleAdvancement.Builder criteriaMerger(CriteriaMerger criteriaMerger) {
			this.merger = criteriaMerger;
			return this;
		}

		public boolean findParent(Function<Identifier, SimpleAdvancement> function) {
			if (this.field_1152 == null) {
				return true;
			} else {
				if (this.parentObj == null) {
					this.parentObj = (SimpleAdvancement)function.apply(this.field_1152);
				}

				return this.parentObj != null;
			}
		}

		public SimpleAdvancement method_695(Identifier identifier) {
			if (!this.findParent(identifierx -> null)) {
				throw new IllegalStateException("Tried to build incomplete advancement!");
			} else {
				if (this.requirements == null) {
					this.requirements = this.merger.createRequirements(this.criteria.keySet());
				}

				return new SimpleAdvancement(identifier, this.parentObj, this.field_1147, this.field_1153, this.criteria, this.requirements);
			}
		}

		public SimpleAdvancement build(Consumer<SimpleAdvancement> consumer, String string) {
			SimpleAdvancement simpleAdvancement = this.method_695(new Identifier(string));
			consumer.accept(simpleAdvancement);
			return simpleAdvancement;
		}

		public JsonObject toJson() {
			if (this.requirements == null) {
				this.requirements = this.merger.createRequirements(this.criteria.keySet());
			}

			JsonObject jsonObject = new JsonObject();
			if (this.parentObj != null) {
				jsonObject.addProperty("parent", this.parentObj.method_688().toString());
			} else if (this.field_1152 != null) {
				jsonObject.addProperty("parent", this.field_1152.toString());
			}

			if (this.field_1147 != null) {
				jsonObject.add("display", this.field_1147.toJson());
			}

			jsonObject.add("rewards", this.field_1153.toJson());
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

		public void serialize(PacketByteBuf packetByteBuf) {
			if (this.field_1152 == null) {
				packetByteBuf.writeBoolean(false);
			} else {
				packetByteBuf.writeBoolean(true);
				packetByteBuf.method_10812(this.field_1152);
			}

			if (this.field_1147 == null) {
				packetByteBuf.writeBoolean(false);
			} else {
				packetByteBuf.writeBoolean(true);
				this.field_1147.toPacket(packetByteBuf);
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
				+ this.field_1152
				+ ", display="
				+ this.field_1147
				+ ", rewards="
				+ this.field_1153
				+ ", criteria="
				+ this.criteria
				+ ", requirements="
				+ Arrays.deepToString(this.requirements)
				+ '}';
		}

		public static SimpleAdvancement.Builder deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			Identifier identifier = jsonObject.has("parent") ? new Identifier(JsonHelper.getString(jsonObject, "parent")) : null;
			AdvancementDisplay advancementDisplay = jsonObject.has("display")
				? AdvancementDisplay.deserialize(JsonHelper.getObject(jsonObject, "display"), jsonDeserializationContext)
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

				return new SimpleAdvancement.Builder(identifier, advancementDisplay, advancementRewards, map, strings);
			}
		}

		public static SimpleAdvancement.Builder deserialize(PacketByteBuf packetByteBuf) {
			Identifier identifier = packetByteBuf.readBoolean() ? packetByteBuf.method_10810() : null;
			AdvancementDisplay advancementDisplay = packetByteBuf.readBoolean() ? AdvancementDisplay.fromPacket(packetByteBuf) : null;
			Map<String, AdvancementCriterion> map = AdvancementCriterion.fromPacket(packetByteBuf);
			String[][] strings = new String[packetByteBuf.readVarInt()][];

			for (int i = 0; i < strings.length; i++) {
				strings[i] = new String[packetByteBuf.readVarInt()];

				for (int j = 0; j < strings[i].length; j++) {
					strings[i][j] = packetByteBuf.readString(32767);
				}
			}

			return new SimpleAdvancement.Builder(identifier, advancementDisplay, AdvancementRewards.NONE, map, strings);
		}

		public Map<String, AdvancementCriterion> getCriteria() {
			return this.criteria;
		}
	}
}
