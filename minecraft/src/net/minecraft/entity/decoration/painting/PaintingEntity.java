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
			this.method_6892(direction);
			if (this.method_6888()) {
				list.add(paintingMotive);
				int j = paintingMotive.getWidth() * paintingMotive.getTextureY();
				if (j > i) {
					i = j;
				}
			}
		}

		if (!list.isEmpty()) {
			Iterator<PaintingMotive> iterator = list.iterator();

			while (iterator.hasNext()) {
				PaintingMotive paintingMotivex = (PaintingMotive)iterator.next();
				if (paintingMotivex.getWidth() * paintingMotivex.getTextureY() < i) {
					iterator.remove();
				}
			}

			this.motive = (PaintingMotive)list.get(this.random.nextInt(list.size()));
		}

		this.method_6892(direction);
	}

	@Environment(EnvType.CLIENT)
	public PaintingEntity(World world, BlockPos blockPos, Direction direction, PaintingMotive paintingMotive) {
		this(world, blockPos, direction);
		this.motive = paintingMotive;
		this.method_6892(direction);
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		compoundTag.putString("Motive", Registry.MOTIVE.method_10221(this.motive).toString());
		super.method_5652(compoundTag);
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		this.motive = Registry.MOTIVE.method_10223(Identifier.create(compoundTag.getString("Motive")));
		super.method_5749(compoundTag);
	}

	@Override
	public int getWidthPixels() {
		return this.motive.getWidth();
	}

	@Override
	public int getHeightPixels() {
		return this.motive.getTextureY();
	}

	@Override
	public void copyEntityData(@Nullable Entity entity) {
		if (this.field_6002.getGameRules().getBoolean("doEntityDrops")) {
			this.method_5783(SoundEvents.field_14809, 1.0F, 1.0F);
			if (entity instanceof PlayerEntity) {
				PlayerEntity playerEntity = (PlayerEntity)entity;
				if (playerEntity.abilities.creativeMode) {
					return;
				}
			}

			this.method_5706(Items.field_8892);
		}
	}

	@Override
	public void method_6894() {
		this.method_5783(SoundEvents.field_14875, 1.0F, 1.0F);
	}

	@Override
	public void setPositionAndAngles(double d, double e, double f, float g, float h) {
		this.setPosition(d, e, f);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setPositionAndRotations(double d, double e, double f, float g, float h, int i, boolean bl) {
		BlockPos blockPos = this.field_7100.add(d - this.x, e - this.y, f - this.z);
		this.setPosition((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
	}

	@Override
	public Packet<?> method_18002() {
		return new PaintingSpawnS2CPacket(this);
	}
}
