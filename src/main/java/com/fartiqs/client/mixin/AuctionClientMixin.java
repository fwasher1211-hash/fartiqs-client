package com.fartiqs.client.mixin;

import com.fartiqs.client.FartiqsConfig;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.example.auction.client.AuctionModClient", remap = false)
public abstract class AuctionClientMixin {
    /** Ignore payment messages while Auction is off. */
    @Inject(method = "handle(Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private static void fartiqs$gate(String msg, CallbackInfo ci) {
        if (!FartiqsConfig.isOn("auction")) ci.cancel();
    }

    /** The Auction mod's own Right Shift key is disabled so Right Shift only opens the Fartiqs Client menu. */
    @Redirect(method = "lambda$onInitializeClient$0(Lnet/minecraft/class_310;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/class_304;method_1436()Z", remap = false),
            remap = false)
    private static boolean fartiqs$noOwnKey(KeyMapping key) {
        return false;
    }
}
