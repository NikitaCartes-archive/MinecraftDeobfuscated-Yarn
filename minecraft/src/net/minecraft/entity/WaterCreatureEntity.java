package net.minecraft.entity;

import net.minecraft.class_1310;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.sortme.Living;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public abstract class WaterCreatureEntity extends MobEntityWithAi implements Living {
	protected WaterCreatureEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public boolean method_6094() {
		return true;
	}

	@Override
	public class_1310 method_6046() {
		return class_1310.field_6292;
	}

	@Override
	public boolean method_5957(ViewableWorld viewableWorld) {
		return viewableWorld.method_8606(this, this.getBoundingBox()) && viewableWorld.method_8587(this, this.getBoundingBox());
	}

	@Override
	public int method_5970() {
		return 120;
	}

	@Override
	public boolean method_5974(double d) {
		return true;
	}

	@Override
	protected int method_6110(PlayerEntity playerEntity) {
		return 1 + this.world.random.nextInt(3);
	}

	protected void method_6673(int i) {
		if (this.isValid() && !this.method_5816()) {
			this.setBreath(i - 1);
			if (this.getBreath() == -20) {
				this.setBreath(0);
				this.damage(DamageSource.DROWN, 2.0F);
			}
		} else {
			this.setBreath(300);
		}
	}

	@Override
	public void updateLogic() {
		int i = this.getBreath();
		super.updateLogic();
		this.method_6673(i);
	}

	@Override
	public boolean canFly() {
		return false;
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity playerEntity) {
		return false;
	}
}
