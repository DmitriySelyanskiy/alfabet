# alfabet
Setup:
    1. In libraries ../development/docker/mariadb need to run command docker-compose up (terminal)
    2. In libraries ../development/docker/neo4j need to run docker-compose build, docker-compose up and after run the script 'property.sh' (terminal)
    3. Build libraries ../development/libs/internal mvn clean install (terminal)

Architecture:
   Three microservices were created:
        1. Auth - implementing logic for basic auth and routing
        2. Event - event management (CRUD for events)
        3. User - user management (create user and subscribe on event)

Rest Api:
    create event http://localhost:8080/event/create
        request:
           [
               {
                   "eventName": "party",
                   "startAt": 1718643654000,
                   "location": "London",
                   "venue": "Big Ban"
               },
               {{} ... additional events}
           ]
        response:
            {
                   "upsert": 1,
                   "notUpsert" 0,
                   "notValidEvents": [Not valid events with cause]
            }

    update event  http://localhost:8080/event/update
        request:
                   [
                       {
                           "id": 1,
                           "createdAt": 1618643654000 //use value from response only
                           "eventName": "party",
                           "startAt": 1718643654000,
                           "location": "London",
                           "venue": "Big Ban",
                           "participants": 10, //use value from response only
                           "completed": false //use value from response only
                       },
                       {{} ... additional events}
                   ]
                response:
                    {
                           "upsert": 1,
                           "notUpsert" 0,
                           "notValidEvents": [Not valid events with cause]
                    }

    delete events http://localhost:8080/event/delete
        request:
                    [1, 2, 3, 4]

    find by context http://localhost:8080/event/byContext
        request:
                    {
                            "eventIds": [1, 2]  //optional
                            "location": "London"    //optional
                            "venue": "Big Ban"  //optional
                            "type":  //optional (types: [ createdAt, startAt, participants])
                    }
        response:
                    {
                            "id": 1,
                            "createdAt": 1618643654000 //use value for request
                            "eventName": "party",
                            "startAt": 1718643654000,
                            "location": "London",
                            "venue": "Big Ban",
                            "participants": 10, //use value for request
                            "completed": false //use value for request
                    }

    find all events   http://localhost:8080/event/all
        response:
                     {
                        [
                           {
                             "id": 1,
                             "createdAt": 1618643654000 //use value for request
                             "eventName": "party",
                             "startAt": 1718643654000,
                             "location": "London",
                             "venue": "Big Ban",
                             "participants": 10, //use value for request
                             "completed": false //use value for request
                           }
                        ]
                     }

    create user: http://localhost:8080/user/create
        request:
                     {
                         "userProperty": {
                             "NAME": "Yoda",
                             "EMAIL": "yoda@mock.il",
                             "PERMISSIONS": "ADMIN",
                             "PASSWORD": "admin"
                         }
                     }

        response:
                    {
                        "id - generated string"
                    }

    subscribe on event: http://localhost:8080/user/subscribe?userId=adasdsddda&eventId=5
        response:
                    {
                        true/false
                    }


Additionally:
    Used prefix Alf for internal classes
    Not implemented logic for password and email processing (User service). Need to create an otp password and send to auth to decode
    Not implemented logic for validation on unique email
    For user storage chose Neo4j, because changes in the user storage scheme and addition of new features are possible. Neo4j is more flexible
    Implemented notification for subscribers by rsocket and not implemented notification by mail sender
