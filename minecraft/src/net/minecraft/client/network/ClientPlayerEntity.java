package net.minecraft.client.network;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.JigsawBlockScreen;
import net.minecraft.client.gui.screen.ingame.MinecartCommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen;
import net.minecraft.client.input.Input;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.sound.AmbientSoundLoops;
import net.minecraft.client.sound.AmbientSoundPlayer;
import net.minecraft.client.sound.BiomeEffectSoundPlayer;
import net.minecraft.client.sound.BubbleColumnSoundPlayer;
import net.minecraft.client.sound.ElytraSoundInstance;
import net.minecraft.client.sound.MinecartInsideSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.RecipeBookDataC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Recipe;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.StatHandler;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.CommandBlockExecutor;

/**
 * Represents the client's own player entity.
 */
@Environment(EnvType.CLIENT)
public class ClientPlayerEntity extends AbstractClientPlayerEntity {
	private static final int field_32671 = 20;
	private static final int field_32672 = 600;
	private static final int field_32673 = 100;
	private static final float field_32674 = 0.6F;
	private static final double field_32675 = 0.35;
	private static final double MAX_SOFT_COLLISION_RADIANS = 0.13962634F;
	private static final float field_38337 = 0.3F;
	public final ClientPlayNetworkHandler networkHandler;
	private final StatHandler statHandler;
	private final ClientRecipeBook recipeBook;
	private final List<ClientPlayerTickable> tickables = Lists.<ClientPlayerTickable>newArrayList();
	private int clientPermissionLevel = 0;
	private double lastX;
	private double lastBaseY;
	private double lastZ;
	private float lastYaw;
	private float lastPitch;
	private boolean lastOnGround;
	private boolean inSneakingPose;
	private boolean lastSneaking;
	private boolean lastSprinting;
	private int ticksSinceLastPositionPacketSent;
	private boolean healthInitialized;
	@Nullable
	private String serverBrand;
	public Input input;
	protected final MinecraftClient client;
	protected int ticksLeftToDoubleTapSprint;
	public int ticksSinceSprintingChanged;
	public float renderYaw;
	public float renderPitch;
	public float lastRenderYaw;
	public float lastRenderPitch;
	private int field_3938;
	private float mountJumpStrength;
	public float nextNauseaStrength;
	public float lastNauseaStrength;
	private boolean usingItem;
	@Nullable
	private Hand activeHand;
	private boolean riding;
	private boolean autoJumpEnabled = true;
	private int ticksToNextAutojump;
	private boolean falling;
	private int underwaterVisibilityTicks;
	private boolean showsDeathScreen = true;

	public ClientPlayerEntity(
		MinecraftClient client,
		ClientWorld world,
		ClientPlayNetworkHandler networkHandler,
		StatHandler stats,
		ClientRecipeBook recipeBook,
		boolean lastSneaking,
		boolean lastSprinting
	) {
		super(world, networkHandler.getProfile());
		this.client = client;
		this.networkHandler = networkHandler;
		this.statHandler = stats;
		this.recipeBook = recipeBook;
		this.lastSneaking = lastSneaking;
		this.lastSprinting = lastSprinting;
		this.tickables.add(new AmbientSoundPlayer(this, client.getSoundManager()));
		this.tickables.add(new BubbleColumnSoundPlayer(this));
		this.tickables.add(new BiomeEffectSoundPlayer(this, client.getSoundManager(), world.getBiomeAccess()));
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		return false;
	}

	@Override
	public void heal(float amount) {
	}

	@Override
	public boolean startRiding(Entity entity, boolean force) {
		if (!super.startRiding(entity, force)) {
			return false;
		} else {
			if (entity instanceof AbstractMinecartEntity) {
				this.client.getSoundManager().play(new MinecartInsideSoundInstance(this, (AbstractMinecartEntity)entity, true));
				this.client.getSoundManager().play(new MinecartInsideSoundInstance(this, (AbstractMinecartEntity)entity, false));
			}

			return true;
		}
	}

	@Override
	public void dismountVehicle() {
		super.dismountVehicle();
		this.riding = false;
	}

	@Override
	public float getPitch(float tickDelta) {
		return this.getPitch();
	}

	@Override
	public float getYaw(float tickDelta) {
		return this.hasVehicle() ? super.getYaw(tickDelta) : this.getYaw();
	}

