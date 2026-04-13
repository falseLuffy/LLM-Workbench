# 技术设计方案 (TDR) - 基于 Rust & Tauri 的打印系统

## 1. 架构设计
采用 **Tauri** 框架进行开发：
*   **Frontend**: Vue 3 + Tailwind CSS (构建用户交互界面)。
*   **Backend**: Rust (负责文件系统 IO、调用 Windows Shell API、管理并发任务)。
*   **Communication**: 通过 Tauri 的 IPC (Invoke) 机制进行 JSON-RPC 协议通信。

## 2. 核心技术栈
*   **Runtime**: [Tauri](https://tauri.app/)
*   **Windows API**: [`windows-rs`](https://github.com/microsoft/windows-rs) (访问原生系统调用)
*   **并发管理**: [`tokio`](https://tokio.rs/) (处理异步任务流)
*   **对话框**: `@tauri-apps/api/dialog`

## 3. 详细实现方案

### 3.1 打印分发逻辑 (Print Dispatcher)
Rust 后端接收到路径列表后，根据扩展名分流：

#### 方案 A：通用 ShellExecute 打印 (适用于 PDF, Office)
利用 Windows Shell 关联的 "print" 动词。
```rust
// 使用 windows-rs 调用 ShellExecuteW
ShellExecuteW(
    HWND(0),
    w!("print"),     // 操作：打印
    w!(file_path),   // 文件路径
    PCWSTR::null(),
    PCWSTR::null(),
    SW_HIDE,         // 隐藏启动窗口
);
```

#### 方案 B：图片原生打印 (适用于 JPG, PNG)
虽然 ShellExecute 支持图片打印，但通常会弹出“照片打印向导”。
若追求无感打印，技术方案如下：
1.  使用 `GDI+` 或 `Print Document API`。
2.  将图片加载为 `BitMap`。
3.  通过 `StartDoc` / `StartPage` 构建打印作业。
4.  将图片绘制到打印机 `HDC` 设备上下文。

### 3.2 任务调度模型
1.  **生产者-消费者模型**: 维护一个 `Arc<Mutex<VecDeque<Task>>>`。
2.  **串行打印隔离**: 由于部分软件（如 Word）不支持并行的打印实例，后端需强制执行“先进先出”的串行打印，每个任务间隔 500ms-1s 以防止假死。

### 3.3 状态流转
*   `Pending` -> `Processing` -> `Completed` / `Failed(ErrorInfo)`

## 4. 异常处理
*   **Office 缺失**: 捕获 `SE_ERR_NOASSOC` 错误并提示用户安装对应软件。
*   **权限问题**: 检测文件是否被其他进程占用。
*   **打印机脱机**: 监控 `GetPrintProcessorDirectory` 相关的假脱机状态（可选进阶功能）。

## 5. 安全性考虑
*   **作用域限制**: Tauri 仅允许访问用户选择的合法路径。
*   **内存安全**: 利用 Rust 的所有权模型防止多线程下的数据竞争。
