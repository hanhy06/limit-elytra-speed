package io.github.hanhy06.limitelytraspeed;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LimitElytraSpeed implements ModInitializer {
	public static final String MOD_ID = "limit-elytra-speed";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final GameRules.Key<GameRules.IntRule> LIMIT_ELYTRA_SPEED = GameRuleRegistry.register(
            "limit_elytra_speed",
            GameRules.Category.PLAYER,
            GameRuleFactory.createIntRule(70)
    );

	@Override
	public void onInitialize() {
        EntityElytraEvents.CUSTOM.register(LimitElytraSpeed::LimitSpeed);

		LOGGER.info(MOD_ID + "Loaded");
	}

    public static boolean LimitSpeed(LivingEntity entity, boolean tickElytra){
        World world = entity.getEntityWorld();
        if (world.isClient()) return tickElytra;

        ServerWorld serverWorld = world.getServer().getWorld(world.getRegistryKey());
        int limitSpeed = serverWorld.getGameRules().getInt(LIMIT_ELYTRA_SPEED);

        double limitPerTick = limitSpeed / 20.0;
        double limitPerTickSquared = limitPerTick * limitPerTick;

        Vec3d currentVelocity = entity.getVelocity();
        double currentVelocitySquared = currentVelocity.lengthSquared();

        if (currentVelocitySquared > limitPerTickSquared && entity instanceof ServerPlayerEntity player) {
            entity.setVelocity(currentVelocity.normalize().multiply(limitPerTick));
            entity.velocityModified = true;
        }

        return tickElytra;
    }
}