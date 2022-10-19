package net.minecraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class WeightedPressurePlateBlock extends AbstractPressurePlateBlock {
	public static final IntProperty POWER = Properties.POWER;
	private final int weight;
	private final SoundEvent depressSound;
	private final SoundEvent pressSound;

	protected WeightedPressurePlateBlock(int weight, AbstractBlock.Settings settings, SoundEvent depressSound, SoundEvent pressSound) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(POWER, Integer.valueOf(0)));
		this.weight = weight;
		this.depressSound = depressSound;
		this.pressSound = pressSound;
	}

	@Override
	protected int getRedstoneOutput(World world, BlockPos pos) {
		int i = Math.min(world.getNonSpectatingEntities(Entity.class, BOX.offset(pos)).size(), this.weight);
		if (i > 0) {
			float f = (float)Math.min(this.weight, i) / (float)this.weight;
			return MathHelper.ceil(f * 15.0F);
		} else {
			return 0;
		}
	}

	@Override
	protected void playPressSound(WorldAccess world, BlockPos pos) {
		world.playSound(null, pos, this.pressSound, SoundCategory.BLOCKS);
	}

	@Override
	protected void playDepressSound(WorldAccess world, BlockPos pos) {
		world.playSound(null, pos, this.depressSound, SoundCategory.BLOCKS);
	}

	@Override
	protected int getRedstoneOutput(BlockState state) {
		return (Integer)state.get(POWER);
	}

	@Override
	protected BlockState setRedstoneOutput(BlockState state, int rsOut) {
		return state.with(POWER, Integer.valueOf(rsOut));
	}

	@Override
	protected int getTickRate() {
		return 10;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(POWER);
	}
}
