package com.uncreated.civilized.neoforge.registration.attachments;

import io.netty.buffer.ByteBuf;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

@Getter
@Setter
@EqualsAndHashCode
public class CurrencyValue {

   private int value;

   public CurrencyValue(int value) {
      this.value = value;
   }

   public static final Codec<CurrencyValue> CODEC =
         RecordCodecBuilder.create(
               instance -> instance.group(Codec.INT.fieldOf("value").forGetter(CurrencyValue::getValue))
                     .apply(instance, CurrencyValue::new));

   public static final StreamCodec<ByteBuf, CurrencyValue> STREAM_CODEC =
         StreamCodec.composite(ByteBufCodecs.INT, CurrencyValue::getValue, CurrencyValue::new);

}
