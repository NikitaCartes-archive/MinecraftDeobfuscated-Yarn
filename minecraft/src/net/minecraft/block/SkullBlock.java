package net.minecraft.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Map;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class SkullBlock extends AbstractSkullBlock {
	public static final MapCodec<SkullBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(SkullBlock.SkullType.CODEC.fieldOf("kind").forGetter(AbstractSkullBlock::getSkullType), createSettingsCodec())
				.apply(instance, SkullBlock::new)
	);
	public static final int MAX_ROTATION_INDEX = RotationPropertyHelper.getMax();
	private static final int MAX_ROTATIONS = MAX_ROTATION_INDEX + 1;
	public static final IntProperty ROTATION = Properties.ROTATION;
	protected static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);
	protected static final VoxelShape PIGLIN_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);

	@Override
	public MapCodec<? extends SkullBlock> getCodec() {
		return CODEC;
	}

	protected SkullBlock(SkullBlock.SkullType skullType, AbstractBlock.Settings settings) {
		super(skullType, settings);
		this.setDefaultState(this.getDefaultState().with(ROTATION, Integer.valueOf(0)));
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return this.getSkullType() == SkullBlock.Type.PIGLIN ? PIGLIN_SHAPE : SHAPE;
	}

	@Override
	protected VoxelShape getCullingShape(BlockState state) {
		return VoxelShapes.empty();
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return super.getPlacementState(ctx).with(ROTATION, Integer.valueOf(RotationPropertyHelper.fromYaw(ctx.getPlayerYaw())));
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(ROTATION, Integer.valueOf(rotation.rotate((Integer)state.get(ROTATION), MAX_ROTATIONS)));
	}

	@Override
	protected BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.with(ROTATION, Integer.valueOf(mirror.mirror((Integer)state.get(ROTATION), MAX_ROTATIONS)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(ROTATION);
	}

	public interface SkullType extends StringIdentifiable {
		Map<String, SkullBlock.SkullType> TYPES = new Object2ObjectArrayMap<>();
		Codec<SkullBlock.SkullType> CODEC = Codec.stringResolver(StringIdentifiable::asString, TYPES::get);
	}

	public static enum Type implements SkullBlock.SkullType {
		SKELETON("skeleton"),
		WITHER_SKELETON("wither_skeleton"),
		PLAYER("player"),
		ZOMBIE("zombie"),
		CREEPER("creeper"),
		PIGLIN("piglin"),
		DRAGON("dragon");

		private final String id;

		private Type(final String id) {
			this.id = id;
			TYPES.put(id, this);
		}

		@Override
		public String asString() {
			return this.id;
		}
	}
}
