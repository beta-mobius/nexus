package com.example.user;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务实现
 */
public class UserServiceImpl implements UserService {
    
    private final Map<String, User> userStore = new HashMap<>();
    private int nextUserId = 1;
    
    @Override
    public User getUser(String userId) {
        return userStore.get(userId);
    }
    
    @Override
    public String createUser(User user) {
        String userId = "user-" + nextUserId++;
        user.setUserId(userId);
        userStore.put(userId, user);
        return userId;
    }
    
    @Override
    public void updateUser(User user) {
        if (user.getUserId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        userStore.put(user.getUserId(), user);
    }
    
    @Override
    public void deleteUser(String userId) {
        userStore.remove(userId);
    }
}