package io.github.hanhy06.limitelytraspeed.mixin;

import io.github.hanhy06.limitelytraspeed.LimitElytraSpeed;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class ElytraPhysicsMixin {
    @Inject(method = "calcGlidingVelocity", at = @At("RETURN"), cancellable = true)
    private void limitElytraSpeed(Vec3d oldVelocity, CallbackInfoReturnable<Vec3d> cir) {
        Vec3d calculatedVelocity = cir.getReturnValue();

        if (calculatedVelocity.lengthSquared() > LimitElytraSpeed.limitSpeed) {
            cir.setReturnValue(calculatedVelocity.normalize().multiply(LimitElytraSpeed.limitSpeed));
        }
    }
}