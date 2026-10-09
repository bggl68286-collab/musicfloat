package dev.musicfloat.napoleonicfantasy.network;
import dev.musicfloat.napoleonicfantasy.NapoleonicFantasy;
import dev.musicfloat.napoleonicfantasy.client.CannonEffects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.*;
import net.minecraftforge.network.simple.SimpleChannel;
import java.util.UUID;
import java.util.function.Supplier;
public final class CannonNetwork {
    private static final SimpleChannel CHANNEL=NetworkRegistry.newSimpleChannel(new ResourceLocation(NapoleonicFantasy.MOD_ID,"effects"),()->"1","1"::equals,"1"::equals);
    public static void register(){CHANNEL.messageBuilder(Effect.class,0,NetworkDirection.PLAY_TO_CLIENT).encoder(Effect::encode).decoder(Effect::decode).consumerMainThread(Effect::handle).add();}
    public static void send(UUID owner,int kind,Vec3 p,Vec3 d,Level l,boolean sound){CHANNEL.send(PacketDistributor.NEAR.with(()->new PacketDistributor.TargetPoint(p.x,p.y,p.z,128,l.dimension())),new Effect(owner,kind,p,d,sound));}
    public record Effect(UUID owner,int kind,Vec3 position,Vec3 direction,boolean sound){
        public void encode(FriendlyByteBuf b){b.writeUUID(owner);b.writeVarInt(kind);b.writeDouble(position.x);b.writeDouble(position.y);b.writeDouble(position.z);b.writeDouble(direction.x);b.writeDouble(direction.y);b.writeDouble(direction.z);b.writeBoolean(sound);}
        public static Effect decode(FriendlyByteBuf b){return new Effect(b.readUUID(),b.readVarInt(),new Vec3(b.readDouble(),b.readDouble(),b.readDouble()),new Vec3(b.readDouble(),b.readDouble(),b.readDouble()),b.readBoolean());}
        public void handle(Supplier<NetworkEvent.Context> c){DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()->()->CannonEffects.receive(this));c.get().setPacketHandled(true);}
    }
    private CannonNetwork(){}
}
