package net.minecraft.client.network;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1104;
import net.minecraft.class_1116;
import net.minecraft.class_1118;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.audio.ElytraSoundInstance;
import net.minecraft.client.audio.MinecartSoundInstance;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.client.gui.CommandBlockMinecartScreen;
import net.minecraft.client.gui.CommandBlockScreen;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.client.gui.ingame.EditBookScreen;
import net.minecraft.client.gui.ingame.EditSignScreen;
import net.minecraft.client.gui.ingame.JigsawBlockScreen;
import net.minecraft.client.gui.ingame.StructureBlockScreen;
import net.minecraft.client.input.Input;
import net.minecraft.client.recipe.book.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.packet.ChatMessageC2SPacket;
import net.minecraft.server.network.packet.ClientCommandC2SPacket;
import net.minecraft.server.network.packet.ClientStatusC2SPacket;
import net.minecraft.server.network.packet.GuiCloseC2SPacket;
import net.minecraft.server.network.packet.HandSwingC2SPacket;
import net.minecraft.server.network.packet.PlayerActionC2SPacket;
import net.minecraft.server.network.packet.PlayerInputC2SPacket;
import net.minecraft.server.network.packet.PlayerMoveServerMessage;
import net.minecraft.server.network.packet.RecipeBookDataC2SPacket;
import net.minecraft.server.network.packet.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.server.network.packet.VehicleMoveC2SPacket;
import net.minecraft.sortme.CommandBlockExecutor;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.StatHandler;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.dimension.DimensionType;

@Environment(EnvType.CLIENT)
public class ClientPlayerEntity extends AbstractClientPlayerEntity {
	public final ClientPlayNetworkHandler networkHandler;
	private final StatHandler stats;
	private final ClientRecipeBook recipeBook;
	private final List<class_1104> field_3933 = Lists.<class_1104>newArrayList();
	private int clientPermissionLevel = 0;
	private double field_3926;
	private double field_3940;
	private double field_3924;
	private float field_3941;
	private float field_3925;
	private boolean field_3920;
	private boolean field_3936;
	private boolean field_3919;
	private int field_3923;
	private boolean field_3918;
	private String serverBrand;
	public Input input;
	protected final MinecraftClient client;
	protected int field_3935;
	public int field_3921;
	public float field_3932;
	public float field_3916;
	public float field_3931;
	public float field_3914;
	private int field_3938;
	private float field_3922;
	public float field_3929;
	public float field_3911;
	private boolean field_3915;
	private Hand activeHand;
	private boolean field_3942;
	private boolean field_3927 = true;
	private int field_3934;
	private boolean field_3939;
	private int field_3917;

	public ClientPlayerEntity(
		MinecraftClient minecraftClient,
		ClientWorld clientWorld,
		ClientPlayNetworkHandler clientPlayNetworkHandler,
		StatHandler statHandler,
		ClientRecipeBook clientRecipeBook
	) {
		super(clientWorld, clientPlayNetworkHandler.getProfile());
		this.networkHandler = clientPlayNetworkHandler;
		this.stats = statHandler;
		this.recipeBook = clientRecipeBook;
		this.client = minecraftClient;
		this.dimension = DimensionType.field_13072;
		this.field_3933.add(new class_1116(this, minecraftClient.getSoundLoader()));
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		return false;
	}

	@Override
	public void heal(float f) {
	}

	@Override
	public boolean startRiding(Entity entity, boolean bl) {
		if (!super.startRiding(entity, bl)) {
			return false;
		} else {
			if (entity instanceof AbstractMinecartEntity) {
				this.client.getSoundLoader().play(new MinecartSoundInstance(this, (AbstractMinecartEntity)entity));
			}

			if (entity instanceof BoatEntity) {
				this.prevYaw = entity.yaw;
				this.yaw = entity.yaw;
				this.setHeadYaw(entity.yaw);
			}

			return true;
		}
	}

	@Override
	public void stopRiding() {
		super.stopRiding();
		this.field_3942 = false;
	}

