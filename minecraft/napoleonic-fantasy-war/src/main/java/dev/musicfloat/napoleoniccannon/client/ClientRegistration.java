package dev.musicfloat.napoleonicfantasy.client;
import dev.musicfloat.napoleonicfantasy.NapoleonicFantasy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.List;
import net.minecraft.world.entity.EquipmentSlot;
import dev.musicfloat.napoleonicfantasy.uniform.SkullHussarArmorModel;
@Mod.EventBusSubscriber(modid=NapoleonicFantasy.MOD_ID,bus=Mod.EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class ClientRegistration {
    public static final List<String> PARTS=List.of("carriage","barrel","wheel_left","wheel_right","ramrod","ball","powder","canister","wick");
    @SubscribeEvent public static void particles(RegisterParticleProvidersEvent e){e.registerSpriteSet(NapoleonicFantasy.FX.get(),CannonParticle.Provider::new);}
    @SubscribeEvent public static void models(ModelEvent.RegisterAdditional e){for(String part:PARTS)e.register(new ResourceLocation(NapoleonicFantasy.MOD_ID,"item/"+part));}
    @SubscribeEvent public static void entities(EntityRenderersEvent.RegisterRenderers e){e.registerEntityRenderer(NapoleonicFantasy.BALL.get(),CannonballRenderer::new);}
    @SubscribeEvent public static void layers(EntityRenderersEvent.RegisterLayerDefinitions e){
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            e.registerLayerDefinition(SkullHussarArmorModel.layer(slot, false), () -> SkullHussarArmorModel.create(slot, false));
            e.registerLayerDefinition(SkullHussarArmorModel.layer(slot, true), () -> SkullHussarArmorModel.create(slot, true));
        }
    }
    private ClientRegistration(){}
}
