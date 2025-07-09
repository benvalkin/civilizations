package com.uncreated.civilized.networking;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.network.FriendlyByteBuf;

public class PacketHelper {

   public static <T> List<T> readPrefixedList(FriendlyByteBuf buffer, Function<FriendlyByteBuf, T> readItem) {
      int listLength = buffer.readInt();
      List<T> result = new ArrayList<>(listLength);
      for (int i = 0; i < listLength; i++) {
         result.add(readItem.apply(buffer));
      }
      return result;
   }

   public static <T> void writePrefixedList(
         FriendlyByteBuf buffer,
         List<T> list,
         BiConsumer<FriendlyByteBuf, T> writeItem) {
      buffer.writeInt(list.size());
      for (T t : list) {
         writeItem.accept(buffer, t);
      }
   }
}
