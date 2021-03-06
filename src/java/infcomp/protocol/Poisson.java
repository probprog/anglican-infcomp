// automatically generated by the FlatBuffers compiler, do not modify

package infcomp.protocol;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class Poisson extends Table {
  public static Poisson getRootAsPoisson(ByteBuffer _bb) { return getRootAsPoisson(_bb, new Poisson()); }
  public static Poisson getRootAsPoisson(ByteBuffer _bb, Poisson obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; }
  public Poisson __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public double priorLambda() { int o = __offset(4); return o != 0 ? bb.getDouble(o + bb_pos) : 0.0; }
  public double proposalLambda() { int o = __offset(6); return o != 0 ? bb.getDouble(o + bb_pos) : 0.0; }

  public static int createPoisson(FlatBufferBuilder builder,
      double prior_lambda,
      double proposal_lambda) {
    builder.startObject(2);
    Poisson.addProposalLambda(builder, proposal_lambda);
    Poisson.addPriorLambda(builder, prior_lambda);
    return Poisson.endPoisson(builder);
  }

  public static void startPoisson(FlatBufferBuilder builder) { builder.startObject(2); }
  public static void addPriorLambda(FlatBufferBuilder builder, double priorLambda) { builder.addDouble(0, priorLambda, 0.0); }
  public static void addProposalLambda(FlatBufferBuilder builder, double proposalLambda) { builder.addDouble(1, proposalLambda, 0.0); }
  public static int endPoisson(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

