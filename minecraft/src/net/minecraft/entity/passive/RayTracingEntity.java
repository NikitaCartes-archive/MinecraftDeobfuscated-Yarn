package net.minecraft.entity.passive;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.class_8293;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.StopAndLookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.mob.ZoglinEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class RayTracingEntity extends PathAwareEntity {
	private static final String NAME = "Ray Tracing";
	private static final List<Text> POST_DEATH_MESSAGES = (List<Text>)Stream.of(
			"That was just a warm-up, next time I'll be ready!",
			"I got caught off-guard. I won't make that mistake again",
			"It's not my fault, the lag made me miss my jump!",
			"I was distracted by that beautiful sunset",
			"I was just testing to see how much fall damage I could survive",
			"I was trying to show off my parkour skills, but it didn't go as planned",
			"I thought I had enough food to survive, but apparently not",
			"I underestimated the strength of those zombies",
			"I was practicing my speedrun strats and got a bit carried away",
			"I was trying to get a better view and accidentally fell"
		)
		.map(message -> Text.translatable("chat.type.text", "Ray Tracing", message))
		.collect(Collectors.toList());
	private static final List<Text> IDLE_MESSAGES = (List<Text>)Stream.of(
			"I just found diamonds! Wait, no, it's just coal. Again.",
			"I'm a master builder. I built a dirt house once",
			"Creepers? Never heard of 'em",
			"I could've sworn I put that torch there",
			"If at first you don't succeed, dig straight down",
			"I'm not lost, I'm just exploring",
			"Is it just me, or do those cows have judgmental eyes?",
			"I've never been to the Nether, but I hear it's a nice place to vacation",
			"I heard if you punch trees long enough, they turn into diamonds",
			"Sometimes I feel like the only thing I'm good at is dying in lava",
			"I'm not lost, I'm just temporarily misplaced",
			"I like my Minecraft like I like my coffee: with extra sugar cubes",
			"I heard that if you stare at a pig long enough, it will give you its best pork chop",
			"I'm convinced that creepers were invented by the developers just to mess with us",
			"Why build a fancy castle when you can watch a video of someone else building one?"
		)
		.map(message -> Text.translatable("chat.type.text", "Ray Tracing", message))
		.collect(Collectors.toList());
	private static final List<Text> JUST_SPAWNED_MESSAGES = (List<Text>)Stream.of(
			"Did someone say cake? I'm here for the cake!",
			"Greetings, fellow Minecrafters! Let's build some amazing things together",
			"I come bearing gifts... of dirt. Lots and lots of dirt",
			"I hope everyone is ready for some serious block-placing action!",
			"I don't always play Minecraft, but when I do, I prefer to play with awesome people like you",
			"I heard there was a party happening here. Did I miss the memo?",
			"Hey everyone, can I join your Minecraft book club?",
			"I'm not saying I'm the best Minecraft player, but I did once build a castle out of wool",
			"I'm here to mine some blocks and chew bubblegum... and I'm all out of bubblegum",
			"Hello, is this the Minecraft support group? I think I'm addicted"
		)
		.map(message -> Text.translatable("chat.type.text", "Ray Tracing", message))
		.collect(Collectors.toList());
	private static final List<Text> ON_DISABLE_MESSAGES = (List<Text>)Stream.of(
			"I have to go take care of my real-life sheep. See you all later!",
			"My mom is calling me for dinner. Gotta run!",
			"Sorry guys, I have a meeting with the Ender Dragon. It's urgent.",
			"I have to go put out a fire... in the real world. Bye!",
			"I'm sorry, I have to go study for my Minecraft finals",
			"My boss just messaged me. Apparently, there's a creeper invasion at work. Gotta go!",
			"I have to go feed my pet slime. They get cranky if I don't feed them on time",
			"I need to take a break. Bye everyone!",
			"I promised my little sibling I would play Minecraft with them. Time to go fulfill that promise!",
			"I'm sorry, but I have to go save the world from the zombie apocalypse. Wish me luck!"
		)
		.map(message -> Text.translatable("chat.type.text", "Ray Tracing", message))
		.collect(Collectors.toList());
	private static final Text FRENCH_MESSAGE = Text.translatable("chat.type.text", "Ray Tracing", Text.literal("Omelette du fromage"));
	public boolean justSpawned;
	private long nextMessageTime;

	public RayTracingEntity(EntityType<RayTracingEntity> entityType, World world) {
		super(entityType, world);
		this.nextMessageTime = world.getTime() + (long)world.random.nextBetweenExclusive(80, 600);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new FleeEntityGoal(this, ZombieEntity.class, 8.0F, 1.0, 1.0));
		this.goalSelector.add(1, new FleeEntityGoal(this, EvokerEntity.class, 12.0F, 1.0, 1.0));
		this.goalSelector.add(1, new FleeEntityGoal(this, VindicatorEntity.class, 8.0F, 1.0, 1.0));
		this.goalSelector.add(1, new FleeEntityGoal(this, VexEntity.class, 8.0F, 1.0, 1.0));
		this.goalSelector.add(1, new FleeEntityGoal(this, PillagerEntity.class, 15.0F, 1.0, 1.0));
		this.goalSelector.add(1, new FleeEntityGoal(this, IllusionerEntity.class, 12.0F, 1.0, 1.0));
		this.goalSelector.add(1, new FleeEntityGoal(this, ZoglinEntity.class, 10.0F, 1.0, 1.0));
		this.goalSelector.add(1, new EscapeDangerGoal(this, 1.0));
		this.goalSelector.add(2, new TemptGoal(this, 1.0, Ingredient.ofItems(Items.DIAMOND), false));
		this.goalSelector.add(2, new LookAroundGoal(this));
		this.goalSelector.add(2, new GoToWalkTargetGoal(this, 1.0));
		this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0));
		this.goalSelector.add(3, new StopAndLookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
		this.goalSelector.add(3, new LookAtEntityGoal(this, MobEntity.class, 16.0F));
	}

	public static DefaultAttributeContainer.Builder createRayAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23F);
	}

	@Override
	public boolean cannotDespawn() {
		return true;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_PLAYER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_PLAYER_DEATH;
	}

	@Override
	public void tick() {
		if (this.nextMessageTime <= this.world.getTime()) {
			this.nextMessageTime = this.world.getTime() + (long)this.world.random.nextBetweenExclusive(600, 3600);
			if (this.justSpawned) {
				this.broadcastJustSpawnedMessage();
				this.justSpawned = false;
			} else if (!class_8293.field_43659.method_50116()) {
				this.broadcastOnDisableMessage();
				this.remove(Entity.RemovalReason.DISCARDED);
				if (!this.world.isClient && this.world instanceof ServerWorld serverWorld) {
					serverWorld.getServer().getPlayerManager().broadcast(Text.translatable("multiplayer.player.left", "Ray Tracing").formatted(Formatting.YELLOW), false);
				}
			} else {
				this.broadcast((Text)IDLE_MESSAGES.get(this.random.nextInt(IDLE_MESSAGES.size())));
			}
		}

		super.tick();
	}

	@Override
	protected void updatePostDeath() {
		super.updatePostDeath();
		if (this.deathTime >= 20 && !this.world.isClient() && this.world instanceof ServerWorld serverWorld) {
			serverWorld.spawnRayTracingEntity();
			this.broadcast((Text)POST_DEATH_MESSAGES.get(this.random.nextInt(POST_DEATH_MESSAGES.size())));
		}
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		if (!this.world.isClient && this.world instanceof ServerWorld serverWorld) {
			this.broadcastDeathMessage(serverWorld.getServer().getPlayerManager());
		}

		super.onDeath(damageSource);
	}

	private void broadcastDeathMessage(PlayerManager playerManager) {
		boolean bl = this.world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES);
		if (bl) {
			playerManager.broadcast(this.getDamageTracker().getDeathMessage(), false);
		}
	}

	public void broadcast(Text message) {
		if (!this.world.isClient() && this.world instanceof ServerWorld serverWorld) {
			if (class_8293.field_43576.method_50116()) {
				message = FRENCH_MESSAGE;
			}

			serverWorld.getServer().getPlayerManager().broadcast(message, false);
		}
	}

	public void broadcastJustSpawnedMessage() {
		this.broadcast((Text)JUST_SPAWNED_MESSAGES.get(this.random.nextInt(JUST_SPAWNED_MESSAGES.size())));
	}

	public void broadcastOnDisableMessage() {
		this.broadcast((Text)ON_DISABLE_MESSAGES.get(this.random.nextInt(ON_DISABLE_MESSAGES.size())));
	}
}
