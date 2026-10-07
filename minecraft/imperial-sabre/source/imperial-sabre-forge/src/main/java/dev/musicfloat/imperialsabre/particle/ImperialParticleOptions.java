package dev.musicfloat.imperialsabre.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.musicfloat.imperialsabre.ImperialSabre;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import java.util.Locale;

/** Sprite index: 0 slash, 1 burst, 2 eagle, 3 horizontal seal. */
public record ImperialParticleOptions(int kind, float size, int lifetime, float roll) implements ParticleOptions {
    public static final Codec<ImperialParticleOptions> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.intRange(0, 3).fieldOf("kind").forGetter(ImperialParticleOptions::kind),
        Codec.floatRange(0.1f, 64f).fieldOf("size").forGetter(ImperialParticleOptions::size),
        Codec.intRange(1, 100).fieldOf("lifetime").forGetter(ImperialParticleOptions::lifetime),
        Codec.FLOAT.fieldOf("roll").forGetter(ImperialParticleOptions::roll)
    ).apply(i, ImperialParticleOptions::new));
    public static final Deserializer<ImperialParticleOptions> DESERIALIZER = new Deserializer<>() {
        @Override public ImperialParticleOptions fromCommand(ParticleType<ImperialParticleOptions> type, StringReader r) throws CommandSyntaxException {
            r.expect(' '); int kind = r.readInt(); r.expect(' '); float size = r.readFloat();
            r.expect(' '); int life = r.readInt(); r.expect(' '); float roll = r.readFloat();
            return new ImperialParticleOptions(kind, size, life, roll);
        }
        @Override public ImperialParticleOptions fromNetwork(ParticleType<ImperialParticleOptions> type, FriendlyByteBuf b) {
            return new ImperialParticleOptions(b.readVarInt(), b.readFloat(), b.readVarInt(), b.readFloat());
        }
    };
    @Override public ParticleType<?> getType() { return ImperialSabre.IMPERIAL_FX.get(); }
    @Override public void writeToNetwork(FriendlyByteBuf b) { b.writeVarInt(kind); b.writeFloat(size); b.writeVarInt(lifetime); b.writeFloat(roll); }
    @Override public String writeToString() { return String.format(Locale.ROOT, "%s:imperial_fx %d %.3f %d %.3f", ImperialSabre.MOD_ID, kind, size, lifetime, roll); }
}
