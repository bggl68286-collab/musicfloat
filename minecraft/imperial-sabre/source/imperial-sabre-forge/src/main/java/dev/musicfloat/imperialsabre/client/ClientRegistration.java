package dev.musicfloat.imperialsabre.client;

import dev.musicfloat.imperialsabre.ImperialSabre;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=ImperialSabre.MOD_ID,bus=Mod.EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class ClientRegistration {
    @SubscribeEvent public static void particles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ImperialSabre.IMPERIAL_FX.get(),ImperialSpriteParticle.Provider::new);
    }
}
