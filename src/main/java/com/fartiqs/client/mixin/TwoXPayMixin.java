package com.fartiqs.client.mixin;

import com.fartiqs.client.FartiqsConfig;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** When "2x Pay" is off, chat messages pass through untouched. */
@Mixin(targets = "com.fartiq.twoxpay.FartiqsTwoXPayMod", remap = false)
public abstract class TwoXPayMixin {
    @Inject(method = "lambda$onInitializeClient$0(Lnet/minecraft/class_2561;Z)Lnet/minecraft/class_2561;",
            at = @At("HEAD"), cancellable = true, remap = false)
    private static void fartiqs$gate(Component message, boolean overlay, CallbackInfoReturnable<Component> cir) {
        if (!FartiqsConfig.isOn("pay2x")) cir.setReturnValue(message);
    }
}
