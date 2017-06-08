// automatically generated by the FlatBuffers compiler, do not modify

package infcomp.protocol;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class Beta extends Table {
  public static Beta getRootAsBeta(ByteBuffer _bb) { return getRootAsBeta(_bb, new Beta()); }
  public static Beta getRootAsBeta(ByteBuffer _bb, Beta obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; }
  public Beta __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public double proposalMode() { int o = __offset(4); return o != 0 ? bb.getDouble(o + bb_pos) : 0.0; }
  public double proposalCertainty() { int o = __offset(6); return o != 0 ? bb.getDouble(o + bb_pos) : 0.0; }

  public static int createBeta(FlatBufferBuilder builder,
      double proposal_mode,
      double proposal_certainty) {
    builder.startObject(2);
    Beta.addProposalCertainty(builder, proposal_certainty);
    Beta.addProposalMode(builder, proposal_mode);
    return Beta.endBeta(builder);
  }

  public static void startBeta(FlatBufferBuilder builder) { builder.startObject(2); }
  public static void addProposalMode(FlatBufferBuilder builder, double proposalMode) { builder.addDouble(0, proposalMode, 0.0); }
  public static void addProposalCertainty(FlatBufferBuilder builder, double proposalCertainty) { builder.addDouble(1, proposalCertainty, 0.0); }
  public static int endBeta(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

