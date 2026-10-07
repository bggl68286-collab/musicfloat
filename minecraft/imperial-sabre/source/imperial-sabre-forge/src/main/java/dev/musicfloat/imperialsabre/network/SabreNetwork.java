package dev.musicfloat.imperialsabre.network;

import dev.musicfloat.imperialsabre.ImperialSabre;
import dev.musicfloat.imperialsabre.client.ClientEffects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import java.util.UUID;
import java.util.function.Supplier;

public final class SabreNetwork {
    private static final String VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(ImperialSabre.MOD_ID, "effects"), () -> VERSION, VERSION::equals, VERSION::equals);
    public static void register() {
        CHANNEL.messageBuilder(Effect.class, 0, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(Effect::encode).decoder(Effect::decode).consumerMainThread(Effect::handle).add();
    }
    public static void effect(Player player, int kind, float range) {
        Vec3 p = player.position(), d = player.getLookAngle();
        CHANNEL.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
            p.x, p.y, p.z, 64, player.level().dimension())), new Effect(player.getUUID(), kind, p, d, range));
    }
    public record Effect(UUID owner, int kind, Vec3 position, Vec3 direction, float range) {
        public void encode(FriendlyByteBuf b) {
            b.writeUUID(owner); b.writeVarInt(kind);
            b.writeDouble(position.x); b.writeDouble(position.y); b.writeDouble(position.z);
            b.writeDouble(direction.x); b.writeDouble(direction.y); b.writeDouble(direction.z); b.writeFloat(range);
        }
        public static Effect decode(FriendlyByteBuf b) {
            return new Effect(b.readUUID(), b.readVarInt(), new Vec3(b.readDouble(), b.readDouble(), b.readDouble()),
                new Vec3(b.readDouble(), b.readDouble(), b.readDouble()), b.readFloat());
        }
        public void handle(Supplier<NetworkEvent.Context> ctx) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientEffects.receive(this));
            ctx.get().setPacketHandled(true);
        }
    }
    private SabreNetwork() {}
}
