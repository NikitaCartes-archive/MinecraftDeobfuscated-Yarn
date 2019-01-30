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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextTypes;

public class AdvancementRewards {
	public static final AdvancementRewards NONE = new AdvancementRewards(0, new Identifier[0], new Identifier[0], CommandFunction.LazyContainer.EMPTY);
	private final int experience;
	private final Identifier[] loot;
	private final Identifier[] recipes;
	private final CommandFunction.LazyContainer function;

	public AdvancementRewards(int i, Identifier[] identifiers, Identifier[] identifiers2, CommandFunction.LazyContainer lazyContainer) {
		this.experience = i;
		this.loot = identifiers;
		this.recipes = identifiers2;
		this.function = lazyContainer;
	}

	public void apply(ServerPlayerEntity serverPlayerEntity) {
		serverPlayerEntity.addExperience(this.experience);
		LootContext lootContext = new LootContext.Builder(serverPlayerEntity.getServerWorld())
			.put(LootContextParameters.field_1226, serverPlayerEntity)
			.put(LootContextParameters.field_1232, new BlockPos(serverPlayerEntity))
			.setRandom(serverPlayerEntity.getRand())
			.build(LootContextTypes.ADVANCEMENT_REWARD);
		boolean bl = false;

		for (Identifier identifier : this.loot) {
			for (ItemStack itemStack : serverPlayerEntity.server.getLootManager().getSupplier(identifier).getDrops(lootContext)) {
				if (serverPlayerEntity.method_7270(itemStack)) {
					serverPlayerEntity.world
						.playSound(
							null,
							serverPlayerEntity.x,
							serverPlayerEntity.y,
							serverPlayerEntity.z,
							SoundEvents.field_15197,
							SoundCategory.field_15248,
							0.2F,
							((serverPlayerEntity.getRand().nextFloat() - serverPlayerEntity.getRand().nextFloat()) * 0.7F + 1.0F) * 2.0F
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
			serverPlayerEntity.containerPlayer.sendContentUpdates();
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

		public static AdvancementRewards.Builder experience(int i) {
			return new AdvancementRewards.Builder().setExperience(i);
		}

		public AdvancementRewards.Builder setExperience(int i) {
			this.experience += i;
			return this;
		}

		public static AdvancementRewards.Builder recipe(Identifier identifier) {
			return new AdvancementRewards.Builder().addRecipe(identifier);
		}

		public AdvancementRewards.Builder addRecipe(Identifier identifier) {
			this.recipes.add(identifier);
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
