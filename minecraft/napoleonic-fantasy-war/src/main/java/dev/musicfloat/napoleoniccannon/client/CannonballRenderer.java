package dev.musicfloat.napoleonicfantasy.client;
import dev.musicfloat.napoleonicfantasy.entity.Cannonball;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
public final class CannonballRenderer extends ThrownItemRenderer<Cannonball>{
    public CannonballRenderer(EntityRendererProvider.Context c){super(c,.45f,false);}
    @Override public void render(Cannonball e,float yaw,float partial,PoseStack p,MultiBufferSource b,int light){p.pushPose();if(e.isPellet())p.scale(.27f,.27f,.27f);super.render(e,yaw,partial,p,b,light);p.popPose();}
}
