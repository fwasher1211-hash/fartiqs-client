package com.fartiqs.client.mixin;

import com.fartiqs.client.FartiqsConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.fartiqs.profit.ProfitHudClient", remap = false)
public abstract class ProfitClientMixin {
    @Inject(method = "tick(Lnet/minecraft/class_310;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private static void fartiqs$gateTick(Minecraft mc, CallbackInfo ci) {
        if (!FartiqsConfig.isOn("profit")) ci.cancel();
    }

    @Inject(method = "render(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V",
            at = @At("HEAD"), cancellable = true, remap = false)
    private static void fartiqs$gateRender(GuiGraphics g, DeltaTracker dt, CallbackInfo ci) {
        if (!FartiqsConfig.isOn("profit")) ci.cancel();
    }
}
