// automatically generated by the FlatBuffers compiler, do not modify

package infcomp.flatbuffers;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class ObservesInitReply extends Table {
  public static ObservesInitReply getRootAsObservesInitReply(ByteBuffer _bb) { return getRootAsObservesInitReply(_bb, new ObservesInitReply()); }
  public static ObservesInitReply getRootAsObservesInitReply(ByteBuffer _bb, ObservesInitReply obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; }
  public ObservesInitReply __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public boolean success() { int o = __offset(4); return o != 0 ? 0!=bb.get(o + bb_pos) : false; }

  public static int createObservesInitReply(FlatBufferBuilder builder,
      boolean success) {
    builder.startObject(1);
    ObservesInitReply.addSuccess(builder, success);
    return ObservesInitReply.endObservesInitReply(builder);
  }

  public static void startObservesInitReply(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addSuccess(FlatBufferBuilder builder, boolean success) { builder.addBoolean(0, success, false); }
  public static int endObservesInitReply(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

