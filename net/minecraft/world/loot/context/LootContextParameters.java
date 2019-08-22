/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot.context;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.loot.context.LootContextParameter;

public class LootContextParameters {
    public static final LootContextParameter<Entity> THIS_ENTITY = LootContextParameters.register("this_entity");
    public static final LootContextParameter<PlayerEntity> LAST_DAMAGE_PLAYER = LootContextParameters.register("last_damage_player");
    public static final LootContextParameter<DamageSource> DAMAGE_SOURCE = LootContextParameters.register("damage_source");
    public static final LootContextParameter<Entity> KILLER_ENTITY = LootContextParameters.register("killer_entity");
    public static final LootContextParameter<Entity> DIRECT_KILLER_ENTITY = LootContextParameters.register("direct_killer_entity");
    public static final LootContextParameter<BlockPos> POSITION = LootContextParameters.register("position");
    public static final LootContextParameter<BlockState> BLOCK_STATE = LootContextParameters.register("block_state");
    public static final LootContextParameter<BlockEntity> BLOCK_ENTITY = LootContextParameters.register("block_entity");
    public static final LootContextParameter<ItemStack> TOOL = LootContextParameters.register("tool");
    public static final LootContextParameter<Float> EXPLOSION_RADIUS = LootContextParameters.register("explosion_radius");

    private static <T> LootContextParameter<T> register(String string) {
        return new LootContextParameter(new Identifier(string));
    }
}

