package dev.musicfloat.napoleonicfantasy.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.musicfloat.napoleonicfantasy.NapoleonicFantasy;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import java.util.Locale;

/** Sprite index: 0 muzzle fire, 1 powder smoke, 2 ignition sparks, 3 impact dust. */
public record CannonParticleOptions(int kind, float size, int lifetime, float roll) implements ParticleOptions {
    public static final Codec<CannonParticleOptions> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.intRange(0, 3).fieldOf("kind").forGetter(CannonParticleOptions::kind),
        Codec.floatRange(0.1f, 64f).fieldOf("size").forGetter(CannonParticleOptions::size),
        Codec.intRange(1, 100).fieldOf("lifetime").forGetter(CannonParticleOptions::lifetime),
        Codec.FLOAT.fieldOf("roll").forGetter(CannonParticleOptions::roll)
    ).apply(i, CannonParticleOptions::new));
    public static final Deserializer<CannonParticleOptions> DESERIALIZER = new Deserializer<>() {
        @Override public CannonParticleOptions fromCommand(ParticleType<CannonParticleOptions> type, StringReader r) throws CommandSyntaxException {
            r.expect(' '); int kind = r.readInt(); r.expect(' '); float size = r.readFloat();
            r.expect(' '); int life = r.readInt(); r.expect(' '); float roll = r.readFloat();
            return new CannonParticleOptions(kind, size, life, roll);
        }
        @Override public CannonParticleOptions fromNetwork(ParticleType<CannonParticleOptions> type, FriendlyByteBuf b) {
            return new CannonParticleOptions(b.readVarInt(), b.readFloat(), b.readVarInt(), b.readFloat());
        }
    };
    @Override public ParticleType<?> getType() { return NapoleonicFantasy.FX.get(); }
    @Override public void writeToNetwork(FriendlyByteBuf b) { b.writeVarInt(kind); b.writeFloat(size); b.writeVarInt(lifetime); b.writeFloat(roll); }
    @Override public String writeToString() { return String.format(Locale.ROOT, "%s:cannon_fx %d %.3f %d %.3f", NapoleonicFantasy.MOD_ID, kind, size, lifetime, roll); }
}
