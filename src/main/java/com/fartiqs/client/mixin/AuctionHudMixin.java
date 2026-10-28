package com.fartiqs.client.mixin;

import com.fartiqs.client.FartiqsConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.example.auction.client.AuctionHud", remap = false)
public abstract class AuctionHudMixin {
    @Inject(method = "render(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V",
            at = @At("HEAD"), cancellable = true, remap = false)
    private static void fartiqs$gate(GuiGraphics g, DeltaTracker dt, CallbackInfo ci) {
        if (!FartiqsConfig.isOn("auction")) ci.cancel();
    }
}
