package dev.musicfloat.napoleonicfantasy.client;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.musicfloat.napoleonicfantasy.NapoleonicFantasy;
import dev.musicfloat.napoleonicfantasy.item.HandCannonItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.util.Mth;

public final class CannonRenderer extends BlockEntityWithoutLevelRenderer {
    public CannonRenderer(BlockEntityRenderDispatcher d,EntityModelSet m){super(d,m);}
    private static void part(String name,ItemStack s,PoseStack p,MultiBufferSource b,int light,int overlay){
        var mc=Minecraft.getInstance();var model=mc.getModelManager().getModel(new ResourceLocation(NapoleonicFantasy.MOD_ID,"item/"+name));
        p.pushPose();p.translate(-.5,-.5,-.5);mc.getItemRenderer().renderModelLists(model,s,light,overlay,p,b.getBuffer(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS)));p.popPose();
    }
    @Override public void renderByItem(ItemStack s,ItemDisplayContext ctx,PoseStack p,MultiBufferSource b,int light,int overlay){
        var mc=Minecraft.getInstance();float partial=mc.getFrameTime();
        LivingEntity owner=ctx.firstPerson()?mc.player:CannonEffects.renderEntity;
        if(owner!=null&&owner.getMainHandItem()!=s)owner=null;
        boolean using=owner!=null&&owner.isUsingItem()&&owner.getUseItem()==s;
        boolean loading=using&&!HandCannonItem.loaded(s);float t=using?owner.getTicksUsingItem()+partial:0;
        int stage=HandCannonItem.stage((int)t);float recoil=CannonEffects.recoil(owner,partial);
        p.pushPose();p.translate(.5,.5,.5);
        part("carriage",s,p,b,light,overlay);
        p.pushPose();p.translate(0,0,recoil*.18);p.mulPose(Axis.XP.rotationDegrees(recoil*-3));part("barrel",s,p,b,light,overlay);p.popPose();
        for(int side:new int[]{-1,1}){p.pushPose();p.translate(side*13.9/16,-8.5/16,1d/16);p.mulPose(Axis.XP.rotationDegrees(recoil*10));p.translate(-side*13.9/16,8.5/16,-1d/16);part(side<0?"wheel_left":"wheel_right",s,p,b,light,overlay);p.popPose();}
        p.pushPose();
        if(loading&&(stage==0||stage==3)){
            float stroke=stage==0?.5f-.5f*Mth.cos(t*(float)Math.PI/8):Mth.sin((t-72)/36f*(float)Math.PI);
            p.translate(0,0,(-25+Math.max(0,stroke)*12)/16);
            if(stage==3){p.translate(0,0,-38d/16);p.mulPose(Axis.YP.rotationDegrees(180));}
        }else p.translate(7.3/16,-5.7/16,12d/16);
        part("ramrod",s,p,b,light,overlay);p.popPose();
        if(loading&&(stage==1||stage==2)){
            float progress=stage==1?(t-32)/20:(t-52)/20;
            p.pushPose();p.translate(0,0,(-31+Mth.clamp(progress,0,1)*9)/16);part(stage==1?"powder":HandCannonItem.mode(s)==0?"ball":"canister",s,p,b,light,overlay);p.popPose();
        }
        if(s.hasTag()&&s.getTag().contains("IgniteAt")){p.pushPose();p.translate(.5,.32,.56);p.mulPose(Axis.YP.rotationDegrees(-55));part("wick",s,p,b,light,overlay);p.popPose();}
        if(ctx.firstPerson()&&mc.player!=null&&!mc.player.isInvisible()){
            var render=mc.getEntityRenderDispatcher().getRenderer(mc.player);
            if(render instanceof PlayerRenderer pr){
                float gripZ=.23f+recoil*.08f,gripX=.3f,gripY=-.28f;
                if(loading){
                    if(stage==0||stage==3){float stroke=stage==0?.5f-.5f*Mth.cos(t*(float)Math.PI/8):Mth.sin((t-72)/36f*(float)Math.PI);gripZ=(-31+Math.max(0,stroke)*12)*.48f/16;gripX=.04f;gripY=-.02f;}
                    else if(stage==1||stage==2){float progress=stage==1?(t-32)/20:(t-52)/20;gripZ=(-31+Mth.clamp(progress,0,1)*9)*.48f/16;gripX=.08f;gripY=-.04f;}
                }
                boolean left=ctx==ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
                arm(pr,!left,gripX*(left?-1:1),gripY,gripZ,p,b,light);
                arm(pr,left,-.35f*(left?-1:1),-.24f,-.08f,p,b,light);
            }
        }
        p.popPose();
    }
    private static void arm(PlayerRenderer pr,boolean right,float x,float y,float z,PoseStack p,MultiBufferSource b,int light){
        p.pushPose();p.scale(1/.48f,1/.48f,1/.48f);p.translate(x+(right?.375:-.375),y,z+.75);
        p.mulPose(Axis.XP.rotationDegrees(-90));
        if(right)pr.renderRightHand(p,b,light,Minecraft.getInstance().player);else pr.renderLeftHand(p,b,light,Minecraft.getInstance().player);
        p.popPose();
    }
}
