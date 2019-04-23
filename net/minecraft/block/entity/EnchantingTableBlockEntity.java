/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Nameable;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class EnchantingTableBlockEntity
extends BlockEntity
implements Nameable,
Tickable {
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
    private Component customName;

    public EnchantingTableBlockEntity() {
        super(BlockEntityType.ENCHANTING_TABLE);
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        super.toTag(compoundTag);
        if (this.hasCustomName()) {
            compoundTag.putString("CustomName", Component.Serializer.toJsonString(this.customName));
        }
        return compoundTag;
    }

    @Override
    public void fromTag(CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        if (compoundTag.containsKey("CustomName", 8)) {
            this.customName = Component.Serializer.fromJsonString(compoundTag.getString("CustomName"));
        }
    }

    @Override
    public void tick() {
        float g;
        this.pageTurningSpeed = this.nextPageTurningSpeed;
        this.field_11963 = this.field_11964;
        PlayerEntity playerEntity = this.world.getClosestPlayer((double)((float)this.pos.getX() + 0.5f), (double)((float)this.pos.getY() + 0.5f), (double)((float)this.pos.getZ() + 0.5f), 3.0, false);
        if (playerEntity != null) {
            double d = playerEntity.x - (double)((float)this.pos.getX() + 0.5f);
            double e = playerEntity.z - (double)((float)this.pos.getZ() + 0.5f);
            this.field_11962 = (float)MathHelper.atan2(e, d);
            this.nextPageTurningSpeed += 0.1f;
            if (this.nextPageTurningSpeed < 0.5f || RANDOM.nextInt(40) == 0) {
                float f = this.field_11969;
                do {
                    this.field_11969 += (float)(RANDOM.nextInt(4) - RANDOM.nextInt(4));
                } while (f == this.field_11969);
            }
        } else {
            this.field_11962 += 0.02f;
            this.nextPageTurningSpeed -= 0.1f;
        }
        while (this.field_11964 >= (float)Math.PI) {
            this.field_11964 -= (float)Math.PI * 2;
        }
        while (this.field_11964 < (float)(-Math.PI)) {
            this.field_11964 += (float)Math.PI * 2;
        }
        while (this.field_11962 >= (float)Math.PI) {
            this.field_11962 -= (float)Math.PI * 2;
        }
        while (this.field_11962 < (float)(-Math.PI)) {
            this.field_11962 += (float)Math.PI * 2;
        }
        for (g = this.field_11962 - this.field_11964; g >= (float)Math.PI; g -= (float)Math.PI * 2) {
        }
        while (g < (float)(-Math.PI)) {
            g += (float)Math.PI * 2;
        }
        this.field_11964 += g * 0.4f;
        this.nextPageTurningSpeed = MathHelper.clamp(this.nextPageTurningSpeed, 0.0f, 1.0f);
        ++this.ticks;
        this.pageAngle = this.nextPageAngle;
        float h = (this.field_11969 - this.nextPageAngle) * 0.4f;
        float i = 0.2f;
        h = MathHelper.clamp(h, -0.2f, 0.2f);
        this.field_11967 += (h - this.field_11967) * 0.9f;
        this.nextPageAngle += this.field_11967;
    }

    @Override
    public Component getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return new TranslatableComponent("container.enchant", new Object[0]);
    }

    public void setCustomName(@Nullable Component component) {
        this.customName = component;
    }

    @Override
    @Nullable
    public Component getCustomName() {
        return this.customName;
    }
}

