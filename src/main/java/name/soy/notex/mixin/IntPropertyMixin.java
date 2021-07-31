package name.soy.notex.mixin;

import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntProperty.class)
public abstract class IntPropertyMixin extends Property<Integer> {
	protected IntPropertyMixin(String name, Class<Integer> type) {
		super(name, type);
	}

	@Inject(method = "of", at = @At("RETURN"), cancellable = true)
	private static void of(String name, int min, int max, CallbackInfoReturnable<IntProperty> cir) {
		if (name.equals("note") && min == 0 && max == 24) {
			cir.setReturnValue(IntProperty.of("note", 0, 128));
		}
	}
}
