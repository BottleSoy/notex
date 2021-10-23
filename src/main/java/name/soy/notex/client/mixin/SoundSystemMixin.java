package name.soy.notex.client.mixin;

import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 解除pitch的最大拉伸限度(0.5-2)
 */
@Mixin(SoundSystem.class)
public class SoundSystemMixin {
	@Redirect(method = "getAdjustedPitch", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F"))
	float clamp(float value, float min, float max) {
		return value;
	}
}
