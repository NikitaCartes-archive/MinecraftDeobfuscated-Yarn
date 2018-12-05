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
import net.minecraft.world.loot.context.LootContextTypes;
import net.minecraft.world.loot.context.Parameters;

public class AdvancementRewards {
	public static final AdvancementRewards NONE = new AdvancementRewards(0, new Identifier[0], new Identifier[0], CommandFunction.class_2159.field_9809);
	private final int experience;
	private final Identifier[] loot;
	private final Identifier[] recipes;
	private final CommandFunction.class_2159 field_1163;

	public AdvancementRewards(int i, Identifier[] identifiers, Identifier[] identifiers2, CommandFunction.class_2159 arg) {
		this.experience = i;
		this.loot = identifiers;
		this.recipes = identifiers2;
		this.field_1163 = arg;
	}

	public void apply(ServerPlayerEntity serverPlayerEntity) {
		serverPlayerEntity.addExperience(this.experience);
		LootContext lootContext = new LootContext.Builder(serverPlayerEntity.getServerWorld())
			.put(Parameters.field_1226, serverPlayerEntity)
			.put(Parameters.field_1232, new BlockPos(serverPlayerEntity))
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
			serverPlayerEntity.method_7335(this.recipes);
		}

		MinecraftServer minecraftServer = serverPlayerEntity.server;
		CommandFunction commandFunction = this.field_1163.method_9196(minecraftServer.getCommandFunctionManager());
		if (commandFunction != null) {
			minecraftServer.getCommandFunctionManager().method_12904(commandFunction, serverPlayerEntity.getCommandSource().withSilent().withLevel(2));
		}
	}

	public String toString() {
		return "AdvancementRewards{experience="
			+ this.experience
			+ ", loot="
			+ Arrays.toString(this.loot)
			+ ", recipes="
			+ Arrays.toString(this.recipes)
			+ ", function="
			+ this.field_1163
			+ '}';
	}

	public JsonElement method_747() {
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

			if (this.field_1163.method_9197() != null) {
				jsonObject.addProperty("function", this.field_1163.method_9197().toString());
			}

			return jsonObject;
		}
	}

	public static class Builder {
		private int field_1169;
		private final List<Identifier> field_1171 = Lists.<Identifier>newArrayList();
		private final List<Identifier> field_1168 = Lists.<Identifier>newArrayList();
		@Nullable
		private Identifier field_1170;

		public static AdvancementRewards.Builder method_750(int i) {
			return new AdvancementRewards.Builder().method_749(i);
		}

		public AdvancementRewards.Builder method_749(int i) {
			this.field_1169 += i;
			return this;
		}

		public static AdvancementRewards.Builder method_753(Identifier identifier) {
			return new AdvancementRewards.Builder().method_752(identifier);
		}

		public AdvancementRewards.Builder method_752(Identifier identifier) {
			this.field_1168.add(identifier);
			return this;
		}

		public AdvancementRewards build() {
			return new AdvancementRewards(
				this.field_1169,
				(Identifier[])this.field_1171.toArray(new Identifier[0]),
				(Identifier[])this.field_1168.toArray(new Identifier[0]),
				this.field_1170 == null ? CommandFunction.class_2159.field_9809 : new CommandFunction.class_2159(this.field_1170)
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

			CommandFunction.class_2159 lv;
			if (jsonObject.has("function")) {
				lv = new CommandFunction.class_2159(new Identifier(JsonHelper.getString(jsonObject, "function")));
			} else {
				lv = CommandFunction.class_2159.field_9809;
			}

			return new AdvancementRewards(i, identifiers, identifiers2, lv);
		}
	}
}