	@Override
	public float getPitch(float f) {
		return this.pitch;
	}

	@Override
	public float getYaw(float f) {
		return this.hasVehicle() ? super.getYaw(f) : this.yaw;
	}

	@Override
	public void update() {
		if (this.world.isBlockLoaded(new BlockPos(this.x, 0.0, this.z))) {
			super.update();
			if (this.hasVehicle()) {
				this.networkHandler.sendPacket(new PlayerMoveServerMessage.LookOnly(this.yaw, this.pitch, this.onGround));
				this.networkHandler.sendPacket(new PlayerInputC2SPacket(this.movementInputSideways, this.movementInputForward, this.input.jumping, this.input.sneaking));
				Entity entity = this.getTopmostRiddenEntity();
				if (entity != this && entity.method_5787()) {
					this.networkHandler.sendPacket(new VehicleMoveC2SPacket(entity));
				}
			} else {
				this.method_3136();
			}

			for (class_1104 lv : this.field_3933) {
				lv.method_4756();
			}
		}
	}

	private void method_3136() {
		boolean bl = this.isSprinting();
		if (bl != this.field_3919) {
			if (bl) {
				this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.field_12981));
			} else {
				this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.field_12985));
			}

			this.field_3919 = bl;
		}

		boolean bl2 = this.isSneaking();
		if (bl2 != this.field_3936) {
			if (bl2) {
				this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.field_12979));
			} else {
				this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.field_12984));
			}

			this.field_3936 = bl2;
		}

		if (this.method_3134()) {
			BoundingBox boundingBox = this.getBoundingBox();
			double d = this.x - this.field_3926;
			double e = boundingBox.minY - this.field_3940;
			double f = this.z - this.field_3924;
			double g = (double)(this.yaw - this.field_3941);
			double h = (double)(this.pitch - this.field_3925);
			this.field_3923++;
			boolean bl3 = d * d + e * e + f * f > 9.0E-4 || this.field_3923 >= 20;
			boolean bl4 = g != 0.0 || h != 0.0;
			if (this.hasVehicle()) {
				Vec3d vec3d = this.getVelocity();
				this.networkHandler.sendPacket(new PlayerMoveServerMessage.Both(vec3d.x, -999.0, vec3d.z, this.yaw, this.pitch, this.onGround));
				bl3 = false;
			} else if (bl3 && bl4) {
				this.networkHandler.sendPacket(new PlayerMoveServerMessage.Both(this.x, boundingBox.minY, this.z, this.yaw, this.pitch, this.onGround));
			} else if (bl3) {
				this.networkHandler.sendPacket(new PlayerMoveServerMessage.PositionOnly(this.x, boundingBox.minY, this.z, this.onGround));
			} else if (bl4) {
				this.networkHandler.sendPacket(new PlayerMoveServerMessage.LookOnly(this.yaw, this.pitch, this.onGround));
			} else if (this.field_3920 != this.onGround) {
				this.networkHandler.sendPacket(new PlayerMoveServerMessage(this.onGround));
			}

			if (bl3) {
				this.field_3926 = this.x;
				this.field_3940 = boundingBox.minY;
				this.field_3924 = this.z;
				this.field_3923 = 0;
			}

			if (bl4) {
				this.field_3941 = this.yaw;
				this.field_3925 = this.pitch;
			}

			this.field_3920 = this.onGround;
			this.field_3927 = this.client.options.autoJump;
		}
	}

	@Nullable
	@Override
	public ItemEntity dropSelectedItem(boolean bl) {
		PlayerActionC2SPacket.Action action = bl ? PlayerActionC2SPacket.Action.field_12970 : PlayerActionC2SPacket.Action.field_12975;
		this.networkHandler.sendPacket(new PlayerActionC2SPacket(action, BlockPos.ORIGIN, Direction.DOWN));
		this.inventory
			.takeInvStack(this.inventory.selectedSlot, bl && !this.inventory.getMainHandStack().isEmpty() ? this.inventory.getMainHandStack().getAmount() : 1);
		return null;
	}

	public void sendChatMessage(String string) {
		this.networkHandler.sendPacket(new ChatMessageC2SPacket(string));
	}

	@Override
	public void swingHand(Hand hand) {
		super.swingHand(hand);
		this.networkHandler.sendPacket(new HandSwingC2SPacket(hand));
	}

	@Override
	public void requestRespawn() {
		this.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.field_12774));
	}

	@Override
	protected void applyDamage(DamageSource damageSource, float f) {
		if (!this.isInvulnerableTo(damageSource)) {
			this.setHealth(this.getHealth() - f);
		}
	}

	@Override
	public void closeGui() {
		this.networkHandler.sendPacket(new GuiCloseC2SPacket(this.container.syncId));
		this.method_3137();
	}

	public void method_3137() {
		this.inventory.setCursorStack(ItemStack.EMPTY);
		super.closeGui();
		this.client.openScreen(null);
	}

	public void updateHealth(float f) {
		if (this.field_3918) {
			float g = this.getHealth() - f;
			if (g <= 0.0F) {
				this.setHealth(f);
				if (g < 0.0F) {
					this.field_6008 = 10;
				}
			} else {
				this.field_6253 = g;
				this.setHealth(this.getHealth());
				this.field_6008 = 20;
				this.applyDamage(DamageSource.GENERIC, g);
				this.field_6254 = 10;
				this.hurtTime = this.field_6254;
			}
		} else {
			this.setHealth(f);
			this.field_3918 = true;
		}
	}

	@Override
	public void method_7355() {
		this.networkHandler.sendPacket(new UpdatePlayerAbilitiesC2SPacket(this.abilities));
	}

	@Override
	public boolean method_7340() {
		return true;
	}

	protected void startRidingJump() {
		this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.field_12987, MathHelper.floor(this.method_3151() * 100.0F)));
	}

	public void openRidingInventory() {
		this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.field_12988));
	}

	public void setServerBrand(String string) {
		this.serverBrand = string;
	}

	public String getServerBrand() {
		return this.serverBrand;
	}

	public StatHandler getStats() {
		return this.stats;
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

	public void setClientPermissionLevel(int i) {
		this.clientPermissionLevel = i;
	}

	@Override
	public void addChatMessage(TextComponent textComponent, boolean bl) {
		if (bl) {
			this.client.inGameHud.setOverlayMessage(textComponent, false);
		} else {
			this.client.inGameHud.getChatHud().addMessage(textComponent);
		}
	}

	@Override
	protected void method_5632(double d, double e, double f) {
		BlockPos blockPos = new BlockPos(d, e, f);
		double g = d - (double)blockPos.getX();
		double h = f - (double)blockPos.getZ();
		if (this.method_3150(blockPos)) {
			int i = -1;
			double j = 9999.0;
			if (this.method_7352(blockPos.west()) && g < j) {
				j = g;
				i = 0;
			}

			if (this.method_7352(blockPos.east()) && 1.0 - g < j) {
				j = 1.0 - g;
				i = 1;
			}

			if (this.method_7352(blockPos.north()) && h < j) {
				j = h;
				i = 4;
			}

			if (this.method_7352(blockPos.south()) && 1.0 - h < j) {
				j = 1.0 - h;
				i = 5;
			}

			Vec3d vec3d = this.getVelocity();
			double k = vec3d.x;
			double l = vec3d.y;
			double m = vec3d.z;
			float n = 0.1F;
			if (i == 0) {
				k = -0.1F;
			}

			if (i == 1) {
				k = 0.1F;
			}

			if (i == 4) {
				m = -0.1F;
			}

			if (i == 5) {
				m = 0.1F;
			}

			this.setVelocity(k, l, m);
		}
	}

	private boolean method_3150(BlockPos blockPos) {
		return this.isSwimming() ? !this.method_7326(blockPos) : !this.method_7352(blockPos);
	}

	@Override
	public void setSprinting(boolean bl) {
		super.setSprinting(bl);
		this.field_3921 = 0;
	}

	public void method_3145(float f, int i, int j) {
		this.experienceBarProgress = f;
		this.experienceLevel = i;
		this.experience = j;
	}

	@Override
	public void appendCommandFeedback(TextComponent textComponent) {
		this.client.inGameHud.getChatHud().addMessage(textComponent);
	}

	@Override
	public void method_5711(byte b) {
		if (b >= 24 && b <= 28) {
			this.setClientPermissionLevel(b - 24);
		} else {
			super.method_5711(b);
		}
	}

	@Override
	public void playSound(SoundEvent soundEvent, float f, float g) {
		this.world.playSound(this.x, this.y, this.z, soundEvent, this.getSoundCategory(), f, g, false);
	}

	@Override
	public void playSound(SoundEvent soundEvent, SoundCategory soundCategory, float f, float g) {
		this.world.playSound(this.x, this.y, this.z, soundEvent, soundCategory, f, g, false);
	}

	@Override
	public boolean method_6034() {
		return true;
	}

	@Override
	public void setCurrentHand(Hand hand) {
		ItemStack itemStack = this.getStackInHand(hand);
		if (!itemStack.isEmpty() && !this.isUsingItem()) {
			super.setCurrentHand(hand);
			this.field_3915 = true;
			this.activeHand = hand;
		}
	}

	@Override
	public boolean isUsingItem() {
		return this.field_3915;
	}

	@Override
	public void method_6021() {
		super.method_6021();
		this.field_3915 = false;
	}

	@Override
	public Hand getActiveHand() {
		return this.activeHand;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> trackedData) {
		super.onTrackedDataSet(trackedData);
		if (LIVING_FLAGS.equals(trackedData)) {
			boolean bl = (this.dataTracker.get(LIVING_FLAGS) & 1) > 0;
			Hand hand = (this.dataTracker.get(LIVING_FLAGS) & 2) > 0 ? Hand.OFF : Hand.MAIN;
			if (bl && !this.field_3915) {
				this.setCurrentHand(hand);
			} else if (!bl && this.field_3915) {
				this.method_6021();
			}
		}

		if (ENTITY_FLAGS.equals(trackedData) && this.isFallFlying() && !this.field_3939) {
			this.client.getSoundLoader().play(new ElytraSoundInstance(this));
		}
	}

	public boolean hasJumpingMount() {
		Entity entity = this.getRiddenEntity();
		return this.hasVehicle() && entity instanceof JumpingMount && ((JumpingMount)entity).canJump();
	}

	public float method_3151() {
		return this.field_3922;
	}

	@Override
	public void openEditSignScreen(SignBlockEntity signBlockEntity) {
		this.client.openScreen(new EditSignScreen(signBlockEntity));
	}

	@Override
	public void openCommandBlockMinecartScreen(CommandBlockExecutor commandBlockExecutor) {
		this.client.openScreen(new CommandBlockMinecartScreen(commandBlockExecutor));
	}

	@Override
	public void openCommandBlockScreen(CommandBlockBlockEntity commandBlockBlockEntity) {
		this.client.openScreen(new CommandBlockScreen(commandBlockBlockEntity));
	}

	@Override
	public void openStructureBlockScreen(StructureBlockBlockEntity structureBlockBlockEntity) {
		this.client.openScreen(new StructureBlockScreen(structureBlockBlockEntity));
	}

	@Override
	public void openJigsawScreen(JigsawBlockEntity jigsawBlockEntity) {
		this.client.openScreen(new JigsawBlockScreen(jigsawBlockEntity));
	}

	@Override
	public void openEditBookScreen(ItemStack itemStack, Hand hand) {
		Item item = itemStack.getItem();
		if (item == Items.field_8674) {
			this.client.openScreen(new EditBookScreen(this, itemStack, hand));
		}
	}

	@Override
	public void addCritParticles(Entity entity) {
		this.client.particleManager.addEmitter(entity, ParticleTypes.field_11205);
	}

	@Override
	public void addEnchantedHitParticles(Entity entity) {
		this.client.particleManager.addEmitter(entity, ParticleTypes.field_11208);
	}

	@Override
	public boolean isSneaking() {
		return this.input != null && this.input.sneaking;
	}

	@Override
	public void method_6023() {
		super.method_6023();
		if (this.method_3134()) {
			this.movementInputSideways = this.input.movementSideways;
			this.movementInputForward = this.input.movementForward;
			this.field_6282 = this.input.jumping;
			this.field_3931 = this.field_3932;
			this.field_3914 = this.field_3916;
			this.field_3916 = (float)((double)this.field_3916 + (double)(this.pitch - this.field_3916) * 0.5);
			this.field_3932 = (float)((double)this.field_3932 + (double)(this.yaw - this.field_3932) * 0.5);
		}
	}

	protected boolean method_3134() {
		return this.client.getCameraEntity() == this;
	}

	@Override
	public void updateMovement() {
		this.field_3921++;
		if (this.field_3935 > 0) {
			this.field_3935--;
		}

		this.method_18654();
		boolean bl = this.input.jumping;
		boolean bl2 = this.input.sneaking;
		float f = 0.8F;
		boolean bl3 = this.input.movementForward >= 0.8F;
		this.input.tick();
		this.client.getTutorialManager().method_4909(this.input);
		if (this.isUsingItem() && !this.hasVehicle()) {
			this.input.movementSideways *= 0.2F;
			this.input.movementForward *= 0.2F;
			this.field_3935 = 0;
		}

		boolean bl4 = false;
		if (this.field_3934 > 0) {
			this.field_3934--;
			bl4 = true;
			this.input.jumping = true;
		}

		if (!this.noClip) {
			BoundingBox boundingBox = this.getBoundingBox();
			this.method_5632(this.x - (double)this.getWidth() * 0.35, boundingBox.minY + 0.5, this.z + (double)this.getWidth() * 0.35);
			this.method_5632(this.x - (double)this.getWidth() * 0.35, boundingBox.minY + 0.5, this.z - (double)this.getWidth() * 0.35);
			this.method_5632(this.x + (double)this.getWidth() * 0.35, boundingBox.minY + 0.5, this.z - (double)this.getWidth() * 0.35);
			this.method_5632(this.x + (double)this.getWidth() * 0.35, boundingBox.minY + 0.5, this.z + (double)this.getWidth() * 0.35);
		}

		boolean bl5 = (float)this.getHungerManager().getFoodLevel() > 6.0F || this.abilities.allowFlying;
		if ((this.onGround || this.isInWater())
			&& !bl2
			&& !bl3
			&& this.input.movementForward >= 0.8F
			&& !this.isSprinting()
			&& bl5
			&& !this.isUsingItem()
			&& !this.hasPotionEffect(StatusEffects.field_5919)) {
			if (this.field_3935 <= 0 && !this.client.options.keySprint.isPressed()) {
				this.field_3935 = 7;
			} else {
				this.setSprinting(true);
			}
		}

		if (!this.isSprinting()
			&& (!this.isInsideWater() || this.isInWater())
			&& this.input.movementForward >= 0.8F
			&& bl5
			&& !this.isUsingItem()
			&& !this.hasPotionEffect(StatusEffects.field_5919)
			&& this.client.options.keySprint.isPressed()) {
			this.setSprinting(true);
		}

		if (this.isSprinting()) {
			boolean bl6 = this.input.movementForward < 0.8F || !bl5;
			boolean bl7 = bl6 || this.horizontalCollision || this.isInsideWater() && !this.isInWater();
			if (this.isSwimming()) {
				if (!this.onGround && !this.input.sneaking && bl6 || !this.isInsideWater()) {
					this.setSprinting(false);
				}
			} else if (bl7) {
				this.setSprinting(false);
			}
		}

		if (this.abilities.allowFlying) {
			if (this.client.interactionManager.isFlyingLocked()) {
				if (!this.abilities.flying) {
					this.abilities.flying = true;
					this.method_7355();
				}
			} else if (!bl && this.input.jumping && !bl4) {
				if (this.field_7489 == 0) {
					this.field_7489 = 7;
				} else if (!this.isSwimming()) {
					this.abilities.flying = !this.abilities.flying;
					this.method_7355();
					this.field_7489 = 0;
				}
			}
		}

		if (this.input.jumping && !bl && !this.onGround && this.getVelocity().y < 0.0 && !this.isFallFlying() && !this.abilities.flying) {
			ItemStack itemStack = this.getEquippedStack(EquipmentSlot.CHEST);
			if (itemStack.getItem() == Items.field_8833 && ElytraItem.isUsable(itemStack)) {
				this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.field_12982));
			}
		}

		this.field_3939 = this.isFallFlying();
		if (this.isInsideWater() && this.input.sneaking) {
			this.method_6093();
		}

		if (this.isInFluid(FluidTags.field_15517)) {
			int i = this.isSpectator() ? 10 : 1;
			this.field_3917 = MathHelper.clamp(this.field_3917 + i, 0, 600);
		} else if (this.field_3917 > 0) {
			this.isInFluid(FluidTags.field_15517);
			this.field_3917 = MathHelper.clamp(this.field_3917 - 10, 0, 600);
		}

		if (this.abilities.flying && this.method_3134()) {
			int i = 0;
			if (this.input.sneaking) {
				this.input.movementSideways = (float)((double)this.input.movementSideways / 0.3);
				this.input.movementForward = (float)((double)this.input.movementForward / 0.3);
				i--;
			}

			if (this.input.jumping) {
				i++;
			}

			if (i != 0) {
				this.setVelocity(this.getVelocity().add(0.0, (double)((float)i * this.abilities.getFlySpeed() * 3.0F), 0.0));
			}
		}

		if (this.hasJumpingMount()) {
			JumpingMount jumpingMount = (JumpingMount)this.getRiddenEntity();
			if (this.field_3938 < 0) {
				this.field_3938++;
				if (this.field_3938 == 0) {
					this.field_3922 = 0.0F;
				}
			}

			if (bl && !this.input.jumping) {
				this.field_3938 = -10;
				jumpingMount.setJumpStrength(MathHelper.floor(this.method_3151() * 100.0F));
				this.startRidingJump();
			} else if (!bl && this.input.jumping) {
				this.field_3938 = 0;
				this.field_3922 = 0.0F;
			} else if (bl) {
				this.field_3938++;
				if (this.field_3938 < 10) {
					this.field_3922 = (float)this.field_3938 * 0.1F;
				} else {
					this.field_3922 = 0.8F + 2.0F / (float)(this.field_3938 - 9) * 0.1F;
				}
			}
		} else {
			this.field_3922 = 0.0F;
		}

		super.updateMovement();
		if (this.onGround && this.abilities.flying && !this.client.interactionManager.isFlyingLocked()) {
			this.abilities.flying = false;
			this.method_7355();
		}
	}

	private void method_18654() {
		this.field_3911 = this.field_3929;
		if (this.inPortal) {
			if (this.client.currentScreen != null && !this.client.currentScreen.isPauseScreen()) {
				if (this.client.currentScreen instanceof ContainerScreen) {
					this.closeGui();
				}

				this.client.openScreen(null);
			}

			if (this.field_3929 == 0.0F) {
				this.client.getSoundLoader().play(PositionedSoundInstance.master(SoundEvents.field_14669, this.random.nextFloat() * 0.4F + 0.8F));
			}

			this.field_3929 += 0.0125F;
			if (this.field_3929 >= 1.0F) {
				this.field_3929 = 1.0F;
			}

			this.inPortal = false;
		} else if (this.hasPotionEffect(StatusEffects.field_5916) && this.getPotionEffect(StatusEffects.field_5916).getDuration() > 60) {
			this.field_3929 += 0.006666667F;
			if (this.field_3929 > 1.0F) {
				this.field_3929 = 1.0F;
			}
		} else {
			if (this.field_3929 > 0.0F) {
				this.field_3929 -= 0.05F;
			}

			if (this.field_3929 < 0.0F) {
				this.field_3929 = 0.0F;
			}
		}

		this.updatePortalCooldown();
	}

	@Override
	public void updateRiding() {
		super.updateRiding();
		this.field_3942 = false;
		if (this.getRiddenEntity() instanceof BoatEntity) {
			BoatEntity boatEntity = (BoatEntity)this.getRiddenEntity();
			boatEntity.method_7535(this.input.pressingLeft, this.input.pressingRight, this.input.pressingForward, this.input.pressingBack);
			this.field_3942 = this.field_3942 | (this.input.pressingLeft || this.input.pressingRight || this.input.pressingForward || this.input.pressingBack);
		}
	}

	public boolean method_3144() {
		return this.field_3942;
	}

	@Nullable
	@Override
	public StatusEffectInstance removePotionEffect(@Nullable StatusEffect statusEffect) {
		if (statusEffect == StatusEffects.field_5916) {
			this.field_3911 = 0.0F;
			this.field_3929 = 0.0F;
		}

		return super.removePotionEffect(statusEffect);
	}

	@Override
	public void move(MovementType movementType, Vec3d vec3d) {
		double d = this.x;
		double e = this.z;
		super.move(movementType, vec3d);
		this.method_3148((float)(this.x - d), (float)(this.z - e));
	}

	public boolean method_3149() {
		return this.field_3927;
	}

	protected void method_3148(float f, float g) {
		if (this.method_3149()) {
			if (this.field_3934 <= 0 && this.onGround && !this.isSneaking() && !this.hasVehicle()) {
				Vec2f vec2f = this.input.getMovementInput();
				if (vec2f.x != 0.0F || vec2f.y != 0.0F) {
					Vec3d vec3d = new Vec3d(this.x, this.getBoundingBox().minY, this.z);
					Vec3d vec3d2 = new Vec3d(this.x + (double)f, this.getBoundingBox().minY, this.z + (double)g);
					Vec3d vec3d3 = new Vec3d((double)f, 0.0, (double)g);
					float h = this.getMovementSpeed();
					float i = (float)vec3d3.lengthSquared();
					if (i <= 0.001F) {
						float j = h * vec2f.x;
						float k = h * vec2f.y;
						float l = MathHelper.sin(this.yaw * (float) (Math.PI / 180.0));
						float m = MathHelper.cos(this.yaw * (float) (Math.PI / 180.0));
						vec3d3 = new Vec3d((double)(j * m - k * l), vec3d3.y, (double)(k * m + j * l));
						i = (float)vec3d3.lengthSquared();
						if (i <= 0.001F) {
							return;
						}
					}

					float j = (float)MathHelper.fastInverseSqrt((double)i);
					Vec3d vec3d4 = vec3d3.multiply((double)j);
					Vec3d vec3d5 = this.getRotationVecClient();
					float m = (float)(vec3d5.x * vec3d4.x + vec3d5.z * vec3d4.z);
					if (!(m < -0.15F)) {
						VerticalEntityPosition verticalEntityPosition = VerticalEntityPosition.fromEntity(this);
						BlockPos blockPos = new BlockPos(this.x, this.getBoundingBox().maxY, this.z);
						BlockState blockState = this.world.getBlockState(blockPos);
						if (blockState.getCollisionShape(this.world, blockPos, verticalEntityPosition).isEmpty()) {
							blockPos = blockPos.up();
							BlockState blockState2 = this.world.getBlockState(blockPos);
							if (blockState2.getCollisionShape(this.world, blockPos, verticalEntityPosition).isEmpty()) {
								float n = 7.0F;
								float o = 1.2F;
								if (this.hasPotionEffect(StatusEffects.field_5913)) {
									o += (float)(this.getPotionEffect(StatusEffects.field_5913).getAmplifier() + 1) * 0.75F;
								}

								float p = Math.max(h * 7.0F, 1.0F / j);
								Vec3d vec3d7 = vec3d2.add(vec3d4.multiply((double)p));
								float q = this.getWidth();
								float r = this.getHeight();
								BoundingBox boundingBox = new BoundingBox(vec3d, vec3d7.add(0.0, (double)r, 0.0)).expand((double)q, 0.0, (double)q);
								Vec3d vec3d6 = vec3d.add(0.0, 0.51F, 0.0);
								vec3d7 = vec3d7.add(0.0, 0.51F, 0.0);
								Vec3d vec3d8 = vec3d4.crossProduct(new Vec3d(0.0, 1.0, 0.0));
								Vec3d vec3d9 = vec3d8.multiply((double)(q * 0.5F));
								Vec3d vec3d10 = vec3d6.subtract(vec3d9);
								Vec3d vec3d11 = vec3d7.subtract(vec3d9);
								Vec3d vec3d12 = vec3d6.add(vec3d9);
								Vec3d vec3d13 = vec3d7.add(vec3d9);
								Iterator<BoundingBox> iterator = this.world
									.getCollidingBoundingBoxesForEntity(this, boundingBox, Collections.emptySet())
									.flatMap(voxelShapex -> voxelShapex.getBoundingBoxList().stream())
									.iterator();
								float s = Float.MIN_VALUE;

								while (iterator.hasNext()) {
									BoundingBox boundingBox2 = (BoundingBox)iterator.next();
									if (boundingBox2.intersects(vec3d10, vec3d11) || boundingBox2.intersects(vec3d12, vec3d13)) {
										s = (float)boundingBox2.maxY;
										Vec3d vec3d14 = boundingBox2.getCenter();
										BlockPos blockPos2 = new BlockPos(vec3d14);

										for (int t = 1; (float)t < o; t++) {
											BlockPos blockPos3 = blockPos2.up(t);
											BlockState blockState3 = this.world.getBlockState(blockPos3);
											VoxelShape voxelShape;
											if (!(voxelShape = blockState3.getCollisionShape(this.world, blockPos3, verticalEntityPosition)).isEmpty()) {
												s = (float)voxelShape.getMaximum(Direction.Axis.Y) + (float)blockPos3.getY();
												if ((double)s - this.getBoundingBox().minY > (double)o) {
													return;
												}
											}

											if (t > 1) {
												blockPos = blockPos.up();
												BlockState blockState4 = this.world.getBlockState(blockPos);
												if (!blockState4.getCollisionShape(this.world, blockPos, verticalEntityPosition).isEmpty()) {
													return;
												}
											}
										}
										break;
									}
								}

								if (s != Float.MIN_VALUE) {
									float u = (float)((double)s - this.getBoundingBox().minY);
									if (!(u <= 0.5F) && !(u > o)) {
										this.field_3934 = 1;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public float method_3140() {
		if (!this.isInFluid(FluidTags.field_15517)) {
			return 0.0F;
		} else {
			float f = 600.0F;
			float g = 100.0F;
			if ((float)this.field_3917 >= 600.0F) {
				return 1.0F;
			} else {
				float h = MathHelper.clamp((float)this.field_3917 / 100.0F, 0.0F, 1.0F);
				float i = (float)this.field_3917 < 100.0F ? 0.0F : MathHelper.clamp(((float)this.field_3917 - 100.0F) / 500.0F, 0.0F, 1.0F);
				return h * 0.6F + i * 0.39999998F;
			}
		}
	}

	@Override
	public boolean isInWater() {
		return this.isInWater;
	}

	@Override
	protected boolean updateInWater() {
		boolean bl = this.isInWater;
		boolean bl2 = super.updateInWater();
		if (this.isSpectator()) {
			return this.isInWater;
		} else {
			if (!bl && bl2) {
				this.world.playSound(this.x, this.y, this.z, SoundEvents.field_14756, SoundCategory.field_15256, 1.0F, 1.0F, false);
				this.client.getSoundLoader().play(new class_1118.class_1120(this));
			}

			if (bl && !bl2) {
				this.world.playSound(this.x, this.y, this.z, SoundEvents.field_14828, SoundCategory.field_15256, 1.0F, 1.0F, false);
			}

			return this.isInWater;
		}
	}
}
