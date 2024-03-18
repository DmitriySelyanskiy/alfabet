#!/bin/bash
docker exec -i neo4j cypher-shell -a localhost:7687 'CREATE (:`name`:`property`:`property` {Type: "Property", AlfId: "name"}), (:`email`:`property`:`property` {Type: "Property", AlfId: "email"}), (:`permissions`:`property`:`property` {Type: "Property", AlfId: "permissions"}), (:`password`:`property`:`property` {Type: "Property", AlfId: "password"}) RETURN true;'