	@Override
	public void tick() {
		if (this.world.isPosLoaded(this.getBlockX(), this.getBlockZ())) {
			super.tick();
			if (this.hasVehicle()) {
				this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(this.getYaw(), this.getPitch(), this.onGround));
				this.networkHandler.sendPacket(new PlayerInputC2SPacket(this.sidewaysSpeed, this.forwardSpeed, this.input.jumping, this.input.sneaking));
				Entity entity = this.getRootVehicle();
				if (entity != this && entity.isLogicalSideForUpdatingMovement()) {
					this.networkHandler.sendPacket(new VehicleMoveC2SPacket(entity));
				}
			} else {
				this.sendMovementPackets();
			}

			for (ClientPlayerTickable clientPlayerTickable : this.tickables) {
				clientPlayerTickable.tick();
			}
		}
	}

	/**
	 * {@return the percentage for the biome mood sound for the debug HUD to
	 * display}
	 */
	public float getMoodPercentage() {
		for (ClientPlayerTickable clientPlayerTickable : this.tickables) {
			if (clientPlayerTickable instanceof BiomeEffectSoundPlayer) {
				return ((BiomeEffectSoundPlayer)clientPlayerTickable).getMoodPercentage();
			}
		}

		return 0.0F;
	}

	private void sendMovementPackets() {
		boolean bl = this.isSprinting();
		if (bl != this.lastSprinting) {
			ClientCommandC2SPacket.Mode mode = bl ? ClientCommandC2SPacket.Mode.START_SPRINTING : ClientCommandC2SPacket.Mode.STOP_SPRINTING;
			this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, mode));
			this.lastSprinting = bl;
		}

		boolean bl2 = this.isSneaking();
		if (bl2 != this.lastSneaking) {
			ClientCommandC2SPacket.Mode mode2 = bl2 ? ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY : ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY;
			this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, mode2));
			this.lastSneaking = bl2;
		}

		if (this.isCamera()) {
			double d = this.getX() - this.lastX;
			double e = this.getY() - this.lastBaseY;
			double f = this.getZ() - this.lastZ;
			double g = (double)(this.getYaw() - this.lastYaw);
			double h = (double)(this.getPitch() - this.lastPitch);
			this.ticksSinceLastPositionPacketSent++;
			boolean bl3 = MathHelper.squaredMagnitude(d, e, f) > MathHelper.square(2.0E-4) || this.ticksSinceLastPositionPacketSent >= 20;
			boolean bl4 = g != 0.0 || h != 0.0;
			if (this.hasVehicle()) {
				Vec3d vec3d = this.getVelocity();
				this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(vec3d.x, -999.0, vec3d.z, this.getYaw(), this.getPitch(), this.onGround));
				bl3 = false;
			} else if (bl3 && bl4) {
				this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch(), this.onGround));
			} else if (bl3) {
				this.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(this.getX(), this.getY(), this.getZ(), this.onGround));
			} else if (bl4) {
				this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(this.getYaw(), this.getPitch(), this.onGround));
			} else if (this.lastOnGround != this.onGround) {
				this.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(this.onGround));
			}

			if (bl3) {
				this.lastX = this.getX();
				this.lastBaseY = this.getY();
				this.lastZ = this.getZ();
				this.ticksSinceLastPositionPacketSent = 0;
			}

			if (bl4) {
				this.lastYaw = this.getYaw();
				this.lastPitch = this.getPitch();
			}

			this.lastOnGround = this.onGround;
			this.autoJumpEnabled = this.client.options.getAutoJump().getValue();
		}
	}

	public boolean dropSelectedItem(boolean entireStack) {
		PlayerActionC2SPacket.Action action = entireStack ? PlayerActionC2SPacket.Action.DROP_ALL_ITEMS : PlayerActionC2SPacket.Action.DROP_ITEM;
		ItemStack itemStack = this.getInventory().dropSelectedItem(entireStack);
		this.networkHandler.sendPacket(new PlayerActionC2SPacket(action, BlockPos.ORIGIN, Direction.DOWN));
		return !itemStack.isEmpty();
	}

	/**
	 * Sends a chat message to the server.
	 * 
	 * <p>The message will be truncated to at most 256 characters before
	 * sending to the server.
	 * 
	 * <p>If the message contains an invalid character (see {@link
	 * net.minecraft.SharedConstants#isValidChar isValidChar}), the server will
	 * reject the message and disconnect the client.
	 * 
	 * @apiNote This method is used to send a message typed in {@linkplain
	 * net.minecraft.client.gui.screen the chat screen}. This includes a
	 * command message (a message that starts with {@code /}).
	 * 
	 * @param message the message to send
	 */
	public void sendChatMessage(String message) {
		this.networkHandler.sendPacket(new ChatMessageC2SPacket(message));
	}

	@Override
	public void swingHand(Hand hand) {
		super.swingHand(hand);
		this.networkHandler.sendPacket(new HandSwingC2SPacket(hand));
	}

	@Override
	public void requestRespawn() {
		this.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN));
	}

	@Override
	protected void applyDamage(DamageSource source, float amount) {
		if (!this.isInvulnerableTo(source)) {
			this.setHealth(this.getHealth() - amount);
		}
	}

	@Override
	public void closeHandledScreen() {
		this.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(this.currentScreenHandler.syncId));
		this.closeScreen();
	}

	public void closeScreen() {
		super.closeHandledScreen();
		this.client.setScreen(null);
	}

	public void updateHealth(float health) {
		if (this.healthInitialized) {
			float f = this.getHealth() - health;
			if (f <= 0.0F) {
				this.setHealth(health);
				if (f < 0.0F) {
					this.timeUntilRegen = 10;
				}
			} else {
				this.lastDamageTaken = f;
				this.timeUntilRegen = 20;
				this.setHealth(health);
				this.maxHurtTime = 10;
				this.hurtTime = this.maxHurtTime;
			}
		} else {
			this.setHealth(health);
			this.healthInitialized = true;
		}
	}

	@Override
	public void sendAbilitiesUpdate() {
		this.networkHandler.sendPacket(new UpdatePlayerAbilitiesC2SPacket(this.getAbilities()));
	}

	@Override
	public boolean isMainPlayer() {
		return true;
	}

	@Override
	public boolean isHoldingOntoLadder() {
		return !this.getAbilities().flying && super.isHoldingOntoLadder();
	}

	@Override
	public boolean shouldSpawnSprintingParticles() {
		return !this.getAbilities().flying && super.shouldSpawnSprintingParticles();
	}

	@Override
	public boolean shouldDisplaySoulSpeedEffects() {
		return !this.getAbilities().flying && super.shouldDisplaySoulSpeedEffects();
	}

	protected void startRidingJump() {
		this.networkHandler
			.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.START_RIDING_JUMP, MathHelper.floor(this.getMountJumpStrength() * 100.0F)));
	}

	public void openRidingInventory() {
		this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.OPEN_INVENTORY));
	}

	public void setServerBrand(@Nullable String serverBrand) {
		this.serverBrand = serverBrand;
	}

	@Nullable
	public String getServerBrand() {
		return this.serverBrand;
	}

	public StatHandler getStatHandler() {
		return this.statHandler;
	}

	public ClientRecipeBook getRecipeBook() {
		return this.recipeBook;
	}

	public void onRecipeDisplayed(Recipe<?> recipe) {
		if (this.recipeBook.shouldDisplay(recipe)) {
			this.recipeBook.onRecipeDisplayed(recipe);
			this.networkHandler.sendPacket(new RecipeBookDataC2SPacket(recipe));
		}
	}

	@Override
	protected int getPermissionLevel() {
		return this.clientPermissionLevel;
	}

	public void setClientPermissionLevel(int clientPermissionLevel) {
		this.clientPermissionLevel = clientPermissionLevel;
	}

	@Override
	public void sendMessage(Text message, boolean actionBar) {
		if (actionBar) {
			this.client.inGameHud.setOverlayMessage(message, false);
		} else {
			this.client.inGameHud.getChatHud().addMessage(message);
		}
	}

	private void pushOutOfBlocks(double x, double z) {
		BlockPos blockPos = new BlockPos(x, this.getY(), z);
		if (this.wouldCollideAt(blockPos)) {
			double d = x - (double)blockPos.getX();
			double e = z - (double)blockPos.getZ();
			Direction direction = null;
			double f = Double.MAX_VALUE;
			Direction[] directions = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};

			for (Direction direction2 : directions) {
				double g = direction2.getAxis().choose(d, 0.0, e);
				double h = direction2.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - g : g;
				if (h < f && !this.wouldCollideAt(blockPos.offset(direction2))) {
					f = h;
					direction = direction2;
				}
			}

			if (direction != null) {
				Vec3d vec3d = this.getVelocity();
				if (direction.getAxis() == Direction.Axis.X) {
					this.setVelocity(0.1 * (double)direction.getOffsetX(), vec3d.y, vec3d.z);
				} else {
					this.setVelocity(vec3d.x, vec3d.y, 0.1 * (double)direction.getOffsetZ());
				}
			}
		}
	}

	private boolean wouldCollideAt(BlockPos pos) {
		Box box = this.getBoundingBox();
		Box box2 = new Box((double)pos.getX(), box.minY, (double)pos.getZ(), (double)pos.getX() + 1.0, box.maxY, (double)pos.getZ() + 1.0).contract(1.0E-7);
		return this.world.canCollide(this, box2);
	}

	@Override
	public void setSprinting(boolean sprinting) {
		super.setSprinting(sprinting);
		this.ticksSinceSprintingChanged = 0;
	}

	public void setExperience(float progress, int total, int level) {
		this.experienceProgress = progress;
		this.totalExperience = total;
		this.experienceLevel = level;
	}

	@Override
	public void sendSystemMessage(Text message, UUID sender) {
		this.client.inGameHud.getChatHud().addMessage(message);
	}

	@Override
	public void handleStatus(byte status) {
		if (status >= EntityStatuses.SET_OP_LEVEL_0 && status <= EntityStatuses.SET_OP_LEVEL_4) {
			this.setClientPermissionLevel(status - EntityStatuses.SET_OP_LEVEL_0);
		} else {
			super.handleStatus(status);
		}
	}

	public void setShowsDeathScreen(boolean showsDeathScreen) {
		this.showsDeathScreen = showsDeathScreen;
	}

	public boolean showsDeathScreen() {
		return this.showsDeathScreen;
	}

	@Override
	public void playSound(SoundEvent sound, float volume, float pitch) {
		this.world.playSound(this.getX(), this.getY(), this.getZ(), sound, this.getSoundCategory(), volume, pitch, false);
	}

	@Override
	public void playSound(SoundEvent event, SoundCategory category, float volume, float pitch) {
		this.world.playSound(this.getX(), this.getY(), this.getZ(), event, category, volume, pitch, false);
	}

	@Override
	public boolean canMoveVoluntarily() {
		return true;
	}

	@Override
	public void setCurrentHand(Hand hand) {
		ItemStack itemStack = this.getStackInHand(hand);
		if (!itemStack.isEmpty() && !this.isUsingItem()) {
			super.setCurrentHand(hand);
			this.usingItem = true;
			this.activeHand = hand;
		}
	}

	@Override
	public boolean isUsingItem() {
		return this.usingItem;
	}

	@Override
	public void clearActiveItem() {
		super.clearActiveItem();
		this.usingItem = false;
	}

	@Override
	public Hand getActiveHand() {
		return (Hand)Objects.requireNonNullElse(this.activeHand, Hand.MAIN_HAND);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		super.onTrackedDataSet(data);
		if (LIVING_FLAGS.equals(data)) {
			boolean bl = (this.dataTracker.get(LIVING_FLAGS) & 1) > 0;
			Hand hand = (this.dataTracker.get(LIVING_FLAGS) & 2) > 0 ? Hand.OFF_HAND : Hand.MAIN_HAND;
			if (bl && !this.usingItem) {
				this.setCurrentHand(hand);
			} else if (!bl && this.usingItem) {
				this.clearActiveItem();
			}
		}

		if (FLAGS.equals(data) && this.isFallFlying() && !this.falling) {
			this.client.getSoundManager().play(new ElytraSoundInstance(this));
		}
	}

	public boolean hasJumpingMount() {
		Entity entity = this.getVehicle();
		return this.hasVehicle() && entity instanceof JumpingMount && ((JumpingMount)entity).canJump();
	}

	public float getMountJumpStrength() {
		return this.mountJumpStrength;
	}

	@Override
	public void openEditSignScreen(SignBlockEntity sign) {
		this.client.setScreen(new SignEditScreen(sign, this.client.shouldFilterText()));
	}

	@Override
	public void openCommandBlockMinecartScreen(CommandBlockExecutor commandBlockExecutor) {
		this.client.setScreen(new MinecartCommandBlockScreen(commandBlockExecutor));
	}

	@Override
	public void openCommandBlockScreen(CommandBlockBlockEntity commandBlock) {
		this.client.setScreen(new CommandBlockScreen(commandBlock));
	}

	@Override
	public void openStructureBlockScreen(StructureBlockBlockEntity structureBlock) {
		this.client.setScreen(new StructureBlockScreen(structureBlock));
	}

	@Override
	public void openJigsawScreen(JigsawBlockEntity jigsaw) {
		this.client.setScreen(new JigsawBlockScreen(jigsaw));
	}

	@Override
	public void useBook(ItemStack book, Hand hand) {
		if (book.isOf(Items.WRITABLE_BOOK)) {
			this.client.setScreen(new BookEditScreen(this, book, hand));
		}
	}

	@Override
	public void addCritParticles(Entity target) {
		this.client.particleManager.addEmitter(target, ParticleTypes.CRIT);
	}

	@Override
	public void addEnchantedHitParticles(Entity target) {
		this.client.particleManager.addEmitter(target, ParticleTypes.ENCHANTED_HIT);
	}

	@Override
	public boolean isSneaking() {
		return this.input != null && this.input.sneaking;
	}

	@Override
	public boolean isInSneakingPose() {
		return this.inSneakingPose;
	}

	public boolean shouldSlowDown() {
		return this.isInSneakingPose() || this.shouldLeaveSwimmingPose();
	}

	@Override
	public void tickNewAi() {
		super.tickNewAi();
		if (this.isCamera()) {
			this.sidewaysSpeed = this.input.movementSideways;
			this.forwardSpeed = this.input.movementForward;
			this.jumping = this.input.jumping;
			this.lastRenderYaw = this.renderYaw;
			this.lastRenderPitch = this.renderPitch;
			this.renderPitch = this.renderPitch + (this.getPitch() - this.renderPitch) * 0.5F;
			this.renderYaw = this.renderYaw + (this.getYaw() - this.renderYaw) * 0.5F;
		}
	}

	protected boolean isCamera() {
		return this.client.getCameraEntity() == this;
	}

	public void init() {
		this.setPose(EntityPose.STANDING);
		if (this.world != null) {
			for (double d = this.getY(); d > (double)this.world.getBottomY() && d < (double)this.world.getTopY(); d++) {
				this.setPosition(this.getX(), d, this.getZ());
				if (this.world.isSpaceEmpty(this)) {
					break;
				}
			}

			this.setVelocity(Vec3d.ZERO);
			this.setPitch(0.0F);
		}

		this.setHealth(this.getMaxHealth());
		this.deathTime = 0;
	}

	@Override
	public void tickMovement() {
		this.ticksSinceSprintingChanged++;
		if (this.ticksLeftToDoubleTapSprint > 0) {
			this.ticksLeftToDoubleTapSprint--;
		}

		this.updateNausea();
		boolean bl = this.input.jumping;
		boolean bl2 = this.input.sneaking;
		boolean bl3 = this.isWalking();
		this.inSneakingPose = !this.getAbilities().flying
			&& !this.isSwimming()
			&& this.wouldPoseNotCollide(EntityPose.CROUCHING)
			&& (this.isSneaking() || !this.isSleeping() && !this.wouldPoseNotCollide(EntityPose.STANDING));
		float f = MathHelper.clamp(0.3F + EnchantmentHelper.getSwiftSneakSpeedBoost(this), 0.0F, 1.0F);
		this.input.tick(this.shouldSlowDown(), f);
		this.client.getTutorialManager().onMovement(this.input);
		if (this.isUsingItem() && !this.hasVehicle()) {
			this.input.movementSideways *= 0.2F;
			this.input.movementForward *= 0.2F;
			this.ticksLeftToDoubleTapSprint = 0;
		}

		boolean bl4 = false;
		if (this.ticksToNextAutojump > 0) {
			this.ticksToNextAutojump--;
			bl4 = true;
			this.input.jumping = true;
		}

		if (!this.noClip) {
			this.pushOutOfBlocks(this.getX() - (double)this.getWidth() * 0.35, this.getZ() + (double)this.getWidth() * 0.35);
			this.pushOutOfBlocks(this.getX() - (double)this.getWidth() * 0.35, this.getZ() - (double)this.getWidth() * 0.35);
			this.pushOutOfBlocks(this.getX() + (double)this.getWidth() * 0.35, this.getZ() - (double)this.getWidth() * 0.35);
			this.pushOutOfBlocks(this.getX() + (double)this.getWidth() * 0.35, this.getZ() + (double)this.getWidth() * 0.35);
		}

		if (bl2) {
			this.ticksLeftToDoubleTapSprint = 0;
		}

		boolean bl5 = (float)this.getHungerManager().getFoodLevel() > 6.0F || this.getAbilities().allowFlying;
		if ((this.onGround || this.isSubmergedInWater())
			&& !bl2
			&& !bl3
			&& this.isWalking()
			&& !this.isSprinting()
			&& bl5
			&& !this.isUsingItem()
			&& !this.hasStatusEffect(StatusEffects.BLINDNESS)) {
			if (this.ticksLeftToDoubleTapSprint <= 0 && !this.client.options.sprintKey.isPressed()) {
				this.ticksLeftToDoubleTapSprint = 7;
			} else {
				this.setSprinting(true);
			}
		}

		if (!this.isSprinting()
			&& (!this.isTouchingWater() || this.isSubmergedInWater())
			&& this.isWalking()
			&& bl5
			&& !this.isUsingItem()
			&& !this.hasStatusEffect(StatusEffects.BLINDNESS)
			&& this.client.options.sprintKey.isPressed()) {
			this.setSprinting(true);
		}

		if (this.isSprinting()) {
			boolean bl6 = !this.input.hasForwardMovement() || !bl5;
			boolean bl7 = bl6 || this.horizontalCollision && !this.collidedSoftly || this.isTouchingWater() && !this.isSubmergedInWater();
			if (this.isSwimming()) {
				if (!this.onGround && !this.input.sneaking && bl6 || !this.isTouchingWater()) {
					this.setSprinting(false);
				}
			} else if (bl7) {
				this.setSprinting(false);
			}
		}

		boolean bl6 = false;
		if (this.getAbilities().allowFlying) {
			if (this.client.interactionManager.isFlyingLocked()) {
				if (!this.getAbilities().flying) {
					this.getAbilities().flying = true;
					bl6 = true;
					this.sendAbilitiesUpdate();
				}
			} else if (!bl && this.input.jumping && !bl4) {
				if (this.abilityResyncCountdown == 0) {
					this.abilityResyncCountdown = 7;
				} else if (!this.isSwimming()) {
					this.getAbilities().flying = !this.getAbilities().flying;
					bl6 = true;
					this.sendAbilitiesUpdate();
					this.abilityResyncCountdown = 0;
				}
			}
		}

		if (this.input.jumping && !bl6 && !bl && !this.getAbilities().flying && !this.hasVehicle() && !this.isClimbing()) {
			ItemStack itemStack = this.getEquippedStack(EquipmentSlot.CHEST);
			if (itemStack.isOf(Items.ELYTRA) && ElytraItem.isUsable(itemStack) && this.checkFallFlying()) {
				this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
			}
		}

		this.falling = this.isFallFlying();
		if (this.isTouchingWater() && this.input.sneaking && this.shouldSwimInFluids()) {
			this.knockDownwards();
		}

		if (this.isSubmergedIn(FluidTags.WATER)) {
			int i = this.isSpectator() ? 10 : 1;
			this.underwaterVisibilityTicks = MathHelper.clamp(this.underwaterVisibilityTicks + i, 0, 600);
		} else if (this.underwaterVisibilityTicks > 0) {
			this.isSubmergedIn(FluidTags.WATER);
			this.underwaterVisibilityTicks = MathHelper.clamp(this.underwaterVisibilityTicks - 10, 0, 600);
		}

		if (this.getAbilities().flying && this.isCamera()) {
			int i = 0;
			if (this.input.sneaking) {
				i--;
			}

			if (this.input.jumping) {
				i++;
			}

			if (i != 0) {
				this.setVelocity(this.getVelocity().add(0.0, (double)((float)i * this.getAbilities().getFlySpeed() * 3.0F), 0.0));
			}
		}

		if (this.hasJumpingMount()) {
			JumpingMount jumpingMount = (JumpingMount)this.getVehicle();
			if (this.field_3938 < 0) {
				this.field_3938++;
				if (this.field_3938 == 0) {
					this.mountJumpStrength = 0.0F;
				}
			}

			if (bl && !this.input.jumping) {
				this.field_3938 = -10;
				jumpingMount.setJumpStrength(MathHelper.floor(this.getMountJumpStrength() * 100.0F));
				this.startRidingJump();
			} else if (!bl && this.input.jumping) {
				this.field_3938 = 0;
				this.mountJumpStrength = 0.0F;
			} else if (bl) {
				this.field_3938++;
				if (this.field_3938 < 10) {
					this.mountJumpStrength = (float)this.field_3938 * 0.1F;
				} else {
					this.mountJumpStrength = 0.8F + 2.0F / (float)(this.field_3938 - 9) * 0.1F;
				}
			}
		} else {
			this.mountJumpStrength = 0.0F;
		}

		super.tickMovement();
		if (this.onGround && this.getAbilities().flying && !this.client.interactionManager.isFlyingLocked()) {
			this.getAbilities().flying = false;
			this.sendAbilitiesUpdate();
		}
	}

	@Override
	protected void updatePostDeath() {
		this.deathTime++;
		if (this.deathTime == 20) {
			this.remove(Entity.RemovalReason.KILLED);
		}
	}

	private void updateNausea() {
		this.lastNauseaStrength = this.nextNauseaStrength;
		if (this.inNetherPortal) {
			if (this.client.currentScreen != null
				&& !this.client.currentScreen.shouldPause()
				&& !(this.client.currentScreen instanceof DeathScreen)
				&& !(this.client.currentScreen instanceof DownloadingTerrainScreen)) {
				if (this.client.currentScreen instanceof HandledScreen) {
					this.closeHandledScreen();
				}

				this.client.setScreen(null);
			}

			if (this.nextNauseaStrength == 0.0F) {
				this.client.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.BLOCK_PORTAL_TRIGGER, this.random.nextFloat() * 0.4F + 0.8F, 0.25F));
			}

			this.nextNauseaStrength += 0.0125F;
			if (this.nextNauseaStrength >= 1.0F) {
				this.nextNauseaStrength = 1.0F;
			}

			this.inNetherPortal = false;
		} else if (this.hasStatusEffect(StatusEffects.NAUSEA) && this.getStatusEffect(StatusEffects.NAUSEA).getDuration() > 60) {
			this.nextNauseaStrength += 0.006666667F;
			if (this.nextNauseaStrength > 1.0F) {
				this.nextNauseaStrength = 1.0F;
			}
		} else {
			if (this.nextNauseaStrength > 0.0F) {
				this.nextNauseaStrength -= 0.05F;
			}

			if (this.nextNauseaStrength < 0.0F) {
				this.nextNauseaStrength = 0.0F;
			}
		}

		this.tickNetherPortalCooldown();
	}

	@Override
	public void tickRiding() {
		super.tickRiding();
		this.riding = false;
		if (this.getVehicle() instanceof BoatEntity) {
			BoatEntity boatEntity = (BoatEntity)this.getVehicle();
			boatEntity.setInputs(this.input.pressingLeft, this.input.pressingRight, this.input.pressingForward, this.input.pressingBack);
			this.riding = this.riding | (this.input.pressingLeft || this.input.pressingRight || this.input.pressingForward || this.input.pressingBack);
		}
	}

	public boolean isRiding() {
		return this.riding;
	}

	@Nullable
	@Override
	public StatusEffectInstance removeStatusEffectInternal(@Nullable StatusEffect type) {
		if (type == StatusEffects.NAUSEA) {
			this.lastNauseaStrength = 0.0F;
			this.nextNauseaStrength = 0.0F;
		}

		return super.removeStatusEffectInternal(type);
	}

	@Override
	public void move(MovementType movementType, Vec3d movement) {
		double d = this.getX();
		double e = this.getZ();
		super.move(movementType, movement);
		this.autoJump((float)(this.getX() - d), (float)(this.getZ() - e));
	}

	public boolean isAutoJumpEnabled() {
		return this.autoJumpEnabled;
	}

	protected void autoJump(float dx, float dz) {
		if (this.shouldAutoJump()) {
			Vec3d vec3d = this.getPos();
			Vec3d vec3d2 = vec3d.add((double)dx, 0.0, (double)dz);
			Vec3d vec3d3 = new Vec3d((double)dx, 0.0, (double)dz);
			float f = this.getMovementSpeed();
			float g = (float)vec3d3.lengthSquared();
			if (g <= 0.001F) {
				Vec2f vec2f = this.input.getMovementInput();
				float h = f * vec2f.x;
				float i = f * vec2f.y;
				float j = MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0));
				float k = MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0));
				vec3d3 = new Vec3d((double)(h * k - i * j), vec3d3.y, (double)(i * k + h * j));
				g = (float)vec3d3.lengthSquared();
				if (g <= 0.001F) {
					return;
				}
			}

			float l = MathHelper.fastInverseSqrt(g);
			Vec3d vec3d4 = vec3d3.multiply((double)l);
			Vec3d vec3d5 = this.getRotationVecClient();
			float j = (float)(vec3d5.x * vec3d4.x + vec3d5.z * vec3d4.z);
			if (!(j < -0.15F)) {
				ShapeContext shapeContext = ShapeContext.of(this);
				BlockPos blockPos = new BlockPos(this.getX(), this.getBoundingBox().maxY, this.getZ());
				BlockState blockState = this.world.getBlockState(blockPos);
				if (blockState.getCollisionShape(this.world, blockPos, shapeContext).isEmpty()) {
					blockPos = blockPos.up();
					BlockState blockState2 = this.world.getBlockState(blockPos);
					if (blockState2.getCollisionShape(this.world, blockPos, shapeContext).isEmpty()) {
						float m = 7.0F;
						float n = 1.2F;
						if (this.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
							n += (float)(this.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier() + 1) * 0.75F;
						}

						float o = Math.max(f * 7.0F, 1.0F / l);
						Vec3d vec3d7 = vec3d2.add(vec3d4.multiply((double)o));
						float p = this.getWidth();
						float q = this.getHeight();
						Box box = new Box(vec3d, vec3d7.add(0.0, (double)q, 0.0)).expand((double)p, 0.0, (double)p);
						Vec3d vec3d6 = vec3d.add(0.0, 0.51F, 0.0);
						vec3d7 = vec3d7.add(0.0, 0.51F, 0.0);
						Vec3d vec3d8 = vec3d4.crossProduct(new Vec3d(0.0, 1.0, 0.0));
						Vec3d vec3d9 = vec3d8.multiply((double)(p * 0.5F));
						Vec3d vec3d10 = vec3d6.subtract(vec3d9);
						Vec3d vec3d11 = vec3d7.subtract(vec3d9);
						Vec3d vec3d12 = vec3d6.add(vec3d9);
						Vec3d vec3d13 = vec3d7.add(vec3d9);
						Iterable<VoxelShape> iterable = this.world.getCollisions(this, box);
						Iterator<Box> iterator = StreamSupport.stream(iterable.spliterator(), false).flatMap(shape -> shape.getBoundingBoxes().stream()).iterator();
						float r = Float.MIN_VALUE;

						while (iterator.hasNext()) {
							Box box2 = (Box)iterator.next();
							if (box2.intersects(vec3d10, vec3d11) || box2.intersects(vec3d12, vec3d13)) {
								r = (float)box2.maxY;
								Vec3d vec3d14 = box2.getCenter();
								BlockPos blockPos2 = new BlockPos(vec3d14);

								for (int s = 1; (float)s < n; s++) {
									BlockPos blockPos3 = blockPos2.up(s);
									BlockState blockState3 = this.world.getBlockState(blockPos3);
									VoxelShape voxelShape;
									if (!(voxelShape = blockState3.getCollisionShape(this.world, blockPos3, shapeContext)).isEmpty()) {
										r = (float)voxelShape.getMax(Direction.Axis.Y) + (float)blockPos3.getY();
										if ((double)r - this.getY() > (double)n) {
											return;
										}
									}

									if (s > 1) {
										blockPos = blockPos.up();
										BlockState blockState4 = this.world.getBlockState(blockPos);
										if (!blockState4.getCollisionShape(this.world, blockPos, shapeContext).isEmpty()) {
											return;
										}
									}
								}
								break;
							}
						}

						if (r != Float.MIN_VALUE) {
							float t = (float)((double)r - this.getY());
							if (!(t <= 0.5F) && !(t > n)) {
								this.ticksToNextAutojump = 1;
							}
						}
					}
				}
			}
		}
	}

	@Override
	protected boolean hasCollidedSoftly(Vec3d adjustedMovement) {
		float f = this.getYaw() * (float) (Math.PI / 180.0);
		double d = (double)MathHelper.sin(f);
		double e = (double)MathHelper.cos(f);
		double g = (double)this.sidewaysSpeed * e - (double)this.forwardSpeed * d;
		double h = (double)this.forwardSpeed * e + (double)this.sidewaysSpeed * d;
		double i = MathHelper.square(g) + MathHelper.square(h);
		double j = MathHelper.square(adjustedMovement.x) + MathHelper.square(adjustedMovement.z);
		if (!(i < 1.0E-5F) && !(j < 1.0E-5F)) {
			double k = g * adjustedMovement.x + h * adjustedMovement.z;
			double l = Math.acos(k / Math.sqrt(i * j));
			return l < 0.13962634F;
		} else {
			return false;
		}
	}

	private boolean shouldAutoJump() {
		return this.isAutoJumpEnabled()
			&& this.ticksToNextAutojump <= 0
			&& this.onGround
			&& !this.clipAtLedge()
			&& !this.hasVehicle()
			&& this.hasMovementInput()
			&& (double)this.getJumpVelocityMultiplier() >= 1.0;
	}

	/**
	 * {@return whether the player has movement input}
	 */
	private boolean hasMovementInput() {
		Vec2f vec2f = this.input.getMovementInput();
		return vec2f.x != 0.0F || vec2f.y != 0.0F;
	}

	private boolean isWalking() {
		double d = 0.8;
		return this.isSubmergedInWater() ? this.input.hasForwardMovement() : (double)this.input.movementForward >= 0.8;
	}

	/**
	 * {@return the color multiplier of vision in water} Visibility in
	 * water is reduced when the player just entered water.
	 */
	public float getUnderwaterVisibility() {
		if (!this.isSubmergedIn(FluidTags.WATER)) {
			return 0.0F;
		} else {
			float f = 600.0F;
			float g = 100.0F;
			if ((float)this.underwaterVisibilityTicks >= 600.0F) {
				return 1.0F;
			} else {
				float h = MathHelper.clamp((float)this.underwaterVisibilityTicks / 100.0F, 0.0F, 1.0F);
				float i = (float)this.underwaterVisibilityTicks < 100.0F ? 0.0F : MathHelper.clamp(((float)this.underwaterVisibilityTicks - 100.0F) / 500.0F, 0.0F, 1.0F);
				return h * 0.6F + i * 0.39999998F;
			}
		}
	}

	@Override
	public boolean isSubmergedInWater() {
		return this.isSubmergedInWater;
	}

	@Override
	protected boolean updateWaterSubmersionState() {
		boolean bl = this.isSubmergedInWater;
		boolean bl2 = super.updateWaterSubmersionState();
		if (this.isSpectator()) {
			return this.isSubmergedInWater;
		} else {
			if (!bl && bl2) {
				this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.AMBIENT_UNDERWATER_ENTER, SoundCategory.AMBIENT, 1.0F, 1.0F, false);
				this.client.getSoundManager().play(new AmbientSoundLoops.Underwater(this));
			}

			if (bl && !bl2) {
				this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundCategory.AMBIENT, 1.0F, 1.0F, false);
			}

			return this.isSubmergedInWater;
		}
	}

	@Override
	public Vec3d getLeashPos(float delta) {
		if (this.client.options.getPerspective().isFirstPerson()) {
			float f = MathHelper.lerp(delta * 0.5F, this.getYaw(), this.prevYaw) * (float) (Math.PI / 180.0);
			float g = MathHelper.lerp(delta * 0.5F, this.getPitch(), this.prevPitch) * (float) (Math.PI / 180.0);
			double d = this.getMainArm() == Arm.RIGHT ? -1.0 : 1.0;
			Vec3d vec3d = new Vec3d(0.39 * d, -0.6, 0.3);
			return vec3d.rotateX(-g).rotateY(-f).add(this.getCameraPosVec(delta));
		} else {
			return super.getLeashPos(delta);
		}
	}

	@Override
	public void onPickupSlotClick(ItemStack cursorStack, ItemStack slotStack, ClickType clickType) {
		this.client.getTutorialManager().onPickupSlotClick(cursorStack, slotStack, clickType);
	}
}
