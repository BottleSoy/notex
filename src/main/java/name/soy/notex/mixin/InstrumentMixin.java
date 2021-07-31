package name.soy.notex.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.enums.Instrument;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Instrument.class)
public class InstrumentMixin {
	@Inject(method = "fromBlockState",at=@At("RETURN"), cancellable = true)
	private static void getIns(BlockState state, CallbackInfoReturnable<Instrument> cir){
		cir.setReturnValue(Instrument.HARP);
	}
}
