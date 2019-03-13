package net.minecraft.server.network;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import it.unimi.dsi.fastutil.ints.Int2ShortMap;
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.class_4210;
import net.minecraft.class_4211;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.network.packet.BlockUpdateS2CPacket;
import net.minecraft.client.network.packet.ChatMessageS2CPacket;
import net.minecraft.client.network.packet.CommandSuggestionsS2CPacket;
import net.minecraft.client.network.packet.ConfirmGuiActionS2CPacket;
import net.minecraft.client.network.packet.DisconnectS2CPacket;
import net.minecraft.client.network.packet.GuiSlotUpdateS2CPacket;
import net.minecraft.client.network.packet.HeldItemChangeS2CPacket;
import net.minecraft.client.network.packet.KeepAliveS2CPacket;
import net.minecraft.client.network.packet.PlayerPositionLookS2CPacket;
import net.minecraft.client.network.packet.TagQueryResponseS2CPacket;
import net.minecraft.client.network.packet.VehicleMoveS2CPacket;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.container.AnvilContainer;
import net.minecraft.container.BeaconContainer;
import net.minecraft.container.Container;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.MerchantContainer;
import net.minecraft.container.Slot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WritableBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.ButtonClickServerPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.packet.AdvancementTabC2SPacket;
import net.minecraft.server.network.packet.BoatPaddleStateC2SPacket;
import net.minecraft.server.network.packet.BookUpdateC2SPacket;
import net.minecraft.server.network.packet.ChatMessageC2SPacket;
import net.minecraft.server.network.packet.ClickWindowC2SPacket;
import net.minecraft.server.network.packet.ClientCommandC2SPacket;
import net.minecraft.server.network.packet.ClientSettingsC2SPacket;
import net.minecraft.server.network.packet.ClientStatusC2SPacket;
import net.minecraft.server.network.packet.CraftRequestC2SPacket;
import net.minecraft.server.network.packet.CreativeInventoryActionC2SPacket;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.server.network.packet.GuiActionConfirmC2SPacket;
import net.minecraft.server.network.packet.GuiCloseC2SPacket;
import net.minecraft.server.network.packet.HandSwingC2SPacket;
import net.minecraft.server.network.packet.KeepAliveC2SPacket;
import net.minecraft.server.network.packet.PickFromInventoryC2SPacket;
import net.minecraft.server.network.packet.PlayerActionC2SPacket;
import net.minecraft.server.network.packet.PlayerInputC2SPacket;
import net.minecraft.server.network.packet.PlayerInteractBlockC2SPacket;
import net.minecraft.server.network.packet.PlayerInteractEntityC2SPacket;
import net.minecraft.server.network.packet.PlayerInteractItemC2SPacket;
import net.minecraft.server.network.packet.PlayerMoveServerMessage;
import net.minecraft.server.network.packet.QueryBlockNbtC2SPacket;
import net.minecraft.server.network.packet.QueryEntityNbtC2SPacket;
import net.minecraft.server.network.packet.RecipeBookDataC2SPacket;
import net.minecraft.server.network.packet.RenameItemC2SPacket;
import net.minecraft.server.network.packet.RequestCommandCompletionsC2SPacket;
import net.minecraft.server.network.packet.ResourcePackStatusC2SPacket;
import net.minecraft.server.network.packet.SelectVillagerTradeC2SPacket;
import net.minecraft.server.network.packet.SpectatorTeleportC2SPacket;
import net.minecraft.server.network.packet.TeleportConfirmC2SPacket;
import net.minecraft.server.network.packet.UpdateBeaconC2SPacket;
import net.minecraft.server.network.packet.UpdateCommandBlockC2SPacket;
import net.minecraft.server.network.packet.UpdateCommandBlockMinecartC2SPacket;
import net.minecraft.server.network.packet.UpdateJigsawC2SPacket;
import net.minecraft.server.network.packet.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.server.network.packet.UpdateSelectedSlotC2SPacket;
import net.minecraft.server.network.packet.UpdateSignC2SPacket;
import net.minecraft.server.network.packet.UpdateStructureBlockC2SPacket;
import net.minecraft.server.network.packet.VehicleMoveC2SPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sortme.ChatMessageType;
import net.minecraft.sortme.CommandBlockExecutor;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.dimension.DimensionType;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayNetworkHandler implements ServerPlayPacketListener {
	private static final Logger LOGGER = LogManager.getLogger();
	public final ClientConnection client;
	private final MinecraftServer server;
	public ServerPlayerEntity player;
	private int ticks;
	private long lastKeepAliveTime;
	private boolean waitingForKeepAlive;
	private long keepAliveId;
	private int messageCooldown;
	private int creativeItemDropThreshold;
	private final Int2ShortMap transactions = new Int2ShortOpenHashMap();
	private double lastTickX;
	private double lastTickY;
	private double lastTickZ;
	private double updatedX;
	private double updatedY;
	private double updatedZ;
	private Entity topmostRiddenEntity;
	private double lastTickRiddenX;
	private double lastTickRiddenY;
	private double lastTickRiddenZ;
	private double updatedRiddenX;
	private double updatedRiddenY;
	private double updatedRiddenZ;
	private Vec3d requestedTeleportPos;
	private int requestedTeleportId;
	private int teleportRequestTick;
	private boolean floating;
	private int floatingTicks;
	private boolean ridingEntity;
	private int vehicleFloatingTicks;
	private int movePacketsCount;
	private int lastTickMovePacketsCount;

	public ServerPlayNetworkHandler(MinecraftServer minecraftServer, ClientConnection clientConnection, ServerPlayerEntity serverPlayerEntity) {
		this.server = minecraftServer;
		this.client = clientConnection;
		clientConnection.method_10763(this);
		this.player = serverPlayerEntity;
		serverPlayerEntity.field_13987 = this;
	}

	public void method_18784() {
		this.syncWithPlayerPosition();
		this.player.method_14226();
		this.player.setPositionAnglesAndUpdate(this.lastTickX, this.lastTickY, this.lastTickZ, this.player.yaw, this.player.pitch);
		this.ticks++;
		this.lastTickMovePacketsCount = this.movePacketsCount;
		if (this.floating) {
			if (++this.floatingTicks > 80) {
				LOGGER.warn("{} was kicked for floating too long!", this.player.method_5477().getString());
				this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.flying"));
				return;
			}
		} else {
			this.floating = false;
			this.floatingTicks = 0;
		}

		this.topmostRiddenEntity = this.player.getTopmostRiddenEntity();
		if (this.topmostRiddenEntity != this.player && this.topmostRiddenEntity.getPrimaryPassenger() == this.player) {
			this.lastTickRiddenX = this.topmostRiddenEntity.x;
			this.lastTickRiddenY = this.topmostRiddenEntity.y;
			this.lastTickRiddenZ = this.topmostRiddenEntity.z;
			this.updatedRiddenX = this.topmostRiddenEntity.x;
			this.updatedRiddenY = this.topmostRiddenEntity.y;
			this.updatedRiddenZ = this.topmostRiddenEntity.z;
			if (this.ridingEntity && this.player.getTopmostRiddenEntity().getPrimaryPassenger() == this.player) {
				if (++this.vehicleFloatingTicks > 80) {
					LOGGER.warn("{} was kicked for floating a vehicle too long!", this.player.method_5477().getString());
					this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.flying"));
					return;
				}
			} else {
				this.ridingEntity = false;
				this.vehicleFloatingTicks = 0;
			}
		} else {
			this.topmostRiddenEntity = null;
			this.ridingEntity = false;
			this.vehicleFloatingTicks = 0;
		}

		this.server.getProfiler().push("keepAlive");
		long l = SystemUtil.getMeasuringTimeMs();
		if (l - this.lastKeepAliveTime >= 15000L) {
			if (this.waitingForKeepAlive) {
				this.disconnect(new TranslatableTextComponent("disconnect.timeout"));
			} else {
				this.waitingForKeepAlive = true;
				this.lastKeepAliveTime = l;
				this.keepAliveId = l;
				this.sendPacket(new KeepAliveS2CPacket(this.keepAliveId));
			}
		}

		this.server.getProfiler().pop();
		if (this.messageCooldown > 0) {
			this.messageCooldown--;
		}

		if (this.creativeItemDropThreshold > 0) {
			this.creativeItemDropThreshold--;
		}

		if (this.player.getLastActionTime() > 0L
			&& this.server.getPlayerIdleTimeout() > 0
			&& SystemUtil.getMeasuringTimeMs() - this.player.getLastActionTime() > (long)(this.server.getPlayerIdleTimeout() * 1000 * 60)) {
			this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.idling"));
		}
	}

	public void syncWithPlayerPosition() {
		this.lastTickX = this.player.x;
		this.lastTickY = this.player.y;
		this.lastTickZ = this.player.z;
		this.updatedX = this.player.x;
		this.updatedY = this.player.y;
		this.updatedZ = this.player.z;
	}

	public ClientConnection getConnection() {
		return this.client;
	}

	private boolean method_19507() {
		return this.server.method_19466(this.player.getGameProfile());
	}

	public void disconnect(TextComponent textComponent) {
		this.client.method_10752(new DisconnectS2CPacket(textComponent), future -> this.client.method_10747(textComponent));
		this.client.disableAutoRead();
		this.server.executeFuture(this.client::handleDisconnection).join();
	}

	@Override
	public void method_12067(PlayerInputC2SPacket playerInputC2SPacket) {
		NetworkThreadUtils.method_11073(playerInputC2SPacket, this, this.player.getServerWorld());
		this.player
			.method_14218(playerInputC2SPacket.getSideways(), playerInputC2SPacket.getForward(), playerInputC2SPacket.isJumping(), playerInputC2SPacket.isSneaking());
	}

	private static boolean validatePlayerMove(PlayerMoveServerMessage playerMoveServerMessage) {
		return Doubles.isFinite(playerMoveServerMessage.getX(0.0))
				&& Doubles.isFinite(playerMoveServerMessage.getY(0.0))
				&& Doubles.isFinite(playerMoveServerMessage.getZ(0.0))
				&& Floats.isFinite(playerMoveServerMessage.getPitch(0.0F))
				&& Floats.isFinite(playerMoveServerMessage.getYaw(0.0F))
			? Math.abs(playerMoveServerMessage.getX(0.0)) > 3.0E7
				|| Math.abs(playerMoveServerMessage.getY(0.0)) > 3.0E7
				|| Math.abs(playerMoveServerMessage.getZ(0.0)) > 3.0E7
			: true;
	}

	private static boolean validateVehicleMove(VehicleMoveC2SPacket vehicleMoveC2SPacket) {
		return !Doubles.isFinite(vehicleMoveC2SPacket.getX())
			|| !Doubles.isFinite(vehicleMoveC2SPacket.getY())
			|| !Doubles.isFinite(vehicleMoveC2SPacket.getZ())
			|| !Floats.isFinite(vehicleMoveC2SPacket.getPitch())
			|| !Floats.isFinite(vehicleMoveC2SPacket.getYaw());
	}

	@Override
	public void method_12078(VehicleMoveC2SPacket vehicleMoveC2SPacket) {
		NetworkThreadUtils.method_11073(vehicleMoveC2SPacket, this, this.player.getServerWorld());
		if (validateVehicleMove(vehicleMoveC2SPacket)) {
			this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.invalid_vehicle_movement"));
		} else {
			Entity entity = this.player.getTopmostRiddenEntity();
			if (entity != this.player && entity.getPrimaryPassenger() == this.player && entity == this.topmostRiddenEntity) {
				ServerWorld serverWorld = this.player.getServerWorld();
				double d = entity.x;
				double e = entity.y;
				double f = entity.z;
				double g = vehicleMoveC2SPacket.getX();
				double h = vehicleMoveC2SPacket.getY();
				double i = vehicleMoveC2SPacket.getZ();
				float j = vehicleMoveC2SPacket.getYaw();
				float k = vehicleMoveC2SPacket.getPitch();
				double l = g - this.lastTickRiddenX;
				double m = h - this.lastTickRiddenY;
				double n = i - this.lastTickRiddenZ;
				double o = entity.method_18798().lengthSquared();
				double p = l * l + m * m + n * n;
				if (p - o > 100.0 && !this.method_19507()) {
					LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", entity.method_5477().getString(), this.player.method_5477().getString(), l, m, n);
					this.client.method_10743(new VehicleMoveS2CPacket(entity));
					return;
				}

				boolean bl = serverWorld.method_8587(entity, entity.method_5829().contract(0.0625));
				l = g - this.updatedRiddenX;
				m = h - this.updatedRiddenY - 1.0E-6;
				n = i - this.updatedRiddenZ;
				entity.method_5784(MovementType.field_6305, new Vec3d(l, m, n));
				l = g - entity.x;
				m = h - entity.y;
				if (m > -0.5 || m < 0.5) {
					m = 0.0;
				}

				n = i - entity.z;
				p = l * l + m * m + n * n;
				boolean bl2 = false;
				if (p > 0.0625) {
					bl2 = true;
					LOGGER.warn("{} moved wrongly!", entity.method_5477().getString());
				}

				entity.setPositionAnglesAndUpdate(g, h, i, j, k);
				boolean bl3 = serverWorld.method_8587(entity, entity.method_5829().contract(0.0625));
				if (bl && (bl2 || !bl3)) {
					entity.setPositionAnglesAndUpdate(d, e, f, j, k);
					this.client.method_10743(new VehicleMoveS2CPacket(entity));
					return;
				}

				this.player.getServerWorld().method_14178().method_14096(this.player);
				this.player.method_7282(this.player.x - d, this.player.y - e, this.player.z - f);
				this.ridingEntity = m >= -0.03125
					&& !this.server.isFlightEnabled()
					&& !serverWorld.method_8534(entity.method_5829().expand(0.0625).stretch(0.0, -0.55, 0.0));
				this.updatedRiddenX = entity.x;
				this.updatedRiddenY = entity.y;
				this.updatedRiddenZ = entity.z;
			}
		}
	}

	@Override
	public void method_12050(TeleportConfirmC2SPacket teleportConfirmC2SPacket) {
		NetworkThreadUtils.method_11073(teleportConfirmC2SPacket, this, this.player.getServerWorld());
		if (teleportConfirmC2SPacket.getTeleportId() == this.requestedTeleportId) {
			this.player
				.setPositionAnglesAndUpdate(this.requestedTeleportPos.x, this.requestedTeleportPos.y, this.requestedTeleportPos.z, this.player.yaw, this.player.pitch);
			this.updatedX = this.requestedTeleportPos.x;
			this.updatedY = this.requestedTeleportPos.y;
			this.updatedZ = this.requestedTeleportPos.z;
			if (this.player.isInTeleportationState()) {
				this.player.onTeleportationDone();
			}

			this.requestedTeleportPos = null;
		}
	}

	@Override
	public void method_12047(RecipeBookDataC2SPacket recipeBookDataC2SPacket) {
		NetworkThreadUtils.method_11073(recipeBookDataC2SPacket, this, this.player.getServerWorld());
		if (recipeBookDataC2SPacket.getMode() == RecipeBookDataC2SPacket.Mode.field_13011) {
			this.server.getRecipeManager().method_8130(recipeBookDataC2SPacket.method_12406()).ifPresent(this.player.method_14253()::onRecipeDisplayed);
		} else if (recipeBookDataC2SPacket.getMode() == RecipeBookDataC2SPacket.Mode.field_13010) {
			this.player.method_14253().setGuiOpen(recipeBookDataC2SPacket.isGuiOpen());
			this.player.method_14253().setFilteringCraftable(recipeBookDataC2SPacket.isFilteringCraftable());
			this.player.method_14253().setFurnaceGuiOpen(recipeBookDataC2SPacket.isFurnaceGuiOpen());
			this.player.method_14253().setFurnaceFilteringCraftable(recipeBookDataC2SPacket.isFurnaceFilteringCraftable());
			this.player.method_14253().setBlastFurnaceGuiOpen(recipeBookDataC2SPacket.isBlastFurnaceGuiOpen());
			this.player.method_14253().setBlastFurnaceFilteringCraftable(recipeBookDataC2SPacket.isBlastFurnaceFilteringCraftable());
			this.player.method_14253().setSmokerGuiOpen(recipeBookDataC2SPacket.isSmokerGuiOpen());
			this.player.method_14253().setSmokerFilteringCraftable(recipeBookDataC2SPacket.isSmokerGuiFilteringCraftable());
		}
	}

	@Override
	public void method_12058(AdvancementTabC2SPacket advancementTabC2SPacket) {
		NetworkThreadUtils.method_11073(advancementTabC2SPacket, this, this.player.getServerWorld());
		if (advancementTabC2SPacket.getAction() == AdvancementTabC2SPacket.Action.field_13024) {
			Identifier identifier = advancementTabC2SPacket.method_12416();
			SimpleAdvancement simpleAdvancement = this.server.method_3851().get(identifier);
			if (simpleAdvancement != null) {
				this.player.getAdvancementManager().setDisplayTab(simpleAdvancement);
			}
		}
	}

	@Override
	public void method_12059(RequestCommandCompletionsC2SPacket requestCommandCompletionsC2SPacket) {
		NetworkThreadUtils.method_11073(requestCommandCompletionsC2SPacket, this, this.player.getServerWorld());
		StringReader stringReader = new StringReader(requestCommandCompletionsC2SPacket.getPartialCommand());
		if (stringReader.canRead() && stringReader.peek() == '/') {
			stringReader.skip();
		}

		ParseResults<ServerCommandSource> parseResults = this.server.getCommandManager().getDispatcher().parse(stringReader, this.player.method_5671());
		this.server
			.getCommandManager()
			.getDispatcher()
			.getCompletionSuggestions(parseResults)
			.thenAccept(suggestions -> this.client.method_10743(new CommandSuggestionsS2CPacket(requestCommandCompletionsC2SPacket.getCompletionId(), suggestions)));
	}

	@Override
	public void method_12077(UpdateCommandBlockC2SPacket updateCommandBlockC2SPacket) {
		NetworkThreadUtils.method_11073(updateCommandBlockC2SPacket, this, this.player.getServerWorld());
		if (!this.server.areCommandBlocksEnabled()) {
			this.player.method_9203(new TranslatableTextComponent("advMode.notEnabled"));
		} else if (!this.player.isCreativeLevelTwoOp()) {
			this.player.method_9203(new TranslatableTextComponent("advMode.notAllowed"));
		} else {
			CommandBlockExecutor commandBlockExecutor = null;
			CommandBlockBlockEntity commandBlockBlockEntity = null;
			BlockPos blockPos = updateCommandBlockC2SPacket.getBlockPos();
			BlockEntity blockEntity = this.player.field_6002.method_8321(blockPos);
			if (blockEntity instanceof CommandBlockBlockEntity) {
				commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
				commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
			}

			String string = updateCommandBlockC2SPacket.getCommand();
			boolean bl = updateCommandBlockC2SPacket.shouldTrackOutput();
			if (commandBlockExecutor != null) {
				Direction direction = this.player.field_6002.method_8320(blockPos).method_11654(CommandBlock.field_10791);
				switch (updateCommandBlockC2SPacket.getType()) {
					case field_11922: {
						BlockState blockState = Blocks.field_10395.method_9564();
						this.player
							.field_6002
							.method_8652(
								blockPos,
								blockState.method_11657(CommandBlock.field_10791, direction)
									.method_11657(CommandBlock.field_10793, Boolean.valueOf(updateCommandBlockC2SPacket.isConditional())),
								2
							);
						break;
					}
					case field_11923: {
						BlockState blockState = Blocks.field_10263.method_9564();
						this.player
							.field_6002
							.method_8652(
								blockPos,
								blockState.method_11657(CommandBlock.field_10791, direction)
									.method_11657(CommandBlock.field_10793, Boolean.valueOf(updateCommandBlockC2SPacket.isConditional())),
								2
							);
						break;
					}
					case field_11924:
					default: {
						BlockState blockState = Blocks.field_10525.method_9564();
						this.player
							.field_6002
							.method_8652(
								blockPos,
								blockState.method_11657(CommandBlock.field_10791, direction)
									.method_11657(CommandBlock.field_10793, Boolean.valueOf(updateCommandBlockC2SPacket.isConditional())),
								2
							);
					}
				}

				blockEntity.validate();
				this.player.field_6002.method_8526(blockPos, blockEntity);
				commandBlockExecutor.setCommand(string);
				commandBlockExecutor.shouldTrackOutput(bl);
				if (!bl) {
					commandBlockExecutor.method_8291(null);
				}

				commandBlockBlockEntity.setAuto(updateCommandBlockC2SPacket.isAlwaysActive());
				commandBlockExecutor.method_8295();
				if (!ChatUtil.isEmpty(string)) {
					this.player.method_9203(new TranslatableTextComponent("advMode.setCommand.success", string));
				}
			}
		}
	}

	@Override
	public void method_12049(UpdateCommandBlockMinecartC2SPacket updateCommandBlockMinecartC2SPacket) {
		NetworkThreadUtils.method_11073(updateCommandBlockMinecartC2SPacket, this, this.player.getServerWorld());
		if (!this.server.areCommandBlocksEnabled()) {
			this.player.method_9203(new TranslatableTextComponent("advMode.notEnabled"));
		} else if (!this.player.isCreativeLevelTwoOp()) {
			this.player.method_9203(new TranslatableTextComponent("advMode.notAllowed"));
		} else {
			CommandBlockExecutor commandBlockExecutor = updateCommandBlockMinecartC2SPacket.getMinecartCommandExecutor(this.player.field_6002);
			if (commandBlockExecutor != null) {
				commandBlockExecutor.setCommand(updateCommandBlockMinecartC2SPacket.getCommand());
				commandBlockExecutor.shouldTrackOutput(updateCommandBlockMinecartC2SPacket.shouldTrackOutput());
				if (!updateCommandBlockMinecartC2SPacket.shouldTrackOutput()) {
					commandBlockExecutor.method_8291(null);
				}

				commandBlockExecutor.method_8295();
				this.player.method_9203(new TranslatableTextComponent("advMode.setCommand.success", updateCommandBlockMinecartC2SPacket.getCommand()));
			}
		}
	}

	@Override
	public void method_12084(PickFromInventoryC2SPacket pickFromInventoryC2SPacket) {
		NetworkThreadUtils.method_11073(pickFromInventoryC2SPacket, this, this.player.getServerWorld());
		this.player.inventory.swapSlotWithHotbar(pickFromInventoryC2SPacket.getSlot());
		this.player
			.field_13987
			.sendPacket(new GuiSlotUpdateS2CPacket(-2, this.player.inventory.selectedSlot, this.player.inventory.method_5438(this.player.inventory.selectedSlot)));
		this.player
			.field_13987
			.sendPacket(new GuiSlotUpdateS2CPacket(-2, pickFromInventoryC2SPacket.getSlot(), this.player.inventory.method_5438(pickFromInventoryC2SPacket.getSlot())));
		this.player.field_13987.sendPacket(new HeldItemChangeS2CPacket(this.player.inventory.selectedSlot));
	}

	@Override
	public void method_12060(RenameItemC2SPacket renameItemC2SPacket) {
		NetworkThreadUtils.method_11073(renameItemC2SPacket, this, this.player.getServerWorld());
		if (this.player.field_7512 instanceof AnvilContainer) {
			AnvilContainer anvilContainer = (AnvilContainer)this.player.field_7512;
			String string = SharedConstants.stripInvalidChars(renameItemC2SPacket.getItemName());
			if (string.length() <= 35) {
				anvilContainer.setNewItemName(string);
			}
		}
	}

	@Override
	public void method_12057(UpdateBeaconC2SPacket updateBeaconC2SPacket) {
		NetworkThreadUtils.method_11073(updateBeaconC2SPacket, this, this.player.getServerWorld());
		if (this.player.field_7512 instanceof BeaconContainer) {
			((BeaconContainer)this.player.field_7512).method_17372(updateBeaconC2SPacket.getPrimaryEffectId(), updateBeaconC2SPacket.getSecondaryEffectId());
		}
	}

	@Override
	public void method_12051(UpdateStructureBlockC2SPacket updateStructureBlockC2SPacket) {
		NetworkThreadUtils.method_11073(updateStructureBlockC2SPacket, this, this.player.getServerWorld());
		if (this.player.isCreativeLevelTwoOp()) {
			BlockPos blockPos = updateStructureBlockC2SPacket.getPos();
			BlockState blockState = this.player.field_6002.method_8320(blockPos);
			BlockEntity blockEntity = this.player.field_6002.method_8321(blockPos);
			if (blockEntity instanceof StructureBlockBlockEntity) {
				StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)blockEntity;
				structureBlockBlockEntity.method_11381(updateStructureBlockC2SPacket.getMode());
				structureBlockBlockEntity.setStructureName(updateStructureBlockC2SPacket.getStructureName());
				structureBlockBlockEntity.method_11378(updateStructureBlockC2SPacket.getOffset());
				structureBlockBlockEntity.method_11377(updateStructureBlockC2SPacket.getSize());
				structureBlockBlockEntity.setMirror(updateStructureBlockC2SPacket.getMirror());
				structureBlockBlockEntity.setRotation(updateStructureBlockC2SPacket.getRotation());
				structureBlockBlockEntity.setMetadata(updateStructureBlockC2SPacket.getMetadata());
				structureBlockBlockEntity.setIgnoreEntities(updateStructureBlockC2SPacket.getIgnoreEntities());
				structureBlockBlockEntity.setShowAir(updateStructureBlockC2SPacket.shouldShowAir());
				structureBlockBlockEntity.setShowBoundingBox(updateStructureBlockC2SPacket.shouldShowBoundingBox());
				structureBlockBlockEntity.setIntegrity(updateStructureBlockC2SPacket.getIntegrity());
				structureBlockBlockEntity.setSeed(updateStructureBlockC2SPacket.getSeed());
				if (structureBlockBlockEntity.hasStructureName()) {
					String string = structureBlockBlockEntity.getStructureName();
					if (updateStructureBlockC2SPacket.getAction() == StructureBlockBlockEntity.Action.field_12110) {
						if (structureBlockBlockEntity.saveStructure()) {
							this.player.method_7353(new TranslatableTextComponent("structure_block.save_success", string), false);
						} else {
							this.player.method_7353(new TranslatableTextComponent("structure_block.save_failure", string), false);
						}
					} else if (updateStructureBlockC2SPacket.getAction() == StructureBlockBlockEntity.Action.field_12109) {
						if (!structureBlockBlockEntity.isStructureAvailable()) {
							this.player.method_7353(new TranslatableTextComponent("structure_block.load_not_found", string), false);
						} else if (structureBlockBlockEntity.loadStructure()) {
							this.player.method_7353(new TranslatableTextComponent("structure_block.load_success", string), false);
						} else {
							this.player.method_7353(new TranslatableTextComponent("structure_block.load_prepare", string), false);
						}
					} else if (updateStructureBlockC2SPacket.getAction() == StructureBlockBlockEntity.Action.field_12106) {
						if (structureBlockBlockEntity.detectStructureSize()) {
							this.player.method_7353(new TranslatableTextComponent("structure_block.size_success", string), false);
						} else {
							this.player.method_7353(new TranslatableTextComponent("structure_block.size_failure"), false);
						}
					}
				} else {
					this.player.method_7353(new TranslatableTextComponent("structure_block.invalid_structure_name", updateStructureBlockC2SPacket.getStructureName()), false);
				}

				structureBlockBlockEntity.markDirty();
				this.player.field_6002.method_8413(blockPos, blockState, blockState, 3);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_16383(UpdateJigsawC2SPacket updateJigsawC2SPacket) {
		NetworkThreadUtils.method_11073(updateJigsawC2SPacket, this, this.player.getServerWorld());
		if (this.player.isCreativeLevelTwoOp()) {
			BlockPos blockPos = updateJigsawC2SPacket.getPos();
			BlockState blockState = this.player.field_6002.method_8320(blockPos);
			BlockEntity blockEntity = this.player.field_6002.method_8321(blockPos);
			if (blockEntity instanceof JigsawBlockEntity) {
				JigsawBlockEntity jigsawBlockEntity = (JigsawBlockEntity)blockEntity;
				jigsawBlockEntity.method_16379(updateJigsawC2SPacket.method_16395());
				jigsawBlockEntity.method_16378(updateJigsawC2SPacket.method_16394());
				jigsawBlockEntity.setFinalState(updateJigsawC2SPacket.getFinalState());
				jigsawBlockEntity.markDirty();
				this.player.field_6002.method_8413(blockPos, blockState, blockState, 3);
			}
		}
	}

	@Override
	public void method_12080(SelectVillagerTradeC2SPacket selectVillagerTradeC2SPacket) {
		NetworkThreadUtils.method_11073(selectVillagerTradeC2SPacket, this, this.player.getServerWorld());
		int i = selectVillagerTradeC2SPacket.method_12431();
		Container container = this.player.field_7512;
		if (container instanceof MerchantContainer) {
			((MerchantContainer)container).setRecipeIndex(i);
		}
	}

	@Override
	public void method_12053(BookUpdateC2SPacket bookUpdateC2SPacket) {
		ItemStack itemStack = bookUpdateC2SPacket.stack();
		if (!itemStack.isEmpty()) {
			if (WritableBookItem.method_8047(itemStack.method_7969())) {
				ItemStack itemStack2 = this.player.method_5998(bookUpdateC2SPacket.hand());
				if (itemStack.getItem() == Items.field_8674 && itemStack2.getItem() == Items.field_8674) {
					if (bookUpdateC2SPacket.method_12238()) {
						ItemStack itemStack3 = new ItemStack(Items.field_8360);
						CompoundTag compoundTag = itemStack2.method_7969();
						if (compoundTag != null) {
							itemStack3.method_7980(compoundTag.method_10553());
						}

						itemStack3.method_7959("author", new StringTag(this.player.method_5477().getString()));
						itemStack3.method_7959("title", new StringTag(itemStack.method_7969().getString("title")));
						ListTag listTag = itemStack.method_7969().method_10554("pages", 8);

						for (int i = 0; i < listTag.size(); i++) {
							String string = listTag.getString(i);
							TextComponent textComponent = new StringTextComponent(string);
							string = TextComponent.Serializer.toJsonString(textComponent);
							listTag.method_10606(i, new StringTag(string));
						}

						itemStack3.method_7959("pages", listTag);
						this.player.method_6122(bookUpdateC2SPacket.hand(), itemStack3);
					} else {
						itemStack2.method_7959("pages", itemStack.method_7969().method_10554("pages", 8));
					}
				}
			}
		}
	}

	@Override
	public void method_12074(QueryEntityNbtC2SPacket queryEntityNbtC2SPacket) {
		NetworkThreadUtils.method_11073(queryEntityNbtC2SPacket, this, this.player.getServerWorld());
		if (this.player.allowsPermissionLevel(2)) {
			Entity entity = this.player.getServerWorld().getEntityById(queryEntityNbtC2SPacket.getEntityId());
			if (entity != null) {
				CompoundTag compoundTag = entity.method_5647(new CompoundTag());
				this.player.field_13987.sendPacket(new TagQueryResponseS2CPacket(queryEntityNbtC2SPacket.getTransactionId(), compoundTag));
			}
		}
	}

	@Override
	public void method_12072(QueryBlockNbtC2SPacket queryBlockNbtC2SPacket) {
		NetworkThreadUtils.method_11073(queryBlockNbtC2SPacket, this, this.player.getServerWorld());
		if (this.player.allowsPermissionLevel(2)) {
			BlockEntity blockEntity = this.player.getServerWorld().method_8321(queryBlockNbtC2SPacket.getPos());
			CompoundTag compoundTag = blockEntity != null ? blockEntity.method_11007(new CompoundTag()) : null;
			this.player.field_13987.sendPacket(new TagQueryResponseS2CPacket(queryBlockNbtC2SPacket.getTransactionId(), compoundTag));
		}
	}

	@Override
	public void method_12063(PlayerMoveServerMessage playerMoveServerMessage) {
		NetworkThreadUtils.method_11073(playerMoveServerMessage, this, this.player.getServerWorld());
		if (validatePlayerMove(playerMoveServerMessage)) {
			this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.invalid_player_movement"));
		} else {
			ServerWorld serverWorld = this.server.method_3847(this.player.field_6026);
			if (!this.player.notInAnyWorld) {
				if (this.ticks == 0) {
					this.syncWithPlayerPosition();
				}

				if (this.requestedTeleportPos != null) {
					if (this.ticks - this.teleportRequestTick > 20) {
						this.teleportRequestTick = this.ticks;
						this.teleportRequest(this.requestedTeleportPos.x, this.requestedTeleportPos.y, this.requestedTeleportPos.z, this.player.yaw, this.player.pitch);
					}
				} else {
					this.teleportRequestTick = this.ticks;
					if (this.player.hasVehicle()) {
						this.player
							.setPositionAnglesAndUpdate(
								this.player.x, this.player.y, this.player.z, playerMoveServerMessage.getYaw(this.player.yaw), playerMoveServerMessage.getPitch(this.player.pitch)
							);
						this.player.getServerWorld().method_14178().method_14096(this.player);
					} else {
						double d = this.player.x;
						double e = this.player.y;
						double f = this.player.z;
						double g = this.player.y;
						double h = playerMoveServerMessage.getX(this.player.x);
						double i = playerMoveServerMessage.getY(this.player.y);
						double j = playerMoveServerMessage.getZ(this.player.z);
						float k = playerMoveServerMessage.getYaw(this.player.yaw);
						float l = playerMoveServerMessage.getPitch(this.player.pitch);
						double m = h - this.lastTickX;
						double n = i - this.lastTickY;
						double o = j - this.lastTickZ;
						double p = this.player.method_18798().lengthSquared();
						double q = m * m + n * n + o * o;
						if (this.player.isSleeping()) {
							if (q > 1.0) {
								this.teleportRequest(
									this.player.x, this.player.y, this.player.z, playerMoveServerMessage.getYaw(this.player.yaw), playerMoveServerMessage.getPitch(this.player.pitch)
								);
							}
						} else {
							this.movePacketsCount++;
							int r = this.movePacketsCount - this.lastTickMovePacketsCount;
							if (r > 5) {
								LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", this.player.method_5477().getString(), r);
								r = 1;
							}

							if (!this.player.isInTeleportationState()
								&& (!this.player.getServerWorld().getGameRules().getBoolean("disableElytraMovementCheck") || !this.player.isFallFlying())) {
								float s = this.player.isFallFlying() ? 300.0F : 100.0F;
								if (q - p > (double)(s * (float)r) && !this.method_19507()) {
									LOGGER.warn("{} moved too quickly! {},{},{}", this.player.method_5477().getString(), m, n, o);
									this.teleportRequest(this.player.x, this.player.y, this.player.z, this.player.yaw, this.player.pitch);
									return;
								}
							}

							boolean bl = serverWorld.method_8587(this.player, this.player.method_5829().contract(0.0625));
							m = h - this.updatedX;
							n = i - this.updatedY;
							o = j - this.updatedZ;
							if (this.player.onGround && !playerMoveServerMessage.isOnGround() && n > 0.0) {
								this.player.jump();
							}

							this.player.method_5784(MovementType.field_6305, new Vec3d(m, n, o));
							this.player.onGround = playerMoveServerMessage.isOnGround();
							m = h - this.player.x;
							n = i - this.player.y;
							if (n > -0.5 || n < 0.5) {
								n = 0.0;
							}

							o = j - this.player.z;
							q = m * m + n * n + o * o;
							boolean bl2 = false;
							if (!this.player.isInTeleportationState()
								&& q > 0.0625
								&& !this.player.isSleeping()
								&& !this.player.field_13974.isCreative()
								&& this.player.field_13974.getGameMode() != GameMode.field_9219) {
								bl2 = true;
								LOGGER.warn("{} moved wrongly!", this.player.method_5477().getString());
							}

							this.player.setPositionAnglesAndUpdate(h, i, j, k, l);
							this.player.method_7282(this.player.x - d, this.player.y - e, this.player.z - f);
							if (!this.player.noClip && !this.player.isSleeping()) {
								boolean bl3 = serverWorld.method_8587(this.player, this.player.method_5829().contract(0.0625));
								if (bl && (bl2 || !bl3)) {
									this.teleportRequest(d, e, f, k, l);
									return;
								}
							}

							this.floating = n >= -0.03125
								&& this.player.field_13974.getGameMode() != GameMode.field_9219
								&& !this.server.isFlightEnabled()
								&& !this.player.abilities.allowFlying
								&& !this.player.hasPotionEffect(StatusEffects.field_5902)
								&& !this.player.isFallFlying()
								&& !serverWorld.method_8534(this.player.method_5829().expand(0.0625).stretch(0.0, -0.55, 0.0));
							this.player.onGround = playerMoveServerMessage.isOnGround();
							this.player.getServerWorld().method_14178().method_14096(this.player);
							this.player.method_14207(this.player.y - g, playerMoveServerMessage.isOnGround());
							this.updatedX = this.player.x;
							this.updatedY = this.player.y;
							this.updatedZ = this.player.z;
						}
					}
				}
			}
		}
	}

	public void teleportRequest(double d, double e, double f, float g, float h) {
		this.teleportRequest(d, e, f, g, h, Collections.emptySet());
	}

	public void teleportRequest(double d, double e, double f, float g, float h, Set<PlayerPositionLookS2CPacket.Flag> set) {
		double i = set.contains(PlayerPositionLookS2CPacket.Flag.X) ? this.player.x : 0.0;
		double j = set.contains(PlayerPositionLookS2CPacket.Flag.Y) ? this.player.y : 0.0;
		double k = set.contains(PlayerPositionLookS2CPacket.Flag.Z) ? this.player.z : 0.0;
		float l = set.contains(PlayerPositionLookS2CPacket.Flag.Y_ROT) ? this.player.yaw : 0.0F;
		float m = set.contains(PlayerPositionLookS2CPacket.Flag.X_ROT) ? this.player.pitch : 0.0F;
		this.requestedTeleportPos = new Vec3d(d, e, f);
		if (++this.requestedTeleportId == Integer.MAX_VALUE) {
			this.requestedTeleportId = 0;
		}

		this.teleportRequestTick = this.ticks;
		this.player.setPositionAnglesAndUpdate(d, e, f, g, h);
		this.player.field_13987.sendPacket(new PlayerPositionLookS2CPacket(d - i, e - j, f - k, g - l, h - m, set, this.requestedTeleportId));
	}

	@Override
	public void method_12066(PlayerActionC2SPacket playerActionC2SPacket) {
		NetworkThreadUtils.method_11073(playerActionC2SPacket, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.server.method_3847(this.player.field_6026);
		BlockPos blockPos = playerActionC2SPacket.getPos();
		this.player.updateLastActionTime();
		switch (playerActionC2SPacket.getAction()) {
			case field_12969:
				if (!this.player.isSpectator()) {
					ItemStack itemStack = this.player.method_5998(Hand.OFF);
					this.player.method_6122(Hand.OFF, this.player.method_5998(Hand.MAIN));
					this.player.method_6122(Hand.MAIN, itemStack);
				}

				return;
			case field_12975:
				if (!this.player.isSpectator()) {
					this.player.dropSelectedItem(false);
				}

				return;
			case field_12970:
				if (!this.player.isSpectator()) {
					this.player.dropSelectedItem(true);
				}

				return;
			case field_12974:
				this.player.method_6075();
				return;
			case field_12968:
			case field_12971:
			case field_12973:
				double d = this.player.x - ((double)blockPos.getX() + 0.5);
				double e = this.player.y - ((double)blockPos.getY() + 0.5) + 1.5;
				double f = this.player.z - ((double)blockPos.getZ() + 0.5);
				double g = d * d + e * e + f * f;
				if (g > 36.0) {
					return;
				} else if (blockPos.getY() >= this.server.getWorldHeight()) {
					return;
				} else {
					if (playerActionC2SPacket.getAction() == PlayerActionC2SPacket.Action.field_12968) {
						if (!this.server.isSpawnProtected(serverWorld, blockPos, this.player) && serverWorld.method_8621().method_11952(blockPos)) {
							this.player.field_13974.method_14263(blockPos, playerActionC2SPacket.getDirection());
						} else {
							this.player.field_13987.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos));
						}
					} else {
						if (playerActionC2SPacket.getAction() == PlayerActionC2SPacket.Action.field_12973) {
							this.player.field_13974.method_14258(blockPos);
						} else if (playerActionC2SPacket.getAction() == PlayerActionC2SPacket.Action.field_12971) {
							this.player.field_13974.method_14269();
						}

						if (!serverWorld.method_8320(blockPos).isAir()) {
							this.player.field_13987.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos));
						}
					}

					return;
				}
			default:
				throw new IllegalArgumentException("Invalid player action");
		}
	}

	@Override
	public void method_12046(PlayerInteractBlockC2SPacket playerInteractBlockC2SPacket) {
		NetworkThreadUtils.method_11073(playerInteractBlockC2SPacket, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.server.method_3847(this.player.field_6026);
		Hand hand = playerInteractBlockC2SPacket.getHand();
		ItemStack itemStack = this.player.method_5998(hand);
		BlockHitResult blockHitResult = playerInteractBlockC2SPacket.getHitY();
		BlockPos blockPos = blockHitResult.method_17777();
		Direction direction = blockHitResult.method_17780();
		this.player.updateLastActionTime();
		if (blockPos.getY() < this.server.getWorldHeight() - 1 || direction != Direction.UP && blockPos.getY() < this.server.getWorldHeight()) {
			if (this.requestedTeleportPos == null
				&& this.player.squaredDistanceTo((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) < 64.0
				&& !this.server.isSpawnProtected(serverWorld, blockPos, this.player)
				&& serverWorld.method_8621().method_11952(blockPos)) {
				this.player.field_13974.interactBlock(this.player, serverWorld, itemStack, hand, blockHitResult);
			}
		} else {
			TextComponent textComponent = new TranslatableTextComponent("build.tooHigh", this.server.getWorldHeight()).applyFormat(TextFormat.field_1061);
			this.player.field_13987.sendPacket(new ChatMessageS2CPacket(textComponent, ChatMessageType.field_11733));
		}

		this.player.field_13987.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos));
		this.player.field_13987.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos.method_10093(direction)));
	}

	@Override
	public void method_12065(PlayerInteractItemC2SPacket playerInteractItemC2SPacket) {
		NetworkThreadUtils.method_11073(playerInteractItemC2SPacket, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.server.method_3847(this.player.field_6026);
		Hand hand = playerInteractItemC2SPacket.getHand();
		ItemStack itemStack = this.player.method_5998(hand);
		this.player.updateLastActionTime();
		if (!itemStack.isEmpty()) {
			this.player.field_13974.interactItem(this.player, serverWorld, itemStack, hand);
		}
	}

	@Override
	public void method_12073(SpectatorTeleportC2SPacket spectatorTeleportC2SPacket) {
		NetworkThreadUtils.method_11073(spectatorTeleportC2SPacket, this, this.player.getServerWorld());
		if (this.player.isSpectator()) {
			for (ServerWorld serverWorld : this.server.getWorlds()) {
				Entity entity = spectatorTeleportC2SPacket.method_12541(serverWorld);
				if (entity != null) {
					this.player.method_14251(serverWorld, entity.x, entity.y, entity.z, entity.yaw, entity.pitch);
					return;
				}
			}
		}
	}

	@Override
	public void method_12081(ResourcePackStatusC2SPacket resourcePackStatusC2SPacket) {
	}

	@Override
	public void method_12064(BoatPaddleStateC2SPacket boatPaddleStateC2SPacket) {
		NetworkThreadUtils.method_11073(boatPaddleStateC2SPacket, this, this.player.getServerWorld());
		Entity entity = this.player.getRiddenEntity();
		if (entity instanceof BoatEntity) {
			((BoatEntity)entity).setPaddleState(boatPaddleStateC2SPacket.isLeftPaddling(), boatPaddleStateC2SPacket.isRightPaddling());
		}
	}

	@Override
	public void method_10839(TextComponent textComponent) {
		LOGGER.info("{} lost connection: {}", this.player.method_5477().getString(), textComponent.getString());
		this.server.method_3856();
		this.server.method_3760().sendToAll(new TranslatableTextComponent("multiplayer.player.left", this.player.method_5476()).applyFormat(TextFormat.field_1054));
		this.player.method_14231();
		this.server.method_3760().method_14611(this.player);
		if (this.method_19507()) {
			LOGGER.info("Stopping singleplayer server as player logged out");
			this.server.stop(false);
		}
	}

	public void sendPacket(Packet<?> packet) {
		this.sendPacket(packet, null);
	}

	public void sendPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
		if (packet instanceof ChatMessageS2CPacket) {
			ChatMessageS2CPacket chatMessageS2CPacket = (ChatMessageS2CPacket)packet;
			ChatVisibility chatVisibility = this.player.getClientChatVisibility();
			if (chatVisibility == ChatVisibility.HIDDEN && chatMessageS2CPacket.getLocation() != ChatMessageType.field_11733) {
				return;
			}

			if (chatVisibility == ChatVisibility.COMMANDS && !chatMessageS2CPacket.isNonChat()) {
				return;
			}
		}

		try {
			this.client.method_10752(packet, genericFutureListener);
		} catch (Throwable var6) {
			CrashReport crashReport = CrashReport.create(var6, "Sending packet");
			CrashReportSection crashReportSection = crashReport.method_562("Packet being sent");
			crashReportSection.method_577("Packet class", () -> packet.getClass().getCanonicalName());
			throw new CrashException(crashReport);
		}
	}

	@Override
	public void method_12056(UpdateSelectedSlotC2SPacket updateSelectedSlotC2SPacket) {
		NetworkThreadUtils.method_11073(updateSelectedSlotC2SPacket, this, this.player.getServerWorld());
		if (updateSelectedSlotC2SPacket.getSelectedSlot() >= 0 && updateSelectedSlotC2SPacket.getSelectedSlot() < PlayerInventory.getHotbarSize()) {
			this.player.inventory.selectedSlot = updateSelectedSlotC2SPacket.getSelectedSlot();
			this.player.updateLastActionTime();
		} else {
			LOGGER.warn("{} tried to set an invalid carried item", this.player.method_5477().getString());
		}
	}

	@Override
	public void method_12048(ChatMessageC2SPacket chatMessageC2SPacket) {
		NetworkThreadUtils.method_11073(chatMessageC2SPacket, this, this.player.getServerWorld());
		if (this.player.getClientChatVisibility() == ChatVisibility.HIDDEN) {
			this.sendPacket(new ChatMessageS2CPacket(new TranslatableTextComponent("chat.cannotSend").applyFormat(TextFormat.field_1061)));
		} else {
			this.player.updateLastActionTime();
			String string = chatMessageC2SPacket.getChatMessage();
			string = StringUtils.normalizeSpace(string);

			for (int i = 0; i < string.length(); i++) {
				if (!SharedConstants.isValidChar(string.charAt(i))) {
					this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.illegal_characters"));
					return;
				}
			}

			if (string.startsWith("/")) {
				this.executeCommand(string);
			} else {
				TextComponent textComponent = new TranslatableTextComponent("chat.type.text", this.player.method_5476(), string);
				this.server.method_3760().broadcastChatMessage(textComponent, false);
			}

			this.messageCooldown += 20;
			if (this.messageCooldown > 200 && !this.server.method_3760().isOperator(this.player.getGameProfile())) {
				this.disconnect(new TranslatableTextComponent("disconnect.spam"));
			}
		}
	}

	private void executeCommand(String string) {
		this.server.getCommandManager().execute(this.player.method_5671(), string);
	}

	@Override
	public void method_12052(HandSwingC2SPacket handSwingC2SPacket) {
		NetworkThreadUtils.method_11073(handSwingC2SPacket, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		this.player.swingHand(handSwingC2SPacket.getHand());
	}

	@Override
	public void method_12045(ClientCommandC2SPacket clientCommandC2SPacket) {
		NetworkThreadUtils.method_11073(clientCommandC2SPacket, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		switch (clientCommandC2SPacket.getMode()) {
			case field_12979:
				this.player.setSneaking(true);
				break;
			case field_12984:
				this.player.setSneaking(false);
				break;
			case field_12981:
				this.player.setSprinting(true);
				break;
			case field_12985:
				this.player.setSprinting(false);
				break;
			case field_12986:
				if (this.player.isSleeping()) {
					this.player.wakeUp(false, true, true);
					this.requestedTeleportPos = new Vec3d(this.player.x, this.player.y, this.player.z);
				}
				break;
			case field_12987:
				if (this.player.getRiddenEntity() instanceof JumpingMount) {
					JumpingMount jumpingMount = (JumpingMount)this.player.getRiddenEntity();
					int i = clientCommandC2SPacket.getMountJumpHeight();
					if (jumpingMount.canJump() && i > 0) {
						jumpingMount.startJumping(i);
					}
				}
				break;
			case field_12980:
				if (this.player.getRiddenEntity() instanceof JumpingMount) {
					JumpingMount jumpingMount = (JumpingMount)this.player.getRiddenEntity();
					jumpingMount.stopJumping();
				}
				break;
			case field_12988:
				if (this.player.getRiddenEntity() instanceof HorseBaseEntity) {
					((HorseBaseEntity)this.player.getRiddenEntity()).method_6722(this.player);
				}
				break;
			case field_12982:
				if (!this.player.onGround && this.player.method_18798().y < 0.0 && !this.player.isFallFlying() && !this.player.isInsideWater()) {
					ItemStack itemStack = this.player.method_6118(EquipmentSlot.CHEST);
					if (itemStack.getItem() == Items.field_8833 && ElytraItem.method_7804(itemStack)) {
						this.player.method_14243();
					}
				} else {
					this.player.method_14229();
				}
				break;
			default:
				throw new IllegalArgumentException("Invalid client command!");
		}
	}

	@Override
	public void method_12062(PlayerInteractEntityC2SPacket playerInteractEntityC2SPacket) {
		NetworkThreadUtils.method_11073(playerInteractEntityC2SPacket, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.server.method_3847(this.player.field_6026);
		Entity entity = playerInteractEntityC2SPacket.getEntity(serverWorld);
		this.player.updateLastActionTime();
		if (entity != null) {
			boolean bl = this.player.canSee(entity);
			double d = 36.0;
			if (!bl) {
				d = 9.0;
			}

			if (this.player.squaredDistanceTo(entity) < d) {
				if (playerInteractEntityC2SPacket.getType() == PlayerInteractEntityC2SPacket.InteractionType.field_12876) {
					Hand hand = playerInteractEntityC2SPacket.getHand();
					this.player.interact(entity, hand);
				} else if (playerInteractEntityC2SPacket.getType() == PlayerInteractEntityC2SPacket.InteractionType.field_12873) {
					Hand hand = playerInteractEntityC2SPacket.getHand();
					entity.method_5664(this.player, playerInteractEntityC2SPacket.getHitPosition(), hand);
				} else if (playerInteractEntityC2SPacket.getType() == PlayerInteractEntityC2SPacket.InteractionType.field_12875) {
					if (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof ProjectileEntity || entity == this.player) {
						this.disconnect(new TranslatableTextComponent("multiplayer.disconnect.invalid_entity_attacked"));
						this.server.warn("Player " + this.player.method_5477().getString() + " tried to attack an invalid entity");
						return;
					}

					this.player.attack(entity);
				}
			}
		}
	}

	@Override
	public void method_12068(ClientStatusC2SPacket clientStatusC2SPacket) {
		NetworkThreadUtils.method_11073(clientStatusC2SPacket, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		ClientStatusC2SPacket.Mode mode = clientStatusC2SPacket.getMode();
		switch (mode) {
			case field_12774:
				if (this.player.notInAnyWorld) {
					this.player.notInAnyWorld = false;
					this.player = this.server.method_3760().method_14556(this.player, DimensionType.field_13072, true);
					Criterions.CHANGED_DIMENSION.method_8794(this.player, DimensionType.field_13078, DimensionType.field_13072);
				} else {
					if (this.player.getHealth() > 0.0F) {
						return;
					}

					this.player = this.server.method_3760().method_14556(this.player, DimensionType.field_13072, false);
					if (this.server.isHardcore()) {
						this.player.method_7336(GameMode.field_9219);
						this.player.getServerWorld().getGameRules().put("spectatorsGenerateChunks", "false", this.server);
					}
				}
				break;
			case field_12775:
				this.player.method_14248().method_14910(this.player);
		}
	}

	@Override
	public void method_12054(GuiCloseC2SPacket guiCloseC2SPacket) {
		NetworkThreadUtils.method_11073(guiCloseC2SPacket, this, this.player.getServerWorld());
		this.player.method_14247();
	}

	@Override
	public void method_12076(ClickWindowC2SPacket clickWindowC2SPacket) {
		NetworkThreadUtils.method_11073(clickWindowC2SPacket, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		if (this.player.field_7512.syncId == clickWindowC2SPacket.getSyncId() && this.player.field_7512.isRestricted(this.player)) {
			if (this.player.isSpectator()) {
				DefaultedList<ItemStack> defaultedList = DefaultedList.create();

				for (int i = 0; i < this.player.field_7512.slotList.size(); i++) {
					defaultedList.add(((Slot)this.player.field_7512.slotList.get(i)).method_7677());
				}

				this.player.method_7634(this.player.field_7512, defaultedList);
			} else {
				ItemStack itemStack = this.player
					.field_7512
					.method_7593(clickWindowC2SPacket.getSlot(), clickWindowC2SPacket.getButton(), clickWindowC2SPacket.getActionType(), this.player);
				if (ItemStack.areEqual(clickWindowC2SPacket.getStack(), itemStack)) {
					this.player.field_13987.sendPacket(new ConfirmGuiActionS2CPacket(clickWindowC2SPacket.getSyncId(), clickWindowC2SPacket.getTransactionId(), true));
					this.player.field_13991 = true;
					this.player.field_7512.sendContentUpdates();
					this.player.method_14241();
					this.player.field_13991 = false;
				} else {
					this.transactions.put(this.player.field_7512.syncId, clickWindowC2SPacket.getTransactionId());
					this.player.field_13987.sendPacket(new ConfirmGuiActionS2CPacket(clickWindowC2SPacket.getSyncId(), clickWindowC2SPacket.getTransactionId(), false));
					this.player.field_7512.setPlayerRestriction(this.player, false);
					DefaultedList<ItemStack> defaultedList2 = DefaultedList.create();

					for (int j = 0; j < this.player.field_7512.slotList.size(); j++) {
						ItemStack itemStack2 = ((Slot)this.player.field_7512.slotList.get(j)).method_7677();
						defaultedList2.add(itemStack2.isEmpty() ? ItemStack.EMPTY : itemStack2);
					}

					this.player.method_7634(this.player.field_7512, defaultedList2);
				}
			}
		}
	}

	@Override
	public void method_12061(CraftRequestC2SPacket craftRequestC2SPacket) {
		NetworkThreadUtils.method_11073(craftRequestC2SPacket, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		if (!this.player.isSpectator()
			&& this.player.field_7512.syncId == craftRequestC2SPacket.getSyncId()
			&& this.player.field_7512.isRestricted(this.player)
			&& this.player.field_7512 instanceof CraftingContainer) {
			this.server
				.getRecipeManager()
				.method_8130(craftRequestC2SPacket.method_12320())
				.ifPresent(recipe -> ((CraftingContainer)this.player.field_7512).method_17697(craftRequestC2SPacket.shouldCraftAll(), recipe, this.player));
		}
	}

	@Override
	public void method_12055(ButtonClickServerPacket buttonClickServerPacket) {
		NetworkThreadUtils.method_11073(buttonClickServerPacket, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		if (this.player.field_7512.syncId == buttonClickServerPacket.getSyncId() && this.player.field_7512.isRestricted(this.player) && !this.player.isSpectator()) {
			this.player.field_7512.onButtonClick(this.player, buttonClickServerPacket.method_12186());
			this.player.field_7512.sendContentUpdates();
		}
	}

	@Override
	public void method_12070(CreativeInventoryActionC2SPacket creativeInventoryActionC2SPacket) {
		NetworkThreadUtils.method_11073(creativeInventoryActionC2SPacket, this, this.player.getServerWorld());
		if (this.player.field_13974.isCreative()) {
			boolean bl = creativeInventoryActionC2SPacket.getSlot() < 0;
			ItemStack itemStack = creativeInventoryActionC2SPacket.getItemStack();
			CompoundTag compoundTag = itemStack.method_7941("BlockEntityTag");
			if (!itemStack.isEmpty() && compoundTag != null && compoundTag.containsKey("x") && compoundTag.containsKey("y") && compoundTag.containsKey("z")) {
				BlockPos blockPos = new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z"));
				BlockEntity blockEntity = this.player.field_6002.method_8321(blockPos);
				if (blockEntity != null) {
					CompoundTag compoundTag2 = blockEntity.method_11007(new CompoundTag());
					compoundTag2.remove("x");
					compoundTag2.remove("y");
					compoundTag2.remove("z");
					itemStack.method_7959("BlockEntityTag", compoundTag2);
				}
			}

			boolean bl2 = creativeInventoryActionC2SPacket.getSlot() >= 1 && creativeInventoryActionC2SPacket.getSlot() <= 45;
			boolean bl3 = itemStack.isEmpty() || itemStack.getDamage() >= 0 && itemStack.getAmount() <= 64 && !itemStack.isEmpty();
			if (bl2 && bl3) {
				if (itemStack.isEmpty()) {
					this.player.field_7498.method_7619(creativeInventoryActionC2SPacket.getSlot(), ItemStack.EMPTY);
				} else {
					this.player.field_7498.method_7619(creativeInventoryActionC2SPacket.getSlot(), itemStack);
				}

				this.player.field_7498.setPlayerRestriction(this.player, true);
			} else if (bl && bl3 && this.creativeItemDropThreshold < 200) {
				this.creativeItemDropThreshold += 20;
				ItemEntity itemEntity = this.player.method_7328(itemStack, true);
				if (itemEntity != null) {
					itemEntity.method_6980();
				}
			}
		}
	}

	@Override
	public void method_12079(GuiActionConfirmC2SPacket guiActionConfirmC2SPacket) {
		NetworkThreadUtils.method_11073(guiActionConfirmC2SPacket, this, this.player.getServerWorld());
		int i = this.player.field_7512.syncId;
		if (i == guiActionConfirmC2SPacket.getWindowId()
			&& this.transactions.getOrDefault(i, (short)(guiActionConfirmC2SPacket.getSyncId() + 1)) == guiActionConfirmC2SPacket.getSyncId()
			&& !this.player.field_7512.isRestricted(this.player)
			&& !this.player.isSpectator()) {
			this.player.field_7512.setPlayerRestriction(this.player, true);
		}
	}

	@Override
	public void method_12071(UpdateSignC2SPacket updateSignC2SPacket) {
		NetworkThreadUtils.method_11073(updateSignC2SPacket, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		ServerWorld serverWorld = this.server.method_3847(this.player.field_6026);
		BlockPos blockPos = updateSignC2SPacket.getPos();
		if (serverWorld.method_8591(blockPos)) {
			BlockState blockState = serverWorld.method_8320(blockPos);
			BlockEntity blockEntity = serverWorld.method_8321(blockPos);
			if (!(blockEntity instanceof SignBlockEntity)) {
				return;
			}

			SignBlockEntity signBlockEntity = (SignBlockEntity)blockEntity;
			if (!signBlockEntity.isEditable() || signBlockEntity.getEditor() != this.player) {
				this.server.warn("Player " + this.player.method_5477().getString() + " just tried to change non-editable sign");
				return;
			}

			String[] strings = updateSignC2SPacket.getText();

			for (int i = 0; i < strings.length; i++) {
				signBlockEntity.method_11299(i, new StringTextComponent(TextFormat.stripFormatting(strings[i])));
			}

			signBlockEntity.markDirty();
			serverWorld.method_8413(blockPos, blockState, blockState, 3);
		}
	}

	@Override
	public void method_12082(KeepAliveC2SPacket keepAliveC2SPacket) {
		if (this.waitingForKeepAlive && keepAliveC2SPacket.getId() == this.keepAliveId) {
			int i = (int)(SystemUtil.getMeasuringTimeMs() - this.lastKeepAliveTime);
			this.player.field_13967 = (this.player.field_13967 * 3 + i) / 4;
			this.waitingForKeepAlive = false;
		} else if (!this.method_19507()) {
			this.disconnect(new TranslatableTextComponent("disconnect.timeout"));
		}
	}

	@Override
	public void method_12083(UpdatePlayerAbilitiesC2SPacket updatePlayerAbilitiesC2SPacket) {
		NetworkThreadUtils.method_11073(updatePlayerAbilitiesC2SPacket, this, this.player.getServerWorld());
		this.player.abilities.flying = updatePlayerAbilitiesC2SPacket.isFlying() && this.player.abilities.allowFlying;
	}

	@Override
	public void method_12069(ClientSettingsC2SPacket clientSettingsC2SPacket) {
		NetworkThreadUtils.method_11073(clientSettingsC2SPacket, this, this.player.getServerWorld());
		this.player.setClientSettings(clientSettingsC2SPacket);
	}

	@Override
	public void method_12075(CustomPayloadC2SPacket customPayloadC2SPacket) {
	}

	@Override
	public void method_19475(class_4210 arg) {
		NetworkThreadUtils.method_11073(arg, this, this.player.getServerWorld());
		if (this.player.allowsPermissionLevel(2) || this.method_19507()) {
			this.server.setDifficulty(arg.method_19478(), false);
		}
	}

	@Override
	public void method_19476(class_4211 arg) {
		NetworkThreadUtils.method_11073(arg, this, this.player.getServerWorld());
		if (this.player.allowsPermissionLevel(2) || this.method_19507()) {
			this.server.method_19467(arg.method_19485());
		}
	}
}
