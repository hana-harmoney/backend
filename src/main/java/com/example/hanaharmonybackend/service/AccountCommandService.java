package com.example.hanaharmonybackend.service;

import com.example.hanaharmonybackend.domain.Account;
import com.example.hanaharmonybackend.domain.User;

public interface AccountCommandService {
  Account createFor(User user);
  void delete(Long accountId, Long requesterId);
}
