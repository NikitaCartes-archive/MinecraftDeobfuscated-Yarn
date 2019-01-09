package net.minecraft;

import javax.annotation.Nullable;

public class class_1282 {
	public static final class_1282 field_5867 = new class_1282("inFire").method_5507();
	public static final class_1282 field_5861 = new class_1282("lightningBolt");
	public static final class_1282 field_5854 = new class_1282("onFire").method_5508().method_5507();
	public static final class_1282 field_5863 = new class_1282("lava").method_5507();
	public static final class_1282 field_5858 = new class_1282("hotFloor").method_5507();
	public static final class_1282 field_5855 = new class_1282("inWall").method_5508();
	public static final class_1282 field_5844 = new class_1282("cramming").method_5508();
	public static final class_1282 field_5859 = new class_1282("drown").method_5508();
	public static final class_1282 field_5852 = new class_1282("starve").method_5508().method_5509();
	public static final class_1282 field_5848 = new class_1282("cactus");
	public static final class_1282 field_5868 = new class_1282("fall").method_5508();
	public static final class_1282 field_5843 = new class_1282("flyIntoWall").method_5508();
	public static final class_1282 field_5849 = new class_1282("outOfWorld").method_5508().method_5505();
	public static final class_1282 field_5869 = new class_1282("generic").method_5508();
	public static final class_1282 field_5846 = new class_1282("magic").method_5508().method_5515();
	public static final class_1282 field_5850 = new class_1282("wither").method_5508();
	public static final class_1282 field_5865 = new class_1282("anvil");
	public static final class_1282 field_5847 = new class_1282("fallingBlock");
	public static final class_1282 field_5856 = new class_1282("dragonBreath").method_5508();
	public static final class_1282 field_5860 = new class_1282("fireworks").method_5518();
	public static final class_1282 field_5842 = new class_1282("dryout");
	public static final class_1282 field_16992 = new class_1282("sweetBerryBush");
	private boolean field_5840;
	private boolean field_5857;
	private boolean field_5839;
	private float field_5845 = 0.1F;
	private boolean field_5866;
	private boolean field_5853;
	private boolean field_5864;
	private boolean field_5851;
	private boolean field_5862;
	public final String field_5841;

	public static class_1282 method_5511(class_1309 arg) {
		return new class_1285("mob", arg);
	}

	public static class_1282 method_5519(class_1297 arg, class_1309 arg2) {
		return new class_1284("mob", arg, arg2);
	}

	public static class_1282 method_5532(class_1657 arg) {
		return new class_1285("player", arg);
	}

	public static class_1282 method_5522(class_1665 arg, @Nullable class_1297 arg2) {
		return new class_1284("arrow", arg, arg2).method_5517();
	}

	public static class_1282 method_5520(class_1297 arg, @Nullable class_1297 arg2) {
		return new class_1284("trident", arg, arg2).method_5517();
	}

	public static class_1282 method_5521(class_1668 arg, @Nullable class_1297 arg2) {
		return arg2 == null ? new class_1284("onFire", arg, arg).method_5507().method_5517() : new class_1284("fireball", arg, arg2).method_5507().method_5517();
	}

	public static class_1282 method_5524(class_1297 arg, @Nullable class_1297 arg2) {
		return new class_1284("thrown", arg, arg2).method_5517();
	}

	public static class_1282 method_5536(class_1297 arg, @Nullable class_1297 arg2) {
		return new class_1284("indirectMagic", arg, arg2).method_5508().method_5515();
	}

	public static class_1282 method_5513(class_1297 arg) {
		return new class_1285("thorns", arg).method_5550().method_5515();
	}

	public static class_1282 method_5531(@Nullable class_1927 arg) {
		return arg != null && arg.method_8347() != null
			? new class_1285("explosion.player", arg.method_8347()).method_5516().method_5518()
			: new class_1282("explosion").method_5516().method_5518();
	}

	public static class_1282 method_5512(@Nullable class_1309 arg) {
		return arg != null ? new class_1285("explosion.player", arg).method_5516().method_5518() : new class_1282("explosion").method_5516().method_5518();
	}

	public static class_1282 method_5523() {
		return new class_1286();
	}

	public boolean method_5533() {
		return this.field_5853;
	}

	public class_1282 method_5517() {
		this.field_5853 = true;
		return this;
	}

	public boolean method_5535() {
		return this.field_5862;
	}

	public class_1282 method_5518() {
		this.field_5862 = true;
		return this;
	}

	public boolean method_5537() {
		return this.field_5840;
	}

	public float method_5528() {
		return this.field_5845;
	}

	public boolean method_5538() {
		return this.field_5857;
	}

	public boolean method_5504() {
		return this.field_5839;
	}

	protected class_1282(String string) {
		this.field_5841 = string;
	}

	@Nullable
	public class_1297 method_5526() {
		return this.method_5529();
	}

	@Nullable
	public class_1297 method_5529() {
		return null;
	}

	protected class_1282 method_5508() {
		this.field_5840 = true;
		this.field_5845 = 0.0F;
		return this;
	}

	protected class_1282 method_5505() {
		this.field_5857 = true;
		return this;
	}

	protected class_1282 method_5509() {
		this.field_5839 = true;
		this.field_5845 = 0.0F;
		return this;
	}

	protected class_1282 method_5507() {
		this.field_5866 = true;
		return this;
	}

	public class_2561 method_5506(class_1309 arg) {
		class_1309 lv = arg.method_6124();
		String string = "death.attack." + this.field_5841;
		String string2 = string + ".player";
		return lv != null ? new class_2588(string2, arg.method_5476(), lv.method_5476()) : new class_2588(string, arg.method_5476());
	}

	public boolean method_5534() {
		return this.field_5866;
	}

	public String method_5525() {
		return this.field_5841;
	}

	public class_1282 method_5516() {
		this.field_5864 = true;
		return this;
	}

	public boolean method_5514() {
		return this.field_5864;
	}

	public boolean method_5527() {
		return this.field_5851;
	}

	public class_1282 method_5515() {
		this.field_5851 = true;
		return this;
	}

	public boolean method_5530() {
		class_1297 lv = this.method_5529();
		return lv instanceof class_1657 && ((class_1657)lv).field_7503.field_7477;
	}

	@Nullable
	public class_243 method_5510() {
		return null;
	}
}
