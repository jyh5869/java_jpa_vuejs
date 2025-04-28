package com.example.java_jpa_vuejs.auth.repositoryService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;

/** mTxId ↔ redirectTo 를 저장하는 단순 매핑 서비스 */
@Service
public class RedirectService {
    private final ConcurrentMap<String,String> map = new ConcurrentHashMap<>();

    public void save(String mTxId, String redirectTo) {
        map.put(mTxId, redirectTo);
    }

    public String remove(String mTxId) {
        return map.remove(mTxId);
    }
}