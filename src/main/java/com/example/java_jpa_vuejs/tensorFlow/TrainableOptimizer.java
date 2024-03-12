package com.example.java_jpa_vuejs.tensorFlow;

import org.tensorflow.Operand;
import org.tensorflow.op.Ops;
import org.tensorflow.op.core.Assign;
import org.tensorflow.types.TFloat32;

public class TrainableOptimizer<S> {

    public TrainableOptimizer(Ops tf, float learningRate) {
        //TODO Auto-generated constructor stub
    }

    public Operand<TFloat32> minimize(Operand<TFloat32> loss, Assign<TFloat32> assign) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'minimize'");
    }

}
