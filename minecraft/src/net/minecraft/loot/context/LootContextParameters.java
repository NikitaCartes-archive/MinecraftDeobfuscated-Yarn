package net.minecraft.loot.context;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.Vec3d;

public class LootContextParameters {
	public static final ContextParameter<Entity> THIS_ENTITY = ContextParameter.of("this_entity");
	public static final ContextParameter<PlayerEntity> LAST_DAMAGE_PLAYER = ContextParameter.of("last_damage_player");
	public static final ContextParameter<DamageSource> DAMAGE_SOURCE = ContextParameter.of("damage_source");
	public static final ContextParameter<Entity> ATTACKING_ENTITY = ContextParameter.of("attacking_entity");
	public static final ContextParameter<Entity> DIRECT_ATTACKING_ENTITY = ContextParameter.of("direct_attacking_entity");
	public static final ContextParameter<Vec3d> ORIGIN = ContextParameter.of("origin");
	public static final ContextParameter<BlockState> BLOCK_STATE = ContextParameter.of("block_state");
	public static final ContextParameter<BlockEntity> BLOCK_ENTITY = ContextParameter.of("block_entity");
	public static final ContextParameter<ItemStack> TOOL = ContextParameter.of("tool");
	public static final ContextParameter<Float> EXPLOSION_RADIUS = ContextParameter.of("explosion_radius");
	public static final ContextParameter<Integer> ENCHANTMENT_LEVEL = ContextParameter.of("enchantment_level");
	public static final ContextParameter<Boolean> ENCHANTMENT_ACTIVE = ContextParameter.of("enchantment_active");
}
