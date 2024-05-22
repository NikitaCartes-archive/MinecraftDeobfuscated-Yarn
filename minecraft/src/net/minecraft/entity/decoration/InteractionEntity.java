package net.minecraft.entity.decoration;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.UUID;
import javax.annotation.Nullable;
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
import org.slf4j.Logger;

public class InteractionEntity extends Entity implements Attackable, Targeter {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final TrackedData<Float> WIDTH = DataTracker.registerData(InteractionEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> HEIGHT = DataTracker.registerData(InteractionEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Boolean> RESPONSE = DataTracker.registerData(InteractionEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final String WIDTH_KEY = "width";
	private static final String HEIGHT_KEY = "height";
	private static final String ATTACK_KEY = "attack";
	private static final String INTERACTION_KEY = "interaction";
	private static final String RESPONSE_KEY = "response";
	@Nullable
	private InteractionEntity.Interaction attack;
	@Nullable
	private InteractionEntity.Interaction interaction;

	public InteractionEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
		this.noClip = true;
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		builder.add(WIDTH, 1.0F);
		builder.add(HEIGHT, 1.0F);
		builder.add(RESPONSE, false);
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		if (nbt.contains("width", NbtElement.NUMBER_TYPE)) {
			this.setInteractionWidth(nbt.getFloat("width"));
		}

		if (nbt.contains("height", NbtElement.NUMBER_TYPE)) {
			this.setInteractionHeight(nbt.getFloat("height"));
		}

		if (nbt.contains("attack")) {
			InteractionEntity.Interaction.CODEC
				.decode(NbtOps.INSTANCE, nbt.get("attack"))
				.resultOrPartial(Util.addPrefix("Interaction entity", LOGGER::error))
				.ifPresent(pair -> this.attack = (InteractionEntity.Interaction)pair.getFirst());
		} else {
			this.attack = null;
		}

		if (nbt.contains("interaction")) {
			InteractionEntity.Interaction.CODEC
				.decode(NbtOps.INSTANCE, nbt.get("interaction"))
				.resultOrPartial(Util.addPrefix("Interaction entity", LOGGER::error))
				.ifPresent(pair -> this.interaction = (InteractionEntity.Interaction)pair.getFirst());
		} else {
			this.interaction = null;
		}

		this.setResponse(nbt.getBoolean("response"));
		this.setBoundingBox(this.calculateBoundingBox());
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putFloat("width", this.getInteractionWidth());
		nbt.putFloat("height", this.getInteractionHeight());
		if (this.attack != null) {
			InteractionEntity.Interaction.CODEC.encodeStart(NbtOps.INSTANCE, this.attack).ifSuccess(nbtElement -> nbt.put("attack", nbtElement));
		}

		if (this.interaction != null) {
			InteractionEntity.Interaction.CODEC.encodeStart(NbtOps.INSTANCE, this.interaction).ifSuccess(nbtElement -> nbt.put("interaction", nbtElement));
		}

		nbt.putBoolean("response", this.shouldRespond());
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
	public boolean canAvoidTraps() {
		return true;
	}

	@Override
	public boolean handleAttack(Entity attacker) {
		if (attacker instanceof PlayerEntity playerEntity) {
			this.attack = new InteractionEntity.Interaction(playerEntity.getUuid(), this.getWorld().getTime());
			if (playerEntity instanceof ServerPlayerEntity serverPlayerEntity) {
				Criteria.PLAYER_HURT_ENTITY.trigger(serverPlayerEntity, this, playerEntity.getDamageSources().generic(), 1.0F, 1.0F, false);
			}

			return !this.shouldRespond();
		} else {
			return false;
		}
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		if (this.getWorld().isClient) {
			return this.shouldRespond() ? ActionResult.SUCCESS : ActionResult.CONSUME;
		} else {
			this.interaction = new InteractionEntity.Interaction(player.getUuid(), this.getWorld().getTime());
			return ActionResult.CONSUME;
		}
	}

	@Override
	public void tick() {
	}

	@Nullable
	@Override
	public LivingEntity getLastAttacker() {
		return this.attack != null ? this.getWorld().getPlayerByUuid(this.attack.player()) : null;
	}

	@Nullable
	@Override
	public LivingEntity getTarget() {
		return this.interaction != null ? this.getWorld().getPlayerByUuid(this.interaction.player()) : null;
	}

	private void setInteractionWidth(float width) {
		this.dataTracker.set(WIDTH, width);
	}

	private float getInteractionWidth() {
		return this.dataTracker.get(WIDTH);
	}

	private void setInteractionHeight(float height) {
		this.dataTracker.set(HEIGHT, height);
	}

	private float getInteractionHeight() {
		return this.dataTracker.get(HEIGHT);
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

	static record Interaction(UUID player, long timestamp) {
		public static final Codec<InteractionEntity.Interaction> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Uuids.INT_STREAM_CODEC.fieldOf("player").forGetter(InteractionEntity.Interaction::player),
						Codec.LONG.fieldOf("timestamp").forGetter(InteractionEntity.Interaction::timestamp)
					)
					.apply(instance, InteractionEntity.Interaction::new)
		);
	}
}
