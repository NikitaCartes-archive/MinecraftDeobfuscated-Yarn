package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import javax.annotation.Nullable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.UniformLootTableRange;
import net.minecraft.world.loot.context.LootContext;

public class class_4571 implements class_4570 {
	@Nullable
	private final Long field_20767;
	private final UniformLootTableRange field_20768;

	private class_4571(@Nullable Long long_, UniformLootTableRange uniformLootTableRange) {
		this.field_20767 = long_;
		this.field_20768 = uniformLootTableRange;
	}

	public boolean method_22587(LootContext lootContext) {
		ServerWorld serverWorld = lootContext.getWorld();
		long l = serverWorld.getTimeOfDay();
		if (this.field_20767 != null) {
			l %= this.field_20767;
		}

		return this.field_20768.contains((int)l);
	}

	public static class class_4572 extends class_4570.Factory<class_4571> {
		public class_4572() {
			super(new Identifier("time_check"), class_4571.class);
		}

		public void method_22591(JsonObject jsonObject, class_4571 arg, JsonSerializationContext jsonSerializationContext) {
			jsonObject.addProperty("period", arg.field_20767);
			jsonObject.add("value", jsonSerializationContext.serialize(arg.field_20768));
		}

		public class_4571 method_22590(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			Long long_ = jsonObject.has("period") ? JsonHelper.method_22449(jsonObject, "period") : null;
			UniformLootTableRange uniformLootTableRange = JsonHelper.deserialize(jsonObject, "value", jsonDeserializationContext, UniformLootTableRange.class);
			return new class_4571(long_, uniformLootTableRange);
		}
	}
}
