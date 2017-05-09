// automatically generated by the FlatBuffers compiler, do not modify

package infcomp.protocol;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class TracesFromPriorReply extends Table {
  public static TracesFromPriorReply getRootAsTracesFromPriorReply(ByteBuffer _bb) { return getRootAsTracesFromPriorReply(_bb, new TracesFromPriorReply()); }
  public static TracesFromPriorReply getRootAsTracesFromPriorReply(ByteBuffer _bb, TracesFromPriorReply obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; }
  public TracesFromPriorReply __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public Trace traces(int j) { return traces(new Trace(), j); }
  public Trace traces(Trace obj, int j) { int o = __offset(4); return o != 0 ? obj.__assign(__indirect(__vector(o) + j * 4), bb) : null; }
  public int tracesLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }

  public static int createTracesFromPriorReply(FlatBufferBuilder builder,
      int tracesOffset) {
    builder.startObject(1);
    TracesFromPriorReply.addTraces(builder, tracesOffset);
    return TracesFromPriorReply.endTracesFromPriorReply(builder);
  }

  public static void startTracesFromPriorReply(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addTraces(FlatBufferBuilder builder, int tracesOffset) { builder.addOffset(0, tracesOffset, 0); }
  public static int createTracesVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startTracesVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endTracesFromPriorReply(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}
