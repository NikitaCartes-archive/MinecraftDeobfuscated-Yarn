package net.minecraft.entity.mob;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1310;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.world.World;

public abstract class IllagerEntity extends RaiderEntity {
	protected static final TrackedData<Byte> field_7206 = DataTracker.registerData(IllagerEntity.class, TrackedDataHandlerRegistry.BYTE);

	protected IllagerEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_7206, (byte)0);
	}

	@Environment(EnvType.CLIENT)
	protected boolean method_6991(int i) {
		int j = this.dataTracker.get(field_7206);
		return (j & i) != 0;
	}

	protected void method_6992(int i, boolean bl) {
		int j = this.dataTracker.get(field_7206);
		if (bl) {
			j |= i;
		} else {
			j &= ~i;
		}

		this.dataTracker.set(field_7206, (byte)(j & 0xFF));
	}

	@Override
	public class_1310 method_6046() {
		return class_1310.field_6291;
	}

	@Environment(EnvType.CLIENT)
	public IllagerEntity.class_1544 method_6990() {
		return IllagerEntity.class_1544.field_7207;
	}

	@Environment(EnvType.CLIENT)
	public static enum class_1544 {
		field_7207,
		field_7211,
		field_7212,
		field_7208,
		field_7213,
		field_7210;
	}
}
