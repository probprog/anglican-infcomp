// automatically generated by the FlatBuffers compiler, do not modify

package infcomp.protocol;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class UniformContinuousAlt extends Table {
  public static UniformContinuousAlt getRootAsUniformContinuousAlt(ByteBuffer _bb) { return getRootAsUniformContinuousAlt(_bb, new UniformContinuousAlt()); }
  public static UniformContinuousAlt getRootAsUniformContinuousAlt(ByteBuffer _bb, UniformContinuousAlt obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; }
  public UniformContinuousAlt __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public double priorMin() { int o = __offset(4); return o != 0 ? bb.getDouble(o + bb_pos) : 0.0; }
  public double priorMax() { int o = __offset(6); return o != 0 ? bb.getDouble(o + bb_pos) : 0.0; }
  public NDArray proposalMeans() { return proposalMeans(new NDArray()); }
  public NDArray proposalMeans(NDArray obj) { int o = __offset(8); return o != 0 ? obj.__assign(__indirect(o + bb_pos), bb) : null; }
  public NDArray proposalStds() { return proposalStds(new NDArray()); }
  public NDArray proposalStds(NDArray obj) { int o = __offset(10); return o != 0 ? obj.__assign(__indirect(o + bb_pos), bb) : null; }
  public NDArray proposalCoeffs() { return proposalCoeffs(new NDArray()); }
  public NDArray proposalCoeffs(NDArray obj) { int o = __offset(12); return o != 0 ? obj.__assign(__indirect(o + bb_pos), bb) : null; }

  public static int createUniformContinuousAlt(FlatBufferBuilder builder,
      double prior_min,
      double prior_max,
      int proposal_meansOffset,
      int proposal_stdsOffset,
      int proposal_coeffsOffset) {
    builder.startObject(5);
    UniformContinuousAlt.addPriorMax(builder, prior_max);
    UniformContinuousAlt.addPriorMin(builder, prior_min);
    UniformContinuousAlt.addProposalCoeffs(builder, proposal_coeffsOffset);
    UniformContinuousAlt.addProposalStds(builder, proposal_stdsOffset);
    UniformContinuousAlt.addProposalMeans(builder, proposal_meansOffset);
    return UniformContinuousAlt.endUniformContinuousAlt(builder);
  }

  public static void startUniformContinuousAlt(FlatBufferBuilder builder) { builder.startObject(5); }
  public static void addPriorMin(FlatBufferBuilder builder, double priorMin) { builder.addDouble(0, priorMin, 0.0); }
  public static void addPriorMax(FlatBufferBuilder builder, double priorMax) { builder.addDouble(1, priorMax, 0.0); }
  public static void addProposalMeans(FlatBufferBuilder builder, int proposalMeansOffset) { builder.addOffset(2, proposalMeansOffset, 0); }
  public static void addProposalStds(FlatBufferBuilder builder, int proposalStdsOffset) { builder.addOffset(3, proposalStdsOffset, 0); }
  public static void addProposalCoeffs(FlatBufferBuilder builder, int proposalCoeffsOffset) { builder.addOffset(4, proposalCoeffsOffset, 0); }
  public static int endUniformContinuousAlt(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

