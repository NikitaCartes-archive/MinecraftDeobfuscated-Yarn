package net.minecraft.entity.decoration.painting;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.PaintingSpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class PaintingEntity extends AbstractDecorationEntity {
	public PaintingMotive motive;

	public PaintingEntity(EntityType<? extends PaintingEntity> entityType, World world) {
		super(entityType, world);
	}

	public PaintingEntity(World world, BlockPos blockPos, Direction direction) {
		super(EntityType.PAINTING, world, blockPos);
		List<PaintingMotive> list = Lists.<PaintingMotive>newArrayList();
		int i = 0;

		for (PaintingMotive paintingMotive : Registry.MOTIVE) {
			this.motive = paintingMotive;
			this.setFacing(direction);
			if (this.method_6888()) {
				list.add(paintingMotive);
				int j = paintingMotive.getWidth() * paintingMotive.getHeight();
				if (j > i) {
					i = j;
				}
			}
		}

		if (!list.isEmpty()) {
			Iterator<PaintingMotive> iterator = list.iterator();

			while (iterator.hasNext()) {
				PaintingMotive paintingMotivex = (PaintingMotive)iterator.next();
				if (paintingMotivex.getWidth() * paintingMotivex.getHeight() < i) {
					iterator.remove();
				}
			}

			this.motive = (PaintingMotive)list.get(this.random.nextInt(list.size()));
		}

		this.setFacing(direction);
	}

	@Environment(EnvType.CLIENT)
	public PaintingEntity(World world, BlockPos blockPos, Direction direction, PaintingMotive paintingMotive) {
		this(world, blockPos, direction);
		this.motive = paintingMotive;
		this.setFacing(direction);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		compoundTag.putString("Motive", Registry.MOTIVE.getId(this.motive).toString());
		super.writeCustomDataToTag(compoundTag);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		this.motive = Registry.MOTIVE.get(Identifier.create(compoundTag.getString("Motive")));
		super.readCustomDataFromTag(compoundTag);
	}

	@Override
	public int getWidthPixels() {
		return this.motive.getWidth();
	}

	@Override
	public int getHeightPixels() {
		return this.motive.getHeight();
	}

	@Override
	public void onBreak(@Nullable Entity entity) {
		if (this.world.getGameRules().getBoolean("doEntityDrops")) {
			this.playSound(SoundEvents.field_14809, 1.0F, 1.0F);
			if (entity instanceof PlayerEntity) {
				PlayerEntity playerEntity = (PlayerEntity)entity;
				if (playerEntity.abilities.creativeMode) {
					return;
				}
			}

			this.dropItem(Items.field_8892);
		}
	}

	@Override
	public void onPlace() {
		this.playSound(SoundEvents.field_14875, 1.0F, 1.0F);
	}

	@Override
	public void setPositionAndAngles(double d, double e, double f, float g, float h) {
		this.setPosition(d, e, f);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setPositionAndRotations(double d, double e, double f, float g, float h, int i, boolean bl) {
		BlockPos blockPos = this.blockPos.add(d - this.x, e - this.y, f - this.z);
		this.setPosition((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new PaintingSpawnS2CPacket(this);
	}
}
