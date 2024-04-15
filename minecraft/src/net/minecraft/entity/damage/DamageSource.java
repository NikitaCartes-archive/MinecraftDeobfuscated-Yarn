package net.minecraft.entity.damage;

import javax.annotation.Nullable;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class DamageSource {
	private final RegistryEntry<DamageType> type;
	@Nullable
	private final Entity attacker;
	@Nullable
	private final Entity source;
	@Nullable
	private final Vec3d position;

	public String toString() {
		return "DamageSource (" + this.getType().msgId() + ")";
	}

	public float getExhaustion() {
		return this.getType().exhaustion();
	}

	public boolean isIndirect() {
		return this.attacker != this.source;
	}

	private DamageSource(RegistryEntry<DamageType> type, @Nullable Entity source, @Nullable Entity attacker, @Nullable Vec3d position) {
		this.type = type;
		this.attacker = attacker;
		this.source = source;
		this.position = position;
	}

	public DamageSource(RegistryEntry<DamageType> type, @Nullable Entity source, @Nullable Entity attacker) {
		this(type, source, attacker, null);
	}

	public DamageSource(RegistryEntry<DamageType> type, Vec3d position) {
		this(type, null, null, position);
	}

	public DamageSource(RegistryEntry<DamageType> type, @Nullable Entity attacker) {
		this(type, attacker, attacker);
	}

	public DamageSource(RegistryEntry<DamageType> type) {
		this(type, null, null, null);
	}

	@Nullable
	public Entity getSource() {
		return this.source;
	}

	@Nullable
	public Entity getAttacker() {
		return this.attacker;
	}

	public Text getDeathMessage(LivingEntity killed) {
		String string = "death.attack." + this.getType().msgId();
		if (this.attacker == null && this.source == null) {
			LivingEntity livingEntity2 = killed.getPrimeAdversary();
			String string2 = string + ".player";
			return livingEntity2 != null
				? Text.translatable(string2, killed.getDisplayName(), livingEntity2.getDisplayName())
				: Text.translatable(string, killed.getDisplayName());
		} else {
			Text text = this.attacker == null ? this.source.getDisplayName() : this.attacker.getDisplayName();
			ItemStack itemStack = this.attacker instanceof LivingEntity livingEntity ? livingEntity.getMainHandStack() : ItemStack.EMPTY;
			return !itemStack.isEmpty() && itemStack.contains(DataComponentTypes.CUSTOM_NAME)
				? Text.translatable(string + ".item", killed.getDisplayName(), text, itemStack.toHoverableText())
				: Text.translatable(string, killed.getDisplayName(), text);
		}
	}

	public String getName() {
		return this.getType().msgId();
	}

	public boolean isScaledWithDifficulty() {
		return switch (this.getType().scaling()) {
			case NEVER -> false;
			case WHEN_CAUSED_BY_LIVING_NON_PLAYER -> this.attacker instanceof LivingEntity && !(this.attacker instanceof PlayerEntity);
			case ALWAYS -> true;
			default -> throw new MatchException(null, null);
		};
	}

	public boolean isSourceCreativePlayer() {
		if (this.getAttacker() instanceof PlayerEntity playerEntity && playerEntity.getAbilities().creativeMode) {
			return true;
		}

		return false;
	}

	@Nullable
	public Vec3d getPosition() {
		if (this.position != null) {
			return this.position;
		} else {
			return this.source != null ? this.source.getPos() : null;
		}
	}

	@Nullable
	public Vec3d getStoredPosition() {
		return this.position;
	}

	public boolean isIn(TagKey<DamageType> tag) {
		return this.type.isIn(tag);
	}

	public boolean isOf(RegistryKey<DamageType> typeKey) {
		return this.type.matchesKey(typeKey);
	}

	public DamageType getType() {
		return this.type.value();
	}

	public RegistryEntry<DamageType> getTypeRegistryEntry() {
		return this.type;
	}
}
