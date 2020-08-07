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
	public static final LootContextParameter<Entity> field_1226 = register("this_entity");
	public static final LootContextParameter<PlayerEntity> field_1233 = register("last_damage_player");
	public static final LootContextParameter<DamageSource> field_1231 = register("damage_source");
	public static final LootContextParameter<Entity> field_1230 = register("killer_entity");
	public static final LootContextParameter<Entity> field_1227 = register("direct_killer_entity");
	public static final LootContextParameter<Vec3d> field_24424 = register("origin");
	public static final LootContextParameter<BlockState> field_1224 = register("block_state");
	public static final LootContextParameter<BlockEntity> field_1228 = register("block_entity");
	public static final LootContextParameter<ItemStack> field_1229 = register("tool");
	public static final LootContextParameter<Float> field_1225 = register("explosion_radius");

	private static <T> LootContextParameter<T> register(String name) {
		return new LootContextParameter<>(new Identifier(name));
	}
}
