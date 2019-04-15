/**
 *  Created by JuerGenie.
 *  DateTime: 2019/4/9 23:26
 */

// get handler
function get() {
    // result must be an object or null
    return {
        status: 200,
        message: "ok!",
        data: {
            say: "hello world!",
            do: "anything!"
        }
    }
}

// give engine something result for script's initialize
var result = "it's working!";