FROM neo4j:5.9.0
MAINTAINER alf

ENV NEO4J_AUTH=none
ENV NEO4J_dbms_security_procedures_unrestricted=apoc.*,algo.*,gds.*
ENV NEO4J_dbms_security_procedures_allowlist=apoc.*,algo.*,gds.*
ENV NEO4J_server_memory_heap_initial__size=12G
ENV NEO4J_server_memory_heap_max__size=12G
ENV NEO4J_server_jvm_additional=-XX:+CrashOnOutOfMemoryError
ENV NEO4J_server_default__listen__address=0.0.0.0
ENV NEO4J_initial_dbms_default__database=neo4j
ENV NEO4J_apoc_import_file_enabled=true
ENV NEO4J_apoc_export_file_enabled=true
ENV NEO4J_apoc_import_file_use__neo4j__config=true
ENV NEO4J_apoc_uuid_enabled=true
ENV NEO4J_server.config.strict_validation.enabled=true





