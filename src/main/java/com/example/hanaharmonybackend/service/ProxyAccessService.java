package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.domain.ProxyAccess;

public interface ProxyAccessService {
    ProxyAccess createForProfileCreate(Long ownerUserId);

    ProxyAccess validateUsable(String token);

    void markUsed(ProxyAccess pa);
}

