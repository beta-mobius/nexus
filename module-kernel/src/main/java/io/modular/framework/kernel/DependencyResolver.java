package io.modular.framework.kernel;

import java.util.*;

/**
 * 依赖解析器
 * 负责解析模块依赖关系，包括依赖传递、冲突检测、版本选择
 */
public class DependencyResolver {
    
    private final ModuleRegistry moduleRegistry;
    
    public DependencyResolver(ModuleRegistry moduleRegistry) {
        this.moduleRegistry = moduleRegistry;
    }
    
    /**
     * 解析模块依赖关系
     * 
     * @param moduleId 要解析的模块ID
     * @return 解析后的模块依赖图，包含所有传递依赖
     * @throws DependencyResolutionException 如果依赖解析失败
     */
    public DependencyGraph resolveDependencies(ModuleId moduleId) throws DependencyResolutionException {
        Module module = moduleRegistry.getModule(moduleId)
            .orElseThrow(() -> new DependencyResolutionException("Module not found: " + moduleId));
        
        DependencyGraph graph = new DependencyGraph();
        resolveDependenciesRecursive(module, graph, new HashSet<>());
        
        // 检测并解决版本冲突
        resolveVersionConflicts(graph);
        
        return graph;
    }
    
    /**
     * 递归解析依赖关系
     */
    private void resolveDependenciesRecursive(Module module, DependencyGraph graph, 
                                             Set<ModuleId> visited) throws DependencyResolutionException {
        if (visited.contains(module.getId())) {
            return; // 避免循环依赖
        }
        
        visited.add(module.getId());
        graph.addModule(module);
        
        // 解析直接依赖
        for (ModuleDependency dependency : module.getDependencyObjects()) {
            // 跳过可选依赖（如果找不到）
            if (dependency.isOptional()) {
                try {
                    resolveDependency(dependency, module, graph, visited);
                } catch (DependencyResolutionException e) {
                    // 可选依赖找不到是可以接受的
                    System.out.println("Optional dependency not found: " + dependency);
                }
            } else {
                // 必需依赖必须找到
                resolveDependency(dependency, module, graph, visited);
            }
        }
        
        visited.remove(module.getId());
    }
    
    /**
     * 解析单个依赖
     */
    private void resolveDependency(ModuleDependency dependency, Module module, 
                                  DependencyGraph graph, Set<ModuleId> visited) 
            throws DependencyResolutionException {
        
        // 查找符合版本范围的模块
        List<Module> candidates = findCompatibleModules(dependency.getModuleName(), 
                                                       dependency.getVersionRange());
        
        if (candidates.isEmpty()) {
            throw new DependencyResolutionException(
                "No compatible module found for dependency: " + dependency);
        }
        
        // 选择最佳版本（最新稳定版本）
        Module selected = selectBestVersion(candidates);
        graph.addDependency(module.getId(), selected.getId());
        
        // 如果是传递依赖，递归解析依赖的依赖
        if (dependency.isTransitive()) {
            resolveDependenciesRecursive(selected, graph, visited);
        }
    }
    
    /**
     * 查找符合版本范围的模块
     */
    private List<Module> findCompatibleModules(String moduleName, VersionRange versionRange) {
        List<Module> candidates = new ArrayList<>();
        
        // 从注册表中查找所有同名模块
        for (Module module : moduleRegistry.getAllModules()) {
            if (module.getName().equals(moduleName) && versionRange.contains(module.getId().getVersion())) {
                candidates.add(module);
            }
        }
        
        // 按版本排序（从高到低）
        candidates.sort((a, b) -> b.getId().getVersion().compareTo(a.getId().getVersion()));
        return candidates;
    }
    
    /**
     * 查找符合版本范围的模块（字符串版本，用于兼容性）
     */
    private List<Module> findCompatibleModules(String moduleName, String versionRangeStr) {
        return findCompatibleModules(moduleName, VersionRange.parse(versionRangeStr));
    }
    
    /**
     * 选择最佳版本
     * 优先选择稳定版本，然后选择最新版本
     */
    private Module selectBestVersion(List<Module> candidates) {
        // 首先尝试选择稳定版本
        for (Module module : candidates) {
            if (module.getId().getVersion().isStable()) {
                return module;
            }
        }
        
        // 如果没有稳定版本，返回最新版本（列表已排序）
        return candidates.get(0);
    }
    
