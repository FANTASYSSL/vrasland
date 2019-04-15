## About VRASLAND

> Try to build RESTful API Server like build FTP server.
> This is an just focus on RESTful API's webapp framework, mapping request to filesystem's path, not need to registry route, you can do that just need to create some directory and script.

## Other Language Version
[简体中文(zhCN)](https://github.com/JuerGenie/vrasland/blob/master/README.md)
[English(enUS)](https://github.com/JuerGenie/vrasland/blob/master/README.enUS.md)

## How to use

### 1. Clone this repository
`git clone https://github.com/JuerGenie/vransland`

### 2. Build file struct like your API's struct
  ```
  E.G.：
  
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
  > On version 1.0(vl-k), Complex path (contain params, like '/users/{index}/info') mapping is supported.

  File      | Request Map              | Description
  :----      | :----                | :----
  /api       | *                 | 这是一个文件夹，也是所有API的根目录，象征着 API 的 '/' 路径。<br />这个名称并不是固定的，你可以通过修改配置文件来进行定制。 
  users.js   | /users               | 这将是第一个处理脚本，用于处理类似 getAll、put 和 post 的针对 users 的请求。<br />看起来它就像是一个普通的资源？这就对了。 
  /users     | *                 | 这是针对该资源（Users）的更细分的资源，它就是一个文件夹，用于映射路径。
  {index}.js | /users/{index}       | 这是针对请求路径中的参数进行的映射，注意，'{index}' 这个名称是必须的。<br />若要进行映射，请将相应部分替换为 {index}。<br/>这些被匹配的参数会被放置到脚本中的全局变量 'args' 中。<br />args 将会是一个数组，具体可以通过 {index}.js 来看看该如何使用。 
  /{index}   | *                 | 与 {index}.js 类似，这个文件夹也将映射路径的一部分。<br />被匹配的参数同样会被（按顺序）放置进 args 中。 
  score.js   | /users/{index}/score | 这对应的是相匹配的资源下的细项资源。

### 3. Write some script and put it on the true path that you want to map
Just put it on like step.2 said, the script file's content like this:
```js
// users.js
// On vl-k (vrasland-kotlin) ver, javascript is the default language to handle request.
function get() {
  var users = [
    {name: "Tom", age: 21, gender: "male"},
    {name: "Shelly", age: 19, gender: "female"}
  ];
  
  return {
    status: 200,         // response's HTTP STATUS
    message: "on work!", // response's friendly message
    data: users          // response's data
  };
}
```

### 4. Run the library
`java -jar vrasland.jar`

### 5. Try to invoke path on browser

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