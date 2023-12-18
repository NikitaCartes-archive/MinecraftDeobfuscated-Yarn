package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class FlowerBlock extends PlantBlock implements SuspiciousStewIngredient {
	protected static final MapCodec<List<SuspiciousStewIngredient.StewEffect>> STEW_EFFECT_CODEC = SuspiciousStewIngredient.StewEffect.LIST_CODEC
		.fieldOf("suspicious_stew_effects");
	public static final MapCodec<FlowerBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(STEW_EFFECT_CODEC.forGetter(FlowerBlock::getStewEffects), createSettingsCodec()).apply(instance, FlowerBlock::new)
	);
	protected static final float field_31094 = 3.0F;
	protected static final VoxelShape SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 10.0, 11.0);
	private final List<SuspiciousStewIngredient.StewEffect> stewEffects;

	@Override
	public MapCodec<? extends FlowerBlock> getCodec() {
		return CODEC;
	}

	public FlowerBlock(RegistryEntry<StatusEffect> stewEffect, int duration, AbstractBlock.Settings settings) {
		this(createStewEffectList(stewEffect, duration), settings);
	}

	public FlowerBlock(List<SuspiciousStewIngredient.StewEffect> stewEffects, AbstractBlock.Settings settings) {
		super(settings);
		this.stewEffects = stewEffects;
	}

	protected static List<SuspiciousStewIngredient.StewEffect> createStewEffectList(RegistryEntry<StatusEffect> effect, int duration) {
		return List.of(new SuspiciousStewIngredient.StewEffect(effect, duration * 20));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Vec3d vec3d = state.getModelOffset(world, pos);
		return SHAPE.offset(vec3d.x, vec3d.y, vec3d.z);
	}

	@Override
	public List<SuspiciousStewIngredient.StewEffect> getStewEffects() {
		return this.stewEffects;
	}
}
