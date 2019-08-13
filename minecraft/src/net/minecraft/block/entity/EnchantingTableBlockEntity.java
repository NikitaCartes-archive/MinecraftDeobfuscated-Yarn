package net.minecraft.block.entity;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Nameable;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.MathHelper;

public class EnchantingTableBlockEntity extends BlockEntity implements Nameable, Tickable {
	public int ticks;
	public float nextPageAngle;
	public float pageAngle;
	public float field_11969;
	public float field_11967;
	public float nextPageTurningSpeed;
	public float pageTurningSpeed;
	public float field_11964;
	public float field_11963;
	public float field_11962;
	private static final Random RANDOM = new Random();
	private Text customName;

	public EnchantingTableBlockEntity() {
		super(BlockEntityType.field_11912);
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		if (this.hasCustomName()) {
			compoundTag.putString("CustomName", Text.Serializer.toJson(this.customName));
		}

		return compoundTag;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		if (compoundTag.containsKey("CustomName", 8)) {
			this.customName = Text.Serializer.fromJson(compoundTag.getString("CustomName"));
		}
	}

	@Override
	public void tick() {
		this.pageTurningSpeed = this.nextPageTurningSpeed;
		this.field_11963 = this.field_11964;
		PlayerEntity playerEntity = this.world
			.getClosestPlayer((double)((float)this.pos.getX() + 0.5F), (double)((float)this.pos.getY() + 0.5F), (double)((float)this.pos.getZ() + 0.5F), 3.0, false);
		if (playerEntity != null) {
			double d = playerEntity.x - (double)((float)this.pos.getX() + 0.5F);
			double e = playerEntity.z - (double)((float)this.pos.getZ() + 0.5F);
			this.field_11962 = (float)MathHelper.atan2(e, d);
			this.nextPageTurningSpeed += 0.1F;
			if (this.nextPageTurningSpeed < 0.5F || RANDOM.nextInt(40) == 0) {
				float f = this.field_11969;

				do {
					this.field_11969 = this.field_11969 + (float)(RANDOM.nextInt(4) - RANDOM.nextInt(4));
				} while (f == this.field_11969);
			}
		} else {
			this.field_11962 += 0.02F;
			this.nextPageTurningSpeed -= 0.1F;
		}

		while (this.field_11964 >= (float) Math.PI) {
			this.field_11964 -= (float) (Math.PI * 2);
		}

		while (this.field_11964 < (float) -Math.PI) {
			this.field_11964 += (float) (Math.PI * 2);
		}

		while (this.field_11962 >= (float) Math.PI) {
			this.field_11962 -= (float) (Math.PI * 2);
		}

		while (this.field_11962 < (float) -Math.PI) {
			this.field_11962 += (float) (Math.PI * 2);
		}

		float g = this.field_11962 - this.field_11964;

		while (g >= (float) Math.PI) {
			g -= (float) (Math.PI * 2);
		}

		while (g < (float) -Math.PI) {
			g += (float) (Math.PI * 2);
		}

		this.field_11964 += g * 0.4F;
		this.nextPageTurningSpeed = MathHelper.clamp(this.nextPageTurningSpeed, 0.0F, 1.0F);
		this.ticks++;
		this.pageAngle = this.nextPageAngle;
		float h = (this.field_11969 - this.nextPageAngle) * 0.4F;
		float i = 0.2F;
		h = MathHelper.clamp(h, -0.2F, 0.2F);
		this.field_11967 = this.field_11967 + (h - this.field_11967) * 0.9F;
		this.nextPageAngle = this.nextPageAngle + this.field_11967;
	}

	@Override
	public Text getName() {
		return (Text)(this.customName != null ? this.customName : new TranslatableText("container.enchant"));
	}

	public void setCustomName(@Nullable Text text) {
		this.customName = text;
	}

	@Nullable
	@Override
	public Text getCustomName() {
		return this.customName;
	}
}
