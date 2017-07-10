import boto3
import json

def lambda_handler(event, context):
    access_token = event['payload']['accessToken']

    if event['header']['namespace'] == 'Alexa.ConnectedHome.Discovery':
        return handleDiscovery(context, event)

    elif event['header']['namespace'] == 'Alexa.ConnectedHome.Control':
        return handleControl(context, event)

def handleDiscovery(context, event):
    endpoint = 'a23om0asc4d73b.iot.eu-west-1.amazonaws.com'
    payload = ''
    header = {
        "namespace": "Alexa.ConnectedHome.Discovery",
        "name": "DiscoverAppliancesResponse",
        "payloadVersion": "2"
        }

    if event['header']['name'] == 'DiscoverAppliancesRequest':
        payload = {
            "discoveredAppliances":[
                {
                    "applianceId":"device001",
                    "manufacturerName":"yourManufacturerName",
                    "modelName":"model 01",
                    "version":"your software version number here.",
                    "friendlyName":"Smart Home Virtual Device",
                    "friendlyDescription":"Virtual Device for the Sample Hello World Skill",
                    "isReachable":True,
                    "actions":[
                        "turnOn",
                        "turnOff"
                    ],
                    "additionalApplianceDetails":{
                        "extraDetail1":"optionalDetailForSkillAdapterToReferenceThisDevice",
                        "extraDetail2":"There can be multiple entries",
                        "extraDetail3":"but they should only be used for reference purposes.",
                        "extraDetail4":"This is not a suitable place to maintain current device state"
                    }
                }
            ]
        }

    client = boto3.client('iot-data', region_name='eu-west-1')
    response = client.publish(
        topic='aws-in-topic',
        qos=0,
        payload=json.dumps({"action" : "TOGGLE_LIGHT", "location":"BEDROOM"})
    )
    return { 'header': header, 'payload': payload }

def handleControl(context, event):
    endpoint = 'a23om0asc4d73b.iot.eu-west-1.amazonaws.com'
    payload = ''
    device_id = event['payload']['appliance']['applianceId']
    message_id = event['header']['messageId']

    if event['header']['name'] == 'TurnOnRequest':
        payload = {"action" : "TURN_ON", "location":"BEDROOM"}
    elif event['header']['name'] == 'TurnOffRequest':
        payload = {"action" : "TURN_OFF", "location":"BEDROOM"}

    header = {
        "namespace":"Alexa.ConnectedHome.Control",
        "name":"ToggleConfirmation",
        "payloadVersion":"2",
        "messageId": message_id
        }

    client = boto3.client('iot-data', region_name='eu-west-1')
    response = client.publish(
        topic='aws-in-topic',
        qos=0,
        payload=json.dumps(payload)
    )
    return { 'header': header, 'payload': payload }