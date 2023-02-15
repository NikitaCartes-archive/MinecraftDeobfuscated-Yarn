/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.decoration;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.UUID;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Targeter;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class InteractionEntity
extends Entity
implements Attackable,
Targeter {
    private static final Logger field_42624 = LogUtils.getLogger();
    private static final TrackedData<Float> WIDTH = DataTracker.registerData(InteractionEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> HEIGHT = DataTracker.registerData(InteractionEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Boolean> RESPONSE = DataTracker.registerData(InteractionEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final String WIDTH_KEY = "width";
    private static final String HEIGHT_KEY = "height";
    private static final String ATTACK_KEY = "attack";
    private static final String INTERACTION_KEY = "interaction";
    private static final String RESPONSE_KEY = "response";
    @Nullable
    private Interaction attack;
    @Nullable
    private Interaction interaction;

    public InteractionEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
        this.noClip = true;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(WIDTH, Float.valueOf(1.0f));
        this.dataTracker.startTracking(HEIGHT, Float.valueOf(1.0f));
        this.dataTracker.startTracking(RESPONSE, false);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains(WIDTH_KEY, NbtElement.NUMBER_TYPE)) {
            this.setInteractionWidth(nbt.getFloat(WIDTH_KEY));
        }
        if (nbt.contains(HEIGHT_KEY, NbtElement.NUMBER_TYPE)) {
            this.setInteractionHeight(nbt.getFloat(HEIGHT_KEY));
        }
        if (nbt.contains(ATTACK_KEY)) {
            Interaction.CODEC.decode(NbtOps.INSTANCE, nbt.get(ATTACK_KEY)).resultOrPartial(Util.addPrefix("Interaction entity", field_42624::error)).ifPresent(pair -> {
                this.attack = (Interaction)pair.getFirst();
            });
        } else {
            this.attack = null;
        }
        if (nbt.contains(INTERACTION_KEY)) {
            Interaction.CODEC.decode(NbtOps.INSTANCE, nbt.get(INTERACTION_KEY)).resultOrPartial(Util.addPrefix("Interaction entity", field_42624::error)).ifPresent(pair -> {
                this.interaction = (Interaction)pair.getFirst();
            });
        } else {
            this.interaction = null;
        }
        this.setResponse(nbt.getBoolean(RESPONSE_KEY));
        this.setBoundingBox(this.calculateBoundingBox());
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putFloat(WIDTH_KEY, this.getInteractionWidth());
        nbt.putFloat(HEIGHT_KEY, this.getInteractionHeight());
        if (this.attack != null) {
            Interaction.CODEC.encodeStart(NbtOps.INSTANCE, this.attack).result().ifPresent(nbtElement -> nbt.put(ATTACK_KEY, (NbtElement)nbtElement));
        }
        if (this.interaction != null) {
            Interaction.CODEC.encodeStart(NbtOps.INSTANCE, this.interaction).result().ifPresent(nbtElement -> nbt.put(INTERACTION_KEY, (NbtElement)nbtElement));
        }
        nbt.putBoolean(RESPONSE_KEY, this.shouldRespond());
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (HEIGHT.equals(data) || WIDTH.equals(data)) {
            this.setBoundingBox(this.calculateBoundingBox());
        }
    }

    @Override
    public boolean canBeHitByProjectile() {
        return false;
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public PistonBehavior getPistonBehavior() {
        return PistonBehavior.IGNORE;
    }

    @Override
    public boolean handleAttack(Entity attacker) {
        if (attacker instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)attacker;
            this.attack = new Interaction(playerEntity.getUuid(), this.world.getTime());
            if (playerEntity instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)playerEntity;
                Criteria.PLAYER_HURT_ENTITY.trigger(serverPlayerEntity, this, playerEntity.getDamageSources().generic(), 1.0f, 1.0f, false);
            }
            return !this.shouldRespond();
        }
        return false;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (this.world.isClient) {
            return this.shouldRespond() ? ActionResult.SUCCESS : ActionResult.CONSUME;
        }
        this.interaction = new Interaction(player.getUuid(), this.world.getTime());
        return ActionResult.CONSUME;
    }

    @Override
    public void tick() {
    }

    @Override
    @Nullable
    public LivingEntity getLastAttacker() {
        if (this.attack != null) {
            return this.world.getPlayerByUuid(this.attack.player());
        }
        return null;
    }

    @Override
    @Nullable
    public LivingEntity getTarget() {
        if (this.interaction != null) {
            return this.world.getPlayerByUuid(this.interaction.player());
        }
        return null;
    }

    private void setInteractionWidth(float width) {
        this.dataTracker.set(WIDTH, Float.valueOf(width));
    }

    private float getInteractionWidth() {
        return this.dataTracker.get(WIDTH).floatValue();
    }

    private void setInteractionHeight(float height) {
        this.dataTracker.set(HEIGHT, Float.valueOf(height));
    }

    private float getInteractionHeight() {
        return this.dataTracker.get(HEIGHT).floatValue();
    }

    private void setResponse(boolean response) {
        this.dataTracker.set(RESPONSE, response);
    }

    private boolean shouldRespond() {
        return this.dataTracker.get(RESPONSE);
    }

    private EntityDimensions getDimensions() {
        return EntityDimensions.changing(this.getInteractionWidth(), this.getInteractionHeight());
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return this.getDimensions();
    }

    @Override
    protected Box calculateBoundingBox() {
        return this.getDimensions().getBoxAt(this.getPos());
    }

    record Interaction(UUID player, long timestamp) {
        public static final Codec<Interaction> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Uuids.INT_STREAM_CODEC.fieldOf("player")).forGetter(Interaction::player), ((MapCodec)Codec.LONG.fieldOf("timestamp")).forGetter(Interaction::timestamp)).apply((Applicative<Interaction, ?>)instance, Interaction::new));
    }
}

