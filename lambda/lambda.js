var AWS = require('aws-sdk');
var iotdata = new AWS.IotData({endpoint: 'a23om0asc4d73b.iot.eu-west-1.amazonaws.com'});
exports.handler = function(event, context) {

    var params = {
        topic: 'aws-in-topic',
        payload: '{"action" : "TOGGLE_LIGHT", "location" : "BEDROOM"}',
        qos: 0
        };


    iotdata.publish(params, function(err, data){
        if(err){
            console.log(err);
        }
        else{
            console.log("success?");
            //context.succeed(event);
        }
    });

    return 'OK'
};