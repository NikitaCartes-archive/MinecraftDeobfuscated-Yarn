/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class class_6404 {
    public static final class_6404 field_33920 = new class_6404(NumberRange.IntRange.ANY, EntityPredicate.ANY);
    private static final String field_33921 = "blocks_set_on_fire";
    private static final String field_33922 = "entity_struck";
    private final NumberRange.IntRange field_33923;
    private final EntityPredicate field_33924;

    private class_6404(NumberRange.IntRange intRange, EntityPredicate entityPredicate) {
        this.field_33923 = intRange;
        this.field_33924 = entityPredicate;
    }

    public static class_6404 method_37237(NumberRange.IntRange intRange) {
        return new class_6404(intRange, EntityPredicate.ANY);
    }

    public static class_6404 method_37238(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return field_33920;
        }
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "lightning");
        return new class_6404(NumberRange.IntRange.fromJson(jsonObject.get(field_33921)), EntityPredicate.fromJson(jsonObject.get(field_33922)));
    }

    public JsonElement method_37234() {
        if (this == field_33920) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(field_33921, this.field_33923.toJson());
        jsonObject.add(field_33922, this.field_33924.toJson());
        return jsonObject;
    }

    public boolean method_37236(Entity entity2, ServerWorld serverWorld, @Nullable Vec3d vec3d) {
        if (this == field_33920) {
            return true;
        }
        if (!(entity2 instanceof LightningEntity)) {
            return false;
        }
        LightningEntity lightningEntity = (LightningEntity)entity2;
        return this.field_33923.test(lightningEntity.method_37220()) && (this.field_33924 == EntityPredicate.ANY || lightningEntity.method_37221().anyMatch(entity -> this.field_33924.test(serverWorld, vec3d, (Entity)entity)));
    }
}

