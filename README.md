# Nexus Framework 模块化桥接框架

## 项目概述

Nexus Framework 是一个与Spring解耦的模块化桥接框架，提供模块加载、服务管理、依赖注入等功能，支持多框架适配器。框架名"Nexus"寓意连接点和枢纽，体现连接业务模块与底层框架的核心功能。

## 项目结构

```
D:\workspace\code\
├── nexus-kernel\          # Nexus内核层
├── nexus-api\             # Nexus服务接口（零Spring依赖）
├── nexus-driver-spring\   # Nexus Spring驱动层
├── nexus-driver-lightweight\ # Nexus轻量级驱动（无Spring依赖）
├── business-modules\      # 示例业务模块
│   ├── example-user\     # 用户管理示例模块
│   └── example-order\    # 订单管理示例模块
├── nexus-tools\          # Nexus开发工具链
├── test\                 # 测试套件
└── pom.xml              # Maven父项目配置
```

## 核心特性

### 1. 模块化核心
- **模块加载**：从JAR文件加载模块
- **类加载隔离**：每个模块独立的ClassLoader
- **模块生命周期**：INSTALLED → RESOLVED → ACTIVE → STOPPED
- **依赖解析**：支持版本范围依赖
- **ModuleActivator**：模块启动/停止回调接口

### 2. 服务抽象层
- **零Spring依赖**：纯接口定义，不与任何框架绑定
- **统一接口**：
  - `BeanContainer`：Bean容器接口
  - `TransactionManager`：事务管理器
  - `EventPublisher`：事件发布器
  - `AspectManager`：切面管理器
  - `Scheduler`：调度器
  - `Validator`：验证器

### 3. 框架驱动

#### Spring驱动
- **SpringFrameworkDriver**：Spring框架适配器
- **适配器模式**：
  - `SpringBeanContainer`：Spring容器适配器
  - `SpringTransactionManager`：Spring事务适配器
  - `SpringEventPublisher`：Spring事件适配器
  - `SpringAspectManager`：Spring AOP适配器
  - `SpringScheduler`：Spring调度适配器
  - `SpringValidator`：Spring验证适配器

#### 轻量级驱动（无Spring依赖）
- **LightweightFrameworkDriver**：轻量级实现，适用于小型应用
- **简单实现**：
  - `SimpleBeanContainer`：基于Map的简单容器
  - `SimpleTransactionManager`：简单事务管理
  - `SimpleEventPublisher`：简单事件发布
  - `SimpleAspectManager`：基于动态代理的AOP
  - `SimpleScheduler`：基于ScheduledExecutor的调度器
  - `SimpleValidator`：基本验证支持

#### 驱动选择
- **生产环境**：推荐Spring驱动，功能完整
- **测试环境**：推荐轻量级驱动，无外部依赖
- **自定义驱动**：实现FrameworkDriver接口扩展新框架

## 编译和运行

### 前提条件
1. **JDK 11+**：Java开发环境
2. **Maven 3.6+**：构建工具
3. **Windows/Linux/Mac**：操作系统

### 安装步骤

#### 1. 安装JDK
- 下载并安装 [OpenJDK 11](https://jdk.java.net/11/)
- 设置环境变量 `JAVA_HOME`
- 添加 `%JAVA_HOME%\bin` 到 PATH

#### 2. 安装Maven
- 下载并安装 [Apache Maven](https://maven.apache.org/download.cgi)
- 设置环境变量 `MAVEN_HOME`
- 添加 `%MAVEN_HOME%\bin` 到 PATH

#### 3. 验证安装
```bash
java -version
mvn -v
```

### 编译项目
```bash
# 进入项目目录
cd D:\workspace\code

# 编译整个项目
mvn clean compile

# 打包所有模块
mvn clean package
```

### 运行测试
```bash
# 运行测试套件
mvn test

# 运行主测试程序
cd test
mvn exec:java -Dexec.mainClass="test.TestFramework"
```

### 运行示例模块
```bash
# 打包示例模块
cd business-modules\example-user
mvn clean package

# 生成的JAR文件
target/example-user-1.0.0-SNAPSHOT.jar
```

## 快速开始

### 1. 创建模块
```java
public class UserModule implements ModuleActivator {
    @Override
    public void start(ModuleContext context) {
        // 注册服务
        UserService service = new UserServiceImpl();
        context.registerService(service, UserService.class);
        System.out.println("用户模块启动完成");
    }
    
    @Override
    public void stop(ModuleContext context) {
        System.out.println("用户模块停止");
    }
}
```

### 2. 模块描述文件
```properties
# META-INF/module.properties
module.name=com.example.user
module.version=1.0.0
module.description=用户管理模块
module.exports=com.example.user
module.activator=com.example.user.UserModule
```

### 3. 加载模块
```java
// 创建模块加载器
ModuleRegistry registry = new SimpleModuleRegistry();
SimpleModuleRepository repository = new SimpleModuleRepository(Paths.get("repository"));
ModuleLoader loader = new ModuleLoader(registry, repository);

// 加载模块
Path jarPath = Paths.get("example-user.jar");
Module module = loader.loadModule(jarPath);

// 启动模块
module.start();

// 使用模块服务
UserService service = module.getService(UserService.class);
```

## 与Spring集成

### 1. 使用Spring驱动
```java
// 创建Spring驱动
DriverConfig config = new DriverConfig();
config.setDriverType("spring");
config.setProperty("spring.context", "classpath:applicationContext.xml");

// 初始化驱动
FrameworkDriver driver = new SpringFrameworkDriver();
driver.initialize(config);

// 获取Spring服务
BeanContainer container = driver.getBeanContainer();
TransactionManager txManager = driver.getTransactionManager();
```

### 2. 模块使用Spring服务
```java
public class BusinessModule implements ModuleActivator {
    @Override
    public void start(ModuleContext context) {
        // 通过驱动访问Spring服务
        BeanContainer container = context.getBeanContainer();
        UserRepository userRepo = container.getBean(UserRepository.class);
        
        // 业务逻辑...
    }
}
```

## 优势特点

### ✅ 解耦架构
- 业务代码不直接依赖Spring
- 通过抽象接口访问框架功能
- 支持框架切换（Spring → 其他框架）

### ✅ 模块化设计
- 模块独立开发、测试、部署
- 支持模块热更新
- 类加载隔离，避免冲突

### ✅ 可扩展性
- 支持自定义驱动实现
- 可添加新的服务接口
- 支持插件化扩展

### ✅ 兼容性
- 支持现有Spring项目迁移
- 渐进式采用策略
- 与Spring生态兼容

## 性能考虑

### 类加载优化
- 模块级ClassLoader缓存
- 依赖共享机制
- 懒加载策略

### 资源管理
- 模块资源隔离
- 内存泄漏防护
- 垃圾回收优化

## 后续规划

### 短期目标
1. 完善依赖解析算法
2. 添加模块热更新支持
3. 优化类加载性能

### 中期目标
1. 实现轻量级驱动（不依赖Spring）
2. 支持多框架适配（Quarkus, Micronaut等）
3. 提供监控和管理工具

### 长期目标
1. 构建模块市场生态
2. 支持云原生部署
3. 提供IDE插件支持

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交代码变更
4. 创建Pull Request

## 许可证

MIT License