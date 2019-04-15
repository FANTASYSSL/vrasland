## 关于 VRASLAND

> 来试一下用搭建 FTP 的方式搭建 API 吧！
> 这是一个专注于 RESTful API 的 Webapp 框架，提供基于路径的 API 管理，无需注册路由，直接按照 API 的模样搭建路径即可。

## 如何使用

### 1. 同步该库
`git clone https://github.com/JuerGenie/vransland`

### 2. 依据 REST API 的结构，创建文件夹
  ```
  示例：
  
  --+ /project
    |
    +-- <other project file>
    |
    +-+ /api
      |
      +-- users.js
      |
      +-+ /users
        |
        +-- {index}.js
        |
        +-+ /{index}
          |
          +-- score.js
  ```
  > 已于 1.0(vl-k) 中支持复杂路径映射。

  文件名      | 对应路径              | 说明
  :----      | :----                | :----
  /api       | *                 | 这是一个文件夹，也是所有API的根目录，象征着 API 的 '/' 路径。<br />这个名称并不是固定的，你可以通过修改配置文件来进行定制。 
  users.js   | /users               | 这将是第一个处理脚本，用于处理类似 getAll、put 和 post 的针对 users 的请求。<br />看起来它就像是一个普通的资源？这就对了。 
  /users     | *                 | 这是针对该资源（Users）的更细分的资源，它就是一个文件夹，用于映射路径。
  {index}.js | /users/{index}       | 这是针对请求路径中的参数进行的映射，注意，'{index}' 这个名称是必须的。<br />若要进行映射，请将相应部分替换为 {index}。<br/>这些被匹配的参数会被放置到脚本中的全局变量 'args' 中。<br />args 将会是一个数组，具体可以通过 {index}.js 来看看该如何使用。 
  /{index}   | *                 | 与 {index}.js 类似，这个文件夹也将映射路径的一部分。<br />被匹配的参数同样会被（按顺序）放置进 args 中。 
  score.js   | /users/{index}/score | 这对应的是相匹配的资源下的细项资源。

### 3. 在相应位置填入对应脚本
  > 如同 step.2 中所示那样放置，脚本内容如下：
  >   ```js
  > // users.js
  > // 在 vl-k (vrasland-kotlin) 版本中，默认使用 javascript 作为处理脚本
  > function get() {
  >   var users = [
  >     {name: "Tom", age: 21, gender: "male"},
  >     {name: "Shelly", age: 19, gender: "female"}
  >   ]
  >   return {
  >     status: 200,         // 用于响应的 HTTP STATUS
  >     message: "on work!", // 用于响应的友好信息
  >     data: users          // 用于响应的数据内容
  >   }
  > }
  >   ```

### 4. 启动库
`java -jar vrasland.jar`

### 5. 尝试在浏览器上进行访问

  ```shell
>>> get http://localhost:8080/users
{
  "statue": "ok",
  "message": "get success!",
  "data": [
    {
      "name": "Tom",
      "age": 21,
      "gender": "male"
    },
    {
      "name": "Shelly",
      "age": 19,
      "gender": "female
    }
  ]
}
  
>>> get http://localhost:8080/users/1
{
  "status": "ok",
  "message": "get success!",
  "data": {
    "name": "Shelly",
    "age": 19,
    "gender": "female
  }
}
  ```