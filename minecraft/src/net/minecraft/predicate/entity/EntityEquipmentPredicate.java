package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.raid.Raid;
import net.minecraft.item.Items;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.JsonHelper;

public class EntityEquipmentPredicate {
	public static final EntityEquipmentPredicate ANY = new EntityEquipmentPredicate(
		ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY
	);
	public static final EntityEquipmentPredicate field_19240 = new EntityEquipmentPredicate(
		ItemPredicate.Builder.create().item(Items.WHITE_BANNER).nbt(Raid.getOminousBanner().getTag()).build(),
		ItemPredicate.ANY,
		ItemPredicate.ANY,
		ItemPredicate.ANY,
		ItemPredicate.ANY,
		ItemPredicate.ANY
	);
	private final ItemPredicate head;
	private final ItemPredicate chest;
	private final ItemPredicate legs;
	private final ItemPredicate feet;
	private final ItemPredicate mainhand;
	private final ItemPredicate offhand;

	public EntityEquipmentPredicate(
		ItemPredicate head, ItemPredicate chest, ItemPredicate legs, ItemPredicate feet, ItemPredicate mainhand, ItemPredicate itemPredicate
	) {
		this.head = head;
		this.chest = chest;
		this.legs = legs;
		this.feet = feet;
		this.mainhand = mainhand;
		this.offhand = itemPredicate;
	}

	public boolean test(@Nullable Entity entity) {
		if (this == ANY) {
			return true;
		} else if (!(entity instanceof LivingEntity)) {
			return false;
		} else {
			LivingEntity livingEntity = (LivingEntity)entity;
			if (!this.head.test(livingEntity.getEquippedStack(EquipmentSlot.HEAD))) {
				return false;
			} else if (!this.chest.test(livingEntity.getEquippedStack(EquipmentSlot.CHEST))) {
				return false;
			} else if (!this.legs.test(livingEntity.getEquippedStack(EquipmentSlot.LEGS))) {
				return false;
			} else if (!this.feet.test(livingEntity.getEquippedStack(EquipmentSlot.FEET))) {
				return false;
			} else {
				return !this.mainhand.test(livingEntity.getEquippedStack(EquipmentSlot.MAINHAND))
					? false
					: this.offhand.test(livingEntity.getEquippedStack(EquipmentSlot.OFFHAND));
			}
		}
	}

	public static EntityEquipmentPredicate deserialize(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "equipment");
			ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("head"));
			ItemPredicate itemPredicate2 = ItemPredicate.fromJson(jsonObject.get("chest"));
			ItemPredicate itemPredicate3 = ItemPredicate.fromJson(jsonObject.get("legs"));
			ItemPredicate itemPredicate4 = ItemPredicate.fromJson(jsonObject.get("feet"));
			ItemPredicate itemPredicate5 = ItemPredicate.fromJson(jsonObject.get("mainhand"));
			ItemPredicate itemPredicate6 = ItemPredicate.fromJson(jsonObject.get("offhand"));
			return new EntityEquipmentPredicate(itemPredicate, itemPredicate2, itemPredicate3, itemPredicate4, itemPredicate5, itemPredicate6);
		} else {
			return ANY;
		}
	}

	public JsonElement toJson() {
		if (this == ANY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("head", this.head.toJson());
			jsonObject.add("chest", this.chest.toJson());
			jsonObject.add("legs", this.legs.toJson());
			jsonObject.add("feet", this.feet.toJson());
			jsonObject.add("mainhand", this.mainhand.toJson());
			jsonObject.add("offhand", this.offhand.toJson());
			return jsonObject;
		}
	}
}
