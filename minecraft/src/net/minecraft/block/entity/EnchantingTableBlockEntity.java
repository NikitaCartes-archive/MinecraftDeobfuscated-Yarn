package net.minecraft.block.entity;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerProvider;
import net.minecraft.container.EnchantingTableContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.MathHelper;

public class EnchantingTableBlockEntity extends BlockEntity implements ContainerProvider, Tickable {
	public int ticks;
	public float field_11958;
	public float field_11960;
	public float field_11969;
	public float field_11967;
	public float field_11966;
	public float field_11965;
	public float field_11964;
	public float field_11963;
	public float field_11962;
	private static final Random RANDOM = new Random();
	private TextComponent customName;

	public EnchantingTableBlockEntity() {
		super(BlockEntityType.ENCHANTING_TABLE);
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		if (this.hasCustomName()) {
			compoundTag.putString("CustomName", TextComponent.Serializer.toJsonString(this.customName));
		}

		return compoundTag;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		if (compoundTag.containsKey("CustomName", 8)) {
			this.customName = TextComponent.Serializer.fromJsonString(compoundTag.getString("CustomName"));
		}
	}

	@Override
	public void tick() {
		this.field_11965 = this.field_11966;
		this.field_11963 = this.field_11964;
		PlayerEntity playerEntity = this.world
			.getClosestPlayer((double)((float)this.pos.getX() + 0.5F), (double)((float)this.pos.getY() + 0.5F), (double)((float)this.pos.getZ() + 0.5F), 3.0, false);
		if (playerEntity != null) {
			double d = playerEntity.x - (double)((float)this.pos.getX() + 0.5F);
			double e = playerEntity.z - (double)((float)this.pos.getZ() + 0.5F);
			this.field_11962 = (float)MathHelper.atan2(e, d);
			this.field_11966 += 0.1F;
			if (this.field_11966 < 0.5F || RANDOM.nextInt(40) == 0) {
				float f = this.field_11969;

				do {
					this.field_11969 = this.field_11969 + (float)(RANDOM.nextInt(4) - RANDOM.nextInt(4));
				} while (f == this.field_11969);
			}
		} else {
			this.field_11962 += 0.02F;
			this.field_11966 -= 0.1F;
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
		this.field_11966 = MathHelper.clamp(this.field_11966, 0.0F, 1.0F);
		this.ticks++;
		this.field_11960 = this.field_11958;
		float h = (this.field_11969 - this.field_11958) * 0.4F;
		float i = 0.2F;
		h = MathHelper.clamp(h, -0.2F, 0.2F);
		this.field_11967 = this.field_11967 + (h - this.field_11967) * 0.9F;
		this.field_11958 = this.field_11958 + this.field_11967;
	}

	@Override
	public TextComponent getName() {
		return (TextComponent)(this.customName != null ? this.customName : new TranslatableTextComponent("container.enchant"));
	}

	public void setCustomName(@Nullable TextComponent textComponent) {
		this.customName = textComponent;
	}

	@Nullable
	@Override
	public TextComponent getCustomName() {
		return this.customName;
	}

	@Override
	public Container createContainer(PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new EnchantingTableContainer(playerInventory, this.world, this.pos);
	}

	@Override
	public String getContainerId() {
		return "minecraft:enchanting_table";
	}
}
