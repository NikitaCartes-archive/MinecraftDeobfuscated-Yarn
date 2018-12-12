package net.minecraft;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriteriaMerger;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemProvider;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class class_3893 {
	private final Item field_17185;
	private final Ingredient field_17186;
	private final float field_17187;
	private final int field_17188;
	private final SimpleAdvancement.Builder field_17189 = SimpleAdvancement.Builder.create();
	private String field_17190;

	public class_3893(Ingredient ingredient, ItemProvider itemProvider, float f, int i) {
		this.field_17185 = itemProvider.getItem();
		this.field_17186 = ingredient;
		this.field_17187 = f;
		this.field_17188 = i;
	}

	public static class_3893 method_17181(Ingredient ingredient, ItemProvider itemProvider, float f, int i) {
		return new class_3893(ingredient, itemProvider, f, i);
	}

	public class_3893 method_17182(String string, CriterionConditions criterionConditions) {
		this.field_17189.criterion(string, criterionConditions);
		return this;
	}

	public void method_17183(Consumer<class_2444> consumer, String string) {
		Identifier identifier = Registry.ITEM.getId(this.field_17185);
		if (new Identifier(string).equals(identifier)) {
			throw new IllegalStateException("Smoking Recipe " + string + " should remove its 'save' argument");
		} else {
			this.method_17184(consumer, new Identifier(string));
		}
	}

	public void method_17184(Consumer<class_2444> consumer, Identifier identifier) {
		this.method_17185(identifier);
		this.field_17189
			.parent(new Identifier("recipes/root"))
			.criterion("has_the_recipe", new RecipeUnlockedCriterion.Conditions(identifier))
			.rewards(AdvancementRewards.Builder.recipe(identifier))
			.criteriaMerger(CriteriaMerger.OR);
		consumer.accept(
			new class_3893.class_3894(
				identifier,
				this.field_17190 == null ? "" : this.field_17190,
				this.field_17186,
				this.field_17185,
				this.field_17187,
				this.field_17188,
				this.field_17189,
				new Identifier(identifier.getNamespace(), "recipes/" + this.field_17185.getItemGroup().method_7751() + "/" + identifier.getPath())
			)
		);
	}

	private void method_17185(Identifier identifier) {
		if (this.field_17189.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + identifier);
		}
	}

	public static class class_3894 implements class_2444 {
		private final Identifier field_17191;
		private final String field_17192;
		private final Ingredient field_17193;
		private final Item field_17194;
		private final float field_17195;
		private final int field_17196;
		private final SimpleAdvancement.Builder field_17197;
		private final Identifier field_17198;

		public class_3894(
			Identifier identifier, String string, Ingredient ingredient, Item item, float f, int i, SimpleAdvancement.Builder builder, Identifier identifier2
		) {
			this.field_17191 = identifier;
			this.field_17192 = string;
			this.field_17193 = ingredient;
			this.field_17194 = item;
			this.field_17195 = f;
			this.field_17196 = i;
			this.field_17197 = builder;
			this.field_17198 = identifier2;
		}

		@Override
		public JsonObject method_10416() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("type", "smoking");
			if (!this.field_17192.isEmpty()) {
				jsonObject.addProperty("group", this.field_17192);
			}

			jsonObject.add("ingredient", this.field_17193.toJson());
			jsonObject.addProperty("result", Registry.ITEM.getId(this.field_17194).toString());
			jsonObject.addProperty("experience", this.field_17195);
			jsonObject.addProperty("cookingtime", this.field_17196);
			return jsonObject;
		}

		@Override
		public Identifier method_10417() {
			return this.field_17191;
		}

		@Nullable
		@Override
		public JsonObject method_10415() {
			return this.field_17197.toJson();
		}

		@Nullable
		@Override
		public Identifier method_10418() {
			return this.field_17198;
		}
	}
}
