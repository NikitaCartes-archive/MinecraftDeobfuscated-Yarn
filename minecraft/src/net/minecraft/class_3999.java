package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface class_3999 {
	class_3999 field_17827 = new class_3999() {
		@Override
		public void method_18130(class_287 arg, class_1060 arg2) {
			class_308.method_1450();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
			arg2.method_4618(class_1059.field_5275);
			arg.method_1328(7, class_290.field_1584);
		}

		@Override
		public void method_18131(class_289 arg) {
			arg.method_1350();
		}

		public String toString() {
			return "TERRAIN_SHEET";
		}
	};
	class_3999 field_17828 = new class_3999() {
		@Override
		public void method_18130(class_287 arg, class_1060 arg2) {
			class_308.method_1450();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
			arg2.method_4618(class_1059.field_17898);
			arg.method_1328(7, class_290.field_1584);
		}

		@Override
		public void method_18131(class_289 arg) {
			arg.method_1350();
		}

		public String toString() {
			return "PARTICLE_SHEET_OPAQUE";
		}
	};
	class_3999 field_17829 = new class_3999() {
		@Override
		public void method_18130(class_287 arg, class_1060 arg2) {
			class_308.method_1450();
			GlStateManager.depthMask(false);
			arg2.method_4618(class_1059.field_17898);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.alphaFunc(516, 0.003921569F);
			arg.method_1328(7, class_290.field_1584);
		}

		@Override
		public void method_18131(class_289 arg) {
			arg.method_1350();
		}

		public String toString() {
			return "PARTICLE_SHEET_TRANSLUCENT";
		}
	};
	class_3999 field_17830 = new class_3999() {
		@Override
		public void method_18130(class_287 arg, class_1060 arg2) {
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
			arg2.method_4618(class_1059.field_17898);
			class_308.method_1450();
			arg.method_1328(7, class_290.field_1584);
		}

		@Override
		public void method_18131(class_289 arg) {
			arg.method_1350();
		}

		public String toString() {
			return "PARTICLE_SHEET_LIT";
		}
	};
	class_3999 field_17831 = new class_3999() {
		@Override
		public void method_18130(class_287 arg, class_1060 arg2) {
			GlStateManager.depthMask(true);
			GlStateManager.disableBlend();
		}

		@Override
		public void method_18131(class_289 arg) {
		}

		public String toString() {
			return "CUSTOM";
		}
	};
	class_3999 field_17832 = new class_3999() {
		@Override
		public void method_18130(class_287 arg, class_1060 arg2) {
		}

		@Override
		public void method_18131(class_289 arg) {
		}

		public String toString() {
			return "NO_RENDER";
		}
	};

	void method_18130(class_287 arg, class_1060 arg2);

	void method_18131(class_289 arg);
}
