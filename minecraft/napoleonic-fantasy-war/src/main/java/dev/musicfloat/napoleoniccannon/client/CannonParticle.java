package dev.musicfloat.napoleonicfantasy.client;
import dev.musicfloat.napoleonicfantasy.particle.CannonParticleOptions;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class CannonParticle extends TextureSheetParticle {
    private final int kind;private final float angle;private final Vector3f forward;
    private CannonParticle(ClientLevel l,double x,double y,double z,double dx,double dy,double dz,CannonParticleOptions o,SpriteSet sprites){
        super(l,x,y,z,0,0,0);kind=Mth.clamp(o.kind(),0,3);angle=o.roll();lifetime=Mth.clamp(o.lifetime(),1,100);quadSize=Mth.clamp(o.size(),.1f,32)*.5f;
        xd=dx;yd=dy;zd=dz;forward=new Vector3f((float)dx,(float)dy,(float)dz);if(forward.lengthSquared()<1e-8)forward.set(0,0,1);else forward.normalize();hasPhysics=false;gravity=0;friction=.97f;setSprite(sprites.get(0,1));
    }
    @Override protected float getU0(){float a=super.getU0();return a+(super.getU1()-a)*(kind%2)*.5f;}
    @Override protected float getU1(){float a=super.getU0();return a+(super.getU1()-a)*(kind%2+1)*.5f;}
    @Override protected float getV0(){float a=super.getV0();return a+(super.getV1()-a)*(kind/2)*.5f;}
    @Override protected float getV1(){float a=super.getV0();return a+(super.getV1()-a)*(kind/2+1)*.5f;}
    @Override public ParticleRenderType getRenderType(){return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;}
    @Override public int getLightColor(float t){return kind==0||kind==2?0xF000F0:super.getLightColor(t);}
    @Override public void tick(){super.tick();if(kind==1)yd+=.0013;float a=age/(float)lifetime;alpha=(kind==1?.67f:.9f)*Mth.clamp((1-a)*3,0,1)*(kind==1?Mth.clamp(age/4f,0,1):1);}
    @Override public float getQuadSize(float t){float a=(age+t)/lifetime;return quadSize*(kind==1?.65f+a*1.5f:kind==3?.7f+a*.9f:1);}
    @Override public void render(VertexConsumer b,Camera camera,float t){
        Vec3 c=camera.getPosition();float px=(float)(Mth.lerp(t,xo,x)-c.x),py=(float)(Mth.lerp(t,yo,y)-c.y),pz=(float)(Mth.lerp(t,zo,z)-c.z);
        if(kind==0){
            Vector3f up=Math.abs(forward.y)<.95?new Vector3f(0,1,0):new Vector3f(1,0,0);
            Vector3f lateral=new Vector3f(forward).cross(up).normalize();up=new Vector3f(lateral).cross(forward).normalize();
            jetPlane(b,forward,up,px,py,pz,t);jetPlane(b,forward,lateral,px,py,pz,t);return;
        }
        Quaternionf q=new Quaternionf(camera.rotation()).rotateZ(angle+(kind==1?(age+t)*.002f:0));
        Vector3f[] v={new Vector3f(-1.5f,-1,0),new Vector3f(-1.5f,1,0),new Vector3f(1.5f,1,0),new Vector3f(1.5f,-1,0)};
        float[][] uv={{getU1(),getV1()},{getU1(),getV0()},{getU0(),getV0()},{getU0(),getV1()}};
        for(int i=0;i<4;i++){v[i].rotate(q).mul(getQuadSize(t)).add(px,py,pz);b.vertex(v[i].x,v[i].y,v[i].z).uv(uv[i][0],uv[i][1]).color(rCol,gCol,bCol,alpha).uv2(getLightColor(t)).endVertex();}
    }
    private void jetPlane(VertexConsumer b,Vector3f axis,Vector3f cross,float x,float y,float z,float t){
        float size=getQuadSize(t);float[][] xy={{-.35f,-1},{-.35f,1},{2.65f,1},{2.65f,-1}};
        float[][] uv={{getU0(),getV1()},{getU0(),getV0()},{getU1(),getV0()},{getU1(),getV1()}};
        // Match the image's bright emitter on its left edge to the actual muzzle origin.
        for(int i=0;i<4;i++){Vector3f v=new Vector3f(axis).mul(xy[i][0]*size).add(new Vector3f(cross).mul(xy[i][1]*size)).add(x,y,z);b.vertex(v.x,v.y,v.z).uv(uv[i][0],uv[i][1]).color(rCol,gCol,bCol,alpha).uv2(getLightColor(t)).endVertex();}
    }
    public record Provider(SpriteSet sprites) implements ParticleProvider<CannonParticleOptions>{
        @Override public Particle createParticle(CannonParticleOptions o,ClientLevel l,double x,double y,double z,double dx,double dy,double dz){return new CannonParticle(l,x,y,z,dx,dy,dz,o,sprites);}
    }
}
