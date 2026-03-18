# Nexus Framework 模块化桥接框架

![Java Version](https://img.shields.io/badge/Java-11%2B-blue)
![License](https://img.shields.io/badge/License-MIT-green)
![Maven](https://img.shields.io/badge/Maven-3.6%2B-orange)

## 项目概述

Nexus Framework 是一个与Spring解耦的模块化桥接框架，提供模块加载、服务管理、依赖注入等功能，支持多框架适配器。框架名"Nexus"寓意连接点和枢纽，体现连接业务模块与底层框架的核心功能。

### 核心价值
- **🚀 框架解耦**：业务代码不直接依赖Spring，通过抽象接口访问框架功能
- **🧩 模块化设计**：支持模块独立开发、测试、部署和热更新
- **🔌 多驱动支持**：提供Spring驱动和轻量级驱动（无Spring依赖）
- **⚡ 性能优化**：模块级类加载缓存，支持懒加载策略
- **🔧 易于扩展**：支持自定义驱动实现，可扩展新的服务接口

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

## 📚 详细使用指南

### 核心概念

#### 1. 模块 (Module)
模块是Nexus Framework的基本单元，包含：
- **模块标识符**：唯一标识（名称+版本）
- **依赖声明**：可选的模块依赖关系
- **服务注册**：模块提供的服务接口
- **生命周期**：安装→解析→激活→停止

#### 2. 模块上下文 (ModuleContext)
模块运行时环境，提供：
- **服务注册/查找**：`registerService()`, `getService()`
- **框架服务访问**：`getBeanContainer()`, `getTransactionManager()`等
- **模块信息**：`getModule()`, `getClassLoader()`

#### 3. 框架驱动 (FrameworkDriver)
连接业务模块与底层框架的适配器：
- **Spring驱动**：`SpringFrameworkDriver` - 完整Spring生态集成
- **轻量级驱动**：`LightweightFrameworkDriver` - 无Spring依赖的简单实现

### API使用示例

#### 1. 创建业务模块
```java
package com.example.product;

import com.mobius.nexus.kernel.*;
import com.mobius.nexus.api.BeanContainer;

public class ProductModule implements ModuleActivator {
    private ProductService productService;
    
    @Override
    public void start(ModuleContext context) {
        // 初始化服务
        productService = new ProductServiceImpl();
        
        // 注册服务到模块上下文
        context.registerService(productService, ProductService.class);
        
        // 获取框架服务（如Bean容器）
        BeanContainer container = context.getBeanContainer();
        if (container != null) {
            // 使用框架服务
            ProductRepository repo = container.getBean(ProductRepository.class);
            productService.setRepository(repo);
        }
        
        System.out.println("产品模块已启动");
    }
    
    @Override
    public void stop(ModuleContext context) {
        // 清理资源
        if (productService != null) {
            productService.shutdown();
        }
        System.out.println("产品模块已停止");
    }
}
```

#### 2. 模块配置（module.properties）
```properties
# META-INF/module.properties
module.name=com.example.product
module.version=1.0.0
module.type=business
module.description=产品管理模块

# 依赖声明（支持版本范围）
module.dependencies=com.example.user:[1.0,2.0)?,com.mobius.nexus.kernel:[1.0,2.0)

# 导出的包（其他模块可访问）
module.exports=com.example.product

# 模块入口类
module.activator=com.example.product.ProductModule
```

#### 3. 模块加载和管理
```java
import com.mobius.nexus.kernel.*;
import java.nio.file.*;

public class Application {
    public static void main(String[] args) {
        // 1. 创建模块注册表和仓库
        ModuleRegistry registry = new SimpleModuleRegistry();
        ModuleRepository repository = new SimpleModuleRepository(Paths.get("modules"));
        
        // 2. 创建模块加载器
        ModuleLoader loader = new ModuleLoader(registry, repository);
        
        // 3. 加载模块
        Path moduleJar = Paths.get("product-module-1.0.0.jar");
        try {
            Module module = loader.loadModule(moduleJar);
            
            // 4. 解析依赖（自动处理依赖关系）
            loader.resolveDependencies(module.getId());
            
            // 5. 启动模块
            module.start();
            
            // 6. 使用模块服务
            ProductService service = module.getService(ProductService.class);
            if (service != null) {
                List<Product> products = service.getAllProducts();
                System.out.println("获取到 " + products.size() + " 个产品");
            }
            
            // 7. 停止模块（优雅关闭）
            module.stop();
            
        } catch (ModuleException e) {
            System.err.println("模块加载失败: " + e.getMessage());
        }
    }
}
```

#### 4. 依赖解析示例
```java
// 创建依赖解析器
DependencyResolver resolver = new DependencyResolver();

// 定义模块依赖
Map<String, String> dependencies = new HashMap<>();
dependencies.put("com.example.user", "[1.0,2.0)");  // 用户模块，版本1.0到2.0（不包括2.0）
dependencies.put("com.example.util", "[2.5,3.0)?"); // 工具模块，可选依赖

// 解析依赖
List<ModuleDependency> resolved = resolver.resolveDependencies(dependencies);
for (ModuleDependency dep : resolved) {
    System.out.println("依赖: " + dep.getName() + 
                      " 版本: " + dep.getVersionRange() +
                      " 可选: " + dep.isOptional());
}
```

### 框架驱动配置

#### Spring驱动配置
```java
import com.mobius.nexus.driver.*;

// 1. 创建驱动配置
DriverConfig config = new DriverConfig();
config.setDriverType("spring");
config.setProperty("spring.context", "classpath:applicationContext.xml");
config.setProperty("spring.profiles.active", "dev");
config.setProperty("spring.config.location", "classpath:/,file:./config/");

// 2. 初始化Spring驱动
FrameworkDriver driver = new SpringFrameworkDriver();
driver.initialize(config);

// 3. 获取框架服务
BeanContainer container = driver.getBeanContainer();
TransactionManager txManager = driver.getTransactionManager();
EventPublisher eventPublisher = driver.getEventPublisher();
AspectManager aspectManager = driver.getAspectManager();

// 4. 在模块中使用
ModuleContext context = ...; // 模块上下文
context.setBeanContainer(container);
context.setTransactionManager(txManager);
```

#### 轻量级驱动配置
```java
// 1. 创建轻量级驱动配置
DriverConfig config = new DriverConfig();
config.setDriverType("lightweight");
config.setProperty("bean.container.type", "simple");  // 使用简单Bean容器
config.setProperty("transaction.enabled", "true");    // 启用事务支持
config.setProperty("event.publisher.enabled", "true"); // 启用事件发布

// 2. 初始化轻量级驱动
FrameworkDriver driver = new LightweightFrameworkDriver();
driver.initialize(config);

// 3. 注册自定义Bean
BeanContainer container = driver.getBeanContainer();
container.registerBean("userService", new UserServiceImpl());
container.registerBean("productService", new ProductServiceImpl());

// 4. 使用AOP功能
AspectManager aspectManager = driver.getAspectManager();
aspectManager.addAdvisor(new LoggingAdvisor()); // 添加日志切面
```

### 高级特性

#### 1. 事务管理
```java
// 编程式事务
TransactionManager txManager = context.getTransactionManager();
TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());

try {
    // 业务操作
    userService.createUser(user);
    orderService.createOrder(order);
    
    // 提交事务
    txManager.commit(status);
} catch (Exception e) {
    // 回滚事务
    txManager.rollback(status);
    throw e;
}

// 声明式事务（通过AOP）
@Transactional
public void createUserWithOrder(User user, Order order) {
    userService.createUser(user);
    orderService.createOrder(order);
}
```

#### 2. 事件发布/订阅
```java
// 发布事件
EventPublisher publisher = context.getEventPublisher();
publisher.publishEvent(new UserCreatedEvent(user));

// 订阅事件
@Component
public class UserEventListener {
    @EventListener
    public void handleUserCreated(UserCreatedEvent event) {
        System.out.println("用户创建事件: " + event.getUser().getName());
        // 发送欢迎邮件等
    }
}
```

#### 3. AOP切面编程
```java
// 定义切面
public class LoggingAspect implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            System.out.println("方法调用开始: " + invocation.getMethod().getName());
            return invocation.proceed();
        } finally {
            long duration = System.currentTimeMillis() - start;
            System.out.println("方法调用结束: " + invocation.getMethod().getName() + 
                             ", 耗时: " + duration + "ms");
        }
    }
}

// 注册切面
AspectManager aspectManager = context.getAspectManager();
aspectManager.addAdvisor(new PointcutAdvisor(
    new AnnotationMatchingPointcut(Transactional.class),
    new LoggingAspect()
));
```

#### 4. 定时任务调度
```java
Scheduler scheduler = context.getScheduler();

// 注册定时任务
ScheduledTask task = scheduler.scheduleAtFixedRate(
    () -> {
        System.out.println("定时任务执行: " + new Date());
        // 执行清理、统计等后台任务
    },
    1000,  // 初始延迟1秒
    5000   // 每5秒执行一次
);

// 取消任务
task.cancel();
```

### 最佳实践

#### 1. 模块设计原则
- **单一职责**：每个模块专注于一个业务领域
- **明确依赖**：清晰声明模块依赖关系
- **接口隔离**：通过接口暴露功能，隐藏实现细节
- **版本管理**：遵循语义化版本规范

#### 2. 配置管理
- **环境分离**：开发、测试、生产环境使用不同配置
- **外部化配置**：配置文件外置，便于部署
- **配置验证**：启动时验证配置有效性

#### 3. 错误处理
- **模块异常**：使用ModuleException包装模块相关错误
- **优雅降级**：可选依赖不存在时提供降级方案
- **日志记录**：详细记录模块生命周期事件

#### 4. 性能优化
- **懒加载**：服务按需初始化
- **缓存策略**：合理使用缓存提升性能
- **资源清理**：及时释放模块占用的资源

## 性能考虑

### 类加载优化
- 模块级ClassLoader缓存
- 依赖共享机制
- 懒加载策略

### 资源管理
- 模块资源隔离
- 内存泄漏防护
- 垃圾回收优化

## 🚀 发展路线图

### ✅ 已完成
1. **核心模块化架构**：模块加载、生命周期管理、类加载隔离
2. **服务抽象层**：31个零Spring依赖的服务接口
3. **Spring驱动**：完整Spring生态集成适配器
4. **轻量级驱动**：无Spring依赖的简单实现（已完成）
5. **工具链**：模块打包工具（ModulePackager）
6. **示例模块**：用户管理和订单管理完整示例

### 🎯 短期目标（1-3个月）
1. **单元测试覆盖**：为所有核心模块添加完整单元测试
2. **CI/CD流水线**：配置GitHub Actions自动构建和测试
3. **文档完善**：完善API文档和用户指南
4. **性能优化**：进一步优化类加载和内存管理

### 🌟 中期目标（3-6个月）
1. **扩展驱动支持**：支持Quarkus、Micronaut等现代框架
2. **监控和管理**：提供模块运行时监控和管理工具
3. **云原生适配**：优化容器化部署和Kubernetes集成
4. **开发者工具**：IDE插件和CLI工具支持

### 🚀 长期愿景（6-12个月）
1. **模块生态系统**：构建模块市场和社区生态
2. **企业级特性**：多租户、权限管理、审计日志等
3. **云服务平台**：提供托管版Nexus云服务
4. **标准化推广**：推动成为模块化架构标准实践

## 🤝 贡献指南

我们欢迎并感谢所有形式的贡献！以下是参与Nexus Framework开发的步骤：

### 开始贡献

#### 1. 开发环境准备
- **JDK 11+**：确保已安装Java开发工具包
- **Maven 3.6+**：项目使用Maven进行构建
- **Git**：版本控制系统
- **IDE推荐**：IntelliJ IDEA、Eclipse或VS Code

#### 2. 设置开发环境
```bash
# 1. Fork项目到您的GitHub账户
# 2. 克隆您的fork到本地
git clone https://github.com/your-username/nexus.git
cd nexus

# 3. 添加上游仓库
git remote add upstream https://github.com/beta-mobius/nexus.git

# 4. 构建项目
mvn clean compile

# 5. 运行测试
mvn test
```

#### 3. 选择贡献类型

##### 代码贡献
- **修复Bug**：查看[Issues](https://github.com/beta-mobius/nexus/issues)中的bug报告
- **新功能**：在开始前请先创建Issue讨论功能设计
- **性能优化**：优化算法、内存使用或启动时间
- **测试覆盖**：添加缺失的单元测试或集成测试

##### 文档贡献
- **完善文档**：改进README、API文档或教程
- **翻译**：将文档翻译为其他语言
- **示例代码**：创建更多使用示例和演示项目

##### 社区支持
- **回答问题**：在Issues或讨论区帮助其他用户
- **代码审查**：审查Pull Request并提供反馈
- **推广分享**：分享使用经验或写博客文章

### 开发流程

#### 1. 创建分支
```bash
# 从master创建功能分支
git checkout -b feature/your-feature-name

# 或修复bug分支
git checkout -b fix/issue-number-description
```

#### 2. 编码规范
- **代码风格**：遵循Java编码规范，使用4空格缩进
- **注释**：为公共API添加JavaDoc注释
- **测试**：新功能必须包含单元测试
- **提交信息**：使用清晰的提交信息，格式为：`类型: 描述`

提交类型：
- `feat`: 新功能
- `fix`: bug修复
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建工具或依赖更新

#### 3. 提交更改
```bash
# 添加更改
git add .

# 提交更改
git commit -m "feat: 添加用户管理模块缓存支持"

# 推送到您的fork
git push origin feature/your-feature-name
```

#### 4. 创建Pull Request
1. 访问您的GitHub仓库
2. 点击"Compare & pull request"
3. 填写PR描述，说明：
   - 解决的问题或添加的功能
   - 测试方法
   - 相关Issue编号
4. 等待代码审查和CI测试

### 质量要求

#### 代码质量
- **代码覆盖率**：新代码应达到80%以上的测试覆盖率
- **静态分析**：通过Checkstyle、PMD等代码质量检查
- **性能影响**：重大更改需进行性能测试

#### 文档要求
- **API文档**：公共API必须包含JavaDoc
- **更新日志**：重大更改需更新CHANGELOG.md
- **示例代码**：复杂功能应提供使用示例

#### 测试要求
- **单元测试**：使用JUnit 5编写测试
- **集成测试**：涉及多个模块的功能需要集成测试
- **边缘情况**：测试边界条件和错误处理

### 获取帮助

- **Issues**：报告bug或请求功能
- **讨论区**：讨论设计思路或提出问题
- **邮件列表**：通过邮件参与讨论（待建立）

### 行为准则

我们遵循[贡献者公约](https://www.contributor-covenant.org/version/2/0/code_of_conduct/)行为准则，请确保：
- 尊重所有社区成员
- 建设性的沟通和反馈
- 包容不同的观点和经验

## 📄 许可证

Nexus Framework 采用 **MIT License** 开源许可证。

### 许可证摘要
- ✅ **允许**：商业使用、修改、分发、私人使用
- ✅ **要求**：包含版权声明和许可证
- ✅ **允许**：专利使用
- ❌ **不保证**：无担保责任
- ❌ **不承担**：作者不承担损害赔偿责任

### 完整许可证
详见 [LICENSE](LICENSE) 文件。

### 第三方依赖
部分第三方库可能使用不同的许可证，请参考各依赖的许可证声明。

## 📞 联系与支持

- **GitHub Issues**：[问题报告和功能请求](https://github.com/beta-mobius/nexus/issues)
- **文档网站**：[待建立]()
- **社区论坛**：[待建立]()
- **邮件联系**：contact@mobius-team.org（待建立）

## 🙏 致谢

感谢所有为Nexus Framework做出贡献的开发者！

### 核心贡献者
- **李真人** - 项目发起人和架构师
- **秦琼 (Clawdbot)** - 核心开发和文档编写

### 特别感谢
- Spring Framework团队提供的优秀框架基础
- Apache Maven项目提供的构建工具
- 所有开源社区成员的支持和贡献

---
*最后更新：2026年3月18日*