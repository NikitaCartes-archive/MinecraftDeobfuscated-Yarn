package net.minecraft.loot.context;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class LootContextParameters {
	public static final LootContextParameter<Entity> THIS_ENTITY = register("this_entity");
	public static final LootContextParameter<PlayerEntity> LAST_DAMAGE_PLAYER = register("last_damage_player");
	public static final LootContextParameter<DamageSource> DAMAGE_SOURCE = register("damage_source");
	public static final LootContextParameter<Entity> KILLER_ENTITY = register("killer_entity");
	public static final LootContextParameter<Entity> DIRECT_KILLER_ENTITY = register("direct_killer_entity");
	public static final LootContextParameter<Vec3d> ORIGIN = register("origin");
	public static final LootContextParameter<BlockState> BLOCK_STATE = register("block_state");
	public static final LootContextParameter<BlockEntity> BLOCK_ENTITY = register("block_entity");
	public static final LootContextParameter<ItemStack> TOOL = register("tool");
	public static final LootContextParameter<Float> EXPLOSION_RADIUS = register("explosion_radius");

	private static <T> LootContextParameter<T> register(String name) {
		return new LootContextParameter<>(new Identifier(name));
	}
}
