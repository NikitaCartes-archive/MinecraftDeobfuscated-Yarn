package net.minecraft.block;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;

public class InfestedBlock extends Block {
	public static final MapCodec<InfestedBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(Registries.BLOCK.getCodec().fieldOf("host").forGetter(InfestedBlock::getRegularBlock), createSettingsCodec())
				.apply(instance, InfestedBlock::new)
	);
	private final Block regularBlock;
	private static final Map<Block, Block> REGULAR_TO_INFESTED_BLOCK = Maps.<Block, Block>newIdentityHashMap();
	private static final Map<BlockState, BlockState> REGULAR_TO_INFESTED_STATE = Maps.<BlockState, BlockState>newIdentityHashMap();
	private static final Map<BlockState, BlockState> INFESTED_TO_REGULAR_STATE = Maps.<BlockState, BlockState>newIdentityHashMap();

	@Override
	public MapCodec<? extends InfestedBlock> getCodec() {
		return CODEC;
	}

	/**
	 * Creates an infested block
	 * 
	 * @param regularBlock the block this infested block should mimic
	 * @param settings block settings
	 */
	public InfestedBlock(Block regularBlock, AbstractBlock.Settings settings) {
		super(settings.hardness(regularBlock.getHardness() / 2.0F).resistance(0.75F));
		this.regularBlock = regularBlock;
		REGULAR_TO_INFESTED_BLOCK.put(regularBlock, this);
	}

	public Block getRegularBlock() {
		return this.regularBlock;
	}

	public static boolean isInfestable(BlockState block) {
		return REGULAR_TO_INFESTED_BLOCK.containsKey(block.getBlock());
	}

	private void spawnSilverfish(ServerWorld world, BlockPos pos) {
		SilverfishEntity silverfishEntity = EntityType.SILVERFISH.create(world);
		if (silverfishEntity != null) {
			silverfishEntity.refreshPositionAndAngles((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, 0.0F, 0.0F);
			world.spawnEntity(silverfishEntity);
			silverfishEntity.playSpawnEffects();
		}
	}

	@Override
	protected void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
		super.onStacksDropped(state, world, pos, tool, dropExperience);
		if (world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS) && !EnchantmentHelper.hasAnyEnchantmentsIn(tool, EnchantmentTags.PREVENTS_INFESTED_SPAWNS)) {
			this.spawnSilverfish(world, pos);
		}
	}

	public static BlockState fromRegularState(BlockState regularState) {
		return copyProperties(REGULAR_TO_INFESTED_STATE, regularState, () -> ((Block)REGULAR_TO_INFESTED_BLOCK.get(regularState.getBlock())).getDefaultState());
	}

	public BlockState toRegularState(BlockState infestedState) {
		return copyProperties(INFESTED_TO_REGULAR_STATE, infestedState, () -> this.getRegularBlock().getDefaultState());
	}

	private static BlockState copyProperties(Map<BlockState, BlockState> stateMap, BlockState fromState, Supplier<BlockState> toStateSupplier) {
		return (BlockState)stateMap.computeIfAbsent(fromState, infestedState -> {
			BlockState blockState = (BlockState)toStateSupplier.get();

			for (Property property : infestedState.getProperties()) {
				blockState = blockState.contains(property) ? blockState.with(property, infestedState.get(property)) : blockState;
			}

			return blockState;
		});
	}
}
