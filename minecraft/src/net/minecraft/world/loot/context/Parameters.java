package net.minecraft.world.loot.context;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class Parameters {
	public static final Parameter<Entity> field_1226 = register("this_entity");
	public static final Parameter<PlayerEntity> field_1233 = register("last_damage_player");
	public static final Parameter<DamageSource> field_1231 = register("damage_source");
	public static final Parameter<Entity> field_1230 = register("killer_entity");
	public static final Parameter<Entity> field_1227 = register("direct_killer_entity");
	public static final Parameter<BlockPos> field_1232 = register("position");
	public static final Parameter<BlockState> field_1224 = register("block_state");
	public static final Parameter<BlockEntity> field_1228 = register("block_entity");
	public static final Parameter<ItemStack> field_1229 = register("tool");
	public static final Parameter<Float> field_1225 = register("explosion_radius");

	private static <T> Parameter<T> register(String string) {
		return new Parameter<>(new Identifier(string));
	}
}
