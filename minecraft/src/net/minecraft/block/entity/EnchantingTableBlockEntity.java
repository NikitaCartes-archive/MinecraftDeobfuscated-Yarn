package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class EnchantingTableBlockEntity extends BlockEntity implements Nameable {
	public int ticks;
	public float nextPageAngle;
	public float pageAngle;
	public float flipRandom;
	public float flipTurn;
	public float nextPageTurningSpeed;
	public float pageTurningSpeed;
	public float bookRotation;
	public float lastBookRotation;
	public float targetBookRotation;
	private static final Random RANDOM = Random.create();
	@Nullable
	private Text customName;

	public EnchantingTableBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.ENCHANTING_TABLE, pos, state);
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
		if (this.hasCustomName()) {
			nbt.putString("CustomName", Text.Serialization.toJsonString(this.customName, registryLookup));
		}
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
		if (nbt.contains("CustomName", NbtElement.STRING_TYPE)) {
			this.customName = tryParseCustomName(nbt.getString("CustomName"), registryLookup);
		}
	}

	public static void tick(World world, BlockPos pos, BlockState state, EnchantingTableBlockEntity blockEntity) {
		blockEntity.pageTurningSpeed = blockEntity.nextPageTurningSpeed;
		blockEntity.lastBookRotation = blockEntity.bookRotation;
		PlayerEntity playerEntity = world.getClosestPlayer((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 3.0, false);
		if (playerEntity != null) {
			double d = playerEntity.getX() - ((double)pos.getX() + 0.5);
			double e = playerEntity.getZ() - ((double)pos.getZ() + 0.5);
			blockEntity.targetBookRotation = (float)MathHelper.atan2(e, d);
			blockEntity.nextPageTurningSpeed += 0.1F;
			if (blockEntity.nextPageTurningSpeed < 0.5F || RANDOM.nextInt(40) == 0) {
				float f = blockEntity.flipRandom;

				do {
					blockEntity.flipRandom = blockEntity.flipRandom + (float)(RANDOM.nextInt(4) - RANDOM.nextInt(4));
				} while (f == blockEntity.flipRandom);
			}
		} else {
			blockEntity.targetBookRotation += 0.02F;
			blockEntity.nextPageTurningSpeed -= 0.1F;
		}

		while (blockEntity.bookRotation >= (float) Math.PI) {
			blockEntity.bookRotation -= (float) (Math.PI * 2);
		}

		while (blockEntity.bookRotation < (float) -Math.PI) {
			blockEntity.bookRotation += (float) (Math.PI * 2);
		}

		while (blockEntity.targetBookRotation >= (float) Math.PI) {
			blockEntity.targetBookRotation -= (float) (Math.PI * 2);
		}

		while (blockEntity.targetBookRotation < (float) -Math.PI) {
			blockEntity.targetBookRotation += (float) (Math.PI * 2);
		}

		float g = blockEntity.targetBookRotation - blockEntity.bookRotation;

		while (g >= (float) Math.PI) {
			g -= (float) (Math.PI * 2);
		}

		while (g < (float) -Math.PI) {
			g += (float) (Math.PI * 2);
		}

		blockEntity.bookRotation += g * 0.4F;
		blockEntity.nextPageTurningSpeed = MathHelper.clamp(blockEntity.nextPageTurningSpeed, 0.0F, 1.0F);
		blockEntity.ticks++;
		blockEntity.pageAngle = blockEntity.nextPageAngle;
		float h = (blockEntity.flipRandom - blockEntity.nextPageAngle) * 0.4F;
		float i = 0.2F;
		h = MathHelper.clamp(h, -0.2F, 0.2F);
		blockEntity.flipTurn = blockEntity.flipTurn + (h - blockEntity.flipTurn) * 0.9F;
		blockEntity.nextPageAngle = blockEntity.nextPageAngle + blockEntity.flipTurn;
	}

	@Override
	public Text getName() {
		return (Text)(this.customName != null ? this.customName : Text.translatable("container.enchant"));
	}

	public void setCustomName(@Nullable Text customName) {
		this.customName = customName;
	}

	@Nullable
	@Override
	public Text getCustomName() {
		return this.customName;
	}

	@Override
	protected void readComponents(BlockEntity.ComponentsAccess components) {
		super.readComponents(components);
		this.customName = components.get(DataComponentTypes.CUSTOM_NAME);
	}

	@Override
	protected void addComponents(ComponentMap.Builder componentMapBuilder) {
		super.addComponents(componentMapBuilder);
		componentMapBuilder.add(DataComponentTypes.CUSTOM_NAME, this.customName);
	}

	@Override
	public void removeFromCopiedStackNbt(NbtCompound nbt) {
		nbt.remove("CustomName");
	}
}
