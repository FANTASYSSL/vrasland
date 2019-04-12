## 关于 VRASLAND

> 来试一下用搭建 FTP 的方式搭建 API 吧！
> 这是一个专注于 RESTful API 的 Webapp 框架，提供基于路径的 API 管理，无需注册路由，直接按照 API 的模样搭建路径即可。

## 如何使用

### 1. 同步该库
  > git clone https://github.com/JuerGenie/vransland

### 2. 依据 REST API 的结构，创建文件夹
  ```
  示例：
  
  |-+ /project
    |-- <other project file>
    |-+ /restful_api
      |-- users.lua
      |-+ /users
        |-- {users}.lua
  ```
  > ~~注：{users}.lua 形式的API的支持尚未实现，但已在未来的更新计划中。~~\
  > 已于 1.0.0.8-SNAPSHOT 版本之后支持 {index}.lua 形式的请求路径解析。
  
### 3. 在相应位置填入对应脚本
  > 如同 step.2 中所示那样，脚本内容如下：
  ```lua
  -- users.lua
  -- 这是一个get api，将会响应对于 /users 的 get 请求
  function get()
    local users = {
      {name = "Tom", age = 21, gender = 0},
      {name = "Shelly", age = 19, gender = 1}
    }
    return users, 'get success', 200  -- 返回值为如下结构： <result object>, [result message, [response status]]
  end
  ```
  
### 4. 启动库
  > 若使用 jar 包形式，请使用：`java -jar --staticPath="./restful_api" vrasland.jar`

### 5. 尝试在浏览器上进行访问
  ```
  >>> get http://localhost:8080/users
  
  {
    "statue": "ok",
    "message": "get success!",
    "meta": null,
    "data": {
      "1": {
        "name": "Tom",
        "age": 21,
        "gender": 0
      },
      "2": {
        "name": "Shelly",
        "age": 19,
        "gender": 1
      }
    }
  }
  ```
  > 注：由于 Lua 中并不存在真正意义上的 List，因此返回后的值最后会转变成对象形式。所幸的是，这个形式在使用上并不会对JS造成太大的影响（除了无法使用 fori 循环，以及 Lua 的 index 是从1开始，后者可以解决）。后续将会尝试解决这个问题，在问题被解决之前，推荐使用以下形式来绕过这个问题：
  ```lua
  -- 使用java的List
  local result = luajava.newInstance("java.util.ArrayList")
  result:add({...})
  return result, '...', 200
  ```
