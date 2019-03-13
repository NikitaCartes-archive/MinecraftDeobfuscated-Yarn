package net.minecraft.block.entity;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
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
	private TextComponent field_11959;

	public EnchantingTableBlockEntity() {
		super(BlockEntityType.ENCHANTING_TABLE);
	}

	@Override
	public CompoundTag method_11007(CompoundTag compoundTag) {
		super.method_11007(compoundTag);
		if (this.hasCustomName()) {
			compoundTag.putString("CustomName", TextComponent.Serializer.toJsonString(this.field_11959));
		}

		return compoundTag;
	}

	@Override
	public void method_11014(CompoundTag compoundTag) {
		super.method_11014(compoundTag);
		if (compoundTag.containsKey("CustomName", 8)) {
			this.field_11959 = TextComponent.Serializer.fromJsonString(compoundTag.getString("CustomName"));
		}
	}

	@Override
	public void tick() {
		this.pageTurningSpeed = this.nextPageTurningSpeed;
		this.field_11963 = this.field_11964;
		PlayerEntity playerEntity = this.world
			.method_18459(
				(double)((float)this.field_11867.getX() + 0.5F),
				(double)((float)this.field_11867.getY() + 0.5F),
				(double)((float)this.field_11867.getZ() + 0.5F),
				3.0,
				false
			);
		if (playerEntity != null) {
			double d = playerEntity.x - (double)((float)this.field_11867.getX() + 0.5F);
			double e = playerEntity.z - (double)((float)this.field_11867.getZ() + 0.5F);
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
	public TextComponent method_5477() {
		return (TextComponent)(this.field_11959 != null ? this.field_11959 : new TranslatableTextComponent("container.enchant"));
	}

	public void method_11179(@Nullable TextComponent textComponent) {
		this.field_11959 = textComponent;
	}

	@Nullable
	@Override
	public TextComponent method_5797() {
		return this.field_11959;
	}
}
