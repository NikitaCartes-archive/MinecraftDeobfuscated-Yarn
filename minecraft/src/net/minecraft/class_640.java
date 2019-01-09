package net.minecraft;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_640 {
	private final GameProfile field_3741;
	private final Map<Type, class_2960> field_3742 = Maps.newEnumMap(Type.class);
	private class_1934 field_3744;
	private int field_3739;
	private boolean field_3740;
	private String field_3745;
	private class_2561 field_3743;
	private int field_3738;
	private int field_3736;
	private long field_3737;
	private long field_3747;
	private long field_3746;

	public class_640(GameProfile gameProfile) {
		this.field_3741 = gameProfile;
	}

	public class_640(class_2703.class_2705 arg) {
		this.field_3741 = arg.method_11726();
		this.field_3744 = arg.method_11725();
		this.field_3739 = arg.method_11727();
		this.field_3743 = arg.method_11724();
	}

	public GameProfile method_2966() {
		return this.field_3741;
	}

	public class_1934 method_2958() {
		return this.field_3744;
	}

	protected void method_2963(class_1934 arg) {
		this.field_3744 = arg;
	}

	public int method_2959() {
		return this.field_3739;
	}

	protected void method_2970(int i) {
		this.field_3739 = i;
	}

	public boolean method_2967() {
		return this.method_2968() != null;
	}

	public String method_2977() {
		return this.field_3745 == null ? class_1068.method_4647(this.field_3741.getId()) : this.field_3745;
	}

	public class_2960 method_2968() {
		this.method_2969();
		return MoreObjects.firstNonNull((class_2960)this.field_3742.get(Type.SKIN), class_1068.method_4648(this.field_3741.getId()));
	}

	@Nullable
	public class_2960 method_2979() {
		this.method_2969();
		return (class_2960)this.field_3742.get(Type.CAPE);
	}

	@Nullable
	public class_2960 method_2957() {
		this.method_2969();
		return (class_2960)this.field_3742.get(Type.ELYTRA);
	}

	@Nullable
	public class_268 method_2955() {
		return class_310.method_1551().field_1687.method_8428().method_1164(this.method_2966().getName());
	}

	protected void method_2969() {
		synchronized (this) {
			if (!this.field_3740) {
				this.field_3740 = true;
				class_310.method_1551().method_1582().method_4652(this.field_3741, (type, arg, minecraftProfileTexture) -> {
					switch (type) {
						case SKIN:
							this.field_3742.put(Type.SKIN, arg);
							this.field_3745 = minecraftProfileTexture.getMetadata("model");
							if (this.field_3745 == null) {
								this.field_3745 = "default";
							}
							break;
						case CAPE:
							this.field_3742.put(Type.CAPE, arg);
							break;
						case ELYTRA:
							this.field_3742.put(Type.ELYTRA, arg);
					}
				}, true);
			}
		}
	}

	public void method_2962(@Nullable class_2561 arg) {
		this.field_3743 = arg;
	}

	@Nullable
	public class_2561 method_2971() {
		return this.field_3743;
	}

	public int method_2973() {
		return this.field_3738;
	}

	public void method_2972(int i) {
		this.field_3738 = i;
	}

	public int method_2960() {
		return this.field_3736;
	}

	public void method_2965(int i) {
		this.field_3736 = i;
	}

	public long method_2974() {
		return this.field_3737;
	}

	public void method_2978(long l) {
		this.field_3737 = l;
	}

	public long method_2961() {
		return this.field_3747;
	}

	public void method_2975(long l) {
		this.field_3747 = l;
	}

	public long method_2976() {
		return this.field_3746;
	}

	public void method_2964(long l) {
		this.field_3746 = l;
	}
}
