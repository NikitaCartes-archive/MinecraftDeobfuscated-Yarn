package net.minecraft;

import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public abstract class class_1425 extends FishEntity {
	private class_1425 field_6734;
	private int field_6733 = 1;

	public class_1425(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.goalSelector.add(5, new class_1349(this));
	}

	@Override
	public int getLimitPerChunk() {
		return this.method_6465();
	}

	public int method_6465() {
		return super.getLimitPerChunk();
	}

	@Override
	protected boolean method_6456() {
		return !this.method_6470();
	}

	public boolean method_6470() {
		return this.field_6734 != null && this.field_6734.isValid();
	}

	public class_1425 method_6461(class_1425 arg) {
		this.field_6734 = arg;
		arg.method_6462();
		return arg;
	}

	public void method_6466() {
		this.field_6734.method_6459();
		this.field_6734 = null;
	}

	private void method_6462() {
		this.field_6733++;
	}

	private void method_6459() {
		this.field_6733--;
	}

	public boolean method_6469() {
		return this.method_6467() && this.field_6733 < this.method_6465();
	}

	@Override
	public void update() {
		super.update();
		if (this.method_6467() && this.world.random.nextInt(200) == 1) {
			List<FishEntity> list = this.world.getVisibleEntities(this.getClass(), this.getBoundingBox().expand(8.0, 8.0, 8.0));
			if (list.size() <= 1) {
				this.field_6733 = 1;
			}
		}
	}

	public boolean method_6467() {
		return this.field_6733 > 1;
	}

	public boolean method_6464() {
		return this.squaredDistanceTo(this.field_6734) <= 121.0;
	}

	public void method_6463() {
		if (this.method_6470()) {
			this.getNavigation().method_6335(this.field_6734, 1.0);
		}
	}

	public void method_6468(Stream<class_1425> stream) {
		stream.limit((long)(this.method_6465() - this.field_6733)).filter(arg -> arg != this).forEach(arg -> arg.method_6461(this));
	}

	@Nullable
	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, class_3730 arg, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		super.method_5943(iWorld, localDifficulty, arg, entityData, compoundTag);
		if (entityData == null) {
			entityData = new class_1425.class_1426(this);
		} else {
			this.method_6461(((class_1425.class_1426)entityData).field_6735);
		}

		return entityData;
	}

	public static class class_1426 implements EntityData {
		public final class_1425 field_6735;

		public class_1426(class_1425 arg) {
			this.field_6735 = arg;
		}
	}
}