    /**
     * 解决版本冲突
     */
    private void resolveVersionConflicts(DependencyGraph graph) throws DependencyResolutionException {
        Map<String, List<Module>> modulesByName = new HashMap<>();
        
        // 按模块名称分组
        for (Module module : graph.getAllModules()) {
            modulesByName.computeIfAbsent(module.getName(), k -> new ArrayList<>())
                        .add(module);
        }
        
        // 检查每个模块组是否有版本冲突
        List<String> conflicts = new ArrayList<>();
        for (Map.Entry<String, List<Module>> entry : modulesByName.entrySet()) {
            String moduleName = entry.getKey();
            List<Module> versions = entry.getValue();
            
            if (versions.size() > 1) {
                // 有多个版本，检查是否都是相同版本
                boolean hasDifferentVersions = false;
                Version firstVersion = versions.get(0).getId().getVersion();
                
                for (int i = 1; i < versions.size(); i++) {
                    if (!versions.get(i).getId().getVersion().equals(firstVersion)) {
                        hasDifferentVersions = true;
                        break;
                    }
                }
                
                if (hasDifferentVersions) {
                    conflicts.add(moduleName + ": " + versions.stream()
                        .map(m -> m.getId().getVersion().toString())
                        .reduce((a, b) -> a + ", " + b)
                        .orElse(""));
                }
            }
        }
        
        if (!conflicts.isEmpty()) {
            throw new DependencyResolutionException("Version conflicts detected:\n" + 
                String.join("\n", conflicts));
        }
    }
    
    /**
     * 获取模块的拓扑排序
     * 返回依赖顺序（被依赖的模块在前，依赖的模块在后）
     */
    public List<Module> getTopologicalOrder(ModuleId moduleId) throws DependencyResolutionException {
        DependencyGraph graph = resolveDependencies(moduleId);
        return topologicalSort(graph);
    }
    
    /**
     * 拓扑排序
     */
    private List<Module> topologicalSort(DependencyGraph graph) {
        List<Module> result = new ArrayList<>();
        Set<ModuleId> visited = new HashSet<>();
        Set<ModuleId> temp = new HashSet<>(); // 用于检测环
        
        for (Module module : graph.getAllModules()) {
            if (!visited.contains(module.getId())) {
                topologicalSortVisit(graph, module.getId(), visited, temp, result);
            }
        }
        
        Collections.reverse(result); // 反转得到正确的依赖顺序
        return result;
    }
    
    private void topologicalSortVisit(DependencyGraph graph, ModuleId moduleId, 
                                     Set<ModuleId> visited, Set<ModuleId> temp, 
                                     List<Module> result) {
        if (temp.contains(moduleId)) {
            throw new IllegalStateException("Circular dependency detected involving module: " + moduleId);
        }
        
        if (visited.contains(moduleId)) {
            return;
        }
        
        temp.add(moduleId);
        
        for (ModuleId depId : graph.getDependencies(moduleId)) {
            topologicalSortVisit(graph, depId, visited, temp, result);
        }
        
        temp.remove(moduleId);
        visited.add(moduleId);
        graph.getModule(moduleId).ifPresent(result::add);
    }
    
    /**
     * 依赖图类
     */
    public static class DependencyGraph {
        private final Map<ModuleId, Module> modules = new HashMap<>();
        private final Map<ModuleId, Set<ModuleId>> dependencies = new HashMap<>();
        
        public void addModule(Module module) {
            modules.put(module.getId(), module);
            dependencies.computeIfAbsent(module.getId(), k -> new HashSet<>());
        }
        
        public void addDependency(ModuleId from, ModuleId to) {
            dependencies.computeIfAbsent(from, k -> new HashSet<>()).add(to);
        }
        
        public Optional<Module> getModule(ModuleId moduleId) {
            return Optional.ofNullable(modules.get(moduleId));
        }
        
        public Set<ModuleId> getDependencies(ModuleId moduleId) {
            return dependencies.getOrDefault(moduleId, Collections.emptySet());
        }
        
        public Collection<Module> getAllModules() {
            return modules.values();
        }
        
        public boolean containsModule(ModuleId moduleId) {
            return modules.containsKey(moduleId);
        }
    }
    
    /**
     * 依赖解析异常
     */
    public static class DependencyResolutionException extends Exception {
        public DependencyResolutionException(String message) {
            super(message);
        }
        
        public DependencyResolutionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}