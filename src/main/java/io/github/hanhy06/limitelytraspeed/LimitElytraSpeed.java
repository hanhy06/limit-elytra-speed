package io.github.hanhy06.limitelytraspeed;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRuleCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LimitElytraSpeed implements ModInitializer {
	public static final String MOD_ID = "limit-elytra-speed";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final GameRule<Integer> LIMIT_ELYTRA_SPEED = GameRuleBuilder
            .forInteger(70)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(Identifier.of("limit_elytra_speed"));

    public static double limitSpeed = 70;

	@Override
	public void onInitialize() {
        GameRuleEvents.changeCallback(LIMIT_ELYTRA_SPEED).register(LimitElytraSpeed::calculateSpeed);
        EntityElytraEvents.CUSTOM.register(LimitElytraSpeed::LimitSpeed);

		LOGGER.info(MOD_ID + "Loaded");
	}

    private static void calculateSpeed(Integer integer, MinecraftServer server){
        double limitPerTick = integer / 20.0;
        limitSpeed = limitPerTick * limitPerTick;
    }

    public static boolean LimitSpeed(LivingEntity entity, boolean tickElytra){
        World world = entity.getEntityWorld();
        if (world.isClient()) return tickElytra;

        Vec3d currentVelocity = entity.getVelocity();
        double currentVelocitySquared = currentVelocity.lengthSquared();

        if (currentVelocitySquared > limitSpeed) {
            entity.setVelocity(currentVelocity.normalize().multiply(limitSpeed));
            entity.velocityDirty = true;
        }

        return tickElytra;
    }
}