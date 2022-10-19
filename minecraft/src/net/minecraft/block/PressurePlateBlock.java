package net.minecraft.block;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class PressurePlateBlock extends AbstractPressurePlateBlock {
	public static final BooleanProperty POWERED = Properties.POWERED;
	private final PressurePlateBlock.ActivationRule type;
	private final SoundEvent depressSound;
	private final SoundEvent pressSound;

	protected PressurePlateBlock(PressurePlateBlock.ActivationRule type, AbstractBlock.Settings settings, SoundEvent depressSound, SoundEvent pressSound) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, Boolean.valueOf(false)));
		this.type = type;
		this.depressSound = depressSound;
		this.pressSound = pressSound;
	}

	@Override
	protected int getRedstoneOutput(BlockState state) {
		return state.get(POWERED) ? 15 : 0;
	}

	@Override
	protected BlockState setRedstoneOutput(BlockState state, int rsOut) {
		return state.with(POWERED, Boolean.valueOf(rsOut > 0));
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
	protected int getRedstoneOutput(World world, BlockPos pos) {
		Box box = BOX.offset(pos);
		List<? extends Entity> list;
		switch (this.type) {
			case EVERYTHING:
				list = world.getOtherEntities(null, box);
				break;
			case MOBS:
				list = world.getNonSpectatingEntities(LivingEntity.class, box);
				break;
			default:
				return 0;
		}

		if (!list.isEmpty()) {
			for (Entity entity : list) {
				if (!entity.canAvoidTraps()) {
					return 15;
				}
			}
		}

		return 0;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(POWERED);
	}

	public static enum ActivationRule {
		EVERYTHING,
		MOBS;
	}
}
