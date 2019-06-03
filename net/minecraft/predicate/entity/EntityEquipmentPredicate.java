/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.raid.Raid;
import net.minecraft.item.Items;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

public class EntityEquipmentPredicate {
    public static final EntityEquipmentPredicate ANY = new EntityEquipmentPredicate(ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY);
    public static final EntityEquipmentPredicate field_19240 = new EntityEquipmentPredicate(ItemPredicate.Builder.create().item(Items.WHITE_BANNER).nbt(Raid.getOminousBanner().getTag()).build(), ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY);
    private final ItemPredicate head;
    private final ItemPredicate chest;
    private final ItemPredicate legs;
    private final ItemPredicate feet;
    private final ItemPredicate mainhand;
    private final ItemPredicate offhand;

    public EntityEquipmentPredicate(ItemPredicate itemPredicate, ItemPredicate itemPredicate2, ItemPredicate itemPredicate3, ItemPredicate itemPredicate4, ItemPredicate itemPredicate5, ItemPredicate itemPredicate6) {
        this.head = itemPredicate;
        this.chest = itemPredicate2;
        this.legs = itemPredicate3;
        this.feet = itemPredicate4;
        this.mainhand = itemPredicate5;
        this.offhand = itemPredicate6;
    }

    public boolean test(@Nullable Entity entity) {
        if (this == ANY) {
            return true;
        }
        if (!(entity instanceof LivingEntity)) {
            return false;
        }
        LivingEntity livingEntity = (LivingEntity)entity;
        if (!this.head.test(livingEntity.getEquippedStack(EquipmentSlot.HEAD))) {
            return false;
        }
        if (!this.chest.test(livingEntity.getEquippedStack(EquipmentSlot.CHEST))) {
            return false;
        }
        if (!this.legs.test(livingEntity.getEquippedStack(EquipmentSlot.LEGS))) {
            return false;
        }
        if (!this.feet.test(livingEntity.getEquippedStack(EquipmentSlot.FEET))) {
            return false;
        }
        if (!this.mainhand.test(livingEntity.getEquippedStack(EquipmentSlot.MAINHAND))) {
            return false;
        }
        return this.offhand.test(livingEntity.getEquippedStack(EquipmentSlot.OFFHAND));
    }

    public static EntityEquipmentPredicate deserialize(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "equipment");
        ItemPredicate itemPredicate = ItemPredicate.deserialize(jsonObject.get("head"));
        ItemPredicate itemPredicate2 = ItemPredicate.deserialize(jsonObject.get("chest"));
        ItemPredicate itemPredicate3 = ItemPredicate.deserialize(jsonObject.get("legs"));
        ItemPredicate itemPredicate4 = ItemPredicate.deserialize(jsonObject.get("feet"));
        ItemPredicate itemPredicate5 = ItemPredicate.deserialize(jsonObject.get("mainhand"));
        ItemPredicate itemPredicate6 = ItemPredicate.deserialize(jsonObject.get("offhand"));
        return new EntityEquipmentPredicate(itemPredicate, itemPredicate2, itemPredicate3, itemPredicate4, itemPredicate5, itemPredicate6);
    }

    public JsonElement serialize() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("head", this.head.serialize());
        jsonObject.add("chest", this.chest.serialize());
        jsonObject.add("legs", this.legs.serialize());
        jsonObject.add("feet", this.feet.serialize());
        jsonObject.add("mainhand", this.mainhand.serialize());
        jsonObject.add("offhand", this.offhand.serialize());
        return jsonObject;
    }
}

