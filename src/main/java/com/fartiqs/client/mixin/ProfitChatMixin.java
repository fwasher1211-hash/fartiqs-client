package com.fartiqs.client.mixin;

import com.fartiqs.client.FartiqsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.fartiqs.profit.ChatHook", remap = false)
public abstract class ProfitChatMixin {
    @Inject(method = "onEvent(Ljava/lang/String;Z)V", at = @At("HEAD"), cancellable = true, remap = false)
    private static void fartiqs$gate(String text, boolean overlay, CallbackInfo ci) {
        if (!FartiqsConfig.isOn("profit")) ci.cancel();
    }
}
