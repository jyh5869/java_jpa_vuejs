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
import com.example.java_jpa_vuejs.geomBoard.BoardDto;
import com.example.java_jpa_vuejs.geomBoard.entity.GeometryBoard;
import com.example.java_jpa_vuejs.tensorFlow.entity.Roads;
import com.example.java_jpa_vuejs.tensorFlow.model.RoadDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.net.URLDecoder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HelloTensorFlow {

    final private static Logger LOG = Logger.getGlobal();

    private final TensorDataService tensorDataService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    /**
    * @method 클라우드를 통한 리스트 호출
    * @param  null
    * @throws Exception
    */
    @GetMapping("/noAuth/getSearchAddr")
    public Map<String, Object> index(@Valid PaginationDto paginationDto) throws Exception {
        Map<String, Object> retMap = new HashMap<String, Object>();
        List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();


        
        Iterable<Roads> list = tensorDataService.getAddrData();
        
        Iterator<Roads> itr = list.iterator();

        //리턴 리스트에 담기위한 형식변경
        int idx = 0;
        while(itr.hasNext()){ 
            idx++;
            Roads RoadEntity = itr.next();
            RoadDTO boardDto = RoadEntity.toDto(RoadEntity);
            System.out.println(idx + "      - "+boardDto.getRn());
            Map<String, Object> map = objectMapper.convertValue(boardDto, new TypeReference<Map<String, Object>>() {});

            retList.add(map);
        }


        

        System.out.println("Hello TensorFlow " + TensorFlow.version());

        try (ConcreteFunction dbl = ConcreteFunction.create(HelloTensorFlow::dbl);
            Tensor<TInt32> x = TInt32.scalarOf(10);
            Tensor<TInt32> dblX = dbl.call(x).expect(TInt32.DTYPE)) {
            System.out.println(x.data().getInt() + " doubled is " + dblX.data().getInt());
        }



        retMap.put("list", retList);

        return retMap;
    }

    private static Signature dbl(Ops tf) {
        Placeholder<TInt32> x = tf.placeholder(TInt32.DTYPE);
        Add<TInt32> dblX = tf.math.add(x, x);
        return Signature.builder().input("x", x).output("dbl", dblX).build();
    }

}