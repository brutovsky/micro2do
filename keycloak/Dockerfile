FROM jboss/keycloak:16.0.0

COPY ./micro2do-realm.json /tmp/realm-export.json
COPY ./profile.properties /opt/jboss/keycloak/standalone/configuration/profile.properties

ENV KEYCLOAK_IMPORT /tmp/realm-export.json