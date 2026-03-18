package com.example.user;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUser(String userId);
    
    /**
     * 创建用户
     * @param user 用户信息
     * @return 创建的用户ID
     */
    String createUser(User user);
    
    /**
     * 更新用户
     * @param user 用户信息
     */
    void updateUser(User user);
    
    /**
     * 删除用户
     * @param userId 用户ID
     */
    void deleteUser(String userId);
}