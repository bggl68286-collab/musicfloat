package dev.musicfloat.napoleonicfantasy.saber.client;

import dev.musicfloat.napoleonicfantasy.NapoleonicFantasy;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=NapoleonicFantasy.MOD_ID,bus=Mod.EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class ClientRegistration {
    @SubscribeEvent public static void particles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(NapoleonicFantasy.IMPERIAL_FX.get(),ImperialSpriteParticle.Provider::new);
    }
}
