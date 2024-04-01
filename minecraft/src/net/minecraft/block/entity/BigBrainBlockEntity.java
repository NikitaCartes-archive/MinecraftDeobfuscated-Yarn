package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.XpComponent;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BigBrainBlockEntity extends BlockEntity {
	public static final String AMOUNT_NBT_KEY = "amount";
	private static final double field_50904 = 10.0;
	private static final int DEFAULT_DELAY = 5;
	private static final String DELAY_NBT_KEY = "delay";
	private int delay = 5;
	private int amount;

	public BigBrainBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.BIG_BRAIN, pos, state);
	}

	@Override
	public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		this.amount = nbt.getInt("amount");
		this.delay = nbt.getInt("delay");
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		nbt.putInt("amount", this.amount);
		nbt.putInt("delay", this.delay);
	}

	public int getAmount() {
		return this.amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	@Override
	public void addComponents(ComponentMap.Builder componentMapBuilder) {
		super.addComponents(componentMapBuilder);
		componentMapBuilder.add(DataComponentTypes.XP, new XpComponent(this.amount));
	}

	@Override
	public void removeFromCopiedStackNbt(NbtCompound nbt) {
		nbt.remove("amount");
	}

	public static void tick(World world, BlockPos pos, BlockState state, BigBrainBlockEntity blockEntity) {
		if (--blockEntity.delay <= 0) {
			blockEntity.delay = 5;
			Box box = Box.of(Vec3d.ofCenter(pos), 10.0, 10.0, 10.0);

			for (ExperienceOrbEntity experienceOrbEntity : world.getEntitiesByType(TypeFilter.instanceOf(ExperienceOrbEntity.class), box, experienceOrbEntityx -> true)) {
				experienceOrbEntity.method_58857(pos);
			}
		}
	}
}
