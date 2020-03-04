package net.minecraft.advancement;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class AdvancementRewards {
	public static final AdvancementRewards NONE = new AdvancementRewards(0, new Identifier[0], new Identifier[0], CommandFunction.LazyContainer.EMPTY);
	private final int experience;
	private final Identifier[] loot;
	private final Identifier[] recipes;
	private final CommandFunction.LazyContainer function;

	public AdvancementRewards(int experience, Identifier[] loot, Identifier[] recipes, CommandFunction.LazyContainer lazyContainer) {
		this.experience = experience;
		this.loot = loot;
		this.recipes = recipes;
		this.function = lazyContainer;
	}

	public void apply(ServerPlayerEntity serverPlayerEntity) {
		serverPlayerEntity.addExperience(this.experience);
		LootContext lootContext = new LootContext.Builder(serverPlayerEntity.getServerWorld())
			.put(LootContextParameters.THIS_ENTITY, serverPlayerEntity)
			.put(LootContextParameters.POSITION, serverPlayerEntity.getSenseCenterPos())
			.setRandom(serverPlayerEntity.getRandom())
			.build(LootContextTypes.ADVANCEMENT_REWARD);
		boolean bl = false;

		for (Identifier identifier : this.loot) {
			for (ItemStack itemStack : serverPlayerEntity.server.getLootManager().getTable(identifier).getDrops(lootContext)) {
				if (serverPlayerEntity.giveItemStack(itemStack)) {
					serverPlayerEntity.world
						.playSound(
							null,
							serverPlayerEntity.getX(),
							serverPlayerEntity.getY(),
							serverPlayerEntity.getZ(),
							SoundEvents.ENTITY_ITEM_PICKUP,
							SoundCategory.PLAYERS,
							0.2F,
							((serverPlayerEntity.getRandom().nextFloat() - serverPlayerEntity.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F
						);
					bl = true;
				} else {
					ItemEntity itemEntity = serverPlayerEntity.dropItem(itemStack, false);
					if (itemEntity != null) {
						itemEntity.resetPickupDelay();
						itemEntity.setOwner(serverPlayerEntity.getUuid());
					}
				}
			}
		}

		if (bl) {
			serverPlayerEntity.playerScreenHandler.sendContentUpdates();
		}

		if (this.recipes.length > 0) {
			serverPlayerEntity.unlockRecipes(this.recipes);
		}

		MinecraftServer minecraftServer = serverPlayerEntity.server;
		this.function
			.get(minecraftServer.getCommandFunctionManager())
			.ifPresent(
				commandFunction -> minecraftServer.getCommandFunctionManager().execute(commandFunction, serverPlayerEntity.getCommandSource().withSilent().withLevel(2))
			);
	}

	public String toString() {
		return "AdvancementRewards{experience="
			+ this.experience
			+ ", loot="
			+ Arrays.toString(this.loot)
			+ ", recipes="
			+ Arrays.toString(this.recipes)
			+ ", function="
			+ this.function
			+ '}';
	}

	public JsonElement toJson() {
		if (this == NONE) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			if (this.experience != 0) {
				jsonObject.addProperty("experience", this.experience);
			}

			if (this.loot.length > 0) {
				JsonArray jsonArray = new JsonArray();

				for (Identifier identifier : this.loot) {
					jsonArray.add(identifier.toString());
				}

				jsonObject.add("loot", jsonArray);
			}

			if (this.recipes.length > 0) {
				JsonArray jsonArray = new JsonArray();

				for (Identifier identifier : this.recipes) {
					jsonArray.add(identifier.toString());
				}

				jsonObject.add("recipes", jsonArray);
			}

			if (this.function.getId() != null) {
				jsonObject.addProperty("function", this.function.getId().toString());
			}

			return jsonObject;
		}
	}

	public static class Builder {
		private int experience;
		private final List<Identifier> loot = Lists.<Identifier>newArrayList();
		private final List<Identifier> recipes = Lists.<Identifier>newArrayList();
		@Nullable
		private Identifier function;

		public static AdvancementRewards.Builder experience(int experience) {
			return new AdvancementRewards.Builder().setExperience(experience);
		}

		public AdvancementRewards.Builder setExperience(int experience) {
			this.experience += experience;
			return this;
		}

		public static AdvancementRewards.Builder recipe(Identifier recipe) {
			return new AdvancementRewards.Builder().addRecipe(recipe);
		}

		public AdvancementRewards.Builder addRecipe(Identifier recipe) {
			this.recipes.add(recipe);
			return this;
		}

		public AdvancementRewards build() {
			return new AdvancementRewards(
				this.experience,
				(Identifier[])this.loot.toArray(new Identifier[0]),
				(Identifier[])this.recipes.toArray(new Identifier[0]),
				this.function == null ? CommandFunction.LazyContainer.EMPTY : new CommandFunction.LazyContainer(this.function)
			);
		}
	}

	public static class Deserializer implements JsonDeserializer<AdvancementRewards> {
		public AdvancementRewards deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "rewards");
			int i = JsonHelper.getInt(jsonObject, "experience", 0);
			JsonArray jsonArray = JsonHelper.getArray(jsonObject, "loot", new JsonArray());
			Identifier[] identifiers = new Identifier[jsonArray.size()];

			for (int j = 0; j < identifiers.length; j++) {
				identifiers[j] = new Identifier(JsonHelper.asString(jsonArray.get(j), "loot[" + j + "]"));
			}

			JsonArray jsonArray2 = JsonHelper.getArray(jsonObject, "recipes", new JsonArray());
			Identifier[] identifiers2 = new Identifier[jsonArray2.size()];

			for (int k = 0; k < identifiers2.length; k++) {
				identifiers2[k] = new Identifier(JsonHelper.asString(jsonArray2.get(k), "recipes[" + k + "]"));
			}

			CommandFunction.LazyContainer lazyContainer;
			if (jsonObject.has("function")) {
				lazyContainer = new CommandFunction.LazyContainer(new Identifier(JsonHelper.getString(jsonObject, "function")));
			} else {
				lazyContainer = CommandFunction.LazyContainer.EMPTY;
			}

			return new AdvancementRewards(i, identifiers, identifiers2, lazyContainer);
		}
	}
}
