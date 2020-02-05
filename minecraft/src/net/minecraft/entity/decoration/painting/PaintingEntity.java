package net.minecraft.entity.decoration.painting;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.PaintingSpawnS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class PaintingEntity extends AbstractDecorationEntity {
	public PaintingMotive motive;

	public PaintingEntity(EntityType<? extends PaintingEntity> entityType, World world) {
		super(entityType, world);
	}

	public PaintingEntity(World world, BlockPos pos, Direction direction) {
		super(EntityType.PAINTING, world, pos);
		List<PaintingMotive> list = Lists.<PaintingMotive>newArrayList();
		int i = 0;

		for (PaintingMotive paintingMotive : Registry.PAINTING_MOTIVE) {
			this.motive = paintingMotive;
			this.setFacing(direction);
			if (this.canStayAttached()) {
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
	public PaintingEntity(World world, BlockPos pos, Direction direction, PaintingMotive motive) {
		this(world, pos, direction);
		this.motive = motive;
		this.setFacing(direction);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		tag.putString("Motive", Registry.PAINTING_MOTIVE.getId(this.motive).toString());
		super.writeCustomDataToTag(tag);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		this.motive = Registry.PAINTING_MOTIVE.get(Identifier.tryParse(tag.getString("Motive")));
		super.readCustomDataFromTag(tag);
	}

	@Override
	public int getWidthPixels() {
		return this.motive == null ? 1 : this.motive.getWidth();
	}

	@Override
	public int getHeightPixels() {
		return this.motive == null ? 1 : this.motive.getHeight();
	}

	@Override
	public void onBreak(@Nullable Entity entity) {
		if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
			this.playSound(SoundEvents.ENTITY_PAINTING_BREAK, 1.0F, 1.0F);
			if (entity instanceof PlayerEntity) {
				PlayerEntity playerEntity = (PlayerEntity)entity;
				if (playerEntity.abilities.creativeMode) {
					return;
				}
			}

			this.dropItem(Items.PAINTING);
		}
	}

	@Override
	public void onPlace() {
		this.playSound(SoundEvents.ENTITY_PAINTING_PLACE, 1.0F, 1.0F);
	}

	@Override
	public void refreshPositionAndAngles(double x, double y, double z, float yaw, float pitch) {
		this.updatePosition(x, y, z);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
		BlockPos blockPos = this.attachmentPos.add(x - this.getX(), y - this.getY(), z - this.getZ());
		this.updatePosition((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new PaintingSpawnS2CPacket(this);
	}
}
