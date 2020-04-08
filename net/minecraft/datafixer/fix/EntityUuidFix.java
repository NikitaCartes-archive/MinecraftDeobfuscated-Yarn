/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.Sets;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import java.util.Set;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.AbstractUuidFix;

public class EntityUuidFix
extends AbstractUuidFix {
    private static final Set<String> RIDEABLE_TAMEABLES = Sets.newHashSet();
    private static final Set<String> TAMEABLE_PETS = Sets.newHashSet();
    private static final Set<String> BREEDABLES = Sets.newHashSet();
    private static final Set<String> LEASHABLES = Sets.newHashSet();
    private static final Set<String> OTHER_LIVINGS = Sets.newHashSet();
    private static final Set<String> PROJECTILES = Sets.newHashSet();

    public EntityUuidFix(Schema outputSchema) {
        super(outputSchema, TypeReferences.ENTITY);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("EntityUUIDFixes", this.getInputSchema().getType(this.typeReference), typed -> {
            typed = typed.update(DSL.remainderFinder(), EntityUuidFix::updateSelfUuid);
            for (String string : RIDEABLE_TAMEABLES) {
                typed = this.updateTyped((Typed<?>)typed, string, EntityUuidFix::updateTameable);
            }
            for (String string : TAMEABLE_PETS) {
                typed = this.updateTyped((Typed<?>)typed, string, EntityUuidFix::updateTameable);
            }
            for (String string : BREEDABLES) {
                typed = this.updateTyped((Typed<?>)typed, string, EntityUuidFix::updateBreedable);
            }
            for (String string : LEASHABLES) {
                typed = this.updateTyped((Typed<?>)typed, string, EntityUuidFix::updateLeashable);
            }
            for (String string : OTHER_LIVINGS) {
                typed = this.updateTyped((Typed<?>)typed, string, EntityUuidFix::updateLiving);
            }
            for (String string : PROJECTILES) {
                typed = this.updateTyped((Typed<?>)typed, string, EntityUuidFix::updateProjectile);
            }
            typed = this.updateTyped((Typed<?>)typed, "minecraft:bee", EntityUuidFix::updateZombifiedPiglin);
            typed = this.updateTyped((Typed<?>)typed, "minecraft:zombified_piglin", EntityUuidFix::updateZombifiedPiglin);
            typed = this.updateTyped((Typed<?>)typed, "minecraft:fox", EntityUuidFix::updateFox);
            typed = this.updateTyped((Typed<?>)typed, "minecraft:item", EntityUuidFix::updateItemEntity);
            typed = this.updateTyped((Typed<?>)typed, "minecraft:shulker_bullet", EntityUuidFix::updateShulkerBullet);
            typed = this.updateTyped((Typed<?>)typed, "minecraft:area_effect_cloud", EntityUuidFix::updateAreaEffectCloud);
            typed = this.updateTyped((Typed<?>)typed, "minecraft:zombie_villager", EntityUuidFix::updateZombieVillager);
            typed = this.updateTyped((Typed<?>)typed, "minecraft:evoker_fangs", EntityUuidFix::updateEvokerFangs);
            typed = this.updateTyped((Typed<?>)typed, "minecraft:piglin", EntityUuidFix::updateAngryAtMemory);
            return typed;
        });
    }

    private static Dynamic<?> updateAngryAtMemory(Dynamic<?> dynamic2) {
        return dynamic2.update("Brain", dynamic -> dynamic.update("memories", dynamic2 -> dynamic2.update("minecraft:angry_at", dynamic -> EntityUuidFix.updateStringUuid(dynamic, "value", "value").orElseGet(() -> {
            LOGGER.warn("angry_at has no value.");
            return dynamic;
        }))));
    }

    private static Dynamic<?> updateEvokerFangs(Dynamic<?> dynamic) {
        return EntityUuidFix.updateRegularMostLeast(dynamic, "OwnerUUID", "Owner").orElse(dynamic);
    }

    private static Dynamic<?> updateZombieVillager(Dynamic<?> dynamic) {
        return EntityUuidFix.updateRegularMostLeast(dynamic, "ConversionPlayer", "ConversionPlayer").orElse(dynamic);
    }

    private static Dynamic<?> updateAreaEffectCloud(Dynamic<?> dynamic) {
        return EntityUuidFix.updateRegularMostLeast(dynamic, "OwnerUUID", "Owner").orElse(dynamic);
    }

    private static Dynamic<?> updateShulkerBullet(Dynamic<?> dynamic) {
        dynamic = EntityUuidFix.updateCompoundUuid(dynamic, "Owner", "Owner").orElse(dynamic);
        return EntityUuidFix.updateCompoundUuid(dynamic, "Target", "Target").orElse(dynamic);
    }

    private static Dynamic<?> updateItemEntity(Dynamic<?> dynamic) {
        dynamic = EntityUuidFix.updateCompoundUuid(dynamic, "Owner", "Owner").orElse(dynamic);
        return EntityUuidFix.updateCompoundUuid(dynamic, "Thrower", "Thrower").orElse(dynamic);
    }

    private static Dynamic<?> updateFox(Dynamic<?> dynamic) {
        Optional<Dynamic> optional = dynamic.get("TrustedUUIDs").map(dynamic22 -> dynamic.createList(dynamic22.asStream().map(dynamic -> EntityUuidFix.createArrayFromCompoundUuid(dynamic).orElseGet(() -> {
            LOGGER.warn("Trusted contained invalid data.");
            return dynamic;
        }))));
        return DataFixUtils.orElse(optional.map(dynamic2 -> dynamic.remove("TrustedUUIDs").set("Trusted", (Dynamic<?>)dynamic2)), dynamic);
    }

    private static Dynamic<?> updateZombifiedPiglin(Dynamic<?> dynamic) {
        return EntityUuidFix.updateStringUuid(dynamic, "HurtBy", "HurtBy").orElse(dynamic);
    }

    private static Dynamic<?> updateTameable(Dynamic<?> dynamic) {
        Dynamic<?> dynamic2 = EntityUuidFix.updateBreedable(dynamic);
        return EntityUuidFix.updateStringUuid(dynamic2, "OwnerUUID", "Owner").orElse(dynamic2);
    }

    private static Dynamic<?> updateBreedable(Dynamic<?> dynamic) {
        Dynamic<?> dynamic2 = EntityUuidFix.updateLeashable(dynamic);
        return EntityUuidFix.updateRegularMostLeast(dynamic2, "LoveCause", "LoveCause").orElse(dynamic2);
    }

    private static Dynamic<?> updateLeashable(Dynamic<?> dynamic2) {
        return EntityUuidFix.updateLiving(dynamic2).update("Leash", dynamic -> EntityUuidFix.updateRegularMostLeast(dynamic, "UUID", "UUID").orElse((Dynamic<?>)dynamic));
    }

    public static Dynamic<?> updateLiving(Dynamic<?> dynamic) {
        return dynamic.update("Attributes", dynamic22 -> dynamic.createList(dynamic22.asStream().map(dynamic -> dynamic.update("Modifiers", dynamic22 -> dynamic.createList(dynamic22.asStream().map(dynamic -> EntityUuidFix.updateRegularMostLeast(dynamic, "UUID", "UUID").orElse((Dynamic<?>)dynamic)))))));
    }

    private static Dynamic<?> updateProjectile(Dynamic<?> dynamic) {
        return DataFixUtils.orElse(dynamic.get("OwnerUUID").map(dynamic2 -> dynamic.remove("OwnerUUID").set("Owner", (Dynamic<?>)dynamic2)), dynamic);
    }

    public static Dynamic<?> updateSelfUuid(Dynamic<?> dynamic) {
        return EntityUuidFix.updateRegularMostLeast(dynamic, "UUID", "UUID").orElse(dynamic);
    }

    static {
        RIDEABLE_TAMEABLES.add("minecraft:donkey");
        RIDEABLE_TAMEABLES.add("minecraft:horse");
        RIDEABLE_TAMEABLES.add("minecraft:llama");
        RIDEABLE_TAMEABLES.add("minecraft:mule");
        RIDEABLE_TAMEABLES.add("minecraft:skeleton_horse");
        RIDEABLE_TAMEABLES.add("minecraft:trader_llama");
        RIDEABLE_TAMEABLES.add("minecraft:zombie_horse");
        TAMEABLE_PETS.add("minecraft:cat");
        TAMEABLE_PETS.add("minecraft:parrot");
        TAMEABLE_PETS.add("minecraft:wolf");
        BREEDABLES.add("minecraft:bee");
        BREEDABLES.add("minecraft:chicken");
        BREEDABLES.add("minecraft:cow");
        BREEDABLES.add("minecraft:fox");
        BREEDABLES.add("minecraft:mooshroom");
        BREEDABLES.add("minecraft:ocelot");
        BREEDABLES.add("minecraft:panda");
        BREEDABLES.add("minecraft:pig");
        BREEDABLES.add("minecraft:polar_bear");
        BREEDABLES.add("minecraft:rabbit");
        BREEDABLES.add("minecraft:sheep");
        BREEDABLES.add("minecraft:turtle");
        BREEDABLES.add("minecraft:hoglin");
        LEASHABLES.add("minecraft:bat");
        LEASHABLES.add("minecraft:blaze");
        LEASHABLES.add("minecraft:cave_spider");
        LEASHABLES.add("minecraft:cod");
        LEASHABLES.add("minecraft:creeper");
        LEASHABLES.add("minecraft:dolphin");
        LEASHABLES.add("minecraft:drowned");
        LEASHABLES.add("minecraft:elder_guardian");
        LEASHABLES.add("minecraft:ender_dragon");
        LEASHABLES.add("minecraft:enderman");
        LEASHABLES.add("minecraft:endermite");
        LEASHABLES.add("minecraft:evoker");
        LEASHABLES.add("minecraft:ghast");
        LEASHABLES.add("minecraft:giant");
        LEASHABLES.add("minecraft:guardian");
        LEASHABLES.add("minecraft:husk");
        LEASHABLES.add("minecraft:illusioner");
        LEASHABLES.add("minecraft:magma_cube");
        LEASHABLES.add("minecraft:pufferfish");
        LEASHABLES.add("minecraft:zombified_piglin");
        LEASHABLES.add("minecraft:salmon");
        LEASHABLES.add("minecraft:shulker");
        LEASHABLES.add("minecraft:silverfish");
        LEASHABLES.add("minecraft:skeleton");
        LEASHABLES.add("minecraft:slime");
        LEASHABLES.add("minecraft:snow_golem");
        LEASHABLES.add("minecraft:spider");
        LEASHABLES.add("minecraft:squid");
        LEASHABLES.add("minecraft:stray");
        LEASHABLES.add("minecraft:tropical_fish");
        LEASHABLES.add("minecraft:vex");
        LEASHABLES.add("minecraft:villager");
        LEASHABLES.add("minecraft:iron_golem");
        LEASHABLES.add("minecraft:vindicator");
        LEASHABLES.add("minecraft:pillager");
        LEASHABLES.add("minecraft:wandering_trader");
        LEASHABLES.add("minecraft:witch");
        LEASHABLES.add("minecraft:wither");
        LEASHABLES.add("minecraft:wither_skeleton");
        LEASHABLES.add("minecraft:zombie");
        LEASHABLES.add("minecraft:zombie_villager");
        LEASHABLES.add("minecraft:phantom");
        LEASHABLES.add("minecraft:ravager");
        LEASHABLES.add("minecraft:piglin");
        OTHER_LIVINGS.add("minecraft:armor_stand");
        PROJECTILES.add("minecraft:arrow");
        PROJECTILES.add("minecraft:dragon_fireball");
        PROJECTILES.add("minecraft:firework_rocket");
        PROJECTILES.add("minecraft:fireball");
        PROJECTILES.add("minecraft:llama_spit");
        PROJECTILES.add("minecraft:small_fireball");
        PROJECTILES.add("minecraft:snowball");
        PROJECTILES.add("minecraft:spectral_arrow");
        PROJECTILES.add("minecraft:egg");
        PROJECTILES.add("minecraft:ender_pearl");
        PROJECTILES.add("minecraft:experience_bottle");
        PROJECTILES.add("minecraft:potion");
        PROJECTILES.add("minecraft:trident");
        PROJECTILES.add("minecraft:wither_skull");
    }
}

