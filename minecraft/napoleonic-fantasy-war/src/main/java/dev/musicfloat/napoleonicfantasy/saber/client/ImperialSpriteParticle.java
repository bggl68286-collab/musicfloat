package dev.musicfloat.napoleonicfantasy.saber.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.musicfloat.napoleonicfantasy.saber.particle.ImperialParticleOptions;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class ImperialSpriteParticle extends TextureSheetParticle {
    private final int kind;
    private final float initialRoll;
    private ImperialSpriteParticle(ClientLevel level, double x, double y, double z, ImperialParticleOptions opts, SpriteSet sprites) {
        super(level, x, y, z, 0, 0, 0);
        kind = Mth.clamp(opts.kind(), 0, 3);
        lifetime = Mth.clamp(opts.lifetime(), 1, 100);
        quadSize = Mth.clamp(opts.size(), 0.1f, 64f) * 0.5f;
        initialRoll = opts.roll();
        hasPhysics = false; gravity = 0; xd = yd = zd = 0;
        setSprite(sprites.get(0, 1));
    }
    @Override protected float getU0() { float a=super.getU0(); return a+(super.getU1()-a)*(kind%2)*0.5f; }
    @Override protected float getU1() { float a=super.getU0(); return a+(super.getU1()-a)*(kind%2+1)*0.5f; }
    @Override protected float getV0() { float a=super.getV0(); return a+(super.getV1()-a)*(kind/2)*0.5f; }
    @Override protected float getV1() { float a=super.getV0(); return a+(super.getV1()-a)*(kind/2+1)*0.5f; }
    @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; }
    @Override public int getLightColor(float partialTick) { return 0xF000F0; }
    @Override public void tick() {
        super.tick();
        alpha = Mth.clamp((lifetime-age) / (lifetime * 0.55f), 0, 1) * 0.9f;
    }
    @Override public float getQuadSize(float partialTick) {
        float t=Mth.clamp((age+partialTick)/lifetime,0,1);
        return quadSize * (kind==1 ? 0.65f+t*0.55f : 0.9f+t*0.15f);
    }
    @Override public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        Vec3 c=camera.getPosition();
        float px=(float)(Mth.lerp(partialTick,xo,x)-c.x), py=(float)(Mth.lerp(partialTick,yo,y)-c.y), pz=(float)(Mth.lerp(partialTick,zo,z)-c.z);
        Quaternionf q=kind==3 ? new Quaternionf().rotationX((float)Math.PI/2) : new Quaternionf(camera.rotation());
        q.rotateZ(initialRoll + (kind==3 ? (age+partialTick)*0.025f : 0));
        Vector3f[] corners={new Vector3f(-1,-1,0),new Vector3f(-1,1,0),new Vector3f(1,1,0),new Vector3f(1,-1,0)};
        float[][] uv={{getU1(),getV1()},{getU1(),getV0()},{getU0(),getV0()},{getU0(),getV1()}};
        for(int i=0;i<4;i++) {
            corners[i].rotate(q).mul(getQuadSize(partialTick)).add(px,py,pz);
            buffer.vertex(corners[i].x,corners[i].y,corners[i].z).uv(uv[i][0],uv[i][1]).color(rCol,gCol,bCol,alpha).uv2(getLightColor(partialTick)).endVertex();
        }
    }
    public record Provider(SpriteSet sprites) implements ParticleProvider<ImperialParticleOptions> {
        @Override public Particle createParticle(ImperialParticleOptions opts, ClientLevel level, double x,double y,double z,double dx,double dy,double dz) {
            return new ImperialSpriteParticle(level,x,y,z,opts,sprites);
        }
    }
}
