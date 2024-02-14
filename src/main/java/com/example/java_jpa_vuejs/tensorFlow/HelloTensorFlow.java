package com.example.java_jpa_vuejs.tensorFlow;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tensorflow.ConcreteFunction;
import org.tensorflow.Signature;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;
import org.tensorflow.op.Ops;
import org.tensorflow.op.core.Placeholder;
import org.tensorflow.op.math.Add;
import org.tensorflow.types.TInt32;

import com.example.java_jpa_vuejs.auth.repositoryService.SignService;
import com.example.java_jpa_vuejs.common.PaginationDto;
import com.example.java_jpa_vuejs.geomBoard.entity.GeometryBoard;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.net.URLDecoder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HelloTensorFlow {

    final private static Logger LOG = Logger.getGlobal();

    private final TensorDataService tensorDataService;

    /**
    * @method 클라우드를 통한 리스트 호출
    * @param  null
    * @throws Exception
    */
    @GetMapping("/setTensorFlow")
    public Map<String, Object> index(@Valid PaginationDto paginationDto) throws Exception {

        tensorDataService.getAddrData();

        Map<String, Object> retMap = new HashMap<String, Object>();

        System.out.println("Hello TensorFlow " + TensorFlow.version());

        try (ConcreteFunction dbl = ConcreteFunction.create(HelloTensorFlow::dbl);
            Tensor<TInt32> x = TInt32.scalarOf(10);
            Tensor<TInt32> dblX = dbl.call(x).expect(TInt32.DTYPE)) {
            System.out.println(x.data().getInt() + " doubled is " + dblX.data().getInt());
        }

        return retMap;
    }

    private static Signature dbl(Ops tf) {
        Placeholder<TInt32> x = tf.placeholder(TInt32.DTYPE);
        Add<TInt32> dblX = tf.math.add(x, x);
        return Signature.builder().input("x", x).output("dbl", dblX).build();
    }

}