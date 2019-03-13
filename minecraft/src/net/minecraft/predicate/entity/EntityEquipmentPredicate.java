package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.JsonHelper;

public class EntityEquipmentPredicate {
	public static final EntityEquipmentPredicate ANY = new EntityEquipmentPredicate(
		ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY
	);
	private final ItemPredicate field_16483;
	private final ItemPredicate field_16487;
	private final ItemPredicate field_16488;
	private final ItemPredicate field_16489;
	private final ItemPredicate field_16486;
	private final ItemPredicate field_16484;

	public EntityEquipmentPredicate(
		ItemPredicate itemPredicate,
		ItemPredicate itemPredicate2,
		ItemPredicate itemPredicate3,
		ItemPredicate itemPredicate4,
		ItemPredicate itemPredicate5,
		ItemPredicate itemPredicate6
	) {
		this.field_16483 = itemPredicate;
		this.field_16487 = itemPredicate2;
		this.field_16488 = itemPredicate3;
		this.field_16489 = itemPredicate4;
		this.field_16486 = itemPredicate5;
		this.field_16484 = itemPredicate6;
	}

	public boolean test(@Nullable Entity entity) {
		if (this == ANY) {
			return true;
		} else if (!(entity instanceof LivingEntity)) {
			return false;
		} else {
			LivingEntity livingEntity = (LivingEntity)entity;
			if (!this.field_16483.test(livingEntity.method_6118(EquipmentSlot.HEAD))) {
				return false;
			} else if (!this.field_16487.test(livingEntity.method_6118(EquipmentSlot.CHEST))) {
				return false;
			} else if (!this.field_16488.test(livingEntity.method_6118(EquipmentSlot.LEGS))) {
				return false;
			} else if (!this.field_16489.test(livingEntity.method_6118(EquipmentSlot.FEET))) {
				return false;
			} else {
				return !this.field_16486.test(livingEntity.method_6118(EquipmentSlot.HAND_MAIN))
					? false
					: this.field_16484.test(livingEntity.method_6118(EquipmentSlot.HAND_OFF));
			}
		}
	}

	public static EntityEquipmentPredicate deserialize(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "equipment");
			ItemPredicate itemPredicate = ItemPredicate.deserialize(jsonObject.get("head"));
			ItemPredicate itemPredicate2 = ItemPredicate.deserialize(jsonObject.get("chest"));
			ItemPredicate itemPredicate3 = ItemPredicate.deserialize(jsonObject.get("legs"));
			ItemPredicate itemPredicate4 = ItemPredicate.deserialize(jsonObject.get("feet"));
			ItemPredicate itemPredicate5 = ItemPredicate.deserialize(jsonObject.get("mainhand"));
			ItemPredicate itemPredicate6 = ItemPredicate.deserialize(jsonObject.get("offhand"));
			return new EntityEquipmentPredicate(itemPredicate, itemPredicate2, itemPredicate3, itemPredicate4, itemPredicate5, itemPredicate6);
		} else {
			return ANY;
		}
	}

	public JsonElement serialize() {
		if (this == ANY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("head", this.field_16483.serialize());
			jsonObject.add("chest", this.field_16487.serialize());
			jsonObject.add("legs", this.field_16488.serialize());
			jsonObject.add("feet", this.field_16489.serialize());
			jsonObject.add("mainhand", this.field_16486.serialize());
			jsonObject.add("offhand", this.field_16484.serialize());
			return jsonObject;
		}
	}
}
